package compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import stmt.BranchStmt;
import stmt.EntryStmt;
import stmt.MoveStmt;
import stmt.PhiNode;
import stmt.Stmt;
import token.Token;
import token.Variable;

public class SSATransform {
	
	private Routine routine;
	private List<Block> blocks;
	private Block entryBlock;
	private List<Variable> localVars;
	
	// SSA renaming
	private Stack<String> varStack[];
	private int varCounter[];
	private List<Variable> ssaVars;
	
	public SSATransform(Routine r) {
		this.routine = r;
		this.blocks = routine.getBlocks();
		this.entryBlock = routine.getEntryBlock();
		this.localVars = routine.getLocalVars();
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
		
		// place entry stmt
		routine.getEntryBlock().body.add(0, new EntryStmt(localVars, entryBlock));
		
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
	
	private void genSSAName(Variable v) {
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
	
	private void setSSAName(Variable v) {
		int i;
		for (i = 0; i < localVars.size(); i++)
			if (localVars.get(i).equals(v))
				break;
		
		v.setSSAName(varStack[i].peek());
	}
	
	private void popSSAName(Variable v) {
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
			
			/*if (var.getOffset() > 0) {
				varCounter[i] = 1;
				String varName = var.getName() + "$" + "0";
				stack.add(varName);
				ssaVars.add(new Variable(varName));
			} else
				varCounter[i] = 0;*/
			varCounter[i] = 0;
			varStack[i] = stack;
		}
		
		renameBlock(entryBlock);
	}
	
	// recursive
	private void renameBlock(Block block) {
		
//		System.out.println("rename block#" + index);
		List<Variable> lhsList = new LinkedList<Variable>();
		List<PhiNode> phiNodeList = block.getPhiNode();
		
		for (int i = 0; i < phiNodeList.size(); i++) {
			PhiNode phiNode = phiNodeList.get(i);
			Variable lhs = (Variable) phiNode.getLHS().get(0);
			genSSAName(lhs);
			lhsList.add(lhs);
		}
		
		
		for (Stmt stmt: block.body) {
			for (Token t: stmt.getRHS())
				if (t instanceof Variable)
					setSSAName((Variable) t);
			for (Token t: stmt.getLHS()) {
				if (t instanceof Variable) {
					lhsList.add((Variable) t);
					genSSAName((Variable) t);
				}
			}
			//ssaStmts.add(stmt);
		}
		
		for (Block succ: block.getSuccs())
			updatePhiStmt(succ, block);
		
		for (Block child: block.getChildren())
			renameBlock(child);
		
		for (Variable v: lhsList)
			popSSAName(v);
	}
	
	private void updatePhiStmt(Block block, Block refBlock) {
		
		int i;
		List<Block> preds = block.getPreds();
		
		for (i = 0; i < preds.size(); i++)
			if (preds.get(i) == refBlock)
				break;
		for (PhiNode phiNode: block.getPhiNode()) {
			Variable refVar = new Variable(phiNode.getVarName());
			setSSAName(refVar);
			phiNode.setRHS(i, refVar);
		}
	}
	
	public void translateToSSA() {
		genDomFrontier();
		placePhi();
		rename();
	}
	
	private void insertPhiMoveStmt(Block block, Stmt stmt) {
		int size = block.body.size();
		if (block.body.get(size - 1) instanceof BranchStmt) {
			block.body.add(size - 1, stmt);
		} else 
			block.body.add(stmt);
	}
	
	private void removePhiNode() {
		for (Block b: blocks) {
			Iterator<PhiNode> it = b.getPhiNode().iterator();
			List<Block> preds = b.getPreds();
			while (it.hasNext()) {
				PhiNode phi = it.next();
				it.remove();
				Token lhs = phi.getLHS().get(0);
				List<Token> rhs = phi.getRHS();
				for (int i = 0; i < rhs.size(); i++) {
					MoveStmt moveStmt = new MoveStmt(rhs.get(i), lhs);
					insertPhiMoveStmt(preds.get(i), moveStmt);
				}
			}
		}
	}
	
	public void translateBackFromSSA() {
		removePhiNode();
	}
}
