package xyz.marsavic.gfxlab.entry_points;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.AggregatorFixed;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.scenes.Mirrors;
import xyz.marsavic.gfxlab.playground.colorfunctions.Black;
import xyz.marsavic.gfxlab.tonemapping.ColorTransform;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.colortransforms.Identity;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.testing.Benchmarking;
import xyz.marsavic.utils.Hash;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public class Benchmark {
	
	// When benchmarking:
	// - Use AggregatorFixed.constructor(N), where N is number of iterations. Set it  
	// - Set power profile: performance (on power)
	// - Close browsers
	
	static void benchmark(ElementF<Animation> animation, String description, boolean saveToFile) throws IOException {
		Image[] image = new Image[1];
		
		var result = Benchmarking.run(1, () -> {
			Rr<Matrix<Integer>> rMI = animation.result().at(0);
			Vector size = rMI.f(Array2::size);
			
			if (saveToFile) {
				image[0] = rMI.f(UtilsFX::writeArray2ToImage);
			}
			
			rMI.release();
			return size;
		});
		result.print();
		
		
		if (saveToFile) {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
			String directoryPath = "renders/benchmarks";
			Files.createDirectories(Paths.get(directoryPath));
			String fileName = String.format("%s/%s %7.2f %s.png", directoryPath, timestamp, result.stats().min(), description);
			ImageIO.write(SwingFXUtils.fromFXImage(image[0], null), "png", new File(fileName));		
		}
	}


	static void main() throws IOException {
		
		int i = 0;
		//noinspection InfiniteLoopStatement
		while (true) {
			benchmark(
					setupMirrors(), "Mirrors"
					
					, i == 0
//					, false
			);
			i++;
		}
	}
	
	
	// ============================================================================================
	

	public static ElementF<Animation> setupMirrors() {
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
								e(xyz(960, 640, 640)),
								e(true),								
								e(false),								
								e(Hash.class, e(0x8EE6B0C4E02CA7B2L))
						),
						e(ToneMapping2.class,
								e(AutoSoft.class, e(0x1p-4), e(1.0))
						)
				);
	}
	
	
	
	public static ElementF<Animation> setupBlack() {
		return
				e(ToneMapping3.class,
						new EAggregator(
								e(AggregatorFixed.constructor(16384)),
	                            e(Black.class
						        ),
								e(TransformationFromSize.ToIdentity_.class),
								e(Vec3.xyz(1, 640, 640)),
								e(true),								
								e(false),								
								e(Hash.class, e(0x8EE6B0C4E02CA7B2L))
						),
						e(ToneMapping2.class,
								e(ColorTransform::asColorTransformFromMatrixColor, e(Identity.class))
						)
				);
	}
	
	
}
