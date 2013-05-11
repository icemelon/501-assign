package compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import attr.ConstantAttr;
import attr.ConstantAttr.ConstantType;

import stmt.ArithStmt;
import stmt.BranchStmt;
import stmt.EntryStmt;
import stmt.MemoryStmt;
import stmt.MoveStmt;
import stmt.PhiNode;
import stmt.Stmt;
import token.Code;
import token.Constant;
import token.GP;
import token.Offset;
import token.Register;
import token.Token;
import token.Variable;

public class ConstantPropOpt {
	private class FlowEdge {
		public Block src;
		public Block dst;
		
		public FlowEdge(Block src, Block dst) {
			this.src = src;
			this.dst = dst;
		}
	}

	private Routine routine;
	private DefUseAnalysis du;
	
	private HashSet<String> executeFlag = new HashSet<String>();
	private List<FlowEdge> flowWorkList = new LinkedList<FlowEdge>();
	private List<Stmt> ssaWorkList = new LinkedList<Stmt>();
	private HashMap<String, ConstantAttr> varAttr = new HashMap<String, ConstantAttr>();
	
	public ConstantPropOpt(Routine r) {
		this.routine = r;
		this.du = new DefUseAnalysis(r);
	}
	
	private String getFlowEdgeString(FlowEdge e) {
		if (e.src == null)
			return "b#" + "->" + "b#" + e.dst.index;
		else
			return "b#" + e.src.index + "->" + "b#" + e.dst.index; 
	}
	
	private String getFlowEdgeString(Block src, Block dst) {
		if (src == null)
			return "b#" + "->" + "b#" + dst.index;
		else
			return "b#" + src.index + "->" + "b#" + dst.index;
	}
	
	private void insertFlowWorkList(Block src, Block dst) {
		flowWorkList.add(new FlowEdge(src, dst));
	}
	
	private void insertSSAWorkList(String var) {
		for (Stmt stmt: du.getDefUseList(var))
			ssaWorkList.add(stmt);
	}
	
	private ConstantAttr getTokenAttr(Token t) {
		if (t instanceof Variable || t instanceof Register)
			return varAttr.get(t.toSSAString());
		else if (t instanceof Constant)
			return new ConstantAttr(((Constant) t).getValue());
		else if (t instanceof Offset)
			return new ConstantAttr(((Offset) t).getValue());
		else if (t instanceof GP)
			return new ConstantAttr(ConstantType.Bottom);
		return null;
	}
	
	private void visitPhi(PhiNode phiNode) {
		
		List<Token> rhs = phiNode.getRHS();
		String name = phiNode.getLHS().get(0).toSSAString();
		ConstantAttr oldAttr = varAttr.get(name);
		ConstantAttr newAttr = ConstantAttr.operate(Stmt.Operator.phi, 
				getTokenAttr(rhs.get(0)),
				getTokenAttr(rhs.get(1)));
		
		for (int i = 2; i < rhs.size(); i++)
			newAttr = ConstantAttr.operate(Stmt.Operator.phi, newAttr, getTokenAttr(rhs.get(i)));
		
		if (!newAttr.equals(oldAttr)) {
			varAttr.put(name, newAttr);
			insertSSAWorkList(name);
		}
		
		System.out.println(phiNode.toSSAString() + " old:" + oldAttr + ", new:" + newAttr);
	}
	
	private void visitArith(ArithStmt stmt) {
		
		List<Token> rhs = stmt.getRHS();
		Token lhs = stmt.getLHS().get(0);
		String name = lhs.toSSAString();
		ConstantAttr newAttr;
		ConstantAttr oldAttr = getTokenAttr(lhs);
		
		Stmt.Operator op = stmt.getOperator();
		if (op == Stmt.Operator.neg)
			newAttr = ConstantAttr.operate(op, getTokenAttr(rhs.get(0)));
		else
			newAttr = ConstantAttr.operate(op, getTokenAttr(rhs.get(0)), getTokenAttr(rhs.get(1)));
		
		if (!newAttr.equals(oldAttr)) {
			varAttr.put(name, newAttr);
			insertSSAWorkList(name);
		}
		
		System.out.println(stmt.toSSAString() + " old:" + oldAttr + ", new:" + newAttr);
	}
	
	private void visitMove(MoveStmt stmt) {
		Token rhs = stmt.getRHS().get(0);
		Token lhs = stmt.getLHS().get(0);
		
		ConstantAttr oldAttr = getTokenAttr(lhs);
		ConstantAttr newAttr = getTokenAttr(rhs);
		
		if (!newAttr.equals(oldAttr)) {
			varAttr.put(lhs.toSSAString(), newAttr);
			insertSSAWorkList(lhs.toSSAString());
		}
		
		System.out.println(stmt.toSSAString() + " old:" + oldAttr + ", new:" + newAttr);
	}
	
	private void visitBranch(BranchStmt stmt) {
		int brVal;
		
		if (stmt.getOperator() == Stmt.Operator.blbc)
			brVal = 0;
		else if (stmt.getOperator() == Stmt.Operator.blbs)
			brVal = 1;
		else
			return;
		
		Register r = (Register) stmt.getRHS().get(0);
		ConstantAttr attr = varAttr.get(r.toSSAString());
		Block b = stmt.getBlock();
		
		if (attr.type == ConstantAttr.ConstantType.Bottom) {
			insertFlowWorkList(b, b.getSuccs().get(0));
			insertFlowWorkList(b, b.getSuccs().get(1));
		} else if (attr.type == ConstantAttr.ConstantType.Constant) {
			Block dst = ((Code) stmt.getRHS().get(1)).getDstStmt().getBlock();
			if (attr.value == brVal) {
				insertFlowWorkList(b, dst);
			} else {
				if (dst != b.getSuccs().get(0))
					insertFlowWorkList(b, b.getSuccs().get(0));
				else 
					insertFlowWorkList(b, b.getSuccs().get(1));
			}
		} else {
			System.out.println("ConstantPropOpt.visitBranch error: branch reg isn't initialized (" + stmt.toSSAString() + ")");
		}
	}
	
	private void visitOtherStmt(Stmt stmt) {
		if (stmt instanceof MemoryStmt) {
			ConstantAttr attr = getTokenAttr(stmt.getLHS().get(0));
			attr.type = ConstantType.Bottom;
		}
		// TODO
	}
	
	private void visitStmt(Stmt stmt) {
		if (stmt instanceof ArithStmt) {
			visitArith((ArithStmt) stmt);
		} else if (stmt instanceof MoveStmt) {
			visitMove((MoveStmt) stmt);
		} else if (stmt instanceof BranchStmt) {
			visitBranch((BranchStmt) stmt);
		} else if (stmt instanceof PhiNode) {
			visitPhi((PhiNode) stmt);
		} else {
			visitOtherStmt(stmt);
		}
	}
	
	private void visitStmt(Block b) {
//		System.out.println("visit block#" + b.index);
		
		for (PhiNode phiNode: b.getPhiNode())
			visitPhi(phiNode);
		
		for (Stmt stmt: b.body)
			visitStmt(stmt);
	}
	
	private int reachCount(Block block) {
		int count = 0;
		for (Block pred: block.getPreds())
			if (executeFlag.contains(getFlowEdgeString(pred, block)))
				count ++;
		if (executeFlag.contains(getFlowEdgeString(null, block)))
			count ++;
		
		return count;
	}
	
	public void optimize() {
		
		du.analyze();
		
		Block entryBlock = routine.getEntryBlock();
		
		Set<String> varName = du.getAllVarName();
		for (String s: varName)
			varAttr.put(s, new ConstantAttr(ConstantAttr.ConstantType.Top));
		
		for (Token t: entryBlock.body.get(0).getLHS())
			if (((Variable) t).getOffset() > 0) {
				ConstantAttr attr = varAttr.get(t.toSSAString());
				attr.type = ConstantAttr.ConstantType.Bottom;
			}
		
		FlowEdge entryEdge = new FlowEdge(null, entryBlock);
		flowWorkList.add(entryEdge);
		
		while (!flowWorkList.isEmpty() || !ssaWorkList.isEmpty()) {
			while (!flowWorkList.isEmpty()) {
				
				FlowEdge edge = flowWorkList.get(0);
				flowWorkList.remove(0);
				
				if (executeFlag.contains(getFlowEdgeString(edge)))
					continue;
				executeFlag.add(getFlowEdgeString(edge));
				
				Block block = edge.dst;
				
				int count = reachCount(block);
				if (count == 1)
					visitStmt(block);
				
				if (block.getSuccs().size() == 1)
					insertFlowWorkList(block, block.getSuccs().get(0));
				
			}
			
			while (!ssaWorkList.isEmpty()) {
				Stmt stmt = ssaWorkList.get(0);
				ssaWorkList.remove(0);
				
				Block block = stmt.getBlock();
				if (reachCount(block) > 0)
					visitStmt(stmt);
			}
		}
	}
	
	public void dump() {
		routine.dumpSSA();
		Set<String> keys = varAttr.keySet();
		for (String key: keys)
			System.out.println(key + ": " + varAttr.get(key));
	}
}
