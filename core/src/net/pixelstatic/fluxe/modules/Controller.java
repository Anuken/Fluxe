package net.pixelstatic.fluxe.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.modules.Module;
import net.pixelstatic.fluxe.Fluxe;
import net.pixelstatic.fluxe.generation.Crux;
import net.pixelstatic.fluxe.generation.DefaultRasterizer;
import net.pixelstatic.fluxe.generation.Fluxor;
import net.pixelstatic.fluxe.generation.TreeVoxelizer;

public class Controller extends Module<Fluxe>{
	Crux crux = new Crux();
	Fluxor flux = new Fluxor(new TreeVoxelizer(), new DefaultRasterizer());;
	SpriteBatch batch = new SpriteBatch();
	Texture lastTexture;
	Pixmap lastPixmap;

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.Q) || Gdx.graphics.getFrameId() == 1){
			if(lastPixmap != null){
				lastPixmap.dispose();
				lastTexture.dispose();
			}
			Pixmap pixmap = crux.render(flux);
			
			
			lastPixmap = pixmap;
			lastTexture = new Texture(pixmap);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.E) && lastPixmap != null){
			Pixmap out = PixmapUtils.scale(lastPixmap, 5);
			PixmapIO.writePNG(Gdx.files.local("tree.png"), out);
			out.dispose();
		}

		if(lastPixmap != null){
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			batch.draw(lastTexture, Gdx.graphics.getWidth()/2 - lastPixmap.getWidth()*2, Gdx.graphics.getHeight()/2 - lastPixmap.getHeight()*2, lastPixmap.getWidth()*4, lastPixmap.getHeight()*4);
			batch.end();
		}
	}

}
