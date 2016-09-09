package net.pixelstatic.fluxe;

import net.pixelstatic.fluxe.modules.FluxViewer;
import net.pixelstatic.fluxe.modules.Input;
import net.pixelstatic.gdxutils.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(Input.class);
		addModule(FluxViewer.class);
	}
	
}
