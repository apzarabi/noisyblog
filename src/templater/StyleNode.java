package templater;
import java.util.ArrayList;


public class StyleNode {
	private static int numberOfStyleNodes = 0;
	private int styleNodeNumber;
	private Element parent;
	private int repetition;
	private ArrayList<Element> elements;
	private double compImp;
	private boolean compCalculated = false;

	
	/**
	 * for cloning
	 */
	public StyleNode(){
		styleNodeNumber = numberOfStyleNodes++;
		this.repetition = 1;
	}
	public StyleNode(Element parent, ArrayList<Element> elements) {
		this.repetition = 1;
		this.parent = parent;
		this.elements = elements;
		for (Element element : elements) {
			element.setWrapper(this);
		}
		styleNodeNumber = numberOfStyleNodes++;
	}
	
	public double comImp() {
		if (compCalculated)
			return compImp;
		else
			compCalculated = true;
		int k = elements.size();
		double ret = 0;
		for (int i = 0; i < k; i++)
			ret += elements.get(i).compImp();
		compImp = ret / k;
		return compImp;
	}

	
	public void merge(StyleNode styleNode) {
		for (int i = 0; i < elements.size(); i++) {
			Element thisNodeElement = elements.get(i), thatNodeElement = styleNode.getElement(i);
			thisNodeElement.merge(thatNodeElement);
		}
	}
	/**
	 * 
	 * @param node: another StyleNode which should be compared to this.
	 * @return true if equal and false otherwise.
	 */
	public boolean equal(StyleNode node) {
		if (elements.size() != node.getElements().size())
			return false;
		for (int i = 0; i < elements.size(); i++)
			if (!elements.get(i).equal(node.getElement(i)))
				return false;
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj == null )
			return false;
		if( !(obj instanceof StyleNode) )
			return false;
		StyleNode x = (StyleNode) obj;
		if(this.elements == null){
			if(x.elements == null)
				return true;
			else
				return false;
		}
		if(this.elements.size() != x.elements.size())
			return false;
		for(int i =0 ; i<this.elements.size(); i++)
			if( ! this.elements.get(i).getTagName().equals(x.elements.get(i).getTagName()) )
				return false;
		return true;
	}
	/**
	 * for cloning
	 */
	@Override
	protected StyleNode clone() throws CloneNotSupportedException {
		StyleNode ret = new StyleNode();
		ret.repetition = this.repetition;
		ArrayList<Element> elements = new ArrayList<Element>();
		for(Element element: this.getElements()){
			Element elementClone = element.clone();
			elementClone.setWrapper(ret);
			elements.add(elementClone);
		}
		ret.elements = elements;
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = String.format("<snode #%d, stylenodeNum=%d, elements={ "
				,repetition, styleNodeNumber);
		for (Element element : elements) 
			ret += element.toString() + ", ";
		ret += "} comImp=" + this.comImp() + ">";
		return ret;
	}
	
	public Element getParent() {
		return parent;
	}

	public void setParent(Element parent) {
		this.parent = parent;
	}

	public int getRepetition() {
		return repetition;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public ArrayList<Element> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Element> elements) {
		this.elements = elements;
	}
	public Element getElement(int i) {
		return elements.get(i);
	}

	public void addRepetition() {
		this.repetition++;
	}
	
	public int getStyleNodeNumber() {
		return styleNodeNumber;
	}

}
