package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.Camera;
import xyz.marsavic.gfxlab.graphics3d.FFSceneT;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.gfxlab.graphics3d.cameras.ThinLensFOV;
import xyz.marsavic.gfxlab.graphics3d.cameras.TransformedCamera;
import xyz.marsavic.gfxlab.graphics3d.raytracers.PathTracerIterative;
import xyz.marsavic.gfxlab.graphics3d.solids.Box;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.random.sampling.Sampler;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import java.util.ArrayList;
import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record CityOfNight(
	int n,
	Hash hash,
	Camera camera		
) implements FFSceneT {
	
	public Solid solid() {
		List<Solid> solids = new ArrayList<>();
		Sampler sampler = new Sampler(hash);
		
		List<xyz.marsavic.geometry.Box> boxes = new ArrayList<>();
		
		int nTrials = 0;
		
		while (boxes.size() < n && nTrials < 10000) {
			nTrials++;
			Vector c = sampler.randomInBox(xyz.marsavic.geometry.Box.cr(3));
			Vector r = Vector.xy(sampler.uniform(0.05, 0.5), sampler.uniform(0.05, 0.5));
			xyz.marsavic.geometry.Box bCandidate = xyz.marsavic.geometry.Box.cr(c, r);
			
			if (boxes.stream().noneMatch(b -> b.intersects(bCandidate))) {
				boxes.add(bCandidate.grow(0.05));
				
				Material material =
						sampler.uniform() < 0.1 ?
								Material.LIGHT :
								sampler.uniform() < 0.2 ?
										Material.mirror(0.75) :
										Material.matte(Color.okhcl(sampler.uniform(), 0.1, 0.8));
										
				
				double h = sampler.exponential(1);
				solids.add(Box.$.pd(Vec3.yp(0, bCandidate.p()), Vec3.yp(h, bCandidate.d())).material(material));
				
				nTrials = 0;
			}
		}
		
		solids.add(HalfSpace.pn(Vec3.ZERO, Vec3.EY, Material.matte(0.8)));
		
		return Group.of(solids);
	}
	
	
/*
	@Override
	public F1<Camera, Double> fCameraT() {
		ThinLensFOV source = new ThinLensFOV(1.0 / 3, 7.0, 0.1);
		Affine3 transformationStart = Affine3.IDENTITY.then(Affine3.translation(xyz(0, 0, -7)));
		
		return t -> 
				new TransformedCamera(
						source,
						transformationStart
								.then(Affine3.rotationAboutX(-Numeric.cosT(t) * 0.10 + 0.12))
								.then(Affine3.rotationAboutY(t))
				);
	}
*/
	
	// ================================================================================================================

	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFrameLast::new),
//								e(AggregatorOnDemand::new),
								e(PathTracerIterative.class,
										e(CityOfNight.class,
												e(50),
												e(Hash.class, e(0xE425CDF40718EA9AL)),
//												e(Hash.class, e(0xE425CDF40718EAA0L)),
												e(TransformedCamera.class,
														e(ThinLensFOV.class, e(1.0 / 3), e(7.0), e(0.1)),
														e((x, y) -> Affine3.IDENTITY
																.then(Affine3.translation(Vec3.xyz(0, 0, -7)))
																.then(Affine3.rotationAboutX(x))
																.then(Affine3.rotationAboutY(y)),
																e(0.12), e(-0.1)
														)
												)										),
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
