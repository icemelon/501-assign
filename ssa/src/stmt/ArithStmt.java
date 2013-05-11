package stmt;

import java.util.List;

import token.Register;
import token.Token;

// add, sub, mul, div, mod, neg, cmpeq, cmple, cmplt

public class ArithStmt extends Stmt  {
	
	public ArithStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		if (op == Stmt.Operator.neg) {
			rhs = oprands.subList(0, 1);
			lhs = oprands.subList(1, 2);
		} else {
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
		sb.append(lhs.get(0).toSSAString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());

		return sb.toString();
	}
}
