import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Test {
	//a path to the directory, containing a file "sources"
	//the first line should show the target page, 
	//and all other line should be url to post for building StyleTree
	static String sourceFolderPath = "tests/tarlantab.blogfa.com";
	static ArrayList<String> sources = new ArrayList<String>();
	static String target;

			
	public static void main(String[] args) throws Exception{
		
		setSources(sourceFolderPath + "/sources");
		
		//building SiteStyleTree
		StyleTree tree = urlToStyleTree(sources.get(0));
		buildStyleTree(tree);		
		tree.mark(tree.getRoot());
		tree.print();
		
		//creating PageStyleTree and mapping
		StyleTree pst =urlToStyleTree(target);
		tree.MapSST(pst);
//		pst.print();
		System.out.println("######\n\n####");
		System.out.println(pst.toHTML());	
		writeToFile(pst.toHTML(), sourceFolderPath+"/output.html");
	}
	
	public static void writeToFile(String content, String path){
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(path, "UTF-8");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		writer.println(content);
		writer.close();
	}
	
	public static void setSources(String path) throws IOException{
		FileReader fr = null;
		try{
			fr = new FileReader(new File(path));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		BufferedReader textReader = new BufferedReader(fr);
		
		// first line of the file should be the url to target page
		String newLine = textReader.readLine();
		target = newLine;
		
		//all other lines should be a url of a page, for building SiteStyleTree
		while( (newLine = textReader.readLine()) != null){
			if( newLine.length() > 0) {
				sources.add(newLine);
			}
		}
		return;
	}
	
	// the first source has been used to construct the tree itself.
	public static void buildStyleTree(StyleTree tree){
		for(int i = 0; i<sources.size(); i++){
			String input = sources.get(i);
			StyleTree page = urlToStyleTree(input);
			tree.update(page);
		}
	}
	
	public static StyleTree urlToStyleTree(String url){
		org.w3c.dom.Document doc = Run.openURL(url);
		org.w3c.dom.Element root = doc.getDocumentElement();
		StyleTree tree = new StyleTree(root);
		return tree;
	}
	
	public static void printAllAttrs(Element e){
		System.out.println(String.format("%s's attrs: %s ", e.getTagName(), e.getAllAttributes()));
		for(StyleNode sn: e.getChildren())
			for(Element element: sn.getElements())
				printAllAttrs(element);
		
	}
	
}












