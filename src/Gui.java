import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gui extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private int Width = 800;
	private int Height = 600;
	
	private DefaultTableModel talbeModel = new DefaultTableModel(); 
	private JTable table = new JTable(talbeModel); 
	
	private String[] columns;
	private List<Element> elementList = new ArrayList<Element>();
	
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
}
