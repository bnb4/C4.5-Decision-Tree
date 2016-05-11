import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Gui extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private int Width = 1200;
	private int Height = 600;
	
	private DefaultTableModel talbeModel = new DefaultTableModel(); 
	private JTable table = new JTable(talbeModel); 

	private TreePanel treePanel = new TreePanel();
	
	private String[] columns;
	private List<Element> elementList = new ArrayList<Element>();
	private DecisionTree decisionTree;
	
	public Gui() {
		super("C4.5-Decision-Tree");
		createWindow();

	}
	
	private void createWindow() {
		this.setSize(Width, Height);
		this.setPreferredSize(new Dimension(Width,Height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLayout(null);
		this.setBackground(null);
		
	    buildTable();
	    
	    JScrollPane scrollPane = new JScrollPane(table);
	    scrollPane.setBounds(30, 20, 300, 500);
	    
	    this.add(scrollPane);
	    this.add(treePanel);
		
		this.pack();
		this.setVisible(true);
	}
	
	private void buildTable() {
		if (columns == null) {
			return;
		}
		
		talbeModel = new DefaultTableModel();
		table.setModel(talbeModel);
		
		for (String a : columns) {
			talbeModel.addColumn(a); 
		}
		
		for (Element e : elementList) {
			Object[] tmp = new Object [columns.length];
			for (int i = 0; i < columns.length - 1; i++) {
				tmp[i] = e.getAttributeData(columns[i]);
			}
			tmp[columns.length - 1] = e.getOutput();
			talbeModel.addRow(tmp);
		}
	}
	
	public void setElementData(List<Element> elementList) {
		this.elementList = elementList;
		buildTable();
	}
	
	public void setAttribute(String[] attributes) {
		
		columns = new String [attributes.length + 1];
		
		for (int i = 0; i < attributes.length; i++) {
			columns[i] = attributes[i]; 
		}
		
		columns[columns.length - 1] = "output";

		buildTable();
	}
	
	public void setTree(DecisionTree decisionTree) {
		this.decisionTree = decisionTree;
		treePanel.repaint();
	}
	
	class TreePanel extends JPanel {
		private int width = 800;
		private int height = 450;

		private int circleSize = 50;
		
		private int rootLeft = width / 2;
		private int rootTop = 10;
		
		private int StringLeft = 14;
		private int StringTop = 25;
		
		private int nodeHeight = 50;
		
		TreePanel() {
			this.setBounds(360, 20, width, height);
			this.setLayout(null);
			this.setBackground(Color.white);
		}
		
		protected void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			if (decisionTree == null) {
				return;
			}
			
			
			drawTree(g, decisionTree.getRoot(), 0, width, rootTop);
			
		}
		
		private void drawTree(Graphics g, DecisionTree.Node node, int index, int layerWidth, int top) {
			
			int CircleLeft = (layerWidth * index) + (layerWidth - circleSize) / 2;
			
			if (node.getChildrens().isEmpty()) {
				g.drawString(node.getName(), CircleLeft, top);
				return;
			}
			
			
			g.drawRoundRect(CircleLeft , top, circleSize, circleSize, circleSize, circleSize);
			g.drawString(node.getName(), CircleLeft, top);
			Map<String, DecisionTree.Node> childrens = node.getChildrens();
			int childrensSize = childrens.size();
			
			int newtop = top + nodeHeight + circleSize;
			int newLayerWidth = width / childrensSize;
			

			int newIndex = 0;
			for (String k : childrens.keySet()){
			
				drawTree(g, childrens.get(k), newIndex, newLayerWidth, newtop);
				newIndex ++;/*
				System.out.println(newIndex);
				System.out.println(childrens.get(k).getName());
				

				CircleLeft = (newLayerWidth * newIndex) + (newLayerWidth - circleSize) / 2;
				System.out.println(CircleLeft);
				g.drawRoundRect(CircleLeft , newtop, circleSize, circleSize, circleSize, circleSize);
				g.drawString(node.getName(), CircleLeft, newtop);
				newIndex++;*/
			}
			
		
		}
	}
}
