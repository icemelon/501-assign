
public class Stmt {
	public final int index;
	public final String instr;
	//public final String op1, op2;
	//public final String type;
	
	public Stmt(int index, String instr) {
		this.index = index;
		this.instr = instr;
	}
	
	public void dump() {
		System.out.println(index + ": " + instr);
	}
	
	public static Stmt parseStmt(String line) {
		int index = Integer.parseInt(line.substring(line.indexOf(' ') + 1, line.indexOf(':')));
		String instr = line.substring(line.indexOf(':') + 2);
		
		//System.out.println(index + ": " + instr);
		
		return new Stmt(index, instr);
	}
}
