package io.anuke.fluxe.generation;

import io.anuke.ucore.ValueMap;

public class Fluxor{
	private Voxelizer generator;
	private Rasterizer filter;
	private ValueMap values;
	
	public Fluxor(Voxelizer generator, Rasterizer filter){
		this.generator = generator;
		this.filter = filter;
		values = new ValueMap();
		values.put("pixelscale", 10);
		values.put("size", 50);
	}
	
	public ValueMap getValues(){
		return values;
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
		return generator.generate(values.getInt("size"));
	}
}
