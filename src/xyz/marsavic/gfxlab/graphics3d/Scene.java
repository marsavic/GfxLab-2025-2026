package xyz.marsavic.gfxlab.graphics3d;

import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.graphics3d.cameras.Perspective;
import xyz.marsavic.gfxlab.graphics3d.solids.Nothing;

import java.util.Collection;
import java.util.List;


/**
 * Represents a 3D scene in a single moment of time, containing a solid, a camera, lights, and a background color.
 * This interface defines the core components needed for rendering a 3D scene using ray tracing.
 */
public interface Scene {
	Solid             defaultSolid           = Nothing.INSTANCE;
	Camera            defaultCamera          = Perspective.DEFAULT;
	Collection<Light> defaultLights          = List.of();
	Color             defaultColorBackground = Color.BLACK;
	
	default Solid             solid          () { return defaultSolid          ; }
	default Camera            camera         () { return defaultCamera         ; }
	default Collection<Light> lights         () { return defaultLights         ; }
	default Color             colorBackground() { return defaultColorBackground; }
	
	
/*
	class Base implements Scene {
		private final Solid             solid           = createSolid          ();          
		private final Camera            camera          = createCamera         ();         
		private final Collection<Light> lights          = createLights         ();        
		private final Color             colorBackground = createColorBackground();
		
		public final Solid             solid          () { return solid          ; }
		public final Camera            camera         () { return camera         ; }
		public final Collection<Light> lights         () { return lights         ; }
		public final Color             colorBackground() { return colorBackground; }
		
		protected Solid             createSolid          () { return defaultSolid          ; }
		protected Camera            createCamera         () { return defaultCamera         ; }
		protected Collection<Light> createLights         () { return defaultLights         ; }
		protected Color             createColorBackground() { return defaultColorBackground; }
	}

*/
	record Record(
			Solid solid,
			Camera camera,
			Collection<Light> lights,
			Color colorBackground
	) implements Scene {}
	
}


