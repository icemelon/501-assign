package profile;

import java.util.LinkedList;
import java.util.List;                                         

import stmt.BranchStmt;
import stmt.CountStmt;
import stmt.Stmt;
import token.Constant;

import attr.EdgeAttr;

import compiler.Block;
import compiler.Routine;

public class Profile {
	
	
	private List<Edge> routineEdgeList = new LinkedList<Edge>();
	private Routine routine;
	private List<Block> blocks;
	
	public Profile(Routine routine) {
		this.routine = routine;
		this.blocks = routine.getBlocks();
		
		genEdges();
	}
	
	private void genEdges() {
		for (Block b: blocks) {
			for (Block succ: b.getSuccs()) {
				Edge e = new Edge(b, succ);
				routineEdgeList.add(e);
				Edge.ProfEdgeList.add(e);
				
				if (b.attr == null || !(b.attr instanceof EdgeAttr))
					b.attr = new EdgeAttr();
				((EdgeAttr) b.attr).addEdge(e);
				
				if (succ.attr == null || !(succ.attr instanceof EdgeAttr))
					succ.attr = new EdgeAttr();
				((EdgeAttr) succ.attr).addEdge(e);
			}
		}
	}
	
	private void profileEdge(Edge edge) {
		
		Block srcBlock = edge.src;
		Block dstBlock = edge.dst;
		Block profBlock = new Block(routine);
		
		// set up profile block
		{
			CountStmt count = new CountStmt(new Constant(edge.index));
			BranchStmt branch = new BranchStmt(dstBlock);
			profBlock.body.add(count);
			profBlock.body.add(branch);
		}
		
		// modify CFG
		Stmt lastStmt = srcBlock.body.get(srcBlock.body.size() - 1);
		
		if ((lastStmt instanceof BranchStmt) && (((BranchStmt) lastStmt).getBranchBlock() == dstBlock)) {
			
			((BranchStmt) lastStmt).setBranchBlock(profBlock);
			
		} else {
			srcBlock.setProfBranchStmt(new BranchStmt(profBlock));
		}
		
		blocks.add(profBlock);
		edge.profBlock = profBlock;
	}
	
	public void instrument() {
		for (Edge e: routineEdgeList) {
			profileEdge(e);
		}
	}
}
