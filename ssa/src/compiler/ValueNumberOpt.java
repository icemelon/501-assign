package compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import attr.ConstantAttr;
import attr.ValueNumberAttr;
import attr.ConstantAttr.ConstantType;

import stmt.AllocStmt;
import stmt.ArithStmt;
import stmt.DynamicStmt;
import stmt.EntryStmt;
import stmt.MemoryStmt;
import stmt.MoveStmt;
import stmt.ObjCmpStmt;
import stmt.PhiNode;
import stmt.SafetyStmt;
import stmt.Stmt;
import stmt.Stmt.Operator;
import token.Register;
import token.Token;
import token.Variable;

public class ValueNumberOpt {
	
	public class Expr {
		public Operator op;
		public String op1;
		public String op2;
		
		public Expr(Operator op, String op1, String op2) {
			this.op = op;
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public Expr(Operator op) {
			this.op = op;
		}
		
		@Override
		public String toString() {
			return op.toString() + " " + op1 + " " + op2;
		}
		
		public boolean equals(Object o) {
			Expr expr = (Expr) o;
			if (op != expr.op)
				return false;
			
			if ((op1.equals(expr.op1) && op2.equals(expr.op2)) ||
				(op1.equals(expr.op2) && op2.equals(expr.op1)))
				return true;
			
			return false;
		}
		
		public int hashCode() {
			int hash = 17031;
			int code = op.getIndex() * hash + op1.hashCode() + op2.hashCode();
			return code;
		}
	}
	
	private Routine routine;
	public int counter = 0;
	
	public ValueNumberOpt(Routine r) {
		this.routine = r;
	}
	
	private Token genNewToken(String val) {
		int pos;
		if ((pos = val.indexOf('$')) < 0) {
			int index = Integer.parseInt(val.substring(1));
			return new Register(index);
		} else {
			//System.out.println("ValueNumberOpt.genNewRegister error: parse in variable type (" + val + ")");
			String name = val.substring(0, pos);
			Variable v = new Variable(name);
			v.ssaName = val;
			return v;
		}
	}
	
	private void dumpValueNumber(HashMap<String, String> valueNumber) {
		Set<String> keySet = valueNumber.keySet();
		for (String key: keySet)
			System.out.println(key + ": " + valueNumber.get(key));
		
	}
	
	// PhiNode cannot call this function
	// only replace rhs token
	private String getValue(Stmt stmt, int index, HashMap<String, String> valueNumber) {
		String val;
		Token token = stmt.getRHS().get(index);
		if (token instanceof Variable || token instanceof Register) {
			val = valueNumber.get(token.toSSAString());
			if (val == null) {
				System.out.println("ValueNumberOpt.getValue error: Token not found " + token.toSSAString());
//				dumpValueNumber(valueNumber);
			} else if (!val.equals(token.toSSAString())) {
				Token newReg = genNewToken(val);
				stmt.setRHS(index, newReg);
			}
		} else
			val = token.toSSAString();
		return val;
	}
	
	// return: 0 -> keep same, 1 -> meaningless, 2 -> redundant
	private int visitPhiNode(PhiNode phi, HashMap<String, String> valueNumber, HashMap<Expr, String> exprHash) {
		
//		System.out.println("visit PhiNode:" + phi.toSSAString());
		
		boolean same = true;
		String lhs = phi.getLHS().get(0).toSSAString();
		
		for (Token t: phi.getRHS())
			if (t.getAttr() == null) {
				valueNumber.put(lhs, lhs);
				return 0;
			}

		String op1 = ((ValueNumberAttr)phi.getRHS().get(0).getAttr()).val;
		String op2 = ((ValueNumberAttr)phi.getRHS().get(1).getAttr()).val;
		
		if (op1.equals(op2)) {
//			System.out.println("Meaningless phi: " + lhs + " := " + op1);
			valueNumber.put(lhs, op1);
			return 1;
		}
		
		Expr expr = new Expr(Operator.phinode, op1, op2);
		
		if (exprHash.containsKey(expr)) {
//			System.out.println("Redundant phi: " + lhs + " := " + exprHash.get(expr));
			valueNumber.put(lhs, exprHash.get(expr));
			return 2;
		} else {
//			System.out.println("phi: " + lhs + " := " + lhs);
			exprHash.put(expr, lhs);
			valueNumber.put(lhs, lhs);
			return 0;
		}
	}
	
	private boolean visitArithStmt(ArithStmt stmt, HashMap<String, String> valueNumber, HashMap<Expr, String> exprHash) {
		
//		System.out.println("visit ArithStmt:" + stmt.toSSAString());
		
		if (stmt.getOperator() == Operator.neg)
			return false;
		
		Expr expr = new Expr(stmt.getOperator());
		expr.op1 = getValue(stmt, 0, valueNumber);
		expr.op2 = getValue(stmt, 1, valueNumber);
		
		String lhs = stmt.getLHS().get(0).toSSAString();
		
		if (exprHash.containsKey(expr)) {
			valueNumber.put(lhs, exprHash.get(expr));
			
//			System.out.println("remove stmt:" + stmt.toSSAString());
//			System.out.println("replace " + lhs + " by " + exprHash.get(expr));
			return true;
		} else {
			valueNumber.put(lhs, lhs);
			exprHash.put(expr, lhs);
			return false;
		}
	}
	
	private void visitMoveStmt(MoveStmt stmt, HashMap<String, String> valueNumber, HashMap<Expr, String> exprHash) {
		
//		System.out.println("visit MoveStmt:" + stmt.toSSAString());
		
		Token rhs = stmt.getRHS().get(0);
		String lhs = stmt.getLHS().get(0).toSSAString();
		
		if (rhs instanceof Variable || rhs instanceof Register) {
			String val = getValue(stmt, 0, valueNumber);
			valueNumber.put(lhs, val);
			
		} else
			valueNumber.put(lhs, lhs);
	}
	
	private void visitOtherStmt(Stmt stmt, HashMap<String, String> valueNumber, HashMap<Expr, String> exprHash) {
		if (stmt instanceof EntryStmt) {
			for (Token t: stmt.getLHS())
				valueNumber.put(t.toSSAString(), t.toSSAString());
		} else if (stmt instanceof MemoryStmt) {
			if (stmt.getOperator() == Operator.load) {
				String lhs = stmt.getLHS().get(0).toSSAString();
				valueNumber.put(lhs, lhs);
			}
		} else if (stmt instanceof DynamicStmt) {
			if (stmt.getOperator() == Operator.lddynamic) {
				String lhs = stmt.getLHS().get(0).toSSAString();
				valueNumber.put(lhs, lhs);
			}
		} else if (stmt instanceof AllocStmt) {
			// new, newlist on heap, it should be bottom
			String lhs = stmt.getLHS().get(0).toSSAString();
			valueNumber.put(lhs, lhs);
		} else if (stmt instanceof ObjCmpStmt) {
			// istype/isnull check, bottom
			String lhs = stmt.getLHS().get(0).toSSAString();
			valueNumber.put(lhs, lhs);
		} else if (stmt instanceof SafetyStmt) {
			// safety check, bottom
			if (stmt.getLHS().size() > 0) {
				String lhs = stmt.getLHS().get(0).toSSAString();
				valueNumber.put(lhs, lhs);
			}
		}
		
		for (int i = 0; i < stmt.getRHS().size(); i++)
			getValue(stmt, i, valueNumber);
	}
	
	private void DVNT(Block block, HashMap<String, String> vnParent, HashMap<Expr, String> exprParent) {
		
		HashMap<String, String> valueNumber = new HashMap<String, String>(vnParent);
		HashMap<Expr, String> exprHash = new HashMap<Expr, String>(exprParent);
		
		Iterator<PhiNode> itPhi = block.getPhiNode().iterator();
		while (itPhi.hasNext()) {
			PhiNode phiNode = itPhi.next();
			int ret = visitPhiNode(phiNode, valueNumber, exprHash);
			if (ret == 1) {
				itPhi.remove();
				++ counter;
				
				String val = ((ValueNumberAttr)phiNode.getRHS().get(0).getAttr()).val;
				List<Token> rhs = new LinkedList<Token>();
				List<Token> lhs = new LinkedList<Token>();
				rhs.add(genNewToken(val));
				lhs.add(phiNode.getLHS().get(0));
				
				Stmt mov = new MoveStmt(rhs, lhs);
				block.body.add(0, mov);
				
			} else if (ret == 2) {
				itPhi.remove();
				++ counter;
				
				String op1 = ((ValueNumberAttr)phiNode.getRHS().get(0).getAttr()).val;
				String op2 = ((ValueNumberAttr)phiNode.getRHS().get(1).getAttr()).val;
				Expr expr = new Expr(Operator.phinode, op1, op2);
				
				List<Token> rhs = new LinkedList<Token>();
				List<Token> lhs = new LinkedList<Token>();
				rhs.add(genNewToken(exprHash.get(expr)));
				lhs.add(phiNode.getLHS().get(0));

				Stmt mov = new MoveStmt(rhs, lhs);
				block.body.add(0, mov);
			}
		}
		
		Iterator<Stmt> itBody = block.body.iterator();
		while (itBody.hasNext()) {
			Stmt stmt = itBody.next();
			if (stmt instanceof ArithStmt) {
				boolean del = visitArithStmt((ArithStmt) stmt, valueNumber, exprHash);
				if (del) {
					++ counter;
					itBody.remove();
				}
			} else if (stmt instanceof MoveStmt)
				visitMoveStmt((MoveStmt) stmt, valueNumber, exprHash);
			else {
				visitOtherStmt(stmt, valueNumber, exprHash);
			}
		}
		
		for (Block succ: block.getSuccs()) {
			int i;
			for (i = 0; i < succ.getPreds().size(); i++)
				if (succ.getPreds().get(i) == block)
					break;
				
			for (PhiNode phi: succ.getPhiNode()) {
				Token t = phi.getRHS().get(i);
				if (valueNumber.containsKey(t.toSSAString())) {
					t.setAttr(new ValueNumberAttr(valueNumber.get(t.toSSAString())));
				}
			}
				
		}
		
		for (Block child: block.getChildren())
			DVNT(child, valueNumber, exprHash);
	}
	
	public void optimize() {
		DVNT(routine.getEntryBlock(), new HashMap<String, String>(), new HashMap<Expr, String>());
	}
	
	public void dump() {
		routine.dumpSSA();
	}
}
