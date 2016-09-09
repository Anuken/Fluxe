package net.pixelstatic.fluxe.modules;

import java.nio.ByteBuffer;

import net.pixelstatic.fluxe.generation.DefaultRasterizer;
import net.pixelstatic.fluxe.generation.Fluxor;
import net.pixelstatic.fluxe.generation.Rasterizer;
import net.pixelstatic.fluxe.meshes.VoxelVisualizer;
import net.pixelstatic.gdxutils.graphics.FrameBufferMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

@SuppressWarnings("deprecation")
public class Crux implements Disposable{
	public Environment environment;
	public Camera cam;
	public ModelBatch modelBatch, shadowBatch;
	public SpriteBatch batch;
	public FirstPersonCameraController camController;
	public FrameBufferMap buffers = new FrameBufferMap();
	public DirectionalShadowLight shadowLight;
	public boolean pixelate = false;
	ShaderProgram shader;
	Rasterizer filter = new DefaultRasterizer();

	public Crux(){
		ShaderProgram.pedantic = true;
		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vertex"), Gdx.files.internal("shaders/oilpaint.fragment"));
		if( !shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());

		shadowLight = new DirectionalShadowLight(1440 * 10, 900 * 10, 400f, 400f, 1f, 300f);

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

		//cam = new OrthographicCamera(swidth(), sheight());
		cam = new PerspectiveCamera(67, swidth(), sheight());
	}

	public Pixmap render(Fluxor flux){
		int pixelscale = flux.getValues().getInt("pixelscale");
		int size = flux.getValues().getInt("size");
		int[][][] voxels = flux.getVoxelizer().generate(size);
		
		cam.position.set(size * 4, size * 2, size * 4);
		cam.lookAt(size * 2, size * 2, size * 2);
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();
		
		Model model = VoxelVisualizer.generateVoxelModel(voxels);
		ModelInstance minstance = new ModelInstance(model);

		if(flux.getValues().getBoolean("shadows")){
			shadowLight.begin(Vector3.Zero, cam.direction);
			shadowBatch.begin(shadowLight.getCamera());
			Gdx.gl.glClearColor(0, 0, 0, 0);
			shadowBatch.render(minstance, environment);

			shadowBatch.end();
			shadowLight.end();
		}else{
			shadowLight.begin(Vector3.Zero, cam.direction);
			Gdx.gl.glClearColor(1, 1, 1, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			shadowLight.end();
		}

		Gdx.gl.glViewport(0, 0, swidth() / pixelscale, sheight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		buffers.begin("pixel");

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);

		modelBatch.render(minstance, environment);

		modelBatch.end();

		buffers.end("pixel");

		Gdx.gl.glViewport(0, 0, swidth() / pixelscale, sheight() / pixelscale);
		batch.setShader(flux.getValues().getBoolean("oil") ? shader : null);
		batch.begin();

		batch.draw(buffers.texture("pixel"), 0, sheight(), swidth(), -sheight());
		batch.end();

		Gdx.gl.glViewport(0, 0, swidth(), sheight());

		byte[] bytes = ScreenUtils.getFrameBufferPixels(0, 0, swidth()/pixelscale, sheight()/pixelscale, false);

		Pixmap pixmap = new Pixmap(swidth()/pixelscale, sheight()/pixelscale, Format.RGBA8888);

		ByteBuffer pixels = pixmap.getPixels();
		pixels.clear();
		pixels.put(bytes);
		pixels.position(0);

		pixmap = flux.getRasterizer().process(pixmap);

		Texture texture = new Texture(pixmap);

		batch.setShader(null);
		batch.begin();
		batch.draw(texture, 0, sheight(), swidth(), -sheight());
		batch.end();

		texture.dispose();
		
		model.dispose();
		
		return pixmap;
	}
	
	public int swidth(){
		return Gdx.graphics.getWidth();
	}
	
	public int sheight(){
		return Gdx.graphics.getHeight();
	}

	public void resize(int width, int height){
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	@Override
	public void dispose(){
		batch.dispose();
		modelBatch.dispose();
		shadowBatch.dispose();
		shader.dispose();
		buffers.dispose();
	}
}
