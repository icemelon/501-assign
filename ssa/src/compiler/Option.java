package compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Option {
	
	public enum BackendOption {
		ASM,
		CFG,
		IR,
		SSA,
		Report,
	};
	
	public enum OptimizeOption {
		CP, // 
		VN,
		SSA,
	};
	
	public List<String> options;
	public String fileName;
	public List<OptimizeOption> optimize;
	public BackendOption backend; 
	
	public void usage() {
		System.out.println("java -jar opt.jar <filename> [-opt=<optimize>] [-backend=<backend>]\n");
		System.out.println("Optimiziation supported options:");
		System.out.println("ssa\tSSA optimization");
		System.out.println("cp\tConstant propagation optimization (depends on SSA)");
		System.out.println("vn\tValue numbering optimization (depends on SSA)");
		System.out.println("\nBackend supported options:");
		System.out.println("asm\tAssembly code (default");
		System.out.println("cfg\tControl flow graph");
		System.out.println("ir\tIntermediate representation");
		System.out.println("ssa\tSSA code");
		System.out.println("report\tReport");
	}
	
	public boolean parse(String[] args) {

		options = new LinkedList<String>();
		optimize = new LinkedList<OptimizeOption>();
		backend = BackendOption.IR;
		
		for (int i = 0; i < args.length; i++)
			options.add(args[i]);
		
		Iterator<String> it = options.iterator();
		if (it.hasNext()) {
			fileName = it.next();
//			System.out.println(fileName);
		} else
			return false;
		
		while (it.hasNext()) {
			String arg = it.next();
			
			while (arg.startsWith("-"))
				arg = arg.substring(1);
			
			if (arg.startsWith("opt")) {
				
				arg = arg.substring(arg.indexOf('=') + 1).toLowerCase();
				String[] term = arg.split(",");
				for (String s: term) {
					if (s.equals("cp"))
						optimize.add(OptimizeOption.CP);
					else if (s.equals("vn"))
						optimize.add(OptimizeOption.VN);
					else if (s.equals("ssa"))
						optimize.add(OptimizeOption.SSA);
					else {
						System.out.println("Unsupported optimization option: " + s + "\n");
						return false;
					}
				}
				
			} else if (arg.startsWith("backend")) {
				arg = arg.substring(arg.indexOf('=') + 1).toLowerCase();
				if (arg.equals("asm"))
					backend = BackendOption.ASM;
				else if (arg.equals("ir"))
					backend = BackendOption.IR;
				else if (arg.equals("cfg"))
					backend = BackendOption.CFG;
				else if (arg.equals("ssa"))
					backend = BackendOption.SSA;
				else if (arg.equals("report"))
					backend = BackendOption.Report;
				else {
					System.out.println("Unsupported backend option: " + arg + "\n");
					return false;
				}
			}
		}
		
		return true;
	}
}