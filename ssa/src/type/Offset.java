package type;

public class Offset extends Token implements Cloneable {
	
	private String name;
	private int offset;
	private boolean dynamic;
	
	public Offset(String name, int offset) {
		this.name = name;
		this.offset = offset;
		this.dynamic = false;
	}
	
	public Offset(String name) {
		this.name = name;
		this.dynamic = true;
		this.offset = 0; // offset unavailable
	}
	
	@Override
	public String toString() {
		return (name + "#" + (dynamic ? "?" : offset));
	}
	
	@Override
	public String toIRString() {
		return (name + "#" + (dynamic ? "?" : offset));
	}
	
	@Override
	public String toSSAString() {
		return (name + "#" + (dynamic ? "?" : offset));
	}
	
	public Object clone() {
		Offset o = (Offset) super.clone();
		return o;
	}
}
