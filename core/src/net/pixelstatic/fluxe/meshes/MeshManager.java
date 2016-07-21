package net.pixelstatic.fluxe.meshes;

import java.util.EnumSet;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MeshManager{
	private final VertexInfo vertTmp1 = new VertexInfo();
	private final VertexInfo vertTmp2 = new VertexInfo();
	private final VertexInfo vertTmp3 = new VertexInfo();
	private final VertexInfo vertTmp4 = new VertexInfo();
	private final Color color = new Color(1, 0, 0, 1);
	private final Vector3[] vectors = new Vector3[8];
	private final MeshBuilder meshBuilder = new MeshBuilder();
	private final ModelBuilder modelBuilder = new ModelBuilder();
	private final MarchingCubes cubes = new MarchingCubes();

	private Array<Mesh> meshes;

	public MeshManager(){
		for(int i = 0;i < vectors.length;i ++){
			vectors[i] = new Vector3();
		}

	}

	public void begin(){
		System.out.println("beginning");
		if(meshes != null) throw new IllegalArgumentException("Call end() first.");
		meshes = new Array<Mesh>();

	}

	public Mesh[] end(){
		if(meshes == null) throw new IllegalArgumentException("Call begin() first.");

		if(meshBuilder.getAttributes() != null){
			endMesh();
		}


		Mesh[] array = meshes.toArray(Mesh.class);

		meshes = null;
		
		System.out.println("ending");

		return array;
	}

	public Mesh[] generateVoxelMesh(int[][][] voxels, float px, float py, float pz, float size){
		begin();
		
		

		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					//EnumSet<Direction> flags = getFlags(voxels, x, y, z);

					color.set(voxels[x][y][z]);

					if(voxels[x][y][z] != 0) cube(px + x * size - size * voxels.length / 2, py + y * size - size * voxels[0].length / 2, pz + z * size - size * voxels[0][0].length / 2, size, //
							!exists(voxels, x, y + 1, z), //top
							!exists(voxels, x, y - 1, z), //bottom
							!exists(voxels, x - 1, y, z), //left
							!exists(voxels, x + 1, y, z), //right
							!exists(voxels, x, y, z + 1), //front
							!exists(voxels, x, y, z - 1));//back
				}
			}
		}

		return end();
	}

	public Model generateVoxelModel(int[][][] voxels){
		
		
		Mesh[] meshes = cubes.createVoxelMesh(voxels);
		
		modelBuilder.begin();
		int i = 0;
		
		for(Mesh mesh : meshes){
			modelBuilder.part("mesh" + i ++, mesh, GL20.GL_TRIANGLES, new Material());
		}

		return modelBuilder.end();
	}

	public EnumSet<Direction> getFlags(int[][][] voxels, int x, int y, int z){
		EnumSet<Direction> set = EnumSet.noneOf(Direction.class);

		if( !exists(voxels, x, y + 1, z)) set.add(Direction.top); //top
		if( !exists(voxels, x, y - 1, z)) set.add(Direction.bottom); //bottom
		if( !exists(voxels, x - 1, y, z)) set.add(Direction.left); //left
		if( !exists(voxels, x + 1, y, z)) set.add(Direction.right); //right
		if( !exists(voxels, x, y, z + 1)) set.add(Direction.front); //front
		if( !exists(voxels, x, y, z - 1)) set.add(Direction.back);//back

		return set;
	}

	public boolean exists(int[][][] array, int x, int y, int z){
		if( !(x >= 0 && y >= 0 && z >= 0 && x < array.length && y < array[0].length && z < array[0][0].length)) return false;

		return array[x][y][z] != 0;
	}

	public void cube(float x, float y, float z, float size, boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back){

		checkMesh();

		meshBuilder.ensureRectangles(6);

		vectors[0].set(x, y, z);
		vectors[1].set(x, y, z + size);
		vectors[2].set(x + size, y, z + size);
		vectors[3].set(x + size, y, z);

		vectors[4].set(x, y + size, z);
		vectors[5].set(x, y + size, z + size);
		vectors[6].set(x + size, y + size, z + size);
		vectors[7].set(x + size, y + size, z);

		if(top) rect(vectors[4], vectors[5], vectors[6], vectors[7], Normals.up); //top
		if(bottom) rect(vectors[3], vectors[2], vectors[1], vectors[0], Normals.down); //bottom

		if(left) rect(vectors[5], vectors[4], vectors[0], vectors[1], Normals.left); //left
		if(right) rect(vectors[2], vectors[3], vectors[7], vectors[6], Normals.right); //right

		if(front) rect(vectors[6], vectors[5], vectors[1], vectors[2], Normals.front); //front
		if(back) rect(vectors[3], vectors[0], vectors[4], vectors[7], Normals.back); //back
	}

	private void rect(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Vector3 normal){
		meshBuilder.rect(vertTmp1.set(a, normal, color, null).setUV(0f, 1f), vertTmp2.set(b, normal, color, null).setUV(1f, 1f), vertTmp3.set(c, normal, color, null).setUV(1f, 0f), vertTmp4.set(d, normal, color, null).setUV(0f, 0f));
	}

	private void checkMesh(){
		if(meshBuilder.getAttributes() == null){

			meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);
			//System.out.println("Beginning first mesh build");

		}else if(meshBuilder.getNumIndices() >= Short.MAX_VALUE + 16000 /*if the vertices will exceed max vertices soon*/){
			endMesh();

			//System.out.println("Adding new mesh.");

			meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);
		}
	}

	private void endMesh(){
		Mesh mesh = meshBuilder.end();
		meshes.add(mesh);
		System.out.println("End mesh. Adding to array.");
	}

	//void rect(short a, short b, short c, short d){
	//	meshBuilder.index(a, b, c, c, d, a);
	//}
}
