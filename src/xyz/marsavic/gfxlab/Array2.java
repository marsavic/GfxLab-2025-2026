package xyz.marsavic.gfxlab;

import xyz.marsavic.functions.FO_II;
import xyz.marsavic.geometry.Vector;


public interface Array2<E> extends FO_II<E> {
	
	Vector size();
	
	@Override
	E at(int x, int y);
	
	default E at(Vector p) {
		return at(p.xInt(), p.yInt());
	}
	
	
	static <E> Array2<E> of(Vector size, FO_II<E> f) {
		return new Array2<>() {
			@Override public Vector size() { return size; }
			@Override public E at(int x, int y) { return f.at(x, y); }
		};
	}
	
	
	static void assertSize(Array2<?> a, Vector size) {
		if (!a.size().equals(size)) {
			throw new IllegalArgumentException("Array2 is not of the designated size.");
		}
	}

	
	static Vector assertEqualSizes(Array2<?> a, Array2<?>... o) {
		Vector size = a.size();
		for (Array2<?> b : o) {
			assertSize(b, size);
		}
		return size;
	}
	
}
