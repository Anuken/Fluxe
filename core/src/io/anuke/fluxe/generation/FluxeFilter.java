package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Pixmap;

public interface FluxeFilter{
	public Pixmap process(Pixmap input);
}
