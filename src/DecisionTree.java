import java.io.File;
import java.io.IOException;
import java.util.*;

class Node {
	
	private String description = "";
	
	public Node() {}
	public Node(String description) { this.description = description; }
	
	public String getDescription() {
		return description;
	}
}

class AttributeNode<T> extends Node {
	
	// 該Attribute可能的選項
	private List<String> decisions = new ArrayList<>();
	
	// 該Attribute的子節點
	private Map<String, Node> children = new HashMap<>();
	
	// 目前此節點包含的可能 Class
	private Set<T> classes = new HashSet<>();
	
	// 建構子，傳入選項(黑、白...) 及描述(毛色)
	public AttributeNode(List<String> decisions, String description) {
		super(description);
		this.decisions = decisions;
	}
	
	// 加入class清單
	public boolean addClass(T _class) {
		return classes.add(_class);
	}
	
	// 取得所有 class
	@SuppressWarnings("unchecked")
	public T[] getClasses() {
		return (T[]) classes.toArray(new Object[classes.size()]);
	}
	
	// 取得decisions
	public String[] getDecisions() {
		return decisions.toArray(new String[decisions.size()]);
	}
	
	// 設定子節點
	public boolean setNode(Node node, String decision) {
		if (!decisions.contains(decision)) return false;
		children.put(decision, node);
		return true;
	}
	
	// 取得子節點
	public Node getNode(String decision) {
		if (!decisions.contains(decision)) return null;
		return children.get(decision);
	}
}

// 末端節點，儲存結果
class LeafNode<T> extends Node {

	// 目前此節點包含最終 Class
	private T finalClass;
	
	public LeafNode(T finalClass) {
		this.finalClass = finalClass;
	}
	
	public T getData() {
		return finalClass;
	}
}

// ====================以上為樹狀結構=================

/**
 * 存放單筆資料
 *
 */
class Element {
	private Map<String, String> attData = new HashMap<>(); // <Attribute名稱: Attribute值>
	private String output = null;
	
	public Element(String [] attributes, String [] attData, String result) {
		for (int i = 0; i < attributes.length; i++) {
			this.attData.put(attributes[i], attData[i]);
		}
		this.output = result;
	}
	
	public String getAttributeData(String attribute) {
		return this.attData.get(attribute);
	}
	
	public String getOutput() {
		return this.output;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Element)) return false;
		Element oElement = (Element) other;
		boolean isSame = true;
		for(String attribute : attData.keySet()) {
			if (!this.getAttributeData(attribute).equals(oElement.getAttributeData(attribute))) {
				isSame = false;
				break;
			}
		}
		return isSame && (this.getOutput().equals(oElement.getOutput()));
	}
	
}

/**
 * Bag 存放分堆的Element
 */
class Bag {
	
	// 此分堆剩餘未用到的 attributes
	private String [] attributes;	
	
	// 此分堆包含的資料
	private List<Element> data = new ArrayList<>();
	
	public Bag(String [] attributes) {
		this.attributes = attributes;
	}
	
	// 新增資料到該Bag
	public Bag addElement(Element element) {
		data.add(element);
		return this;
	}
	
	// 取得該Bag中的 Attributes
	public String [] getAttrubutes() {
		return attributes;
	}
	
	//Math.log(x) / Math.log(2)
	
	public double getEntropy(String attribute) {
		return getOutputEntropy() - getAttributeEntropy(attribute);
	}
	
	private double getOutputEntropy() {
		HashMap<String, Integer> outputs = new HashMap<>();
		double entropy = 0.0;
		
		for (Element e : data) {
			if (!outputs.containsKey(e.getOutput())) {
				outputs.put(e.getOutput(), 1);
			} else {
				outputs.put(e.getOutput(), outputs.get(e.getOutput()) + 1);
			}
		}
		
		for (String att : outputs.keySet()) {
             
            double part = outputs.get(att) * 1.0 / data.size();
            entropy -= ( (part) * (Math.log(part) / Math.log(2)));
        }
		return entropy;
	}
	
	private double getAttributeEntropy(String attribute) {
		HashMap<String, HashMap<String, Integer>> outputs = new HashMap<>();
		HashMap<String, Integer> attributes = new HashMap<>();

		double entropy = 0.0;
		
		for (Element e : data) {

			String attData = e.getAttributeData(attribute);
			
			if (!attributes.containsKey(attData)) {
				attributes.put(attData, 1);
				outputs.put(attData, new HashMap<String, Integer>());
			} else {
				attributes.put(attData, attributes.get(attData) + 1);
			}
			
			HashMap<String, Integer> attOutput = outputs.get(attData);
			if (!attOutput.containsKey(e.getOutput())) attOutput.put(e.getOutput(), 1);
			else attOutput.put(e.getOutput(), attOutput.get(e.getOutput()) + 1);
		}
		
		for (String att : attributes.keySet()) {
			double tmp = 0.0;
            HashMap<String, Integer> output = outputs.get(att);
            for (String eachOutput: output.keySet()) {
            	double part = output.get(eachOutput) * 1.0 / attributes.get(att);
            	tmp -= ((part) * (Math.log(part) / Math.log(2)));
            }

            double part = attributes.get(att) * 1.0 / data.size();
            entropy += part * tmp;
        }
		return entropy;
	}
	
}

/**
 * 將檔案 parse 並取出
 */
class FileParser {
	
	private String [] attributes;
	private List<Element> data = new ArrayList<>();
	
	public FileParser(String fileName) {
		
		try (Scanner scanner = new Scanner(new File(fileName))) {
			if (scanner.hasNext()) loadAttribute(scanner.nextLine());
			while (scanner.hasNext()){
				addData(scanner.nextLine());
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	// 取得檔案中內含的 Attribute (內部)
	private void loadAttribute(String attStr) {
		// 文件第一行是attribute名稱
		String [] raw = attStr.trim().split(" ");
		attributes = Arrays.copyOfRange(raw, 0, raw.length - 1);
	}
	
	// 取得所有 Attributes (外部 getter)
	public String [] getAttributes() {
		return attributes;
	}
	
	// 讀入檔案資料
	private void addData(String dataStr) {
		if (dataStr == null || dataStr.equals("")) return;

		String [] splData = dataStr.split(" ");
		
		if (splData.length != getAttributesCount() + 1) return; // 資料不全
		
		data.add(new Element(getAttributes()
							, Arrays.copyOfRange(splData, 0, getAttributesCount())
							, splData[getAttributesCount()] )
				);
	}
	
	// 取得 Attributes 數量
	public int getAttributesCount() {
		if (attributes == null) return 0; 
		return attributes.length;
	}
	
	// 取得所有數據
	public List<Element> getDatas() {
		return new ArrayList<>(data);
	}
}

public class DecisionTree {
	public static void main(String [] args) {
		FileParser filePaser = new FileParser("TEST.txt");
		List<Element> o = filePaser.getDatas();
		Bag bag = new Bag(filePaser.getAttributes());
		for (Element e : o) bag.addElement(e);
		
		System.out.println(bag.getEntropy("Attribute3"));
		System.out.println("S");
	}
}
