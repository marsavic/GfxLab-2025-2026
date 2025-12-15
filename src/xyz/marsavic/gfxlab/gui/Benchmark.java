package xyz.marsavic.gfxlab.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import xyz.marsavic.gfxlab.playground.GfxLab;
import xyz.marsavic.testing.Testing;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Benchmark {
	
	// Use AggregatorFixed when benchmarking.
	
	private static Image generateImage() {
		var sink = GfxLab.setup();
		
		var rMI = sink.result().at(0);
		Image image = rMI.f(UtilsGL::writeMatrixToImage);
		rMI.release();
		return image;
	}
	
	
	static void main() throws IOException {
		var image = Testing.run(Benchmark::generateImage);		
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
		String fileName = String.format("%s %7.2f.png", timestamp, image.runningTime());
		ImageIO.write(SwingFXUtils.fromFXImage(image.result(), null), "png", new File(fileName));
	}
	
}
