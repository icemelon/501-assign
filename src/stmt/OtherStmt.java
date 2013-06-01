package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Token;

// entrypc, nop
public class OtherStmt extends Stmt {
	public OtherStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		rhs = new LinkedList<Token>();
		lhs = new LinkedList<Token>();
	}
	
	public OtherStmt(Operator op) {
		super(op);
		rhs = new LinkedList<Token>();
		lhs = new LinkedList<Token>();
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
