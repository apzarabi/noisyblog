package templater;
import java.io.IOException;
import java.util.ArrayList;

public class Q2 {
	public static void main(String[] args) throws IOException {
		ArrayList<String> s = new ArrayList<String>(10);
		s.add("salam");
		for(int i =0; i<s.size(); i++)
			System.out.println(s.get(i));
		System.out.println(s.size());
	}
}
