package haichen;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BasicBlock {
	public static int GlobalIndex = 0;
	
	public final int index;
	public final int startLine;
	public final int endLine;
	public final List<Stmt> stmts;
	
	public List<Stmt> SSAstmts;
	
	private Set<BasicBlock> preds;
	private Set<BasicBlock> succs;
	private Set<BasicBlock> children; // in dominator tree
	
	private BasicBlock idom;
	
	public BasicBlock(int begin, int end, List<Stmt> s) {
		index = GlobalIndex++;
		//index = i;
		startLine = begin;
		endLine = end;
		stmts = s;
		
		preds = new HashSet<BasicBlock>();
		succs = new HashSet<BasicBlock>();
		idom = null;
	}
	
	public void addPred(BasicBlock b) { preds.add(b); }
	
	public void addSucc(BasicBlock b) { succs.add(b); }
	
	public Set<BasicBlock> getSuccs() { return succs; }
	
	public Set<BasicBlock> getPreds() { return preds; }
	
	public void setIdom(BasicBlock b) { idom = b; }
	
	public BasicBlock getIdom() { return idom; }
	
	public void dump() {
		System.out.print("Block #" + index + " @[" + startLine + ", " + endLine + "]");
		
		System.out.print("  Preds:");
		for (BasicBlock b: preds)
			System.out.print(" " + b.index);
		
		System.out.print(", Succs:");
		for (BasicBlock b: succs)
			System.out.print(" " + b.index);
		
		System.out.print(", Idom: ");
		if (idom != null)
			System.out.println(idom.index);
		else
			System.out.println();
		
		for (Stmt stmt: stmts)
			stmt.dump();
	}
}
