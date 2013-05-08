package compiler;

import java.util.LinkedList;
import java.util.List;

import type.Register;
import type.Token;

public class Stmt implements Cloneable {
	public static int globalIndex;
	public final int index;
	private String instr;
	private List<Token> oprands;
	private String type;
	
	public Stmt(int index, String instr, List<Token> oprands, String type) {
		++globalIndex;
		this.index = index;
		this.instr = instr;
		this.oprands = oprands;
		this.type = type;
	}
	
	public Stmt(String instr) {
		this.index = ++globalIndex;
		this.instr = instr;
		this.oprands = new LinkedList<Token>();
		this.type = null;
	}
	
	public String getInstr() { return instr; }
	
	public List<Token> getOprands() { return oprands; }
	
	public String getType() { return type; }
	
	public void addOprands(Token t) { oprands.add(t); }
	
	public void setInstr(String instr) { this.instr = instr; }
	
	public void setType(String type) { this.type = type; }
	
	public List<Token> getRHS() {
		if (instr.equals("add") || instr.equals("sub") || instr.equals("mul") ||
			instr.equals("div") || instr.equals("mod") || instr.equals("neg") ||
			instr.equals("cmpeq") || instr.equals("cmple") || instr.equals("cmplt") ||
			instr.equals("isnull") || instr.equals("istype") || instr.equals("load") ||
			instr.equals("checknull") || instr.equals("checktype") || instr.equals("checkbounds") ||
			instr.equals("lddynamic") || instr.equals("write") || instr.equals("param")) {
			return oprands;
		} else if (instr.equals("store") || instr.equals("move") || instr.equals("stdynamic")) {
			return oprands.subList(0, 1);
		} else
			return new LinkedList<Token>();
	}
	
	public List<Token> getLHS() {
		if (instr.equals("store") || instr.equals("move")) {
			return oprands.subList(1, 2);
		} else if (instr.equals("stdynamic")) {
			return oprands.subList(1, 3);
		} else
			return new LinkedList<Token>();
	}
	
	public void dump() {
		System.out.print("    instr " + index + ": " + instr);
		for (Token t: oprands)
			System.out.print(" " + t);
		if (type != null)
			System.out.print(" :" + type);
		System.out.println();
	}
	
	public void dumpSSA() {
		System.out.print("    instr " + index + ": " + instr);
		for (Token t: oprands)
			System.out.print(" " + t.toSSAString());
		System.out.println();
	}
	
	public Object clone() {
		
		Stmt o = null;
		try {
			o = (Stmt) super.clone();
			
			o.oprands = new LinkedList<Token>();
			for (Token t: oprands) {
				o.oprands.add((Token) t.clone());
			}
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return o;
	}
	
	public static Stmt parseStmt(String line) {
		
		int index;
		String instr;
		List<Token> oprands = new LinkedList<Token>();
		String type = null;
		
		index = Integer.parseInt(line.substring(6, line.indexOf(':')));
		
		String[] tokens = line.substring(line.indexOf(':') + 1).trim().split(" ");
		int size = tokens.length;
		
		instr = tokens[0];
		--size;
		
		if (tokens[tokens.length - 1].startsWith(":")) {
			type = tokens[tokens.length - 1].substring(1);
			--size;
		}
		
		for (int i = 1; i <= size; i++)
			oprands.add(Token.parseToken(tokens[i]));
		
		return new Stmt(index, instr, oprands, type);
	}
}
