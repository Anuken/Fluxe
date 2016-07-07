package net.pixelstatic.fluxe.desktop;

import java.awt.Toolkit;

import net.pixelstatic.fluxe.Fluxe;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		config.width = tool.getScreenSize().width;
		config.height = tool.getScreenSize().height;
		
		new LwjglApplication(new Fluxe(), config);
	}
}
