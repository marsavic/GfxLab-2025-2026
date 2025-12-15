package xyz.marsavic.gfxlab.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import xyz.marsavic.functions.A0;
import xyz.marsavic.functions.A1;
import xyz.marsavic.functions.A2;
import xyz.marsavic.functions.F0;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.resources.BorrowManagerMap;
import xyz.marsavic.time.Profiler;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;


public class UtilsGL {
	
	private static final Set<Profiler> profilers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
	
	
	public static Profiler profiler(Object object, String description) {
		String name =
				String.format("%08x", System.identityHashCode(object)) +
						" " +
						object.getClass().getSimpleName() +
						" " +
						description;
		
		Profiler profiler = new Profiler(name);
		profilers.add(profiler);
		return profiler;
	}
	
	
	/**
	 * Live profilers, but not a live collection.
	 * The returned collection is immutable and contains only the profilers present at the moment of calling.
	 */
	public static Collection<Profiler> profilers() {
		synchronized (profilers) {
			return List.of(profilers.toArray(Profiler[]::new));
		}
	}
	
	
	// JavaFX helpers
	
	static final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	
	
	public static Vector imageSize(Image image) {
		return Vector.xy(image.getWidth(), image.getHeight());
	}
	
	
	public static WritableImage createWritableImage(Vector size) {
		return new WritableImage(size.xInt(), size.yInt());
	}
	
	
	public static Image writeMatrixToImage(Array2<Integer> inArray2) {
		WritableImage image = createWritableImage(inArray2.size());
		writeMatrixToImage(image, inArray2);
		return image;
	}
	
	
	public static void writeMatrixToImage(WritableImage outImage, Array2<Integer> inArray2) {
		int sx = inArray2.size().xInt();
		int sy = inArray2.size().yInt();
		
		if (inArray2 instanceof MatrixInts m) {
			outImage.getPixelWriter().setPixels(0, 0, sx, sy, pixelFormat, m.array(), 0, sx);
		} else {
			matricesInt.obtain(inArray2.size(), true, m -> {
				m.copyFrom(inArray2);
				outImage.getPixelWriter().setPixels(0, 0, sx, sy, pixelFormat, ((MatrixInts) m).array(), 0, sx);
			});
		}
	}
	
	
	public static void saveImageToFileWithDialog(Image image) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export Image");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.png"),
				new FileChooser.ExtensionFilter("All Files", "*.*")
		);
		File file = fileChooser.showSaveDialog(null);
		
		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	public static void copyImageToClipboard(Image image) {
		ClipboardContent content = new ClipboardContent();
		content.putImage(image);
		Clipboard.getSystemClipboard().setContent(content);
	}
	
	
	// --- Parallel utils -------
	
	
	private static final ExecutorService pool;           // Consider changing the type to ForkJoinPool to be able to change the parallelism in real time.
	
	
	public static <T> T futureGet(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static <R> R trace(F0<R> function) {
		try {
			return function.at();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void trace(A0 action) {
		try {
			action.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static <V> Future<V> submitTask(F0<V> f) {
		return pool.submit(() -> trace(f));
	}
	
	public static Future<?> submitTask(A0 a) {
		return pool.submit(() -> trace(a));
	}
	
	public static void submitTaskAndWait(A0 a) {
		Future<?> future = submitTask(a);
		futureGet(future);
	}
	
	public static final int parallelism;	
	
	static {
//		int p = ForkJoinPool.getCommonPoolParallelism() + 1;
		int p = (ForkJoinPool.getCommonPoolParallelism() + 1) / 2;
		p = Math.max(1, p);
//		p = 1;
/*
		try {
			boolean obsRunning = false;
			obsRunning |= ProcessHandle.allProcesses().anyMatch(ph -> ph.info().command().orElse("").contains("obs64")); // Windows
			obsRunning |= !Utils.runCommand("top -b -n 1 | grep \" obs\"").isEmpty(); // Linux
			obsRunning |= true;
			if (obsRunning) {
				p -= 3;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
*/
		parallelism = p;
		pool = new ForkJoinPool(parallelism);
	}
	
	
	public static void parallel(int iFrom, int iTo, IntConsumer action) {
		submitTaskAndWait(() -> IntStream.range(iFrom, iTo).parallel().forEach(action));
	}
	
	public static void parallel(int iTo, IntConsumer action) {
		parallel(0, iTo, action);
	}
	
	public static void parallelY(Vector size, IntConsumer action) {
		parallel(size.yInt(), action);
	}
	
	public static void parallelY(Vector size, A2<Integer, Integer> action) {
		parallelY(size, y -> {
			for (int x = 0; x < size.xInt(); x++) {
				action.execute(x, y);
			}
		});
	}
	
	public static void parallel(Vector size, A1<Vector> action) {
		parallelY(size, y -> {
			for (int x = 0; x < size.xInt(); x++) {
				action.execute(Vector.xy(x, y));
			}
		});
	}
	
	
	public static final BorrowManagerMap<Vector, Matrix<Color>> matricesColor = new BorrowManagerMap<>(
			MatrixColor::new, (m, _sz) -> ((MatrixColor) m).fillBlack()
	);

	public static final BorrowManagerMap<Vector, Matrix<Integer>> matricesInt = new BorrowManagerMap<>(
			MatrixInts::new, (m, _sz) -> m.fill(0)
	);
	
}
