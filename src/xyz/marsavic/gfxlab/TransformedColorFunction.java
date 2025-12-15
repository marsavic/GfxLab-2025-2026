package xyz.marsavic.gfxlab;

public record TransformedColorFunction(
		ColorFunction colorFunction,
		Transformation transformation
) implements ColorFunction {
	
	@Override
	public Color at(Vec3 p) {
		return colorFunction.at(transformation.at(p));
	}
	
}
