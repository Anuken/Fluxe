package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;

public interface ColorFilter{
	public void modify(Color input, int x, int y);
}
