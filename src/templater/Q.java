package templater;

import java.io.InputStream;
import java.net.URL;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class Q {

	public static void main(String[] args) throws Exception {
		try (InputStream input = new URL("http://www.apple.com").openStream()) {
			String encoding = "UTF-8";
			org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser(
					new HTMLConfiguration());
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
			parser.setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags", true);
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
			parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");

			InputSource source = new InputSource(input);
			parser.parse(source);
			source.setEncoding(encoding);
			Element root = parser.getDocument().getDocumentElement();
			System.out.println(root);
			NodeList nodeList = root.getChildNodes();
			findText(root);
		}
	}
	public static void findText(Node node){
		if(node.getNodeValue() != null){
			System.out.println(node.getNodeName() + " <---" + node.getParentNode().getNodeName() + " ::: " + node.getNodeValue());
			System.out.println(node.getParentNode().getAttributes());
			for(int i = 0;i<node.getParentNode().getAttributes().getLength(); i++)
				System.out.println(node.getParentNode().getAttributes().item(i));
			System.out.println("_________________");
		}
		NodeList nodeList = node.getChildNodes();
		for(int i = 0; i<nodeList.getLength(); i++)
			findText(nodeList.item(i));
			
	}

}
