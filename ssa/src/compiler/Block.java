package compiler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import stmt.EntryStmt;
import stmt.MoveStmt;
import stmt.PhiNode;
import stmt.Stmt;
import token.Token;
import token.Variable;


public class Block {
	public static int globalIndex = 0;
	
	public final int index;
	public final int startLine;
	public final int endLine;
	public List<Stmt> body;
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
		this.body = new ArrayList(stmts);
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
	
	public List<PhiNode> getPhiNode() { return phiNodeList; }
	
	public void setIdom(Block b) { idom = b; }
	
	public Block getIdom() { return idom; }
	
	public boolean hasAssignment(Variable v) {
		
		for (Stmt stmt: body) {
			if (stmt instanceof MoveStmt || stmt instanceof EntryStmt) {
				for (Token t: stmt.getLHS())
					if (((Variable) t).equals(v))
						return true;
			}
		}
		
		return false;
	}
	
	public void insertPhiNode(Variable v) {
		
		phiNodeList.add(new PhiNode(v.getName(), preds.size(), this));
		
	}
	
	public void rename() {
		for (int i = 0; i < phiNodeList.size(); i++) {
			PhiNode phiNode = phiNodeList.get(i);
			routine.genSSAName((Variable) phiNode.getLHS().get(0)); 
		}
		
		List<Variable> lhsList = new LinkedList<Variable>();
		
		for (Stmt stmt: body) {
			for (Token t: stmt.getRHS())
				if (t instanceof Variable)
					routine.setSSAName((Variable) t);
			for (Token t: stmt.getLHS()) {
				if (t instanceof Variable) {
					lhsList.add((Variable) t);
					routine.genSSAName((Variable) t);
				}
			}
			//ssaStmts.add(stmt);
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
			phiNode.setRHS(i, refVar);
		}
	}
	
	public void removeStmt(Stmt stmt) {
		System.out.println("block#" + index + " remove stmt:" + stmt.toSSAString());
		body.remove(stmt);
//		dumpSSA();
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
			System.out.println(phiNode.toSSAString());
		}
		
		for (Stmt stmt: body)
			System.out.println(stmt.toSSAString());
	}
}
