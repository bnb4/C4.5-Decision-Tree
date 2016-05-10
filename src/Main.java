import java.util.ArrayList;
import java.util.List;

public class Main {

	private final static double PERCENT = 0.75;
	private final static double MINACCURACY = 0.6;
	
	private static List<Element> trainingSet = new ArrayList<Element>();
	private static List<Element> testSet  = new ArrayList<Element>();
	
	public static void main(String[] args) {
		
		double accuracy = 0;
		do {
			FileParser filePaser = new FileParser("Data.txt");
			List<Element> elementList = filePaser.getElementList();
			
			assignElements(elementList);
			
			DecisionTree decisionTree = new DecisionTree(trainingSet, 
														filePaser.getAttributes(), 
														filePaser.getNumberOfOutput());
			
			accuracy = decisionTree.calculateAccuracy(testSet);
		} while (accuracy < MINACCURACY);		
		
	}
	
	private static void assignElements(List<Element> elementList) {
		int numOfTraining = (int)(elementList.size() * PERCENT);
		for (int i = 0; i < numOfTraining; i++) {
			int randomNum = (int)(Math.random() * (elementList.size() - 1));
			trainingSet.add(elementList.get(randomNum));
			elementList.remove(randomNum);
		}
		testSet = elementList;
	}
}
