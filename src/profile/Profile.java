package profile;

public interface Profile {

	// instrument profiling instructions
	public void instrument();
	
	// remove profiling instructions
	public void clean();
	
	// optimize based on profiling result
	public void optimize();
}
