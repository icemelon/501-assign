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


import stmt.BranchStmt;
import stmt.MoveStmt;
import stmt.Stmt;
import stmt.Stmt.Operator;
import token.Token;
import token.Code;
import token.Variable;

public class Routine {
	private int startLine;
	private int endLine;
	private String name;
	
	private List<Variable> localVars;
	private List<Stmt> body;
	private List<Block> blocks = new LinkedList<Block>();
	
	// SSA renaming
	private Stack<String> varStack[];
	private int varCounter[];
	private List<Variable> ssaVars;
	
	private int blockCount = 0;
	private Block entryBlock;
	
	private Routine(String name, int startLine, List<Variable> vars) {
		this.name = name;
		this.startLine = startLine;
		this.localVars = vars;
	}
	
	public String getName() { return name; }
	
	public void setStartLine(int start) { startLine = start; }
	
	public void setEndLine(int end) { endLine = end; }
	
	public void setBody(List<Stmt> stmts) { body = stmts; }
	
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
		
		for (Stmt stmt: body) {
			Operator op = stmt.getOperator();
			if (op == Operator.br) {
				boundary.add(stmt.index + 1);
				boundary.add(((Code) stmt.getOprands().get(0)).getIndex());
			} else if (op == Operator.blbc || op == Operator.blbs) {
				boundary.add(stmt.index + 1);
				boundary.add(((Code) stmt.getOprands().get(1)).getIndex());
			}
		}
		
		Iterator<Integer> it = boundary.iterator();
		Integer begin = it.next();
		while (it.hasNext()) {
			Integer end = it.next();
			blocks.add(new Block(begin, end - 1, body.subList(begin - startLine, end - startLine), this));
			begin = end;
		}
		blocks.add(new Block(begin, endLine, body.subList(begin - startLine, endLine - startLine + 1), this));
		
		blockCount = blocks.size();
		
		for (int i = 0; i < blockCount; i++) {
			Block block = blocks.get(i);
			Stmt stmt = block.body.get(block.body.size() - 1);
			
			int brOp = 0;
			Operator op = stmt.getOperator();
			if (op == Operator.blbc || op == Operator.blbs) {
				brOp = ((Code) stmt.getOprands().get(1)).getIndex();
				
				Block b1 = blocks.get(i + 1); 
				Block b2 = searchBlock(brOp);
				
				block.addSucc(b1);
				b1.addPred(block);
				
				block.addSucc(b2);
				b2.addPred(block);
				
			} else if (op == Operator.br) {
				brOp = ((Code) stmt.getOprands().get(0)).getIndex();
				Block b = searchBlock(brOp);
				
				block.addSucc(b);
				b.addPred(block);
			} else if (!(op == Operator.ret)) {
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
	
	private void genDomFrontier() {
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
	
	private void placePhi() {
		List<Block> workList = new LinkedList<Block>();
		Set<Block> everOnWorkList = new HashSet<Block>();
		Set<Block> hasAlready = new HashSet<Block>();
		
		for (Variable var: localVars) {
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
						df.insertPhiNode(var);
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
	
	public void genSSAName(Variable v) {
		int i;
		for (i = 0; i < localVars.size(); i++)
			if (localVars.get(i).equals(v))
				break;
		
		Integer index = varCounter[i]++;
		String name = v.getName() + "$" + index;
		v.setSSAName(name);
		varStack[i].push(name);
		ssaVars.add(new Variable(name));
	}
	
	public void setSSAName(Variable v) {
		int i;
		for (i = 0; i < localVars.size(); i++)
			if (localVars.get(i).equals(v))
				break;
		
		Stack<String> stack = varStack[i];
		if (stack.isEmpty()) {
			//entryBlock.insertParam(v.getName());
			stack.push(v.getName() + "$0");
			varCounter[i] = 1;
		}
		v.setSSAName(stack.peek());
	}
	
	public void popSSAName(Variable v) {
		int i;
		for (i = 0; i < localVars.size(); i++)
			if (localVars.get(i).equals(v))
				break;
		
		varStack[i].pop();
	}
	
	private void rename() {
		int n = localVars.size();
		varStack = new Stack[n];
		varCounter = new int[n];
		ssaVars = new ArrayList<Variable>();
		
		for (int i = 0; i < n; i++) {
			Variable var = localVars.get(i);
			Stack<String> stack = new Stack<String>(); 
			
			if (var.getOffset() > 0) {
				varCounter[i] = 1;
				String varName = var.getName() + "$" + "0";
				stack.add(varName);
				ssaVars.add(new Variable(varName));
			} else
				varCounter[i] = 0;
			
			varStack[i] = stack;
		}
		
		entryBlock.rename();
	}
	
	public void genSSA() {
		genDomFrontier();
		placePhi();
		rename();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			sb.append(" " + v.fullString());
		return sb.toString();
	}
	
	public void dump() {
		System.out.println(toString());
		System.out.println("Entryblock #" + entryBlock.index);
		for (Block b: blocks) {
			System.out.println();
			b.dump();
		}
	}
	
	public void dumpIR() {
		System.out.print("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			System.out.print(" " + v.fullString());
		System.out.println("\nEntryblock #" + entryBlock.index);
		for (Block b: blocks) {
			System.out.println();
			b.dumpIR();
		}
	}
	
	public void dumpSSA() {
		System.out.print("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			System.out.print(" " + v.fullString());
		System.out.println("\nEntryblock #" + entryBlock.index);
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
