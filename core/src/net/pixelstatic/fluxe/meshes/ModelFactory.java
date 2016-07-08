package net.pixelstatic.fluxe.meshes;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ModelFactory{
	private final static Vector3 v1 = new Vector3();
	private final static Vector3 v2 = new Vector3();
	private final static Vector3 v3 = new Vector3();
	private final static Vector3 v4 = new Vector3();
	private final static VertexInfo vi1 = new VertexInfo();
	private final static VertexInfo vi2 = new VertexInfo();
	private final static VertexInfo vi3 = new VertexInfo();
	private final static VertexInfo vi4 = new VertexInfo();
	private final static short[][] fa = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0},
			{0, 0, 0, 0}};
	private final static MeshBuilder builder = new MeshBuilder();

	//model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.CORAL)), Usage.Position | Usage.Normal);

	public static Model createModel(){
		ModelBuilder modelBuilder = new ModelBuilder();

		float size = 0.1f;
		float gsize = 10;

		modelBuilder.begin();
		builder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
		/*
				for(int x = 0;x < gsize;x ++){
					for(int y = 0;y < gsize;y ++){
						for(int z = 0;z < gsize;z ++){
							if(Math.random() < 0.3){
								meshbuilder.box(x * size - gsize * size / 2, y * size - gsize * size / 2, z * size - gsize * size / 2, size, size, size);
							}
						}
					}
				}
				*/

		short[] s = rect(0, new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(1, 0, 0), null);
		rect(0, new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(1, 1, 1), new Vector3(0, 1, 1), s);

		//builder.box(1, 1, 1);
		//createCube(builder, 0, 0, 0, 2);
		//	meshbuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 0));

		//meshbuilder.rect(corner00, corner10, corner11, corner01);

		Mesh mesh1 = builder.end();
		
		

		modelBuilder.part("wew", mesh1, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.CORAL)));
		
		
		return modelBuilder.end();
	}

	public static void createCube(MeshBuilder builder, float x, float y, float z, float size){

	}

	public static short[] rect(int index, Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, short[] vals){
		//vi1.set(corner00, null, null, null);
		//vi2.set(corner10, null, null, null);
		///vi3.set(corner11, null, null, null);
		//vi4.set(corner01, null, null, null);

		builder.ensureRectangleIndices(1);

		Vector3 normal = new Vector3(0, 1, 0);

		if(vals == null){
			fa[index][0] = builder.vertex(corner00, normal, null, new Vector2(0, 1));
			fa[index][1] = builder.vertex(corner10, normal, null, new Vector2(1, 1));
		}else{
			fa[index][0] = vals[1];
			fa[index][1] = vals[2];

		}

		fa[index][2] = builder.vertex(corner11, normal, null, new Vector2(1, 0));
		fa[index][3] = builder.vertex(corner01, normal, null, new Vector2(0, 0));

		builder.index(fa[index][0], fa[index][1], fa[index][2], fa[index][2], fa[index][3], fa[index][0]);

		return fa[index];
	}

	public static void init(){

	}
}
