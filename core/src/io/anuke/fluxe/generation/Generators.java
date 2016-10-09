package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.anuke.ucore.UCore;

public enum Generators implements FluxeGenerator{
	simplepinetree{
		void generate(){
			int trunk = size / 5;

			for(int y = trunk; y < size - 4; y++){
				disc(size / 2, y, size / 2, (size - 5 - y) / 4 + (y % 3) + 1, 0);
			}

			disc(size / 2, size - 4, size / 2, 2, 0);

			for(int y = 2; y < trunk; y++){
				disc(size / 2, y, size / 2, (trunk + 2 - y) / 3 + 1, 1);
			}

			disc(size / 2, 2, size / 2, 4, 1);
		}
	},
	bush{
		void generate(){
		
			
			int rad = 4;
			int trunk = 7;
			
			for(int y = 1; y < trunk; y++){
				disc(size / 2, y, size / 2, 1, 1);
			}
			
			
			sphere(size/2, trunk, size/2, rad, 0);
			
			int max = MathUtils.random(4,9);
			int d = 2;
			
			for(int i = 0; i < max; i ++)
				sphere(size/2+MathUtils.random(-d,d), trunk+MathUtils.random(-d,d), size/2+MathUtils.random(-d,d), MathUtils.random(1,3), 0);
		}
	};

	protected int[][][] voxels;
	protected int size, center;

	void generate(){
	}

	@Override
	public int[][][] generate(int size){
		this.size = size;
		center = size / 2;
		voxels = new int[size][size][size];
		generate();
		return voxels;
	}

	void disc(int px, int py, int pz, float rad, int type){
		for(int x = 0; x < voxels.length; x++){
			for(int z = 0; z < voxels[x][py].length; z++){
				if(Vector2.dst(px, pz, x, z) < rad)
					place(x, py, z, type);
			}
		}
	}

	void sphere(int px, int py, int pz, float rad, int type){
		for(int x = 0; x < voxels.length; x++){
			for(int y = 0; y < voxels.length; y++){
				for(int z = 0; z < voxels[x][py].length; z++){
					if(Vector3.dst(px, py, pz, x, y, z) < rad )
						place(x, y, z, type);
				}
			}
		}
	}

	void place(int x, int y, int z, int color){
		if(!UCore.inBounds(x, y + 1, z, voxels.length, 1))
			return;
		voxels[x][y + 1][z] = color + 1;
	}

	static Color hex(String s){
		return Color.valueOf(s);
	}
}
