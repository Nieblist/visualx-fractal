package fractal;

public class Scope {
	public double iX, fX, iY, fY;
	
	public Scope(double iX, double fX, double iY, double fY) {
		this.iX = iX;
		this.fX = fX;
		this.iY = iY;
		this.fY = fY;
	}
	
	public Scope(Scope anotherScope) {
		this.iX = anotherScope.iX;
		this.fX = anotherScope.fX;
		this.iY = anotherScope.iY;
		this.fY = anotherScope.fY;
	}
}
