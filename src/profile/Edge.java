package profile;

import java.util.LinkedList;
import java.util.List;

import compiler.Block;

public class Edge {
	
	public static int GlobalIndex = 0; 
	public static List<Edge> ProfEdgeList = new LinkedList<Edge>();
	
	public final int index;
	public final Block src, dst;
	
	//public double weight;
	public int counter;
	public Block profBlock;
	
	public Edge(Block src, Block dst) {
		this.index = ++GlobalIndex;
		this.src = src;
		this.dst = dst;
	}
	
}
