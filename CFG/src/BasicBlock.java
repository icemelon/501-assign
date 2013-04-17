import java.util.List;


public class BasicBlock {
	public static int GlobalIndex = 0;
	
	private final int index;
	private int startLine;
	private int endLine;
	private List<Stmt> stmts;
	
	public BasicBlock(int begin, int end, List<Stmt> s) {
		index = GlobalIndex++;
		startLine = begin;
		endLine = end;
		stmts = s;
	}
	
	public void dump() {
		System.out.println("Block#" + index + " @[" + startLine + ", " + endLine + "]");
		for (Stmt stmt: stmts)
			stmt.dump();
	}
}
