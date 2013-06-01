package compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import stmt.EntryStmt;
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
	
	public List<Stmt> getUseList(String var) { return defUse.get(var); }
	
	public Stmt getDef(String var) { return varDef.get(var); }
	
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
	
	private void updateUseList(Stmt stmt) {
		for (Token t: stmt.getRHS()) {
			if (t instanceof Variable || t instanceof Register) {
				String name = t.toSSAString();
				List<Stmt> list = defUse.get(name);
				if (list != null) {
					list.remove(stmt);
					if (list.size() == 0)
						removeToken(t.toSSAString());
				}
			}
		}
	}
	
	private void removeToken(String var) {
		Stmt def = varDef.get(var);
		if (def instanceof EntryStmt) {
			Iterator<Token> it = def.getLHS().iterator();
			while (it.hasNext()) {
				Token t = it.next();
				if (t.toSSAString().equals(var)) {
					it.remove();
					break;
				}
			}
			if (def.getLHS().size() == 0)
				def.getBlock().removeStmt(def);
		} else {
			def.getBlock().removeStmt(def);
			updateUseList(def);
		}
	}
	
	public void eliminateUnused() {
		Set<String> keySet = defUse.keySet();
		for (String var: keySet) {
			List<Stmt> useList = defUse.get(var);
			if (useList.size() == 0) {
				removeToken(var);
			}
			//System.out.println("var " + key + " def: " + varDef.get(key).index);
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
