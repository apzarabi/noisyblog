package blogtester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import templater.StyleTree;

// about /blog.ir/:
// this folder contains 94 weblogs. 12 of them with just 2 posts,
// 17 with 3 posts, 36 with 4 post, 11 with 5 posts
// exactly 1 with 6, 1 with 7, 2 with 8, 2 with 9
// and 12 with 10 posts.

public class Main {
	static ArrayList<Weblog> weblogs;
	static File dir = new File("blog.ir/");
	
	public static void main(String[] args) {
		fillWeblogs();	
		for(Weblog weblog: weblogs){
			weblog.fillContent();
			weblog.writeContentToFile();
		}
	}

	
	/**
	 * at start, this method should be called. it looks at the /blog.ir/ 
	 * and finds all weblogs in it.
	 */
	public static void fillWeblogs(){
		String[] list = dir.list();
		weblogs = new ArrayList<Weblog>(list.length/3);
		
		//filling weblogs from 'blog.ir/'.list() 
		Weblog currentWeblog;
		ArrayList<Integer> currentPN;
		for(String s: list){
			String[] tokens = s.split("_|\\.\\w+$");
			if(s.charAt(0) == '.')
				continue;
			if(weblogs.size() == 0 || (!weblogs.get(weblogs.size()-1).url.equals(tokens[0]))){
				currentWeblog = new Weblog();
				currentWeblog.url = dir + tokens[0];
				currentWeblog.post_number.add(Integer.parseInt(tokens[1]));
				weblogs.add(currentWeblog);
			}
			else{
				currentWeblog = weblogs.get(weblogs.size()-1);
				currentPN = currentWeblog.post_number;
				if(currentPN.get(currentPN.size()-1) != Integer.parseInt(tokens[1]))
					currentPN.add(Integer.parseInt(tokens[1]));
			}
		}
	}
}
