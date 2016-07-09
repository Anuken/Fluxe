package net.pixelstatic.fluxe.world;

import java.nio.file.Paths;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.utils.modules.Module;

public class World extends Module<Fluxe>{
	public static final int chunksize = 32;
	
	private ChunkMap chunks = new ChunkMap();
	private WorldFile file = new WorldFile(Paths.get(System.getProperty("user.dir"), "world"));
	
	@Override
	public void init(){
		
	}
	
	@Override
	public void update(){
		
	}
	
}
