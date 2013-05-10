package attr;

public class ConstantAttr extends Attribute {
	public enum Type {
		Top,
		Constant,
		Bottom;
	};
	
	private Type type;
	private int value;
	
	public ConstantAttr(Type type) {
		this.type = type;
	}
	
	public ConstantAttr(Type type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public boolean equals(ConstantAttr cAttr) {
		if (type == cAttr.getType()) {
			if (type == Type.Constant)
				return (value == cAttr.getValue());
			else
				return true;
		} else
			return false;
	}
	
	public void setType(Type type) { this.type = type; }
	
	public void setValue(int value) { this.value = value; }
	
	public Type getType() { return type; }
	
	public int getValue() { return value; }
}
