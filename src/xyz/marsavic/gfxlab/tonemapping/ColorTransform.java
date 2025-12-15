package xyz.marsavic.gfxlab.tonemapping;

import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Array2;
import xyz.marsavic.gfxlab.Color;


public interface ColorTransform extends F1<Color, Color> {
	
	default F1<ColorTransform, Array2<Color>> asColorTransformFromMatrixColor() {
		return colorMatrix -> ColorTransform.this;
	}
	
}

