package net.pixelstatic.fluxe.modules;

import static java.lang.Math.abs;

import java.nio.ByteBuffer;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.generation.TreeVoxelizer;
import net.pixelstatic.fluxe.meshes.MeshManager;
import net.pixelstatic.gdxutils.graphics.FrameBufferMap;
import net.pixelstatic.gdxutils.graphics.PixmapUtils;
import net.pixelstatic.gdxutils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

@SuppressWarnings("deprecation")
public class Renderer extends Module<Fluxe>{
	public Environment environment;
	public Camera cam;
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
	public TreeVoxelizer generator = new TreeVoxelizer();
	public boolean shadows = true, oil = true;
	int[][][] voxels;
	ShaderProgram shader;

	public Renderer(){
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
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	
		cam.position.set(size * 4, size * 2, size * 4);
		cam.lookAt(size * 2, size * 2, size * 2);
		//cam.
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();

		//camController = new FirstPersonCameraController(cam);

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

		if(Gdx.input.isKeyJustPressed(Keys.E)) pixelate = !pixelate;

		if(Gdx.input.isKeyJustPressed(Keys.Q)) shadows = !shadows;
		
		if(Gdx.input.isKeyJustPressed(Keys.T)) oil = !oil;
		
		if(Gdx.input.isKeyJustPressed(Keys.O)){
			Camera old = cam;
			if(cam instanceof OrthographicCamera){
				cam.position.set(size * 4, size * 2, size * 4);

				
				
				cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}else{
				cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				((OrthographicCamera)cam).zoom = 0.4f;
			}
			
			cam.position.set(old.position);
			cam.lookAt(size * 2, size * 2, size * 2);
			cam.near = 1f;
			cam.far = 1000f;
			cam.update();
		}

		if(Gdx.input.isKeyJustPressed(Keys.R)){
			voxels = generator.generate(size);

			for(Model model : models)
				model.dispose();
			models.clear();
			modelInstances.clear();

			Model model = meshes.generateVoxelModel(voxels);

			add(model);
		}

		//camController.update();
		
	
		if(shadows){
			shadowLight.begin(Vector3.Zero, cam.direction);
			shadowBatch.begin(shadowLight.getCamera());
			Gdx.gl.glClearColor(0,0,0,0);
			shadowBatch.render(modelInstances, environment);

			shadowBatch.end();
			shadowLight.end();
		}else{
			shadowLight.begin(Vector3.Zero, cam.direction);
			Gdx.gl.glClearColor(1,1,1,0);
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

			pixmap = process(pixmap);

			Texture texture = new Texture(pixmap);

			batch.setShader(null);
			batch.begin();
			batch.draw(texture, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
			batch.end();

			texture.dispose();
			pixmap.dispose();
		}
	}

	Color[] ramp = {hex("4e9449"), hex("3e723a"), hex("2e532b"), hex("254223"), hex("172916"), hex("965f18"),
			hex("7c4f15"), hex("613e10"), hex("462d0c"), hex("38240b")};

	Color hex(String s){
		return Color.valueOf(s);
	}
	
	
	
	Color leaves = Color.valueOf("965f18");
	Color bark = Color.valueOf("439432");
	
	Color[] colors = {leaves, bark};

	Pixmap process(Pixmap input){
		Color color = new Color();

		Pixmap pixmap = PixmapUtils.copy(input);

		int blank = Color.rgba8888(0, 0, 0, 1);

		for(int x = 0;x < input.getWidth();x ++){
			for(int y = 0;y < input.getHeight();y ++){
				int i = input.getPixel(x, y);

				//	if(alpha(i) == 0) continue;

				color.set(i);

				if(color.a + color.r + color.g + color.b < 1.001f || color.r + color.g + color.b >= 2.3f){

					pixmap.drawPixel(x, y, blank);
					continue;
				}

				//if(color.a < 0.001f && false){
				//	if( !empty(input.getPixel(x + 1, y)) || !empty(input.getPixel(x - 1, y)) || !empty(input.getPixel(x, y + 1)) || !empty(input.getPixel(x, y - 1))) color.set(1, 1, 1, 1);
				//}else{
				//color.r = round(color.r);
				//color.g = round(color.g);
				//color.b = round(color.b);
				//}
				
				/*
				float md = 3f;
				Color closest = null;
				for(Color c : ramp){
					float diff = Math.abs(c.r - color.r) + Math.abs(c.g - color.g) + Math.abs(c.b - color.b);

					if(diff < md){
						closest = c;
						md = diff;
					}
				}

				color.set(closest);
				*/
				
				float md = 3f;
				float shade = 0f;
				Color closest = null;
				
				for(Color c : colors){
					//float rd =  c.r /color.r;
					//float gd =  c.g /color.g;
					//float bd =  c.b /color.b;
					
					float max1 = Math.max(Math.max(c.r, c.g), c.b);
					float max2 = Math.max(Math.max(color.r, color.g), color.b);
					//float delta = 0.15f;
					
					float dif = abs(c.r/max1 - color.r/max2) + abs(c.g/max1 - color.g/max2) + abs(c.b/max1 - color.b/max2);
					
					if(dif < md){
						closest = c.cpy();
						md = dif;
						shade = (int)(1f/(((c.r /color.r + c.g/color.g + c.b/color.b)/3f))/0.2f)*0.2f;
					}
				}
				
				pixmap.setColor(closest.mul(shade, shade, shade, 1f));
				pixmap.drawPixel(x, y);
			}
		}

		for(int x = 0;x < input.getWidth();x ++){
			for(int y = 0;y < input.getHeight();y ++){
				input.drawPixel(x, y, pixmap.getPixel(x, y));
			}
		}
		/*
		//smooth colors
		for(int x = 0;x < pixmap.getWidth();x ++){
			for(int y = 0;y < pixmap.getHeight();y ++){
				int c = input.getPixel(x, y);
				
				if(input.getPixel(x, y+1) != c && input.getPixel(x, y-1) != c && input.getPixel(x+1, y) != c && input.getPixel(x-1, y) != c){
					pixmap.setColor(input.getPixel(x, y+1));
					//pixmap.drawPixel(x,y);
				}
			}
		}
		*/
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
