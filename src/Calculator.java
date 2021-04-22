import java.util.ArrayList;

public class Calculator extends Thread{
	private ArrayList<OrbitalBody> oBs;
	int object;
	double timeCon;
	double reposCon;
	
	public Calculator(ArrayList<OrbitalBody> toBs, int tobject, double ttimeCon, double treposCon) {
		oBs = toBs;
		object = tobject;
		timeCon = ttimeCon;
		reposCon = treposCon;
		
		System.out.println(oBs);
		
		for(int i = 0; i < reposCon/timeCon;i++) {
			oBs.get(object).applyAcc(grav(oBs.get(object), oBs.get(i)), timeCon);
		}
	}
	
	public OrbitalBody call() {
		return oBs.get(object);
	}
	
	//calculates the gravitational acceleration vector's x and y components from body 1 to body 2
	private static double[] grav(OrbitalBody oB, OrbitalBody oB2) {
		double[] dn = dN(oB, oB2);
		double dtot = Math.sqrt(dn[0]*dn[0] + dn[1]*dn[1] + dn[2]*dn[2]);
		double acc = (oB2.getGMass())/(dtot*dtot*dtot);
		double[] tr= {acc*dn[0],acc*dn[1], acc*dn[2]};
		//System.out.println(oB2.getName() + " -> " + oB.getName() + ": (" + dtot + " m) (" + dn[0] + ", " + dn[1] + ", " + dn[2] + " m) (" + tr[0] + ", " + tr[1] + ", " + tr[2] + " m/s^2)");
		return tr;
	}
	
	private static double trig(OrbitalBody oB, OrbitalBody oB2, int a, int b) {
		return Math.sqrt((oB2.getPos()[a] - oB.getPos()[a])*(oB2.getPos()[a] - oB.getPos()[a]) + (oB2.getPos()[b] - oB.getPos()[b])*(oB2.getPos()[b] - oB.getPos()[b]));
	}

	//calculates the distance between 2 bodies
	private static double dist(OrbitalBody oB, OrbitalBody oB2) {
		double tr = Math.sqrt(Math.pow((oB2.getPos()[0] - oB.getPos()[0]), 2)
							+ Math.pow((oB2.getPos()[1] - oB.getPos()[1]), 2)
							+ Math.pow((oB2.getPos()[2] - oB.getPos()[2]), 2));
		//if(count%10000 == 0) {
		//	System.out.println(tr);
		//}
		return tr;
	}
	
	private static double[] dN(OrbitalBody oB, OrbitalBody oB2) {
		double[] a = oB.getPos();
		double[] b = oB2.getPos();
		double[] tr = {b[0] - a[0], b[1] - a[1], b[2] - a[2]};
		return tr;
		
	}
}
