package xyz.marsavic.gfxlab.solids;

import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.GeometryUtils;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;


public class HalfSpace implements Solid {
	
	// TODO Homework
	
	/** A half-space defined by a point p on a bounding plane, and a normal vector to the bounding plane. */
	public static HalfSpace pn(Vec3 p, Vec3 n) {
		// dummy
		return new HalfSpace();
	}
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		// dummy
		return Hit.AtInfinity.axisAligned(ray.d(), false);
	}
}
