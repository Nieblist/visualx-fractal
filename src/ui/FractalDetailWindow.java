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

public class FractalDetailWindow extends JFrame {

	public static final int I_DETAIL_MIN = 1;
	public static final int I_DETAIL_MAX = 10000;
	
	private static final long serialVersionUID = -594582942374400425L;
	
	private ArrayList< SelectorListener > listeners;
	private WindowAdapter windowAdapter;
	
	private JSpinner detailSpinner;
	
	private HashMap< String, Object > selectedValues;
	
	public FractalDetailWindow(int currentDetail) {
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
		
        //SPINNER
        SpinnerModel detailModel = new SpinnerNumberModel(currentDetail, I_DETAIL_MIN,  I_DETAIL_MAX, 10);
        
        detailSpinner = new JSpinner(detailModel);
        detailSpinner.setEditor(new JSpinner.NumberEditor(detailSpinner, "#"));
        
		JButton buttonAccept = new JButton();
		buttonAccept.setText("Accept");
		buttonAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	acceptValues();
            }
        }); 

		getContentPane().add(detailSpinner, BorderLayout.NORTH);
		getContentPane().add(buttonAccept, BorderLayout.SOUTH);
		
		setTitle("Fractal detail level");
		setResizable(false);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}

	protected void acceptValues() {
    	selectedValues.put(MainWindow.FIELD_DETAIL, detailSpinner.getValue());
    	for (SelectorListener l : listeners) {
    		l.valuesSelected(this, selectedValues);
    	}
    	dispose();
	}

	public void subscribe(SelectorListener listener) {
		listeners.add(listener);
	}
}



