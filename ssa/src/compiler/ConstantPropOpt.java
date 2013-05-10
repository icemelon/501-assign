package compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import attr.ConstantAttr;

import token.Variable;

public class ConstantPropOpt {
	private class FlowEdge {
		public Block src;
		public Block dst;
		
		public FlowEdge(Block src, Block dst) {
			this.src = src;
			this.dst = dst;
		}
	}

	private Routine routine;
	private DefUseAnalysis du;
	
	private HashSet<FlowEdge> executeFlag = new HashSet<FlowEdge>();
	private List<FlowEdge> flowWorkList = new LinkedList<FlowEdge>();
	private List<Variable> ssaWorkList = new LinkedList<Variable>();
	private HashMap<String, ConstantAttr> varAttr = new HashMap<String, ConstantAttr>();
	
	public ConstantPropOpt(Routine r) {
		this.routine = r;
		this.du = new DefUseAnalysis(r);
		
	}
	
	public void optimize() {
		du.analyze();
		FlowEdge entryEdge = new FlowEdge(null, routine.getEntryBlock());
		flowWorkList.add(entryEdge);
		executeFlag.add(entryEdge);
		
//		Set<String> varName = du.getVarName();
//		for (String s: varName)
//			varAttr.put(s, new ConstantAttr)
		
		while (!flowWorkList.isEmpty() || !ssaWorkList.isEmpty()) {
			while (!flowWorkList.isEmpty()) {
				FlowEdge edge = flowWorkList.get(0);
				flowWorkList.remove(0);
				Block b = edge.dst;
			}
		}
	}
}
