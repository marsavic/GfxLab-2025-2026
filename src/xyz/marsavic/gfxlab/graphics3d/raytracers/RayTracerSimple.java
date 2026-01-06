package xyz.marsavic.gfxlab.graphics3d.raytracers;

import xyz.marsavic.functions.F0;
import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;


public class RayTracerSimple extends RayTracer {
	
	private static final double EPSILON = 1e-9;
	
	private final int maxDepth;

	public RayTracerSimple(F0<F1<Scene, Double>> ffSceneT, int maxDepth) {
		super(ffSceneT);
		this.maxDepth = maxDepth;
	}
	
	@Override
	public Color sample(Scene scene, Ray ray) {
		return sample(scene, ray, maxDepth);
	}	
	
	public Color sample(Scene scene, Ray ray, int depthRemaining) {
		if (depthRemaining == 0) {
			return Color.BLACK;
		}
		Hit hit = scene.solid().firstHit(ray, EPSILON);  // We start from epsilon to avoid floating-point errors
		if (hit.t() == Double.POSITIVE_INFINITY) {
			return scene.colorBackground();
		}
		
		Vec3 p = ray.at(hit.t());                        // The hit point
		Vec3 n_ = hit.n_();                              // Normalized normal to the body surface at the hit point
		Vec3 i_ = ray.d().inverse().normalized_();       // Incoming direction
		Vec3 r_ = GeometryUtils.reflectedN(n_, i_);      // Reflected ray (i_ reflected over n_)
		Material material = hit.material();
		
		Color lightDiffuse = Color.BLACK;                // The sum of diffuse contributions from all the lights
		Color lightSpecular = Color.BLACK;
		
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);                   // Vector from p to the light

			Ray rayToLight = Ray.pd(p, l);
			if (scene.solid().hitBetween(rayToLight, EPSILON, 1)) continue; // If in shadow
			
			double lLSqr = l.lengthSquared();            // Distance from p to the light squared
			double lL = Math.sqrt(lLSqr);                // Distance from p to the light
			double cosLN = n_.dot(l) / lL;               // Cosine of the angle between l and n_
			
			if (cosLN > 0) {                             // If the light is above the surface
				Color irradiance = light.c().mul(cosLN / lLSqr);
				// The irradiance represents how much light is received by a unit area of the surface. It is
				// proportional to the cosine of the incoming angle and inversely proportional to the distance squared
				// (inverse-square law).
				lightDiffuse = lightDiffuse.add(irradiance);
				
				double cosRN = l.dot(r_) / lL;
				if (cosRN > 0) {
					lightSpecular = lightSpecular.add(
							light.c().mul(Math.pow(cosRN, material.shininess()) / lLSqr)
					);
				}
			}
		}
		
		// When material has reflective properties, we recursively find the color visible along the ray (p, r).
		Color lightReflective = material.reflective().zero() ? 
				Color.BLACK :
				sample(scene, Ray.pd(p, r_), depthRemaining - 1);
		
		Color result = Color.BLACK;
		result = result.add(lightDiffuse   .mul(material.diffuse   ()));
		result = result.add(lightSpecular  .mul(material.specular  ()));
		result = result.add(lightReflective.mul(material.reflective()));
		
		return result;
	}
	
}
