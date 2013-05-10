package stmt;

import java.util.List;

import token.Register;
import token.Token;

// lddynamic,  stdynamic
public class DynamicStmt extends Stmt {
	
	public DynamicStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		if (op == Operator.lddynamic) {
			rhs = oprands.subList(0, 2);
			lhs = oprands.get(2);
		} else { // stdynamic
			rhs = oprands;
			lhs = null;
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
		if (op == Operator.lddynamic) {
			sb.append(lhs.toIRString() + " := " + op);
			for (Token t: rhs)
				sb.append(" " + t);
		} else {
			sb.append(rhs.get(1).toIRString() + " " + rhs.get(2).toIRString() + " := " + op + " " + rhs.get(0).toIRString());
		}
		
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (op == Operator.lddynamic) {
			sb.append(lhs.toSSAString() + " := " + op);
			for (Token t: rhs)
				sb.append(" " + t);
		} else {
			sb.append(rhs.get(1).toSSAString() + " " + rhs.get(2).toSSAString() + " := " + op + " " + rhs.get(0).toSSAString());
		}
		
		return sb.toString();
	}
}
