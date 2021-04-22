import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class ApproxRunner {
	
	static JPanel j;
	static Molecule[] mols;//Common atmospheric molecules
	static ArrayList<OrbitalBody> oBs = new ArrayList<OrbitalBody>();
	static OrbitalBody Rocket = new OrbitalBody("Rocket", 5E3, 1E6, 147.41639E9, 0, 0, 0, 31372, 0, 0, new Color(200, 50, 200));
	final static double timeCon = 100;
	final static double reposCon = 1000;
	static boolean paused = true;
	static double scale = 1;
	static double posScale = 0.000000001d;
	static double radScale = 8;
	static long count = 0;
	static int xV = 500;
	static int yV = 500;
	static boolean zaxis = false;
	static int speed = 0;
	static Time curTime = new Time(2000, 1, 1, 12, 0, 0);
	static boolean wentWrong = false;
	static OrbitalBody focus;
	static boolean focused = false;
	static int foc = 0;
	static String s = "";
	static LinkedList<xyz>[] trails;
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
	static Timer t = new Timer(40,render);
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
				paused = !paused;
			}else if(e.getKeyCode() == KeyEvent.VK_Z) {
				zaxis = !zaxis;
			}else if(e.getKeyCode() == KeyEvent.VK_C) {
				save();
				clearTrails();
			}else if(e.getKeyCode() == KeyEvent.VK_X) {
				focused = !focused;
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
			}else if(e.getKeyCode() == KeyEvent.VK_9) {
				speed/=2;
				if(speed == 1)
					speed = 0;
			}else if(e.getKeyCode() == KeyEvent.VK_0) {
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
			f = new Scanner(new File("Solar_System_J2000.txt"));
		}catch(Exception e){
			System.out.print(e);
			f = new Scanner("\\Not working");
		}
		//molsInit();
		radScale = f.nextDouble();
		posScale = f.nextDouble();
		int t = 0;
		System.out.println(radScale);
		System.out.println(posScale);
		
		while(f.hasNext()) {
			System.out.println(f.hasNext());
			String name = f.next();
			if(name.charAt(0) == '/') {
				f.nextLine();
			}else {
				boolean exact = true;
				if(exact) {
					oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
					System.out.println(t + ": " + oBs.get(t));
					t++;
				}else{
					double GM = f.nextDouble() * 6.67408E-11;
					double rD = f.nextDouble(); //radius
					double sM = f.nextDouble(); //semimajor axis
					double eC = f.nextDouble(); //eccentricity
					double iN = f.nextDouble(); //inclination
					double lA = f.nextDouble(); //long Ascending Node
					double lP = f.nextDouble(); //long Periapsis
					double v0 = f.nextDouble(); //V0
					double rO = f.nextDouble(); //Rotation in hours
					Color col = new Color(f.nextInt(), f.nextInt(), f.nextInt());
					String parent = f.next();
					
					OrbitalBody Parent = getOBFromString(parent);
					
					System.out.println(Parent);
					
					if(Parent != null) {
					
						Vector3D P = new Vector3D(
								((Math.cos(lA) * Math.cos(lP - lA)) - (Math.sin(lA) * Math.cos(iN) * Math.cos(lP - lA))),
								((Math.sin(lA) * Math.cos(lP - lA)) + (Math.cos(lA) * Math.cos(iN) * Math.sin(lP - lA))),
								(Math.sin(iN) * Math.sin(lP - lA)));
						
						Vector3D Q = new Vector3D(
								((- (Math.cos(lA) * Math.sin(lP - lA))) - (Math.sin(lA) * Math.cos(iN) * Math.cos(lP - lA))),
								((- (Math.sin(lA) * Math.sin(lP - lA))) + (Math.cos(lA) * Math.cos(iN) * Math.cos(lP - lA))),
								(Math.sin(iN) * Math.cos(lP - lA)));
						
						double rs = (sM * (1 - eC * eC)) / (1 + eC * Math.cos(v0));
						
						Vector3D R = Vector3D.add(Vector3D.sdot((rs * Math.cos(v0)), P), Vector3D.sdot((rs * Math.sin(v0)), Q));
					
						Vector3D V = Vector3D.sdot(
								Math.sqrt(Parent.getGMass()/(sM * (1 - eC * eC))), 
								Vector3D.add(Vector3D.sdot(-Math.sin(v0), P),
											 Vector3D.sdot((eC + Math.cos(v0)), Q)));
						System.out.println("Pos: " + R + "\nVel: " + V);
						
						oBs.add(new OrbitalBody(name, GM, rD, R.x, R.y, R.z, V.x, V.y, V.z, rO, col));
						
					}else {
						oBs.add(new OrbitalBody(name, GM, rD, 0, 0, 0, 0, 0 ,0, rO, col));
						
					}
				}
			}
		}
		
		trails = new LinkedList[oBs.size()];
		
		for(int i = 0;i < oBs.size();i++) {
			trails[i] = new LinkedList<xyz>();
		}
		
		//String name = f.next();
		//oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
		//for(int i = 0;i < 25;i++) {
		//	oBs.add(new OrbitalBody("Object: " + i, Math.random()*1E15, 1E6, Math.random()*1E11 - 5E10, Math.random()*1E11 - 5E10, Math.random()*1E5 - 5E4, Math.random()*1E5 - 5E4, new Color(120, 120, 120)));
		//}
		//focus = oBs.get(1);
		
		//render();
		
		//t.start();
		
		long startTime = System.currentTimeMillis();
		
		r.run();
		while(true) {
			if(!paused && !wentWrong) {
				runner();
				count+=timeCon;
				if(count%31556926 == 0) {
					System.out.println(System.currentTimeMillis() - startTime);
				}
			}else {
				if(Double.isNaN(oBs.get(0).getPos()[0])) {
					System.out.println(s);
					return;
				}
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
			}
			if(speed != 0) {
				for(int i = 0;i < speed;i++) {
					System.nanoTime();
				}
			}
			//System.out.println(" " + System.nanoTime());
		}
		
	}
	
	private static OrbitalBody getOBFromString(String name) {
		for(int i = 0;i < oBs.size();i++) {
			if(name.equals(oBs.get(i).getName())) {
				return oBs.get(i);
			}
		}
		return null;
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
		s = "";
		//ExecutorService calcs = Executors.newFixedThreadPool(oBs.size());
		//ArrayList<Future<OrbitalBody>> oBsCalcs = new ArrayList<Future<OrbitalBody>>();
		for(int i = 0; i < oBs.size(); i++) {
			
			//oBsCalcs.add((Future<OrbitalBody>) calcs.submit(new Calculator(oBs, i, timeCon, reposCon)));
			
			for(int k = 0; k < oBs.size(); k++) {
				if(k != i) {
					oBs.get(i).applyAcc(grav(oBs.get(i), oBs.get(k)), timeCon);
				}
			}
			//s += ("All ok with: " + oBs.get(i) + "\n");

			//Rocket.applyAcc(grav(Rocket, oBs.get(i)), timeCon);
			//System.out.println("All good with: " + oBs.get(i));
			oBs.get(i).tickVel(timeCon);
		}
		
		/*for(int i = 0; i < oBs.size(); i++) {
			try {
				oBs.set(i, oBsCalcs.get(i).get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		calcs.shutdown();*/
		//Rocket.tickVel(timeCon);
		curTime.tick(timeCon);
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
					xV = 500 - (int) (focus.getPos()[0]*posScale/1.44);
					yV = 500 - (int) (focus.getPos()[1]*posScale/1.44);
				}
				OrbitalBody oBcur;
				int rad;
				int x;
				int y;
				p.setColor(Color.BLACK);
				p.fillRect(0, 0, 2000, 2000);
				p.setColor(Color.BLACK);
				p.fillRect(0, 0, 200, 100);
				p.setColor(new Color(25,0,0));
				for(int i = 0;i <= 20;i++) {
					p.drawLine(i*100, 0, i*100, 2000);
					p.drawLine(0, i*100, 2000, i*100);
				}
				p.setColor(Color.WHITE);
				if(zaxis) {
					ArrayList<OrbitalBody> oBsTemp = new ArrayList<OrbitalBody>();
					for(int i = 0;i < oBs.size();i++) {
						oBsTemp.add(oBs.get(i));
						double scaling = ((oBs.get(i).getPos()[1] + 7.4E14) / 7.4E14);
						System.out.println(oBs.get(i).getName() + ": " + (oBs.get(i).getPos()[1]) + ", "+ scaling);
					}
					Collections.sort(oBsTemp, OrbitalBody.ySort);
					for(int i = 0;i < oBsTemp.size();i++) {
						oBcur = oBsTemp.get(i);
						p.setColor(oBcur.getCol());
						double scaling = ((oBs.get(i).getPos()[1] + 7.4E14) / 7.4E14);
						//log radius calculator
						rad = (int) (Math.log10(oBcur.getRad()/100)*radScale*scaling);
						//System.out.println(oBs.get(i).getName() + ": " + rad);
						x = (int) (scale(oBcur.getPos()[0], rad, 0));
						y = (int) (scale(oBcur.getPos()[2], rad, 2));
						p.fillOval(x, y, rad, rad);
					}
				} else {
					for(int i = 0;i < oBs.size();i++) {
						p.setColor(oBs.get(i).getCol());
						ListIterator<xyz> li = trails[i].listIterator(0);
						
						while(li.hasNext()) {
							xyz tra = li.next();
							p.drawRect(tra.x, tra.y, 1, 1);
						}
					}
					
					
					for(int i = 0;i < oBs.size();i++) {
						oBcur = oBs.get(i);
						p.setColor(oBcur.getCol());
						//log radius calculator
						rad = (int) (Math.log10(oBcur.getRad()/100)*radScale);
						x = (int) (scale(oBcur.getPos()[0], rad, 0));
						y = (int) (scale(oBcur.getPos()[1], rad, 1));
						
						
						
						p.fillOval(x, y, rad, rad);
						trails[i].addFirst(new xyz(x + rad/2, y + rad/2, 0));
						if(count > 1000000000) {
							trails[i].removeLast();
						}
						
						if(i != 0) {
							p.setColor(oBcur.getCol().darker());
							int ang = getXYAngle(oBs.get(0), oBcur) + 180;
							p.fillArc(x, y, rad, rad, ang, 180);
						}
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
						//p.drawLine((int) (x + rad2*Math.sin(oBcur.getCurRot())), (int) (y + rad2*Math.cos(oBcur.getCurRot())), (int) (x - rad2*Math.sin(oBcur.getCurRot())), (int) (y - rad2*Math.cos(oBcur.getCurRot())));
						
						
						
					}
				}
				//p.setColor(Rocket.getCol());
				//rad = (int) (Math.log10(Rocket.getRad()/100)*radScale);
				//x = (int) (scale(Rocket.getPos()[0], rad, 0));
				//y = (int) (scale(Rocket.getPos()[1], rad, 1));
				//p.fillOval(x, y, rad, rad);
				p.setColor(Color.WHITE);
				//long time = count/(3600*24);
				//p.drawString("Days: " + time, 10, 30);
				p.drawString(curTime.toString(), 10, 30);
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
	
	private static int getXYAngle(OrbitalBody a, OrbitalBody b) {
		double[] ap = a.getPos();
		double[] bp = b.getPos();
		
		return (int) ((Math.atan2((bp[0] - ap[0]),(bp[1] - ap[1]))/(Math.PI))*180);
		
	}
	
	private static int getZAngle(OrbitalBody a, OrbitalBody b) {
		double[] ap = a.getPos();
		double[] bp = b.getPos();
		
		return (int) Math.atan((Math.pow(ap[0] - bp[0], 2) * Math.pow(ap[0] - bp[0], 2))/(ap[2] - bp[2]));
		
	}

	private static int scale(double d, double r, int axis) {
		////log
		///return (int) (((Math.abs(d)/d)*Math.log10(Math.abs(d) + 1)*posScale) + 500 - r/2);
		//non-log
		
		if(axis == 0) {
			return (int) (d*posScale/1.44 + xV - r/2);	
		}
		return (int) (d*posScale/1.44 + yV - r/2);
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
	
	private static void clearTrails() {
		for(int i = 0;i < trails.length;i++) {
			trails[i].removeAll(trails[i]);
		}
	}
}
