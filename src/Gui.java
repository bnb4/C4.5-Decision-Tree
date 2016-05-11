import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Gui extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	// 視窗大小
	private int Width = 1500;
	private int Height = 700;
	
	// 資料表物件
	private DefaultTableModel trainingSetTalbeModel = new DefaultTableModel(); 
	private JTable trainingSetTable = new JTable(trainingSetTalbeModel); 	
	private DefaultTableModel testSetTalbeModel = new DefaultTableModel(); 
	private JTable testSetTable = new JTable(testSetTalbeModel); 
	private String[] columns;

	// 決策樹繪圖區物件
	private TreePanel treePanel = new TreePanel();
	
	// 訓練集與測試集資料行
	private static List<Element> trainingSet = new ArrayList<Element>();
	private static List<Element> testSet  = new ArrayList<Element>();
	
	// 決策樹
	private DecisionTree decisionTree;
	private JButton btnShowData= new JButton("顯示資料");
	
	// 測試集正確率
	private double accuracy = 0.0;
	private JLabel info = new JLabel("測試集正確率 :" + accuracy);
	
	/*
	 * 建構子
	 * 開新視窗
	 */
	public Gui() {
		super("C4.5-Decision-Tree");
		createWindow();
	}
	
	/*
	 * 建立視窗
	 */
	private void createWindow() {
		this.setSize(Width, Height);
		this.setPreferredSize(new Dimension(Width, Height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLayout(null);
		this.setBackground(null);	
		
		// 排版
		btnShowData.setBounds(10, 620, 100, 40);
	    info.setBounds(600, 620, 200, 40);
	    
	    // 監聽按鈕事件
	    btnShowData.addActionListener(this);

	    this.add(treePanel);
	    this.add(btnShowData);
	    this.add(info);
		
		this.pack();
		this.setVisible(true);
	}
	
	/*
	 * 將資料填入資料表
	 */
	private void buildTable(List<Element> elementList, 
							DefaultTableModel talbeModel,
							JTable table) {
		// 如果沒有資料
		if (columns == null) {
			return;
		}
		
		talbeModel = new DefaultTableModel();
		table.setModel(talbeModel);
		
		for (String column : columns) {
			talbeModel.addColumn(column); 
		}
		
		for (Element element : elementList) {
			Object[] tmp = new Object[columns.length];
			for (int i = 0; i < columns.length - 1; i++) {
				tmp[i] = element.getAttributeData(columns[i]);
			}
			tmp[columns.length - 1] = element.getOutput();
			talbeModel.addRow(tmp);
		}
	}
	
	/*
	 * 設定訓練集資料
	 */
	public void setTrainingSet(List<Element> trainingSet) {
		this.trainingSet = trainingSet;
		buildTable(trainingSet, trainingSetTalbeModel,trainingSetTable);
	}

	/*
	 * 設定測試集資料 
	 */
	public void setTestSet(List<Element> testSet) {
		this.testSet = testSet;
		buildTable(testSet, testSetTalbeModel, testSetTable);
	}
	
	
	/*
	 * 設定正確率
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
		info.setText("測試集正確率  : " + accuracy + " %");
		
	}
	
	/*
	 * 設定屬性
	 */
	public void setAttribute(String[] attributes) {
		columns = new String [attributes.length + 1];	
		for (int i = 0; i < attributes.length; i++) {
			columns[i] = attributes[i]; 
		}	
		columns[columns.length - 1] = "output";
		buildTable(trainingSet, trainingSetTalbeModel,trainingSetTable);
		buildTable(testSet, testSetTalbeModel, testSetTable);
	}
	
	/*
	 * 設定決策樹
	 */
	public void setTree(DecisionTree decisionTree) {
		this.decisionTree = decisionTree;
		treePanel.repaint();
	}
	
	// 決策樹繪圖區
	class TreePanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private int width = 1340;
		private int height = 600;
		private int rootTop = 10;
		private int circleSize = 50;
		private int nodeHeight = 50;
		
		/*
		 * 建構子
		 */
		TreePanel() {
			this.setBounds(10, 20, width, height);
			this.setLayout(null);
			this.setBackground(Color.white);
		}
		
		/*
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		protected void paintComponent(Graphics g) {		
			super.paintComponent(g);
			
			if (decisionTree == null) {
				return;
			}
			
			drawTree(g, decisionTree.getRoot(), null, null, 0, 0, width, rootTop);		
		}
		
		/*
		 * 繪製樹
		 */
		private void drawTree(Graphics g, 
							  DecisionTree.Node node, 
							  String value,
							  Point parentPoint, 
							  int index,
							  int left, 
							  int layerWidth, 
							  int top) {
			
			int spaceLeft = left + layerWidth * index;
			int circleLeft = spaceLeft + (layerWidth - circleSize) / 2;
			
			// 達葉子節點
			if (node.getChildrens().isEmpty()) {
				g.drawString(value, circleLeft, top-10);
				g.drawLine(parentPoint.x, parentPoint.y, circleLeft+circleSize/2, top);
				g.drawString(node.getName(), circleLeft, top+10);
				return;
			}
			
			// 根節點
			if (value != null) {
				// 繪製屬性值
				g.drawString(value, circleLeft, top-10);
				// 繪製線
				g.drawLine(parentPoint.x, parentPoint.y, circleLeft+circleSize/2, top);
			}
			
			// 繪製節點
			g.drawRoundRect(circleLeft, top, circleSize, circleSize, circleSize, circleSize);
			g.drawString(node.getName(), circleLeft, top+25);
			
			// 子節點
			Map<String, DecisionTree.Node> childrens = node.getChildrens();
			
			Point myPoint = new Point(circleLeft+25, top+circleSize);
			int childrensSize = childrens.size();
			int newtop = top + nodeHeight + circleSize;
			int newLayerWidth = layerWidth / childrensSize;

			int newIndex = 0;
			for (String key : childrens.keySet()){
				//繪製子節點
				drawTree(g, childrens.get(key), key, myPoint, newIndex, spaceLeft, newLayerWidth, newtop);
				newIndex++;	
			}
		}
	}
	
	/*
	 * 顯示資料視窗
	 */
	class DataWindow extends JFrame {
		// 視窗大小
		private int Width = 780;
		private int Height = 460;
		
		public DataWindow() {
			this.setSize(Width, Height);
			this.setPreferredSize(new Dimension(Width, Height));
			this.setLocationRelativeTo(null);
			this.setResizable(false);
			this.setLayout(null);
			this.setBackground(null);	
			
			// 訓練集資料表
			JScrollPane scrollPane1 = new JScrollPane(trainingSetTable);
		    scrollPane1.setBounds(10, 10, 350, 350);
		    JLabel trainingInfo = new JLabel("訓練集 : " + trainingSet.size() + "筆");
		    
		    // 測試集
		    JScrollPane scrollPane2 = new JScrollPane(testSetTable);
		    scrollPane2.setBounds(400, 10, 350, 350);
		    JLabel testInfo = new JLabel("測試集 : " + testSet.size() + "筆");
		    
		    // 排版
		    trainingInfo.setBounds(140, 350, 100, 100);
		    testInfo.setBounds(540, 350, 100, 100);
			
		    this.add(scrollPane1);
		    this.add(scrollPane2);
		    this.add(trainingInfo);
		    this.add(testInfo);
			
			this.pack();
			this.setVisible(true);
		}
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnShowData) {
			DataWindow dataWindow = new DataWindow();
		}
	}
}
