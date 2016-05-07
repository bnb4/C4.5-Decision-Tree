import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 將檔案 parse 並取出資料
 */
public class FileParser {
	
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