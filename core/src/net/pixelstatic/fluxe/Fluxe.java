package net.pixelstatic.fluxe;

import io.anuke.gdxutils.modules.ModuleController;
import net.pixelstatic.fluxe.modules.Controller;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(Controller.class);
		//addModule(Input.class);
		//addModule(FluxViewer.class);
	}
	
}
