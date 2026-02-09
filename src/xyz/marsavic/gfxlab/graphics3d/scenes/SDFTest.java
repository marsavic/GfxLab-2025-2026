package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Affine3;
import xyz.marsavic.gfxlab.Animation;
import xyz.marsavic.gfxlab.TransformationFromSize;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.FFSceneT;
import xyz.marsavic.gfxlab.graphics3d.Light;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.solids.SDF;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import java.util.Collection;
import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;

public record SDFTest (
		double x,
		double r,
		double phiX,
		double phiY,
		double k
) implements FFSceneT {
	
	@Override
	public Solid solid() {
		return
				SDF.smoothUnion(
						SDF.ball(Vec3.xyz(x, 0, 0), 1.0),
						SDF.box(EXYZ),
						r
				);
	}
	
	
	@Override
	public Collection<Light> lights() {
		return List.of(
				Light.p(Vec3.xyz(-8, 3, -10))
		);
	}
	
	@Override
	public Camera camera() {
		return new TransformedCamera(
				new Perspective(k),
				Affine3.chain( 
						Affine3.translation(Vec3.xyz(0, 0, -6)),
						Affine3.rotationAboutX(phiX),		
						Affine3.rotationAboutY(phiY)		
				)
		);				
	}
	
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
								e(RayTracerSimple.class,
										e(SDFTest.class,
											e(-0.5),
											e(0.2),
											e(0.06),
											e(0.11),
											e(1 / 3.0)
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
