package xyz.marsavic.gfxlab.graphics3d;


import xyz.marsavic.gfxlab.Vec3;

public class GeometryUtils {
	
	/** An orthogonal vector to v. */
	public static Vec3 normal(Vec3 v) {
		if (v.x() != 0 || v.y() != 0) {
			return Vec3.xyz(-v.y(), v.x(), 0);
		} else {
			return Vec3.EX;
		}
	}
	
	
	/** d reflected over n */
	public static Vec3 reflected(Vec3 n, Vec3 d) {
		return n.mul(2 * d.dot(n) / n.lengthSquared()).sub(d);
	}
	
	
	/** d reflected over n_ */
	public static Vec3 reflectedN(Vec3 n_, Vec3 d) {
		return n_.mul(2 * d.dot(n_)).sub(d);
	}

	
	public static Vec3 refractedN(Vec3 n_, Vec3 i, double refractiveIndex) {
		// TODO optimize like refracted NN if possible. If it's not better, normalize and delegate to refractedNN.
		double ri = refractiveIndex;
		double k = 1;
		double lI = i.length();
		
		double c1 = i.dot(n_) / lI;
		if (c1 < 0) { 		                              // We are exiting the object
			ri = 1.0 / ri;
			k = -1;
		}
		double c2Sqr = 1 - (1 - c1 * c1) / (ri * ri);
		
		Vec3 f;
		if (c2Sqr > 0) {
			double c2 = Math.sqrt(c2Sqr);
			f = n_.mul(c1/ri - k * c2).sub(i.div(ri*lI)); // refraction
		} else {
			f = reflectedN(n_, i);                          // total reflection
		}
		return f;
	}
	
	
	public static Vec3 refractedNN(Vec3 n_, Vec3 i_, double refractiveIndex) {
		double c1 = i_.dot(n_);
		
		double ri, k;
		if (c1 >= 0) { ri = refractiveIndex       ; k =  1; } 
		else         { ri = -1.0 / refractiveIndex; k = -1; }
		
		double c2Sqr = ri * ri + c1 * c1 - 1;
		
		return c2Sqr > 0 ?
				n_.mul(c1 - Math.sqrt(c2Sqr) * k).sub(i_) :    // refraction
				n_.mul(2 * c1).sub(i_);                        // total reflection
	}
	
}
