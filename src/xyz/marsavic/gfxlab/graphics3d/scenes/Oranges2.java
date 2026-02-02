package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.FFSceneT;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.PathTracerIterative;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Numeric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record Oranges2(
		Camera base,
		double phiY
) implements FFSceneT {
	
	public Solid solid() {
		Collection<Solid> solids = new ArrayList<>();

		int n = 4;
		double sqrt3 = Math.sqrt(3);
		
		Vec3 dI = xyz(1.0, 0.0, 0.0);
		Vec3 dJ = xyz(0.5, 0.0, sqrt3/2);
		Vec3 dK = xyz(0.5, Math.sqrt(2.0/3.0), 1.0/(2*sqrt3));
		
		Vec3 o = dI.add(dJ.add(dK)).mul((n-1)/4.0);
		
		double d = 1.6 / n;
		
		Material mMatte = Material.matte(0.7);

		for (int i = 0; i < n; i++) {
			for (int j = 0; i+j < n; j++) {
				for (int k = 0; i+j+k < n; k++) {
					Vec3 c = dI.mul(i).add(dJ.mul(j)).add(dK.mul(k)).sub(o).mul(d).sub(Vec3.EY.mul(1-d/2-d*o.y()));
					solids.add(Ball.cr(c, d/2,
							v -> Numeric.mod(v.dot(Vector.xy(3, 2))) < 0.2 ? Material.light(Color.okhcl(v.y(), 0.12, 0.75)) : mMatte
					));
				}
			}
		}
		
		Collections.addAll(solids,
				HalfSpace.pn(xyz( 0, -1,  0), xyz( 0,  1,  0), mMatte)
		);
		
		return Group.of(solids);		
	}
	
	
	@Override
	public F1<Camera, Double> fCameraT() {
		Camera camera = new TransformedCamera(
				base,
				Affine3.IDENTITY
						.then(Affine3.rotationAboutX(0.025))
						.then(Affine3.translation(xyz(0, 0, -3)))
						.then(Affine3.rotationAboutX(0.04))
		);
		
		return t -> new TransformedCamera(
				camera,
				Affine3.rotationAboutY(phiY + Math.exp(t))
		);
	}

	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
								e(PathTracerIterative.class,
										e(Oranges2.class,
												e(Perspective.class, e(1.0 / 3)),
												e(0.11)
										),
										e(16)
								),
								e(TransformationFromSize.ToGeometricT0_.class),
								e(xyz(1, 640, 640)),
								e(true),								
								e(false),								
								e(Hash.class, e(0x8EE6B0C4E02CA7B2L))
						),
						e(ToneMapping2.class,
								e(AutoSoft.class, e(0x1p-4), e(1.0))
						)
				);
	}
	
}
