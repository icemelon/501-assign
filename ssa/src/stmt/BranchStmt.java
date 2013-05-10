package stmt;

import java.util.List;

import token.Token;

// br, blbc, blbs
public class BranchStmt extends Stmt {

	public BranchStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands;
		lhs = null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t.toIRString());

		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());

		return sb.toString();
	}
}
