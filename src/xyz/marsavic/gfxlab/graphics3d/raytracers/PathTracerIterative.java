package xyz.marsavic.gfxlab.graphics3d.raytracers;

import xyz.marsavic.functions.F0;
import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.utils.Hash;


public class PathTracerIterative extends RayTracer {
	
	private static final Hash HASH = new Hash(0x724C5708B51B8BBAL);
	private static final double EPSILON = 1e-9;
	
	private final int maxDepth;
	
	
	public PathTracerIterative(F0<F1<Scene, Double>> ffScene, int maxDepth) {
		super(ffScene);
		this.maxDepth = maxDepth;
	}
	
	
	@Override
	protected Color sample(Scene scene, Ray ray) {
		return radiance(scene.solid(), ray);
	}
	
	
	private Color radiance(Solid solid, Ray ray) {
		Hash hash = HASH.add(ray);

		Color result = Color.BLACK;
		Color m = Color.WHITE;
		
		int depth = 0;
		
		while (depth < maxDepth) {
			Hit hit = solid.firstHit(ray, EPSILON);
			
			Material material = hit.material();
			result = result.add(material.emittance().mul(m));
			
			if (hit.t() == Double.POSITIVE_INFINITY) {
				break;
			}

			Vec3 i = ray.d().inverse();                 // Incoming direction
			Vec3 n_ = hit.n_();                         // Normalized normal to the body surface at the hit point
			
			BSDF.Result bsdfResult = material.bsdf().sample(n_, i, hash.add(depth));
			
			m = bsdfResult.color().mul(m);
			if (m.zero()) {
				break;
			}

			Vec3 p = ray.at(hit.t());               // Point of collision
			ray = Ray.pd(p, bsdfResult.out());
			
			depth++;
		}
//		if (depth < maxDepth - 1) result = Color.BLACK; // To see only the contribution of rays of the length equal to maxDepth. 
		return result;
	}
	
}
