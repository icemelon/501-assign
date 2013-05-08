package type;

public class Register extends Token implements Cloneable {
	
	private int index;
	private String type;
	
	public Register(int index) {
		this.index = index;
	}
	
	public Register(int index, String type) {
		this.index = index;
		this.type = type;
	}
	
	public int getIndex() { return index; }
	
	public String getType() { return type; }
	
	@Override
	public String toString() {
		return "(" + index + ")";
	}
	
	@Override
	public String toSSAString() {
		return "(" + index + ")";
	}
	
	public Object clone() {
		Register o = null;
		
		o = (Register) super.clone();
		
		return o;
	}
}
