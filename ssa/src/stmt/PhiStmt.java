package stmt;

import java.util.LinkedList;

import token.Register;
import token.Token;
import token.Variable;

public class PhiStmt extends Stmt {
	public PhiStmt() {
		super(Operator.phi);
		lhs = new Register(index);
		rhs = new LinkedList<Token>();
	}
	
	public void addRHS(Variable v) { rhs.add(v); }
	
	@Override
	public String toString() {
		return "";
	}
	
	@Override
	public String toIRString() {
		return "";
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
