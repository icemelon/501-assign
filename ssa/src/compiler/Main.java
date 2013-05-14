package compiler;

public class Main {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("SSA.jar [Input file]"	);
			return;
		}
		Program p = new Program();
		p.scanFile(args[0]);

		long startTime, endTime;
		
		startTime = System.nanoTime();
		p.genCFG();
		endTime = System.nanoTime();
		//System.out.println(((endTime - startTime) / 1000.0));

		startTime = System.nanoTime();
		p.genDominator();
		endTime = System.nanoTime();
		
		p.tranformToSSA();
//		p.dumpSSA();
		
		//System.out.println(((endTime - startTime) / 1000.0));
		//System.out.println(BasicBlock.globalIndex);

		//p.dumpIR();

		p.constantPropOpt();
		p.valueNumberOpt();
//		p.dumpSSA();
		
		p.transformBackFromSSA();
//		p.dumpIR();
		p.dump();
		
		/*Routine r = p.getRoutines().get(0);
		r.dumpSSA();
		System.out.println("\n*********************************************");
		r.transformBackFromSSA();
		r.dumpIR();*/
		
//		DefUseAnalysis du = new DefUseAnalysis(p.getRoutines().get(0));
//		du.analyze();
//		du.dump();
		
//		ConstantPropOpt cpo = new ConstantPropOpt(p.getRoutines().get(2));
//		cpo.optimize();
//		cpo.dump();
		
//		ValueNumberOpt vno = new ValueNumberOpt(p.getRoutines().get(1));
//		vno.dump();
//		vno.optimize();
//		vno.dump();
	}
}
