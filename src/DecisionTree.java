import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 決策樹
 */
public class DecisionTree {
	
	private Node root = null;
	
	/*
	 * 建構子
	 */
	public DecisionTree(List<Element> trainingSet, String[] attribute, int numOfOutput) {
		Bag b = new Bag(null, null, trainingSet, attribute, numOfOutput);
		test(b, null);
	}
	
	private void test(Bag b, Node parent) {
		Bag[] b2 = b.classifyByMaxIG();
		
		if (b2 == null) {
			String dog = b.getElementList().get(0).getOutput();
			Node nnn = new Node(dog, parent);
			parent.addChildren(b.getName(), nnn);
			return;
		}
		
		String a = b2[0].getRootAttribute();
		Node n2 = new Node(a, parent);
		
		if (root == null) {
			root = n2;
		} else {
			parent.addChildren(b.getName(), n2);
		}
		
		for (Bag tmpb : b2) {
			test(tmpb, n2);
		}			
	}
	
	/*
	 * 節點物件
	 */
    private class Node {
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