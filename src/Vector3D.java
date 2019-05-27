
public class Vector3D {
	
	public double x = 0;
	public double y = 0;
	public double z = 0;
	
	public Vector3D(double a, double b, double c) {
		x = a;
		y = b;
		z = c;
	}
	
	public static Vector3D cross(Vector3D a, Vector3D b) {
		Vector3D tr = new Vector3D(0,0,0);
		
		tr.x = a.y * b.z - a.z * b.y;
		tr.y = a.z * b.x - a.x * b.z;
		tr.z = a.x * b.y - a.y * b.x;
		
		return tr;
	}
	
	public static Vector3D dot(Vector3D a, Vector3D b) {
		Vector3D tr = new Vector3D(0,0,0);
		
		tr.x = a.x * b.x;
		tr.y = a.y * b.y;
		tr.z = a.z * b.z;
		
		return tr;
	}
	
	public static Vector3D add(Vector3D a, Vector3D b) {
		Vector3D tr = new Vector3D(0,0,0);
		
		tr.x = a.x + b.x;
		tr.y = a.y + b.y;
		tr.z = a.z + b.z;
		
		return tr;
	}
	
	public static Vector3D sdot(double a, Vector3D b) {
		Vector3D tr = new Vector3D(0,0,0);
		
		tr.x = a * b.x;
		tr.y = a * b.y;
		tr.z = a * b.z;
		
		return tr;
	}
	
	public String toString() {
		return "x: " + x + ", y: " + y + ", z: " + z;
	}
	
	
}
