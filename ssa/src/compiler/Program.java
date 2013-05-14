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
	
	public void scanFile(String filename) {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("method")) {
					routines.add(Routine.parseRoutine(line));
				} else if (line.startsWith("instr")) {
					Stmt s = Stmt.parseStmt(line);
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
		} catch (IOException e) {
			e.printStackTrace();
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
	}
	
	public List<Routine> getRoutines() { return routines; }
	
	public void genCFG() {
		for (Routine r: routines) {
			r.genCFG();
		}
	}
	
	public void genDominator() {
		for (Routine r: routines) {
			r.genDominator();
		}
	}
	
	public void tranformToSSA() {
		for (Routine r: routines) {
//			System.out.println("routine " + r.getName() + " gen SSA");
			r.tranformToSSA();
		}
	}
	
	public void transformBackFromSSA() {
		for (Routine r: routines) {
//			System.out.println("routine " + r.getName() + " gen SSA");
			r.transformBackFromSSA();
		}
		
		Stmt.globalIndex = 2;
		for (Routine r: routines) {
//			System.out.println("routine " + r.getName() + " gen SSA");
			r.numberStmt();
		}
	}
	
	public void constantPropOpt() {
		for (Routine r: routines) {
			ConstantPropOpt cpo = new ConstantPropOpt(r);
//			r.dumpSSA();
			cpo.optimize();
//			cpo.dump();
		}
	}
	
	public void valueNumberOpt() {
		for (Routine r: routines) {
			ValueNumberOpt vno = new ValueNumberOpt(r);
//			r.dumpSSA();
			vno.optimize();
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
//			System.out.println("\n*********************************************");
	}
	
	public void dumpIR() {
		for (Routine r: routines) {
			r.dumpIR();
			System.out.println("\n*********************************************");
		}
	}
	
	public void dumpSSA() {
		for (Routine r: routines) {
			r.dumpSSA();
			System.out.println("\n*********************************************");
		}
	}
	
}
