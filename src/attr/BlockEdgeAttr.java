package attr;

import java.util.LinkedList;
import java.util.List;

import profile.Edge;

public class BlockEdgeAttr extends Attribute {
	
	private List<Edge> edgeList;
	
	public BlockEdgeAttr() {
		edgeList = new LinkedList<Edge>();
	}
	
	public void addEdge(Edge e) { edgeList.add(e); }
	
	public List<Edge> getEdgeList() { return edgeList; }
}
