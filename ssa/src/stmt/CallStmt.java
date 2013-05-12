package stmt;

import java.util.LinkedList;
import java.util.List;

import compiler.Routine;

import token.Token;

public class CallStmt extends Stmt {
	
	private Routine routine = null;

	public CallStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		rhs = oprands;
		lhs = new LinkedList();
	}
	
	public void setRoutine(Routine r) { routine = r; }
	
	public Routine getRoutine() { return routine; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		sb.append(" [" + routine.getEntryBlock().body.get(0).index + "]");
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
