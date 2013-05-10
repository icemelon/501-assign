package stmt;

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
			rhs = oprands.subList(0, 1);
			lhs = oprands.subList(1, 3);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": " + op);
		for (Token t: rhs)
			sb.append(" " + t);
		
		if (op == Operator.lddynamic) {
			sb.append(" :" + ((Register) lhs.get(0)).getType());
		} else {
			sb.append(" " + lhs.get(0) + " " + lhs.get(1));
		}
		return sb.toString();
	}
	
	@Override
	public String toIRString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		
		for (Token t: lhs)
			sb.append(" " + t.toIRString());
		
		sb.append(" := " + op);
		
		for (Token t: rhs)
			sb.append(" " + t.toIRString());
		
		return sb.toString();
	}
	
	@Override
	public String toSSAString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("    instr " + index + ": ");
		
		for (Token t: lhs)
			sb.append(" " + t.toSSAString());
		
		sb.append(" := " + op);
		
		for (Token t: rhs)
			sb.append(" " + t.toSSAString());
		
		return sb.toString();
	}
}
