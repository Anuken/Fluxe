package net.pixelstatic.fluxe.generation;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.UCore;

public class TreeVoxelizer implements Voxelizer{
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
		int trunk = 9;

		for(int y = 3;y < trunk + 1;y ++){
			if(y == 3){
				disc2(size / 2, y, size / 2, 4 + (6 - y + 1) / 3, bark);
			}else{
				disc(size / 2, y, size / 2, 4 + (6 - y) / 3, bark);
			}
		}

		disc2(size / 2, trunk + 1, size / 2, (voxels.length - (trunk + 1)) / 5 - ((trunk + 1) % 3) * 2 + 3, leaves);

		for(int y = trunk + 1;y < voxels.length - 6;y ++){
			disc(size / 2, y, size / 2, (voxels.length - y) / 5 - (y % 3) * 2 + 3, leaves);
		}

		for(int y = 0;y < 4;y ++){
			ndisc(size / 2, voxels.length - y - 6, size / 2, 1.2f, leaves);
		}
	}

	void disc(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat() * 180;
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][py].length;z ++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle() + off) / 10;

				rad = brad + (int)(Math.sin(angle) * 1.2f);

				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py - 1, z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py, z, type);
				}

			}
		}
	}

	void disc2(int px, int py, int pz, int rad, int type){
		int brad = rad;
		Vector2 vector = new Vector2();
		float off = random.nextFloat() * 180;
		for(int x = 0;x < voxels.length;x ++){
			for(int z = 0;z < voxels[x][py].length;z ++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle() + off) / 10;

				rad = brad + (int)(Math.sin(angle) * 1.6f);

				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad) place(x, py - (rad - brad), z, type);
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
		if( !UCore.inBounds(x, y + 1, z, voxels.length, 1)) return;
		voxels[x][y + 1][z] = color;
	}
}
