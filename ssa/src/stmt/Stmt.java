package stmt;

import java.util.LinkedList;
import java.util.List;

import attr.Attribute;

import token.Register;
import token.Token;

public abstract class Stmt implements Cloneable {
	
	public enum Operator {
		add("add"),
		sub("sub"),
		mul("mul"),
		div("div"),
		mod("mod"),
		neg("neg"),
		cmpeq("cmpeq"),
		cmple("cmple"),
		cmplt("cmplt"),
		isnull("isnull"),
		istype("istype"),
		br("br"),
		blbc("blbc"),
		blbs("blbs"),
		call("call"),
		load("load"),
		store("store"),
		move("move"),
		newtype("new"),
		newlist("newlist"),
		checknull("checknull"),
		checktype("checktype"),
		checkbounds("checkbounds"),
		lddynamic("lddynmaic"),
		stdynamic("stdynmaic"),
		write("write"),
		wrl("wrl"),
		enter("enter"),
		ret("ret"),
		param("param"),
		entrypc("entrypc"),
		nop("nop"),
		entry("entry"),
		phi("phi"),
		phinode("phinode");
		
		private String str;
		
		private Operator(String s) { this.str = s; }
		
		@Override
		public String toString() { return str; }
		
		public static Operator parseOp(String op) {
			if (op.equals("add"))
				return add;
			if (op.equals("sub"))
				return sub;
			if (op.equals("mul"))
				return mul;
			if (op.equals("div"))
				return div;
			if (op.equals("mod"))
				return mod;
			if (op.equals("neg"))
				return neg;
			if (op.equals("cmpeq"))
				return cmpeq;
			if (op.equals("cmple"))
				return cmple;
			if (op.equals("cmplt"))
				return cmplt;
			if (op.equals("isnull"))
				return isnull;
			if (op.equals("istype"))
				return istype;
			if (op.equals("br"))
				return br;
			if (op.equals("blbc"))
				return blbc;
			if (op.equals("blbs"))
				return blbs;
			if (op.equals("call"))
				return call;
			if (op.equals("load"))
				return load;
			if (op.equals("store"))
				return store;
			if (op.equals("move"))
				return move;
			if (op.equals("new"))
				return newtype;
			if (op.equals("newlist"))
				return newlist;
			if (op.equals("checknull"))
				return checknull;
			if (op.equals("checktype"))
				return checktype;
			if (op.equals("checkbounds"))
				return checkbounds;
			if (op.equals("lddynamic"))
				return lddynamic;
			if (op.equals("stdynamic"))
				return stdynamic;
			if (op.equals("write"))
				return write;
			if (op.equals("wrl"))
				return wrl;
			if (op.equals("enter"))
				return enter;
			if (op.equals("ret"))
				return ret;
			if (op.equals("param"))
				return param;
			if (op.equals("entrypc"))
				return entrypc;
			if (op.equals("nop"))
				return nop;
			if (op.equals("entry"))
				return entry;
			if (op.equals("phi"))
				return phi;
			if (op.equals("phinode"))
				return phinode;

			return null;
		}
	};
	
	public static int globalIndex;
	public final int index;
	protected final Operator op;
	protected List<Token> rhs = null;
	protected List<Token> lhs = null;
	protected Attribute attr = null;
	
	protected Stmt(int index, Operator op) {
		++globalIndex;
		this.index = index;
		this.op = op;
	}
	
	protected Stmt(Operator op) {
		this.index = ++globalIndex;
		this.op = op;
	}
	
	public Operator getOperator() { return op; }
	
	public List<Token> getRHS() { return rhs; }
	
	public List<Token> getLHS() { return lhs; }
	
	/*public List<Token> getOprands() {
		List<Token> oprands = new LinkedList<Token>(rhs);
		oprands.addAll(lhs);
		return oprands;
	}*/
	
	// assembly code
	public abstract String toString();
	
	// IR code
	public abstract String toIRString();

	// SSA code
	public abstract String toSSAString();
	
	public Attribute getAttr() { return attr; }
	
	public void setAttr(Attribute attr) { this.attr = attr; }
	
	@Override
	public Object clone() {
		
		BinopStmt o = null;
		try {
			o = (BinopStmt) super.clone();
			
			o.rhs = new LinkedList<Token>();
			for (Token t: rhs)
				o.rhs.add((Token) t.clone());
			
			o.lhs = new LinkedList<Token>();
			for (Token t: lhs)
				o.lhs.add((Token) t.clone());
//			o.lhs = (Token) lhs.clone();
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return o;
	}
	
	
/*	
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
	}*/
	
	public static Stmt parseStmt(String line) {
		
		int index;
		Operator op;
		List<Token> oprands = new LinkedList<Token>();
		
		index = Integer.parseInt(line.substring(6, line.indexOf(':')));
		String[] tokens = line.substring(line.indexOf(':') + 1).trim().split(" ");
		
		op = Operator.parseOp(tokens[0]);
		
		for (int i = 1; i < tokens.length; i++) {
			if (tokens[i].startsWith(":"))
				oprands.add(new Register(index, tokens[i].substring(1)));
			else if (tokens[i].length() > 0)
				oprands.add(Token.parseToken(tokens[i]));
		}
		
		Stmt stmt;
		switch (op) {
		case add: case sub: case mul: case div: case mod:
		case neg: case cmpeq: case cmple: case cmplt:
			stmt = new BinopStmt(index, op, oprands); break;
		case isnull: case istype:
			stmt = new ObjCmpStmt(index, op, oprands); break;
		case br: case blbc: case blbs:
			stmt = new BranchStmt(index, op, oprands); break;
		case call:
			stmt = new CallStmt(index, op, oprands); break;
		case load: case store:
			stmt = new MemoryStmt(index, op, oprands); break;
		case move:
			stmt = new MoveStmt(index, op, oprands); break;
		case newtype: case newlist:
			stmt = new AllocStmt(index, op, oprands); break;
		case checknull: case checktype: case checkbounds:
			stmt = new SafetyStmt(index, op, oprands); break;
		case lddynamic: case stdynamic:
			stmt = new DynamicStmt(index, op, oprands); break;
		case write: case wrl:
			stmt = new WriteStmt(index, op, oprands); break;
		case enter: case ret: case param:
			stmt = new StackStmt(index, op, oprands); break;
		case entrypc: case nop:
			stmt = new OtherStmt(index, op, oprands); break;
		default:
			System.out.println("Stmt parsing error!");
			stmt = null;
		}
		
		//System.out.println(stmt);
		return stmt;
	}
}
