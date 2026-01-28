package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.solids.*;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Numeric;

import java.util.Collection;
import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record GoldTrinket(
		double phiX,
		double phiY,
		Camera camera
) implements FFSceneT {
	
	@Override
	public Solid solid() {
		Color cl = Color.hsb(0.16, 0.5, 0.9);
		Material m = Material.mirror(cl).diffuse(cl.mul(0.02));
		
		Solid sA = Box.$.r(0.66)
				.material(m)
				.transformed(Affine3.IDENTITY
						.then(Affine3.rotationAboutX(phiX))
						.then(Affine3.rotationAboutY(phiY))
				);
		Solid sB = Ball.cr(Vec3.xyz(0, 0, 0), 0.80, m);
		Solid sC = Ball.cr(Vec3.xyz(0, 0, 0), 0.88, m);
		Solid s = CSG.intersection(CSG.difference(sA, sB), sC);
		
		
		F1<Material, Vector> materialUVWalls = uv -> Material.matte((1 + Numeric.cosT(Numeric.sinT(uv.y()) + uv.x())) * (1 + Numeric.cosT(Numeric.sinT(uv.x()) + uv.y())) / 4);
		
		return Group.of(
				HalfSpace.pn(Vec3.xyz(0, 0, 1.6), Vec3.xyz(0, 0, -1), materialUVWalls),
				s
		);
	}
	
	@Override
	public Collection<Light> lights() {
		double d = 0.6;
		return List.of(
				Light.p(Vec3.xyz(-d,  d, -d)),
				Light.p(Vec3.xyz(-d,  d,  d)),
				Light.p(Vec3.xyz( d,  d, -d)),
				Light.p(Vec3.xyz( d,  d,  d)),
				Light.p(Vec3.xyz(-d, -d, -d)),
				Light.p(Vec3.xyz(-d, -d,  d)),
				Light.p(Vec3.xyz( d, -d, -d)),
				Light.p(Vec3.xyz( d, -d,  d))
		);
		
	}
	
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
								e(RayTracerSimple.class,
										e(GoldTrinket.class
												, e(0.1)
												, e(0.1)
												, e(TransformedCamera.class
														, e(Perspective.class, e(1.0 / 3))
														, e(Affine3.IDENTITY
																.then(Affine3.translation(Vec3.xyz(0, 0, -4)))
														)
												)
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
