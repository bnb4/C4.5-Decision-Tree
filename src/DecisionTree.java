import java.util.*;
/*
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
}*/

// ====================以上為樹狀結構=================

/**
 * 存放單筆資料
 *
 */
class Element {
	private Map<String, String> attData = new HashMap<>(); 	// <Attribute名稱: Attribute值>
	private String output = null;							// CLASS
	
	public Element(String [] attributes, String [] attData, String result) {
		for (int i = 0; i < attributes.length; i++) {
			this.attData.put(attributes[i], attData[i]);
		}
		this.output = result;
	}
	
	// 取得某標籤資料 (input)
	public String getAttributeData(String attribute) {
		return this.attData.get(attribute);
	}
	
	// 取得輸出 (output)
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
	
	// 該Bag分支的名稱
	private String name = null;
	
	// 此分堆包含的資料
	private List<Element> data = new ArrayList<>();
	
	public Bag(String branchName, String [] attributes) {
		this.name = branchName;
		this.attributes = attributes;
	}
	
	// 取得該Bag名稱
	public String getName() {return name;}
	
	// 新增資料到該Bag
	public Bag addElement(Element element) {
		data.add(element);
		return this;
	}
	
	// 取得該Bag中的 Attributes
	public String [] getAttrubutes() {
		return attributes;
	}
	
	// 以熵最大的Attribute分割成多個 Bag
	public Bag [] splitBagByMinEntropy() {
		
		String maxAttribute = getMaxEntropyAttribute();
		String [] newAttributes = new String [this.attributes.length - 1];
		
		// 取得分割後的Bag剩餘的Attribute
		for (int i = 0, counter = 0; i < this.attributes.length; i++) 
			if (!this.attributes[i].equals(maxAttribute)) 
				newAttributes[counter++] = this.attributes[i];
		
		// 分類Bags
		Map<String, Bag> rawBags = new HashMap<>();
		for (Element e : data) {
			String maxAttributeData = e.getAttributeData(maxAttribute);
			
			// 若還沒有該分割種類的包，新增該包
			if (!rawBags.containsKey(maxAttributeData)) 
				rawBags.put(maxAttributeData, new Bag(maxAttributeData, newAttributes));
			
			rawBags.get(maxAttributeData).addElement(e);
		}
		
		return rawBags.values().toArray(new Bag [rawBags.size()]);
	}
	
	// 取得熵最大的attribute
	public String getMaxEntropyAttribute() {
		String min = null;
		double value = 0.0;
		
		for (String attribute : getAttrubutes()) {
			double nowValue = getEntropy(attribute);
			if (nowValue > value) {
				min = attribute;
				value = nowValue;
			}
		}
		
		return min;
	}
	
	// 取得某 attribute 的熵
	public double getEntropy(String attribute) {
		return getOutputEntropy() - getAttributeEntropy(attribute);
	}
	
	// 取得熵計算中output部分
	private double getOutputEntropy() {
		HashMap<String, Integer> outputs = new HashMap<>();
		double entropy = 0.0;
		
		// 把資料讀入Map中
		for (Element e : data) {
			if (!outputs.containsKey(e.getOutput())) {
				outputs.put(e.getOutput(), 1);
			} else {
				outputs.put(e.getOutput(), outputs.get(e.getOutput()) + 1);
			}
		}
		
		// 計算熵
		for (String att : outputs.keySet()) {
            double part = outputs.get(att) * 1.0 / data.size();
            entropy -= ( (part) * (Math.log(part) / Math.log(2)));
        }
		return entropy;
	}
	
	// 取得熵計算中attribute部分
	private double getAttributeEntropy(String attribute) {
		HashMap<String, HashMap<String, Integer>> outputs = new HashMap<>();
		HashMap<String, Integer> attributes = new HashMap<>();
		double entropy = 0.0;
		
		// 把資料讀入Map中
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
		
		// 計算熵
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Bag: ");sb.append(getName()); sb.append("\r\n");
		for (String attribute : getAttrubutes()) {
			sb.append(attribute);sb.append(": ");sb.append(getEntropy(attribute));
			sb.append(" (");sb.append(getOutputEntropy());sb.append(" - ");sb.append(getAttributeEntropy(attribute));sb.append(")");
			sb.append("\r\n");
		}
		return sb.toString();
	}
}

public class DecisionTree {
	
}
