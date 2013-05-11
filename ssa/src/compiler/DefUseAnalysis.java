package compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import stmt.MoveStmt;
import stmt.PhiNode;
import stmt.Stmt;
import token.Register;
import token.Token;
import token.Variable;

public class DefUseAnalysis {

	private Routine routine;
	private HashMap<String, Stmt> varDef = new HashMap<String, Stmt>();;
	private HashMap<String, List<Stmt>> defUse = new HashMap<String, List<Stmt>>();;
	
	public DefUseAnalysis(Routine r) {
		this.routine = r;
	}
	
	public Set<String> getAllVarName() { return varDef.keySet(); }
	
	public List<Stmt> getDefUseList(String var) { return defUse.get(var); }
	
	public void analyze() {
		
		List<Block> blocks = routine.getBlocks();
		for (Block b: blocks) {
			for (PhiNode phiNode: b.getPhiNode()) {
				String name = phiNode.getLHS().get(0).toSSAString();
				varDef.put(name, phiNode);
				defUse.put(name, new LinkedList<Stmt>());
			}
			for (Stmt s: b.body)
				for (Token t: s.getLHS())
					if (t instanceof Variable || t instanceof Register) {
						String name = t.toSSAString();
						varDef.put(name, s);
						defUse.put(name, new LinkedList<Stmt>());
					}
		}
		
		for (Block b: blocks) {
			for (PhiNode phiNode: b.getPhiNode()) {
				for (Token t: phiNode.getRHS()) {
					String name = t.toSSAString();
					List<Stmt> list = defUse.get(name);
					if (list != null)
						list.add(phiNode);
				}
			}

			for (Stmt s: b.body)
				for (Token t: s.getRHS())
					if (t instanceof Variable || t instanceof Register) {
						String name = t.toSSAString();
						List<Stmt> list = defUse.get(name);
						if (list != null)
							list.add(s);
					}
		}
	}
	
	public void dump() {
		routine.dumpSSA();
		
		System.out.println();
		
		Set<String> keySet = varDef.keySet();
		for (String key: keySet) {
			System.out.println("var " + key + " def: " + varDef.get(key).index);
		}
		
		keySet = varDef.keySet();
		for (String key: keySet) {
			System.out.print("var " + key + " use:");
			for (Stmt s: defUse.get(key))
				System.out.print(" " + s.index);
			System.out.println();
		}
	}
}
