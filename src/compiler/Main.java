package compiler;

public class Main {
	
	public static void main(String[] args) {
		
		Option option = new Option();
		if (!option.parse(args)) {
			option.usage();
			return;
		}
		
		Program program = new Program();
		program.run(option);
		
	}
}
