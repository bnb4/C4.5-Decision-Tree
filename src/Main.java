import java.util.ArrayList;
import java.util.List;

public class Main {

	// 定義測試集佔整體資料的比例
	private final static double PERCENT = 0.25;
	// 定義測試集正確率門檻
	private final static double MINACCURACY = 0.6;
	
	// 訓練集與測試集資料行
	private static List<Element> trainingSet = new ArrayList<Element>();
	private static List<Element> testSet  = new ArrayList<Element>();
	
	public static void main(String[] args) {
		
		Gui gui = new Gui();
		
		double accuracy = 0;		// 測試集正確率
		do {
			// 讀取所有資料行
			FileParser filePaser = new FileParser("Data.txt");
			List<Element> elementList = filePaser.getElementList();
			
			gui.setAttribute(filePaser.getAttributes());
			gui.setElementData(elementList);
			
			// 將資料行分成測試集與訓練集
			assignElements(elementList);
			
			// 利用訓練集建立決策樹
			DecisionTree decisionTree = new DecisionTree(trainingSet, 
														filePaser.getAttributes(), 
														filePaser.getNumberOfOutput());
			
			gui.setTree(decisionTree);
			// 試驗測試集正確率
			accuracy = decisionTree.calculateAccuracy(testSet);
		} while (accuracy < MINACCURACY);	// 如果測試集正確率低於設定門檻則重做
		
	}
	
	/*
	 * 將資料行分成測試集與訓練集
	 * @參數 elementList: 欲分類資料行
	 */
	private static void assignElements(List<Element> elementList) {
		// 計算測試集所需資料筆數
		int numOfTest = (int)(elementList.size() * PERCENT);
		
		// 隨機挑選資料行進測試集
		for (int i = 0; i < numOfTest; i++) {
			int randomNum = (int)(Math.random() * (elementList.size() - 1));
			testSet.add(elementList.get(randomNum));
			elementList.remove(randomNum);
		}
		
		// 剩餘的資料為訓練集
		trainingSet = elementList;
	}
}