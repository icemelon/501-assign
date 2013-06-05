package profile;

import java.util.LinkedList;
import java.util.List;

import compiler.Block;

public class Chain {
	public static int GlobalIndex = 0;
	
	public final int index;
	public List<Block> blockList = new LinkedList<Block>();
	
	public Chain() {
		index = GlobalIndex ++;
	}
	
	public void merge( Chain chain ) {
		blockList.addAll( chain.blockList );
	}
	
	public boolean isFirst( Block b ) {
		return ( b == blockList.get(0) );
	}
	
	public boolean isLast( Block b ) {
		return ( b == blockList.get( blockList.size() - 1 ) );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( Block b: blockList )
			sb.append( b.getIndex() + "->" );
		
		int length = sb.length();
		sb.delete( length - 2, length );
		
		return sb.toString();
	}
}
