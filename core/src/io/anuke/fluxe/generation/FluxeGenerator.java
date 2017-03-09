package io.anuke.fluxe.generation;

/**The object that generates the actual voxels*/
public interface FluxeGenerator{
	public int[][][] generate(int size);
}
