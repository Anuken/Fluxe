package net.pixelstatic.fluxe.generation;

public class Fluxor{
	private Voxelizer generator;
	private Rasterizer filter;
	
	public Fluxor(Voxelizer generator){
		this.generator = generator;
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
}
