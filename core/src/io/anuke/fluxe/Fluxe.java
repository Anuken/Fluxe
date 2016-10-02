package io.anuke.fluxe;

import io.anuke.fluxe.modules.Controller;
import io.anuke.ucore.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(Controller.class);
		//addModule(Input.class);
		//addModule(FluxViewer.class);
	}
	
}
