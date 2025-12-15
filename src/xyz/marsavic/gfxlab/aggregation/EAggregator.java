package xyz.marsavic.gfxlab.aggregation;

import xyz.marsavic.elements.ElementF;
import xyz.marsavic.elements.HasOutput;
import xyz.marsavic.functions.A1;
import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.*;
import xyz.marsavic.reactions.values.EventInvalidated;
import xyz.marsavic.resources.Rr;
import xyz.marsavic.utils.Hash;


public class EAggregator extends ElementF<F1<Rr<Matrix<Color>>, Integer>> {
	
	private final Input<Aggregator.F_Aggregator> inFAggregator;
	private final Input<ColorFunction> inColorFunction;
	private final Input<Vec3> inSize;
	
	private final A1<EventInvalidated> onSampleAdded = this::onSampleAdded;
	
	
	
	public EAggregator(HasOutput<Aggregator.F_Aggregator> outFAggregator, HasOutput<ColorFunction> outColorFunction, HasOutput<Vec3> outSize) {
		inFAggregator = new Input<>(outFAggregator);
		inColorFunction = new Input<>(outColorFunction);
		inSize = new Input<>(outSize);
		
		buildItUpFirstTime();
	}
	

	private Aggregator aggregator;
	
	
	@Override
	protected void buildItUp() {
		aggregator = inFAggregator.get().at(inColorFunction.get(), inSize.get());
		aggregator.onInvalidated().add(onSampleAdded);
	}
	
	@Override
	protected void tearItDown() {
		aggregator.release();
	}
	
	public F1<Rr<Matrix<Color>>, Integer> result() {
		return iFrame -> aggregator.rFrame(iFrame);
	}
	
	private void onSampleAdded(EventInvalidated e) {
		out.fireInvalidated();
	}
	
}
