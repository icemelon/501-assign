package compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import token.Variable;

public class ConstantPropOpt {
	class FlowEdge {
		public Block src;
		public Block dst;
	}

	private Routine routine;
	
	private HashMap<FlowEdge, Boolean> executFlag = new HashMap<FlowEdge, Boolean>();
	private List<FlowEdge> flowWorkList = new LinkedList<FlowEdge>();
	private List<Variable> ssaWorkList = new LinkedList<Variable>();
	
	public ConstantPropOpt(Routine r) {
		this.routine = r;
	}
}
