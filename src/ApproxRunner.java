import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class ApproxRunner {
	
	static JPanel j;
	static ArrayList<OrbitalBody> oBs = new ArrayList<OrbitalBody>();
	final static double timeCon = 1;
	final static double posScale = 0.000000001d;
	//for non-log EM use 0.000001d
	//for non-log SEM use 0.000000001d
	//for non-log MPD use 0.000001d
	final static double radScale = 10;
	//for log EM use 9
	//for log SEM use 10
	//for log MPD use 15
	static int count = 0;
	
	public static void main(String[] args) {
		Scanner f;
		try {
			f = new Scanner(new File("Solar_System.txt"));
		}catch(Exception e){
			System.out.print(e);
			f = new Scanner("Not working");
		}
		while(f.hasNextLine()) {
			oBs.add(new OrbitalBody(f.next(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
		}
		
		render();
		
		while(true) {
			run();
			count++;
			j.repaint();
			//try {
			//	Thread.sleep(1);
			//} catch (Exception e) {}
		}
		
	}
	
	//just checks if a is within r of b
	private static boolean inBound(double a, double b, double r) {
		return (Math.abs(a-b) <= r);
	}

	//calculates the gravity from every body to every other one and applies it
	private static void run() {
		double[] acc = new double[2];
		double[] accg = new double[2];
		for(int i = 0; i < oBs.size(); i++) {
			for(int k = 0; k < oBs.size(); k++) {
				if(i != k) {
					accg = grav(oBs.get(i), oBs.get(k));
					acc[0] += accg[0];
					acc[1] += accg[1];
					//This prints out the acceleration caused by body 1 to body 2
					if(count%10000 == 0) {
						System.out.println(oBs.get(k).getName() + " -> " + oBs.get(i).getName() + " " + accg[0] + " " + accg[1]);
					}
				}
			}
			oBs.get(i).applyAcc(acc, timeCon);
			oBs.get(i).tickVel(timeCon);
		}
	}
	
	//calculates the gravitational acceleration vector's x and y components from body 1 to body 2
	private static double[] grav(OrbitalBody oB, OrbitalBody oB2) {
		double[] ds = {oB2.getPos()[0] - oB.getPos()[0],oB2.getPos()[1] - oB.getPos()[1]};
		double dtot = dist(oB,oB2);
		double acc = (oB2.getGMass())/(dtot*dtot);
		double[] tr= {acc*ds[0]/dtot,acc*ds[1]/dtot};
		return tr;
	}
	
	//calculates the angle between 2 bodies
	private static double getAng(OrbitalBody oB, OrbitalBody oB2) {
		return Math.atan2((oB.getPos()[1] - oB2.getPos()[1]),-(oB.getPos()[0] - oB2.getPos()[0]));
	}

	//calculates the distance between 2 bodies
	private static double dist(OrbitalBody oB, OrbitalBody oB2) {
		return Math.sqrt(Math.pow((oB2.getPos()[1] - oB.getPos()[1]), 2) + Math.pow((oB2.getPos()[0] - oB.getPos()[0]), 2));
	}

	private static void render(){
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
					rad = (int) (Math.log10(oBcur.getRad()/100)*radScale);
					//non-log radius calculator
					//rad = (int) (oBcur.getRad()/radScale);
					x = scale(oBcur.getPos()[0], rad);
					y = scale(oBcur.getPos()[1], rad);
					p.fillOval(x, y, rad, rad);
					//p.setColor(Color.YELLOW);
					//p.drawOval(x - 50, y - 50, rad + 100, rad + 100);
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
		//log
		//return (int) (((Math.abs(d)/d)*Math.log10(Math.abs(d) + 1)*posScale) + 500 - r/2);
		//non-log
		return (int) (d*posScale + 500 - r/2);
	}
}
