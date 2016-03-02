package it.dc.bridge.rd;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

/**
 * The class <tt>ResourceDirectory</tt> provides a RD that implements
 * the functions required to register, maintain and remove the
 * CoAP resources. It does not implement resource lookup, as
 * described in <i>draft-ietf-core-resource-directory-05</i>.
 * <p>
 * For the management of the resources, <tt>ResourceDirectory</tt>
 * uses the classes provided by the Californium <i>cf-rd</i> package.
 */ 
public class ResourceDirectory extends CoapServer implements Runnable {

	private static final ResourceDirectory resourceDirectory = new ResourceDirectory();
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	/* Map containing the identifier-context pair for each registered node */
	private Map<String,String> contexts = new HashMap<String,String>();
	
	/**
	 * Instantiates a new Resource Directory.
	 * Since it is a Singleton, the constructor is private.
	 */
	private ResourceDirectory() {

		RDResource rdResource = new RDResource(); 

		// add the rd resource to the server 
		add(rdResource); 

	}

	/**
	 * The Resource Directory is a Singleton.
	 * This method returns the class instance.
	 * @return the class instance
	 */
	public static ResourceDirectory getInstance() {

		return resourceDirectory;

	}

	/*
	 * Add individual endpoints listening on default CoAP port
	 * on all IP addresses of all network interfaces.
	 */
	private void addEndpoints() {

		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (!addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}
	
	/**
	 * Associates the specified context with the specified node 
	 * in the context map. 
	 * If the map previously contained a mapping for the node, 
	 * the old context is replaced by the specified context.
	 * 
	 * @param path node identifier
	 * @param context node context
	 */
	public void addContext(String path, String context) {
		
		contexts.put(path, context);
		
	}
	
	/**
	 * Returns the context to which the specified node is mapped,
	 * or null if this map contains no mapping for the node identifier
	 * 
	 * @param path the node identifier
	 * @return the node context, if present
	 */
	public String getContext(String path) {
		
		return contexts.get(path);
		
	}
	
	/**
	 * Removes the mapping for a node identifier from this map if it is present.
	 * 
	 * @param path the node identifier
	 */
	public void removeContext(String path) {
		
		contexts.remove(path);
		
	}
	
	public void run() {

		// add endpoints on all IP addresses
		resourceDirectory.addEndpoints();

		resourceDirectory.start(); 

		System.out.printf(ResourceDirectory.class.getSimpleName() + " listening on port %d.\n", resourceDirectory.getEndpoints().get(0).getAddress().getPort());

	}

}
