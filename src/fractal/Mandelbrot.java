package fractal;

public class Mandelbrot extends Fractal {
	private static final double D_ESCAPE_RADIUS = 2.0;
	
	public int escapeTime(double x, double y, int maxTime) {
		int time = 0;
		double a = 0;
		double b = 0;
		double radius = 0;
		
		while (time <= maxTime && radius <= D_ESCAPE_RADIUS) {
			double oA = a;
			double oB = b;
			
			a = (oA*oA-oB*oB)+x;
			b = 2*oA*oB+y;
			radius = Math.sqrt(a*a+b*b);
			time++;
		}
		
		if (time > maxTime) {
			return -1;
		}
		
		return time;
	}
}
