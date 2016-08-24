package net.pixelstatic.fluxe.generation;

import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class TreeGenerator{
	boolean generated = false;
	int bark = Color.BROWN.toIntBits();
	int leaves = Color.FOREST.toIntBits();
	int[][][] write;
	int[][][] voxels;
	int size;

	public int[][][] generate(int size){
		generated = false;
		voxels = new int[size][size][size];

		write = new int[size][size][size];
		this.size = size;

		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][1].length;z ++){
				if(Vector2.dst(x, z, size / 2, size / 2) < 6) voxels[x][1][z] = 2;
			}
		}

		while( !generated){
			generate();

			for(int x = 0;x < voxels.length;x ++){
				for(int y = 0;y < voxels[x].length;y ++){
					for(int z = 0;z < voxels[x][y].length;z ++){
						voxels[x][y][z] = write[x][y][z];
					}
				}
			}

			if(generated) finish();

		}
		/*
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
		
		
		int rad = size / 4;
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){

					if(Vector3.dst(size / 2, size / 2, size / 2, x, y, z) < rad) voxels[x][y][z] = (Color.FOREST).toIntBits();

					if(Vector2.dst(size / 2, size / 2, x, z) < size/8 && y < size / 2 && y > 0) voxels[x][y][z] = (Color.BROWN).toIntBits();
				}
			}
		}
		*/
		return voxels;
	}

	void generate(){
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					int v = voxels[x][y][z];

					if(v == 2){
						place(x, y, z, 1);
						//System.out.printf("%d, %d, %d: wew: %d\n", x, y, z, i);
						if(Math.random() < 0.5){
							place(x, y + 1, z, 2);
						}else{
							int modx = 0, modz = 0, mody = 0;
							int mx = x - size/2;
							int mz = z - size/2;
							if(Math.random() < 0.2)
							if(Math.abs(mx) > Math.abs(mz)){
								modx = modx < 0 ? -1 : 1;
							}else{
								modz = modz < 0 ? -1 : 1;
							}
							/*
							if(Math.random() < 0.66){
								modx = MathUtils.randomSign();
							}else if(Math.random() < 0.33){
								mody = 1;
							}else{
								modz = MathUtils.randomSign();
							}
							*/
							place(x + modx, y + 1, z + modz, 2);
						}

						if(y >= voxels[x].length - 2) generated = true;
					}
				}
			}
		}
	}

	void finish(){
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					if(voxels[x][y][z] != 0) voxels[x][y][z] = bark;
				}
			}
		}
	}

	void place(int x, int y, int z, int color){
		if( !MiscUtils.inBounds(x, y, z, voxels.length, 1)) return;
		write[x][y][z] = color;
	}
}
