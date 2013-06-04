package profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import attr.RoutineProfileAttr;

import compiler.Option;
import compiler.Program;
import compiler.Routine;

public class Profile {
	
	private Program program;
	
	public Profile( Program program ) {
		this.program = program;
	}
	
	public void run( Option option ) {
		
		for (Routine r: program.getRoutines()) {
			
			RoutineProfileAttr attr = new RoutineProfileAttr();
			attr.cfgProfile = new CFGProfile( r );
			attr.cfgProfile.instrument();
			
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
				
//				System.out.println( lineStr );
				
				if ( lineStr.contains( "Counts" ) ) {
					profStart = true;
					continue;
				}
				
				if ( profStart ) {
					int id = Integer.parseInt( lineStr.substring( 0, lineStr.indexOf( ':' ) ).trim() );
					int cnt = Integer.parseInt( lineStr.substring( lineStr.indexOf( ':' ) + 1 ).trim() );
					CFGProfile.ProfEdgeList.get( id - 1 ).counter = cnt;
				}
				
			}
//				System.out.println(lineStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		f.delete();
		
		for ( Edge e: CFGProfile.ProfEdgeList ) {
			System.out.println( "edge#" + e.index + ": " + e.counter );
		}
		
		for ( Routine r: program.getRoutines() ) {
			((RoutineProfileAttr) r.attr).cfgProfile.optimize();
		}
		
		System.out.print( program.dumpCFG() );
	}
}
