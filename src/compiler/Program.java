package compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


import stmt.BranchStmt;
import stmt.CallStmt;
import stmt.Stmt;
import token.Code;
import token.Constant;
import token.Token;
import token.Variable;

public class Program {
	private List<String> typeDec;
	private List<String> globalVar;
	private List<Stmt> stmts;
	private List<Routine> routines;
	
	public Program() {
		typeDec = new LinkedList<String>();
		globalVar = new LinkedList<String>();
		stmts = new LinkedList<Stmt>();
		routines = new LinkedList<Routine>();
	}
	
	private Routine searchRoutine(int line) {
		for (Routine r: routines)
			if (r.getStartLine() == line)
				return r;
		return null;
	}
	
	public boolean scanFile(String filename) {
		
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
					globalVar.add(line);
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
	
	public void transformBackFromSSA() {
		for (Routine r: routines) {
			r.ssaTrans.translateBackFromSSA();
		}
		
		Stmt.globalIndex = 2;
		
		for (Routine r: routines) {
			r.ssaTrans.numberStmt();
		}
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
	
	public void dump() {
		for (String type: typeDec)
			System.out.println("    " + type);
		for (Routine r: routines)
			System.out.println(r.toString());
		for (String global: globalVar)
			System.out.println("    " + global);
		System.out.println(stmts.get(0));
		
		for (Routine r: routines)
			r.dump();
	}
	
	public void dumpIR() {
		for (String type: typeDec)
			System.out.println("    " + type);
		for (Routine r: routines)
			System.out.println(r.toString() + " [entryblock#" + r.getEntryBlock().index + "]");
		for (String global: globalVar)
			System.out.println("    " + global);
		System.out.println("\n" + stmts.get(0));
		
		for (Routine r: routines)
			r.dumpIR();
	}
	
	public void dumpCFG() {
		for (String type: typeDec)
			System.out.println("    " + type);
		for (Routine r: routines)
			System.out.println(r.toString() + " [entryblock#" + r.getEntryBlock().index + "]");
		for (String global: globalVar)
			System.out.println("    " + global);
		System.out.println("\n" + stmts.get(0));
		
		for (Routine r: routines)
			r.dumpCFG();
	}
	
	public void dumpSSA() {
		for (String type: typeDec)
			System.out.println("    " + type);
		for (Routine r: routines)
			System.out.println(r.toString() + " [entryblock#" + r.getEntryBlock().index + "]");
		for (String global: globalVar)
			System.out.println("    " + global);
		System.out.println("\n" + stmts.get(0));
		
		for (Routine r: routines)
			r.dumpSSA();
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
	
	public void run(Option option) {
		if (!scanFile(option.fileName))
			return;
		genCFG();
		
		boolean ssa = false;
		
		if (option.optimize.size() > 0) { 
			// need SSA
			transformToSSA();
			ssa = true;
		} if (option.optimize.contains(Option.OptimizeOption.CP))
			constantPropOpt();
		if (option.optimize.contains(Option.OptimizeOption.VN))
			valueNumberOpt();
		
		if (option.backend == Option.BackendOption.SSA) {
			if (!ssa)
				transformToSSA();
			dumpSSA();
		} else if (option.backend == Option.BackendOption.Report) {
			printReport();
		} else {
			if (ssa)
				transformBackFromSSA();
			
			if (option.backend == Option.BackendOption.ASM)
				dump();
			else if (option.backend == Option.BackendOption.IR)
				dumpIR();
			else if (option.backend == Option.BackendOption.CFG)
				dumpCFG();
		}
	}
}
