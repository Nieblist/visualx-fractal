package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class JuliaSetConstantWindow extends JFrame {
	private static final long serialVersionUID = -594582942374400425L;
	
	private static final double D_C_MIN = -3.0;
	private static final double D_C_MAX = 3.0;
	
	private ArrayList< SelectorListener > listeners;
	private WindowAdapter windowAdapter;
	
	private JSpinner rSpinner;
	private JSpinner iSpinner;
	
	private HashMap< String, Object > selectedValues;
	
	public JuliaSetConstantWindow(double currentR, double currentI) {
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
		
        //SPINNERS
        SpinnerModel rModel = new SpinnerNumberModel(currentR, D_C_MIN,  D_C_MAX, 0.1);
        SpinnerModel iModel = new SpinnerNumberModel(currentI, D_C_MIN,  D_C_MAX, 0.1);
        
        rSpinner = new JSpinner(rModel);
        iSpinner = new JSpinner(iModel);
        
		JButton buttonAccept = new JButton();
		buttonAccept.setText("Accept");
		buttonAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	acceptValues();
            }
        }); 

		getContentPane().add(rSpinner, BorderLayout.NORTH);
		getContentPane().add(iSpinner, BorderLayout.CENTER);
		getContentPane().add(buttonAccept, BorderLayout.SOUTH);
		
		setTitle("Julia Set");
		setResizable(false);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}

	protected void acceptValues() {
    	selectedValues.put(MainWindow.FIELD_JULIASET_C_R, rSpinner.getValue());
    	selectedValues.put(MainWindow.FIELD_JULIASET_C_I, iSpinner.getValue());
    	for (SelectorListener l : listeners) {
    		l.valuesSelected(this, selectedValues);
    	}
    	dispose();
	}

	public void subscribe(SelectorListener listener) {
		listeners.add(listener);
	}
}



