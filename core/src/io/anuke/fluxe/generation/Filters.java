package io.anuke.fluxe.generation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.noise.Noise;

/** Utility class for making filters. */
public class Filters implements FluxeFilter{
	protected static Color color = new Color();
	protected Pixmap input;
	protected Pixmap pixmap;

	public void process(){
	}

	@Override
	public Pixmap process(Pixmap input){
		this.input = input;
		this.pixmap = PixmapUtils.copy(input);
		process();
		return pixmap;
	}

	/** Created a sequence of filters. */
	public static FluxeFilter sequence(FluxeFilter... filters){
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

	/** Returns whether or not this pixel should be processed. */
	public boolean skip(int i){
		return alpha(i) == 0;
	}

	/**
	 * Edits the colors of the image. Use when you want to have many color
	 * modification filters in a row efficiently.
	 */
	public static class ColorModFilter extends Filters{
		ColorFilter[] filters;

		public ColorModFilter(ColorFilter... filters) {
			this.filters = filters;
		}

		public void process(){
			for(int x = 0; x < input.getWidth(); x++){
				for(int y = 0; y < input.getHeight(); y++){
					int i = input.getPixel(x, y);
					if(skip(i))
						continue;

					color.set(i);

					for(ColorFilter filter : filters)
						filter.modify(color, x, y);

					pixmap.setColor(color);
					pixmap.drawPixel(x, y);
				}
			}
		}
	}

	/** Applies an outline around the image. */
	public static class OutlineFilter extends Filters{
		private Color outline;

		public OutlineFilter() {
			this(0, 0, 0, 0.2f);
		}

		public OutlineFilter(float r, float g, float b, float a) {
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
	
	/** Blurs the image.*/
	public static class BlurFilter extends Filters{
		float intensity = 1f;
		Color sum = new Color();
		Color current = new Color();
		int i = 0;
		
		public BlurFilter(){
			
		}
		
		public BlurFilter(float intensity){
			this.intensity = intensity;
		}
		
		public void process(){
			for(int x = 0; x < input.getWidth(); x++){
				for(int y = 0; y < input.getHeight(); y++){
					i = input.getPixel(x, y);
					current.set(i);
					
					if(skip(i)) continue;
					setc(x, y + 1);
					Hue.addu(sum, color);
					setc(x, y-1);
					Hue.addu(sum, color);
					setc(x+1, y);
					Hue.addu(sum, color);
					setc(x-1, y);
					Hue.addu(sum, color);
					
					sum.mul(1/4f*intensity);
					pixmap.setColor(sum.add(current.mul(1f-intensity)));
					pixmap.drawPixel(x, y);
					
					sum.set(0);
				}
			}
		}
		
		void setc(int x, int y){
			if(skip(input.getPixel(x, y))){
				color.set(i);
			}else{
				color.set(input.getPixel(x, y));
			}
			
		}
	}

	/** Basically smooths the image.*/
	public static class CellFilter extends Filters{
		public void process(){
			for(int x = 0; x < input.getWidth(); x++){
				for(int y = 0; y < input.getHeight(); y++){
					int i = input.getPixel(x, y);

					if(input.getPixel(x + 1, y) != i && input.getPixel(x - 1, y) != i
							&& input.getPixel(x, y + 1) != i && input.getPixel(x, y - 1) != i){
						pixmap.setColor(input.getPixel(x, y + 1));
						pixmap.drawPixel(x, y);
					}

				}
			}
		}
	}

	/** A sequence of filters. Only used internally. */
	public static class SequenceFilter extends Filters{
		final FluxeFilter[] filters;

		public SequenceFilter(FluxeFilter[] filters) {
			this.filters = filters;
		}

		@Override
		public Pixmap process(Pixmap input){
			for(int i = 0; i < filters.length; i++){
				FluxeFilter filter = filters[i];
				Pixmap out = filter.process(input);
				if(i != filters.length - 1 && i != 0)
					input.dispose();
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

	/** Rounds the color shade to a certain amount. */
	public static class LimitColorFilter implements ColorFilter{
		float round = 0.2f;
		float min = 0.4f, max = 3;

		public LimitColorFilter() {

		}

		public LimitColorFilter(float i) {
			this.round = i;
		}

		@Override
		public void modify(Color input, int x, int y){
			Color[] colors = FluxeRenderer.getCurrentlyRendering().palette.significantColors;
			int index = Hue.closest(input, colors);
			Color c = colors[index];
			float shade = 1f
					/ (((c.r / (input.r + 0.000001f) + c.g / (input.g + 0.000001f) + c.b / (input.b + 0.000001f))
							/ 3f));

			shade = (int) (shade / round) * round;
			if(shade < min)
				shade = min;
			if(shade > max)
				shade = max;

			input.set(c.r * shade, c.g * shade, c.b * shade, 1f);
		}
	}

	/** Adds "dithering" in the form of a grid pattern. */
	public static class DitherColorFilter implements ColorFilter{
		float i = 0.1f;
		boolean multi = true;

		@Override
		public void modify(Color input, int x, int y){
			float d = dither(x, y);
			input.mul(1 + (i * d), 1 + (i * d), 1 + (i * d), 1f);
		}

		float dither(int x, int y){
			boolean i = ((x + y) % 3 == 0 && (x - y) % 3 == 0) && multi;

			return (i ? 1 : 0) + (x + y) % 2;
		}
	}

	/** Adds noise. */
	public static class NoiseColorFilter implements ColorFilter{
		float scale = 3f, mag = 0.1f;

		public NoiseColorFilter() {
		}

		public NoiseColorFilter(float scale, float mag) {
			this.scale = scale;
			this.mag = mag;
		}

		@Override
		public void modify(Color input, int x, int y){
			float d = Noise.snoise(x, y, scale, mag);
			input.mul(1 + (d), 1 + (d), 1 + (d), 1f);
		}
	}

	/** Shifts the hue. */
	public static class ShiftColorFilter implements ColorFilter{

		@Override
		public void modify(Color input, int x, int y){
			input.mul(1f, 1f, 1f, 1f);
		}
	}
}
