package profile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;                                         
import java.util.Set;

import stmt.BranchStmt;
import stmt.CountStmt;
import stmt.Stmt;
import token.Constant;

import attr.BlockEdgeAttr;

import compiler.Block;
import compiler.Routine;

public class CFGProfile {
	
	public static List<Edge> ProfEdgeList = new LinkedList<Edge>();
	
	private List<Edge> localEdgeList = new LinkedList<Edge>();
	private Routine routine;
	private List<Block> blocks;
	
	public CFGProfile( Routine routine ) {
		this.routine = routine;
		this.blocks = routine.getBlocks();
		
		genEdges();
	}
	
	private void genEdges() {
		for ( Block b: blocks ) {
			for ( Block succ: b.getSuccs() ) {
				Edge e = new Edge( b, succ );
				localEdgeList.add( e );
				ProfEdgeList.add( e );
				
				if (b.attr == null || !(b.attr instanceof BlockEdgeAttr))
					b.attr = new BlockEdgeAttr();
				((BlockEdgeAttr) b.attr).addEdge(e);
				
				if (succ.attr == null || !(succ.attr instanceof BlockEdgeAttr))
					succ.attr = new BlockEdgeAttr();
				((BlockEdgeAttr) succ.attr).addEdge(e);
			}
		}
	}
	
	private void addProfileEdge( Edge edge ) {
		
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
		Stmt lastStmt = srcBlock.body.get( srcBlock.body.size() - 1 );
		
		if ( lastStmt instanceof BranchStmt && 
				((BranchStmt) lastStmt).getBranchBlock() == dstBlock ) {
			
			((BranchStmt) lastStmt).setBranchBlock( profBlock );
			
		} else {
			srcBlock.setProfBranchStmt( new BranchStmt( profBlock ) );
		}
		
		blocks.add( profBlock );
		edge.profBlock = profBlock;
	}
	
	private void removeProfileEdge( Edge edge ) {
		
		Block srcBlock = edge.src;
		Block dstBlock = edge.dst;
		
		Stmt lastStmt = srcBlock.body.get( srcBlock.body.size() - 1 );
		
		if ( lastStmt instanceof BranchStmt &&
			((BranchStmt) lastStmt).getBranchBlock() == edge.profBlock ) {
			((BranchStmt) lastStmt).setBranchBlock( dstBlock );
		} else {
			srcBlock.removeProfBranchStmt();
		}
		
		blocks.remove( edge.profBlock );
		edge.profBlock = null;
	}
	
	public void instrument() {
		for ( Edge e: localEdgeList ) {
			addProfileEdge( e );
		}
	}
	
	public void optimize() {
		
		for ( Edge e: localEdgeList ) {
			removeProfileEdge( e );
		}
		
		Set<Block> workingSet = new HashSet<Block>();
		Block block = routine.getEntryBlock();
		
		while (workingSet.size() < blocks.size()) {
			if (block != null) {
				if (block.getSuccs().size() == 1) {
					ProfEdgeList.remove( ((EdgeAttr) block.attr).getEdgeList().get(0) );
				}
			} else {
				
			}
		}
		
		
	}
}
