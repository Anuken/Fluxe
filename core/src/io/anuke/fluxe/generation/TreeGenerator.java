package io.anuke.fluxe.generation;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.UCore;

public class TreeGenerator implements FluxeGenerator{
	static int bark = Color.valueOf("965f18").toIntBits();
	static int leaves = Color.valueOf("439432").toIntBits();
	int[][][] write;
	int[][][] voxels;
	int size;
	Random random = new Random();

	public int[][][] generate(int size){
		voxels = new int[size][size][size];

		write = new int[size][size][size];
		this.size = size;

		generate();

		return voxels;
	}

	void generate(){
		int trunk = size / 5;
		int px=0, pz=0;
		
		for(int y = trunk; y < size - 4; y++){
			//if(MathUtils.randomBoolean())
			//	px += MathUtils.random(-1, 1);
			
			//if(MathUtils.randomBoolean())
			//	pz += MathUtils.random(-1, 1);
			
			ndisc(size / 2+px, y, size / 2+pz, (size - 5 - y) / 4 + (y % 3) + 1, leaves);
			/*
			if(y % 3 == 1)
				disc2(size / 2+px, y, size / 2+pz, (size - 5 - y) / 4 + (y % 2) * 2 + 1, leaves);
			else if(y < size*0.7f)
				ndisc(size / 2+px, y, size / 2+pz, (size - 5 - y) / 10 + 3, leaves);
			else if(y != size-5)
				ndisc(size / 2+px, y, size / 2+pz, 2, leaves);
			else
				ndisc(size / 2+px, y, size / 2+pz, 1, leaves);
				*/
		}
		
		ndisc(size / 2+px, size-4, size / 2+pz, 2, leaves);

		//ndisc(size / 2, size - 3 - 1, size / 2, 1, leaves);

		for(int y = 2; y < trunk; y++){
			ndisc(size / 2, y, size / 2, (trunk + 2 - y) / 3+1, bark);
		}

		ndisc(size / 2, 2, size / 2, 4, bark);

		
		
		 
	}

	void disc(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat() * 180;
		for(int x = 0; x < voxels.length; x++){
			for(int z = 0; z < voxels[x][py].length; z++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle() + off) / 10;

				rad = brad + (int) (Math.sin(angle) * 1.2f);

				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py - 1, z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py, z, type);
				}

			}
		}
	}

	void disc2(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat() * 180;
		for(int x = 0; x < voxels.length; x++){
			for(int z = 0; z < voxels[x][py].length; z++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle() + off) / 10;

				rad = brad + (int) (Math.sin(angle) * 1.6f);

				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py - (rad - brad), z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py, z, type);
				}

			}
		}
	}
	

	void disc3(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat() * 180;
		for(int x = 0; x < voxels.length; x++){
			for(int z = 0; z < voxels[x][py].length; z++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle()+off) / 10;

				rad = brad + (int) (Math.sin(angle) * 2f);

				if(Vector2.dst(px, pz, x, z) < rad)
					place(x, py, z, type);

			}
		}
	}

	void ndisc(int px, int py, int pz, float rad, int type){
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
}
