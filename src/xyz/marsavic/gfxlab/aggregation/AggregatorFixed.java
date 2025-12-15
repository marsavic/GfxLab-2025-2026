package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;


public class AggregatorFixed extends Aggregator {
	
	private final ColorFunction colorFunction;
	private final int nFrames;
	private final Vector sizeFrame;
	private final Hash hash;
	private final int nIterations;
	
	

	public AggregatorFixed(ColorFunction colorFunction, Vec3 size) {
		this(colorFunction, size, new Hash(0xE5A99AE8E24C771DL));
	}
	
	public AggregatorFixed(ColorFunction colorFunction, Vec3 size, Hash hash) {
		this(colorFunction, size, hash, 256);
	}
	
	public AggregatorFixed(ColorFunction colorFunction, Vec3 size, Hash hash, int nIterations) {
		this.colorFunction = colorFunction;
		nFrames = (int) size.x();
		sizeFrame = size.p12();
		this.hash = hash;
		this.nIterations = nIterations;
	}
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {
		Aggregate aggregate;
		
		int iFrame_ = Math.floorMod(iFrame, nFrames);
		
		aggregate = new Aggregate(colorFunction, iFrame_, sizeFrame, hash.add(iFrame));
		for (int i = 0; i < nIterations; i++) {
			aggregate.addSample();
		}
		var avg = aggregate.avg();
		aggregate.release();
		return avg;		
	}
	
	
	@Override
	public void release() {
	}
}
