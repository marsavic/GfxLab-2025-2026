package xyz.marsavic.gfxlab.graphics3d.cameras;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.Ray;


public record Orthographic (
		double k
) implements Camera {
	
	public Orthographic() {
		this(1.0);
	}
	
	
	@Override
	public Ray exitingRay(Vector sensorPosition) {
		return Ray.pd(
				Vec3.zp(0.0, sensorPosition.mul(k)),
				Vec3.EZ
		);
	}
	
}
