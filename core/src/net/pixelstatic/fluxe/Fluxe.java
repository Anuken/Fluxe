package net.pixelstatic.fluxe;

import net.pixelstatic.fluxe.modules.Controller;
import net.pixelstatic.gdxutils.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(Controller.class);
		//addModule(Input.class);
		//addModule(FluxViewer.class);
	}
	
}
