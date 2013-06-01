package stmt;

import java.util.LinkedList;

import compiler.Block;

import token.Token;
import token.Variable;

public class PhiNode extends Stmt {
	
	private PhiStmt phiStmt;
	private MoveStmt moveStmt;
	
	public PhiNode(String name, int count, Block b) {
		super(Stmt.Operator.phinode);
		
		lhs = new LinkedList<Token>();
		lhs.add(new Variable(name));
		rhs = new LinkedList<Token>();
		for (int i = 0; i < count; i++)
			rhs.add(null);
		
		block = b;
		phiStmt = new PhiStmt(b);
		phiStmt.rhs = rhs;
		moveStmt = new MoveStmt(phiStmt.getLHS(), lhs);
		moveStmt.setBlock(b);
	}
	
	public String getVarName() { return ((Variable) lhs.get(0)).name; }
	
	public void setRHS(int index, Variable var) { rhs.set(index, (Variable) var.clone()); }
	
	// ret: true->delete the phi node; false-> don't delete
	public boolean removeRHS(int index) {
		rhs.remove(index);
		if (rhs.size() == 1)
			return true;
		else
			return false;
	}

	@Override
	public String toString() { return ""; }

	@Override
	public String toIRString() { return ""; }

	@Override
	public String toSSAString() {
		return phiStmt.toSSAString() + "\n" + moveStmt.toSSAString();
	}
}
