package io.anuke.fluxe.generation;

import static java.lang.Math.abs;

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
	
	public static class RampColorFilter implements ColorFilter{
		Color[] colors;
		
		public RampColorFilter(Color...colors){
			this.colors = colors;
		}
		
		@Override
		public void modify(Color input, int x, int y){
			
			float md = 3f;
			float shade = 0f;
			Color closest = new Color();
			
			for(Color c : colors){

				float max1 = Math.max(Math.max(c.r, c.g), c.b);
				float max2 = Math.max(Math.max(color.r, color.g), color.b);

				float dif = abs(c.r / max1 - color.r / max2) + abs(c.g / max1 - color.g / max2)
						+ abs(c.b / max1 - color.b / max2);
				
				if(dif < md){
					closest.set(c);
					md = dif;
					System.out.println(md);
					shade = (int) (1f / (((c.r / color.r + c.g / color.g + c.b / color.b) / 3f)) / 0.2f) * 0.2f;
					
					//if(shade < 0.4f) shade = 0.4f;
				}
			}
			
			closest.mul(shade, shade, shade, 1f);
			input.set(closest);
		}
	}
}
