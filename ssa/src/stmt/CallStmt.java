package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Token;

public class CallStmt extends Stmt {

	public CallStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands;
		lhs = new LinkedList();
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
		return toString();
	}
	
	@Override
	public String toSSAString() {
		return toString();
	}
}
