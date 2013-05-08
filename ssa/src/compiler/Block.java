package compiler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import type.Register;
import type.Token;
import type.Variable;


public class Block {
	public static int globalIndex = 0;
	
	public final int index;
	public final int startLine;
	public final int endLine;
	public final List<Stmt> stmts;
	public final Routine routine;
	
	private List<Stmt> ssaStmts = new LinkedList<Stmt>();
	
	private Set<Block> preds = new HashSet<Block>();
	private Set<Block> succs = new HashSet<Block>();
	
	private Block idom = null;
	private Set<Block> children = new HashSet<Block>();
	private Set<Block> DF = new HashSet<Block>();
	
	public Block(int startLine, int endLine, List<Stmt> stmts, Routine routine) {
		globalIndex++;
		this.index = startLine;
		this.startLine = startLine;
		this.endLine = endLine;
		this.stmts = stmts;
		this.routine = routine;
	}
	
	public void addPred(Block b) { preds.add(b); }
	
	public void addSucc(Block b) { succs.add(b); }
	
	public void addChild(Block b) { children.add(b); }
	
	public void addDF(Block b) { DF.add(b); }
	
	public Set<Block> getSuccs() { return succs; }
	
	public Set<Block> getPreds() { return preds; }
	
	public Set<Block> getChildren() { return children; }
	
	public Set<Block> getDF() { return DF; }
	
	public void setIdom(Block b) { idom = b; }
	
	public Block getIdom() { return idom; }
	
	public boolean hasAssignment(Variable v) {
		for (Stmt stmt: stmts) {
			if (stmt.getInstr().equals("move")) {
				if (((Variable) stmt.getOprands().get(1)).getName().equals(v.getName()))
					return true;
			}
		}
		return false;
	}
	
	public void insertPhi(Variable v) {
		
		Stmt phiStmt = new Stmt("phi");
		
		Stmt movStmt = new Stmt("move");
		Register reg = new Register(phiStmt.index);
		movStmt.addOprands(reg);
		Variable phiVar = new Variable(v.getName());
		movStmt.addOprands(phiVar);
		
		ssaStmts.add(phiStmt);
		ssaStmts.add(movStmt);
	}
	
	public void insertParam(String v) {
		Stmt stmt = ssaStmts.get(0);
		if (!stmt.getInstr().equals("enter") || !(stmt.getOprands().get(0) instanceof Variable)) {
			stmt = new Stmt("enter");
			ssaStmts.add(0, stmt);
		}
		Variable var = new Variable(v);
		var.setSSAIndex(0);
		stmt.addOprands(var);
	}
	
	public void rename() {
		for (int i = 0; i < ssaStmts.size(); i++) {
			Stmt s = ssaStmts.get(i);
			if (s.getInstr().equals("move")) {
				routine.genSSAIndex((Variable) s.getOprands().get(1));
			}
		}
		
		List<Variable> lhsList = new LinkedList<Variable>();
		
		for (Stmt s: stmts) {
			Stmt stmt = (Stmt) s.clone();
			for (Token t: stmt.getRHS())
				if (t instanceof Variable) {
					routine.setSSAIndex((Variable) t);
				}
			for (Token t: stmt.getLHS())
				if (t instanceof Variable) {
					lhsList.add((Variable) t);
					routine.genSSAIndex((Variable) t);
				}
			ssaStmts.add(stmt);
			//stmt.dumpSSA();
		}
		
		for (Block succ: succs) {
			succ.updatePhiStmt(this);
		}
		
		for (Block child: children)
			child.rename();
		
		for (Variable v: lhsList)
			routine.popSSAIndex(v);
	}
	
	public void updatePhiStmt(Block refBlock) {
		Iterator<Stmt> it = ssaStmts.iterator();
		
		while (it.hasNext()) {
			Stmt phi = it.next();
			if (!phi.getInstr().equals("phi"))
				break;
			
			Stmt mov = it.next();
			String varName = ((Variable) mov.getOprands().get(1)).getName();
			Variable var = new Variable(varName);
			routine.setSSAIndex(var);
			phi.addOprands(var);
		}
	}
	
	public void dump() {
		System.out.print("Block #" + index);
		
		System.out.print("  Preds:");
		for (Block b: preds)
			System.out.print(" " + b.index);
		
		System.out.print(", Succs:");
		for (Block b: succs)
			System.out.print(" " + b.index);
		
		System.out.print(", Idom: " + idom.index);
		
		System.out.print(", DF:");
		for (Block b: DF)
			System.out.print(" " + b.index);
		
		System.out.println();
		
		for (Stmt stmt: stmts)
			stmt.dump();
	}
	
	public void dumpSSA() {
		System.out.print("Block #" + index);
		
		System.out.print("  Preds:");
		for (Block b: preds)
			System.out.print(" " + b.index);
		
		System.out.print(", Succs:");
		for (Block b: succs)
			System.out.print(" " + b.index);
		
		System.out.print(", Idom: " + idom.index);
		
		System.out.print(", DF:");
		for (Block b: DF)
			System.out.print(" " + b.index);
		
		System.out.println();
		
		for (Stmt stmt: ssaStmts)
			stmt.dumpSSA();
	}
}
