package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Token;

public class CountStmt extends Stmt {
	
	public CountStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands;
		lhs = new LinkedList();
	}
	
	public CountStmt(Token t) {
		super(Operator.count);
		rhs = new LinkedList();
		rhs.add(t);
		lhs = new LinkedList();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op + " " + rhs.get(0).toString());
		return sb.toString();
	}

	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op + " " + rhs.get(0).toIRString());
		return sb.toString();
	}

	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op + " " + rhs.get(0).toSSAString());
		return sb.toString();
	}

}
