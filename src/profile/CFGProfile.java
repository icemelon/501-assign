package profile;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;                                         
import java.util.Map;
import java.util.Set;

import stmt.BranchStmt;
import stmt.CountStmt;
import stmt.Stmt;
import token.Constant;

import attr.BlockCFGProfAttr;

import compiler.Block;
import compiler.Routine;

public class CFGProfile implements Profile {
	
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
			
			b.attr = new BlockCFGProfAttr();
			
			for ( Block succ: b.getSuccs() ) {
				Edge e = new Edge( b, succ );
				localEdgeList.add( e );
				ProfEdgeList.add( e );
				
				((BlockCFGProfAttr) b.attr).addEdge(e);
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
	
	private void topDownOptimize() {
		
		List<Block> workingList = new LinkedList<Block>();
		Block block = routine.getEntryBlock();
		
		while ( workingList.size() < blocks.size() ) {
			
			if ( block != null ) {
				workingList.add( block );
				
//				System.out.println( "visit block#" + block.getIndex() );
				BlockCFGProfAttr attr = (BlockCFGProfAttr) block.attr;
				
				if ( attr.getEdgeList().size() == 0 ) {
					
					block = null;
					
				} else if ( attr.getEdgeList().size() == 1 ) {
					
					Edge edge = attr.getEdgeList().get(0);
					Block nextBlock = edge.dst;
					
					if ( workingList.contains( nextBlock ) ) {
						if (!( block.body.get( block.body.size() - 1 ) instanceof BranchStmt )) {
							BranchStmt brStmt = new BranchStmt( nextBlock );
							block.body.add( brStmt );
						}
						block = null;
					} else {
						block = nextBlock;
					}
					
					attr.removeEdge( edge );
					
				} else if ( attr.getEdgeList().size() == 2 ) {
					BranchStmt brStmt = (BranchStmt) block.body.get( block.body.size() - 1 );
					Block brBlock = brStmt.getBranchBlock();
					
					List<Edge> edgeList = attr.getEdgeList();
					Edge hotEdge, coldEdge;
					
					if ( edgeList.get(0).counter >= edgeList.get(1).counter ) {
						hotEdge = edgeList.get(0);
						coldEdge = edgeList.get(1);
					} else {
						hotEdge = edgeList.get(1);
						coldEdge = edgeList.get(0);
					}
					
					if ( hotEdge.dst == brBlock ) {
						if ( brStmt.getOperator() == Stmt.Operator.blbc )
							brStmt.setOperator( Stmt.Operator.blbs );
						else
							brStmt.setOperator( Stmt.Operator.blbc );
						
						brStmt.setBranchBlock( coldEdge.dst );
					}
					
					block = hotEdge.dst;
					attr.removeEdge( hotEdge );
					
				} else {
					System.out.println( "wtf!!! block#" + block.getIndex() );
				}
			} else {
				
				Edge hotEdge = null;
				for ( Block b: workingList ) {
					BlockCFGProfAttr attr = (BlockCFGProfAttr) b.attr;
					List<Edge> list = attr.getEdgeList();
					Iterator<Edge> it = list.iterator();
					while ( it.hasNext() ) {
						Edge e = it.next();
						if ( workingList.contains( e.dst ) ) {
							it.remove();
						} else if ( hotEdge == null || e.counter > hotEdge.counter ) {
							hotEdge = e;
						}
					}
				}
				
				block = hotEdge.dst;
				((BlockCFGProfAttr) hotEdge.src.attr).removeEdge( hotEdge );

			}
		}
		
		routine.setBlocks(workingList);
	}
	
	private void bottomUpOptimize() {
		Collections.sort( localEdgeList, new Comparator<Edge>() {

			@Override
			public int compare( Edge o1, Edge o2 ) {
				return ( o2.counter - o1.counter );
			}
			
		} );
		
		for ( Edge e: localEdgeList ) {
			System.out.println( e.toString() + ": " + e.counter ); 
		}
		
		List<Chain> chainList = new LinkedList<Chain>();
		Map<Block, Chain> blockChainMap = new HashMap<Block, Chain>();
		
		for ( Edge e: localEdgeList ) {
			
			if ( e.isBackEdge )
				continue;
			
			boolean srcVisited = blockChainMap.containsKey( e.src );
			boolean dstVisited = blockChainMap.containsKey( e.dst );
			
			if ( srcVisited && dstVisited ) {

				Chain srcChain = blockChainMap.get( e.src );
				Chain dstChain = blockChainMap.get( e.dst );
				
				if ( srcChain != dstChain && 
					 srcChain.isLast( e.src ) &&
					 dstChain.isFirst( e.dst ) ) {
					srcChain.merge( dstChain );
					for ( Block b: dstChain.blockList )
						blockChainMap.put( b, srcChain );
					chainList.remove( dstChain );
				}

			} else if ( srcVisited ) {
				
				Chain c = blockChainMap.get( e.src );
				if ( c.isLast( e.src ) ) {
					c.blockList.add( e.dst );
					blockChainMap.put( e.dst, c );
				} else {
					Chain chain = new Chain();
					chain.blockList.add( e.dst );
					chainList.add( chain );
					blockChainMap.put( e.dst, chain );
				}
				
			} else if ( dstVisited ) {
				
				Chain c = blockChainMap.get( e.dst );
				if ( c.isFirst( e.dst ) ) {
					c.blockList.add( 0, e.src );
					blockChainMap.put( e.src, c );
				} else {
					Chain chain = new Chain();
					chain.blockList.add( e.src );
					chainList.add( chain );
					blockChainMap.put( e.src, chain );
				}
				
			} else {
				
				Chain chain = new Chain();
				chain.blockList.add( e.src );
				chain.blockList.add( e.dst );
				
				chainList.add( chain );
				blockChainMap.put( e.src, chain );
				blockChainMap.put( e.dst, chain );
				
			}
		}
		
		for ( Chain c: chainList )
			System.out.println( c );
		
	}
	
	public void clean() {
		for ( Edge e: localEdgeList ) {
			removeProfileEdge( e );
		}
	}
	
	public void optimize() {
		//topDownOptimize();
		bottomUpOptimize();
	}
}
