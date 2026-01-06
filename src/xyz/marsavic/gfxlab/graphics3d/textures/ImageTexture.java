package xyz.marsavic.gfxlab.graphics3d.textures;

import javafx.scene.image.Image;
import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Box;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.MatrixColor;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.javafx.UtilsFX;

import java.io.InputStream;


public class ImageTexture implements F1<Material, Vector> {
	
	private final MatrixColor matrixColor;
	
	
	private ImageTexture(MatrixColor matrixColor) {
		this.matrixColor = matrixColor;
	}
	
	
	public static ImageTexture from(Image img) {
		return new ImageTexture(UtilsFX.matrixFromImage(img));
	}
	
	
	public static ImageTexture from(InputStream is) {
		return new ImageTexture(UtilsFX.matrixFromImage(is));
	}
	
	
	public static ImageTexture from(String fileName) {
		return new ImageTexture(UtilsFX.matrixFromImage(fileName));
	}

	
	@Override
	public Material at(Vector uv) {
		Vector p = uv.mod(Box.UNIT).mul(matrixColor.size());
		return Material.matte(matrixColor.at(p));
	}
	
}
