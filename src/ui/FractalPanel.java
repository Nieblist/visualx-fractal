package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fractal.Fractal;
import fractal.Palette;
import fractal.Scope;

public class FractalPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 5719299850133183903L;
	
	private static final int I_SIZE_DEFAULT_W = 640;
	private static final int I_SIZE_DEFAULT_H = 480;
	private static final int I_DETAIL_DEFAULT = 300;

	private static final double D_X_START = -1.75;
	private static final double D_X_END   =  0.75;
	private static final double D_Y_START = -1.25;
	private static final double D_Y_END   =  1.25;
	
	public static final double D_ZOOM =  2.0;
	
	private BufferedImage screenImage;
	private BufferedImage fractalImage;
	
	private Fractal fractal;
	private int detailLevel = I_DETAIL_DEFAULT;
	
	private Palette palette;
	
	ArrayList< Scope > zoomHistory;
	boolean moving, zooming;
	
	Rectangle selectionRect;
	Point selectionStart, selectionEnd;
	Point clickStart, clickEnd;
	
    public FractalPanel(Fractal fractal, Palette palette) {
       this.fractal = fractal;
       this.palette = palette;
       
       screenImage  = new BufferedImage(I_SIZE_DEFAULT_W, I_SIZE_DEFAULT_H, BufferedImage.TYPE_INT_ARGB);
       fractalImage = new BufferedImage(I_SIZE_DEFAULT_W, I_SIZE_DEFAULT_H, BufferedImage.TYPE_INT_ARGB);
       
       selectionRect  = null;
       selectionStart = new Point();
       selectionEnd   = new Point();
       
       clickStart = new Point();
       clickEnd   = new Point();
       
       moving  = false;
       zooming = false;
       
       zoomHistory = new ArrayList< Scope >();
       
       zoomHistory.add(new Scope(D_X_START, D_X_END, D_Y_START, D_Y_END));
       
       setLayout(new BorderLayout(0, 0));
       
       KeyListener keyboardActions = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent ke) {
				int key = ke.getKeyCode();
				switch (key) {
					case KeyEvent.VK_ADD:
						zoomView(D_ZOOM);
					break;
					case KeyEvent.VK_SUBTRACT:
						zoomView(-D_ZOOM);
					break;
				}
				
			}
	
			@Override
			public void keyReleased(KeyEvent ke) { }
	
			@Override
			public void keyTyped(KeyEvent ke) { }
    	   
       };
       
       MouseAdapter mouseActions = new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent me) {
        	   clickStart = me.getPoint();
           }
    	   
           @Override
           public void mouseMoved(MouseEvent me) {
               clickStart = me.getPoint();
           }

           @Override
           public void mouseDragged(MouseEvent me) {
               clickEnd = me.getPoint();
               
               if (clickStart.x < clickEnd.x) {
            	   selectionStart.x = clickStart.x;
            	   selectionEnd  .x =   clickEnd.x;
               } else {
            	   selectionStart.x =   clickEnd.x;
            	   selectionEnd  .x = clickStart.x;
               }
               
               if (clickStart.y < clickEnd.y) {
            	   selectionStart.y = clickStart.y;
            	   selectionEnd  .y =   clickEnd.y;
               } else {
            	   selectionStart.y =   clickEnd.y;
            	   selectionEnd  .y = clickStart.y;
               }
               
               if (!zooming && SwingUtilities.isRightMouseButton(me)) {
            	   moving = true;
            	   repaint();
               }
               
        	   if (!moving && SwingUtilities.isLeftMouseButton(me)) {
        		   zooming = true;
                   
                   selectionRect = new Rectangle(selectionStart,
                           				   		 new Dimension(Math.abs(clickEnd.x-clickStart.x), Math.abs(clickEnd.y-clickStart.y)));
                   repaint();
    	       }
           }
           
           @Override
           public void mouseReleased(MouseEvent me) {
        	   if (zooming && SwingUtilities.isLeftMouseButton(me)) {
        		   selectionRect = null;
        		   setViewScope(selectionStart, selectionEnd);
        		   refreshView();
        		   zooming = false;
        	   } else if (moving && SwingUtilities.isRightMouseButton(me)) {
        		   moveViewScope(clickStart, clickEnd);
        		   refreshView();
        		   moving = false;
        	   }
           }
       };
       
       MouseWheelListener mouseWheelActions = new MouseWheelListener() {
    	      public void mouseWheelMoved(MouseWheelEvent we) {
    	    	  int notches = we.getWheelRotation();
    	          if (notches < 0) {
    	        	  zoomView(D_ZOOM, we.getX(), we.getY());
    	          } else {
    	        	  zoomView(-D_ZOOM, we.getX(), we.getY());
    	          }
    	      }
    	 };
       
       addKeyListener(keyboardActions);
       addMouseListener(mouseActions);
       addMouseMotionListener(mouseActions);
       addMouseWheelListener(mouseWheelActions);
       
       this.addComponentListener(this);
       
       setFocusable(true);
       requestFocusInWindow();
    }
    
    public void setFractal(Fractal fractal) {
    	this.fractal = fractal;
    	refreshView();
    }
    
	public BufferedImage getFractalImage() {
		return fractalImage;
	}
    
	public int getDetailLevel() {
		return detailLevel;
	}
	
	public void setDetailLevel(int level) {
		detailLevel = level;
		refreshView();
	}
    
    public void zoomView(double d) {
    	zoomView(d, screenImage.getWidth()/2, screenImage.getHeight()/2);
    }
    
    public void zoomView(double d, int screenX, int screenY) {
    	if (d < 0) {
    		d = 1/Math.abs(d);
    	}
    	
    	Scope s = new Scope(fractal.getViewScope());
    	
		double cX = fractal.viewToFractalX(screenX, screenImage.getWidth(), screenImage.getHeight());
		double cY = fractal.viewToFractalY(screenY, screenImage.getWidth(), screenImage.getHeight());
		
		s.iX = (s.iX - cX) / d + cX;
		s.fX = (s.fX - cX) / d + cX;
		s.iY = (s.iY - cY) / d + cY;
		s.fY = (s.fY - cY) / d + cY;
		
		zoomHistory.add(s);
		refreshView();
	}

	public void resetView() {
    	zoomHistory.add(new Scope(D_X_START, D_X_END, D_Y_START, D_Y_END));
    	refreshView();
	}

    public void previousView() {
    	if (zoomHistory.size() > 1) {
 		   zoomHistory.remove(zoomHistory.size() - 1);
 		   refreshView();
 	   }	
    }
    
	public void moveViewScope(Point start, Point end) {
	   Scope newScope = new Scope(fractal.getViewScope());
	   
	   int screenXChange = end.x - start.x;
	   int screenYChange = end.y - start.y;
	  
	   double fractalXChange = fractal.viewToRelativeX(screenXChange, screenImage.getWidth(), screenImage.getHeight());
	   double fractalYChange = fractal.viewToRelativeY(screenYChange, screenImage.getWidth(), screenImage.getHeight());
	  
	   newScope.iX -= fractalXChange;
	   newScope.fX -= fractalXChange;
	   newScope.iY -= fractalYChange;
	   newScope.fY -= fractalYChange;
	   
	   zoomHistory.add(newScope);
    }
    
    public void setViewScope(Point start, Point end) {
    	int xRange = end.x - start.x;
    	int yRange = end.y - start.y;
    	
    	if (xRange < 3 || yRange < 3) {
    		return;
    	}
    	
    	if (xRange > yRange) {
    		end.y = start.y + xRange;
    	} else {
    		end.x = start.x + yRange;
    	}
    	
    	double iX, fX, iY, fY;
    	
    	iX = fractal.viewToFractalX(selectionStart.x, screenImage.getWidth(), screenImage.getHeight());
 	   	fX = fractal.viewToFractalX(selectionEnd  .x, screenImage.getWidth(), screenImage.getHeight());
 	   	iY = fractal.viewToFractalY(selectionStart.y, screenImage.getWidth(), screenImage.getHeight());
 	   	fY = fractal.viewToFractalY(selectionEnd  .y, screenImage.getWidth(), screenImage.getHeight());
 	   	
 	   	zoomHistory.add(new Scope(iX, fX, iY, fY));
    }
    
    public void refreshView() {
    	screenImage  = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		fractalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		fractal.setViewScope(zoomHistory.get(zoomHistory.size() - 1));
		fractal.draw(fractalImage, palette, detailLevel);
		repaint();
    }
    
	public Palette getPalette() {
		return palette;
	}
    
	public void setPalette(Palette newPalette) {
		palette = newPalette;
		refreshView();
	}
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fractalImage.getWidth(), fractalImage.getHeight());
    }
    
	@Override
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);   
        Graphics2D sG = screenImage.createGraphics();
        sG.drawImage(fractalImage, 0, 0, null);
        if (zooming && selectionRect!=null) {
        	sG.setColor(Color.RED);
        	sG.draw(selectionRect);
        	sG.setColor(new Color(255,255,255,70));
        	sG.fill(selectionRect);
        } else if (moving) {
        	sG.setColor(Color.RED);
        	sG.drawLine(clickStart.x, clickStart.y, clickEnd.x, clickEnd.y);
        }
        
        sG.dispose();
        g.drawImage(screenImage, 0, 0, null);
    }

	@Override
	public void componentResized(ComponentEvent c) {
		if (getWidth() <= 0 || getHeight() <= 0) {
			return;
		}
		refreshView();
	}

	@Override
	public void componentHidden(ComponentEvent c) { }

	@Override
	public void componentMoved(ComponentEvent c) { }

	@Override
	public void componentShown(ComponentEvent c) { }

}