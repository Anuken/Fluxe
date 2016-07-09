package net.pixelstatic.fluxe.meshes;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ModelFactory{
	private final static VertexInfo vertTmp1 = new VertexInfo();
	private final static VertexInfo vertTmp2 = new VertexInfo();
	private final static VertexInfo vertTmp3 = new VertexInfo();
	private final static VertexInfo vertTmp4 = new VertexInfo();
	private final static Color color = new Color(1, 0, 0, 1);
	private final static Vector3[] vectors = new Vector3[8];
	private final static MeshBuilder meshBuilder = new MeshBuilder();
	private static Array<Mesh> meshes;

	static{
		for(int i = 0;i < vectors.length;i ++){
			vectors[i] = new Vector3();
		}
	}

	public static Mesh createCube(float x, float y, float z, float size){
		int vsize = 500;

		Mesh mesh = new Mesh(false, false, vsize, vsize, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal()));

		float[] vertices = new float[vsize];

		mesh.getVertices(vertices);

		return mesh;
	}
	
	public static void begin(){
		if(meshes != null) throw new IllegalArgumentException("Call end() first.");
		meshes = new Array<Mesh>();

		//System.out.println("Beginning building of mesh.");
	}
	
	public static Mesh[] end(){
		if(meshes == null) throw new IllegalArgumentException("Call begin() first.");
		
		if(meshBuilder.getAttributes() != null){
			endMesh();
		}
		
		//System.out.println("Ending building of mesh.\n\n");
		
		Mesh[] array = meshes.toArray(Mesh.class);
		
		meshes = null;
		
		return array;
	}

	public static Mesh[] generateVoxelMesh(int[][][] voxels, float px, float py, float pz, float size){
		begin();
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){

					color.set(voxels[x][y][z]);

					if(voxels[x][y][z] != 0) cube(px + x * size ,py + y * size,pz + z * size, size, //
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

	public static boolean exists(int[][][] array, int x, int y, int z){
		if( !(x >= 0 && y >= 0 && z >= 0 && x < array.length && y < array[0].length && z < array[0][0].length)) return true;

		return array[x][y][z] != 0;
	}

	public static void cube(float x, float y, float z, float size, boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back){

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

	private static void rect(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Vector3 normal){
		meshBuilder.rect(vertTmp1.set(a, normal, color, null).setUV(0f, 1f), vertTmp2.set(b, normal, color, null).setUV(1f, 1f), vertTmp3.set(c, normal, color, null).setUV(1f, 0f), vertTmp4.set(d, normal, color, null).setUV(0f, 0f));
	}

	private static void checkMesh(){
		if(meshBuilder.getAttributes() == null){

			meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);
			//System.out.println("Beginning first mesh build");

		}else if(meshBuilder.getNumIndices() >= Short.MAX_VALUE + 16000 /*if the vertices will exceed max vertices soon*/){
			endMesh();

			//System.out.println("Adding new mesh.");

			meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);
		}
	}

	private static void endMesh(){
		Mesh mesh = meshBuilder.end();
		
		//System.out.println("Ending a mesh.");
		
		meshes.add(mesh);
	}

	static void rect(short a, short b, short c, short d){
		meshBuilder.index(a, b, c, c, d, a);
	}
}
