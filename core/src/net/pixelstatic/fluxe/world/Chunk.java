package net.pixelstatic.fluxe.world;

import static net.pixelstatic.fluxe.world.World.chunksize;

import com.badlogic.gdx.graphics.Mesh;

public class Chunk{
	private Mesh[] meshes;
	private int[][][] data;
	
	public Chunk(){
		data = new int[chunksize][chunksize][chunksize];
	}
}
