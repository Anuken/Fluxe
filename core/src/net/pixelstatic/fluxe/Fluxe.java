package net.pixelstatic.fluxe;

import net.pixelstatic.fluxe.modules.Input;
import net.pixelstatic.fluxe.modules.Renderer;
import net.pixelstatic.fluxe.world.World;
import net.pixelstatic.utils.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(World.class);
		addModule(Input.class);
		addModule(Renderer.class);
	}
	
}
