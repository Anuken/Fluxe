package net.pixelstatic.fluxe.world;

import static net.pixelstatic.fluxe.world.World.chunksize;
import static net.pixelstatic.fluxe.world.World.voxelsize;
import net.pixelstatic.fluxe.meshes.ModelFactory;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Chunk implements Disposable{
	private static final Vector3 vector = new Vector3();
	public final int x,y,z;
	private Mesh[] meshes;
	private Renderable[] renderables;
	private int[][][] data;
	
	protected Chunk(int x, int y, int z, int[][][] data){
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.data = data;
		
		loadMeshes();
	}
	
	private void loadMeshes(){
		
		meshes = ModelFactory.generateVoxelMesh(data, x*voxelsize*chunksize, y*voxelsize*chunksize, z*voxelsize*chunksize, voxelsize);
		renderables = new Renderable[meshes.length];
		
		for(int i = 0; i < renderables.length; i ++){
			Renderable renderable = new Renderable();
			renderable.material = new Material();
			renderable.meshPart.set("", meshes[i], 0, meshes[i].getNumIndices(), GL20.GL_TRIANGLES);
			renderables[i] = renderable;
		}
		
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
	
	@Override
	public void dispose(){
		for(Mesh mesh : meshes)
			mesh.dispose();
	}
}
