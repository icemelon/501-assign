package attr;

public class ConstantAttr extends Attribute {
	enum Type {
		Top,
		Constant,
		Bottom;
	};
	
	private Type type;
	private int intValue;
	
	
}
