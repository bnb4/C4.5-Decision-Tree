import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		// 取得測試資料
		FileParser filePaser = new FileParser("Data.txt");
		List<Element> o = filePaser.getDatas();
		
		// 新增一個Bag
		Bag bag = new Bag("root", filePaser.getAttributes());
		// 把資料讀入Bag
		for (Element e : o) {
			bag.addElement(e);
		}
		
		// 分割
		Bag[] bags = bag.splitBagByMinEntropy();
		
		// 印出測試
		System.out.println(bag);
		
		System.out.println("getMaxEntropyAttribute() : " + bag.getMaxEntropyAttribute());
		
		System.out.println("分割後的包:");
		for (Bag b : bags) {
			System.out.println("------");
			System.out.println(b);
		}
		
		// 註: 亂度(0~1)越大的Attribute優先分割，越接近 1 代表該Attribute與CLASS結果關聯越強烈
		// 收斂條件: 若Attribute亂度為0(該Attribute差異與CLASS結果無關) 或亂度極小(該Attribute影響CLASS分類能力極小) 那該分支就可以停止分割了
	}

}
