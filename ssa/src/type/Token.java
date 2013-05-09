package type;

public abstract class Token implements Cloneable {
	
	public static Token parseToken(String s) {
		
		try {
			int value = Integer.parseInt(s);
			return new Constant(value);
			
		} catch (NumberFormatException e) {
			
		}
		
		if (s.equals("GP")) {
			return new GP();
		} else if (s.charAt(0) == '(') {
			int value = Integer.parseInt(s.substring(1, s.length() - 1));
			return new Register(value);
		} else if (s.charAt(0) == '[') {
			int value = Integer.parseInt(s.substring(1, s.length() - 1));
			return new Code(value);
		} else {
			int sep = s.indexOf('#');
			String name = s.substring(0, sep);
			String offset = s.substring(sep + 1, s.length());
			
			if (name.endsWith("_offset") || name.endsWith("_base") || name.endsWith("_type")) {
				try {
					int value = Integer.parseInt(offset);
					return new Offset(name, value);
				} catch (NumberFormatException e) {
					return new Offset(name);
				}
			} else {
				int index = offset.indexOf(':');
				
				if (index > 0) {
					int value = Integer.parseInt(offset.substring(0, index));
					String type = offset.substring(index + 1, offset.length());
					return new Variable(name, type, value);
				} else {
					int value = Integer.parseInt(offset);
					return new Variable(name, value);
				}
			}
		}
	}
	
	public Object clone() {
		Token o = null;
		try {
			o = (Token) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return o;
	}
	
	abstract public String toString();
	
	abstract public String toIRString();
	
	abstract public String toSSAString();	
}
