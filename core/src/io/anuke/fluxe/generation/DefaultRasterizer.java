package io.anuke.fluxe.generation;

import static java.lang.Math.abs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import io.anuke.ucore.Noise;

public class DefaultRasterizer implements Rasterizer{

	Color[] ramp = { hex("4e9449"), hex("3e723a"), hex("2e532b"), hex("254223"), hex("172916"), hex("965f18"),
			hex("7c4f15"), hex("613e10"), hex("462d0c"), hex("38240b") };

	Color leaves = Color.valueOf("805620");
	Color bark = Color.valueOf("50882f");

	Color[] colors = { leaves, bark };
	
	{
		ramp = new Color[10];
		Color color = leaves.cpy();
		for(int i = 0; i < 5; i ++){
			ramp[i] = color.cpy().add(0.01f, 0.01f, 0.01f, 0f);
		}
		
		color = bark.cpy();
		for(int i = 5; i < 10; i ++){
			ramp[i] = color.cpy().add(0.01f, 0.01f, 0.01f, 0f);
		}
	}

	public Pixmap process(Pixmap input){
		Color color = new Color();

		Pixmap pixmap = new Pixmap(input.getWidth(), input.getHeight(), Format.RGBA8888);

		int blank = Color.rgba8888(0, 0, 0, 0);

		for(int x = 0; x < input.getWidth(); x++){
			for(int y = 0; y < input.getHeight(); y++){
				int i = input.getPixel(x, y);

				if(Math.abs(i - blank) < 100){
					if(!empty(input.getPixel(x + 1, y)) || !empty(input.getPixel(x - 1, y))
							|| !empty(input.getPixel(x, y + 1)) || !empty(input.getPixel(x, y - 1))){
						color.set(0, 0, 0, 0.2f);

						pixmap.setColor(color);
						pixmap.drawPixel(x, y);
					}
					continue;
				}

				color.set(i);

				if(color.a + color.r + color.g + color.b < 1.001f || color.r + color.g + color.b >= 2.3f){

					pixmap.drawPixel(x, y, blank);
					continue;
				}

				/*
				 * float md = 3f; Color closest = null; for(Color c : ramp){
				 * float diff = Math.abs(c.r - color.r) + Math.abs(c.g -
				 * color.g) + Math.abs(c.b - color.b);
				 * 
				 * if(diff < md){ closest = c; md = diff; } }
				 * 
				 * color.set(closest);
				 */

				float md = 3f;
				float shade = 0f;
				Color closest = null;

				for(Color c : colors){
					// float rd = c.r /color.r;
					// float gd = c.g /color.g;
					// float bd = c.b /color.b;

					float max1 = Math.max(Math.max(c.r, c.g), c.b);
					float max2 = Math.max(Math.max(color.r, color.g), color.b);
					// float delta = 0.15f;

					float dif = abs(c.r / max1 - color.r / max2) + abs(c.g / max1 - color.g / max2)
							+ abs(c.b / max1 - color.b / max2);

					if(dif < md){
						closest = c.cpy();
						md = dif;
						shade = (int) (1f / (((c.r / color.r + c.g / color.g + c.b / color.b) / 3f) + Noise.normalNoise(x, y, 3f, 0.2f)) / 0.2f) * 0.2f;
					}
				}

				pixmap.setColor(closest.mul(shade*1.1f, shade*1.1f, (shade)*1.1f, 1f));
				pixmap.drawPixel(x, y);
			}
		}

		for(int x = 0; x < input.getWidth(); x++){
			for(int y = 0; y < input.getHeight(); y++){
				input.drawPixel(x, y, pixmap.getPixel(x, y));
			}
		}
		/*
		 * //smooth colors for(int x = 0;x < pixmap.getWidth();x ++){ for(int y
		 * = 0;y < pixmap.getHeight();y ++){ int c = input.getPixel(x, y);
		 * 
		 * if(input.getPixel(x, y+1) != c && input.getPixel(x, y-1) != c &&
		 * input.getPixel(x+1, y) != c && input.getPixel(x-1, y) != c){
		 * pixmap.setColor(input.getPixel(x, y+1)); //pixmap.drawPixel(x,y); } }
		 * }
		 */
		return pixmap;
	}

	Color hex(String s){
		return Color.valueOf(s);
	}

	public boolean empty(int value){
		return alpha(value) == 0;
	}

	public int alpha(int value){
		return((value & 0x000000ff));
	}
}
