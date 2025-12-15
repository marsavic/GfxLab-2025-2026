package xyz.marsavic.gfxlab.tonemapping;

import xyz.marsavic.functions.F1;
import xyz.marsavic.gfxlab.Array2;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Matrix;
import xyz.marsavic.gfxlab.gui.UtilsGL;
import xyz.marsavic.resources.Br;
import xyz.marsavic.resources.Rr;

public record ToneMapping2(
		F1<ColorTransform, Array2<Color>> fColorTransformToMatrixColor
) implements F1<Rr<Matrix<Integer>>, Br<Matrix<Color>>> {
	
	@Override
	public Rr<Matrix<Integer>> at(Br<Matrix<Color>> bMC) {
		return bMC.f(mC -> {
			ColorTransform f = fColorTransformToMatrixColor.at(mC);
			Rr<Matrix<Integer>> rMatI = UtilsGL.matricesInt.obtain(mC.size(), true);
			rMatI.a(mI -> mI.fill((x, y) -> f.at(mC.at(x, y)).code()));
			return rMatI;
		});
	}
	
}
