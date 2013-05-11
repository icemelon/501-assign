package attr;

import stmt.Stmt;

public class ConstantAttr extends Attribute {
	public enum ConstantType {
		Top,
		Constant,
		Bottom;
	}
	
	public ConstantType type;
	public int value;
	
	public ConstantAttr(ConstantType type) {
		this.type = type;
	}
	
	public ConstantAttr(ConstantType type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public ConstantAttr(int value) {
		this.type = ConstantType.Constant;
		this.value = value;
	}
	
	public ConstantAttr(ConstantAttr attr) {
		this.type = attr.type;
		this.value = attr.value;
	}
	
	public boolean equals(ConstantAttr cAttr) {
		if (type == cAttr.type) {
			if (type == ConstantType.Constant)
				return (value == cAttr.value);
			else
				return true;
		} else
			return false;
	}
	
	public static int constantCalc(Stmt.Operator op, int x, int y) {
		int ret;
		switch (op) {
		case add: ret = x + y; break;
		case sub: ret = x - y; break;
		case mul: ret = x * y; break;
		case div: ret = x / y; break;
		case mod: ret = x % y; break;
		case cmpeq: ret = (x == y) ? 1 : 0; break;
		case cmple: ret = (x <= y) ? 1 : 0; break;
		case cmplt: ret = (x < y) ? 1 : 0; break;
		default:
			System.out.println("ConstantAttr.constantCalc error: unsupported operator");
			ret = 1234567;
		}
		return ret;
	}
	
	public static ConstantAttr operate(Stmt.Operator op, ConstantAttr a, ConstantAttr b) {
		ConstantAttr attr = null;
		switch (op) {
		case phi:
			if (a.type == ConstantType.Bottom || b.type == ConstantType.Bottom)
				attr = new ConstantAttr(ConstantType.Bottom);
			else if (a.type == ConstantType.Top)
				attr = new ConstantAttr(b);
			else if (b.type == ConstantType.Top)
				attr = new ConstantAttr(a);
			else {
				if (a.value == b.value)
					attr = new ConstantAttr(a);
				else
					attr = new ConstantAttr(ConstantType.Bottom);
			}
			break;
		case mul:
			if ((a.type == ConstantType.Bottom && b.type == ConstantType.Top) ||
				(b.type == ConstantType.Bottom && a.type == ConstantType.Top))
				attr = new ConstantAttr(0);
			else if ((a.type == ConstantType.Constant && a.value == 0) ||
					 (b.type == ConstantType.Constant && b.value == 0))
				attr = new ConstantAttr(0);
			else if (a.type == ConstantType.Top || b.type == ConstantType.Top)
				attr = new ConstantAttr(ConstantType.Top);
			else if (a.type == ConstantType.Bottom || b.type == ConstantType.Bottom)
				attr = new ConstantAttr(ConstantType.Bottom);
			else
				attr = new ConstantAttr(constantCalc(op, a.value, b.value));
			break;
		case add: case sub: case div: case mod:
		case cmpeq: case cmple: case cmplt:
			if ((a.type == ConstantType.Bottom && b.type == ConstantType.Top) ||
				(b.type == ConstantType.Bottom && a.type == ConstantType.Top))
					attr = new ConstantAttr(0);
			else if (a.type == ConstantType.Top || b.type == ConstantType.Top)
				attr = new ConstantAttr(ConstantType.Top);
			else if (a.type == ConstantType.Bottom || b.type == ConstantType.Bottom)
				attr = new ConstantAttr(ConstantType.Bottom);
			else
				attr = new ConstantAttr(constantCalc(op, a.value, b.value));
			break;
		default:
			System.out.println("ConstantAttr.operate(op, attr) error: unsupported operator");
		}
		
		return attr;
	}
	
	public static ConstantAttr operate(Stmt.Operator op, ConstantAttr a) {
		ConstantAttr attr = null;
		if (op == Stmt.Operator.neg) {
			if (a.type == ConstantType.Constant)
				attr = new ConstantAttr(-a.value);
			else
				attr = new ConstantAttr(a);
		} else
			System.out.println("ConstantAttr.operate(op) error: unsupported operator");
		
		return attr;
	}
	
	
	
	@Override
	public String toString() {
		
		String ret = null;
		switch (type) {
		case Top:
			ret = "Top"; break;
		case Constant:
			ret = "" + value; break;
		case Bottom:
			ret = "Bottom"; break;
		}
		return ret;
	}
}
