package xyz.marsavic.gfxlab.playground;

import xyz.marsavic.gfxlab.*;
import xyz.marsavic.gfxlab.aggregation.*;
import xyz.marsavic.gfxlab.graphics3d.raytracing.RayTracerSimple;
import xyz.marsavic.gfxlab.graphics3d.scenes.DiscoRoom;
import xyz.marsavic.gfxlab.graphics3d.scenes.Mirrors;
import xyz.marsavic.gfxlab.graphics3d.scenes.NewYearsParty;
import xyz.marsavic.gfxlab.graphics3d.scenes.SceneTest;
import xyz.marsavic.gfxlab.playground.colorfunctions.*;
import xyz.marsavic.gfxlab.tonemapping.ColorTransform;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping2;
import xyz.marsavic.gfxlab.tonemapping.ToneMapping3;
import xyz.marsavic.gfxlab.tonemapping.colortransforms.Identity;
import xyz.marsavic.gfxlab.tonemapping.colortransforms.Multiply;
import xyz.marsavic.gfxlab.tonemapping.matrixcolor_to_colortransforms.AutoSoft;
import xyz.marsavic.reactions.elements.ElementF;
import xyz.marsavic.utils.Hash;

import static xyz.marsavic.reactions.elements.Elements.*;


public class GfxLab {

	public static ElementF<Animation> setup() {
		return
//				OkLab
//				ColorFunction3Example	
//				Gradient	
//				ScanLine	
//				GammaTest	
//				Spirals	
//				Blobs	
//				SceneTest	
//				ColorFunction3

//				SceneTest
//				DiscoRoom
				Mirrors
//				NewYearsParty		
		.setup();				
	}
	
}
