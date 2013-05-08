package compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Tools {
	
	private static List<Block> postorder;
	private static HashMap<Block, Boolean> visit;
	
	private static void dfs(Block b) {
		visit.put(b, true);
		
		for (Block child: b.getSuccs())
			if (!visit.get(child))
				dfs(child);
		
		postorder.add(b);
	}
	
	// based on CFG
	public static List<Block> genPostOrder(List<Block> blocks) {
		visit = new HashMap<Block, Boolean>();
		postorder = new LinkedList<Block>();
		
		for (Block b: blocks)
			visit.put(b, false);
		
		dfs(blocks.get(0));
		
		return postorder;
	}
	
	// based on dominator tree
	public static List<Block> genTopOrder(List<Block> blocks) {
		List<Block> workList = new LinkedList<Block>(blocks);
		HashMap<Block, Integer> degree = new HashMap<Block, Integer>();
		List<Block> topOrder = new LinkedList<Block>();
		
		for (Block b: blocks)
			degree.put(b, new Integer(b.getChildren().size()));
		
		while (!workList.isEmpty()) {
			for (int i = 0; i < workList.size(); i++) {
				Block b = workList.get(i);
				Integer out = degree.get(b);
				if (out == 0) {
					workList.remove(i);
					topOrder.add(b);
					
					Block parent = b.getIdom();
					if (parent != null)
						degree.put(parent, degree.get(parent) - 1); 
				}
			}
		}
		
		return topOrder;
	}
}
