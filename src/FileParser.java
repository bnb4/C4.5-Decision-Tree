import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 將檔案 parse 並取出資料
 */
public class FileParser {
	
	// 屬性名稱陣列
	private String[] attributes;
	// 所有資料行
	private List<Element> elementList = new ArrayList<Element>();
	// 結果集合
	private Set<String> outputsSet = new HashSet<String>();
	
	/*
	 * 建構子
	 * 讀取檔案並轉為需要格式
	 * @參數 fileName: 檔案路徑
	 */
	public FileParser(String fileName) {
		try (Scanner scanner = new Scanner(new File(fileName))) {
			// 讀取第一行(屬性名稱)
			if (scanner.hasNext()) {
				loadAttribute(scanner.nextLine());
			}
			// 讀取資料行
			while (scanner.hasNext()) {
				addData(scanner.nextLine());
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	
	/*
	 * 儲存屬性名稱
	 * (不包含"Output"欄名稱)
	 * @參數 attStr 屬性行字串
	 */
	private void loadAttribute(String attStr) {
		String[] raw = attStr.trim().split("\t");
		attributes = Arrays.copyOfRange(raw, 0, raw.length - 1);
	}
	
	/*
	 * 添加資料行至elementList
	 * @參數 dataStr 資料行字串
	 */
	private void addData(String dataStr) {
		// 處理空行
		if (dataStr == null || dataStr.equals("")) {
			return;
		}

		// Tab為切割字元
		String[] dataArray = dataStr.split("\t");
		
		// 資料欄位數量不正確
		if (dataArray.length != getAttributesLength() + 1) {
			return;
		}
		
		// 加入資料
		elementList.add(new Element(getAttributes(),
							Arrays.copyOfRange(dataArray, 0, getAttributesLength()),
							dataArray[getAttributesLength()])
					   );
		
		// 將結果加入結果集合
		outputsSet.add(dataArray[getAttributesLength()]);
	}
	
	/*
	 * 取得 Attributes 數量
	 * @回傳 屬性數量
	 */
	public int getAttributesLength() {
		if (attributes == null) {
			return 0; 
		}
		return attributes.length;
	}
	
	/*
	 * 取得屬性名稱陣列
	 * @回傳 屬性名稱陣列
	 */
	public String[] getAttributes() {
		return attributes;
	}
	
	/*
	 * 取得所有資料行
	 * @回傳 所有資料行
	 */
	public List<Element> getElementList() {
		return new ArrayList<Element>(elementList);
	}
	
	/*
	 * 取得結果的種類數
	 * @回傳 結果的種類數
	 */
	public int getNumberOfOutput() {
		return outputsSet.size();
	}
}