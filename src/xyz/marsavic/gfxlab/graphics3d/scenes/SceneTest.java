package xyz.marsavic.gfxlab.graphics3d.scenes;

import javafx.scene.transform.Affine;
import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
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
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Numeric;

import java.util.List;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public record SceneTest(
	double lightZ,
	int a,
	int b,
	double fov,
	double tX,
	double tY,
	double tZ,
	double phiX,
	double phiY,
	double phiZ
) implements FFSceneT {
	
	@Override
	public F1<Solid, Double> fSolidT() {
		return t -> {
			Ball ball1 = Ball.cr(xyz(-1, 0, 3), 2,
//					uv -> Material.matte(
//							Numeric.mod(uv.dot(Vector.xy(a, b))) < 0.1 ||
//									Numeric.mod(uv.dot(Vector.xy(-a, b))) < 0.1 ?
//									Color.hsb(0.3, 0.8, 1.0) :
//									Color.hsb(0.0, 0.0, 0.2)
//					).specular(Color.WHITE).shininess(16)
					Material.mirror(0.8)
			);
			
			Ball ball2 = Ball.cr(xyz(Numeric.sinT(t * 30), 0, 3), 1, Material.matte(Color.hsb(1.0 / 3, 0.8, 1.0)));
			
			HalfSpace halfSpace = HalfSpace.pn(xyz(0, -2, 0), xyz(0, 1, 0),
					uv -> Material.matte(uv.add(Vector.xy(0.05, 0.05)).mod().min() < 0.1 ? 0.8 : 1.0)
			);
			
			return Group.of(
					ball1,
					ball2,
					halfSpace
			);
		};
	}
	
	
	@Override
	public List<Light> lights() {
		return List.of(
				Light.pc(xyz(2, 2, -5 + lightZ * 20), Color.hsb(0.7, 0.8, 10.0)),
				Light.pc(ZERO, Color.gray(0.2))		
		);
	}


		
	@Override
	public Camera camera() {
		return new TransformedCamera(
				Perspective.fov(fov),
				Affine3.IDENTITY
						.then(Affine3.translation(xyz(tX, tY, tZ).mul(20).sub(EXYZ.mul(10))))
						.then(Affine3.rotationAboutX(phiX))
						.then(Affine3.rotationAboutY(phiY))
						.then(Affine3.rotationAboutZ(phiZ))
		);
	}
	
	
	// ================================================================================================================
	
	public static ElementF<Animation> setup() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorOneAhead::new),
								e(RayTracerSimple.class,
									e(SceneTest.class
											, e(0.0)
											, e(15)
											, e(4)
											, e(0.25)
											, e(0.5), e(0.5), e(0.5)
											, e(0.0), e(0.0), e(0.0)
									),
									e(64)
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
