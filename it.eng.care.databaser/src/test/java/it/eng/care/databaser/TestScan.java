package it.eng.care.databaser;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import it.eng.care.databaser.model.ResourceScanner;

class TestScan {

	@Test
	void test() {

		ResourceScanner resourceScanner = new ResourceScanner();
		resourceScanner.addLocation(Paths.get("C:\\workspaces\\workspaceCareServerCpoe\\it.eng.care.database"));
		resourceScanner.scan();
		
		resourceScanner.getModules().forEach(mod->{
			
			System.out.println("Module "+mod.getModuleName()+" ("+mod.getChangeLogFiles().size()+")");
			
		});
		
	}

}
