package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.PathTracerIterative;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.graphics3d.textures.Grid;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record GITest(
		Camera camera,
		double yGlassSphere
) implements FFSceneT {
	
	@Override
	public Solid solid() {
		var mL = Grid.standardQuarter(Color.hsb(0.0 / 3, 0.5, 0.7));
		var mR = Grid.standardQuarter(Color.hsb(1.0 / 3, 0.5, 0.7));
		var mW = Grid.standardQuarter(Color.gray(0.7));
		
		Material glass = new Material(BSDF.mix(BSDF.refractive(1.9), BSDF.REFLECTIVE, 0.05));
		
		Collection<Solid> solids = new ArrayList<>();
		Collections.addAll(solids,
				HalfSpace.pn(Vec3.xyz(-1,  0,  0), Vec3.xyz( 1,  0,  0), mL),
				HalfSpace.pn(Vec3.xyz( 1,  0,  0), Vec3.xyz(-1,  0,  0), mR),
				HalfSpace.pn(Vec3.xyz( 0, -1,  0), Vec3.xyz( 0,  1,  0), mW),
				HalfSpace.pn(Vec3.xyz( 0,  1,  0), Vec3.xyz( 0, -1,  0), Material.LIGHT),
				HalfSpace.pn(Vec3.xyz( 0,  0,  1), Vec3.xyz( 0,  0, -1), mW),
				
				Ball.cr(Vec3.xyz(-0.2, yGlassSphere,  0.0), 0.3, glass),
				Ball.cr(Vec3.xyz( 0.5, -0.5, -0.3), 0.3, Material.MIRROR),
				Ball.cr(Vec3.xyz( 0.0,  0.2,  0.0), 0.2, Material.matte(0.7)),
				Ball.cr(Vec3.xyz(-0.4,  0.5,  0.1), 0.2, Material.mirror(0.9))
		);
		
		return Group.of(solids);		
	}
	
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
								e(PathTracerIterative.class,
										e(GITest.class,
											e(TransformedCamera.class,
													e(Perspective.class, e(1.0 / 3)),
													e(Affine3.IDENTITY
															.then(Affine3.translation(Vec3.xyz(0, 0, -4)))
													)
											),
											e(-0.5)
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
