package io.anuke.fluxe.generation;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

import io.anuke.fluxe.meshes.VoxelVisualizer;
import io.anuke.ucore.graphics.FrameBufferMap;
import io.anuke.ucore.graphics.PixmapUtils;

@SuppressWarnings("deprecation")
public class FluxeRenderer implements Disposable{
	private Environment environment;
	private Camera cam;
	private ModelBatch modelBatch, shadowBatch;
	private SpriteBatch batch;
	private FrameBufferMap buffers = new FrameBufferMap();
	private DirectionalShadowLight shadowLight;
	private int vwidth = 100, vheight = 200;
	private ShaderProgram oilShader;
	private Fluxor tempFlux = new Fluxor(null, null);

	public FluxeRenderer(){
		ShaderProgram.pedantic = true;
		oilShader = new ShaderProgram(Gdx.files.internal("shaders/default.vertex"), Gdx.files.internal("shaders/oilpaint.fragment"));
		if( !oilShader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + oilShader.getLog());

		shadowLight = new DirectionalShadowLight(1440 * 10, 900 * 10, 400f, 400f, 1f, 300f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));

		environment.add(shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		environment.shadowMap = shadowLight;

		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch();
		batch = new SpriteBatch();

		cam = new OrthographicCamera(swidth(), sheight());
		((OrthographicCamera)cam).zoom = 0.27f;
	}
	/**Renders a fluxe object with the specified parameters.*/
	public Pixmap render(Fluxor flux){
		int size = flux.size;
		int[][][] voxels = flux.generator.generate(size);
		
		cam.position.set(size * 4+20, size * 2+50, size * 4+20);
		cam.lookAt(size * 2, size * 2, size * 2);
		
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();
		
		
		Model model = VoxelVisualizer.generateVoxelModel(voxels);
		ModelInstance minstance = new ModelInstance(model);
		float scale = 4f*flux.zoom;
		minstance.transform.setToTranslation(0, size, 0);
		minstance.transform.scale(scale, scale, scale);
		

		if(flux.shadows){
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

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		buffers.begin("pixel");

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);

		modelBatch.render(minstance, environment);

		modelBatch.end();

		buffers.end("pixel");

		batch.setShader(flux.oilShader ? oilShader : null);
		batch.begin();

		batch.draw(buffers.texture("pixel"), 0, sheight(), swidth(), -sheight());
		batch.end();

		byte[] bytes = ScreenUtils.getFrameBufferPixels(swidth()/2-vwidth/2, sheight()/2-vheight/2, vwidth, vheight, false);

		Pixmap pixmap = new Pixmap(vwidth, vheight, Format.RGBA8888);

		ByteBuffer pixels = pixmap.getPixels();
		pixels.clear();
		pixels.put(bytes);
		pixels.position(0);
		
		int minx = pixmap.getWidth(), maxx = 0;
		int miny = pixmap.getHeight(), maxy = 0;
		
		for(int x = 0; x < pixmap.getWidth(); x ++){
			for(int y = 0; y < pixmap.getHeight(); y ++){
				if(((pixmap.getPixel(x, y) & 0x000000ff)) != 0){
					maxx = Math.max(maxx, x);
					maxy = Math.max(maxy, y);
					
					minx = Math.min(minx, x);
					miny = Math.min(miny, y);
				}
			}
		}
		
		Pixmap cropped = PixmapUtils.crop(pixmap, minx-1, miny-1, maxx - minx+3, maxy - miny+3);
		pixmap.dispose();
		pixmap = cropped;
		flip(pixmap);

		Pixmap out = flux.filter.process(pixmap);
		pixmap.dispose();
		
		model.dispose();
		
		return out;
	}
	
	/**Uses a temporary fluxe object.*/
	public Pixmap render(int size, float zoom, FluxeGenerator generator, FluxeFilter filter, boolean shadows, boolean oil){
		tempFlux.filter = filter;
		tempFlux.size = size;
		tempFlux.zoom = zoom;
		tempFlux.generator = generator;
		tempFlux.shadows = shadows;
		tempFlux.oilShader = oil;
		return render(tempFlux);
	}
	
	void flip(Pixmap pixmap){
		 ByteBuffer pixels = pixmap.getPixels();
         int numBytes = pixmap.getWidth() * pixmap.getHeight() * 4;
         byte[] lines = new byte[numBytes];
         int numBytesPerLine = pixmap.getWidth() * 4;
         for (int i = 0; i < pixmap.getHeight(); i++) {
             pixels.position((pixmap.getHeight() - i - 1) * numBytesPerLine);
             pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
         }
         pixels.clear();
         pixels.put(lines);
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
		oilShader.dispose();
		buffers.dispose();
	}
}