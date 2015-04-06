import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class StyleTree {
	private static int numberOfStyleTrees = 0;
	private int StyleTreeNumber;
	private static int tabNumber = 0;
	private Element root;
	private double threshold = 0.6;


	/**
	 * this constructor is used for cloning
	 * @param root
	 */
	public StyleTree(){
		setStyleTreeNumber(StyleTree.numberOfStyleTrees++);
		return;
	}
	/**
	 * construct a StyleTree from a DOM. 
	 * @param root should be the root of DOM: parser.getDocument().getDocumentElement()
	 */
	public StyleTree(org.w3c.dom.Element domRoot) {
		setStyleTreeNumber(StyleTree.numberOfStyleTrees++);
		this.root = new Element(domRoot,0); 
		this.root.setWrapper(null);
	}


	//TODO
	public void mark(Element v) {		//Arash bayad sedash kone!!!!!!!!!!!
		ArrayList<StyleNode> children = v.getChildren();
		if( children == null ){
			if (v.compImp() < threshold) {
				v.setNoisy(true);
				v.setMeaningful(false);
			}
			else{
				v.setNoisy(false);
				v.setMeaningful(true);			// every text is meaningful :-/
			}
			return;
		}
		for (StyleNode node : children) {
			ArrayList<Element> elements = node.getElements();
			for (Element element : elements) 
				mark(element);
		}
		boolean flag = false;
		for (StyleNode node : children) {
			ArrayList<Element> elements = node.getElements();
			for (Element element : elements) {
				if (!element.isNoisy()) {
					v.setNoisy(false);
					flag = true;
				}
			}
		}
		if (!flag) {
			if (v.compImp() < threshold)
				v.setNoisy(true);
			else
				v.setNoisy(false);
		}
		for (StyleNode node : children) {
			ArrayList<Element> elements = node.getElements();
			for (Element element : elements) {
				if (!element.isMeaningful() || element.isNoisy()) {
					v.setMeaningful(false);
					return;
				}
			}
		}
		v.setMeaningful(true);
	}


	public void print(){
		Queue<Element> queue = new LinkedList<Element>();
		int currentHeight = 0;
		queue.add(root);
		tabNumber = 0;
		while(queue.isEmpty() == false){
			Element top = queue.remove();
			if(top.getHeight() > currentHeight){
				currentHeight = top.getHeight();
				tabNumber++;
			}
			System.out.print(new String(new char[tabNumber]).replace("\0", "\t"));
			System.out.println(top.toString());
			if(top.getChildren() != null){
				for (StyleNode stylenode : top.getChildren()) {
					System.out.print(new String(new char[tabNumber]).replace("\0", "\t"));
					System.out.println(stylenode.toString());
					for (Element element : stylenode.getElements()) {
						queue.add(element);
					}
				}
			}
			System.out.println();
		}
		return;
	}

	public void update(StyleTree styleTree) {
		root.merge(styleTree.getRoot());
	}

	/**
	 * this method is used to find the content of a new page and  
	 * remove all noisy Elements from it
	 * @param styleTree
	 * @throws RuntimeException if there is a Element in styleTree which 
	 * has more than one child.
	 */
	public void MapSST(StyleTree styleTree) throws RuntimeException{
		this.getRoot().MapSST(styleTree.getRoot());
	}

	//TODO written by apzarabi
	@Override
	protected StyleTree clone() throws CloneNotSupportedException {
		StyleTree ret = new StyleTree();
		ret.root = this.getRoot().clone();
		return ret;
	}
	/** 
	 * this method works if the StyleTree is a PageStyleTree
	 * @return
	 */
	public String toHTML(){
		Element.resetTabNumber();
		String html = this.getRoot().toHtml();
		html = "<!DOCTYPE html>\n" + html;
		return html;
	}
	
	public String getContent() {
		return this.getRoot().getContent().toString();
	}

	public Element getRoot() {
		return root;
	}

	public void setRoot(Element root) {
		this.root = root;
	}
	public int getStyleTreeNumber() {
		return StyleTreeNumber;
	}
	public void setStyleTreeNumber(int styleTreeNumber) {
		StyleTreeNumber = styleTreeNumber;
	}

	//TODO written by apzarabi
	/** 
	 * this method add the attribute "background-color:rgba(...) to text nodes of tree
	 */
	/*public void setBackground(){
		//finding BODY
		Element body = this.getRoot().getChildren().get(0).getElement(1);
		if(!body.getTagName().equals("body"))
			throw new RuntimeException("couldn't find body");

		body.setBackground();
		body.normalizeBackground();
	}
	 */
}
