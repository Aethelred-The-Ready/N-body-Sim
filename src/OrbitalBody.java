import java.awt.Color;
import java.util.Comparator;

public class OrbitalBody {

	private static double G = 6.67408E-11;
	private double GM;//Mass in kilograms * Gravitational constant
	private double radius;//radius of body in meters
	private double[] pos = new double[3];//position, in meters, x,y,z
	private double[] vel = new double[3];//velocity in m/s
	private double spin;//spin in hours per rotation
	private double curRot = 0;//Current rotation
	private Color col;
	private String name;
	
	public OrbitalBody(String n, double m, double r, double x, double y, double z, double vx, double vy, double vz, double sp, Color c) {
		GM = m;
		radius = r;
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
		vel[0] = vx;
		vel[1] = vy;
		vel[2] = vz;
		spin = sp * 3600;
		col = c;
		name = n;
	}
	
	public void changeTo(OrbitalBody a) {
		GM = a.GM;
		radius = a.radius;
		pos[0] = a.pos[0];
		pos[1] = a.pos[1];
		pos[2] = a.pos[2];
		vel[0] = a.vel[0];
		vel[1] = a.vel[1];
		vel[2] = a.vel[2];
		col = a.col;
		name = a.name;
	}
	
	public double getGMass() {
		return GM;
	}
	
	public double[] getPos() {
		return pos;
	}
	
	//acc: acceleration in m/s/s, x,y
	//time: time in seconds
	public void applyAcc(double[] acc, double time) {
		vel[0] += acc[0] * time;
		vel[1] += acc[1] * time;
		vel[2] += acc[2] * time;
	}
	
	public void tickVel(double time) {
		pos[0] += vel[0] * time;
		pos[1] += vel[1] * time;
		pos[2] += vel[2] * time;
		curRot -= (Math.PI * 2 * time)/spin;
		if(curRot > Math.PI * 2) {
			curRot -= Math.PI * 2;
		}
	}
	
	public double getCurRot() {
		return curRot;
	}

	public double getRad() {
		return radius;
	}

	public double[] getVel() {
		return vel;
	}
	
	public Color getCol() {
		return col;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name + " " + GM + " " + radius + " " + pos[0] + " " + pos[1] + " " + pos[2] + " " + vel[0] + " " + vel[1] + " " + vel[2] + " " + col.getRed() + " " + col.getGreen() + " " + col.getBlue();
	}
	
	public static Comparator<OrbitalBody> ySort = new Comparator<OrbitalBody>() {
		public int compare(OrbitalBody a, OrbitalBody b) {
			return (int) (a.pos[1]-b.pos[1]);
		}
	};
}