package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.functions.F0;
import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction2;
import xyz.marsavic.gfxlab.ColorFunction3;


public abstract class RayTracer implements ColorFunction3 {
	
	private final F1<Scene, Double> fScene;

	
	public RayTracer(F0<F1<Scene, Double>> ffSceneT) {
		fScene = ffSceneT.at();
	}
	
	
	protected abstract Color sample(Scene scene, Ray ray);
	
	
	public Color at(Scene scene, Vector p) {
		Ray ray = scene.camera().exitingRay(p);
		return sample(scene, ray);
	}
	
	
	@Override
	public Color at(double t, Vector p) {
		return at(fScene.at(t), p);
	}
	
	
	@Override
	public ColorFunction2 sliceAt(double t) {
		Scene scene = fScene.at(t);
		return p -> at(scene, p);
	}
	
}
