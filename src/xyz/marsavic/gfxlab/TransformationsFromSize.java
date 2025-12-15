package xyz.marsavic.gfxlab;


public class TransformationsFromSize {
	
	
	public record ToIdentity(Vec3 s) implements Transformation {
		@Override
		public Vec3 at(Vec3 p) {
			return p;
		}
	}
	
	

	public record ToUnitBox(Vec3 s) implements Transformation {
		@Override
		public Vec3 at(Vec3 p) {
			return p.div(s);
		}
	}
	
	
	public static final class ToGeometric implements Transformation {
		private static final Vec3 t = Vec3.xyz(-1, -1,  1);
		private static final Vec3 u = Vec3.xyz( 2,  2, -2);

		private final Vec3 c;
		
		public ToGeometric(Vec3 s) {
			c = u.div(s);
		}

		@Override
		public Vec3 at(Vec3 p) {
			return p.mul(c).add(t);
		}
	}
	
	

	public static final class ToGeometricFit implements Transformation {
		private final Vec3 k, o;
		
		public ToGeometricFit(Vec3 s) {
			double min = 2 / s.p12().min();
			k = Vec3.xyz(2 / s.x(), min, -min);
			o = Vec3.xyz(s.x() / 2, s.y() / 2, s.z() / 2);
		}
		
		@Override
		public Vec3 at(Vec3 p) {
			return p.sub(o).mul(k);
		}
	}
	
}
