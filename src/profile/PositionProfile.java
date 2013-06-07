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

import attr.BlockPosProfAttr;

import compiler.Block;
import compiler.Routine;

public class PositionProfile implements Profile {
	
	public static List<Edge> ProfEdgeList = new LinkedList<Edge>();
	
	private List<Edge> localEdgeList = new LinkedList<Edge>();
	private Routine routine;
	private List<Block> blocks;
	
	public PositionProfile( Routine routine ) {
		this.routine = routine;
		this.blocks = routine.getBlocks();
		
		genEdges();
	}
	
	private void genEdges() {
		for ( Block b: blocks ) {
			
			b.attr = new BlockPosProfAttr();
			
			for ( Block succ: b.getSuccs() ) {
				Edge e = new Edge( b, succ );
				localEdgeList.add( e );
				ProfEdgeList.add( e );
				
				((BlockPosProfAttr) b.attr).addEdge(e);
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
				BlockPosProfAttr attr = (BlockPosProfAttr) block.attr;
				
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
					BlockPosProfAttr attr = (BlockPosProfAttr) b.attr;
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
				((BlockPosProfAttr) hotEdge.src.attr).removeEdge( hotEdge );

			}
		}
		
		routine.setBlocks(workingList);
	}
	
	private void addChain( List<Block> blockList, Chain chain ) {
		blockList.addAll( chain.blockList );
		for ( Chain c: chain.outEdge ) {
			c.inEdge.remove( chain );
		}
	}
	
	private void bottomUpOptimize() {
		
		if ( localEdgeList.size() == 0 )
			return;
		
		Collections.sort( localEdgeList, new Comparator<Edge>() {

			@Override
			public int compare( Edge o1, Edge o2 ) {
				return ( o2.counter - o1.counter );
			}
			
		} );
		
//		for ( Edge e: localEdgeList )
//			System.out.println( e.toString() + ": " + e.counter ); 
		
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
		
		for ( int i = 0; i < chainList.size(); ++ i )
			chainList.get( i ).index = i;
		
//		for ( Chain c: chainList )
//			System.out.println( c );
		
		// calculate order between chains
		int n = chainList.size();
		
		if (n > 2) {
			int[][] chainOrder = new int[n][n];
			
			for ( int i = 0; i < n; i ++ )
				for ( int j = 0; j < n; j ++ )
					chainOrder[i][j] = 0;
			
			for ( Chain chain: chainList )
				for ( Block block: chain.blockList )
					if ( block.getSuccs().size() > 1 ) {
						
						Block b1 = block.getSuccs().get(0);
						Block b2 = block.getSuccs().get(1);
						
						Chain c1 = blockChainMap.get( b1 );
						Chain c2 = blockChainMap.get( b2 );
						
						BlockPosProfAttr attr = (BlockPosProfAttr) block.attr;
						Edge e1 = attr.searchEdge( b1 );
						Edge e2 = attr.searchEdge( b2 );
						
						if ( c1 != chain ) {
							chainOrder[chain.index][c1.index] += e2.counter; // weight
						} else if ( c2 != chain ) {
							chainOrder[chain.index][c2.index] += e1.counter;
						} 
					}
			
			for ( Chain c1: chainList )
				for ( Chain c2: chainList )
					if ( c1 != c2 ) {
						int id1 = c1.index;
						int id2 = c2.index;
						if ( chainOrder[id1][id2] > chainOrder[id2][id1] ) {
							c1.outEdge.add( c2 );
							c2.inEdge.add( c1 );
						} else if ( chainOrder[id1][id2] < chainOrder[id2][id1] ) {
							c1.inEdge.add( c2 );
							c2.outEdge.add( c1 );
						}
					}
		}
		
		List<Block> newBlockOrder = new LinkedList<Block>();
		Chain entryChain = blockChainMap.get( routine.getEntryBlock() );
		addChain( newBlockOrder, entryChain );
		chainList.remove( entryChain );
		
		while ( !chainList.isEmpty() ) {
			
			Iterator<Chain> it = chainList.iterator();
			Chain chain = null;
			
			while ( it.hasNext() ) {
				chain = it.next();
				if ( chain.inEdge.isEmpty() )
					break;
			}
			addChain( newBlockOrder, chain );
			it.remove();
			
		}
		
//		for ( Block b: newBlockOrder )
//			System.out.print( b.getIndex() + "->" );
//		System.out.println();
		
		int blockCount = newBlockOrder.size();
		for ( int i = 0; i < blockCount; i ++ ) {
			Block block = newBlockOrder.get( i );
			Block nextBlock = ( i < blockCount - 1 ) ? newBlockOrder.get( i + 1 ) : null;
			Stmt lastStmt = block.body.get( block.body.size() - 1 );
			
			if ( block.getSuccs().size() == 1 ) {
				
				Block succ = block.getSuccs().get(0);
				
				if ( lastStmt instanceof BranchStmt ) {
					if ( succ == nextBlock) {
						block.removeStmt( lastStmt );
					}
				} else {
					if ( nextBlock == null || succ != nextBlock ) {
						BranchStmt brStmt = new BranchStmt( succ );
						block.body.add( brStmt );
					}
				}
				
			} else if ( block.getSuccs().size() == 2 ) {
				
				BranchStmt brStmt = (BranchStmt) lastStmt;
				Block elseBlock = brStmt.getBranchBlock(); 
				if ( elseBlock == nextBlock ) {
					brStmt.flipOperator();
					Block thenBlock;
					if ( elseBlock == block.getSuccs().get(0) )
						thenBlock = block.getSuccs().get(1);
					else
						thenBlock = block.getSuccs().get(0);
					brStmt.setBranchBlock(thenBlock);
				}
				
			}
		}
		
		routine.setBlocks( newBlockOrder );
	}
	
	public void clean() {
		for ( Edge e: localEdgeList ) {
			removeProfileEdge( e );
		}
	}
	
	public void optimize() {
		topDownOptimize();
//		bottomUpOptimize();
	}
}
