package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Token;

// load, store
public class MemoryStmt extends Stmt {
	
	public MemoryStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		if (op == Operator.load) {
			rhs = oprands.subList(0, 1);
			lhs = oprands.subList(1, 2);
		} else {
			rhs = oprands;
			lhs = new LinkedList<Token>();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		if (op == Operator.load)
			sb.append(" " + lhs.get(0));
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		if (op == Operator.load)
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
		if (op == Operator.load)
			sb.append(lhs.get(0).toSSAString() + " := ");
		sb.append(op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
