package io.anuke.fluxe.generation;

public class Fluxor{
	public int size= 50, pixelscale = 10;
	public boolean oilShader, shadows;
	private Voxelizer generator;
	private Rasterizer filter;
	
	public Fluxor(Voxelizer generator, Rasterizer filter){
		this.generator = generator;
		this.filter = filter;
	}
	
	public Voxelizer getVoxelizer(){
		return generator;
	}
	
	public void setVoxelizer(Voxelizer generator){
		this.generator = generator;
	}
	
	public Rasterizer getRasterizer(){
		return filter;
	}
	
	public void setRasterizer(Rasterizer filter){
		this.filter = filter;
	}
	
	public int[][][] generate(){
		return generator.generate(size);
	}
}
