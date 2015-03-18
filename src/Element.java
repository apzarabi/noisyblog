import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;


public class Element {
	private static final String backgroundAttrs =
			" background-color:rgba(153, 153, 255, 0.3); color:rgb(255, 51, 0) ";
	static boolean inHead; //for print
	private static int numberOfElements = 0;
	private static int tabNumber = 0;
	private int height;
	private boolean noisy;
	private boolean meaningful;
	private String value;		//if tagName = #text, this field contains the original text.
	private int elementNumber;
	private String tagName;
	private org.w3c.dom.NamedNodeMap attrs;
	private String localAttrs;
	private StyleNode wrapper;
	private ArrayList<StyleNode> children;
	private double gama = 0.9;
	private ArrayList<String> texts = new ArrayList<String>();
	private boolean nodeImpCalculated = false, compImpCalculated = false;
	private double nodeImportance, compImportance;

	
	/** 
	 * this constructor is used for cloning
	 */
	public Element() {
		this.setElementNumber(numberOfElements);
		++numberOfElements;
		this.height = 0;
		this.noisy = false;
		this.meaningful = false;
	}
	
	/**
	 * this constructor is used to convert a DOM to a StyleTree.
	 * @param element
	 */
	public Element(org.w3c.dom.Node element, int height) {
		this.setElementNumber(numberOfElements);
		++numberOfElements;
		setHeight(height);
		this.noisy = false;
		this.meaningful = false;
		
		this.setTagName(element.getNodeName());
		this.setAttrs(element.getAttributes());
		if(this.getTagName().equals("#text")){
			this.setValue(element.getNodeValue().trim());
		}
		org.w3c.dom.NodeList nodelist = element.getChildNodes();
		ArrayList<Element> subElements = new ArrayList<Element>();
		for(int i = 0; i<nodelist.getLength(); i++){
			org.w3c.dom.Node newNode = nodelist.item(i);
			if(newNode.getNodeName().equals("#text")){
				if(newNode.getNodeValue().trim().equals(""))
					continue;
			}
			Element newElement = new Element(newNode, this.height+1);
			subElements.add(newElement);
		}
		if(subElements.size() > 0){
			StyleNode child = new StyleNode(this, subElements);
			this.children = new ArrayList<StyleNode>();
			this.children.add(child);
		}
	}
	
	//TODO
	public double nodeImp() {
		//TODO this is written by apzarabi.
		if (nodeImpCalculated)
			return nodeImportance;
		nodeImpCalculated = true;
		if(this.getWrapper() == null)
			return 1;
		
		
		int m = this.getWrapper().getRepetition();
		if (m == 1) 
			return 1;
		double ret = 0;
		if(children != null){
			for (int i = 0; i < children.size(); i++) {
				double p = (double)children.get(i).getRepetition()/(double)m;
//				System.err.println("P: " + p + " " + p*(Math.log(p)/Math.log(m)) + " " +(Math.log(p)/Math.log(m)));
				ret -= p*(Math.log(p)/Math.log(m));
				
			}
		}
		nodeImportance = ret;
		return nodeImportance;
	}
	
	public double compImp() {
		if (compImpCalculated)
			return compImportance;
		compImpCalculated = true;
		if (children != null && children.size() > 0) {
			int l = children.size();
			double sigma = 0;
			int sum = 0;
			for (int i = 0; i < children.size(); i++)		// mitune behtar beshe agar ke save konam nakha
				sum += children.get(i).getRepetition();		//har dafe hesabesh konam.!!!!!
			for (int i = 0; i < children.size(); i++)
				sigma += (double)children.get(i).getRepetition()/sum * children.get(i).comImp();
			compImportance = (1 - Math.pow(gama, l)) * nodeImp() + Math.pow(gama, l) * sigma;
//			if (attrs.getNamedItem("id") != null) {
//				System.err.println(attrs.getNamedItem("id") + " "+  Math.pow(gama, l)+ " " + sigma + " " + compImportance +
//						" " + nodeImp());
//			}
			return compImportance;
		}
		else {
			if (this.getTagName().equals("#text")) {
				if (wrapper.getRepetition() == 1) {
					compImportance = 1.0;
					return 1;
				}
				String s = this.getValue();
				String splited[] = s.split(" ");
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				for (String word : splited) 
					if (map.containsKey(word))
						map.put(word, map.get(word)+1);
					else
						map.put(word, 1);
				for (String s1 : texts) {
					splited = s1.split(" ");
					for (String word : splited) 
						if (map.containsKey(word))
							map.put(word, map.get(word)+1);
						else
							map.put(word, 1);
				}
				HashMap<String, Double> H = new HashMap<String, Double>(); 
				for (String key : map.keySet()) {
					double sigma = 0.0;
					splited = s.split(" ");
					int sum = 0;
					for (String word : splited) {
						if (word.equals(key))
							sum++;
					}
					double p = (double)sum/map.get(key);
					if (p >= 0.001)
					sigma -= p*(Math.log(p)/Math.log(wrapper.getRepetition()));
					for (String s1 : texts) {
						sum = 0;
						splited = s1.split(" ");
						for (String word : splited) 
							if (word.equals(key))
								sum++;
						p = (double)sum/map.get(key);
						if (p >= 0.001)
							sigma -= p*(Math.log(p)/Math.log(wrapper.getRepetition()));
					}
//					System.err.println("        MAP " + key + " -> " + sigma);
					H.put(key, sigma);
				}
				double ret = 0.0;
				for (Double d : H.values()) {
					ret += d;
				}
				//System.err.println(" RET: " + ret + "  " + H.size());
				ret /= H.size();
				//System.err.println(ret);
				ret = 1-ret;
				compImportance = (int)(ret*10000)/10000.0;
				//System.err.println(ret);
				//compImportance = Math.sqrt(compImportance);
				return compImportance;
			}
			//TODO REALLY
			if (tagName.equals("meta"))
				return 1.0;
			return 0.0;
		}
	}	
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean equal(Element element) {
		return this.tagName.equals(element.tagName);
	}
	
	/**
	 * 
	 * @param element
	 */
	public void merge(Element element) {
		if (element.getChildren() != null && element.getChildren().size() > 0 ) {
			StyleNode stNode = element.getChildren().get(0);
			boolean flag = false;			//equals to one of sst's StyleNodes
			if (children == null)
				children = new ArrayList<StyleNode>();
			for (StyleNode temp : children) {
				if (temp.equal(stNode)) {
					flag = true;
					temp.addRepetition();
					temp.merge(stNode);		//this function merge two StyleNodes.
					return;
				}
			}
			if (!flag) {
				this.children.add(stNode);
				return;
			}
		}
		else {
			if (this.tagName.equals(element.tagName) && this.tagName.equals("#text"))
				this.texts.add(element.value);
		}
	}
	
	/**
	 * in the algorithm, this == E
	 * @param element in the algorithm, this is E_p
	 * @throws RuntimeException if there is a Element in descendants
	 * if this element which has more than one child.
	 */
	public void MapSST(Element element) throws RuntimeException{
		if(this.isNoisy()){
			element.setMeaningful(false);
			element.setNoisy(true);
			return;
		}
		element.setMeaningful(true);
		if(this.isMeaningful()){
			element.setMeaningful(true);
			element.setNoisy(false);
			return;
		}
		if(element.getChildren().size() > 1)
			throw new RuntimeException("this element has more than one child: " + element.toString());
		StyleNode s2 = element.getChildren().get(0);
		boolean flag = false;
		for(StyleNode s:this.getChildren()){
			if(s2.equals(s)){
				flag = true;
				for(int i = 0; i<s2.getElements().size(); i++)
					s.getElement(i).MapSST(s2.getElement(i));
			}
		}
		if(!flag){ // element is possibly meaningful
			System.err.println("blindly returned true " + this.toString() + "||||" + element.toString());
			for(int i = 0; i<element.getChildren().get(0).getElements().size(); i++)
				System.err.println("\t" + element.getChildren().get(0).getElement(i).toString());
			System.err.flush();
			element.setMeaningful(true);
			element.setNoisy(false);
			return;
		}
		return;
	}
	
	//TODO written by apzarabi
	/** 
	 * this is a recursive method for adding attribute "background-color:rgba(...)" to text nodes
	 */
	/*public void setBackground(){
		if(this.getTagName().equals("#text")){
			this.localAttrs = backgroundAttrs;
			return;
		}
		for(StyleNode stylenode: this.getChildren())
			for(Element element: stylenode.getElements())
				element.setBackground();
		return;
	}
	*/
	/**
	 * this element should be in a PageStyleTree
	 */
	/*public void normalizeBackground(){
		if(this.getChildren() == null || this.getChildren().size() == 0 )
			return;
		if(this.getChildren().size() > 1)
			throw new RuntimeException("normalize needed a PageStyleTree");
		boolean flag = true;
		for(Element element: this.getChildren().get(0).getElements()){
			element.normalizeBackground();
			if( element.localAttrs == null || element.localAttrs == "")
				flag = false;
		}
		if(flag){
			for(Element element: this.getChildren().get(0).getElements()){
				element.localAttrs = null;
			}
			this.localAttrs = backgroundAttrs;
		}
	}*/
	
	//TODO written by apzarabi
	@Override
	protected Element clone() throws CloneNotSupportedException {
		Element ret = new Element();
		ret.height = this.height;
		ret.noisy = this.noisy;
		ret.meaningful = this.meaningful;
		ret.value = this.value;
		ret.tagName = this.tagName;
		ret.attrs = this.attrs;	//CAUTION: ret.attrs == this.attrs. they point to same object.
		
		ArrayList<StyleNode> children = new ArrayList<StyleNode>();
		for(StyleNode stylenode: this.getChildren()){
			StyleNode styleNodeClone = stylenode.clone();
			styleNodeClone.setParent(ret); 
			children.add(styleNodeClone);
		}
		
		ret.children = children;
		return ret;
	}
	
	public String toHtml(){
		if( this.isNoisy() )
			return "";
		String tabs = new String(new char[tabNumber]).replace("\0", "\t");
		if(this.getTagName().equals("#text"))
			return tabs + this.getValue() + "\n";
		String ret = String.format("%s<%s %s>\n", tabs, this.getTagName(), this.getAllAttributes());
		tabNumber++;
		if( this.getChildren() != null ){
			if( this.getChildren().size() > 1 )
				throw new RuntimeException("toHtml: PageStyleTree needed.");
			StyleNode stylenode = this.getChildren().get(0);
			for(Element element: stylenode.getElements())
				ret += element.toHtml();
		}
		tabNumber--;
		ret += String.format("%s</%s>\n", tabs, this.getTagName());
		return ret;
	}
	/** 
	 * this element must be in a PageStyleTree
	 * @param ret
	 */
	public String toHtmlWithBackground(){
		String tabs = new String(new char[tabNumber]).replace("\0", "\t");
		if(this.getTagName().equals("#text")){
			if(inHead == false)
				return String.format("%s<span %s> %s </span>\n", tabs, this.getAllAttributes(), this.getValue());
			else
				return tabs + this.getValue() + "\n";
		}
		if(this.getTagName().equals("head"))
			inHead = true;
		String ret = String.format("%s<%s %s>\n", tabs, this.getTagName(), this.getAllAttributes());
		tabNumber++;
		if( this.getChildren() != null ){
			if( this.getChildren().size() > 1 )
				throw new RuntimeException("toHtml: PageStyleTree needed.");
			StyleNode stylenode = this.getChildren().get(0);
			for(Element element: stylenode.getElements())
				ret += element.toHtmlWithBackground();
		}
		tabNumber--;
		ret += String.format("%s</%s>\n", tabs, this.getTagName());
		if(this.getTagName().equals("head"))
			inHead = false;
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = null;
		if(this.getWrapper() != null)
			ret = String.format("<%s num=%d, wrap=%d>",
				tagName.toUpperCase(), elementNumber, wrapper.getStyleNodeNumber());
		else 
			ret = String.format("<%s num=%d, wrap=-1>",
					tagName.toUpperCase(), elementNumber);
		if(this.tagName.equals("#text"))
			ret += "[" + this.getValue() + "] ";
		ret += " nodeImp=" + this.nodeImp() + " compImp" + this.compImp();
		if(this.isMeaningful())
			ret += " !!!";
		if(this.isNoisy())
			ret += " @@@";
		if (this.tagName.equals("div")) {
			ret += "  " + this.attrs.getNamedItem("class");
			ret += "  " + this.attrs.getNamedItem("id");
		}
		return ret;
	}

	public static void resetTabNumber(){
		tabNumber = 0;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public org.w3c.dom.NamedNodeMap getAttrs() {
		return attrs;
	}
	
	public String getAttrsAsString(){
		NamedNodeMap x = this.getAttrs();
		String ret = "";
		if( x == null )
			return ret;
		for(int i = 0; i<x.getLength(); i++)
			ret += x.item(i).toString() + " ";
		return ret;
	}
	/**
	 * @return a string, consisting of all attrs, both in this.attrs and this.localAttrs
	 */
	public String getAllAttributes(){
		NamedNodeMap x = this.getAttrs();
		String ret = "";
		
		boolean flag = false;
		if(x!= null){
			for(int i = 0; i<x.getLength(); i++){
				if( x.item(i).getNodeName().equals("style") && this.localAttrs != null){
					flag = true;				
					x.item(i).setNodeValue(x.item(i).getNodeValue() + this.localAttrs );
				}
				ret += x.item(i).toString() + " " ;			
			}
		}
		if(!flag && this.localAttrs != null){
			ret += String.format(" style=\"%s\" ", this.localAttrs);
		}
		return ret;
	}

	public void setAttrs(org.w3c.dom.NamedNodeMap attrs) {
		this.attrs = attrs;
	}

	public StyleNode getWrapper() {
		return wrapper;
	}

	public void setWrapper(StyleNode wrapper) {
		this.wrapper = wrapper;
	}

	public ArrayList<StyleNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<StyleNode> children) {
		this.children = children;
	}

	public int getElementNumber() {
		return elementNumber;
	}

	public void setElementNumber(int elementNumber) {
		this.elementNumber = elementNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isNoisy() {
		return noisy;
	}

	public void setNoisy(boolean noisy) {
		this.noisy = noisy;
	}

	public boolean isMeaningful() {
		return meaningful;
	}

	public void setMeaningful(boolean meaningful) {
		this.meaningful = meaningful;
	}
	
	
}
