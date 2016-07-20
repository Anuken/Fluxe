package net.pixelstatic.fluxe.modules;

import java.nio.ByteBuffer;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.meshes.MeshManager;
import net.pixelstatic.utils.graphics.FrameBufferMap;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("deprecation")
public class Renderer extends Module<Fluxe>{
	public Environment environment;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch, shadowBatch;
	public SpriteBatch batch;
	public FirstPersonCameraController camController;
	public FrameBufferMap buffers = new FrameBufferMap();
	public final MeshManager meshes = new MeshManager();
	public DirectionalShadowLight shadowLight;
	public int pixelscale = 10;
	public int size = 40;
	public Array<Model> models = new Array<Model>();
	public Array<ModelInstance> modelInstances = new Array<ModelInstance>();
	int[][][] voxels = new int[size][size][size];
	
	public Renderer(){
		
		shadowLight = new DirectionalShadowLight(1440*10, 900*10, 400f, 400f, 1f, 300f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		//environment.add(shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		//environment.shadowMap = shadowLight;
		
		FileHandle fragshader = Gdx.files.internal("shaders/depth.fragment");
		
		DepthShaderProvider provider = new DepthShaderProvider(DepthShader.getDefaultVertexShader(), fragshader.readString());
		
		provider.config.defaultCullFace = GL20.GL_FRONT;
		
		
		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(provider);
		batch = new SpriteBatch();
		
		buffers.add("pixel", Gdx.graphics.getBackBufferWidth()/pixelscale, Gdx.graphics.getBackBufferHeight()/pixelscale);
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 0f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		
		
		camController = new FirstPersonCameraController(cam);
		
		
		for(int x = 0; x < voxels.length; x ++){
			for(int y = 0; y < voxels[x].length; y ++){
				for(int z = 0; z < voxels[x][y].length; z ++){
					//if(Math.random() < 0.4)voxels[x][y][z] = Color.rgba8888(Color.CORAL);
					int rad = 12;
					
					if(Vector3.dst(size/2, size/2, size/2, x, y, z) < rad)
					voxels[x][y][z] = (Color.FOREST).toIntBits();
					
					if(Vector2.dst(size/2, size/2, x, z) < 6 && y < size/2 && y > 0)
						voxels[x][y][z] = (Color.BROWN).toIntBits();
					
				//	if(Vector3.dst(size/2, size/2, size/2, x, y, z) < 6)
					//	voxels[x][y][z] = 0;
					//if(y < 4 || y < Math.sin((x+z)/10f)*20 )
					//	voxels[x][y][z] = Color.rgba8888(Color.CORAL);
				}
			}
		}
		
		Model model = meshes.generateVoxelModel(voxels);
		
		
		add(model);
		
		//add(new ModelBuilder().createXYZCoordinates(5, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal));
	}
	
	void add(Model model){
		models.add(model);
		ModelInstance i = new ModelInstance(model);
		i.transform.scale(4, 4, 4);
		modelInstances.add(i);
	}

	@Override
	public void update(){
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		
		camController.update();
		
		
		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());

		shadowBatch.render(modelInstances, environment);

		shadowBatch.end();
		shadowLight.end();
		

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//buffers.begin("pixel");
		
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(cam);
		
		modelBatch.render(modelInstances, environment);
		
		modelBatch.end();
		
		//buffers.end("pixel");
		
		//texture.
		//Pixmap pixmap = texture.getTextureData().consumePixmap();
		//pixmap.dispose();
		
	//	batch.begin();
		
		//batch.draw(buffers.texture("pixel"), 0, 0, pixWidth(), pixHeight());
		
		//batch.end();
		
		Pixmap pixmap = new Pixmap(pixWidth(), pixHeight(), Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		
		Gdx.gl.glReadPixels(0, 0, pixWidth(), pixHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
	
		Texture texture = new Texture(pixmap);
		//texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		batch.begin();
		batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		
		texture.dispose();
		pixmap.dispose();
	}
	
	public int pixWidth(){
		return Gdx.graphics.getWidth() / pixelscale;
	}
	
	public int pixHeight(){
		return Gdx.graphics.getHeight() / pixelscale;
	}
	
	//public 

	@Override
	public void resize(int width, int height){
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	@Override
	public void dispose(){
		for(Model model : models)
			model.dispose();
		modelBatch.dispose();
		batch.dispose();
		shadowBatch.dispose();
	}
}
