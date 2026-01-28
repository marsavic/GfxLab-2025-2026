package xyz.marsavic.gfxlab.graphics3d.cameras;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.random.sampling.Sampler;
import xyz.marsavic.utils.Hash;


public class ThinLens implements Camera {
	
	private final double focalDistance, lensRadius;
	
	private final Hash hash = new Hash(0x3D8DFF56C147DB5EL);
	
	
	
	public ThinLens(double focalDistance, double lensRadius) {
		this.focalDistance = focalDistance;
		this.lensRadius = lensRadius;
	}
	
	
	@Override
	public Ray exitingRay(Vector sensorPosition) {
		Vec3 q = Vec3.zp(1.0, sensorPosition).mul(focalDistance);
		Vec3 p = Vec3.zp(0, new Sampler(hash.add(sensorPosition)).randomInDisk(lensRadius));
		return Ray.pq(p, q);
	}
	
}
