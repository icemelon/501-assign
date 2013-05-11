package stmt;

import java.util.LinkedList;
import java.util.List;

import compiler.Block;

import token.Token;
import token.Variable;

public class EntryStmt extends Stmt {
	
	public EntryStmt(List<Variable> varList, Block b) {
		super(Stmt.Operator.entry);
		
		block = b;
		
		rhs = new LinkedList<Token>();
		lhs = new LinkedList<Token>(varList);
		
//		for (Variable v: varList)
//			lhs.add(v); //.clone());
	}
	
	//@Override
	//public List<Token> getRHS() { return new LinkedList<Token>(); }
	
	@Override
	public String toString() { return ""; }

	@Override
	public String toIRString() { return ""; }

	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("    instr " + index + ": " + op);
		for (Token t: lhs)
			sb.append(" " + t.toSSAString());
		return sb.toString();
	}
}
