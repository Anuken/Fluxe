package io.anuke.fluxe.generation;

/**Holds parameters for generating a Fluxe bitmap.
 * Has the size, pixelscale, filter and generator, as well as extra rendering parameters.*/
public class Fluxor{
	public int size= 50;
	public float zoom = 0.15f;
	public boolean oilShader, shadows = false;
	public FluxeGenerator generator;
	public FluxeFilter filter;
	public ColorPalette palette;
	
	public Fluxor(FluxeGenerator generator, FluxeFilter filter, ColorPalette palette){
		this.generator = generator;
		this.filter = filter;
		this.palette = palette;
	}
	
	public int[][][] generateVoxels(){
		return generator.generate(size);
	}
}
