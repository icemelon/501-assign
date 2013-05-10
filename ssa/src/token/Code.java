package token;

import stmt.Stmt;

public class Code extends Token implements Cloneable {
	
	private int index;
	private Stmt dstStmt;
	
	public Code(int index) {
		this.index = index;
		this.dstStmt = null;
	}
	
	public int getIndex() { return index; }
	
	public void setDstStmt(Stmt s) { dstStmt = s; }
	
	@Override
	public String toString() {
		return "[" + dstStmt.index + "]";
	}
	
	@Override
	public String toIRString() {
		return "[" + dstStmt.index + "]";
	}
	
	@Override
	public String toSSAString() {
		return "[" + dstStmt.index + "]";
	}
	
	public Object clone() {
		Code o = (Code) super.clone();
		return o;
	}
}
