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
		this.body = new ArrayList<Stmt>(stmts);
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
		
		phiNodeList.add(new PhiNode(v.name, preds.size(), this));
		
	}
	
	
	
	public void removeStmt(Stmt stmt) {
		System.out.println("block#" + index + " remove stmt:" + stmt.toSSAString());
		body.remove(stmt);
//		dumpSSA();
	}
	
	public void replaceStmt(Stmt oldStmt, Stmt newStmt) {
		int i;
		for (i = 0; i < body.size(); i++)
			if (body.get(i) == oldStmt)
				break;
		if (i < body.size()) {
//			System.out.println("block#" + index + " replace stmt" + oldStmt.toSSAString() + " by " + newStmt.toSSAString());
			body.set(i, newStmt);
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
		
		System.out.print(", child:");
		for (Block b: children)
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
		
		System.out.print(", child:");
		for (Block b: children)
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
		
		System.out.print(", child:");
		for (Block b: children)
			System.out.print(" " + b.index);
		
		System.out.println();
		
		for (PhiNode phiNode: phiNodeList) {
			System.out.println(phiNode.toSSAString());
		}
		
		for (Stmt stmt: body)
			System.out.println(stmt.toSSAString());
	}
}
