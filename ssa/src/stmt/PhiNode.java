package stmt;

import java.util.LinkedList;

import token.Token;
import token.Variable;

public class PhiNode extends Stmt {
	
	private PhiStmt phiStmt;
	private MoveStmt moveStmt;
	
	public PhiNode(String name, int count) {
		super(Stmt.Operator.phinode);
		
		lhs = new LinkedList<Token>();
		lhs.add(new Variable(name));
		rhs = new LinkedList<Token>();
		for (int i = 0; i < count; i++)
			rhs.add(null);
		
		phiStmt = new PhiStmt();
		phiStmt.rhs = rhs;
		moveStmt = new MoveStmt(phiStmt.getLHS(), lhs);
	}
	
	public String getVarName() { return ((Variable) lhs.get(0)).getName(); }
	
	public void setRHS(int index, Variable var) { rhs.set(index, var); }

	@Override
	public String toString() { return ""; }

	@Override
	public String toIRString() { return ""; }

	@Override
	public String toSSAString() {
		return phiStmt.toSSAString() + "\n" + moveStmt.toSSAString();
	}
}
