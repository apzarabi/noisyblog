package blogtester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import templater.*;

public class Weblog {
	String content;
	String url;
	ArrayList<Integer> post_number = new ArrayList<Integer>();
	StyleTree site; // based on post_number[0..n-2]
	
	/**
	 * after instantiating a new Weblog and initalizing weblog.url and
	 * 
	 */
	public void fillContent(){
		buildSiteStyleTree();
		// mapping post_number[n-1] to sitestyletree
		StyleTree page = templater.Run.fileToStyleTree(url + "_" + post_number.get(post_number.size()-1) + ".orig");
		site.MapSST(page);
		this.content = page.getContent();
	}
	
	void buildSiteStyleTree(){
		site = templater.Run.fileToStyleTree(url + "_" + post_number.get(0) + ".orig");
		StyleTree temp = null;
		// TODO if you want to use the target for building style tree as well
		// change this for to i < post_number.size()
		for(int i = 1; i<post_number.size()-1; i++){	
			temp = Run.fileToStyleTree(url + "_" + post_number.get(i) + ".orig");
			site.update(temp);
		}
		site.mark(site.getRoot());
	}
	
	/** 
	 * this method writes String content to file <url>_<post_number[n-1]>.out
	 */
	void writeContentToFile(){
		File file = new File(url + "_" + post_number.get(post_number.size()-1) + ".out");
		try{
			System.err.println(file.getCanonicalPath());
			if( !file.exists() )
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}


















