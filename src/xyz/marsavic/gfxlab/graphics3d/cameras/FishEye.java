package xyz.marsavic.gfxlab.graphics3d.cameras;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.Ray;

public record FishEye(
		double k
) implements Camera {
	
	@Override
	public Ray exitingRay(Vector sensorPosition) {
		double phi = sensorPosition.angle();
		double r = sensorPosition.length();
		double theta = k * r;
		
		Vector v = Vector.polar(1, theta);
		
		return Ray.pd(Vec3.ZERO, Vec3.zp(v.x(), Vector.polar(v.y(), phi)));
	}
}
