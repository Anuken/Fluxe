package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.PixmapUtils;

/**Utility class for making filters.*/
public class Filters implements FluxeFilter{
	protected static Color color = new Color();
	protected Pixmap input;
	protected Pixmap pixmap;
	
	public void process(){}
	
	@Override
	public Pixmap process(Pixmap input){
		this.input = input;
		this.pixmap = PixmapUtils.copy(input);
		process();
		return pixmap;
	}
	
	/**Created a sequence of filters.*/
	public static FluxeFilter sequence(FluxeFilter...filters){
		return new SequenceFilter(filters);
	}
	
	public Filters set(){
		return this;
	}
	
	public boolean empty(int value){
		return alpha(value) == 0;
	}

	public int alpha(int value){
		return((value & 0x000000ff));
	}
	
	/**Returns whether or not this pixel should be processed.*/
	public boolean skip(int i){
		return alpha(i) == 0;
	}
	
	/**Edits the colors of the image. Use when you want to have many color modification filters in a row efficiently.*/
	public static class ColorModFilter extends Filters{
		ColorFilter[] filters;
		
		public ColorModFilter(ColorFilter...filters){
			this.filters = filters;
		}
		
		public void process(){
			for(int x = 0; x < input.getWidth(); x++){
				for(int y = 0; y < input.getHeight(); y++){
					int i = input.getPixel(x, y);
					if(skip(i)) continue;
					
					color.set(i);
					
					for(ColorFilter filter : filters)
						filter.modify(color, x, y);
					
					pixmap.setColor(color);
					pixmap.drawPixel(x, y);
				}
			}
		}
	}
	
	/**Applies an outline around the image.*/
	public static class OutlineFilter extends Filters{
		private Color outline;
		
		public OutlineFilter(){
			this(0,0,0,0.2f);
		}
		
		public OutlineFilter(float r, float g, float b, float a){
			outline = new Color(r, g, b, a);
		}
		
		public void process(){
			for(int x = 0; x < input.getWidth(); x++){
				for(int y = 0; y < input.getHeight(); y++){
					int i = input.getPixel(x, y);
					if(skip(i)){
						if(!empty(input.getPixel(x + 1, y)) || !empty(input.getPixel(x - 1, y))
								|| !empty(input.getPixel(x, y + 1)) || !empty(input.getPixel(x, y - 1))){
							pixmap.setColor(Color.rgba8888(outline));
							pixmap.drawPixel(x, y);
						}
					}
				}
			}
		}
	}
	
	/**A sequence of filters. Only used internally.*/
	public static class SequenceFilter extends Filters{
		final FluxeFilter[] filters;
		
		public SequenceFilter(FluxeFilter[] filters){
			this.filters = filters;
		}
		
		@Override
		public Pixmap process(Pixmap input){
			for(int i = 0; i < filters.length; i ++){
				FluxeFilter filter = filters[i];
				Pixmap out = filter.process(input);
				if(i != filters.length-1 && i != 0) input.dispose();
				input = out;
			}
			return input;
		}
	}
	
	public static class PosturizeColorFilter implements ColorFilter{
		@Override
		public void modify(Color input, int x, int y){
			
			Hue.round(input, 0.2f);
		}
	}
	
	/**Rounds the color shade to a certain amount.*/
	public static class LimitColorFilter implements ColorFilter{
		float round = 0.2f;
		
		public LimitColorFilter(){
			
		}
		
		public LimitColorFilter(float i){
			this.round = i;
		}
		
		@Override
		public void modify(Color input, int x, int y){
			Color[] colors = FluxeRenderer.getCurrentlyRenderingFluxor().palette.significantColors;
			int index = Hue.closest(input, colors);
			Color c = colors[index];
			float shade =  1f / (((c.r / (input.r+0.000001f) + c.g / (input.g+0.000001f) + c.b / (input.b+0.000001f)) / 3f));

			
			shade = (int)(shade/round)*round;
			
			input.set(c.r*shade, c.g*shade, c.b*shade, 1f);
		}
	}
	
	public static class DitherColorFilter implements ColorFilter{
		float i = 0.1f;
		
		@Override
		public void modify(Color input, int x, int y){
			float d = dither(x,y);
			input.mul(1+(i*d), 1+(i*d), 1+(i*d), 1f);
		}
		
		float dither(int x, int y){
			return (x+y)%2;
		}
	}
}
