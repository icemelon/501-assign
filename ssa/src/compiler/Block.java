package compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import stmt.MoveStmt;
import stmt.Stmt;
import type.Register;
import type.Token;
import type.Variable;


public class Block {
	public static int globalIndex = 0;
	
	public final int index;
	public final int startLine;
	public final int endLine;
	public final List<Stmt> body;
	public final Routine routine;
	
	private List<PhiNode> phiNodeList = new LinkedList<PhiNode>();
	//private List<Stmt> ssaStmts = new LinkedList<Stmt>();
	
	private List<Block> preds = new ArrayList<Block>();
	private List<Block> succs = new ArrayList<Block>();
	
	private Block idom = null;
	private List<Block> children = new ArrayList<Block>();
	private List<Block> DF = new ArrayList<Block>();
	
	public Block(int startLine, int endLine, List<Stmt> stmts, Routine routine) {
		globalIndex++;
		this.index = startLine;
		this.startLine = startLine;
		this.endLine = endLine;
		this.body = stmts;
		this.routine = routine;
	}
	
	public void addPred(Block b) {
		if (!preds.contains(b))
			preds.add(b); 
	}
	
	public void addSucc(Block b) {
		if (!succs.contains(b))
			succs.add(b); 
	}
	
	public void addChild(Block b) { children.add(b); }
	
	public void addDF(Block b) {
		if (!DF.contains(b))
			DF.add(b); 
	}
	
	public List<Block> getSuccs() { return succs; }
	
	public List<Block> getPreds() { return preds; }
	
	public List<Block> getChildren() { return children; }
	
	public List<Block> getDF() { return DF; }
	
	public void setIdom(Block b) { idom = b; }
	
	public Block getIdom() { return idom; }
	
	public boolean hasAssignment(Variable v) {
		for (Stmt stmt: body) {
			if (stmt instanceof MoveStmt) {
				if (((Variable) stmt.getLHS()).getName().equals(v.getName()))
					return true;
			}
		}
		return false;
	}
	
	public void insertPhiNode(Variable v) {
		
		phiNodeList.add(new PhiNode(v.getName(), preds.size()));
		
	}
	
	/*public void insertParam(String v) {
		Stmt stmt = ssaStmts.get(0);
		if (!stmt.getInstr().equals("enter") || !(stmt.getOprands().get(0) instanceof Variable)) {
			stmt = new Stmt("enter");
			ssaStmts.add(0, stmt);
		}
		Variable var = new Variable(v);
		var.setSSAIndex(0);
		stmt.addOprands(var);
	}*/
	
	public void rename() {
		for (int i = 0; i < phiNodeList.size(); i++) {
			PhiNode phiNode = phiNodeList.get(i);
			routine.genSSAName((Variable) phiNode.getMoveStmt().getLHS()); 
		}
		
		List<Variable> lhsList = new LinkedList<Variable>();
		
		for (Stmt stmt: body) {
			for (Token t: stmt.getRHS())
				if (t instanceof Variable) {
					routine.setSSAName((Variable) t);
				}
			{
				Token t = stmt.getLHS();
				if (t instanceof Variable) {
					lhsList.add((Variable) t);
					routine.genSSAName((Variable) t);
				}
			}
			//ssaStmts.add(stmt);
			//stmt.dumpSSA();
		}
		
		for (Block succ: succs)
			succ.updatePhiStmt(this);
		
		for (Block child: children)
			child.rename();
		
		for (Variable v: lhsList)
			routine.popSSAName(v);
	}
	
	public void updatePhiStmt(Block refBlock) {
		
		int i;
		for (i = 0; i < preds.size(); i++)
			if (preds.get(i) == refBlock)
				break;
		for (PhiNode phiNode: phiNodeList) {
			Variable refVar = new Variable(phiNode.getVarName());
			routine.setSSAName(refVar);
			//System.out.println("Block#" + index + ": " + refVar.toSSAString() + "(" + i + ")");
			phiNode.getRefVars()[i] = refVar;
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
		
		for (Stmt stmt: body)
			System.out.println(stmt);
	}
	
	public void dumpIR() {
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
		
		for (Stmt stmt: body)
			System.out.println(stmt.toIRString());
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
		
		for (PhiNode phiNode: phiNodeList) {
			phiNode.dump();
		}
		
		for (Stmt stmt: body)
			System.out.println(stmt.toSSAString());
	}
}
