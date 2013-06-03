package profile;

import compiler.Block;

public class Edge {
	
	public static int GlobalIndex = 0; 
	
	public final int index;
	public final Block src, dst;
	public double weight;
	
	private boolean profile;
	private Block profBlock;
	
	public Edge(Block src, Block dst) {
		this.index = ++GlobalIndex;
		this.src = src;
		this.dst = dst;
		this.profile = false;
	}
	
	public void setProfileBlock(Block b) {
		profBlock = b;
	}
}
