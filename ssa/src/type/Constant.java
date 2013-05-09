package type;

public class Constant extends Token implements Cloneable {
	
	private int value;
	
	public Constant(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
	
	@Override
	public String toIRString() {
		return "" + value;
	}
	
	@Override
	public String toSSAString() {
		return "" + value;
	}
	
	public Object clone() {
		Constant o = (Constant) super.clone();
		return o;
	}
}
