import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class ApproxRunner {
	
	static JPanel j;
	static Molecule[] mols;//Common atmospheric molecules
	static ArrayList<OrbitalBody> oBs = new ArrayList<OrbitalBody>();
	static OrbitalBody Rocket = new OrbitalBody("Rocket", 5E3, 1E6, 147.09E9, 350E6, 0, -1000, 30290, 0, 0, new Color(200, 50, 200));
	final static double timeCon = 1;
	static boolean paused = false;
	static double scale = 1;
	static double posScale = 0.000000001d;
	static double radScale = 8;
	static long count = 0;
	static int xV = 500;
	static int yV = 500;
	static int speed = 1024;
	static OrbitalBody focus;
	static boolean focused = false;
	static int foc = 0;
	static Runnable r = new Runnable() {

		public void run() {
			render();
			t.start();
		}
	};
	static ActionListener render = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent a) {
			j.repaint();
			t.start();
		}
		
		
	};
	static Timer t = new Timer(17,render);
	static KeyListener k = new KeyListener() {

		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_W)
				yV+=10;
			else if(e.getKeyCode() == KeyEvent.VK_S)
				yV-=10;
			else if(e.getKeyCode() == KeyEvent.VK_A)
				xV+=10;
			else if(e.getKeyCode() == KeyEvent.VK_D)
				xV-=10;
			else if(e.getKeyCode() == KeyEvent.VK_R) {
				posScale *= 0.5;
				scale /= 0.5;
				yV = (int) (((yV - 500) * 0.5) + 500);
				xV = (int) (((xV - 500) * 0.5) + 500);
				radScale -= 1;
			}else if(e.getKeyCode() == KeyEvent.VK_F) {
				posScale *= 2;
				scale /= 2;
				yV = (int) (((yV - 500) * 2) + 500);
				xV = (int) (((xV - 500) * 2) + 500);
				radScale += 1;
			}else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				paused =! paused;
			}else if(e.getKeyCode() == KeyEvent.VK_Z) {
				save();
			}else if(e.getKeyCode() == KeyEvent.VK_X) {
				focused =! focused;
			}else if(e.getKeyCode() == KeyEvent.VK_T) {
				foc++;
				if(foc >= 0)
					focus = oBs.get(foc);
				else
					focus = Rocket;
			}else if(e.getKeyCode() == KeyEvent.VK_G) {
				foc--;
				if(foc >= 0)
					focus = oBs.get(foc);
				else
					focus = Rocket;
			}else if(e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
				speed/=2;
				if(speed == 1)
					speed = 0;
			}else if(e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
				speed*=2;
				if(speed == 0) {
					speed = 1;
				}
			}
			j.repaint();
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void keyTyped(KeyEvent e) {
			
		}
			
	};
	
	public static void main(String[] args) {
		Scanner f;
		try {
			f = new Scanner(new File("Saturn_System.txt"));
		}catch(Exception e){
			System.out.print(e);
			f = new Scanner("\\Not working");
		}
		molsInit();
		radScale = f.nextDouble();
		posScale = f.nextDouble();
		int t = 0;
		while(f.hasNextLine()) {
			String name = f.next();
			if(name.charAt(0) == '\\')
				f.nextLine();
			else
				oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
			System.out.println(t + ": " + oBs.get(t++));
		}
		//String name = f.next();
		//oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
		//for(int i = 0;i < 25;i++) {
		//	oBs.add(new OrbitalBody("Object: " + i, Math.random()*1E15, 1E6, Math.random()*1E11 - 5E10, Math.random()*1E11 - 5E10, Math.random()*1E5 - 5E4, Math.random()*1E5 - 5E4, new Color(120, 120, 120)));
		//}
		//focus = oBs.get(1);
		
		//render();
		
		//t.start();
		r.run();
		while(true) {
			if(!paused) {
				runner();
				count+=timeCon;
			}else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
			}
			for(int i = 0;i < speed;i++) {
				System.nanoTime();
			}
			//System.out.println(" " + System.nanoTime());
		}
		
	}
	
	private static void molsInit() {
		Molecule[] tmols = {new Molecule("AHydrogen", 1), new Molecule("MHydrogen", 2),
							new Molecule("Helium", 4), //new Molecule("Helium", 4),
							new Molecule("ANitrogen", 12), new Molecule("MNitrogen", 24),
							new Molecule("AOxygen", 16), new Molecule("MOxygen", 32),
							new Molecule("CH4", 16), new Molecule("NH3", 17),
							new Molecule("OH2", 18), new Molecule("Neon", 20),
							new Molecule("CO", 28), new Molecule("NO", 30),
							new Molecule("H2S", 34), new Molecule("Argon", 40),
							new Molecule("CO2", 44), new Molecule("N2O", 44),
							new Molecule("NO2", 46), new Molecule("O3", 48),
							new Molecule("SO2", 64), new Molecule("SO3", 80),
							new Molecule("Krypton", 84), new Molecule("Xenon", 131),
							new Molecule("C2H6", 30), new Molecule("Hydrogen Deuteride", 3),
							};
		mols = tmols;
	}

	//calculates the angle between 2 bodies
	private static double getAng(OrbitalBody oB, OrbitalBody oB2) {
		return Math.atan2((oB.getPos()[1] - oB2.getPos()[1]),-(oB.getPos()[0] - oB2.getPos()[0]));
	}
	
	//just checks if a is within r of b
	private static boolean inBound(double a, double b, double r) {
		return (Math.abs(a-b) <= r);
	}

	//calculates the gravity from every body to every other one and applies it
	private static void runner() {
		for(int i = 0; i < oBs.size(); i++) {
			for(int k = 0; k < oBs.size(); k++) {
				if(k != i) {
					//System.out.println("{" + i + ", " + k + "}");
					oBs.get(i).applyAcc(grav(oBs.get(i), oBs.get(k)), timeCon);
					//if(count%10000 == 0) {
					//	if(dist(oBs.get(i), oBs.get(k)) < oBs.get(i).getRad() + oBs.get(k).getRad()) {
					//		coll(oBs.get(i), oBs.get(k));
					//		oBs.remove(k);
					//	}
					//}
				}
			}
			//Rocket.applyAcc(grav(Rocket, oBs.get(i)), timeCon);
			oBs.get(i).tickVel(timeCon);
		}
		//Rocket.tickVel(timeCon);
	}
	
	//calculates the gravitational acceleration vector's x and y components from body 1 to body 2
	private static double[] grav(OrbitalBody oB, OrbitalBody oB2) {
		double dtot = dist(oB,oB2);
		double acc = (oB2.getGMass())/(dtot*dtot*dtot);
		double[] tr= {acc*(oB2.getPos()[0] - oB.getPos()[0]),acc*(oB2.getPos()[1] - oB.getPos()[1]), acc*(oB2.getPos()[2] - oB.getPos()[2])};
		return tr;
	}
	

	//calculates the distance between 2 bodies
	private static double dist(OrbitalBody oB, OrbitalBody oB2) {
		double tr = Math.sqrt(Math.pow((oB2.getPos()[1] - oB.getPos()[1]), 2) + Math.pow((oB2.getPos()[0] - oB.getPos()[0]) + Math.pow((oB2.getPos()[2] - oB.getPos()[2]), 2), 2));
		//if(count%10000 == 0) {
		//	System.out.println(tr);
		//}
		return tr;
	}
	
	//Calculates collision between 2 bodies
	//Shouldn't happen often, mostly for fun
	//Forms a new orbital body with the mass of both, does not simulate breaking
	//Returns the body as a, b should then be deleted.
	private static void coll(OrbitalBody a, OrbitalBody b) {
		OrbitalBody n = new OrbitalBody("",a.getGMass() + b.getGMass(),Math.cbrt(Math.pow(a.getRad(), 3) + Math.pow(b.getRad(), 3)),0,0,0,a.getPos()[0],a.getPos()[1],a.getPos()[2], 20, Color.BLUE);
		double[] acc = new double[3];
		acc[0] = (a.getVel()[0]*a.getGMass() + b.getVel()[0]*b.getGMass()) / n.getGMass();
		acc[1] = (a.getVel()[1]*a.getGMass() + b.getVel()[1]*b.getGMass()) / n.getGMass();
		acc[2] = (a.getVel()[2]*a.getGMass() + b.getVel()[2]*b.getGMass()) / n.getGMass();
		n.applyAcc(acc, 1);
		a.changeTo(n);
	}
	
	private static Color avgCol(Color a, Color b) {
		return new Color((a.getRed() + b.getRed())/2,(a.getGreen() + b.getGreen())/2,(a.getBlue() + b.getBlue())/2);
	}

	private static void render(){
		JFrame frame = new JFrame("Orbital approximator");
		
		j = new JPanel(){
			public void paint(Graphics p) {	
				if(focused) {
					xV = 500 - (int) (focus.getPos()[0]*posScale);
					yV = 500 - (int) (focus.getPos()[1]*posScale);
				}
				OrbitalBody oBcur;
				int rad;
				int x;
				int y;
				p.setColor(Color.BLACK);
				p.fillRect(0, 0, 2000, 2000);
				p.setColor(Color.BLACK);
				p.fillRect(0, 0, 200, 100);
				p.setColor(Color.RED);
				for(int i = 0;i <= 20;i++) {
					p.drawLine(i*100, 0, i*100, 2000);
					p.drawLine(0, i*100, 2000, i*100);
				}
				p.setColor(Color.WHITE);
				for(int i = 0;i < oBs.size();i++) {
					oBcur = oBs.get(i);
					p.setColor(oBcur.getCol());
					//log radius calculator
					rad = (int) (Math.log10(oBcur.getRad()/100)*radScale);
					x = (int) (scale(oBcur.getPos()[0], rad, 0));
					y = (int) (scale(oBcur.getPos()[1], rad, 1));
					p.fillOval(x, y, rad, rad);
					if(i == foc) {
						int ld = (int) (posScale * 1.079E12);
						p.setColor(Color.YELLOW);
						p.drawOval(x - (int) (1.0/2 * ld), y - (int) (1.0/2 * ld), ld, ld);
						p.setColor(Color.WHITE);
						p.drawString(oBcur.getVel()[0] + ", " + oBcur.getVel()[1] + ", " + oBcur.getVel()[2] + " m/s", 10, 110);
						p.drawString(oBcur.getPos()[0] + ", " + oBcur.getPos()[1] + ", " + oBcur.getPos()[2] + " m", 10, 130);
					}
					p.setColor(Color.WHITE);
					double rad2 = rad/2;
					x+=rad2;
					y+=rad2;
					p.drawLine((int) (x + rad2*Math.sin(oBcur.getCurRot())), (int) (y + rad2*Math.cos(oBcur.getCurRot())), (int) (x - rad2*Math.sin(oBcur.getCurRot())), (int) (y - rad2*Math.cos(oBcur.getCurRot())));
					
				}
				//p.setColor(Rocket.getCol());
				//rad = (int) (Math.log10(Rocket.getRad()/100)*radScale);
			//	x = (int) (scale(Rocket.getPos()[0], rad, 0));
			//	y = (int) (scale(Rocket.getPos()[1], rad, 1));
			//	p.fillOval(x, y, rad, rad);
				p.setColor(Color.WHITE);
				long time = count/(3600*24);
				p.drawString("Days: " + time, 10, 30);
				p.drawString("Speed: " + speed, 10, 50);
				p.drawString("Scale: " + scale + " AU", 10, 70);
				
			}
		};
			
		frame.addKeyListener(k);
		frame.add(j);
		frame.setLocation(800,0);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static int scale(double d, double r, int axis) {
		////log
		///return (int) (((Math.abs(d)/d)*Math.log10(Math.abs(d) + 1)*posScale) + 500 - r/2);
		//non-log
		if(axis == 0) {
			return (int) (d*posScale + xV - r/2);	
		}
		return (int) (d*posScale + yV - r/2);
	}
	
	private static void save() {
		try {
			FileWriter fw = new FileWriter("save.txt", false);
			String tw = radScale + " " + posScale + "\n";
			for(OrbitalBody ob : oBs) {
				tw += (ob.toString() + "\n");
			}
			System.out.print(tw);
			System.out.print(count + " ");
			fw.write(tw);
			fw.close();
		} catch (IOException e) {
			System.out.print("" + e);
		}
	}
}
