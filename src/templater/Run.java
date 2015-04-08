package templater;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class Run {
	
	public static StyleTree urlToStyleTree(String url){
		org.w3c.dom.Document doc = Run.openURL(url);
		org.w3c.dom.Element root = doc.getDocumentElement();
		StyleTree tree = new StyleTree(root);
		return tree;
	}
	
	public static StyleTree fileToStyleTree(String filename){
		org.w3c.dom.Document doc = Run.openFile(filename);
		org.w3c.dom.Element root = doc.getDocumentElement();
		StyleTree tree = new StyleTree(root);
		return tree;
	}
	
	/**
	 * 
	 * @param filename string of local path to file
	 * @return a {@link org.w3c.dom.Document} of a DOM 
	 */
	public static org.w3c.dom.Document openFile(String filename){
		File file = new File(filename);
		InputStream input = null;
		try{
			input = new FileInputStream(file);
		}
		catch(Exception e){
			e.printStackTrace();			
		}
		return Run.openInputStream(input);
	}
	public static org.w3c.dom.Document openURL(String urlString){
		URL url = null;
		try{
			url = new URL(urlString);
		}
		catch(MalformedURLException e){
			e.printStackTrace();
		}
		InputStream input = null;
		try{
			input = url.openStream();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return Run.openInputStream(input);
	}
	
	private static org.w3c.dom.Document openInputStream(InputStream input){
		InputSource source = null;
		String encoding = "UTF-8";
		org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser(
				new HTMLConfiguration());
		try{
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
			parser.setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags", true);
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
			parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
			source = new InputSource(input);
			parser.parse(source);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		source.setEncoding(encoding);
		org.w3c.dom.Document Doc = parser.getDocument();
		return Doc;
	}
	
	/**
	 *  by calling Run.printDocument(doc, System.out) where doc is a {@link org.w3c.dom.Document} of a DOM 
	 * @param doc
	 * @param out
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
}





