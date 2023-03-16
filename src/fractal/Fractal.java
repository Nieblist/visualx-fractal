package fractal;

import java.awt.image.BufferedImage;

public abstract class Fractal {	

	Scope viewScope;
	
	abstract public int escapeTime(double x, double y, int maxTime);
	
	public void setViewScope(Scope scope) {
		viewScope = scope;
	}
	
	public Scope getViewScope() {
		return viewScope;
	}
	
	public double viewToRelativeX(int x, int width, int height) {
		double xRange = Math.abs(viewScope.fX - viewScope.iX);
		
		return ((double) x/ (double) height) * xRange;
	}
	
	public double viewToRelativeY(int y, int width, int height) {
		double yRange = Math.abs(viewScope.fY - viewScope.iY);
		
		return ((double) y/ (double) height) * yRange;
	}
	
	public double viewToFractalX(int x, int width, int height) {
		double xRange = Math.abs(viewScope.fX - viewScope.iX);
		int r = (width - height) /2;
		
		return ((double) (x - r)/ (double) height) * xRange + viewScope.iX;
	}
	
	public double viewToFractalY(int y, int width, int height) {
		double yRange = Math.abs(viewScope.fY - viewScope.iY);
		
		return ((double) y/ (double) height) * yRange + viewScope.iY;
	}
	
	public BufferedImage draw(BufferedImage image, Palette palette, int maxTime) {
		for(int j = 0; j < image.getHeight(); j++) {
			for(int i = 0; i < image.getWidth(); i++) {
				double x, y;

				x = viewToFractalX(i, image.getWidth(), image.getHeight());
				y = viewToFractalY(j, image.getWidth(), image.getHeight());
				
				int escapeTime = escapeTime(x, y, maxTime);
				int color = 0;
				
				if (escapeTime != -1) {
					color = palette.getCyclicColor(escapeTime);
				}
				
				image.setRGB(i, j, color);
			}
		}
		
		return image;
	}
}
