package stmt;

import java.util.List;

import type.Register;
import type.Token;

public class SafetyStmt extends Stmt {
	
	public SafetyStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		if (op == Operator.checkbounds) {
			rhs = oprands;
			lhs = null;
		} else if (op == Operator.checknull){
			rhs = oprands.subList(0, 1);
			lhs = oprands.get(1);
		} else { // checktype
			rhs = oprands.subList(0, 2);
			lhs = oprands.get(2);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		if (lhs != null)
			sb.append(" :" + ((Register) lhs).getType());
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (lhs != null)
			sb.append(lhs.toIRString() + " := ");
		sb.append(op);
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (lhs != null)
			sb.append(lhs.toSSAString() + " := ");
		sb.append(op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}