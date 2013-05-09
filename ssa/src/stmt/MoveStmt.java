package stmt;

import java.util.LinkedList;
import java.util.List;

import type.Token;
import type.Variable;

public class MoveStmt extends Stmt {

	public MoveStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		rhs = oprands.subList(0, 1);
		lhs = oprands.get(1);
	}
	
	public MoveStmt(Token rhs, Variable lhs) {
		super(Operator.move);
		this.rhs = new LinkedList();
		this.rhs.add(rhs);
		this.lhs = lhs;
	}
	
	//public void addRHS(Token t) { rhs.add(t); }
	
	//public void setLHS(Variable var) { lhs = var; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		sb.append(" " + lhs);
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.toIRString() + " :=");
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.toSSAString() + " :=");
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
