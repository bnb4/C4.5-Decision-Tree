import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 資料集
 */
public class Bag {
	
	// 此分堆剩餘未用到的attributes
	private String[] unusedAttributes;	
	
	// 來源屬性
	private String rootAttribute;
	
	// 此分支名稱
	private String name;
	
	// 此分堆包含的資料
	private List<Element> elementsInBag = new ArrayList<Element>();
	
	// 結果的種類數
	private int numberOfOutput;
	
	/*
	 * 建構子
	 * @傳入 branchName: 分支名稱
	 * 	    unusedAttributes: 此分堆剩餘未用到的attributes
	 * 	    numberOfOutput: 結果的種類數
	 */
	public Bag(String rootAttribute, String branchName, List<Element> elementsInBag ,String[] unusedAttributes, int numberOfOutput) {
		this.rootAttribute = rootAttribute;
		this.name = branchName;
		this.unusedAttributes = unusedAttributes;
		this.numberOfOutput = numberOfOutput;
		this.elementsInBag = elementsInBag;
	}
	
	/*
	 * 取得分支名稱
	 * @回傳 分支名稱
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * 取得此分堆剩餘未用到的attributes
	 * @回傳 此分堆剩餘未用到的attributes陣列
	 */
	public String[] getUnusedAttrubutes() {
		return unusedAttributes;
	}
	
	/*
	 * 新增資料至分堆
	 * @傳入 element 單筆資料
	 */
	public void addElement(Element element) {
		elementsInBag.add(element);
	}
	
	/*
	 */
	public List<Element> getElementList() {
		return elementsInBag;
	}
	
	public String getRootAttribute() {
		return rootAttribute;
	}
	
	/*
	// 以熵最大的Attribute分割成多個 Bag
	public Bag[] splitBagByMinEntropy() {
		String maxAttribute = getMaxEntropyAttribute();
		String[] newAttributes = new String[this.unusedAttributes.length - 1];
		
		// 取得分割後的Bag剩餘的Attribute
		for (int i = 0, counter = 0; i < this.unusedAttributes.length; i++) {
			if (!this.unusedAttributes[i].equals(maxAttribute)) {
				newAttributes[counter++] = this.unusedAttributes[i];
			}
		}
				
		// 分類Bags
		Map<String, Bag> rawBags = new HashMap<String, Bag>();
		for (Element e : elementsInBag) {
			String maxAttributeData = e.getAttributeData(maxAttribute);
			
			// 若還沒有該分割種類的包，新增該包
			if (!rawBags.containsKey(maxAttributeData)) {
				rawBags.put(maxAttributeData, new Bag(null, maxAttributeData, newAttributes, numberOfOutput));
			}
				
			rawBags.get(maxAttributeData).addElement(e);
		}
		
		return rawBags.values().toArray(new Bag [rawBags.size()]);
	}
	
	// 取得熵最大的attribute
	public String getMaxEntropyAttribute() {
		String min = null;
		double value = 0.0;
		
		for (String attribute : getUnusedAttrubutes()) {
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
		Map<String, Integer> outputs = new HashMap<String, Integer>();
		double entropy = 0.0;
		
		// 把資料讀入Map中
		for (Element e : elementsInBag) {
			if (!outputs.containsKey(e.getOutput())) {
				outputs.put(e.getOutput(), 1);
			} else {
				outputs.put(e.getOutput(), outputs.get(e.getOutput()) + 1);
			}
		}
		
		// 計算熵
		for (String att : outputs.keySet()) {
            double part = outputs.get(att) * 1.0 / elementsInBag.size();
            entropy -= part * Math.log(part) / Math.log(numberOfOutput);
        }
		return entropy;
	}
	
	// 取得熵計算中attribute部分
	private double getAttributeEntropy(String attribute) {
		Map<String, HashMap<String, Integer>> outputs = new HashMap<String, HashMap<String, Integer>>();
		// 符合該屬性值的資料個數
		Map<String, Integer> attributes = new HashMap<String, Integer>();
		double entropy = 0.0;
		
		// 把資料讀入Map中
		for (Element e : elementsInBag) {
			
			String attData = e.getAttributeData(attribute);
			
			if (!attributes.containsKey(attData)) {
				attributes.put(attData, 1);
				outputs.put(attData, new HashMap<String, Integer>());
			} else {
				attributes.put(attData, attributes.get(attData) + 1);
			}
			
			Map<String, Integer> attOutput = outputs.get(attData);
			if (!attOutput.containsKey(e.getOutput())) {
				attOutput.put(e.getOutput(), 1);
			} else {
				attOutput.put(e.getOutput(), attOutput.get(e.getOutput()) + 1);
			}
		}
		
		// 計算各屬性值的熵
		for (String att : attributes.keySet()) {
			double tmp = 0.0;
            Map<String, Integer> output = outputs.get(att);
            for (String eachOutput: output.keySet()) {
	            	double part = output.get(eachOutput) * 1.0 / attributes.get(att);
	            	tmp -= part * Math.log(part) / Math.log(numberOfOutput);
            }

            double part = attributes.get(att) * 1.0 / elementsInBag.size();
            entropy += part * tmp;
        }
		return entropy;
	}
	*/
	
	/*
	 * 用與目前亂度差(Information Gain)最大之屬性做分類
	 * @回傳 分類後資料
	 */
	public Bag[] classifyByMaxIG() {
		// 目前最大亂度差
		double maxInformationGain = 0;
		// 目前產生最大亂度差的屬性名稱
		String targetAttribute = "";
		// 用目前有最大亂度差之屬性分類後資料集
		Map<String, List<Element>> targetClassifiedElements = new HashMap<String, List<Element>>();
		
		// 計算目前資料亂度
		double entropyOfBag = calculateEntropy(countNumberOfEachOutput(elementsInBag));
		
		// 找出能與目前資料亂度產生最大亂度差的屬性
		for (String attribute : unusedAttributes) {
			// 用屬性分類後資料集
			Map<String, List<Element>> classifiedElements = classifyElements(attribute);
			
			// 計算用屬性分類後的亂度
			double entropyOfAttribute = 0;
			for (String value : classifiedElements.keySet()) {
				Map<String, Integer> numberOfEachOutput = countNumberOfEachOutput(classifiedElements.get(value));
				double entropy = calculateEntropy(numberOfEachOutput);
				double scale = countNumberOfElements(numberOfEachOutput) * 1.0 / elementsInBag.size();
				entropyOfAttribute += entropy * scale;
			}
			
			double informationGain = entropyOfBag - entropyOfAttribute;
			
			if (informationGain > maxInformationGain) {
				maxInformationGain = informationGain;
				targetAttribute = attribute;
				targetClassifiedElements = classifiedElements;
			}
		}
		
		if (targetAttribute.equals("")) {
			return null;
		}
		
		Bag[] classifiedBags = new Bag[targetClassifiedElements.keySet().size()];
		String[] newUnusedAttributes = removeElement(unusedAttributes, targetAttribute);
		int index = 0;
		for (String value : targetClassifiedElements.keySet()) {
			classifiedBags[index] = new Bag(targetAttribute, 
											value, 
											targetClassifiedElements.get(value) ,
											newUnusedAttributes, 
											numberOfOutput);
			index++;
		}
		
		return classifiedBags;
	}
	
	/*
	 * 將某項目從該陣列中移除
	 * @參數 array: 要處理的陣列
	 *      e: 欲刪除之項目
	 * @回傳 刪除該項目後的陣列
	 */
	private String[] removeElement(String[] array, String e) {
		String[] newArray = new String[array.length - 1];
		
		int counter = 0;
		for (int i = 0; i < array.length; i++) {
			if (!array[i].equals(e)) {
				newArray[counter++] = array[i];
			}
		}
		
		return newArray;
	}
	
	
	/*
	 * 根據屬性分類資料行
	 * @參數 attribute: 屬性名稱
	 * @回傳 分類後的資料行
	 */
	private Map<String, List<Element>> classifyElements(String attribute) {
		Map<String, List<Element>> classifiedElements = new HashMap<String, List<Element>>();
		for (Element e : elementsInBag) {
			String attributeData = e.getAttributeData(attribute);
			if (!classifiedElements.containsKey(attributeData)) {
				classifiedElements.put(attributeData, new ArrayList<Element>());	
			}
			classifiedElements.get(attributeData).add(e);
		}
		return classifiedElements;
	}
	
	/*
	 * 計算各結果數
	 * @參數 elements: 欲統計資料行
	 * @回傳 各結果與對應數量的Map
	 */
	public Map<String, Integer> countNumberOfEachOutput(List<Element> elements) {
		Map<String, Integer> numberOfEachOutput = new HashMap<String, Integer>();
		for (Element e : elements) {
			String output = e.getOutput();
			if (!numberOfEachOutput.containsKey(output)) {
				numberOfEachOutput.put(output, 1);
			} else {
				numberOfEachOutput.put(output, numberOfEachOutput.get(output) + 1);
			}
		}
		return numberOfEachOutput;
	}
	
	/*
	 * 計算亂度
	 * @參數 numberOfEachOutput: 各結果的數量
	 * @回傳 亂度
	 */
	private double calculateEntropy(Map<String, Integer> numberOfEachOutput) {
		double entropy = 0.0;
		int numberOfElements = countNumberOfElements(numberOfEachOutput);
		
		for (String key : numberOfEachOutput.keySet()) {
            double probability = numberOfEachOutput.get(key) * 1.0 / numberOfElements;
            entropy -= probability * Math.log(probability) / Math.log(numberOfOutput);
        }
		return entropy;
	}
	
	/*
	 * 計算Map中Element數量
	 * @參數 numberOfEachOutput: 各結果的數量
	 * @回傳 數量
	 */
	private int countNumberOfElements(Map<String, Integer> numberOfEachOutput) {
		int sum = 0;
		for (String key : numberOfEachOutput.keySet()) {
			sum += numberOfEachOutput.get(key);
		}
		return sum;
	}
	
	/*
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Bag: ");sb.append(getName()); sb.append("\r\n");
		for (String attribute : getUnusedAttrubutes()) {
			sb.append(attribute);sb.append(": ");sb.append(getEntropy(attribute));
			sb.append(" (");sb.append(getOutputEntropy());sb.append(" - ");sb.append(getAttributeEntropy(attribute));sb.append(")");
			sb.append("\r\n");
		}
		return sb.toString();
	}
	*/
}

/**
 * 單筆資料
 */
class Element {
	
	// <Attribute名稱: Attribute值>
	private Map<String, String> attDataMap = new HashMap<String, String>(); 	
	// 結果
	private String output;
	
	/*
	 * 建構子
	 * 建立屬性名稱與屬性值的關連與設定結果
	 * @參數 attributes: 屬性名稱陣列
	 *      attData: 屬性值陣列
	 *      result: 結果
	 */
	public Element(String[] attributes, String[] attData, String result) {
		for (int i = 0; i < attributes.length; i++) {
			this.attDataMap.put(attributes[i], attData[i]);
		}
		this.output = result;
	}
	
	/*
	 * 取得特定屬性值
	 * @參數 attribute: 屬性名稱
	 * @回傳 屬性值
	 */
	public String getAttributeData(String attribute) {
		return this.attDataMap.get(attribute);
	}
	
	/*
	 * 取得結果
	 * @回傳 結果
	 */
	public String getOutput() {
		return this.output;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Element)) {
			return false;
		}
		Element oElement = (Element) other;
		boolean isSame = true;
		for (String attribute : attDataMap.keySet()) {
			if (!this.getAttributeData(attribute).equals(oElement.getAttributeData(attribute))) {
				isSame = false;
				break;
			}
		}
		isSame = isSame && (this.getOutput().equals(oElement.getOutput()));
		return isSame;
	}*/
}