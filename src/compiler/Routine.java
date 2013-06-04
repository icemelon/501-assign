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

import profile.Profile;


import stmt.BranchStmt;
import stmt.EntryStmt;
import stmt.MoveStmt;
import stmt.Stmt;
import stmt.Stmt.Operator;
import token.Token;
import token.Code;
import token.Variable;

public class Routine extends Node {
	
	private int startLine;
	private int endLine;
	private String name;
	
	private List<Variable> localVars;
	private List<Stmt> body;
	private List<Block> blocks = new LinkedList<Block>();
	
	private int blockCount = 0;
	private Block entryBlock;
	
	public SSATransform ssaTrans = null;
	public ValueNumberOpt vn = null;
	public ConstantPropOpt cp = null;
	public Profile profile = null;
	
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
	
	public Block getEntryBlock() { return entryBlock; }
	
	public List<Variable> getLocalVars() { return localVars; }
	
	public void setLocalVars(List<Variable> vars) { localVars = vars; }
	
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
				boundary.add(((Code) stmt.getRHS().get(0)).getIndex());
			} else if (op == Operator.blbc || op == Operator.blbs) {
				boundary.add(stmt.index + 1);
				boundary.add(((Code) stmt.getRHS().get(1)).getIndex());
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
				brOp = ((Code) stmt.getRHS().get(1)).getIndex();
				
				Block b1 = blocks.get(i + 1); 
				Block b2 = searchBlock(brOp);
				
				block.addSucc(b1);
				b1.addPred(block);
				
				block.addSucc(b2);
				b2.addPred(block);
				
				((BranchStmt)stmt).setBranchBlock(b2);
				
			} else if (op == Operator.br) {
				brOp = ((Code) stmt.getRHS().get(0)).getIndex();
				Block b = searchBlock(brOp);
				
				block.addSucc(b);
				b.addPred(block);
				
				((BranchStmt)stmt).setBranchBlock(b);
				
			} else if (!(op == Operator.ret)) {
				Block b = blocks.get(i + 1);
				
				block.addSucc(b);
				b.addPred(block);
			}
		}
		
		for (Block b: blocks)
			for (Stmt s: b.body)
				s.setBlock(b);
		
		entryBlock = blocks.get(0);
	}
	
	private Block intersect(Block b1, Block b2) {
		while (b1.getIndex() != b2.getIndex()) {
			while (b2.getIndex() < b1.getIndex())
				b1 = b1.getIdom();
			while (b1.getIndex() < b2.getIndex())
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
		
//		for (int i = 0; i < blockCount - 1; ++i) {
//			Block b = postorder.get(i);
//			b.getIdom().addChild(b);
//		}
		
		for (int i = blockCount - 2; i >= 0; --i) {
			Block b = postorder.get(i);
			b.getIdom().addChild(b);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("    method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			sb.append(" " + v.fullString());
		return sb.toString();
	}
	
	public String dump() {
		StringBuilder sb = new StringBuilder();
		for (Block b: blocks) {
			sb.append(b.dump());
		}
		return sb.toString();
	}
	
	public String dumpIR() {
		/*System.out.print("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			System.out.print(" " + v.fullString());
		System.out.println();*/
		StringBuilder sb = new StringBuilder();
		for (Block b: blocks) {
			sb.append(b.dumpIR());
		}
		return sb.toString();
	}
	
	public String dumpCFG() {
		/*System.out.print("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			System.out.print(" " + v.fullString());
		System.out.println(" entryblock #" + entryBlock.index);*/
		StringBuilder sb = new StringBuilder();
		for (Block b: blocks) {
			sb.append("\n" + b.dumpCFG());
		}
		return sb.toString();
	}
	
	public String dumpSSA() {
		/*System.out.print("method " + name + "@" + startLine + ":");
		for (Variable v: localVars)
			System.out.print(" " + v.fullString());
		System.out.println(" entryblock #" + entryBlock.index);*/
		StringBuilder sb = new StringBuilder();
		for (Block b: blocks) {
			sb.append("\n" + b.dumpSSA());
		}
		return sb.toString();
	}
	
	public static Routine parse(String line) {
		String method = line.substring(7);
		String name = method.substring(0, method.indexOf('@'));
		int startLine = Integer.parseInt(method.substring(method.indexOf('@') + 1, method.indexOf(':')));
		String varstring = method.substring(method.indexOf(':') + 1, method.length());
		
		String vars[] = varstring.trim().split(" ");
		List<Variable> varList = new ArrayList<Variable>();
		for (String v: vars) 
			if (v.length() > 0)
				varList.add((Variable)Token.parse(v));
		
		/*for (Variable v: varList)
			System.out.println(v.debug());*/
		
		return new Routine(name, startLine, varList);
	}
}
