package attr;

import java.util.LinkedList;
import java.util.List;

import profile.Profile;

public class RoutineCFGProfAttr extends Attribute {
	
	private List<Profile> profileList = new LinkedList<Profile>();
	
	public void addProfile(Profile profile) {
		profileList.add(profile);
	}
	
	public void instrument() {
		for (Profile p: profileList)
			p.instrument();
	}
	
	public void clean() {
		for (Profile p: profileList)
			p.clean();
	}
	
	public void optimize() {
		for (Profile p: profileList)
			p.optimize();
	}
	
}
