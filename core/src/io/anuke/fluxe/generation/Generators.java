package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.UCore;

public enum Generators implements FluxeGenerator{
	pinetree{
		void generate(){
			int trunk = size / 5;
			
			for(int y = trunk; y < size - 4; y++){
				disc(size / 2, y, size / 2, (size - 5 - y) / 4 + (y % 3) + 1, c(1));
			}
			
			disc(size / 2, size-4, size / 2, 2, c(1));

			for(int y = 2; y < trunk; y++){
				disc(size / 2, y, size / 2, (trunk + 2 - y) / 3+1, c(0));
			}

			disc(size / 2, 2, size / 2, 4, c(0));
		}
	};
	static final Color[] ocolors = {
		hex("FF0000"),		//bark
		hex("00FF00")		//leaves
	};
	static final int[] colors = new int[ocolors.length];
	
	protected int[][][] voxels;
	protected int size, center;
	static{
		for(int i = 0; i < colors.length; i ++){
			colors[i] = ocolors[i].toIntBits();
		}
	}
	
	void generate(){}

	@Override
	public int[][][] generate(int size){
		this.size = size;
		center = size/2;
		voxels = new int[size][size][size];
		generate();
		return voxels;
	}
	
	void disc(int px, int py, int pz, float rad, int type){
		Vector2 vector = new Vector2();
		for(int x = 0; x < voxels.length; x++){
			for(int z = 0; z < voxels[x][py].length; z++){
				vector.set(z - pz, x - px);
				if(Vector2.dst(px, pz, x, z) < rad)
					place(x, py, z, type);
			}
		}
	}
	
	void place(int x, int y, int z, int color){
		if(!UCore.inBounds(x, y + 1, z, voxels.length, 1))
			return;
		voxels[x][y + 1][z] = color;
	}
	
	int c(int i){
		return colors[i];
	}
	
	static Color hex(String s){
		return Color.valueOf(s);
	}
}
