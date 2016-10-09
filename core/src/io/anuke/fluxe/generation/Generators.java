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
	simpletree{
		void generate(){
			int rad = MathUtils.random(size / 10, size / 7);

			for(int y = 1; y < size - rad * 2; y++){
				disc(size / 2, y, size / 2, 1, 1);
				if(Math.random() < 0.4 && y % 3 == 0 && y > size / 3 && y < size - rad * 2 - 2){
					int mx = 0;
					int mz = 0;
					if(Math.random() < 0.8){
						mx = MathUtils.randomSign();
					}else{
						mz = MathUtils.randomSign();
					}
					int l = MathUtils.random(2, 4) + rad / 2 + (int) ((float) y / size * 4);
					for(int i = 0; i < l; i++){
						place(size / 2 + mz * i, y, size / 2 + mx * i, 1);
					}
					sphere(size / 2 + mz * l, y, size / 2 + mx * l, l / 3, 0);
				}
			}
			for(int y = 1; y < 2; y++)
				disc(size / 2, y, size / 2, 2, 1);

			sphere(size / 2, size - rad - 3, size / 2, rad, 0);

		}
	},
	pinetree{
		void generate(){
			int trunk = 4+size / 12;
			
			for(int y = 3; y < trunk + 1; y++){
				if(y == 3){
					rdisc2(size / 2, y, size / 2, (4 + (6 - y + 1) / 3f)*size/60f, 1);
				}else{
					rdisc(size / 2, y, size / 2, 4 + (6 - y) / 3f*size/60f, 1);
				}
			}

			rdisc2(size / 2, trunk + 1, size / 2, (voxels.length - (trunk + 1)) / 5 - ((trunk + 1) % 3) * 2 + 3, 0);

			for(int y = trunk + 1; y < voxels.length - 6; y++){
				rdisc(size / 2, y, size / 2, (voxels.length - y) / 5 - (y % 3) * 2 + 3, 0);
			}

			for(int y = 0; y < 4; y++){
				disc(size / 2, voxels.length - y - 6, size / 2, 1.2f, 0);
			}
		}
	},
	tree{
		void generate(){
			int trunk = 1+size / 12;
			
			int rad = 4+size/5;
			
			for(int y = 2; y < trunk+1; y++){
				if(y == 2){
					rdisc2(size / 2, y, size / 2, (4 + (6 - (y) + 1) / 3f)*size/60f, 1);
				}else{
					rdisc(size / 2, y, size / 2, 4 + (6 - y) / 3f*size/60f, 1);
				}
			}
			
			
			for(int y = trunk + 1; y < size - 1 - rad*2; y++){
				rdisc(size/2, y, size/2, 1 + (6 - y/8) / 3f*size/60f, 1);
			}
			
			for(int y = size - 2 - rad*2; y < size-2; y ++){
				rdisc(size / 2, y, size / 2, (float)Math.sin((y-(size - 2 - rad*2))/(size/6f))*(size/6f) + (y%3)*2, 0);
			}

			//rdisc2(size / 2, trunk + 1, size / 2, (voxels.length - (trunk + 1)) / 5 - ((trunk + 1) % 3) * 2 + 3, 0);

			for(int y = trunk + 1; y < voxels.length - 6; y++){
			//	rdisc(size / 2, y, size / 2, (voxels.length - y) / 5 - (y % 3) * 2 + 3, 0);
			}

			for(int y = 0; y < 4; y++){
			//	disc(size / 2, voxels.length - y - 6, size / 2, 1.2f, 0);
			}
		}
	},
	bush{
		void generate(){

			int rad = MathUtils.random(2, 5);
			int trunk = rad + 3;

			for(int y = 1; y < trunk; y++){
				disc(size / 2, y, size / 2, 1, 1);
			}

			sphere(size / 2, trunk, size / 2, rad, 0);

			int max = MathUtils.random(4, 9);
			int d = 2;

			for(int i = 0; i < max; i++)
				sphere(size / 2 + MathUtils.random(-d, d), trunk, size / 2 + MathUtils.random(-d, d),
						rad + MathUtils.random(-2, 0), 0);
		}
	},
	grass{
		void generate(){

			for(int x = 0; x < size; x++){
				for(int z = 0; z < size; z++){
					if(Vector2.dst(size / 2, size / 2, x, z) < 8 && Math.random() < 0.5 && (x + z) % 3 == 0
							&& (x - z) % 3 == 0){
						int rand = MathUtils.random(0, 15);
						for(int i = 0; i < rand; i++)
							place(x, i, z, 0);
					}
				}
			}

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
		for(int x = 0; x < size; x++){
			for(int z = 0; z < size; z++){
				if(Vector2.dst(px, pz, x, z) < rad)
					place(x, py, z, type);
			}
		}
	}
	
	void rdisc(int px, int py, int pz, float rad, int type){
		float brad = rad;
		Vector2 vector = new Vector2();
		float off = MathUtils.random() * 180;
		for(int x = 0; x < size; x++){
			for(int z = 0; z < size; z++){
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

	void rdisc2(int px, int py, int pz, float rad, int type){
		float brad = rad;
		Vector2 vector = new Vector2();
		float off = MathUtils.random() * 180;
		for(int x = 0; x < size; x++){
			for(int z = 0; z < size; z++){
				vector.set(z - pz, x - px);
				float angle = (vector.angle() + off) / 10;

				rad = brad + (int) (Math.sin(angle) * 1.6f);

				if(rad - brad > 0){
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py - (int)(rad - brad), z, type);
				}else{
					if(Vector2.dst(px, pz, x, z) < rad)
						place(x, py, z, type);
				}

			}
		}
	}

	void sphere(int px, int py, int pz, float rad, int type){
		for(int x = 0; x < voxels.length; x++){
			for(int y = 0; y < voxels.length; y++){
				for(int z = 0; z < voxels[x][py].length; z++){
					if(Vector3.dst(px, py, pz, x, y, z) < rad)
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
