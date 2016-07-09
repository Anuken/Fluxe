package net.pixelstatic.fluxe.modules;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.utils.graphics.FrameBufferMap;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

@SuppressWarnings("deprecation")
public class Renderer extends Module<Fluxe>{
	public Environment environment;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch, shadowBatch;
	public CameraInputController camController;
	public FrameBufferMap buffers = new FrameBufferMap();
	public DirectionalShadowLight shadowLight;
	public int pixelscale = 1;
	public int size = 100;
	public Array<Renderable> renderables = new Array<Renderable>();
	
	public Renderer(){
		
		
		shadowLight = new DirectionalShadowLight(1440*2, 900*2, 20f, 20f, 1f, 100f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		//environment.shadowMap = shadowLight;
		
		FileHandle fragshader = Gdx.files.internal("shaders/depth.fragment");
		
		DepthShaderProvider provider = new DepthShaderProvider(DepthShader.getDefaultVertexShader(), fragshader.readString());
		
		provider.config.defaultCullFace = GL20.GL_FRONT;
		
		modelBatch = new ModelBatch();
		shadowBatch = new ModelBatch(provider);

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 2000f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
		
	}

	@Override
	public void update(){
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		camController.update();
/*
		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());

		shadowBatch.render(new RenderableProvider(){
			@Override
			public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool){
				renderables.addAll(renderables);
			}
		}, environment);

		shadowBatch.end();
		shadowLight.end();
		*/

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / pixelscale, Gdx.graphics.getHeight() / pixelscale);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		modelBatch.begin(cam);
		
			modelBatch.render(new RenderableProvider(){
				@Override
				public void getRenderables(Array<Renderable> array, Pool<Renderable> pool){
					//System.out.println(renderables);
					array.addAll(renderables);
				}
			}, environment);
		
		modelBatch.end();
	}

	@Override
	public void resize(int width, int height){
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		cam.update();
	}

	@Override
	public void dispose(){
		modelBatch.dispose();
	}
}
