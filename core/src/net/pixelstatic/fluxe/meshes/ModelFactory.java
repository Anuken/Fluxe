package net.pixelstatic.fluxe.meshes;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class ModelFactory{
	private final static Vector3[] vectors = new Vector3[8];
	private final static MeshBuilder meshBuilder = new MeshBuilder();
	private final static ModelBuilder modelBuilder = new ModelBuilder();

	static{
		for(int i = 0;i < vectors.length;i ++){
			vectors[i] = new Vector3();
		}
	}

	public static Model createModel(){
		int gsize = 100;

		modelBuilder.begin();

		boolean[][][] voxels = new boolean[gsize][gsize][gsize];

		for(int x = 0;x < gsize;x ++){
			for(int y = 0;y < gsize;y ++){
				for(int z = 0;z < gsize;z ++){
					
					if(Vector3.dst(gsize/2f,gsize/2f,gsize/2f, x, y, z) < gsize/2) voxels[x][y][z] = true;
					//voxels[x][y][z] = Math.random() < 0.3;
				}
			}
		}

		return modelBuilder.end();
	}

	public static Mesh createCube(float x, float y, float z, float size){
		int vsize = 500;

		Mesh mesh = new Mesh(false, false, vsize, vsize, new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal()));

		float[] vertices = new float[vsize];

		mesh.getVertices(vertices);

		return mesh;
	}

	public static void generateVoxelMesh(boolean[][][] voxels, float size){
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					if(voxels[x][y][z])
					cube(x*size - voxels.length*size/2,y*size- voxels[x].length*size/2,z*size- voxels[x][y].length*size/2, size, 
							!exists(voxels, x, y+1, z), //top
							!exists(voxels, x, y-1, z), //bottom
							!exists(voxels, x-1, y, z), //left
							!exists(voxels, x+1, y, z), //right
							!exists(voxels, x, y, z+1), //front
							!exists(voxels, x, y, z-1));//back
				}
			}
		}
		
		if(meshBuilder.getAttributes() != null){
			endMesh();
		}
	}
	
	public static boolean exists(boolean[][][] array, int x, int y, int z){
		if(!(x >= 0 && y >= 0 && z >= 0 && x < array.length && y < array[0].length && z < array[0][0].length)) return false;
		
		return array[x][y][z];
	}
/*
	public static void cube(float x, float y, float z, float size, boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back){
		builder.ensureRectangles(6);

		vectors[0].set(x, y, z);
		vectors[1].set(x, y, z + size);
		vectors[2].set(x + size, y, z + size);
		vectors[3].set(x + size, y, z);

		vectors[4].set(x, y + size, z);
		vectors[5].set(x, y + size, z + size);
		vectors[6].set(x + size, y + size, z + size);
		vectors[7].set(x + size, y + size, z);

		sa[3] = builder.vertex(vectors[0], Normals.down, null, uv01);
		sa[2] = builder.vertex(vectors[1], Normals.down, null, uv11);

		sa[1] = builder.vertex(vectors[2], Normals.down, null, uv10);
		sa[0] = builder.vertex(vectors[3], Normals.down, null, uv00);

		sa[4] = builder.vertex(vectors[4], Normals.up, null, uv01);
		sa[5] = builder.vertex(vectors[5], Normals.up, null, uv11);

		sa[6] = builder.vertex(vectors[6], Normals.up, null, uv10);
		sa[7] = builder.vertex(vectors[7], Normals.up, null, uv00);

		if(top)builder.index(sa[0], sa[1], sa[2], sa[2], sa[3], sa[0]); //top
		if(bottom)builder.index(sa[4], sa[5], sa[6], sa[6], sa[7], sa[4]); //bottom

		if(right)rect(sa[2], sa[1], sa[6], sa[5]); //right
		if(left)rect(sa[4], sa[7], sa[0], sa[3]); //left

		if(back)rect(sa[3], sa[2], sa[5], sa[4]); //back
		if(front)rect(sa[1], sa[0], sa[7], sa[6]); //front
	}
	
	*/
	
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

		if(top)meshBuilder.rect(vectors[4], vectors[5], vectors[6], vectors[7], Normals.up); //top
		if(bottom)meshBuilder.rect(vectors[3], vectors[2], vectors[1], vectors[0], Normals.down); //bottom
		
		if(left)meshBuilder.rect(vectors[5], vectors[4], vectors[0], vectors[1], Normals.left); //left
		if(right)meshBuilder.rect(vectors[2], vectors[3], vectors[7], vectors[6], Normals.right); //right
		
		if(front)meshBuilder.rect(vectors[6], vectors[5], vectors[1], vectors[2], Normals.front); //front
		if(back)meshBuilder.rect(vectors[3], vectors[0], vectors[4], vectors[7], Normals.back); //back
	}
	
	private static void checkMesh(){
		if(meshBuilder.getAttributes() == null){
			
			meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
			System.out.println("Beginning mesh build.");
			
		}else if(meshBuilder.getNumIndices() >= Short.MAX_VALUE+16000 /*if the vertices will exceed max vertices soon*/){
			
			endMesh();
			
			System.out.println("Adding new mesh.");
			
			meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
		}
	}
	
	private static void endMesh(){
		Mesh mesh = meshBuilder.end();
		
		modelBuilder.part("mesh" + MathUtils.random(999999999), mesh, GL20.GL_TRIANGLES, 
				new Material(ColorAttribute.createDiffuse(new Color(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1f))));
		
	}

	static void rect(short a, short b, short c, short d){
		meshBuilder.index(a, b, c, c, d, a);
	}

	public static void init(){

	}
}
