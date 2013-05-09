package compiler;

import stmt.MoveStmt;
import stmt.PhiStmt;
import type.Variable;

public class PhiNode {
	
	private PhiStmt phiStmt;
	private MoveStmt moveStmt;
	private String varName;
	private Variable[] refVars;
	
	public PhiNode(String name, int count) {
		varName = name;
		phiStmt = new PhiStmt();
		moveStmt = new MoveStmt(phiStmt.getLHS(), new Variable(name));
		refVars = new Variable[count];
	}
	
	public String getVarName() { return varName; }
	
	public PhiStmt getPhiStmt() { return phiStmt; }
	
	public MoveStmt getMoveStmt() { return moveStmt; }
	
	public Variable[] getRefVars() { return refVars; }
	
	public void dump() {
		for (int i = 0; i < refVars.length; i++) {
			phiStmt.addRHS(refVars[i]);
		}
			
		System.out.println(phiStmt.toSSAString());
		System.out.println(moveStmt.toSSAString());
	}
}
