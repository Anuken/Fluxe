package net.pixelstatic.fluxe.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

import io.anuke.ucore.modules.Module;
import net.pixelstatic.fluxe.Fluxe;

public class Input extends Module<Fluxe> implements InputProcessor{
	private final IntIntMap keys = new IntIntMap();
	private int STRAFE_LEFT = Keys.A;
	private int STRAFE_RIGHT = Keys.D;
	private int FORWARD = Keys.W;
	private int BACKWARD = Keys.S;
	private int UP = Keys.SPACE;
	private int DOWN = Keys.SHIFT_LEFT;
	private float velocity = 100;
	private float degreesPerPixel = 0.5f;
	private final Vector3 tmp = new Vector3();

	Camera camera;

	{
		Gdx.input.setInputProcessor(this);
	}

	public void update(float deltaTime){
		if(Gdx.input.isKeyJustPressed(Keys.NUM_1)){
			
		}
		
		camera = getModule(FluxViewer.class).cam;
		if(keys.containsKey(FORWARD)){
			if(camera instanceof OrthographicCamera){
				((OrthographicCamera)camera).zoom -= 1 / 80f;
				if(((OrthographicCamera)camera).zoom < 0.01 ) ((OrthographicCamera)camera).zoom = 0.01f;
			}else{
				tmp.set(camera.direction).nor().scl(deltaTime * velocity);
				camera.position.add(tmp);
			}
		}
		if(keys.containsKey(BACKWARD)){
			if(camera instanceof OrthographicCamera){
				((OrthographicCamera)camera).zoom += 1 / 80f;
			}else{
				tmp.set(camera.direction).nor().scl( -deltaTime * velocity);
				camera.position.add(tmp);
			}
		}
		if(keys.containsKey(STRAFE_LEFT)){
			tmp.set(camera.direction).crs(camera.up).nor().scl( -deltaTime * velocity);
			camera.position.add(tmp);
		}
		if(keys.containsKey(STRAFE_RIGHT)){
			tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}
		if(keys.containsKey(UP)){
			tmp.set(camera.up).nor().scl(deltaTime * velocity);
			camera.position.add(tmp);
		}
		if(keys.containsKey(DOWN)){
			tmp.set(camera.up).nor().scl( -deltaTime * velocity);
			camera.position.add(tmp);
		}

		camera.update(true);
	}

	public void init(){
		camera = getModule(FluxViewer.class).cam;
	}

	@Override
	public boolean keyDown(int keycode){
		keys.put(keycode, keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode){
		keys.remove(keycode, 0);
		return true;
	}

	/** Sets the velocity in units per second for moving forward, backward and strafing left/right.
	 * @param velocity the velocity in units per second */
	public void setVelocity(float velocity){
		this.velocity = velocity;
	}

	/** Sets how many degrees to rotate per pixel the mouse moved.
	 * @param degreesPerPixel */
	public void setDegreesPerPixel(float degreesPerPixel){
		this.degreesPerPixel = degreesPerPixel;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
		float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
		camera.direction.rotate(camera.up, deltaX);
		tmp.set(camera.direction).crs(camera.up).nor();
		camera.direction.rotate(tmp, deltaY);
		// camera.up.rotate(tmp, deltaY);
		return true;
	}

	public void update(){
		update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public boolean keyTyped(char character){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount){
		if(camera instanceof OrthographicCamera) ((OrthographicCamera)camera).zoom += amount / 20f;
		return false;
	}
}
