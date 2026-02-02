package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Affine3;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Numeric;

import java.util.random.RandomGenerator;

/** 
 * TODO: Test 
 */
public class Sampling {
	
	
	private Sampling() {}		
	
	
	public static Vec3 hemisphereUniform_          (Vec3 n_, Hash hash ) { return HemisphereUniform.phiX_               (n_, hash); }	
	
	public static Vec3 directionCosineDistributed (Vec3 n , Hash hash ) { return DirectionCosineDistributed.movedBallN(n , hash); }	
	public static Vec3 directionCosineDistributedN(Vec3 n_, Hash hash ) { return DirectionCosineDistributed.movedBallN(n_, hash); }

	public static Vec3 hemisphereCosineDistributed  (Vec3 n , Hash hash ) { return HemisphereCosineDistributed.phiXSqr  (n , hash); }	
	public static Vec3 hemisphereCosineDistributedN_(Vec3 n_, Hash hash ) { return HemisphereCosineDistributed.phiXSqrN_(n_, hash); }
	
	public static Vec3 sphereUniform_              (Hash hash) { return SphereUniform.sampleSphereUniform_(hash); }
	
	public static Vec3 ballUniform                 (Hash hash) { return BallUniform.sampleBallUniform(hash); }
	

	
	// Most of the things are super untested
	
	
	private static class HemisphereUniform {
	
		private static Vec3 phiX_(Vec3 n, Hash hash) {
			double phi = hash.getDouble();
			double x   = hash.add(0).getDouble();
			
			double r = Math.sqrt(1 - x*x);
			double y = Numeric.cosT(phi) * r;
			double z = Numeric.sinT(phi) * r;      // TODO Change sin(phi)*r to sqrt(r*r - y*y)
			
			return (x * n.x() + y * n.y() + z * n.z() < 0) ?
					Vec3.xyz(-x, -y, -z) :
					Vec3.xyz(x, y, z);
		}
		

		private static Vec3 rejection(Vec3 n, Hash hash) {
			RandomGenerator rng = hash.rng();
			double x, y, z, lVSqr;
			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
				lVSqr = x * x + y * y + z * z;
			} while (lVSqr > 0.25);
			
			return (x * n.x() + y * n.y() + z * n.z() < 0) ?
					Vec3.xyz(-x, -y, -z) :
					Vec3.xyz(x, y, z);
		}
		
	
		private static Vec3 rejection_(Vec3 n, Hash hash) {
			RandomGenerator rng = hash.rng();
			double x, y, z, lVSqr;
			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
				lVSqr = x * x + y * y + z * z;
			} while (lVSqr > 0.25);
			
			double c = 1 / Math.sqrt(lVSqr);
			return (x * n.x() + y * n.y() + z * n.z() < 0) ?
					Vec3.xyz(-x * c, -y * c, -z * c) :
					Vec3.xyz(x * c, y * c, z * c);
		}
		
	}

	public static class DirectionCosineDistributed {

		private static Vec3 movedBallN(Vec3 n_, Hash hash) {
			// Sample the sphere with radius 1, add n_

			double phi = hash.getDouble();
			double x = 2 * hash.add(0).getDouble() - 1;
			double r = Math.sqrt(1 - x * x);
			double y = Numeric.cosT(phi) * r;
			double z = Numeric.sinT(phi) * r;
			
			return Vec3.xyz(x, y, z).add(n_);
		}

		
		private static Vec3 rejectionMovedSphere(Vec3 n, Hash hash) {
			// Sample the sphere with radius 1, add n_
			RandomGenerator rng = hash.rng();
			double x, y, z, lVSqr;
			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
				lVSqr = x * x + y * y + z * z;
			} while (lVSqr > 0.25);
			
			double c = Math.sqrt(n.lengthSquared() / lVSqr);
			return Vec3.xyz(x * c, y * c, z * c).add(n);
		}
		
		
		private static Vec3 rejectionMovedSphereN(Vec3 n_, Hash hash) {
			// Sample the sphere with radius 1, add n_
			RandomGenerator rng = hash.rng();
			
			double x, y, z, lVSqr;
			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
				lVSqr = x * x + y * y + z * z;
			} while (lVSqr > 0.25);
			
			double c = 1 / Math.sqrt(lVSqr);
			return Vec3.xyz(x * c, y * c, z * c).add(n_);
		}
		
	}
	
	
	public static class HemisphereCosineDistributed {
		
		private static Vec3 phiXSqr(Vec3 n, Hash hash) {
			double phi = hash.getDouble();
			double xSqr = hash.add(0).getDouble();
			double x = Math.sqrt(xSqr);
			
			double r = Math.sqrt(1 - xSqr);
			double y = Numeric.cosT(phi) * r;
			double z = Numeric.sinT(phi) * r;
			
			return Affine3.asEX(n).applyWithoutTranslationTo(Vec3.xyz(x, y, z));
		}
		
		
		private static Vec3 phiXSqrN_(Vec3 n_, Hash hash) {
			double phi = hash.getDouble();
			double xSqr = hash.add(0).getDouble();
			double x = Math.sqrt(xSqr);
			
			double r = Math.sqrt(1 - xSqr);
			double y = Numeric.cosT(phi) * r;
			double z = Numeric.sinT(phi) * r;
			
			return Affine3.asEXN(n_).applyWithoutTranslationTo(Vec3.xyz(x, y, z));
		}
		
		
		public static Vec3 rejectionFromDiskN(Vec3 n_, Hash hash) {
			RandomGenerator rng = hash.rng();
			
			// Sample the sphere with radius 1, add n_
			double x, y, rSqr;
			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				rSqr = x * x + y * y;
			} while (rSqr > 0.25);
			
			double z = Math.sqrt(1 - 4 * rSqr);
			
			Vec3 ex = GeometryUtils.normal(n_).normalized_();
			Vec3 ey = n_.cross(ex);
			
			return ex.mul(x).add(ey.mul(y)).add(n_.mul(z));
		}
		
	}

	
	private static class SphereUniform {
		
		// TODO (phi, x)
		
		private static Vec3 sampleSphereUniform_(Hash hash) {
			RandomGenerator rng = hash.rng();			
			double x, y, z, lVSqr;			
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
				lVSqr = x * x + y * y + z * z;
			} while (lVSqr > 0.25);
			
			double c = 1 / Math.sqrt(lVSqr);
			return Vec3.xyz(x * c, y * c, z * c);
		}
		
	}


	private static class BallUniform {

		// TODO (phi, x, r)
		
		private static Vec3 sampleBallUniform(Hash hash) {
			RandomGenerator rng = hash.rng();
			double x, y, z;
			do {
				x = rng.nextDouble() - 0.5;
				y = rng.nextDouble() - 0.5;
				z = rng.nextDouble() - 0.5;
			} while (x*x + y*y + z*z > 0.25);
			return Vec3.xyz(2*x, 2*y, 2*z);
		}
		
	}
	
}
