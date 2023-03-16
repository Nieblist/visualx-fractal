package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import fractal.Palette;

public class PalettePanel extends JPanel{
	
	private static final long serialVersionUID = 2566457399354140075L;

	private static final int COLUMN_COLOR      = 0;
	private static final int COLUMN_PERCENTAGE = 1;
	
	private JTable colorsTable;
	private JScrollPane tableScrollPane;
	
	private int minValue, maxValue;
	
	private class ColorsTableSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -6497340558089385662L;

		public ColorsTableSelectionModel () {
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    }

	    @Override
	    public void clearSelection() {
	    }

	    @Override
	    public void removeSelectionInterval(int index0, int index1) {
	    }
	};
	
	private static class ColorTableCellEditor extends DefaultCellEditor implements TableCellEditor {

		private static final long serialVersionUID = -7696260883345565307L;
	    
	    protected static final Border red = new LineBorder(Color.red);
    
	    protected Object value;
	    protected JTextField textField;
	    
	    public ColorTableCellEditor(JTextField textField) {
	        super(textField);
	        this.textField = textField;
	        this.textField.setHorizontalAlignment(JTextField.RIGHT);
	    }

	    @Override
	    public Component getTableCellEditorComponent(JTable table, Object value,  
	    	      boolean isSelected, int row, int column)  
	    	  {  
	    	    this.value = value;    
	    	    Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);  
	    	    ((JComponent) c).setBorder(new LineBorder(Color.BLACK));  
	    	    return c;  
	    	  }
	}
	
	private class PercentageCellEditor extends ColorTableCellEditor implements TableCellEditor {
	    
		private static final long serialVersionUID = -4284072333201372405L;

		public PercentageCellEditor(JTextField textField) {
			super(textField);
		}

		@Override
		    public boolean stopCellEditing() {
		        try {
		            float v = Float.valueOf(textField.getText());
		            if (colorsTable.getSelectedRow() > 0) {
		            	if (v <= (float) colorsTable.getValueAt(colorsTable.getSelectedRow() - 1, COLUMN_PERCENTAGE)) {
		            		throw new NumberFormatException();
		            	}
		            } else {
		            	if (v < 0) {
		            		throw new NumberFormatException();
		            	}
		            }
		            if (colorsTable.getSelectedRow() < (colorsTable.getRowCount() - 1)) {
		            	if (v >= (float) colorsTable.getValueAt(colorsTable.getSelectedRow() + 1, COLUMN_PERCENTAGE)) {
		            		throw new NumberFormatException();
		            	}
		            } else {
		            	if (v >= 100) {
		            		throw new NumberFormatException();
		            	}
		            }
		            	
		        } catch (NumberFormatException e) {
		            textField.setBorder(red);
		            return false;
		        }
		        return super.stopCellEditing();
		    }
	    
	    public Object getCellEditorValue()  
	    {  
	      try  
	      {  
	        Float floatResult = Float.parseFloat(super.getCellEditorValue().toString());  
	        return floatResult;  
	      }  
	      catch (NumberFormatException e)  
	      {  
	        JOptionPane.showMessageDialog(null, "Percentage value is invalid");  
	        return value;
	      }  
	    }
	}
	
	public PalettePanel(Palette palette) {
		minValue = 0;
		maxValue = 255;
		
		colorsTable = new JTable() {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		            Component c = super.prepareRenderer(renderer, row, column);
		            
		            if (column == 0) {
		            		c.setBackground(new Color((int)colorsTable.getValueAt(row, column)));
		            		c.setForeground(new Color((int)colorsTable.getValueAt(row, column)));
		            } else {
		            	if (colorsTable.getSelectedRow() == row) {
		            		c.setBackground(colorsTable.getSelectionBackground());
		            		c.setForeground(colorsTable.getSelectionForeground());
		            	} else {
		            		c.setBackground(colorsTable.getBackground());
		            		c.setForeground(colorsTable.getForeground());
		            	}
		            }
		            return c;
		        }
		};
        colorsTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        colorsTable.getTableHeader().setReorderingAllowed(false);
		tableScrollPane = new JScrollPane(colorsTable);
		
		TableModel ColorTableModel = new DefaultTableModel(
	            new Object [][] { },
	            new String [] { "Color", "Percentage" }
	        ) {
				private static final long serialVersionUID = -4545701455494036509L;
				
				Class<?>[] types = new Class [] {
	                java.lang.Integer.class, java.lang.Float.class, java.lang.Integer.class
	            };
				
	            boolean[] canEdit = new boolean [] {
	                false, true, true
	            };

	            public Class<?> getColumnClass(int columnIndex) {
	                return types [columnIndex];
	            }

	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit[columnIndex];
	            }
	        };
		
		colorsTable.setModel(ColorTableModel);
		
		colorsTable.getColumnModel().getColumn(COLUMN_PERCENTAGE).setCellEditor(new PercentageCellEditor(new JTextField()));
		((DefaultCellEditor) (colorsTable.getColumnModel().getColumn(COLUMN_PERCENTAGE).getCellEditor())).setClickCountToStart(1);
		colorsTable.setSelectionModel(new ColorsTableSelectionModel());
		
		DefaultTableModel model = (DefaultTableModel) colorsTable.getModel();
		
		int heightRange = maxValue; 
	
		for (int i = 0; i < palette.size(); i++){
			model.addRow(new Object[]{palette.getColorByIndex(i), palette.getPercentage(i), (int) (palette.getPercentage(i) * heightRange)/100 + minValue});	
		}

	    colorsTable.getModel().addTableModelListener(new TableModelListener() {
	        public void tableChanged(TableModelEvent e) {

	        }
	      });
		
		tableScrollPane.setViewportView(colorsTable);
		
		JButton buttonAddColor = new JButton();
		buttonAddColor.setText("Add");
		buttonAddColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	float percentage = 0;
            	if (colorsTable.getSelectedRow() == -1 || colorsTable.getSelectedRow() == colorsTable.getRowCount()) {
            		percentage = (float) colorsTable.getValueAt(colorsTable.getRowCount() - 1, COLUMN_PERCENTAGE);
            		percentage += (float) colorsTable.getValueAt(colorsTable.getRowCount() - 2, COLUMN_PERCENTAGE);
            	} else {
            		percentage = (float) colorsTable.getValueAt(colorsTable.getSelectedRow(), COLUMN_PERCENTAGE);
            		percentage += (float) colorsTable.getValueAt(colorsTable.getSelectedRow() + 1, COLUMN_PERCENTAGE);
            	}
            	percentage /= 2;
            	
            	addNewRow(percentage);
            }
        });
		
		JButton buttonChangeColor = new JButton();
		buttonChangeColor.setText("Change");
		buttonChangeColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	if (colorsTable.getSelectedRow() == -1) {
            		return;
            	}
        		Integer color = selectColor();
        		if (color == null) {
        			return;
        		}
            	colorsTable.setValueAt(color, colorsTable.getSelectedRow(), COLUMN_COLOR);
            }
        });
		
		JButton buttonDeleteColor = new JButton();
		buttonDeleteColor.setText("Delete");
		buttonDeleteColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	if (colorsTable.getSelectedRow() == -1) {
            		return;
            	}
            	((DefaultTableModel) colorsTable.getModel()).removeRow(colorsTable.getSelectedRow());
            }
        });
		
		JButton buttonUpColor = new JButton();
		buttonUpColor.setText("Up");
		buttonUpColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	if (colorsTable.getSelectedRow() == -1) {
            		return;
            	}
            	int cellAbove;
            	if (colorsTable.getSelectedRow() != 0) {
            		cellAbove = colorsTable.getSelectedRow() - 1;
            	} else {
            		cellAbove = colorsTable.getRowCount() - 1;
            	}
            	int colorAbove = (int) colorsTable.getValueAt(cellAbove, COLUMN_COLOR);
            	int thisColor  = (int) colorsTable.getValueAt(colorsTable.getSelectedRow(), COLUMN_COLOR); 
            	colorsTable.setValueAt(thisColor, cellAbove, COLUMN_COLOR);
            	colorsTable.setValueAt(colorAbove, colorsTable.getSelectedRow(), COLUMN_COLOR);
            	colorsTable.setRowSelectionInterval(cellAbove, cellAbove);
            }
        });
		
		JButton buttonDownColor = new JButton();
		buttonDownColor.setText("Down");
		buttonDownColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	if (colorsTable.getSelectedRow() == -1) {
            		return;
            	}
            	int cellBelow;
            	if (colorsTable.getSelectedRow() != colorsTable.getRowCount() - 1) {
            		cellBelow = colorsTable.getSelectedRow() + 1;
            	} else {
            		cellBelow = 0;
            	}
            	int colorBelow = (int) colorsTable.getValueAt(cellBelow, COLUMN_COLOR);
            	int thisColor  = (int) colorsTable.getValueAt(colorsTable.getSelectedRow(), COLUMN_COLOR); 
            	colorsTable.setValueAt(thisColor, cellBelow, COLUMN_COLOR);
            	colorsTable.setValueAt(colorBelow, colorsTable.getSelectedRow(), COLUMN_COLOR);
            	colorsTable.setRowSelectionInterval(cellBelow, cellBelow);
            }
        });

		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		GridBagConstraints gbc_tableScrollPane = new GridBagConstraints();
		gbc_tableScrollPane.gridx = 0;
		gbc_tableScrollPane.gridy = 0;
		gbc_tableScrollPane.gridwidth = 6;
		gbc_tableScrollPane.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_addButton = new GridBagConstraints();
		gbc_addButton.gridx = 0;
		gbc_addButton.gridy = 1;
		gbc_addButton.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_ChangeButton = new GridBagConstraints();
		gbc_ChangeButton.gridx = 1;
		gbc_ChangeButton.gridy = 1;
		gbc_ChangeButton.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_DeleteButton = new GridBagConstraints();
		gbc_DeleteButton.gridx = 2;
		gbc_DeleteButton.gridy = 1;
		gbc_DeleteButton.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_UpButton = new GridBagConstraints();
		gbc_UpButton.gridx = 3;
		gbc_UpButton.gridy = 1;
		gbc_UpButton.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_DownButton = new GridBagConstraints();
		gbc_DownButton.gridx = 4;
		gbc_DownButton.gridy = 1;
		gbc_DownButton.fill = GridBagConstraints.BOTH;
		
		add(tableScrollPane, gbc_tableScrollPane);
		add(buttonAddColor, gbc_addButton);
		add(buttonChangeColor, gbc_ChangeButton);
		add(buttonDeleteColor, gbc_DeleteButton);
		add(buttonUpColor, gbc_UpButton);
		add(buttonDownColor, gbc_DownButton);
		
	}

	private Integer selectColor() {
		Color color = JColorChooser.showDialog(this, "Select a color", new Color(0));
		if (color == null) {
			return null;
		}
		return color.getRGB();
	}
	
	private void addNewRow(float percentage) {
		int heightRange = maxValue - minValue;
		int absoluteValue = (int) (percentage * heightRange)/100 + minValue;
		int i = 0;
		while (i < colorsTable.getRowCount() && (float) colorsTable.getValueAt(i, COLUMN_PERCENTAGE) < percentage) {
			i++;
		}
		Integer color = selectColor();
		if (color == null) {
			return;
		}
		((DefaultTableModel) colorsTable.getModel()).insertRow(i, new Object[]{color, percentage, absoluteValue});
	}
	
	public Palette generatePalette() {
		Palette pal = new Palette();
		for(int i = 0; i < colorsTable.getRowCount(); i++) {
			pal.addPercentage((float) colorsTable.getValueAt(i, COLUMN_PERCENTAGE), (int) colorsTable.getValueAt(i, COLUMN_COLOR));
		}
		return pal;
	}
	
}