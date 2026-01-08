package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorOneAhead;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Numeric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record NewYearsParty(
	int nBalls,
	int nLights,
	Hash hash
) implements FFSceneT {

	private static final int a = 5;
	private static final int b = 2;
	
	
	@Override
	public Solid solid() {
		List<Solid> solids = new ArrayList<>();

		for (int i = 0; i < nBalls; i++) {
			Color color = Color.hsb(0.4, 0.8, 0.5);
			F1<Material, Vector> fMaterial = uv -> Material.matte(
					color.mul(Math.min(
							Numeric.mod(uv.dot(Vector.xy(a, b))), 
							Numeric.mod(uv.dot(Vector.xy(-a, b)))
							) < 0.2 ? 1.0 : 0.8										
					)
			);
			
			double k = (double) i / nBalls;
			double m = (1-k)*(1-k);
			Vec3 p = yp(m, Vector.polar(k / 2, k * 37))
					.mul(4)
					.add(xyz(0, -1.5, 0));			
			Ball b = Ball.cr(p, 0.3 * (0.5 + 0.5 * (1-m)), fMaterial);
			solids.add(b);
		}
		
		solids.add(Ball.cr(xyz(0, 0, 0), -6, Material.matte(Color.hsb(0.8, 0.3, 0.07))));
		
		solids.add(Ball.cr(xyz(0, 2.8, 0), 0.2, Material.matte(Color.hsb(0.16, 0.8, 1.0))));
		
		return Group.of(solids);
	}
	
	
	@Override
	public F1<Collection<Light>, Double> fLightsT() {
		return t -> {
			List<Light> lights = new ArrayList<>();
			
			lights.add(Light.pc(xyz(0, 2.5, -1.5), Color.gray(1)));
			
			for (int i = 0; i < nLights; i++) {
				double k = (double) i / nLights;
				Vec3 p = yp(Numeric.sinT(3 * t), Vector.polar(4, 2 * t + k));
				lights.add(Light.pc(p, Color.gray(3.6)));
			}
			
			return lights;
		};
	}
	
	
	@Override
	public F1<Camera, Double> fCameraT() {
		return t -> new TransformedCamera(
				Perspective.DEFAULT,
				Affine3.chain(
						Affine3.translation(Vec3.xyz(0, 0, -4)),
						Affine3.rotationAboutY(t)
				)
		);
	}
	
	
	// ================================================================================================================
	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorOneAhead::new),
								e(RayTracerSimple.class,
										e(NewYearsParty.class
												, e(51)
												, e(3)
												, e(Hash.class, e(0xAC43D7AA071209A6L))
										),
										e(16)
								),
								e(TransformationFromSize.ToGeometricT0_.class),
								e(Vec3.xyz(360, 640, 640)),
								e(false),								
								e(false),								
								e(Hash.class, e(0x8EE6B0C4E02CA7B2L))
						),
						e(ToneMapping2.class,
								e(AutoSoft.class, e(0x1p-4), e(1.0))
						)
				);
	}
	
}
