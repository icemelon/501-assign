package compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import stmt.MoveStmt;
import stmt.PhiNode;
import stmt.Stmt;
import token.Token;
import token.Variable;

public class DefUseAnalysis {

	private Routine routine;
	private HashMap<String, Stmt> varDef = new HashMap<String, Stmt>();;
	private HashMap<String, List<Stmt>> defUse = new HashMap<String, List<Stmt>>();;
	
	public DefUseAnalysis(Routine r) {
		this.routine = r;
	}
	
	public void genDefUse() {
		routine.dumpSSA();
		//HashMap<String, Stmt> varDef = new HashMap<String, Stmt>();
		List<Block> blocks = routine.getBlocks();
		for (Block b: blocks) {
			for (PhiNode phiNode: b.getPhiNode()) {
				String name = ((Variable) phiNode.getLHS()).getSSAName();
				varDef.put(name, phiNode);
				defUse.put(name, new LinkedList<Stmt>());
			}
			for (Stmt s: b.body)
				if (s instanceof MoveStmt) {
					String name = ((Variable) s.getLHS()).getSSAName();
					varDef.put(name, s);
					defUse.put(name, new LinkedList<Stmt>());
				}
		}
		{
			Set<String> keySet = varDef.keySet();
			for (String key: keySet) {
				System.out.println("var " + key + "def: " + varDef.get(key).index);
			}
		}
		
		for (Block b: blocks) {
			for (PhiNode phiNode: b.getPhiNode()) {
				for (Token t: phiNode.getRHS()) {
					String name = ((Variable) t).getSSAName();
					List<Stmt> list = defUse.get(name);
					if (list != null)
						list.add(phiNode);
				}
			}

			for (Stmt s: b.body)
				for (Token t: s.getRHS())
					if (t instanceof Variable) {
						String name = ((Variable) t).getSSAName();
						List<Stmt> list = defUse.get(name);
						if (list != null)
							list.add(s);
					}
		}
		
		{
			Set<String> keySet = varDef.keySet();
			for (String key: keySet) {
				System.out.print("var " + key + "use:");
				for (Stmt s: defUse.get(key))
					System.out.print(" " + s.index);
				System.out.println();
			}
		}
			
	}
}
