package io.anuke.fluxe.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.anuke.fluxe.Fluxe;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		config.setWindowedMode(d.width, d.height);
		
		new Lwjgl3Application(new Fluxe(), config);
	}
}
