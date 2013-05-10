import java.util.List;
import java.util.LinkedList;

public class Test {
	public static void main(String[] args) {
		List<Integer> l = new LinkedList<Integer>();
		l.add(5, 1);
		for (Integer i: l)
			System.out.println(i);
	}
}
