import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	private List<Stmt> stmts;
	private List<Routine> routines;
	
	public Parser() {
		stmts = new LinkedList<Stmt>();
		routines = new LinkedList<Routine>();
	}
	
	public void scanFile(String filename) {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("method")) {
					routines.add(Routine.parseRoutine(line));
				} else if (line.startsWith("instr")) {
					stmts.add(new Stmt(line));
				}
				//System.out.println(line);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		{
			int beginLine = routines.get(0).getStartLine();
			int endLine;
			Routine routine;
			for (int i = 0; i < routines.size() - 1; i++) {
				endLine = routines.get(i + 1).getStartLine() - 1;
				routine = routines.get(i);
				routine.setEndLine(endLine);
				routine.setStmts(stmts.subList(beginLine - 1, endLine));
				beginLine = endLine + 1;
			}
			endLine = stmts.size() - 1; // last instr: nop, skip this one
			routine = routines.get(routines.size() - 1);
			routine.setEndLine(endLine);
			routine.setStmts(stmts.subList(beginLine - 1, endLine));
		}
	}
	
	public void parseRoutines() {
		for (Routine r: routines) {
			r.parse();
		}
	}
	
	public List<Routine> getRoutines() { return routines; }
	
	public void genDom() {
		for (Routine r: routines) {
			r.genDom();
		}
	}
	
	public void dump() {
		for (Routine r: routines) {
			r.dump();
			System.out.println("\n*********************************************");
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("CFG.jar [Input file]"	);
			return;
		}
		Parser p = new Parser();
		p.scanFile(args[0]);

		long startTime, endTime;
		
		startTime = System.nanoTime();
		p.parseRoutines();
		endTime = System.nanoTime();
		//System.out.println(((endTime - startTime) / 1000.0));

		startTime = System.nanoTime();
		p.genDom();
		endTime = System.nanoTime();

		//System.out.println(((endTime - startTime) / 1000.0));
		//System.out.println(BasicBlock.globalIndex);

		//int max = 0;
		//for (Routine r: p.getRoutines()) {
		//	int b = r.getBlockCount();
		//	if (b > max) max = b;
		//}
		//System.out.println(max);
		
		p.dump();
		
	}
}
