package net.pixelstatic.fluxe.world;

import java.nio.file.Files;
import java.nio.file.Path;

public class WorldFile{
	private Path path;
	
	public WorldFile(Path path){
		if(!Files.isDirectory(path)) throw new IllegalArgumentException(path.toAbsolutePath().toString() + ": Path must be a directory!");
			
		
	}
}
