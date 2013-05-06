package haichen;

public class Token {
	public enum Type {
		Global_Pointer(0, "Global Pointer"),
		Local_variable(1, "Local Variable"),
		Constant(2, "Constant"),
		Field_Offset(3, "Field Offset"),
		Global_Offset(4, "Global Offset"),
		Register(5, "Register"),
		Code(6, "Code Label");
		
		private int value;
		private String name;
		
		private Type(int value, String name) {
			this.value = value;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	};
	
	public final Type type;
	private int value;
	private String name;
	private boolean dynamic;
	
	// GP
	private Token(Type type) {
		this.type = type;
	}
	
	// Constant & Code & Register
	private Token(Type type, int value) {
		this.type = type; 
		this.value = value;
	}
	
	// Local_Var & Global_Offset
	private Token(Type type, String name, int value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	// Field_Offset
	private Token(Type type, String name, int value, boolean dynamic) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.dynamic = dynamic;
	}
	
	public int getValue() { return value; }
	public String getName() { return name; }
	public boolean isDynamic() { return dynamic; }
	public boolean isConstant() {
		return (type == Type.Constant || type == Type.Field_Offset || type == Type.Global_Offset);
	}
	
	@Override
	public String toString() {
		String s = null;
		
		switch (type) {
		case Global_Pointer:
			s = "GP"; break;
		case Local_variable:
		case Global_Offset:
			s = name + "#" + value; break;
		case Constant:
			s = "" + value; break; 
		case Field_Offset:
			s = name + "#" + (dynamic ? "?" : value); break;
		case Register:
			s = "(" + value + ")"; break;
		case Code:
			s = "[" + value + "]"; break;
		default:
			s = "";
		}
		
		return s;
	}
	
	public void debug() {
		System.out.println(toString() + "(" + type + ")");
	}
	
	public static Token parseToken(String s) {
		Token t = null;
		
		try {
			int value = Integer.parseInt(s);
			return new Token(Type.Constant, value);
			
		} catch (NumberFormatException e) {
			
		}
		
		if (s.equals("GP")) {
			return new Token(Type.Global_Pointer);
		} else if (s.charAt(0) == '(') {
			int value = Integer.parseInt(s.substring(1, s.length() - 1));
			return new Token(Type.Register, value);
		} else if (s.charAt(0) == '[') {
			int value = Integer.parseInt(s.substring(1, s.length() - 1));
			return new Token(Type.Code, value);
		} else {
			int sep = s.indexOf('#');
			String name = s.substring(0, sep);
			String offset = s.substring(sep + 1, s.length());
			
			if (name.endsWith("_offset")) {
				try {
					int value = Integer.parseInt(offset);
					return new Token(Type.Field_Offset, name, value, false);
				} catch (NumberFormatException e) {
					return new Token(Type.Field_Offset, name, 0, true);
				}
			} else if (name.endsWith("_base")) {
				int value = Integer.parseInt(offset);
				return new Token(Type.Global_Offset, name, value);
			} else {
				int value = Integer.parseInt(offset);
				return new Token(Type.Local_variable, name, value);
			}
		}
	}
	
}
