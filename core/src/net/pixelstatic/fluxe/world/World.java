package net.pixelstatic.fluxe.world;

import java.nio.file.Paths;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.modules.Renderer;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.*;

public class World extends Module<Fluxe>{
	public static final int chunksize = 32;
	public static final float voxelsize = 1;
	public static final float loadrange = 500;

	private ChunkMap chunks = new ChunkMap();
	private Array<Chunk> chunklist = new Array<Chunk>();
	private ChunkLoader chunkloader;
	private Thread chunkthread;
	private WorldFile file = new WorldFile(Paths.get(System.getProperty("user.dir"), "world"));
	private Generator generator;
	private Renderer renderer;
	private PerspectiveCamera camera;
	private Object lock = new Object();

	@Override
	public void init(){
		renderer = getModule(Renderer.class);
		camera = renderer.cam;
		generator = new Generator(this);

		long time = TimeUtils.millis();

		int i = 2;

		for(int x = -1 - i;x <= i;x ++){
			for(int z = -1 - i;z <= i;z ++){
				for(int y = 0;y < 3;y ++){
					//	loadChunk(x, y, z);
				}
			}
		}

		long elapsed = TimeUtils.timeSinceMillis(time);

		System.out.println("Chunk gen time in millis: " + elapsed);
		System.out.println("Average chunk time: " + (elapsed / (i * 2f * i * 2f * 4f)));
		System.out.println("Amount of chunks generated: " + (i * 2 * i * 2 * 3));

		//loadChunk(0,0,1);
		//loadChunk(0,0,2);

		(chunkthread = new Thread((chunkloader = new ChunkLoader()))).start();
	}

	@Override
	public void update(){
		for(Chunk chunk : chunklist){
			if(chunk.getCenterPosition().dst(camera.position.x, camera.position.y, camera.position.z) > loadrange){
				//unloadChunk(chunk);
				//break;
			}
		}

		final int range = 1;

		//System.out.println("loading chunks at range " + range);
		final int camx = toChunkCoords(camera.position.x);
		final int camy = toChunkCoords(camera.position.y);
		final int camz = toChunkCoords(camera.position.z);

		//if(Math.random() < 0.01){
		//	System.out.println("Adding chunk coord to thread.");
		//	chunkloader.queue.put(new ChunkCoord(0,0,0));
		//chunkthread.interrupt();
		//}

		for(int x = -1 - range;x <= range;x ++){
			for(int z = -1 - range;z <= range;z ++){
				for(int y = -1 - range;y <= range;y ++){
					if(chunks.get(x + camx, y + camy, z + camz) == null){
						//System.out.println("loading chunk at " + x + ", " + y + ", " + z);
						scheduleLoadChunk(x + camx, y + camy, z + camz);
					}
				}
			}
		}

	}

	private void scheduleLoadChunk(int x, int y, int z){
		chunkloader.queue.put(Pools.obtain(ChunkCoord.class).set(x, y, z));
	}

	private class ChunkLoader implements Runnable{
		AtomicQueue<ChunkCoord> queue = new AtomicQueue<>(32);

		@Override
		public void run(){
			while(true){
				try{
					ChunkCoord coords = null;
					while((coords = queue.poll()) != null){
						loadChunk(coords.x, coords.y, coords.z);
						Pools.free(coords);
					}

					Thread.sleep(1000);
				}catch(InterruptedException e){
					System.out.println("Interuppted.");
				}
			}
		}
	}

	public static class ChunkCoord{
		public int x, y, z;

		public ChunkCoord(){

		}

		public ChunkCoord(int x, int y, int z){
			set(x, y, z);
		}

		public ChunkCoord set(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
	}

	public int toChunkCoords(float i){
		int r = (int)(i / (chunksize * voxelsize));
		if(i < 0) r --;
		return r;
	}

	private void loadChunk(int x, int y, int z){
		if(chunks.get(x, y, z) != null) throw new RuntimeException("Chunk already loaded!");

		int[][][] data = new int[chunksize][chunksize][chunksize];
		generator.generate(x * chunksize, y * chunksize, z * chunksize, data);

		Chunk chunk = new Chunk(x, y, z, data);

		renderer.renderables.addAll(chunk.getRenderables());

		chunklist.add(chunk);

		chunks.put(chunk, x, y, z);
		System.out.printf("Putting chunk: %d, %d, %d", x, y, z);
	}

	private void unloadChunk(Chunk chunk){
		chunks.remove(chunk);
		chunklist.removeValue(chunk, true);

		for(Renderable renderable : chunk.getRenderables())
			renderer.renderables.removeValue(renderable, true);

		chunk.dispose();
	}

}
