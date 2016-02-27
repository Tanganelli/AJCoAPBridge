package it.dc.bridge.om;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;

/**
 * AJObjectManager is the AllJoyn class that provides
 * the CoAP resources to the AJ network. It takes new
 * resource registrations and registers AJ objects representing
 * that resources.
 */
public class AJObjectManagerApp implements Runnable {

	static {
		System.loadLibrary("alljoyn_java");
	}

	private static final short CONTACT_PORT=42;
	
	private static final Logger LOGGER = Logger.getGlobal();

	private static final AJObjectManagerApp objectManager = new AJObjectManagerApp();
	
	private static List<CoAPResource> resources = new ArrayList<CoAPResource>();
	private static BusAttachment mBus;

	/*
	 * Since the class is a singleton, the constructor must be private
	 */
	private AJObjectManagerApp() {}
	
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
	public synchronized void addResource(String objectPath) {

		CoAPResource resource = new CoAPResource(objectPath);

		Status status = mBus.registerBusObject(resource, objectPath);
		if (Status.OK != status) {
			LOGGER.warning("BusAttachment.registerBusObject() failed: " + status);

			return;
		}

		resources.add(resource);

	}

	/**
	 * Removes and unregisters AllJoyn CoAP resources.
	 * 
	 * @param objectPath location of the resources
	 */
	public synchronized void removeResource(String objectPath) {
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

	/**
	 * Starts the AllJoyn Object Manager application.
	 * The method does the follow:
	 * <ul>
	 * <li>Connects the Object Manager to the AllJoyn Bus</li>
	 * <li>Requests and advertises a well known name</li>
	 * <li>Binds the session port</li>
	 * <li>Starts the OM as a server to listen the requests from the AJ network</li>
	 * </ul>
	 */
	public void start() {

		LOGGER.info("Starting AllJoyn server");

		mBus = new BusAttachment("CoAPBridge");

		// register bus listener
		BusListener listener = new BusListener();
        mBus.registerBusListener(listener);
        
		// connect to the bus
		Status status = mBus.connect();
		if (status != Status.OK) {
			LOGGER.warning("BusAttachment.connect() failed: " + status);
			System.exit(0);
			return;
		}
		LOGGER.info("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address")); 

		// request a well known name
		int flags = 0; //no request name flags
		status = mBus.requestName("com.bridge.coap", flags);
		if (status != Status.OK) {
			LOGGER.warning("BusAttachment.requestName failed: " + status);
			System.exit(0);
			return;
		}
		LOGGER.info("BusAttachment.request 'com.bridge.coap' successful");

		// advertise the well known name
		status = mBus.advertiseName("com.bridge.coap", SessionOpts.TRANSPORT_ANY);
		if (status != Status.OK) {
			LOGGER.warning("Status = " + status);
			mBus.releaseName("com.bridge.coap");
			return;
		}
		LOGGER.info("BusAttachment.advertiseName 'com.bridge.coap' successful");

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = true;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		// bind session port with the session options
		objectManager.bindSessionPort(contactPort, sessionOpts);

	}

	/*
	 * Bind to the session port with the given session options
	 */
	private void bindSessionPort(Mutable.ShortValue contactPort, SessionOpts sessionOpts) {

		Status status = mBus.bindSessionPort(contactPort, sessionOpts, 
				new SessionPortListener() {
			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				LOGGER.info("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}
			public void sessionJoined(short sessionPort, int id, String joiner) {
				LOGGER.info(String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
			}
		});
		if (status != Status.OK) {
			return;
		}
		LOGGER.info("BusAttachment.bindSessionPort successful");

	}
	
	public void run() {

		objectManager.start();
		
		try {
			synchronized(this){
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
