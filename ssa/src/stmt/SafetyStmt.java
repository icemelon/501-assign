package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Register;
import token.Token;

public class SafetyStmt extends Stmt {
	
	public SafetyStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		if (op == Operator.checkbounds) {
			rhs = oprands;
			lhs = new LinkedList();
		} else if (op == Operator.checknull){
			rhs = oprands.subList(0, 1);
			lhs = oprands.subList(1, 2);
		} else { // checktype
			rhs = oprands.subList(0, 2);
			lhs = oprands.subList(2, 3);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		if (lhs.size() > 0)
			sb.append(" :" + ((Register) lhs.get(0)).getType());
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (lhs.size() > 0)
			sb.append(lhs.get(0).toIRString() + " := ");
		sb.append(op);
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (lhs.size() > 0)
			sb.append(lhs.get(0).toSSAString() + " := ");
		sb.append(op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
