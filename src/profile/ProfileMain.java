package profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import attr.RoutinePosProfAttr;

import compiler.Option;
import compiler.Program;
import compiler.Routine;

public class ProfileMain {
	
	private Program program;
	
	public ProfileMain( Program program ) {
		this.program = program;
	}
	
	public void run( Option option ) {
		
//		System.out.print( program.dumpCFG() );
//		System.out.println("*************************************");
		
		for (Routine r: program.getRoutines()) {
			
			RoutinePosProfAttr attr = new RoutinePosProfAttr();
			attr.addProfile( new PositionProfile( r ) );
			attr.instrument();
			
			r.attr = attr;
		}
		
		program.renumberStmt();
		
		String fileName = option.fileName.substring( 0, option.fileName.lastIndexOf( '.' ) ); 
		String outFileName = fileName + "-prof.start";
		File f = new File( outFileName );
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( f ) );
			writer.write( program.dump() );
			writer.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		Runtime run = Runtime.getRuntime();
		String cmd = Option.START_LOC + " -r --stats " + outFileName;
		try {
			Process p = run.exec( cmd );
			BufferedReader reader = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String lineStr;
			boolean profStart = false;
			
			while ( (lineStr = reader.readLine()) != null ) {
				
				if ( lineStr.contains( "Counts" ) ) {
					profStart = true;
					continue;
				}
				
				if ( profStart ) {
					int id = Integer.parseInt( lineStr.substring( 0, lineStr.indexOf( ':' ) ).trim() );
					int cnt = Integer.parseInt( lineStr.substring( lineStr.indexOf( ':' ) + 1 ).trim() );
					PositionProfile.ProfEdgeList.get( id - 1 ).counter = cnt;
				}
				
			}
			
			if ( p.waitFor() != 0 ) {  
                if ( p.exitValue() == 1 )  
                    System.err.println( "Command error" );
            }
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		f.delete();
		
//		for ( Edge e: CFGProfile.ProfEdgeList )
//			System.out.println( e.toString() + ": " + e.counter );
		
		for ( Routine r: program.getRoutines() ) {
			((RoutinePosProfAttr) r.attr).clean();
		}
		
//		program.renumberStmt();
		
		for ( Routine r: program.getRoutines() ) {
			((RoutinePosProfAttr) r.attr).optimize();
		}
		
		program.renumberStmt();
		
//		System.out.println("*************************************");
//		System.out.print( program.dump() );
	}
}
