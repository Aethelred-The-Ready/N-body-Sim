
public class atmo {
	private double atm;//Atmosphere at "surface" in Pa
	private double scaleHeight;//Scale height of atmosphere
	private Comp[] molecules;//Molecular composition of the atmosphere
	
	public atmo (double a, double sH){
		atm = a;
		scaleHeight = sH;
	}
}
