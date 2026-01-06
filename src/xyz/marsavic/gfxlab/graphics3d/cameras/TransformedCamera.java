package xyz.marsavic.gfxlab.graphics3d.cameras;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Affine3;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.Ray;

public record TransformedCamera(
		Camera camera,
		Affine3 transformation
) implements Camera {
	
	@Override
	public Ray exitingRay(Vector sensorPosition) {
		Ray ray = camera.exitingRay(sensorPosition);
		return ray.transformed(transformation);
	}
}
