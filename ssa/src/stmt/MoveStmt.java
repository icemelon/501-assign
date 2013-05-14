package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Token;
import token.Variable;

public class MoveStmt extends Stmt {

	public MoveStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		rhs = oprands.subList(0, 1);
		lhs = oprands.subList(1, 2);
	}
	
	public MoveStmt(List<Token> rhs, List<Token> lhs) {
		super(Operator.move);
		this.rhs = rhs;
		this.lhs = lhs;
	}
	
	public MoveStmt(Token rhs, Token lhs) {
		super(Operator.move);
		this.rhs = new LinkedList<Token>(); 
		this.rhs.add((Token) rhs.clone());
		this.lhs = new LinkedList<Token>();
		this.lhs.add((Token) lhs.clone());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		sb.append(" " + rhs.get(0));
		sb.append(" " + lhs.get(0));
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.get(0).toIRString() + " := " + rhs.get(0).toIRString());
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		sb.append(lhs.get(0).toSSAString() + " := " + rhs.get(0).toSSAString());
		return sb.toString();
	}
}
