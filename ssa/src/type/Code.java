package type;

public class Code extends Token implements Cloneable {
	
	private int index;
	
	public Code(int index) {
		this.index = index;
	}
	
	public int getIndex() { return index; }
	
	@Override
	public String toString() {
		return "[" + index + "]";
	}
	
	@Override
	public String toIRString() {
		return "[" + index + "]";
	}
	
	@Override
	public String toSSAString() {
		return "[" + index + "]";
	}
	
	public Object clone() {
		Code o = (Code) super.clone();
		return o;
	}
}
