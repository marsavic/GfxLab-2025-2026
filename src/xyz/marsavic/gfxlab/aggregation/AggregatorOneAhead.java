package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.ColorFunction;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;

import java.util.concurrent.Future;


public class AggregatorOneAhead extends Aggregator {
	
	private final ColorFunction colorFunction;
	private final int nFrames;
	private final Vector sizeFrame;
	private final Hash hash;
	
	
	private Future<Rr<Matrix<Color>>> frFrameAhead;
	private int iFrameAhead_;
	
	
	
	
	public AggregatorOneAhead(ColorFunction colorFunction, Vec3 size, Hash hash) {
		this.colorFunction = colorFunction;
		nFrames = (int) size.x();
		sizeFrame = size.p12();
		this.hash = hash;
	
		iFrameAhead_ = 0;
		startGettingAhead(iFrameAhead_);
	}
	
	
	private void startGettingAhead(int iFrame) {
		frFrameAhead = UtilsGL.submitTask(() -> rFrameCompute(iFrame));
	}
	
	
	private Rr<Matrix<Color>> rFrameCompute(int iFrame_) {
		Aggregate aggregate = new Aggregate(colorFunction, iFrame_, sizeFrame, hash.add(iFrame_));
		aggregate.addSample();
		var avg = aggregate.avg();
		aggregate.release();
		return avg;
	}
	
	
	@Override
	public Rr<Matrix<Color>> rFrame(int iFrame) {  // not thread safe
		int iFrame_ = Math.floorMod(iFrame, nFrames);
		Rr<Matrix<Color>> rFrame;
		
		if (iFrameAhead_ == iFrame_) {
			rFrame = UtilsGL.futureGet(frFrameAhead);
		} else {
			releaseRFrameAhead();
			rFrame = rFrameCompute(iFrame_);
		}
		iFrameAhead_ = Math.floorMod(iFrame_ + 1, nFrames);
		startGettingAhead(iFrameAhead_);
		return rFrame;
	}
	
	
	private void releaseRFrameAhead() {
		var frFrame_ = frFrameAhead;
		UtilsGL.submitTask(() -> UtilsGL.futureGet(frFrame_).release());
	}
	
	
	@Override
	public void release() {
		releaseRFrameAhead();
	}
}
