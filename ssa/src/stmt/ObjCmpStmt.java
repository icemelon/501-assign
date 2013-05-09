package stmt;

import java.util.List;

import type.Register;
import type.Token;

// istype, isnull
public class ObjCmpStmt extends Stmt implements Cloneable {

	public ObjCmpStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands.subList(0, 2);
		lhs = oprands.get(2);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		sb.append(" :" + ((Register) lhs).getType());
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.toIRString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.toSSAString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
