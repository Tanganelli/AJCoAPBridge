package it.dc.bridge;

import it.dc.bridge.om.AJObjectManagerApp;
import it.dc.bridge.rd.ResourceDirectory;

public class Bridge {

	public static void main(String[] args) {
		
		ResourceDirectory resourceDirectory = ResourceDirectory.getInstance();
		AJObjectManagerApp objectManager = AJObjectManagerApp.getInstance();
				
		resourceDirectory.run();
		
		objectManager.run();		
		
	}
	
}
