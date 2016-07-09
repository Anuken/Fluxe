package net.pixelstatic.fluxe.world;

import java.nio.file.Paths;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.modules.Renderer;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.TimeUtils;

public class World extends Module<Fluxe>{
	public static final int chunksize = 32;
	public static final float voxelsize = 1;
	
	private ChunkMap chunks = new ChunkMap();
	private WorldFile file = new WorldFile(Paths.get(System.getProperty("user.dir"), "world"));
	private Generator generator;
	private Renderer renderer;
	
	@Override
	public void init(){
		renderer = getModule(Renderer.class);
		generator = new Generator(this);
		

		long time = TimeUtils.millis();
		
		int i = 10;
		
		for(int x = -1 - i; x <= i; x ++){
			for(int z = -1 - i; z <= i; z ++){
				for(int y = 0; y < 4; y ++){
					loadChunk(x,y,z);
				}
			}
		}
		
		long elapsed = TimeUtils.timeSinceMillis(time);
		
		System.out.println("Chunk gen time in millis: " +  elapsed);
		System.out.println("Average chunk time: " + (elapsed / (i*2*i*2*4)));
		
		//loadChunk(0,0,1);
		//loadChunk(0,0,2);
	}
	
	@Override
	public void update(){
		
	}
	
	private void loadChunk(int x, int y, int z){
		if(chunks.get(x, y, z) != null) throw new RuntimeException("Chunk already loaded!");
		
		int[][][] data = new int[chunksize][chunksize][chunksize];
		generator.generate(x*chunksize, y*chunksize, z*chunksize, data);
		
		Chunk chunk = new Chunk(x, y, z, data);
		
		renderer.renderables.addAll(chunk.getRenderables());
		
		chunks.put(chunk, x, y, z);
	}
	
	private void unloadChunk(Chunk chunk){
		chunks.remove(chunk);
		
		for(Renderable renderable : chunk.getRenderables())
			renderer.renderables.removeValue(renderable, true);
		
		chunk.dispose();
	}
	
}
