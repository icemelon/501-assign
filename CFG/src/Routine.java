import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Routine {
	private int startLine;
	private int endLine;
	private String name;
	
	private List<Stmt> stmts;
	private List<BasicBlock> blocks = new LinkedList<BasicBlock>();
	
	private int blockCount = 0;
	private BasicBlock entryBlock;
	
	private List<BasicBlock> postorder;
	private HashMap<BasicBlock, Boolean> visit;
	
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
	
	private BasicBlock searchBlock(int stmtIndex) {
		int left = 0;
		int right = blockCount - 1;
		int mid;
		
		while (true) {
			if (left == right)
				return blocks.get(left);
			
			mid = (left + right) / 2;
			BasicBlock block = blocks.get(mid);
			
			if (stmtIndex >= block.startLine && stmtIndex <= block.endLine)
				return block;
			if (stmtIndex < block.startLine)
				right = mid - 1;
			else
				left = mid + 1;
		}
	}
	
	// Find basic blocks and generate CFG
	public void parse() {
		Set<Integer> boundary = new TreeSet<Integer>();
		boundary.add(startLine);
		
		for (Stmt stmt: stmts) {
			String instr = stmt.instr;
			if (instr.equals("br")) {
				boundary.add(stmt.index + 1);
				boundary.add(Stmt.parseIndexFromOp(stmt.op1));
			} else if (instr.equals("blbc") || instr.equals("blbs")) {
				boundary.add(stmt.index + 1);
				boundary.add(Stmt.parseIndexFromOp(stmt.op2));
			}
		}
		
		Iterator<Integer> it = boundary.iterator();
		Integer begin = it.next();
		while (it.hasNext()) {
			Integer end = it.next();
			blocks.add(new BasicBlock(begin, end - 1, stmts.subList(begin - startLine, end - startLine)));
			begin = end;
		}
		blocks.add(new BasicBlock(begin, endLine, stmts.subList(begin - startLine, endLine - startLine + 1)));
		
		blockCount = blocks.size();
		
		for (int i = 0; i < blockCount; i++) {
			BasicBlock block = blocks.get(i);
			Stmt stmt = block.stmts.get(block.stmts.size() - 1);
			
			int brOp = 0;
			if (stmt.instr.equals("blbc") || stmt.instr.equals("blbs")) {
				brOp = Stmt.parseIndexFromOp(stmt.op2);
				
				BasicBlock b1 = blocks.get(i + 1); 
				BasicBlock b2 = searchBlock(brOp);
				
				block.addSucc(b1);
				b1.addPred(block);
				
				block.addSucc(b2);
				b2.addPred(block);
				
			} else if (stmt.instr.equals("br")) {
				brOp = Stmt.parseIndexFromOp(stmt.op1);
				BasicBlock b = searchBlock(brOp);
				
				block.addSucc(b);
				b.addPred(block);
			} else if (!stmt.instr.equals("ret")) {
				BasicBlock b = blocks.get(i + 1);
				
				block.addSucc(b);
				b.addPred(block);
			}
		}
		
		entryBlock = blocks.get(0);
	}
	
	private void dfs(BasicBlock b) {
		visit.put(b, true);
		
		for (BasicBlock child: b.getSuccs())
			if (!visit.get(child))
				dfs(child);
		
		postorder.add(b);
	}
	
	private void genPostorder() {
		visit = new HashMap<BasicBlock, Boolean>();
		postorder = new LinkedList<BasicBlock>();
		
		for (BasicBlock b: blocks)
			visit.put(b, false);
		
		dfs(entryBlock);
	}
	
	private BasicBlock intersect(BasicBlock b1, BasicBlock b2) {
		while (b1.index != b2.index) {
			while (b2.index < b1.index)
				b1 = b1.getIdom();
			while (b1.index < b2.index)
				b2 = b2.getIdom();
		}
		return b1;
	}
	
	public void genDom() {
		
		genPostorder();
		boolean changed = true;
		entryBlock.setIdom(entryBlock);
		
		while (changed) {
			changed = false;
			
			/*for (int i = blockCount - 1; i >= 0; --i) {
				BasicBlock b = postorder.get(i).getIdom();
				System.out.print((b == null ? "u" : b.index) + " ");
			}
			System.out.println("");*/
			
			for (int i = blockCount - 2; i >= 0; --i) {
				BasicBlock b = postorder.get(i);
				Iterator<BasicBlock> it = b.getPreds().iterator();
				
				BasicBlock newIdom = null;
				while (it.hasNext()) {
					BasicBlock tmp = it.next();
					if (tmp.getIdom() != null) {
						newIdom = tmp;
						break;
					}
				}
				
				while (it.hasNext()) {
					BasicBlock tmp = it.next();
					if (tmp.getIdom() != null) {
						newIdom = intersect(tmp, newIdom);
					}
				}
								
				if (b.getIdom() == null || b.getIdom() != newIdom) {
					b.setIdom(newIdom);
					changed = true;
				}
			}
		}
	}
	
	public void dump() {
		System.out.print("Method: " + name + " @[" + startLine + ", " + endLine + "]");
		System.out.println("  Entryblock #" + entryBlock.index);
		for (BasicBlock b: blocks) {
			System.out.println();
			b.dump();
		}
	}
	
	public static Routine parseRoutine(String line) {
		String method = line.substring(7);
		String name = method.substring(0, method.indexOf('@'));
		int startLine;
		if (method.indexOf(':') > 0)
			startLine = Integer.parseInt(method.substring(method.indexOf('@') + 1, method.indexOf(':')));
		else
			startLine = Integer.parseInt(method.substring(method.indexOf('@') + 1, method.length()));
		
		return new Routine(name, startLine);
	}
}
