package compiler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import stmt.BranchStmt;
import stmt.EntryStmt;
import stmt.MoveStmt;
import stmt.OtherStmt;
import stmt.PhiNode;
import stmt.Stmt;
import stmt.Stmt.Operator;
import token.Token;
import token.Variable;

public class Block extends Node {
	
	public static int GlobalIdCounter = 0;
	
	public final int id;
	public int startLine;
	public int endLine;
	public List<Stmt> body;
	public final Routine routine;
	
	private List<PhiNode> phiNodeList = new LinkedList<PhiNode>();
	private BranchStmt profBrStmt = null;
	
	private List<Block> preds = new ArrayList<Block>();
	private List<Block> succs = new ArrayList<Block>();
	
	private Block idom = null;
	private List<Block> children = new ArrayList<Block>();
	private List<Block> DF = new ArrayList<Block>();
	
	public Block(Routine routine) {
		this.id = GlobalIdCounter++;
		this.body = new ArrayList<Stmt>();
		this.routine = routine;
	}
	
	public Block(int startLine, int endLine, List<Stmt> stmts, Routine routine) {
		this.id = GlobalIdCounter++;
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
	
	public int getIndex() { return body.get(0).index; }
	
	public void setProfBranchStmt(BranchStmt s) { profBrStmt = s; }
	
	public BranchStmt getProfBranchStmt() { return profBrStmt; }
	
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
//		System.out.println("block#" + index + " remove stmt:" + stmt.toSSAString());
		if (body.contains(stmt)) {
			body.remove(stmt);
			if (body.size() == 0)
				body.add(new OtherStmt(Operator.nop));
		} else if (phiNodeList.contains(stmt))
			phiNodeList.remove(stmt);
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
		for (Stmt stmt: body)
			System.out.println(stmt);
		
		if (profBrStmt != null)
			System.out.println(profBrStmt.toString());
	}
	
	public void dumpIR() {
		for (Stmt stmt: body)
			System.out.println(stmt.toIRString());
		
		if (profBrStmt != null)
			System.out.println(profBrStmt.toIRString());
	}
	
	public void dumpCFG() {
		System.out.print("Block#" + getIndex());
		
		System.out.print("  Preds:");
		for (Block b: preds)
			System.out.print(" " + b.getIndex());
		
		System.out.print(", Succs:");
		for (Block b: succs)
			System.out.print(" " + b.getIndex());
		
		System.out.print(", Idom:");
		if (idom != null)
			System.out.print(" " + idom.getIndex());
		
		System.out.print(", Children:");
		for (Block b: children)
			System.out.print(" " + b.getIndex());
		
		System.out.println();
		
		for (Stmt stmt: body)
			System.out.println(stmt.toIRString());
		
		if (profBrStmt != null)
			System.out.println(profBrStmt.toIRString());
	}
	
	
	public void dumpSSA() {
		System.out.println("Block #" + getIndex());
		
		for (PhiNode phiNode: phiNodeList) {
			System.out.println(phiNode.toSSAString());
		}
		
		for (Stmt stmt: body)
			System.out.println(stmt.toSSAString());
		
		if (profBrStmt != null)
			System.out.println(profBrStmt.toSSAString());
	}
}
