package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.Affine3;
import xyz.marsavic.gfxlab.Animation;
import xyz.marsavic.gfxlab.TransformationFromSize;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.aggregation.AggregatorFixed;
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
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;

public record Oven (
		Camera camera
) implements FFSceneT {
	
	@Override
	public Solid solid() {
		Ball walls = Ball.cr(ZERO, -1000.0).material(Material.LIGHT);
		Ball cabbage = Ball.cr(ZERO, 1.0).material(Material.MATTE);
		return Group.of(walls, cabbage);
	}
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFixed.constructor(1)),
								e(PathTracerIterative.class,
										e(Oven.class,
											e(TransformedCamera.class,
													e(Perspective.class, e(1.0 / 3)),
													e(Affine3.chain(
															Affine3.translation(Vec3.xyz(0, 0, -4))
													))
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
