package net.pixelstatic.fluxe.world;

import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.modules.Renderer;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.AtomicQueue;
import com.badlogic.gdx.utils.SnapshotArray;

public class World extends Module<Fluxe>{
	public static final int chunksize = 32;
	public static final float voxelsize = 1;
	public static final float loadrange = 500;
	public static final int queuesize = 10000;
	public static final int range = 5;

	private ChunkMap chunks = new ChunkMap();
	private SnapshotArray<Chunk> chunklist = new SnapshotArray<Chunk>();
	private Thread chunkthread;
	private ChunkLoader chunkloader;
	private ChunkCoord tempcoord = new ChunkCoord();
	private AtomicQueue<SyncLoadRequest> requests = new AtomicQueue<>(queuesize);
	private CopyOnWriteArrayList<ChunkCoord> loadingchunks = new CopyOnWriteArrayList<ChunkCoord>();
	private WorldFile file = new WorldFile(Paths.get(System.getProperty("user.dir"), "world"));
	private Generator generator;
	private Renderer renderer;
	private PerspectiveCamera camera;
	private int camx, camy, camz;

	@Override
	public void init(){
		renderer = getModule(Renderer.class);
		camera = renderer.cam;
		generator = new Generator(this);

		//loadChunk(0,0,1);
		//loadChunk(0,0,2);

		(chunkthread = new Thread(chunkloader = new ChunkLoader())).start();

	}

	@Override
	public void update(){
		camx = toChunkCoords(camera.position.x);
		camy = toChunkCoords(camera.position.y);
		camz = toChunkCoords(camera.position.z);


		chunklist.begin();
		for(Chunk chunk : chunklist){
			if(isChunkOutOfRange(chunk.x, chunk.y, chunk.z)){
				unloadChunk(chunk);
			}
		}
		chunklist.end();

		for(int x = -1 - range;x <= range;x ++){
			for(int z = -1 - range;z <= range;z ++){
				for(int y = -1 - range;y <= range;y ++){
					if(chunks.get(x + camx, y + camy, z + camz) == null){
						if(loadingchunks.contains(tempcoord.set(x + camx, y + camy, z + camz))) continue; // chunk is already scheduled, skip
						scheduleChunk(x + camx, y + camy, z + camz);
					}
				}
			}
		}

		SyncLoadRequest request = null;
		while((request = requests.poll()) != null){
			loadChunkSync(request.data, request.coord);
			return;
		}
	}

	void scheduleChunk(int x, int y, int z){
		ChunkCoord coord = new ChunkCoord().set(x, y, z);

		loadingchunks.add(coord);
		chunkloader.queue.put(coord);

		chunkthread.interrupt();
	}

	class ChunkLoader implements Runnable{
		AtomicQueue<ChunkCoord> queue = new AtomicQueue<ChunkCoord>(queuesize);

		public void run(){
			while(true){
				try{
					ChunkCoord coord = null;
					while((coord = queue.poll()) != null){
						//load data
						
						if(isChunkOutOfRange(coord.x,coord.y,coord.z)){
							loadingchunks.remove(coord);
							continue;
						}
						
						int[][][] data = loadChunkAsync(coord.x, coord.y, coord.z);
						
						requests.put(new SyncLoadRequest(data, coord));
					}

					Thread.sleep(2000);
				}catch(InterruptedException e){

				}
			}
		}
	}
	
	public boolean isChunkOutOfRange(int chunkx, int chunky, int chunkz){
		return (!MathUtils.isEqual(chunkx, camx, range) || !MathUtils.isEqual(chunky, camy, range) || !MathUtils.isEqual(chunkz, camz, range));
	}

	public static class ChunkCoord{
		int x, y, z;

		public ChunkCoord(){
		}

		public ChunkCoord set(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		@Override
		public boolean equals(Object other){
			if( !(other instanceof ChunkCoord)) return false;
			ChunkCoord c = (ChunkCoord)other;

			return c.x == x && c.y == y && c.z == z;
		}
	}

	class SyncLoadRequest{
		int[][][] data;
		ChunkCoord coord;

		public SyncLoadRequest(int[][][] data, ChunkCoord coord){
			this.data = data;
			this.coord = coord;
		}
	}

	public int toChunkCoords(float i){
		int r = (int)(i / (chunksize * voxelsize));
		if(i < 0) r --;
		return r;
	}

	private void loadChunkSync(int[][][] data, ChunkCoord coord){
		int x = coord.x, y = coord.y, z = coord.z;
		
		if(isChunkOutOfRange(x,y,z)){
			loadingchunks.remove(coord);
			return;
		}

		Mesh[] meshes = renderer.meshes.generateChunkMesh(data, coord.x, coord.y, coord.z);

		Renderable[] renderables = new Renderable[meshes.length];

		for(int i = 0;i < renderables.length;i ++){
			Renderable renderable = new Renderable();
			renderable.material = new Material();
			renderable.meshPart.set("", meshes[i], 0, meshes[i].getNumIndices(), GL20.GL_TRIANGLES);
			renderables[i] = renderable;
		}

		Chunk chunk = new Chunk(meshes, renderables, x, y, z, data);

		for(Renderable renderable : chunk.getRenderables())
			renderer.renderables.add(renderable);

		chunklist.add(chunk);

		chunks.put(chunk, x, y, z);
		
		//System.out.printf("Putting chunk: %d, %d, %d\n", x, y, z);

		loadingchunks.remove(coord);

		System.out.println("loading " + loadingchunks.size());
	}

	private int[][][] loadChunkAsync(int x, int y, int z){
		if(chunks.get(x, y, z) != null) throw new RuntimeException("Chunk already loaded!");

		int[][][] data = new int[chunksize][chunksize][chunksize];
		generator.generate(x * chunksize, y * chunksize, z * chunksize, data);

		return data;
	}

	private void unloadChunk(Chunk chunk){
		chunks.remove(chunk);
		chunklist.removeValue(chunk, true);

		for(Renderable renderable : chunk.getRenderables())
			renderer.renderables.removeValue(renderable, true);

		chunk.dispose();
	}

}
