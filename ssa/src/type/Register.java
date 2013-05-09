package type;

public class Register extends Token implements Cloneable {
	
	private int index;
	private String type;
	private boolean pointer;
	
	public Register(int index) {
		this.index = index;
		this.type = null;
		this.pointer = false;
	}
	
	public Register(int index, String type) {
		this.index = index;
		this.type = type;
		if (type.endsWith("*"))
			this.pointer = true;
		else
			this.pointer = false;
	}
	
	public int getIndex() { return index; }
	
	public String getType() { return type; }
	
	public boolean isPointer() { return pointer; }
	
	@Override
	public String toString() {
		return "(" + index + ")";
	}
	
	@Override
	public String toIRString() {
		return "r" + index;
	}
	
	@Override
	public String toSSAString() {
		return "r" + index;
	}
	
	public Object clone() {
		Register o = null;
		
		o = (Register) super.clone();
		
		return o;
	}
}
