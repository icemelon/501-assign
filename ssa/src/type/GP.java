package type;

// global pointer
public class GP extends Token implements Cloneable {
	public GP() {
		
	}
	
	@Override
	public String toString() {
		return "GP";
	}
	
	@Override
	public String toSSAString() {
		return "GP";
	}
	
	public Object clone() {
		GP o = (GP) super.clone();
		return o;
	}
}
