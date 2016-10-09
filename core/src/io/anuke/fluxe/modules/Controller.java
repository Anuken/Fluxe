package io.anuke.fluxe.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.fluxe.Fluxe;
import io.anuke.fluxe.generation.ColorPalette;
import io.anuke.fluxe.generation.Filters;
import io.anuke.fluxe.generation.Filters.ColorModFilter;
import io.anuke.fluxe.generation.Filters.DitherColorFilter;
import io.anuke.fluxe.generation.Filters.LimitColorFilter;
import io.anuke.fluxe.generation.Filters.NoiseColorFilter;
import io.anuke.fluxe.generation.Filters.OutlineFilter;
import io.anuke.fluxe.generation.Filters.ShiftColorFilter;
import io.anuke.fluxe.generation.FluxeRenderer;
import io.anuke.fluxe.generation.Fluxor;
import io.anuke.fluxe.generation.Generators;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.modules.Module;

public class Controller extends Module<Fluxe>{
	FluxeRenderer crux = new FluxeRenderer();
	Fluxor flux = new Fluxor(Generators.simplepinetree, 
			Filters.sequence(
					new ColorModFilter(
							new DitherColorFilter(),
							new NoiseColorFilter(),
							new LimitColorFilter(),
							new ShiftColorFilter()
					),
					new OutlineFilter()),
			new ColorPalette("439432", "965f18")
	);
	
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
			
			flux.size = MathUtils.random(30, 59);
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
