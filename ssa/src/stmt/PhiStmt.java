package stmt;

import java.util.LinkedList;

import compiler.Block;

import token.Register;
import token.Token;
import token.Variable;

public class PhiStmt extends Stmt {
	public PhiStmt(Block b) {
		super(Operator.phi);
		block = b;
		lhs = new LinkedList<Token>();
		lhs.add(new Register(index));
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
		sb.append(lhs.get(0).toSSAString() + " := " + op);
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
