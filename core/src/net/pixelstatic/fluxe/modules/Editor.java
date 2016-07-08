package net.pixelstatic.fluxe.modules;

import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.meshes.ModelFactory;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Editor extends Module<Fluxe>{
	public Environment environment;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public Model center;
	public ModelInstance instance, centeri;
	public CameraInputController camController;

	@Override
	public void init(){
		
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        
        ModelFactory.init();
		model = ModelFactory.createModel();
		
		center = new ModelBuilder().createXYZCoordinates(5, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
		
		centeri = new ModelInstance(center);
		
		instance = new ModelInstance(model);
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		camController.update();
		
		modelBatch.begin(cam);
		modelBatch.render(instance, environment);
		modelBatch.render(centeri, environment);
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
		model.dispose();
		center.dispose();
	}
}
