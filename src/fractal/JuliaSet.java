package fractal;

public class JuliaSet extends Fractal {
	private static final double D_ESCAPE_RADIUS = 2.0;
	
	private double cR;
	private double cI;
	
	public JuliaSet(double r, double i) {
		cR = r;
		cI = i;
	}
	
	public void setC(double r, double i) {
		cR = r;
		cI = i;
	}
	
	public int escapeTime(double x, double y, int maxTime) {
		int time = 0;
		double radius = Math.sqrt(x*x+y*y);
		
		while (time <= maxTime && radius <= D_ESCAPE_RADIUS) {
			double oX = x;
			double oY = y;
			
			x = (oX*oX-oY*oY) + cR;
			y =      2*oX*oY  + cI;
			radius = Math.sqrt(x*x+y*y);
			time++;
		}
		
		if (time > maxTime) {
			return -1;
		}
		
		return time;
	}
}
