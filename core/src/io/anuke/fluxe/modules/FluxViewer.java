package io.anuke.fluxe.modules;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

import io.anuke.fluxe.Fluxe;
import io.anuke.fluxe.generation.Crux;
import io.anuke.fluxe.generation.DefaultRasterizer;
import io.anuke.fluxe.generation.Fluxor;
import io.anuke.fluxe.generation.Rasterizer;
import io.anuke.fluxe.generation.TreeVoxelizer;
import io.anuke.fluxe.meshes.VoxelVisualizer;
import io.anuke.ucore.graphics.FrameBufferMap;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.modules.Module;

@SuppressWarnings("deprecation")
public class FluxViewer extends Module<Fluxe>{
	public Environment environment;
	public Camera cam;
	public ModelBatch modelBatch, shadowBatch;
	public SpriteBatch batch;
	public FirstPersonCameraController camController;
	public FrameBufferMap buffers = new FrameBufferMap();
	public DirectionalShadowLight shadowLight;
	public int pixelscale = 10;
	public boolean pixelate = false;
	public Array<Model> models = new Array<Model>();
	public Array<ModelInstance> modelInstances = new Array<ModelInstance>();
	public boolean shadows = true, oil = true;
	ShaderProgram shader;
	Fluxor flux;
	int[][][] voxels;
	Rasterizer filter = new DefaultRasterizer();
	Crux crux = new Crux();

	public FluxViewer(){
		ShaderProgram.pedantic = true;
		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vertex"), Gdx.files.internal("shaders/oilpaint.fragment"));
		if( !shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());

		shadowLight = new DirectionalShadowLight(1440 * 10, 900 * 10, 400f, 400f, 1f, 300f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		//environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		environment.add(shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		environment.shadowMap = shadowLight;

		FileHandle fragshader = Gdx.files.internal("shaders/depth.fragment");

		DepthShaderProvider provider = new DepthShaderProvider(DepthShader.getDefaultVertexShader(), fragshader.readString());

		provider.config.defaultCullFace = GL20.GL_FRONT;

		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(provider);
		batch = new SpriteBatch();

		//buffers.add("pixel", Gdx.graphics.getBackBufferWidth() / pixelscale, Gdx.graphics.getBackBufferHeight() / pixelscale);

		//cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		flux = new Fluxor(new TreeVoxelizer(), new DefaultRasterizer());

		int size = flux.getValues().getInt("size");

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		cam.position.set(size * 4, size * 2, size * 4);
		cam.lookAt(size * 2, size * 2, size * 2);
		//cam.
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();

		//camController = new FirstPersonCameraController(cam);

		voxels = flux.generate();

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
		Model model = VoxelVisualizer.generateVoxelModel(voxels);

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
		if(Gdx.input.isKeyJustPressed(Keys.NUM_1)){
			Pixmap pixmap = crux.render(flux);
			Pixmap out = PixmapUtils.scale(pixmap, 5);
			PixmapIO.writePNG(Gdx.files.local("tree.png"), out);
			pixmap.dispose();
			out.dispose();
		}

		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		if(Gdx.input.isKeyJustPressed(Keys.E)) pixelate = !pixelate;

		if(Gdx.input.isKeyJustPressed(Keys.Q)) shadows = !shadows;

		if(Gdx.input.isKeyJustPressed(Keys.T)) oil = !oil;

		if(Gdx.input.isKeyJustPressed(Keys.O)){
			Camera old = cam;
			if(cam instanceof OrthographicCamera){
				cam.position.set(50 * 4, 50 * 2, 50 * 4);
				cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}else{
				cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				((OrthographicCamera)cam).zoom = 0.4f;
			}

			cam.position.set(old.position);
			cam.lookAt(50 * 2, 50 * 2, 50 * 2);
			cam.near = 1f;
			cam.far = 1000f;
			cam.update();
		}

		if(Gdx.input.isKeyJustPressed(Keys.R)){
			voxels = flux.generate();

			for(Model model : models)
				model.dispose();
			models.clear();
			modelInstances.clear();

			Model model = VoxelVisualizer.generateVoxelModel(voxels);

			add(model);
		}

		//camController.update();

		if(shadows){
			shadowLight.begin(Vector3.Zero, cam.direction);
			shadowBatch.begin(shadowLight.getCamera());
			Gdx.gl.glClearColor(0, 0, 0, 0);
			shadowBatch.render(modelInstances, environment);

			shadowBatch.end();
			shadowLight.end();
		}else{
			shadowLight.begin(Vector3.Zero, cam.direction);
			Gdx.gl.glClearColor(1, 1, 1, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			shadowLight.end();
		}

		if(pixelate) Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		buffers.begin("pixel");

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);

		modelBatch.render(modelInstances, environment);

		modelBatch.end();

		buffers.end("pixel");

		if(pixelate) Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		batch.setShader(oil ? shader : null);
		batch.begin();

		batch.draw(buffers.texture("pixel"), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
		batch.end();

		if(pixelate){
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			byte[] bytes = ScreenUtils.getFrameBufferPixels(0, 0, pixWidth(), pixHeight(), false);

			Pixmap pixmap = new Pixmap(pixWidth(), pixHeight(), Format.RGBA8888);

			ByteBuffer pixels = pixmap.getPixels();
			pixels.clear();
			pixels.put(bytes);
			pixels.position(0);

			pixmap = filter.process(pixmap);

			Texture texture = new Texture(pixmap);

			batch.setShader(null);
			batch.begin();
			batch.draw(texture, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
			batch.end();

			texture.dispose();
			pixmap.dispose();
		}

	}

	public int pixWidth(){
		return Gdx.graphics.getWidth() / pixelscale;
	}

	public int pixHeight(){
		return Gdx.graphics.getHeight() / pixelscale;
	}

	@Override
	public void resize(int width, int height){
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		crux.resize(width, height);
	}

	@Override
	public void dispose(){
		for(Model model : models)
			model.dispose();

		batch.dispose();
		modelBatch.dispose();
		shadowBatch.dispose();
		shader.dispose();
		buffers.dispose();
	}
}
