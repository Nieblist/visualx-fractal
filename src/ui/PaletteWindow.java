package ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fractal.Palette;

public class PaletteWindow extends JFrame {
	
	private static final long serialVersionUID = -6144150719887329483L;
	
	public static final int I_CYCLE_MIN = 1;
	public static final int I_CYCLE_MAX = 10000;
	
	private ArrayList< SelectorListener > listeners;
	private WindowAdapter windowAdapter;
	
	private PalettePanel palettePanel;
	private JSpinner cycleSpinner;
	
	private Palette palette;
	private HashMap< String, Object > selectedValues;
	
	public PaletteWindow(Palette pal) {
		palette = pal;
		listeners = new ArrayList< SelectorListener >();
		selectedValues = new HashMap< String, Object >();
		
		windowAdapter = new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
            	for (SelectorListener l : listeners) {
            		l.selectorClosed();
            	}            	
            	dispose();
            }
        };
        addWindowListener(windowAdapter);
		
        //PALETTE PANEL
		palettePanel = new PalettePanel(palette);
		
		JButton buttonAccept = new JButton();
		buttonAccept.setText("Accept");
		buttonAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                acceptValues();
            }
        }); 

        //SPINNER
        SpinnerModel cycleModel = new SpinnerNumberModel(palette.getCycleSpan(), I_CYCLE_MIN,  I_CYCLE_MAX, 10);
        
        cycleSpinner = new JSpinner(cycleModel);
        cycleSpinner.setEditor(new JSpinner.NumberEditor(cycleSpinner, "#"));

        JLabel cycleLabel = new JLabel("Cycle span: ");
		cycleLabel.setLabelFor(cycleSpinner);
        
		GridBagLayout gridBagLayout = new GridBagLayout();
		getContentPane().setLayout(gridBagLayout);

		GridBagConstraints gbc_palettePanel = new GridBagConstraints();
		gbc_palettePanel.gridx = 0;
		gbc_palettePanel.gridy = 0;
		gbc_palettePanel.gridwidth = 2;
		gbc_palettePanel.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_cycleLabel = new GridBagConstraints();
		gbc_cycleLabel.gridx = 0;
		gbc_cycleLabel.gridy = 1;
		gbc_cycleLabel.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_cycleSpinner = new GridBagConstraints();
		gbc_cycleSpinner.gridx = 1;
		gbc_cycleSpinner.gridy = 1;
		gbc_cycleSpinner.fill = GridBagConstraints.BOTH;
		
		GridBagConstraints gbc_AcceptButton = new GridBagConstraints();
		gbc_AcceptButton.gridx = 0;
		gbc_AcceptButton.gridy = 2;
		gbc_AcceptButton.gridwidth = 2;
		gbc_AcceptButton.fill = GridBagConstraints.BOTH;
		
		
		
		getContentPane().add(palettePanel, gbc_palettePanel);
		getContentPane().add(cycleLabel, gbc_cycleLabel);
		getContentPane().add(cycleSpinner, gbc_cycleSpinner);
		getContentPane().add(buttonAccept, gbc_AcceptButton);
		
		setTitle("Change palette");
		setResizable(false);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}

	protected void acceptValues() {
		Palette newPalette = palettePanel.generatePalette();
		newPalette.setCycleSpan((int) cycleSpinner.getValue());
    	selectedValues.put(MainWindow.FIELD_PALETTE, newPalette);
    	for (SelectorListener l : listeners) {
    		l.valuesSelected(this, selectedValues);
    	}
    	dispose();
	}
	
	public void subscribe(SelectorListener listener) {
		listeners.add(listener);
	}
}



