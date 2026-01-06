package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
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
import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.e;


public record Mirrors(
	int nBalls,
	double k
) implements FFSceneT {
	
	@Override
	public Solid solid() {
		var materialUVWalls  = Grid.standardQuarter(Color.WHITE);
		var materialUVWallsL = Grid.standardQuarter(Color.hsb(0.00, 0.5, 1.0));
		var materialUVWallsR = Grid.standardQuarter(Color.hsb(0.33, 0.5, 1.0));
		
		Collection<Solid> solids = new ArrayList<>();
		Collections.addAll(solids,
				HalfSpace.pn(Vec3.xyz(-1,  0,  0), Vec3.xyz( 1,  0,  0), materialUVWallsL),
				HalfSpace.pn(Vec3.xyz( 1,  0,  0), Vec3.xyz(-1,  0,  0), materialUVWallsR),
				HalfSpace.pn(Vec3.xyz( 0, -1,  0), Vec3.xyz( 0,  1,  0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz( 0,  1,  0), Vec3.xyz( 0, -1,  0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz( 0,  0,  1), Vec3.xyz( 0,  0, -1), materialUVWalls)
		);
		
		for (int i = 0; i < nBalls; i++) {
			Vector c = Vector.polar(0.5, 1.0 * i / nBalls);
			Ball ball = Ball.cr(Vec3.zp(0, c), 0.4, uv -> Material.MIRROR);
			solids.add(ball);
		}
		
		return Group.of(solids);
	}
	
	
	@Override
	public Collection<Light> lights() {
		return List.of(
				Light.pc(Vec3.xyz(-0.8, 0.8, -0.8), Color.WHITE),
				Light.pc(Vec3.xyz(-0.8, 0.8,  0.8), Color.WHITE),
				Light.pc(Vec3.xyz( 0.8, 0.8, -0.8), Color.WHITE),
				Light.pc(Vec3.xyz( 0.8, 0.8,  0.8), Color.WHITE)
		);		
	}
	
	@Override
	public Camera camera() {
		return new TransformedCamera(
				new Perspective(k),
				Affine3.translation(Vec3.xyz(0, 0, -3))
		);
	}
	
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
								e(RayTracerSimple.class,
										e(Mirrors.class
												, e(3)
												, e(0.16)
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
