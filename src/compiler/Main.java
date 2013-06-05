package compiler;

import profile.ProfileMain;

public class Main {
	
	public static void main( String[] args ) {
		
		Option option = new Option();
		if ( !option.parse( args ) ) {
			option.usage();
			return;
		}
		
		Program program = new Program();
		ProfileMain profile;
		
		if ( !program.scanFile( option.fileName ) )
			return;
		program.genCFG();
		
		boolean ssaTrans = false;
		
		if ( option.optimizeList.size() > 0 ) { 
			// need SSA
			program.transformToSSA();
			ssaTrans = true;
		} 
		
		if ( option.optimizeList.contains( Option.OptimizeOption.CP ) )
			program.constantPropOpt();
		if ( option.optimizeList.contains( Option.OptimizeOption.VN ) )
			program.valueNumberOpt();
		
		if ( option.profileList.size() > 0 ) {
			
			if ( ssaTrans ) {
				program.transformBackFromSSA();
				ssaTrans = false;
			}
			profile = new ProfileMain(program);
			profile.run ( option );
			
			return;
		}
		
		if ( option.backend == Option.BackendOption.SSA ) {
			if ( !ssaTrans ) {
				program.transformToSSA();
				ssaTrans = true;
			}
			System.out.print( program.dumpSSA() );
			
		} else if ( option.backend == Option.BackendOption.Report ) {
			program.printReport();
		} else {
			if ( ssaTrans )
				program.transformBackFromSSA();
			
			if ( option.backend == Option.BackendOption.ASM )
				System.out.print( program.dump() );
			else if ( option.backend == Option.BackendOption.IR )
				System.out.print( program.dumpIR() );
			else if ( option.backend == Option.BackendOption.CFG )
				System.out.print( program.dumpCFG() );
		}
		
	}
}
