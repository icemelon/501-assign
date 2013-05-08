package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import type.Token;
import type.Code;
import type.Variable;

public class Routine {
	private int startLine;
	private int endLine;
	private String name;
	
	private List<Variable> vars;
	private List<Stmt> stmts;
	private List<Block> blocks = new LinkedList<Block>();
	
	// SSA renaming
	private List<Stack<Integer>> varStack;
	private List<Integer> varCounter;
	
	private int blockCount = 0;
	private Block entryBlock;
	
	private Routine(String name, int startLine, List<Variable> vars) {
		this.name = name;
		this.startLine = startLine;
		this.vars = vars;
	}
	
	public String getName() { return name; }
	
	public void setStartLine(int start) { startLine = start; }
	
	public void setEndLine(int end) { endLine = end; }
	
	public void setStmts(List<Stmt> s) { stmts = s; }
	
	public int getStartLine() { return startLine; }
	
	public int getEndLine() { return endLine; }
	
	public List<Block> getBlocks() { return blocks; }

	public int getBlockCount() { return blockCount; }
	
	private Block searchBlock(int stmtIndex) {
		int left = 0;
		int right = blockCount - 1;
		int mid;
		
		while (true) {
			if (left == right)
				return blocks.get(left);
			
			mid = (left + right) / 2;
			Block block = blocks.get(mid);
			
			if (stmtIndex == block.startLine || stmtIndex == block.endLine)
				return block;
			if (stmtIndex < block.startLine)
				right = mid - 1;
			else
				left = mid + 1;
		}
	}
	
	// Find basic blocks and generate CFG
	public void genCFG() {
		Set<Integer> boundary = new TreeSet<Integer>();
		boundary.add(startLine);
		
		for (Stmt stmt: stmts) {
			String instr = stmt.getInstr();
			if (instr.equals("br")) {
				boundary.add(stmt.index + 1);
				boundary.add(((Code) stmt.getOprands().get(0)).getIndex());
			} else if (instr.equals("blbc") || instr.equals("blbs")) {
				boundary.add(stmt.index + 1);
				boundary.add(((Code) stmt.getOprands().get(1)).getIndex());
			}
		}
		
		Iterator<Integer> it = boundary.iterator();
		Integer begin = it.next();
		while (it.hasNext()) {
			Integer end = it.next();
			blocks.add(new Block(begin, end - 1, stmts.subList(begin - startLine, end - startLine), this));
			begin = end;
		}
		blocks.add(new Block(begin, endLine, stmts.subList(begin - startLine, endLine - startLine + 1), this));
		
		blockCount = blocks.size();
		
		for (int i = 0; i < blockCount; i++) {
			Block block = blocks.get(i);
			Stmt stmt = block.stmts.get(block.stmts.size() - 1);
			
			int brOp = 0;
			if (stmt.getInstr().equals("blbc") || stmt.getInstr().equals("blbs")) {
				brOp = ((Code) stmt.getOprands().get(1)).getIndex();
				
				Block b1 = blocks.get(i + 1); 
				Block b2 = searchBlock(brOp);
				
				block.addSucc(b1);
				b1.addPred(block);
				
				block.addSucc(b2);
				b2.addPred(block);
				
			} else if (stmt.getInstr().equals("br")) {
				brOp = ((Code) stmt.getOprands().get(0)).getIndex();
				Block b = searchBlock(brOp);
				
				block.addSucc(b);
				b.addPred(block);
			} else if (!stmt.getInstr().equals("ret")) {
				Block b = blocks.get(i + 1);
				
				block.addSucc(b);
				b.addPred(block);
			}
		}
		
		entryBlock = blocks.get(0);
	}
	
	private Block intersect(Block b1, Block b2) {
		while (b1.index != b2.index) {
			while (b2.index < b1.index)
				b1 = b1.getIdom();
			while (b1.index < b2.index)
				b2 = b2.getIdom();
		}
		return b1;
	}
	
	public void genDominator() {
		
		boolean changed = true;
		List<Block> postorder = Tools.genPostOrder(blocks);
		
		entryBlock.setIdom(entryBlock);
		
		while (changed) {
			changed = false;
			
			/*for (int i = blockCount - 1; i >= 0; --i) {
				BasicBlock b = postorder.get(i).getIdom();
				System.out.print((b == null ? "u" : b.index) + " ");
			}
			System.out.println("");*/
			
			for (int i = blockCount - 2; i >= 0; --i) {
				Block b = postorder.get(i);
				Iterator<Block> it = b.getPreds().iterator();
				
				Block newIdom = null;
				while (it.hasNext()) {
					Block tmp = it.next();
					if (tmp.getIdom() != null) {
						newIdom = tmp;
						break;
					}
				}
				
				while (it.hasNext()) {
					Block tmp = it.next();
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
		
		for (int i = 0; i < blockCount; i++) {
			Block b = postorder.get(i);
			
			if (b == entryBlock)
				continue;
			
			b.getIdom().addChild(b);
		}
	}
	
	public void genDomFrontier() {
		List<Block> topOrder = Tools.genTopOrder(blocks);
		
		for (Block block: topOrder) {
			for (Block succ: block.getSuccs()) {
				if (succ.getIdom() != block) {
					block.addDF(succ);
					//System.out.println("block " + block.index + " add " + succ.index);
				}
			}
			for (Block child: block.getChildren())
				for (Block df: child.getDF()) {
					if (df.getIdom() != block) {
						block.addDF(df);
						//System.out.println("block " + block.index + " add " + df.index + " through " + child.index);
					}
				}
		}
	}
	
	public void placePhi() {
		List<Block> workList = new LinkedList<Block>();
		Set<Block> everOnWorkList = new HashSet<Block>();
		Set<Block> hasAlready = new HashSet<Block>();
		
		for (Variable var: vars) {
			workList.clear();
			everOnWorkList.clear();
			hasAlready.clear();
			
			for (Block block: blocks)
				if (block.hasAssignment(var)) {
					workList.add(block);
					everOnWorkList.add(block);
				}
			
			while (!workList.isEmpty()) {
				Block block = workList.get(0);
				//System.out.println("Work on block " + block.index);
				workList.remove(0);
				for (Block df: block.getDF()) {
					if (!hasAlready.contains(df)) {
						df.insertPhi(var);
						hasAlready.add(df);
						if (!everOnWorkList.contains(df)) {
							everOnWorkList.add(df);
							workList.add(df);
						}
					}
				}
			}
		}
	}
	
	public void genSSAIndex(Variable v) {
		int i;
		for (i = 0; i < vars.size(); i++)
			if (vars.get(i).getName().equals(v.getName()))
				break;
		
		Integer index = varCounter.get(i);
		v.setSSAIndex(index);
		varStack.get(i).push(index);
		varCounter.set(i, index + 1);
	}
	
	public void setSSAIndex(Variable v) {
		int i;
		for (i = 0; i < vars.size(); i++)
			if (vars.get(i).getName().equals(v.getName()))
				break;
		
		Stack<Integer> stack = varStack.get(i);
		if (stack.isEmpty()) {
			entryBlock.insertParam(v.getName());
			stack.push(0);
			varCounter.set(i, 1);
		}
		v.setSSAIndex(varStack.get(i).peek());
	}
	
	public void popSSAIndex(Variable v) {
		int i;
		for (i = 0; i < vars.size(); i++)
			if (vars.get(i).getName().equals(v.getName()))
				break;
		
		varStack.get(i).pop();
	}
	
	public void rename() {
		varStack = new ArrayList<Stack<Integer>>();
		varCounter = new ArrayList<Integer>();
		
		for (int i = 0; i < vars.size(); i++) {
			varStack.add(new Stack<Integer>());
			varCounter.add(0);
		}
		
		entryBlock.rename();
	}
	
	public void dump() {
		System.out.print("Method: " + name + " @[" + startLine + ", " + endLine + "]");
		System.out.println("  Entryblock #" + entryBlock.index);
		for (Block b: blocks) {
			System.out.println();
			b.dump();
		}
	}
	
	public void dumpSSA() {
		System.out.print("Method: " + name + " @[" + startLine + ", " + endLine + "]");
		System.out.println("  Entryblock #" + entryBlock.index);
		for (Block b: blocks) {
			System.out.println();
			b.dumpSSA();
		}
	}
	
	public static Routine parseRoutine(String line) {
		String method = line.substring(7);
		String name = method.substring(0, method.indexOf('@'));
		int startLine = Integer.parseInt(method.substring(method.indexOf('@') + 1, method.indexOf(':')));
		String varstring = method.substring(method.indexOf(':') + 1, method.length());
		
		String vars[] = varstring.trim().split(" ");
		List<Variable> varList = new ArrayList<Variable>();
		for (String v: vars) 
			if (v.length() > 0)
				varList.add((Variable)Token.parseToken(v));
		
		/*for (Variable v: varList)
			System.out.println(v.debug());*/
		
		return new Routine(name, startLine, varList);
	}
}
