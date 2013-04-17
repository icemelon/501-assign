import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Routine {
	private int startLine;
	private int endLine;
	private String name;
	
	private List<Stmt> stmts;
	private List<BasicBlock> blocks = new LinkedList<BasicBlock>();
	
	public Routine(String name) {
		this.name = name;
	}
	
	public Routine(String name, int startLine) {
		this.name = name;
		this.startLine = startLine;
	}
	
	public Routine(String name, int startLine, int endLine) {
		this.name = name;
		this.startLine = startLine;
		this.endLine = endLine;
	} 
	
	public String getName() {
		return name;
	}
	
	public void setStartLine(int start) {
		startLine = start;
	}
	
	public void setEndLine(int end) {
		endLine = end;
	}
	
	public void setStmts(List<Stmt> s) {
		stmts = s;
	}
	
	public int getStartLine() {
		return startLine;
	}
	
	public int getEndLine() {
		return endLine;
	}
	
	public void parse() {
		Vector<Integer> bound = new Vector<Integer>();
		int begin = startLine;
		int end;
		
		for (Stmt stmt: stmts) {
			String instr = stmt.instr;
			if (instr.startsWith("br") ||
				instr.startsWith("blbc") ||
				instr.startsWith("blbs") ||
				instr.startsWith("ret")) {
				end = stmt.index;
				blocks.add(new BasicBlock(begin, end, stmts.subList(begin - startLine, end + 1 - startLine)));
				bound.add(begin);
				begin = end + 1;
			}
		}
		
		for (BasicBlock bb: blocks) {
			bb.dump();
		}
	}
	
	public void dump() {
		System.out.println("Method: " + name + "@[" + startLine + ", " + endLine + "]");
		for (Stmt stmt: stmts)
			stmt.dump();
	}
	
	public static Routine parseRoutine(String line) {
		String method = line.substring(7);
		String name = method.substring(0, method.indexOf('@'));
		int startLine = Integer.parseInt(method.substring(method.indexOf('@') + 1, method.indexOf(':')));
		
		return new Routine(name, startLine);
	}
}
