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
	
	/**
	 * Creates a new AllJoyn CoAPResource and registers it
	 * to the AllJoyn Bus.
	 * 
	 * @param objectPath the object path
	 */
	public void addResource(String objectPath) {
		
		CoAPResource resource = new CoAPResource(objectPath);
		
		Status status = mBus.registerBusObject(resource, objectPath);
		if (Status.OK != status) {
		   System.out.println("BusAttachment.registerBusObject() failed: " + status);

		   return;
		}

		resources.add(resource);
		
	}
	
	/**
	 * Removes and unregisters AllJoyn CoAP resources.
	 * 
	 * @param objectPath location of the resources
	 */
	public void removeResource(String objectPath) {
		List<CoAPResource> toRemove = getResourcesFromNode(objectPath);
		
		for(CoAPResource c : toRemove) {
			mBus.unregisterBusObject(c);
			resources.remove(c);
		}
			
	}
	
	/*
	 * Returns the list of CoAP resources in the location
	 * given as parameter.
	 */
	private List<CoAPResource> getResourcesFromNode(String objectPath) {
		
		List<CoAPResource> ret = new ArrayList<CoAPResource>();

		for(CoAPResource c : resources) {
			if(c.getPath().startsWith(objectPath)) {
				ret.add(c);
			}
		}
			
		return ret;
	}

	/**
	 * Prints the object path of the registered AllJoyn resources.
	 */
	public void printResources() {
		
		if(resources.isEmpty()){
			System.out.println("There are not registered resources");
			return;
		}
		for(CoAPResource r : resources) {
			System.out.println(r.getPath());
		}
	}
		
	public static void main (String[]args) {
        
        Status status;
        System.out.println("Object Manager avviato");
        
	}
}
