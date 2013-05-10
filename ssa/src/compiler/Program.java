package compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


import stmt.BranchStmt;
import stmt.Stmt;
import token.Code;
import token.Constant;
import token.Token;
import token.Variable;

public class Program {
	private List<Stmt> stmts;
	private List<Routine> routines;
	
	public Program() {
		stmts = new LinkedList<Stmt>();
		routines = new LinkedList<Routine>();
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
					stmts.add(Stmt.parseStmt(line));
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Stmt s: stmts)
			for (Token t: s.getRHS())
				if (t instanceof Code)
					((Code) t).setDstStmt(stmts.get(((Code) t).getIndex() - 1));
		
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
	
	public void genSSA() {
		for (Routine r: routines)
			r.genSSA();
	}
	
	public void dump() {
		for (Routine r: routines) {
			r.dump();
			System.out.println("\n*********************************************");
		}
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
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("SSA.jar [Input file]"	);
			return;
		}
		Program p = new Program();
		p.scanFile(args[0]);

		long startTime, endTime;
		
		startTime = System.nanoTime();
		p.genCFG();
		endTime = System.nanoTime();
		//System.out.println(((endTime - startTime) / 1000.0));

		startTime = System.nanoTime();
		p.genDominator();
		endTime = System.nanoTime();
		
		p.genSSA();
		//p.simpleConstantProp();
		
		//System.out.println(((endTime - startTime) / 1000.0));
		//System.out.println(BasicBlock.globalIndex);

		//int max = 0;
		//for (Routine r: p.getRoutines()) {
		//	int b = r.getBlockCount();
		//	if (b > max) max = b;
		//}
		//System.out.println(max);
		
		//p.dumpIR();
		//p.dumpSSA();
		DefUseAnalysis dua = new DefUseAnalysis(p.getRoutines().get(0));
		dua.genDefUse();
	}
}
