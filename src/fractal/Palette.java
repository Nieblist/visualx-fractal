package fractal;

import java.util.ArrayList;

public class Palette {
	
	private static final int I_CYCLESPAN_DEFAULT = 400;
	
	private ArrayList<Integer> colors;
	private ArrayList<Float> percentages;
	
	private int cycleSpan = I_CYCLESPAN_DEFAULT;
	
	public Palette() {
		colors = new ArrayList<Integer>();
		percentages = new ArrayList<Float>();
	}
	
	public Palette(Palette anotherPal) {
		colors = new ArrayList<Integer>();
		percentages = new ArrayList<Float>();
		for (int i = 0; i < anotherPal.size(); i++) {
			addPercentage(anotherPal.getPercentage(i), anotherPal.getColorByIndex(i));
		}
	}

	public int addPercentage(float percentage, int color) {
		int i = 0;
		while (i < percentages.size() && percentages.get(i) < percentage) {
			i++;
		}
		percentages.add(i, percentage);
		colors.add(i, color);
		return percentages.size();
	}
	
	public float getPercentage(int index) {
		return percentages.get(index);
	}
	
	public void setPercentage(int index, float percentage) {
		percentages.set(index, percentage);
	}
	
	public Integer getColorByIndex(int index) {
		return colors.get(index);
	}

	public void setColor(int index, int color) {
		colors.set(index, color);
	}
	
	public int getCycleSpan() {
		return cycleSpan;
	}
	
	public void setCycleSpan(int span) {
		cycleSpan = span;
	}
	
	public int size() {
		return percentages.size();
	}
	
	public int getCyclicColor(int value) {
		return getCyclicColor(0, cycleSpan, value);
	}
	
	public int getCyclicColor(int minValue, int maxValue, int value) {
		if (minValue >= maxValue) {
			return 0;
		}
		
		int relativeValue = (value - minValue) % (maxValue - minValue);
		float percentage = (float) (relativeValue * 100) / (float) ((maxValue - minValue));
		
		//Logarithmic coloring
		/*double u = Math.log(value/(minValue+1)) /  Math.log(maxValue/(minValue+1));
		float percentage = (float) u * 100;*/
		
		int i = -1;
		while (i < percentages.size() - 1 && percentage > percentages.get(i+1)) {
			i++;
		}
		
		int colorA = 0;
		float pA = 0;
		if (i == -1) {
			pA = percentages.get(percentages.size() - 1) - 100.0f;
			colorA = colors.get(percentages.size() - 1);
		} else {
			pA = percentages.get(i);
			colorA = colors.get(i);
		}
		
		int colorB = 0;
		float pB = 0;
		if (i == percentages.size() - 1) {
			pB = percentages.get(0) + 100.0f;
			colorB = colors.get(0);
		} else {
			pB = percentages.get(i + 1);
			colorB = colors.get(i+1);
		}
		
		float p = (percentage - pA) / (pB - pA);
		
		int aR = (int) ((colorA >> 16) & 0xFF);
		int aG = (int) ((colorA >> 8)  & 0xFF);
		int aB = (int) ( colorA        & 0xFF);
		
		int bR = (int) ((colorB >> 16) & 0xFF);
		int bG = (int) ((colorB >> 8)  & 0xFF);
		int bB = (int) ( colorB        & 0xFF);
		
		byte rR = (byte) (( (byte)((float)aR * (1.0f - p)) + (byte)((float)bR * p)) & 0xFF);
		byte rG = (byte) (( (byte)((float)aG * (1.0f - p)) + (byte)((float)bG * p)) & 0xFF);
		byte rB = (byte) (( (byte)((float)aB * (1.0f - p)) + (byte)((float)bB * p)) & 0xFF);
			
		int pixelColor;
		pixelColor = 0;
		pixelColor += (rR & 0xFF);
		pixelColor = (pixelColor << 8) + (rG & 0xFF);
		pixelColor = (pixelColor << 8) + (rB & 0xFF);
		
		return pixelColor;
	}

}
