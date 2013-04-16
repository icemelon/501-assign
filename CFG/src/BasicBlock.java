
public class BasicBlock {
	public static int GlobalIndex = 0;
	
	private final int index;
	private int startLine;
	private int endLine;
	
	public BasicBlock() {
		index = GlobalIndex++;
		
	}
}
