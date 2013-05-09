package stmt;

import java.util.List;

import type.Token;

// entrypc, nop
public class OtherStmt extends Stmt {
	public OtherStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		rhs = oprands;
		lhs = null;
	}
	
	@Override
	public String toString() {
		return "    instr " + index + ": " + op;
	}
	
	@Override
	public String toIRString() {
		return "    instr " + index + ": " + op;
	}
	
	@Override
	public String toSSAString() {
		return "    instr " + index + ": " + op;
	}
}
