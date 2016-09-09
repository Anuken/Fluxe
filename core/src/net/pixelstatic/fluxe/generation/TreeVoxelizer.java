package net.pixelstatic.fluxe.generation;

import java.util.Random;

import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class TreeVoxelizer implements Voxelizer{
	boolean generated = false;
	static int bark = Color.valueOf("965f18").toIntBits();
	static int leaves = Color.valueOf("439432").toIntBits();
	int[][][] write;
	int[][][] voxels;
	int size;
	Random random = new Random();

	public int[][][] generate(int size){
		generated = false;
		voxels = new int[size][size][size];

		write = new int[size][size][size];
		this.size = size;
		
		 voxels[size/2][1][size/2] = 2;
		 //voxels[size/2+2][1][size/2+2] = 2;
		// voxels[size/2-2][1][size/2+4] = 2;
		
/*
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][1].length;z ++){
				if(Vector2.dst(x, z, size / 2, size / 2) < 1) voxels[x][1][z] = 2;
			}
		}
		*/

		while( !generated){
			generate();



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
		int trunk = 9;
		
		for(int y = 2;y < trunk+1;y ++){
			if(y == 2){
				disc2(size/2,y,size/2,4+(6-y+1)/3, bark);
			}else{
				disc(size/2,y,size/2,4+(6-y)/3, bark);
			}
		}
		
		disc2(size/2,trunk+1,size/2,(voxels.length-(trunk+1))/5 - ((trunk+1) % 3)*2+3, leaves);
		
		for(int y = trunk+1;y < voxels.length-6;y ++){
			disc(size/2,y,size/2,(voxels.length-y)/5 - (y % 3)*2+3, leaves);
		}
		
		for(int y = 0; y < 4;y ++){
			ndisc(size/2, voxels.length - y - 6,size/2, 1.2f, leaves);
		}
		
		
		generated = true;
		
		/*
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

							
							if(Math.random() < 0.66){
								modx = MathUtils.randomSign();
							}else if(Math.random() < 0.33){
								mody = 1;
							}else{
								modz = MathUtils.randomSign();
							}
							
							place(x + modx, y + 1, z + modz, 2);
						}

						if(y >= voxels[x].length - 2) generated = true;
					}
				}
			}
		}
		*/
	}
	

	void finish(){
		/*
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					if(voxels[x][y][z] != 0) disc(x,y,z,(size-y)/10+1);
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
		
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					if(voxels[x][y][z] != 0) voxels[x][y][z] = bark;
				}
			}
		}
		*/
	}
	
	void disc(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat()*180;
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][py].length;z ++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle()+off)/10;
				
				rad = brad + (int)(Math.sin(angle)*1.2f);
				
				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py-1, z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py, z, type);
				}
				
			}
		}
	}
	
	void disc2(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat()*180;
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][py].length;z ++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle()+off)/10;
				
				rad = brad + (int)(Math.sin(angle)*1.6f);
				
				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py-(rad - brad), z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py, z, type);
				}
				
			}
		}
	}
	
	void ndisc(int px, int py, int pz, float rad, int type){
		Vector2 vector = new Vector2();
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][py].length;z ++){
				vector.set(z - pz, x - px);
				if(Vector2.dst(px, pz, x, z) < rad) place(x, py, z, type);
			}
		}
	}

	void place(int x, int y, int z, int color){
		if( !MiscUtils.inBounds(x, y, z, voxels.length, 1)) return;
		voxels[x][y][z] = color;
	}
}
