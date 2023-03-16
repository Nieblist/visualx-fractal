package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import fractal.JuliaSet;
import fractal.Mandelbrot;
import fractal.Palette;

public class MainWindow extends JFrame implements SelectorListener {

	private static final long serialVersionUID = 7926568576715024627L;

	private static final String S_APP_TITLE              = "Fractal Viewer";
	
	private static final String S_MENU_FILE		          = "File";
	private static final String S_MENU_FILE_SAVE          = "Save image...";
	private static final String S_MENU_FILE_EXIT          = "Exit";
	private static final String S_MENU_VIEW		          = "View";
	private static final String S_MENU_VIEW_ZOOMIN        = "Zoom In";
	private static final String S_MENU_VIEW_ZOOMOUT       = "Zoom Out";
	private static final String S_MENU_VIEW_RESET         = "Reset";
	private static final String S_MENU_VIEW_PREVIOUS      = "Previous";
	private static final String S_MENU_FRACTAL	          = "Fractal";
	private static final String S_MENU_FRACTAL_MANDELBROT = "Mandelbrot";
	private static final String S_MENU_FRACTAL_JULIASET   = "Julia set...";
	private static final String S_MENU_FRACTAL_DETAIL     = "Detail...";
	private static final String S_MENU_PALETTE	          = "Palette";
	private static final String S_MENU_PALETTE_GRAYSCALE  = "Grayscale";
	private static final String S_MENU_PALETTE_RAINBOW    = "Rainbow";
	private static final String S_MENU_PALETTE_HEAVEN     = "Heaven";
	private static final String S_MENU_PALETTE_GRAYRED    = "Red Contrast";
	private static final String S_MENU_PALETTE_CHANGE     = "Change...";
	private static final String S_MENU_HELP	 	          = "Help";
	private static final String S_MENU_HELP_ABOUT         = "About";
	private static final String S_MENU_HELP_ABOUT_TEXT    = "By Leonardo Fernández Esteberena\nFor UNCPBA - Visualización Computacional I\nFrom Argentina";

	public static final String FIELD_DETAIL  = "mainwindow.detail";
	public static final String FIELD_PALETTE = "mainwindow.palette";
	public static final String FIELD_JULIASET_C_R = "mainwindow.juliaSet.C.R";
	public static final String FIELD_JULIASET_C_I = "mainwindow.juliaSet.C.I";
	
	public static final Double JULIASET_DEFAULT_C_R = 0.35;
	public static final Double JULIASET_DEFAULT_C_I = 0.45;

	private FractalPanel fractalPanel;
	
	private double juliaCR = JULIASET_DEFAULT_C_R;
	private double juliaCI = JULIASET_DEFAULT_C_I;
	
	/*ACTIONS*/
	
	ActionListener saveImageFileAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter() {
				public boolean accept(File f) {
				  if (f.isDirectory()) {
					  return true;
				  }
				  
				  String s = f.getName();
				  return s.endsWith(".png")||s.endsWith(".PNG");
				}
				
				public String getDescription() {
					return "*.png,*.PNG";
				}
			});
			chooser.setSelectedFile(new File( "./fractal.png"));
			int actionDialog = chooser.showSaveDialog(MainWindow.this);
			if (actionDialog == JFileChooser.APPROVE_OPTION) {
				File sf = chooser.getSelectedFile();
		          
		        try {
					saveFractalImage(sf);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(new JFrame(), "Error saving image file", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};
	
	ActionListener exitFileAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	
	ActionListener previousViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fractalPanel.previousView();
	      }
	};
	
	ActionListener zoomInViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fractalPanel.zoomView(FractalPanel.D_ZOOM);
	      }
	};
	
	ActionListener zoomOutViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fractalPanel.zoomView(-FractalPanel.D_ZOOM);
	      }
	};
	
	ActionListener resetViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fractalPanel.resetView();
	      }
	};
	
	ActionListener mandelbrotFractalAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fractalPanel.setFractal(new Mandelbrot());
	      }
	};
	
	ActionListener juliaSetFractalAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectJuliaSet();
	      }
	};
	
	ActionListener detailFractalAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  changeFractalDetail();
	      }
	};
	
	ActionListener changePaletteAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  changePalette();
	      }
	};
	
	ActionListener grayscalePaletteAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  grayscalePalette();
	      }
	};
	
	ActionListener rainbowPaletteAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  rainbowPalette();
	      }
	};
	
	ActionListener heavenPaletteAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  heavenPalette();
	      }
	};
	
	ActionListener grayredPaletteAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  grayredPalette();
	      }
	};
	
	ActionListener aboutAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  showAboutDialog();
	      }
	};
	
	private void showAboutDialog() {
		JOptionPane.showMessageDialog(this,
				S_MENU_HELP_ABOUT_TEXT,
				S_MENU_HELP_ABOUT,
			    JOptionPane.DEFAULT_OPTION);
	}

	private void saveFractalImage(File file) throws IOException {
		ImageIO.write(fractalPanel.getFractalImage(), "jpg", file);
	}

	private void grayscalePalette() {
		Palette newPalette = new Palette();
		newPalette.addPercentage(0, 0);
		newPalette.addPercentage(50, 0xFFFFFF);
		
		newPalette.setCycleSpan(250);
		
		fractalPanel.setPalette(newPalette);
	}
	
	private void rainbowPalette() {
		Palette newPalette = new Palette();
		newPalette.addPercentage(40, 0xFF0000);
		newPalette.addPercentage(65, 0xFFFF00);
		newPalette.addPercentage(90, 0x00FF00);
		newPalette.addPercentage(15, 0x0000FF);
		
		newPalette.setCycleSpan(35);
		
		fractalPanel.setPalette(newPalette);
	}
	
	private void heavenPalette() {
		Palette newPalette = new Palette();
		
		newPalette.addPercentage(0, 0xFFFFFF);
		newPalette.addPercentage(10, 0xFFFFFF);
		newPalette.addPercentage(15, 0x51B6F5);
		newPalette.addPercentage(40, 0x51B6F5);
		newPalette.addPercentage(55, 0xFFFC30);
		newPalette.addPercentage(60, 0xC79C0E);
		newPalette.addPercentage(80, 0x9E7F02);
		newPalette.addPercentage(85, 0xFFFC30);
		newPalette.addPercentage(90, 0x000000);
		
		newPalette.setCycleSpan(100);
		
		fractalPanel.setPalette(newPalette);
	}
	
	private void grayredPalette() {
		Palette newPalette = new Palette();

		newPalette.addPercentage(0, 0xFFFFFF);
		newPalette.addPercentage(10, 0x000000);
		newPalette.addPercentage(20, 0x301010);
		newPalette.addPercentage(40, 0x000000);
		newPalette.addPercentage(60, 0x902020);
		newPalette.addPercentage(80, 0xEE0000);
		
		newPalette.setCycleSpan(350);
		
		fractalPanel.setPalette(newPalette);
	}
	
	private void changePalette() {
		setEnabled(false);
		PaletteWindow paletteWindow = new PaletteWindow(fractalPanel.getPalette());
		paletteWindow.subscribe(this);
		paletteWindow.requestFocus();
	}
	
	private void selectJuliaSet() {
		setEnabled(false);
		JuliaSetConstantWindow cSelectorWindow = new JuliaSetConstantWindow(juliaCR, juliaCI);
		cSelectorWindow.subscribe(this);
		cSelectorWindow.requestFocus();
	}
	
	private void changeFractalDetail() {
		setEnabled(false);
		FractalDetailWindow detailWindow = new FractalDetailWindow(fractalPanel.getDetailLevel());
		detailWindow.subscribe(this);
		detailWindow.requestFocus();
	}
	
	public MainWindow() {
		
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	
    	/* MENU BAR */
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);	
		
		//File
		JMenu fileMenu = new JMenu(S_MENU_FILE);
		menuBar.add(fileMenu);
			JMenuItem saveImageFileItem = new JMenuItem(S_MENU_FILE_SAVE);
			fileMenu.add(saveImageFileItem);
			saveImageFileItem.addActionListener(saveImageFileAction);

			fileMenu.addSeparator();
			
			JMenuItem exitFileItem = new JMenuItem(S_MENU_FILE_EXIT);
			fileMenu.add(exitFileItem);
			exitFileItem.addActionListener(exitFileAction);
			
		//View
		JMenu viewMenu = new JMenu(S_MENU_VIEW);
		menuBar.add(viewMenu);
			JMenuItem previousViewItem = new JMenuItem(S_MENU_VIEW_PREVIOUS);
			viewMenu.add(previousViewItem);
			previousViewItem.addActionListener(previousViewAction);
			previousViewItem.setAccelerator(KeyStroke.getKeyStroke("BACK_SPACE"));
		
			viewMenu.addSeparator();
		
			JMenuItem zoomInViewItem = new JMenuItem(S_MENU_VIEW_ZOOMIN);
			viewMenu.add(zoomInViewItem);
			zoomInViewItem.addActionListener(zoomInViewAction);
			zoomInViewItem.setAccelerator(KeyStroke.getKeyStroke("PLUS")); 

			JMenuItem zoomOutViewItem = new JMenuItem(S_MENU_VIEW_ZOOMOUT);
			viewMenu.add(zoomOutViewItem);
			zoomOutViewItem.addActionListener(zoomOutViewAction);
			zoomOutViewItem.setAccelerator(KeyStroke.getKeyStroke("MINUS")); 
			
			viewMenu.addSeparator();
			
			JMenuItem resetViewItem = new JMenuItem(S_MENU_VIEW_RESET);
			viewMenu.add(resetViewItem);
			resetViewItem.addActionListener(resetViewAction);
			resetViewItem.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
			
		//Fractal
			JMenu fractalMenu = new JMenu(S_MENU_FRACTAL);
			menuBar.add(fractalMenu);
				JMenuItem mandelbrotFractalItem = new JMenuItem(S_MENU_FRACTAL_MANDELBROT);
				fractalMenu.add(mandelbrotFractalItem);
				mandelbrotFractalItem.addActionListener(mandelbrotFractalAction);
				
				JMenuItem juliaSetFractalItem = new JMenuItem(S_MENU_FRACTAL_JULIASET);
				fractalMenu.add(juliaSetFractalItem);
				juliaSetFractalItem.addActionListener(juliaSetFractalAction);
				
				fractalMenu.addSeparator();
				
				JMenuItem detailFractalItem = new JMenuItem(S_MENU_FRACTAL_DETAIL);
				fractalMenu.add(detailFractalItem);
				detailFractalItem.addActionListener(detailFractalAction);
				
		//Palette
		JMenu paletteMenu = new JMenu(S_MENU_PALETTE);
		menuBar.add(paletteMenu);
			JMenuItem grayscalePaletteItem = new JMenuItem(S_MENU_PALETTE_GRAYSCALE);
			grayscalePaletteItem.addActionListener(grayscalePaletteAction);
			paletteMenu.add(grayscalePaletteItem);
		
			JMenuItem rainbowPaletteItem = new JMenuItem(S_MENU_PALETTE_RAINBOW);
			rainbowPaletteItem.addActionListener(rainbowPaletteAction);
			paletteMenu.add(rainbowPaletteItem);
			
			JMenuItem heavenPaletteItem = new JMenuItem(S_MENU_PALETTE_HEAVEN);
			heavenPaletteItem.addActionListener(heavenPaletteAction);
			paletteMenu.add(heavenPaletteItem);
		
			JMenuItem grayredPaletteItem = new JMenuItem(S_MENU_PALETTE_GRAYRED);
			grayredPaletteItem.addActionListener(grayredPaletteAction);
			paletteMenu.add(grayredPaletteItem);	
			
			JMenuItem changePaletteItem  = new JMenuItem(S_MENU_PALETTE_CHANGE);
			changePaletteItem.addActionListener(changePaletteAction);
			paletteMenu.add(changePaletteItem);
			
		//Help
		JMenu helpMenu = new JMenu(S_MENU_HELP);
		menuBar.add(helpMenu);
			
			JMenuItem aboutItem = new JMenuItem(S_MENU_HELP_ABOUT);
			aboutItem.addActionListener(aboutAction);
			helpMenu.add(aboutItem);
		
		/*TOOLBAR*/
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		JButton resetViewButton = new JButton(new ImageIcon(getClass().getResource("reset.png")));
		resetViewButton.setToolTipText("Reset view (scape)");
		resetViewButton.addActionListener(resetViewAction);
		toolBar.add(resetViewButton);
		
		toolBar.addSeparator();
		
		JButton zoomInViewButton = new JButton(new ImageIcon(getClass().getResource("in.png")));
		zoomInViewButton.setToolTipText("Zoom in (mouse wheel)");
		zoomInViewButton.addActionListener(zoomInViewAction);
		toolBar.add(zoomInViewButton);
		
		JButton zoomOutViewButton = new JButton(new ImageIcon(getClass().getResource("out.png")));
		zoomOutViewButton.setToolTipText("Zoom out (mouse wheel)");
		zoomOutViewButton.addActionListener(zoomOutViewAction);
		toolBar.add(zoomOutViewButton);
		
		JButton previousViewButton = new JButton(new ImageIcon(getClass().getResource("previous.png")));
		previousViewButton.setToolTipText("Previous view (backspace)");
		previousViewButton.addActionListener(previousViewAction);
		toolBar.add(previousViewButton);
	
		Palette newPalette = new Palette();
		
		newPalette.addPercentage(0, 0xFFFFFF);
		newPalette.addPercentage(10, 0x000000);
		newPalette.addPercentage(20, 0x301010);
		newPalette.addPercentage(40, 0x000000);
		newPalette.addPercentage(60, 0x902020);
		newPalette.addPercentage(80, 0xEE0000);
		
		newPalette.setCycleSpan(350);
		
		fractalPanel = new FractalPanel(new Mandelbrot(), newPalette);
		
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(fractalPanel, BorderLayout.CENTER);

		setTitle(S_APP_TITLE);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}

	@Override
	public void valuesSelected(Object selector, HashMap<String, Object> selectedValues) {
		Double juliaC = null;
		Double juliaI = null;
		for (Map.Entry<String, Object> cursor : selectedValues.entrySet()) {
			switch (cursor.getKey()) {
				case FIELD_DETAIL:
					fractalPanel.setDetailLevel((int) cursor.getValue());
				break;
				case FIELD_PALETTE:
					fractalPanel.setPalette((Palette) cursor.getValue());
				break;
				case FIELD_JULIASET_C_R:
					juliaC = (Double) cursor.getValue();
					juliaCR = juliaC;
				break;
				case FIELD_JULIASET_C_I:
					juliaI = (Double) cursor.getValue();
					juliaCI = juliaI;
				break;
			}
		}
		if (juliaC != null || juliaI != null) {
			fractalPanel.setFractal(new JuliaSet(juliaC, juliaI));
		}
		setEnabled(true);
		requestFocus();
	}

	@Override
	public void selectorClosed() {
		setEnabled(true);
		requestFocus();
	}

}



