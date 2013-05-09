package type;


public class Variable extends Token implements Cloneable {
	
	private String name;
	private String type;
	private int offset;
	
	// for ssa
	private String ssaName;
	
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
	
	public void setSSAName(String name) { this.ssaName = name; }
	
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
		return ssaName;
	}
	
	public boolean equals(Variable v) {
		return name.equals(v.getName());
	}
	
	public String debug() {
		return name + "#" + offset + ":" + type;
	}
	
	public Object clone() {
		
		Variable o = (Variable) super.clone();
		
		return o;
	}
}
