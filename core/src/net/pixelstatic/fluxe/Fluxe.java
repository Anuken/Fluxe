package net.pixelstatic.fluxe;

import net.pixelstatic.fluxe.modules.Editor;
import net.pixelstatic.utils.modules.ModuleController;

public class Fluxe extends ModuleController<Fluxe>{
	
	@Override
	public void init(){
		addModule(Editor.class);
	}
	
}
