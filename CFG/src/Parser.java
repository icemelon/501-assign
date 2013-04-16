import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	private List<String> stmts;
	
	public Parser() {
		stmts = new LinkedList<String>();
	}
	
	public void scanFile(String filename) {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = reader.readLine()) != null) {
				stmts.add(line);
				System.out.println(line);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(filename + "doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("CFG.jar [Input file]"	);
			//for (String arg: args)
			//	System.out.println(arg);
			return;
		}
		Parser p = new Parser();
		p.scanFile(args[0]);
	}
}
