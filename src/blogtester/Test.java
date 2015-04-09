package blogtester;

import java.util.ArrayList;

public class Test {
	static String url = "blog.ir/06247.blog.ir";
	static ArrayList<Integer> p = new ArrayList<Integer>();
	static {
		for(int i = 287; i<297; i++)
			p.add(i);
	}
	
	public static void main(String[] args) {
		Weblog weblog = new Weblog();
		weblog.url = Test.url;
		weblog.post_number = Test.p;
		weblog.fillContent();
		System.out.println(weblog.content);
		weblog.writeContentToFile();
	}
}
