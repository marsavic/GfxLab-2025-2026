package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;
import xyz.marsavic.utils.Hashing;


public class AggregatorOnDemand extends Aggregator {
	
	private final ColorFunction colorFunction;
	private final int nFrames;
	private final Vector sizeFrame;
	private final Hash hash;
	
	private Aggregate aggregate;
	
	
	
	public AggregatorOnDemand(ColorFunction colorFunction, Vec3 size) {
		this(colorFunction, size, new Hash(0xF01A0888E582C47BL));
	}
	
	public AggregatorOnDemand(ColorFunction colorFunction, Vec3 size, Hash hash) {
		this.colorFunction = colorFunction;
		nFrames = (int) size.x();
		sizeFrame = size.p12();
		this.hash = hash;
	}
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {
		if (aggregate != null) {
			aggregate.release();
		}
		
		int iFrame_ = Math.floorMod(iFrame, nFrames);
		
		aggregate = new Aggregate(colorFunction, iFrame_, sizeFrame, hash.add(iFrame));
		aggregate.addSample();
		var avg = aggregate.avg();
		aggregate.release();
		return avg;
		
	}
	
	
	@Override
	public void release() {
		if (aggregate != null) {
			aggregate.release();
		}
	}
}
