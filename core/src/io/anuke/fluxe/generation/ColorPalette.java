package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class ColorPalette{
	public static int maxColors = 10;
	public static Color defaultColor = Color.RED;
	public Color[] colors;
	public Color[] significantColors;
	
	public ColorPalette(String... strings){
		Color[] colors = new Color[strings.length];
		for(int i = 0; i < strings.length; i ++) colors[i] = (strings[i] == null ? null : Color.valueOf(strings[i]));
		setup(colors);
	}
	
	public ColorPalette(Color... ncolors){
		setup(ncolors);
	}
	
	private void setup(Color... ncolors){
		Array<Color> array = new Array<Color>();
		for(Color color : ncolors){
			if(color != null)array.add(color);
		}
		significantColors = array.toArray(Color.class);
		
		colors = new Color[10];
		for(int i = 0; i < 10; i ++){
			colors[i] = (i >= ncolors.length ? null : ncolors[i]);
			if(colors[i] == null) colors[i] = defaultColor;
		}
	}
}
