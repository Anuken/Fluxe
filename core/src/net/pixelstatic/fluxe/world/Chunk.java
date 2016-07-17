package net.pixelstatic.fluxe.world;

import static net.pixelstatic.fluxe.world.World.chunksize;
import static net.pixelstatic.fluxe.world.World.voxelsize;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Chunk implements Disposable{
	private static final Vector3 vector = new Vector3();
	public final int x,y,z;
	private Mesh[] meshes;
	private Renderable[] renderables;
	private int[][][] data;
	
	protected Chunk(Mesh[] meshes, Renderable[] renderables, int x, int y, int z, int[][][] data){
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.data = data;
		this.meshes = meshes;
		this.renderables = renderables;
	}
	
	public Vector3 getCornerPosition(){
		return vector.set(x*voxelsize*chunksize, y*voxelsize*chunksize, z*voxelsize*chunksize);
	}
	
	public Vector3 getCenterPosition(){
		return vector.set(x*voxelsize*chunksize+voxelsize*chunksize/2, y*voxelsize*chunksize+voxelsize*chunksize/2, z*voxelsize*chunksize+voxelsize*chunksize/2);
	}
	
	public Mesh[] getMeshes(){
		return meshes;
	}
	
	public Renderable[] getRenderables(){
		return renderables;
	}
	
	public int[][][] getData(){
		return data;
	}
	
	@Override
	public void dispose(){
	//	for(Mesh mesh : meshes)
	//		mesh.dispose();
	}
}
