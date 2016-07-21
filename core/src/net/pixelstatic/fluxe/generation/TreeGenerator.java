package net.pixelstatic.fluxe.generation;

import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TreeGenerator{
	boolean generated = false;
	int bark = Color.BROWN.toIntBits();
	int leaves = Color.FOREST.toIntBits();
	int[][][] write;
	int[][][] voxels;

	public int[][][] generate(int size){

		voxels = new int[size][size][size];
		/*
		write = new int[size][size][size];
		
		voxels[size/2][0][size/2] = bark;
		
		while( !generated){
			//generate
			for(int x = 0;x < voxels.length;x ++){
				for(int y = 0;y < voxels[x].length;y ++){
					for(int z = 0;z < voxels[x][y].length;z ++){
						int color = voxels[x][y][z];
						
						if(color == bark){
							if(y + 3 >= size){
								generated = true;
							}
							
							place(x,y + 1,z,bark);
							if(Math.random() < 0.01)
								place(x+MathUtils.random(-1, 1),y,z+MathUtils.random(-1, 1),bark);
						}
					}	
				}
			}
			
			for(int x = 0;x < voxels.length;x ++){
				for(int y = 0;y < voxels[x].length;y ++){
					for(int z = 0;z < voxels[x][y].length;z ++){
						voxels[x][y][z] = write[x][y][z];
					}
				}
			}
					
		}
		
		*/
		int rad = size / 4;
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){

					if(Vector3.dst(size / 2, size / 2, size / 2, x, y, z) < rad) voxels[x][y][z] = (Color.FOREST).toIntBits();

					if(Vector2.dst(size / 2, size / 2, x, z) < size/8 && y < size / 2 && y > 0) voxels[x][y][z] = (Color.BROWN).toIntBits();
				}
			}
		}

		return voxels;
	}

	void place(int x, int y, int z, int color){
		if( !MiscUtils.inBounds(x, y, z, voxels.length, 1)) return;
		write[x][y][z] = color;
	}
}
