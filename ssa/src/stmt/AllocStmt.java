package stmt;

import java.util.List;

import token.Register;
import token.Token;

// new, newlist
public class AllocStmt extends Stmt {
	
	public AllocStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands.subList(0, 1);
		lhs = oprands.subList(1, 2);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		sb.append(" :" + ((Register) lhs.get(0)).getType());
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.get(0).toIRString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.get(0).toIRString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
