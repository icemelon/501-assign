
public class Stmt {
	public final int index;
	public final String instr;
	public final String op1, op2;
	public final String type;
	
	public Stmt(String line) {
		index = Integer.parseInt(line.substring(line.indexOf(' ') + 1, line.indexOf(':')));
		
		String[] tokens = line.substring(line.indexOf(':') + 1).trim().split(" ");
		instr = tokens[0];
			
		int length = tokens.length;
		if (tokens[length - 1].startsWith(":")) {
			type = tokens[length - 1].substring(1);
			--length;
		} else
			type = "";
		
		--length;
		if (length == 0)
			op1 = op2 = "";
		else if (length == 1) {
			op1 = tokens[1];
			op2 = "";
		} else {
			op1 = tokens[1];
			op2 = tokens[2];
		}
	}
	
	public void dump() {
		System.out.print("    instr " + index + ": " + instr);
		if (op1 != "")
			System.out.print(" " + op1);
		if (op2 != "")
			System.out.print(" " + op2);
		if (type != "")
			System.out.print(" :" + type);
		System.out.println();
	}
	
	public static int parseIndexFromOp(String op) {
		return Integer.parseInt(op.substring(1, op.length() - 1));
	}
}
