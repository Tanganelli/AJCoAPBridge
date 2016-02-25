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
import org.eclipse.californium.core.CoapServer;

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

	/** The logger. */
	private final static Logger LOGGER = Logger.getLogger(CoapServer.class.getCanonicalName());
	private static final short CONTACT_PORT=42;
	
	static boolean sessionEstablished = false;
    static int sessionId;

	private static final AJObjectManagerApp objectManager = new AJObjectManagerApp();
	private BusAttachment mBus;

	private List<CoAPResource> resources = new ArrayList<CoAPResource>();

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

	public void start() {

		LOGGER.info("Starting AllJoyn server");

		mBus = new BusAttachment("CoAPBridge");

		BusListener listener = new MyBusListener();
        mBus.registerBusListener(listener);
        
		Status status = mBus.connect();
		if (status != Status.OK) {
			System.out.println("BusAttachment.connect() failed: " + status);
			System.exit(0);
			return;
		}
		System.out.println("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address")); 

		int flags = 0; //no request name flags
		status = mBus.requestName("com.bridge.coap", flags);
		if (status != Status.OK) {
			System.out.println("BusAttachment.requestName failed: " + status);
			System.exit(0);
			return;
		}
		System.out.println("BusAttachment.request 'com.bridge.coap' successful");
		
		status = mBus.advertiseName("com.bridge.coap", SessionOpts.TRANSPORT_ANY);
        if (status != Status.OK) {
            System.out.println("Status = " + status);
            mBus.releaseName("com.bridge.coap");
            return;
        }
        System.out.println("BusAttachment.advertiseName 'com.bridge.coap' successful");

	}
	
	private void bindSessionPort(Mutable.ShortValue contactPort, SessionOpts sessionOpts) {
		
		Status status = mBus.bindSessionPort(contactPort, sessionOpts, 
                new SessionPortListener() {
            public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                System.out.println("SessionPortListener.acceptSessionJoiner called");
                if (sessionPort == CONTACT_PORT) {
                    return true;
                } else {
                    return false;
                }
            }
            public void sessionJoined(short sessionPort, int id, String joiner) {
                System.out.println(String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
                sessionId = id;
                sessionEstablished = true;
            }
        });
        if (status != Status.OK) {
            return;
        }
        System.out.println("BusAttachment.bindSessionPort successful");
		
	}

	private static class MyBusListener extends BusListener {
        public void nameOwnerChanged(String busName, String previousOwner, String newOwner) {
            if ("com.my.well.known.name".equals(busName)) {
                System.out.println("BusAttachement.nameOwnerChanged(" + busName + ", " + previousOwner + ", " + newOwner);
            }
        }
    }
	
	public static void main(String[] args) {
		
		objectManager.start();

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = false;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;
		
		objectManager.bindSessionPort(contactPort, sessionOpts);
		
		while (!sessionEstablished) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }
        System.out.println("BusAttachment session established");

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }

	}
}
