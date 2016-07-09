package net.pixelstatic.fluxe.modules;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.meshes.ModelFactory;
import net.pixelstatic.utils.graphics.FrameBufferMap;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("deprecation")
public class Editor extends Module<Fluxe>{
	public Environment environment;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch, shadowBatch;
	public SpriteBatch batch;
	public Model model;
	public ModelInstance instance;
	public CameraInputController camController;
	public FrameBufferMap buffers = new FrameBufferMap();
	public DirectionalShadowLight shadowLight;
	public int pixelscale = 1;
	public int size = 100;
	public int[][][] grid = new int[size][size][size];

	@Override
	public void init(){

		shadowLight = new DirectionalShadowLight(4024, 4024, 20f, 20f, 1f, 100f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		environment.shadowMap = shadowLight;
		
		FileHandle fragshader = Gdx.files.internal("shaders/depth.fragment");
		
		DepthShaderProvider provider = new DepthShaderProvider(DepthShader.getDefaultVertexShader(), fragshader.readString());
		
		provider.config.defaultCullFace = GL20.GL_FRONT;
		
		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(provider);

		batch = new SpriteBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 100f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		generate();

		model = ModelFactory.createVoxelModel(grid, 0.2f);

		instance = new ModelInstance(model);

		buffers.add("instance", Gdx.graphics.getBackBufferWidth() / pixelscale, Gdx.graphics.getBackBufferHeight() / pixelscale);
	}

	void generate(){
		
		for(int x = 0;x < size;x ++){
			for(int y = 0;y < size;y ++){
				for(int z = 0;z < size;z ++){
					if(Vector2.dst(x, z, size/2, size/2) < size/8 - y/20f  && y < size/2)
						grid[x][y][z] = Color.rgba8888(Color.BROWN);
					
					if(Vector3.dst(x, y, z, size/2f, size/2f, size/2f) < size/4){
						grid[x][y][z] = Color.rgba8888(Color.GREEN);
					}
					
					//grid[x][y][z] = (y-offset < 5) || (y-offset < Math.sin((x+z)/5f)*20f+5 ) ? 
					//		Color.rgba8888((float)x/size, 1f-(float)y/size, (float)z/size, 1f): 0;
				}
			}
		}
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		camController.update();

		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());

		shadowBatch.render(instance);

		shadowBatch.end();
		shadowLight.end();

		buffers.begin("instance");
		

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		modelBatch.begin(cam);
		modelBatch.render(instance, environment);
		modelBatch.end();

		buffers.end("instance");

		batch.begin();
		batch.draw( buffers.texture("instance"), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
		batch.end();
	}

	@Override
	public void resize(int width, int height){
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

	}

	@Override
	public void dispose(){
		modelBatch.dispose();
		model.dispose();
	}
}
