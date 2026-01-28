package xyz.marsavic.gfxlab.entry_points;

import javafx.embed.swing.SwingFXUtils;
import xyz.marsavic.gfxlab.Animation;
import xyz.marsavic.gfxlab.TransformationFromSize;
import xyz.marsavic.gfxlab.aggregation.AggregatorFrameLast;
import xyz.marsavic.gfxlab.aggregation.EAggregator;
import xyz.marsavic.gfxlab.graphics3d.raytracers.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.scenes.Mirrors;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.statistics.StatisticsWeightDecayedWithAmount;
import xyz.marsavic.time.Timer;
import xyz.marsavic.utils.Hash;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static xyz.marsavic.gfxlab.Vec3.*;
import static xyz.marsavic.reactions.elements.Elements.*;


public class Render {
		
	static void render(ElementF<Animation> animation, String name, int pFrame, int qFrame) throws IOException {
		String directoryPath = "renders/animation/" + name;
		Files.createDirectories(Paths.get(directoryPath));

		Timer timer = new Timer();
		
		StatisticsWeightDecayedWithAmount statistics = new StatisticsWeightDecayedWithAmount(1.0);
		
		for (int iFrame = pFrame; iFrame < qFrame; iFrame++) {
			String fileName = String.format("%s/%08d.png", directoryPath, iFrame);
			
			double time = -timer.getTime();
			var image = UtilsFX.generateImage(animation, iFrame);
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(fileName));
			time += timer.getTime();
			statistics.add(time);
			
			int timeRemaining = (int) (statistics.value() * (qFrame - iFrame - 1));
			
			int timeRemainingS = (timeRemaining) % 60;
			int timeRemainingM = ((timeRemaining) / 60) % 60;
			int timeRemainingH = (((timeRemaining) / 60) / 60);
			
			System.out.format("%08d  %7.2f  %02d:%02d:%02d\n", iFrame, time, timeRemainingH, timeRemainingM, timeRemainingS);
		}
	}
	
	
	static class ParameterReader {
		private final String[] args;
		private int i = 0;
		private final Scanner scanner = new Scanner(System.in);
		
		public ParameterReader(String[] args) {
			this.args = args;
		}
		
		public String next(String name) {
			System.out.print(name + ": ");
			String value;
			if (i < args.length) {
				value = args[i];
				System.out.println(value);
			} else {
				value = scanner.nextLine();
			}
			i++;
			return value;
		}
		
	}
	
	static void main(String[] args) throws IOException {
		ParameterReader parameterReader = new ParameterReader(args);
		int pFrame = Integer.parseInt(parameterReader.next("pFrame"));
		int qFrame = Integer.parseInt(parameterReader.next("qFrame"));
		
		System.out.println(Profiling.infoTextSystem());
		System.out.println();
		render(
				setupMirrors   (), "Mirrors"   , pFrame, qFrame
		);
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
	
}
