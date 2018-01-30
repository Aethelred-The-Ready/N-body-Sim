import java.awt.Color;
import java.awt.Font;
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
	static ArrayList<OrbitalBody> oBs = new ArrayList<OrbitalBody>();
	final static double timeCon = 1;
	static boolean paused = false;
	static double scale = 1;
	static double posScale = 0.000000001d;
	static double radScale = 8;
	static long count = 0;
	static int xV = 500;
	static int yV = 500;
	static ActionListener action = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent a) {
			j.repaint();
			t.start();
		}
		
		
	};
	static Timer t = new Timer(17,action);
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
			f = new Scanner(new File("Solar_System.txt"));
		}catch(Exception e){
			System.out.print(e);
			f = new Scanner("Not working");
		}
		radScale = f.nextDouble();
		posScale = f.nextDouble();
		while(f.hasNextLine()) {
			String name = f.next();
			if(name.charAt(0) == '\\')
				f.nextLine();
			else
				oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
		}
		//String name = f.next();
		//oBs.add(new OrbitalBody(name, f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), f.nextDouble(), new Color(f.nextInt(), f.nextInt(), f.nextInt())));
		//for(int i = 0;i < 25;i++) {
		//	oBs.add(new OrbitalBody("Object: " + i, Math.random()*1E15, 1E6, Math.random()*1E11 - 5E10, Math.random()*1E11 - 5E10, Math.random()*1E5 - 5E4, Math.random()*1E5 - 5E4, new Color(120, 120, 120)));
		//}
		
		render();
		
		t.start();
		
		while(true) {
			if(!paused) {
				run();
				count+=timeCon;
				//if((count/timeCon)%20000 == 0)
				//	j.repaint();
				//if(inBound(getAng(oBs.get(0), oBs.get(3)), 0, 0.000001)) {
				//	System.out.println(count/(3600.0*24));
				//}
			}else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
			}
			//try {
			//	Thread.sleep(0, 1);
			//} catch (Exception e) {}
		}
		
	}
	
	//just checks if a is within r of b
	private static boolean inBound(double a, double b, double r) {
		return (Math.abs(a-b) <= r);
	}

	//calculates the gravity from every body to every other one and applies it
	private static void run() {
		for(int i = 0; i < oBs.size(); i++) {
			for(int k = 0; k < oBs.size(); k++) {
				if(k != i) {
					oBs.get(i).applyAcc(grav(oBs.get(i), oBs.get(k)), timeCon);
				}
			}
			oBs.get(i).tickVel(timeCon);
		}
	}
	
	//calculates the gravitational acceleration vector's x and y components from body 1 to body 2
	private static double[] grav(OrbitalBody oB, OrbitalBody oB2) {
		double dtot = dist(oB,oB2);
		double acc = (oB2.getGMass())/(dtot*dtot*dtot);
		double[] tr= {acc*(oB2.getPos()[0] - oB.getPos()[0]),acc*(oB2.getPos()[1] - oB.getPos()[1])};
		return tr;
	}
	
	//calculates the angle between 2 bodies
	private static double getAng(OrbitalBody oB, OrbitalBody oB2) {
		return Math.atan2((oB.getPos()[1] - oB2.getPos()[1]),-(oB.getPos()[0] - oB2.getPos()[0]));
	}

	//calculates the distance between 2 bodies
	private static double dist(OrbitalBody oB, OrbitalBody oB2) {
		double tr = Math.sqrt(Math.pow((oB2.getPos()[1] - oB.getPos()[1]), 2) + Math.pow((oB2.getPos()[0] - oB.getPos()[0]), 2));
		//if(count%10000 == 0) {
		//	System.out.println(tr);
		//}
		return tr;
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
				}
				p.setColor(Color.WHITE);
				long time = count/(3600*24);
				p.drawString("Days: " + time, 10, 30);
				p.drawString("Scale: " + scale + " AU", 10, 50);
			}
		};
		
		frame.addKeyListener(k);
		frame.add(j);
		frame.setLocation(100,0);
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
