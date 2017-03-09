package io.anuke.fluxe;

import io.anuke.fluxe.modules.FluxeExample;
import io.anuke.fluxe.modules.LiveViewer;
import io.anuke.fluxe.modules.Input;
import io.anuke.ucore.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	boolean edit = false;
	
	@Override
	public void init(){
		if(edit){
			addModule(Input.class);
			addModule(LiveViewer.class);
		}else{
			addModule(FluxeExample.class);
		}
	}
}
