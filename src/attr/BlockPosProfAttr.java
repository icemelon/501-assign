package attr;

import java.util.LinkedList;
import java.util.List;

import compiler.Block;

import profile.Edge;

public class BlockPosProfAttr extends Attribute {
	
	private List<Edge> edgeList;
	
	public BlockPosProfAttr() {
		edgeList = new LinkedList<Edge>();
	}
	
	public void addEdge( Edge e ) { edgeList.add( e ); }
	
	public void removeEdge( Edge e ) { edgeList.remove( e ); }
	
	public Edge searchEdge( Block b ) {
		for ( Edge e: edgeList )
			if ( e.dst == b )
				return e;
		return null;
	}
	
	public List<Edge> getEdgeList() { return edgeList; }
}
