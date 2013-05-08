package type;

import compiler.Block;


public class Variable extends Token implements Cloneable {
	
	private String name;
	private String type;
	private int offset;
	
	// for ssa
	private int ssaIndex = -1;
	
	public Variable(String name, int offset) {
		this.name = name;
		this.type = null;
		this.offset = offset;
	}
	
	public Variable(String name, String type, int offset) {
		this.name = name;
		this.type = type;
		this.offset = offset;
	}
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
	
	public String getType() { return type; }
	
	public int getOffset() { return offset; }
	
	public void setName(String name) { this.name = name; }
	
	public void setSSAIndex(int index) { ssaIndex = index; }
	
	@Override
	public String toString() {
		return name + "#" + offset;
	}
	
	@Override
	public String toSSAString() {
		return name + "$" + ssaIndex;
	}
	
	public String debug() {
		return name + "#" + offset + ":" + type;
	}
	
	public Object clone() {
		
		Variable o = (Variable) super.clone();
		
		o.name = new String(name);
		
		return o;
	}
}
