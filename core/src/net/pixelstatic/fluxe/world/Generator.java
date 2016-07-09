package net.pixelstatic.fluxe.world;

import net.pixelstatic.utils.Noise;

import com.badlogic.gdx.graphics.Color;

public class Generator{
	
	public static void generate(int x, int y, int z, int[][][] data){
		for(int cx = 0; cx < data.length; cx ++){
			for(int cy = 0; cy < data[0].length; cy ++){
				for(int cz = 0; cz < data[0][0].length; cz ++){
					data[cx][cy][cz] = gen(x+cx,y+cy,z+cz);
				}
			}
		}
	}
	
	public static int gen(int x, int y, int z){
		if(y < Noise.normalNoise(x, z, 120, 90) + 50){
			return Color.rgba8888(Color.SKY);
		}
		return 0;
	}
	
	public Generator(World world){
	//	this.world = world;
	}
}
