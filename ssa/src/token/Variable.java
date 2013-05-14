package token;

public class Variable extends Token implements Cloneable {
	
	public String name;
	public String type;
	public int offset;	// 0 if not set; otherwise non-zero
	
	// for ssa
	public String ssaName;
	
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
		this.type = "";
		this.offset = 0;
	}
	
//	public void setSSAName(String name) { this.ssaName = name; }
	
	@Override
	public String toString() {
		return name + "#" + offset;
	}
	
	@Override
	public String toIRString() {
		return name + "#" + offset;
	}
	
	@Override
	public String toSSAString() {
		return ssaName; // + "(" + type + ")";
	}
	
	public String fullString() {
		return name + "#" + offset + ":" + type;
	}
	
	public boolean equals(Variable v) {
		return name.equals(v.name);
	}
	
	public Object clone() {
		
		Variable o = (Variable) super.clone();
		
		return o;
	}
}
