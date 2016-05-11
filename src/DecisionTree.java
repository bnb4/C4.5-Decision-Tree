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
	
	private void buildTree(Bag bag, Node parent) {
		Bag[] classifiedBags = bag.classifyByMaxIG();
		
		// 分支終點
		if (classifiedBags == null) {
			String ouputString = bag.getElementList().get(0).getOutput();
			Node ouputNode = new Node(ouputString, parent);
			parent.addChildren(bag.getName(), ouputNode);
			return;
		}
		
		String a = classifiedBags[0].getRootAttribute();
		Node n2 = new Node(a, parent);
		
		if (root == null) {
			root = n2;
		} else {
			parent.addChildren(bag.getName(), n2);
		}
		
		for (Bag tmpb : classifiedBags) {
			buildTree(tmpb, n2);
		}			
	}
	
	/*
	 * 節點物件
	 */
    class Node {
        private String name;
        private Node parent;
        private Map<String, Node> childrens = new HashMap<String, Node>();
        
        public Node(String name, Node parent) {
        		this.name = name;
        		this.parent = parent;
        }
        
        public void addChildren(String s, Node n) {
        		childrens.put(s, n);
        	
        }
        
        public String getName() {
			return name;
		}
        
        public Node getParent() {
			return parent;
		}
        
        public Map<String, Node> getChildrens() {
			return childrens;
		}
        
    }
    
    public Node getRoot() {
		return root;
	}
    
    public String findAnswer(Element element) {
    		// TODO
    		return null;
	}
    
    public double calculateAccuracy(List<Element> testSet) {
    		int correct = 0;
    		for (Element e : testSet) {
    			if (findAnswer(e).equals(e.getOutput())) {
    				correct++;
    			}
    		}
		return correct * 1.0 / testSet.size();
	}
}