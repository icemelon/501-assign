package compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import profile.PositionProfile;


import stmt.ArithStmt;
import stmt.BranchStmt;
import stmt.CallStmt;
import stmt.Stmt;
import token.Code;
import token.Constant;
import token.Register;
import token.Token;
import token.Variable;

public class Program extends Node {
	
	private List<String> typeDec;
	private List<String> globalVars;
	private List<Routine> routines;
	private Stmt firstStmt;
	
	public Program() {
		typeDec = new LinkedList<String>();
		globalVars = new LinkedList<String>();
		routines = new LinkedList<Routine>();
	}
	
	private Routine searchRoutine(int line) {
		for (Routine r: routines)
			if (r.getStartLine() == line)
				return r;
		return null;
	}
	
	public boolean scanFile(String filename) {
		
		List<Stmt> stmts = new LinkedList<Stmt>();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("method")) {
					routines.add(Routine.parse(line));
				} else if (line.startsWith("instr")) {
					Stmt s = Stmt.parse(line);
					if (s instanceof CallStmt) {
						int index = ((Code) s.getRHS().get(0)).getIndex();
						Routine r = searchRoutine(index); 
						((CallStmt) s).setRoutine(r);
					}
					stmts.add(s);
				} else if (line.startsWith("type"))
					typeDec.add(line);
				else if (line.startsWith("global"))
					globalVars.add(line);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "doesn't exist");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		{
			int beginLine = routines.get(0).getStartLine();
			int endLine;
			Routine routine;
			for (int i = 0; i < routines.size() - 1; i++) {
				endLine = routines.get(i + 1).getStartLine() - 1;
				routine = routines.get(i);
				routine.setEndLine(endLine);
				routine.setBody(stmts.subList(beginLine - 1, endLine));
				beginLine = endLine + 1;
			}
			endLine = stmts.size() - 1; // last instr: nop, skip this one
			routine = routines.get(routines.size() - 1);
			routine.setEndLine(endLine);
			routine.setBody(stmts.subList(beginLine - 1, endLine));
		}
		
		firstStmt = stmts.get(0);
		
		return true;
	}
	
	public List<Routine> getRoutines() { return routines; }
	
	public void genCFG() {
		for (Routine r: routines) {
			r.genCFG();
			r.genDominator();
		}
	}
	
	public void transformToSSA() {
		for (Routine r: routines) {
			r.ssaTrans = new SSATransform(r);
//			System.out.println("routine " + r.getName() + " gen SSA");
			r.ssaTrans.translateToSSA();
		}
	}
	
	public void renumberStmt() {
		Stmt.GlobalIndex = 1;
		Map<Integer, Integer> newIndexMap = new HashMap<Integer, Integer>();
		
		for (Routine routine: routines) {
			
			for (Block b: routine.getBlocks()) {
				for (Stmt s: b.body) {
					
					++ Stmt.GlobalIndex;
					newIndexMap.put(s.index, Stmt.GlobalIndex);
					s.index = Stmt.GlobalIndex;
					
					for (Token t: s.getRHS())
						if (t instanceof Register) {
							if (!newIndexMap.containsKey(((Register) t).index))
								System.out.println("Program.renumberStmt error: Cannot find register (" + s.toIRString() + ")");
							((Register) t).index = newIndexMap.get(((Register) t).index);
						}
				}
				
				BranchStmt profBrStmt = b.getProfBranchStmt();
				if (profBrStmt != null) {
					++ Stmt.GlobalIndex;
					newIndexMap.put(profBrStmt.index, Stmt.GlobalIndex);
					profBrStmt.index = Stmt.GlobalIndex;
				}
				
				b.startLine = b.body.get(0).index;
			}
			
			routine.setStartLine(routine.getEntryBlock().startLine);
			newIndexMap.clear();
		}
	}
	
	public void transformBackFromSSA() {
		for (Routine r: routines) {
			r.ssaTrans.translateBackFromSSA();
		}
		
		renumberStmt();
	}
	
	public void constantPropOpt() {
		for (Routine r: routines) {
			r.cp = new ConstantPropOpt(r);
//			r.dumpSSA();
			r.cp.optimize();
//			cpo.dump();
		}
	}
	
	public void valueNumberOpt() {
		for (Routine r: routines) {
			r.vn = new ValueNumberOpt(r);
//			r.dumpSSA();
			r.vn.optimize();
//			System.out.println(r.toString() + " remove " + vno.counter + " expressions");
		}
	}
	
	public String dump() {
		StringBuilder sb = new StringBuilder();
		
		for (String type: typeDec)
			sb.append("    " + type + "\n");
		for (Routine r: routines)
			sb.append(r.toString() + "\n");
		for (String global: globalVars)
			sb.append("    " + global + "\n");
		sb.append(firstStmt + "\n");
		
		for (Routine r: routines)
			sb.append(r.dump());
		
		return sb.toString();
	}
	
	public String dumpIR() {
		StringBuilder sb = new StringBuilder();
		
		for (String type: typeDec)
			sb.append("    " + type + "\n");
		for (Routine r: routines)
			sb.append(r.toString() + " [entryblock#" + r.getEntryBlock().getIndex() + "]\n");
		for (String global: globalVars)
			sb.append("    " + global + "\n");
		sb.append(firstStmt + "\n");
		
		for (Routine r: routines)
			sb.append(r.dumpIR());
		
		return sb.toString();
	}
	
	public String dumpCFG() {
		StringBuilder sb = new StringBuilder();
		
		for (String type: typeDec)
			sb.append("    " + type + "\n");
		for (Routine r: routines)
			sb.append(r.toString() + " [entryblock#" + r.getEntryBlock().getIndex() + "]\n");
		for (String global: globalVars)
			sb.append("    " + global + "\n");
		sb.append(firstStmt + "\n");
		
		for (Routine r: routines)
			sb.append(r.dumpCFG());
		
		return sb.toString();
	}
	
	public String dumpSSA() {
		StringBuilder sb = new StringBuilder();
		
		for (String type: typeDec)
			sb.append("    " + type + "\n");
		for (Routine r: routines)
			sb.append(r.toString() + " [entryblock#" + r.getEntryBlock().getIndex() + "]\n");
		for (String global: globalVars)
			sb.append("    " + global + "\n");
		sb.append(firstStmt + "\n");
		
		for (Routine r: routines)
			sb.append(r.dumpSSA());
		
		return sb.toString();
	}
	
	public void printReport() {
		for (Routine r: routines) {
			System.out.println("Function: " + r.getName());
			if (r.cp != null)
				System.out.println("Number of constants propagated: " + r.cp.varCounter);
			if (r.vn != null)
				System.out.println("Number of expressions eliminated: " + r.vn.exprCounter);
		}
	}
}
