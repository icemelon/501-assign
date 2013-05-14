package stmt;

import java.util.LinkedList;
import java.util.List;

import token.Register;
import token.Token;

// lddynamic,  stdynamic
public class DynamicStmt extends Stmt {
	
	public DynamicStmt(int index, Operator op, List<Token> oprands) {
		super(index, op);
		
		if (op == Operator.lddynamic) {
			rhs = oprands.subList(0, 2);
			lhs = oprands.subList(2, 3);
		} else { // stdynamic
			rhs = oprands;
			lhs = new LinkedList<Token>(); //oprands.subList(1, 3);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		
		if (op == Operator.lddynamic) {
			sb.append(" :" + ((Register) lhs.get(0)).type);
		} 
		
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		
		if (op == Operator.lddynamic)
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
		
		if (op == Operator.lddynamic)
			sb.append(lhs.get(0).toSSAString() + " := ");
		sb.append(op);
		
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		
		return sb.toString();
	}
}
