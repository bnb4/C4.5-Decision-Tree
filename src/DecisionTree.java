import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 決策樹
 */
public class DecisionTree {
	
	// 根節點
	private Node root = null;
	
	/*
	 * 建構子
	 * @參數 trainingSet: 訓練集資料行
	 * 		attribute: 屬性名稱陣列
	 * 		numOfOutput: 結果種類數
	 */
	public DecisionTree(List<Element> trainingSet, String[] attribute, int numOfOutput) {
		Bag firstBag = new Bag(null, null, trainingSet, attribute, numOfOutput);
		buildTree(firstBag, null);
	}
	
	/*
	 * 建構決策樹
	 * @參數 bag: 欲分類資料集
	 * 		parent: 父節點
	 */
	private void buildTree(Bag bag, Node parent) {
		Bag[] classifiedBags = bag.classifyByMaxIG();
		
		// 分支終點
		if (classifiedBags == null) {
			// 用結果字串建立節點
			String ouputString = bag.getElementList().get(0).getOutput();
			Node ouputNode = new Node(ouputString, parent);
			// 與父節點關聯
			parent.addChildren(bag.getName(), ouputNode);
			return;
		}
		
		// 用與目前亂度差最大屬性建立節點
		String rootAttribute = classifiedBags[0].getRootAttribute();
		Node newNode = new Node(rootAttribute, parent);
		
		// 是否為根節點
		if (root == null) {
			root = newNode;
		} else {
			// 與父節點關聯
			parent.addChildren(bag.getName(), newNode);
		}
		
		// 各分支繼續建構決策樹
		for (Bag b : classifiedBags) {
			buildTree(b, newNode);
		}			
	}
	
	/*
	 * 節點物件
	 */
    class Node {

        private String name;
        private Node parent;
        private Map<String, Node> childrens = new HashMap<String, Node>();
        
        /*
         * 建構子
         * @參數 name: 節點名稱
         * 		parent: 父節點
         */
        public Node(String name, Node parent) {
        		this.name = name;
        		this.parent = parent;
        }
        
        /*
         * 加入子節點
         * @參數 branchName: 分支名稱
         * 		child: 子節點
         */
        public void addChildren(String branchName, Node child) {
        		childrens.put(branchName, child);
        	
        }
        
        /*
         * 取得節點名稱
         * @回傳 節點名稱
         */
        public String getName() {
			return name;
		}
        
        /*
         * 取得父節點
         * @回傳 父節點
         */
        public Node getParent() {
			return parent;
		}
        
        /*
         * 取得所有子節點
         * @回傳 所有子節點
         */
        public Map<String, Node> getChildrens() {
			return childrens;
		}
        
        /*
         * 取得特定子節點
         * @回傳 特定子節點
         */
        public Node getOneChild(String branchName) {
			return childrens.get(branchName);
		}
        
    }
    
    /*
     * 取得根節點
     * @回傳 根節點
     */
    public Node getRoot() {
		return root;
	}
    
    /*
     * 求解
     * @參數 element: 單一資料行
     * @回傳 成功-結果字串 
     * 		失敗-null
     */
    public String findAnswer(Element element) {
    		Node finialNode = visitNode(root, element);
    		
    		// 中途找不到對應路徑
    		if (finialNode == null) {
    			return null;
    		} 
    		
    		return finialNode.name;
	}
    
    /*
     * 走訪節點
     * @參數 node: 節點
     * 		element: 資料行
     * @回傳 成功-葉子節點
     * 		失敗-null
     */
    private Node visitNode(Node node, Element element) {
    		// 沒有對應路徑
    		if (node == null) {
    			return null;
    		}
    	
    		// 達葉子節點
    		if (node.getChildrens().isEmpty()) {
    			return node;
    		}
    	
    		// 走訪下一個節點
    		Node nextNode = node.getOneChild(element.getAttributeData(node.name));
		return visitNode(nextNode, element);
	}
    
    /*
     * 計算正確率
     * @參數 testSet: 資料集
     * @回傳 正確率
     */
    public double calculateAccuracy(List<Element> testSet) {
    		int correct = 0;
    		for (Element e : testSet) {
    			if (findAnswer(e) != null && findAnswer(e).equals(e.getOutput())) {
    				correct++;
    			}
    		}
		return correct * 1.0 / testSet.size();
	}
}