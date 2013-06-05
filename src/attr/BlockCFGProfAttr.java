package attr;

import java.util.LinkedList;
import java.util.List;

import profile.Edge;

public class BlockCFGProfAttr extends Attribute {
	
	private List<Edge> edgeList;
	
	public BlockCFGProfAttr() {
		edgeList = new LinkedList<Edge>();
	}
	
	public void addEdge( Edge e ) { edgeList.add( e ); }
	
	public void removeEdge( Edge e ) { edgeList.remove( e ); }
	
	public List<Edge> getEdgeList() { return edgeList; }
}
