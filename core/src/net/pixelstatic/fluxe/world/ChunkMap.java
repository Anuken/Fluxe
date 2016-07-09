package net.pixelstatic.fluxe.world;

import com.badlogic.gdx.utils.ObjectMap;

public class ChunkMap{
	private ObjectMap<Integer, ObjectMap<Integer, ObjectMap<Integer, Chunk>>> chunks = new ObjectMap<>();
	
	public Chunk get(int x, int y, int z){
		ObjectMap<Integer, Chunk> map = getZMap(x,y,z);
		
		Chunk chunk = map.get(z);
		
		return chunk;
	}
	
	public void put(Chunk chunk, int x, int y, int z){
		ObjectMap<Integer, Chunk> map = getZMap(x,y,z);
		map.put(z, chunk);
	}
	
	public void remove(Chunk chunk){
		ObjectMap<Integer, Chunk> map = getZMap(chunk.x,chunk.y,chunk.z);
		map.remove(chunk.z);
	}
	
	private ObjectMap<Integer, Chunk> getZMap(int x, int y, int z){
		ObjectMap<Integer, ObjectMap<Integer, Chunk>> map1 = chunks.get(x);
		if(map1 == null){
			map1 = new ObjectMap<>();
			chunks.put(x, map1);
		}
		
		ObjectMap<Integer, Chunk> map2 = map1.get(y);
		if(map2 == null){
			map2 = new ObjectMap<>();
			map1.put(y, map2);
		}
		
		return map2;
	}
	
}
