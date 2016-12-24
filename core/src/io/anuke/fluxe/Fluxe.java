package io.anuke.fluxe;

import io.anuke.fluxe.modules.Controller;
import io.anuke.fluxe.modules.FluxViewer;
import io.anuke.fluxe.modules.Input;
import io.anuke.ucore.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	boolean edit = false
	;
	
	@Override
	public void init(){
		if(edit){
			addModule(Input.class);
			addModule(FluxViewer.class);
		}else{
			addModule(Controller.class);
		}
		
	}
	
}
