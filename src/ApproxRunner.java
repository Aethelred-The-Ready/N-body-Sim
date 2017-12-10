import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ApproxRunner {

	static JPanel j;
	static ArrayList<OrbitalBody> oBs = new ArrayList<OrbitalBody>();
	//Earth Moon
	//static OrbitalBody Earth = new OrbitalBody(3.986004418E14, 6371000, 0, 0, 0, -1.33091E1, Color.GREEN);
	//static OrbitalBody Luna = new OrbitalBody(4.9048695E12, 1737100, 362600000, 0, 0, 1082, new Color(127,127,127));
	//Sun Earth Mars
	static OrbitalBody Sol = new OrbitalBody("Sol", 1.32712440018E20, 695700000, 0, 0, 0, 0, Color.YELLOW);
	static OrbitalBody Earth = new OrbitalBody("Earth", 3.986004418E14, 6371000, 147.09E9, 0, 0, 30290, Color.GREEN);
	static OrbitalBody Mars = new OrbitalBody("Mars", 4.282837E13, 3389500, 206.62E9, 0, 0, 26500, Color.RED);
	final static double timeCon = 1;
	final static double posScale = 0.000000001d;
	//for non-log EM use 0.000001d
	//for non-log SEM use 0.000000001d
	final static double radScale = 10;
	//for non-log EM use 100000
	//for log EM use 9
	static int count = 0;
	
	public static void main(String[] args) {
		//double Max = dist(Earth, Luna);
		//double Min = dist(Earth, Luna);
		oBs.add(Sol);
		oBs.add(Earth);
		oBs.add(Mars);
		//oBs.add(Luna);
		//System.out.print(grav(Earth, Luna)[0]);
		
		render();
		
		while(true) {
			run();
			//if(Max < dist(Earth, Luna)) {
			//	Max = dist(Earth, Luna);
			//}
			//if(Min > dist(Earth, Luna)) {
			//	Min = dist(Earth, Luna);
			//}
			count++;
			if(inBound(getAng(Sol, Mars), 0, 0.000001)) {
				//System.out.println("Time: " + (count*timeCon));
				//System.out.println("Ap: " + Max + "\n" + "Pe: " + Min);
				//System.out.println("Earthvel: " + Earth.getVel()[0] + ", " + Earth.getVel()[0]);
			}
			//if(count%1000 == 0)
			//	System.out.println(Earth.getPos()[1]);
			j.repaint();
			//try {
			//	Thread.sleep(10);
			//} catch (Exception e) {}
		}
		
	}
	
	private static boolean inBound(double a, double b, double r) {
		return (Math.abs(a-b) < r);
	}

	public static void run() {
		double[] acc = new double[2];
		double[] accg = new double[2];
		for(int i = 0; i < oBs.size(); i++) {
			for(int k = 0; k < oBs.size(); k++) {
				if(i != k) {
					accg = grav(oBs.get(i), oBs.get(k));
					acc[0] += accg[0];
					acc[1] += accg[1];
					System.out.println(oBs.get(k).getName() + " -> " + oBs.get(i).getName() + " " + accg[0] + " " + accg[1]);
				}
			}
			oBs.get(i).applyAcc(acc, timeCon);
			oBs.get(i).tickVel(timeCon);
		}
	}
	
	private static double[] grav(OrbitalBody oB, OrbitalBody oB2) {
		double ang = Math.PI - Math.atan2((oB.getPos()[1] - oB2.getPos()[1]),(oB.getPos()[0] - oB2.getPos()[0]));
		//System.out.println("angle: " + ang/Math.PI);
		double acc = (oB2.getGMass())/(Math.pow(dist(oB,oB2), 2));
		double[] tr= {acc*Math.cos(ang),-acc*Math.sin(ang)};
		//System.out.println("acc: " + tr[0] + " " + tr[1]);
		return tr;
	}
	
	public static double getAng(OrbitalBody oB, OrbitalBody oB2) {
		return Math.PI - Math.atan2((oB.getPos()[1] - oB2.getPos()[1]),(oB.getPos()[0] - oB2.getPos()[0]));
	}

	private static double dist(OrbitalBody oB, OrbitalBody oB2) {
		return Math.sqrt(Math.pow((oB2.getPos()[1] - oB.getPos()[1]), 2) + Math.pow((oB2.getPos()[0] - oB.getPos()[0]), 2));
	}

	public static void render(){
		JFrame frame = new JFrame("Orbital approximator");
		
		j = new JPanel(){
			public void paint(Graphics p) {	
				OrbitalBody oBcur;
				int rad;
				int x;
				int y;
				p.setColor(Color.BLACK);
				p.fillRect(0, 0, 2000, 2000);
				p.setColor(Color.WHITE);
				for(int i = 0;i < oBs.size();i++) {
					oBcur = oBs.get(i);
					p.setColor(oBcur.getCol());
					//log radius calculator
					rad = (int) (Math.log10(oBcur.getRad())*radScale);
					//non-log radius calculator
					//rad = (int) (oBcur.getRad()/radScale);
					x = scale(oBcur.getPos()[0], rad);
					y = scale(oBcur.getPos()[1], rad);
					p.fillOval(x, y, rad, rad);
					//System.out.println(scale(oBcur.getPos()[0],rad) + " " + scale(oBcur.getPos()[1],rad));
				}
			}
		};
		

		frame.add(j);
		frame.setLocation(100,0);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	protected static int scale(double d, double r) {
		//System.out.println((int) ((Math.log10((d+1)*225)*1.5)*posScale + 500 - r/2));
		//log
		//return (int) (((Math.abs(d)/d)*Math.log10(Math.abs(d) + 1)*posScale) + 500 - r/2);
		//non-log
		return (int) (d*posScale + 500 - r/2);
	}
}
