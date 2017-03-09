package io.anuke.fluxe.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.fluxe.Fluxe;
import io.anuke.fluxe.generation.*;
import io.anuke.fluxe.generation.Filters.*;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.modules.Module;

/**Example usage.*/
public class FluxeExample extends Module<Fluxe>{
	
	FluxeRenderer crux = new FluxeRenderer();
	FluxePattern flux = new FluxePattern(Generators.simplepinetree, 
			Filters.sequence(
					new ColorModFilter(
							new DitherColorFilter(),
							new NoiseColorFilter(),
							new LimitColorFilter(3f),
							new ShiftColorFilter()
					)/*,
					new OutlineFilter()*/),
			new ColorPalette("72d437", "bd8c46")
	){{
	zoom = 0.23f;	
	}};
	
	SpriteBatch batch = new SpriteBatch();
	Texture lastTexture;
	Pixmap lastPixmap;
	//{UCore.maximizeWindow();}

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
			
			flux.size = MathUtils.random(40, 59);
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
	
	@Override
	public void resize(int w, int h){
		crux.resize(w, h);
	}
}
