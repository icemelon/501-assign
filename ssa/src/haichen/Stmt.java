package haichen;

public class Stmt {
	public final int index;
	public final String instr;
	public final Token oprand[] = new Token[2];
	public final String type;
	
	public Stmt(String line) {
		this.index = Integer.parseInt(line.substring(line.indexOf(' ') + 1, line.indexOf(':')));
		
		String[] tokens = line.substring(line.indexOf(':') + 1).trim().split(" ");
		int length = tokens.length;
		
		this.instr = tokens[0];
		
		if (tokens[length - 1].startsWith(":")) {
			type = tokens[length - 1].substring(1);
			--length;
		} else {
			type = null;
		}
		
		--length;
		for (int i = 0; i < length; i++)
			oprand[i] = Token.parseToken(tokens[i + 1]);
		/*for (; i < 2; i++)
			oprand[i] = null;*/
		/*if (length == 0)
			op1 = op2 = "";
		else if (length == 1) {
			op1 = tokens[1];
			op2 = "";
		} else {
			op1 = tokens[1];
			op2 = tokens[2];
		}*/
	}
	
	public void dump() {
		System.out.print("    instr " + index + ": " + instr);
		if (oprand[0] != null)
			System.out.print(" " + oprand[0]);
		if (oprand[1] != null)
			System.out.print(" " + oprand[1]);
		if (type != null)
			System.out.print(" :" + type);
		System.out.println();
	}
	
	public static int parseIndexFromOp(String op) {
		return Integer.parseInt(op.substring(1, op.length() - 1));
	}
}
