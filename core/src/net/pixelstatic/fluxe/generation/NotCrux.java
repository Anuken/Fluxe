package net.pixelstatic.fluxe.generation;

import net.pixelstatic.fluxe.meshes.MeshManager;
import net.pixelstatic.fluxe.meshes.VoxelVisualizer;
import net.pixelstatic.gdxutils.graphics.FrameBufferMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

@SuppressWarnings({"unused", "deprecation"}) 
public class NotCrux implements Disposable{
	private ModelBuilder builder;
	private Environment environment;
	private Camera cam;
	private ModelBatch modelBatch, shadowBatch;
	private SpriteBatch batch;
	private FirstPersonCameraController camController;
	private FrameBufferMap buffers = new FrameBufferMap();
	private final MeshManager meshes = new MeshManager();
	private DirectionalShadowLight shadowLight;
	private int pixelscale = 10;
	private int size = 50;
	private Array<Model> models = new Array<Model>();
	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();
	private TreeVoxelizer generator = new TreeVoxelizer();
	private ShaderProgram shader;

	public NotCrux(){
		builder = new ModelBuilder();
	}

	public void render(Fluxor flux){

	}

	private Model generateVoxelModel(int[][][] voxels){
		Mesh[] meshes = VoxelVisualizer.createVoxelMesh(voxels);

		builder.begin();
		int i = 0;

		for(Mesh mesh : meshes){
			builder.part("mesh" + i ++, mesh, GL20.GL_TRIANGLES, new Material());
		}

		return builder.end();
	}

	@Override
	public void dispose(){
		for(Model model : models)
			model.dispose();
		buffers.dispose();
		batch.dispose();
		modelBatch.dispose();
		shadowBatch.dispose();
	}
}
