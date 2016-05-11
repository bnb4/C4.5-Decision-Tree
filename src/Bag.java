import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 資料集
 */
public class Bag {
	
	// 此分堆剩餘未用到的屬性陣列
	private String[] unusedAttributes;		
	// 來源屬性 (e.g. "體型")
	private String rootAttribute;	
	// 此分支名稱 (e.g. "大")
	private String name;
	// 內容資料行
	private List<Element> elementsInBag = new ArrayList<Element>();
	// 結果的種類數
	private int numberOfOutput;
	
	/*
	 * 建構子
	 * @傳入 rootAttribute: 來源屬性
	 *      branchName: 分支名稱
	 *      elementsInBag: 內容資料行
	 * 	    unusedAttributes: 此分堆剩餘未用到的屬性陣列
	 * 	    numberOfOutput: 結果的種類數
	 */
	public Bag(String rootAttribute, 
			   String branchName, 
			   List<Element> elementsInBag,
			   String[] unusedAttributes, 
			   int numberOfOutput) {
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
	 * 取得此分堆剩餘未用到的屬性陣列
	 * @回傳 此分堆剩餘未用到的屬性陣列
	 */
	public String[] getUnusedAttrubutes() {
		return unusedAttributes;
	}
	
	/*
	 * 取得內容資料行
	 * @回傳 內容資料行
	 */
	public List<Element> getElementList() {
		return elementsInBag;
	}
	
	/*
	 * 取得來源屬性
	 * @回傳 來源屬性
	 */
	public String getRootAttribute() {
		return rootAttribute;
	}
		
	/*
	 * 用與目前亂度差(Information Gain, IG)最大之屬性做分類
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
			
			// 用屬性分類後的亂度
			double entropyOfAttribute = 0;
			// 計算屬性底下各分支亂度依比例加總
			for (String value : classifiedElements.keySet()) {
				Map<String, Integer> numberOfEachOutput = countNumberOfEachOutput(classifiedElements.get(value));
				// 分支亂度
				double entropyOfBranch = calculateEntropy(numberOfEachOutput);
				// 分支佔屬性的比例
				double scaleOfBranch = countNumberOfElements(numberOfEachOutput) * 1.0 / elementsInBag.size();
				
				entropyOfAttribute += entropyOfBranch * scaleOfBranch;
			}
			
			// 與目前亂度差(Information Gain)
			double informationGain = entropyOfBag - entropyOfAttribute;
			
			// 若與目前亂度差大於記錄的亂度差，更新記錄
			if (informationGain > maxInformationGain) {
				maxInformationGain = informationGain;
				targetAttribute = attribute;
				targetClassifiedElements = classifiedElements;
			}
		}
		
		// 如果"沒有剩餘未分類屬性"或"所有屬性分類後亂度與目前亂度相同"，則此分支結束
		if (targetAttribute.equals("")) {
			return null;
		}
		
		// 建立分類後資料
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
		for (int i = 0, counter = 0; i < array.length; i++) {
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
}