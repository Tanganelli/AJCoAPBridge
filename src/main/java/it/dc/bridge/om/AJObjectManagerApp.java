package it.dc.bridge.om;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;

/**
 * AJObjectManager is the AllJoyn class that provides
 * the CoAP resources to the AJ network. It takes new
 * resource registrations and registers AJ objects representing
 * that resources.
 */
public class AJObjectManagerApp {

	static {
		   System.loadLibrary("alljoyn_java");
		}
	
	private static final AJObjectManagerApp objectManager = new AJObjectManagerApp();
	private BusAttachment mBus;
	
	private List<CoAPResource> resources = new ArrayList<CoAPResource>();
	
	/**
	 * Instantiates a new AJ Object Manager.
	 */
	private AJObjectManagerApp() {
		
		mBus = new BusAttachment("CoAPBridge", BusAttachment.RemoteMessage.Receive);
	}
	
	/**
	 * The AJ Object Manager is a Singleton.
	 * This method returns the class instance.
	 * 
	 * @return the class instance
	 */
	public static AJObjectManagerApp getInstance() {
		
		return objectManager;
		
	}
	
	public void addResource(String objectPath) {
		
		CoAPResource resource = new CoAPResource(objectPath);
		
		Status status = mBus.registerBusObject(resource, objectPath);
		if (Status.OK != status) {
		   System.out.println("BusAttachment.registerBusObject() failed: " + status);

		   return;
		}

		resources.add(resource);
		
	}
	
	public void printResources() {
		
		for(CoAPResource r : resources) {
			System.out.println(r.getPath());
		}
	}
	
	public static void main (String[]args) {
        
        Status status;
        System.out.println("Object Manager avviato");
        
	}
}
