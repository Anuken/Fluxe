package net.pixelstatic.fluxe.modules;

import java.nio.ByteBuffer;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.generation.TreeGenerator;
import net.pixelstatic.fluxe.meshes.MeshManager;
import net.pixelstatic.gdxutils.graphics.FrameBufferMap;
import net.pixelstatic.gdxutils.graphics.PixmapUtils;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

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
	public int size = 50;
	public boolean pixelate = false;
	public Array<Model> models = new Array<Model>();
	public Array<ModelInstance> modelInstances = new Array<ModelInstance>();
	public TreeGenerator generator = new TreeGenerator();
	int[][][] voxels;

	public Renderer(){

		shadowLight = new DirectionalShadowLight(1440 * 10, 900 * 10, 400f, 400f, 1f, 300f);

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

		buffers.add("pixel", Gdx.graphics.getBackBufferWidth() / pixelscale, Gdx.graphics.getBackBufferHeight() / pixelscale);

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(size * 4, size * 2, size * 4);
		cam.lookAt(size * 2, size * 2, size * 2);
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();

		camController = new FirstPersonCameraController(cam);
		
		voxels = generator.generate(size);
		
		/*
		for(int x = 0;x < voxels.length;x ++){
			for(int y = 0;y < voxels[x].length;y ++){
				for(int z = 0;z < voxels[x][y].length;z ++){
					//if(Math.random() < 0.4)voxels[x][y][z] = Color.rgba8888(Color.CORAL);
					int rad = size / 6;

					if(Vector3.dst(size / 2, size / 2, size / 2, x, y, z) < rad) voxels[x][y][z] = (Color.FOREST).toIntBits();

					if(Vector2.dst(size / 2, size / 2, x, z) < 6 && y < size / 2 && y > 0) voxels[x][y][z] = (Color.BROWN).toIntBits();

					//	if(Vector3.dst(size/2, size/2, size/2, x, y, z) < 6)
					//	voxels[x][y][z] = 0;
					//if(y < 4 || y < Math.sin((x+z)/10f)*20 )
					//	voxels[x][y][z] = Color.rgba8888(Color.CORAL);
				}
			}
		}
*/
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
		
		if(Gdx.input.isKeyJustPressed(Keys.R)){
			voxels = generator.generate(size);
			
			for(Model model : models) model.dispose();
			models.clear();
			modelInstances.clear();
			
			Model model = meshes.generateVoxelModel(voxels);

			add(model);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.E))
			pixelate = !pixelate;

		camController.update();
		/*
		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());

		shadowBatch.render(modelInstances, environment);

		shadowBatch.end();
		shadowLight.end();
		*/
		if(pixelate) Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//buffers.begin("pixel");

		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);

		modelBatch.render(modelInstances, environment);

		modelBatch.end();

		if(pixelate){
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			byte[] bytes = ScreenUtils.getFrameBufferPixels(0, 0, pixWidth(), pixHeight(), false);

			Pixmap pixmap = new Pixmap(pixWidth(), pixHeight(), Format.RGBA8888);

			ByteBuffer pixels = pixmap.getPixels();
			pixels.clear();
			pixels.put(bytes);
			pixels.position(0);

			pixmap = process(pixmap);

			Texture texture = new Texture(pixmap);

			batch.begin();
			batch.draw(texture, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
			batch.end();

			texture.dispose();
			pixmap.dispose();
		}
	}

	Pixmap process(Pixmap input){
		Color color = new Color();

		Pixmap pixmap = PixmapUtils.copy(input);

		for(int x = 0;x < input.getWidth();x ++){
			for(int y = 0;y < input.getHeight();y ++){
				int i = input.getPixel(x, y);

				//	if(alpha(i) == 0) continue;

				color.set(i);

				if(color.a < 0.001f){
					if( !empty(input.getPixel(x + 1, y)) || !empty(input.getPixel(x - 1, y)) || !empty(input.getPixel(x, y + 1)) || !empty(input.getPixel(x, y - 1))) color.set(1, 1, 1, 1);
				}else{
					color.r = round(color.r);
					color.g = round(color.g);
					color.b = round(color.b);
				}

				pixmap.setColor(color);
				pixmap.drawPixel(x, y);
			}
		}
		return pixmap;
	}

	public float round(float input){
		float f = 0.1f;
		return (int)(input / f) * f;
	}

	public boolean empty(int value){
		return alpha(value) == 0;
	}

	public int alpha(int value){
		return ((value & 0x000000ff));
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
