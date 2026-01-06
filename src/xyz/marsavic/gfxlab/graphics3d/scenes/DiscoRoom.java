package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorOneAhead;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracing.RayTracerSimple;
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
import java.util.random.RandomGenerator;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;
import static xyz.marsavic.reactions.elements.Elements.e;


public record DiscoRoom(
	int nBalls,
	int nLights,
	Hash hash,
	double shininess
) implements FFSceneT {
	
	
	@Override
	public Solid solid() {
		RandomGenerator rngBalls = hash.rng();
		
		var materialUVWalls = Grid.standardQuarter(Color.WHITE);
		
		Collection<Solid> solids = new ArrayList<>();
		Collections.addAll(solids,
				HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz(1, 0, 0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz(1, 0, 0), Vec3.xyz(-1, 0, 0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz(0, -1, 0), Vec3.xyz(0, 1, 0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz(0, 1, 0), Vec3.xyz(0, -1, 0), materialUVWalls),
				HalfSpace.pn(Vec3.xyz(0, 0, 1), Vec3.xyz(0, 0, -1), materialUVWalls)
		);
		
		for (int i = 0; i < nBalls; i++) {
			double hue = rngBalls.nextDouble();
			Material material = rngBalls.nextDouble() < 0.8 ?
					Material.matte(Color.hsb(hue, 0.9, 0.9)).specular(Color.WHITE).shininess(shininess*100) :
					Material.MIRROR;
			
			solids.add(Ball.cr(Vec3.random(rngBalls).ZOtoMP(), 0.2, material));
		}
		
		return Group.of(solids);
	}
	
	
	@Override
	public Collection<Light> lights() {
		RandomGenerator rngLights = hash.add(0xE43B05F4302C4B1BL).rng();
		List<Light> lights = new ArrayList<>();
		for (int i = 0; i < nLights; i++) {
			lights.add(Light.pc(
					Vec3.random(rngLights).ZOtoMP(),
					Color.hsb(rngLights.nextDouble(), 0.75, 1))
			);
		}
		return lights;
	}
	

	@Override
	public Camera camera() {
		return new TransformedCamera(
				new Perspective(1.0/3),
				Affine3.IDENTITY
						.then(Affine3.translation(xyz(0, 0, -4)))
		);
	}
	
	
	
	// ================================================================================================================
	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorOneAhead::new),
								e(RayTracerSimple.class,
									e(DiscoRoom.class
											, e(16)
											, e(16)
											, e(Hash.class, e(0xF1A423D6167D9818L))
											, e(0.16)
									),
									e(16)
								),
								e(TransformationFromSize.ToGeometricT0_.class),
								e(xyz(360, 640, 640)),
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
