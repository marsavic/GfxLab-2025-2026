package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.utils.Hash;


public interface BSDF {
	// Cos term is included
	// Integrated over a hemisphere of unit area
	
	record Result(
			Vec3 out,
			Color color
	) {
		public static final Result ABSORBED = new Result(null, Color.BLACK);
		
		public Result mul(Color k) {
			return new Result(out, color.mul(k));
		}
		
		public Result mul(double k) {
			return new Result(out, color.mul(k));
		}
	}
	
	
	Result sample(Vec3 n_, Vec3 i, Hash hash);
	
	
	
	default BSDF mul(Color color) {
		return (n_, i, hash) -> this.sample(n_, i, hash).mul(color);
	}
	
	
	default BSDF mul(double k) {
		return mul(Color.gray(k));
	}
	
	
	// ===================================================================================================
	// Utility instances and factories.
	
	
	
	/** Interpolates between two bsdfs. */
	static BSDF mix(BSDF bsdf0, BSDF bsdf1, double k) {
		if (k == 0) return bsdf0;
		if (k == 1) return bsdf1;
		
		return (n_, i, hash) ->
				hash.getDouble() < k ?
						bsdf1.sample(n_, i, hash.add(0xADF296FC86CB9676L)) :
						bsdf0.sample(n_, i, hash.add(0x54CA6DACF51FD045L));
	}
	
	
	/** Returns the average of the specified bsdfs.
	 * Weights are used only for importance sampling. The calls to sample would have the same expected value for the same parameters,
	 * independent of the weights.  
	 */
	static BSDF importanceAverage(BSDF[] bsdfs, double[] importances) {
		// TODO Split into two functions - one that "cleans" the array from those having zero importance, and the other computing the avg of all given bsdfs.
		if (bsdfs.length != importances.length) {
			throw new IllegalArgumentException();
		}
		
		double sum = 0;
		int m = 0;
		for (double w : importances) {
			if (w > 0) {
				m++;
				sum += w;
			}
		}
		
		if (m == 0) return BSDF.ABSORPTIVE;
		
		BSDF[] bsdfs_ = new BSDF[m];
		double[] weights_ = new double[m];
		m = 0;
		for (int j = 0; j < bsdfs.length; j++) {
			double w = importances[j];
			if (w > 0) {
				bsdfs_[m] = bsdfs[j];
				weights_[m] = importances[j] / sum;
				m++;
			}
		}
		
		// Optimizations:
		if (m == 1) return bsdfs_[0];
		
		
		return (n_, i, hash) -> {
			double u = hash.getDouble();
			double s = 0;
			int j = 0;
			while (s <= u) {
				s += weights_[j++];
			}
			j--;
			
			return bsdfs_[j].sample(n_, i, hash.add(0)).mul(1.0 / weights_[j]);
		};
	}
	
	
	
	
	BSDF ABSORPTIVE = (n_, i, hash) -> Result.ABSORBED;
	
	BSDF REFLECTIVE = (n_, i, hash) -> new Result(GeometryUtils.reflected(n_, i), Color.WHITE);
	
	BSDF TRANSMISSIVE = transmissive(Color.WHITE);
	
	
	static BSDF transmissive(Color c) {
		return (n_, i, hash) -> new Result(i.inverse(), c);
	}
	
	
	static BSDF diffuse(Color c) {
		return (n_, i, hash) -> new Result(Sampling.directionCosineDistributedN(n_, hash), c);
		
/*
//		Alternative, using uniform sampling
		return (n_, i, hash) -> {
			Vec3 out = Sampling.hemisphereUniform_(n_, hash);
			return new Result(out, c.mul(out.dot(n_) * 2));
		};
*/
	}
	
	
	static BSDF diffuse(double k) {
		return diffuse(Color.gray(k));
	}
	
	
	static BSDF reflective(double k) {
		return reflective(Color.gray(k));
	}
	
	
	static BSDF reflective(Color c) {
		return (n_, i, hash) -> new Result(GeometryUtils.reflectedN(n_, i), c);
	}
	

	static BSDF refractive(double refractiveIndex) {
		return refractive(1.0, refractiveIndex);
	}
	
	
	static BSDF refractive(double k, double refractiveIndex) {
		return refractive(Color.gray(k), refractiveIndex);
	}
	
	
	static BSDF refractive(Color c, double refractiveIndex) {
		// TODO: Fresnel coefficients for the ratio between the refracted and the reflected part.

		return (n_, i, hash) -> new Result(GeometryUtils.refractedNN(n_, i.normalized_(), refractiveIndex), c);  // TODO Maybe use GeometryUtils.refractedN, without normalizing, to avoid one sqrt.
	}
	
	
}
