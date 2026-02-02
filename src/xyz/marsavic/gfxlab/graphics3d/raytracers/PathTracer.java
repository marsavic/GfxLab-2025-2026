package xyz.marsavic.gfxlab.graphics3d.raytracers;

import xyz.marsavic.functions.F0;
import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.utils.Hash;


public class PathTracer extends RayTracer {
	
	private static final Hash HASH = new Hash(0xDC10BE5196ABD097L);
	private static final double EPSILON = 1e-9;
	
	private final int maxDepth;
	
	
	public PathTracer(F0<F1<Scene, Double>> ffSceneT, int maxDepth) {
		super(ffSceneT);
		this.maxDepth = maxDepth;
	}
	

	@Override
	protected Color sample(Scene scene, Ray ray) {
		return radiance(scene.solid(), ray, maxDepth, HASH.add(ray));
	}
	
	
	private Color radiance(Solid solid, Ray ray, int depthRemaining, Hash hash) {
		if (depthRemaining <= 0) return Color.BLACK;
		
		Hit hit = solid.firstHit(ray, EPSILON);
		Material material = hit.material();
		Color result = material.emittance();
		
		if (hit.t() != Double.POSITIVE_INFINITY) {
			Vec3 i = ray.d().inverse();                 // Incoming direction
			Vec3 n_ = hit.n_();                         // Normalized normal to the body surface at the hit point
			BSDF.Result bsdfResult = material.bsdf().sample(n_, i, hash);
			
			if (bsdfResult.color().notZero()) {
				Vec3 p = ray.at(hit.t());               // Point of collision
				Ray rayScattered = Ray.pd(p, bsdfResult.out());
				Color rO = radiance(solid, rayScattered, depthRemaining - 1, hash.add(0));
				Color rI = rO.mul(bsdfResult.color());
				result = result.add(rI);
			}
		}
		
		return result;
	}
	
}
