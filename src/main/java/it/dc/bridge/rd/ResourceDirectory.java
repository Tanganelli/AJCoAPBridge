package it.dc.bridge.rd;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import it.dc.bridge.om.AJObjectManagerApp;

/**
 * The class <tt>ResourceDirectory</tt> provides a RD that implements
 * the functions required to register, maintain and remove the
 * CoAP resources. It does not implement resource lookup, as
 * described in <i>draft-ietf-core-resource-directory-05</i>.
 * <p>
 * For the management of the resources, <tt>ResourceDirectory</tt>
 * uses the classes provided by the Californium <i>cf-rd</i> package.
 * <p>
 * The <tt>ResourceDirectory</tt> has been re-implemented because of
 * incompleteness and the interaction with the other <tt>Bridge</tt>
 * components: when a resource registration or a resource removal
 * occur, the <tt>ResourceDirectory</tt> has to notify the <tt>AJObjectManagerApp</tt>
 * and the <tt>CoAPProxy</tt>.
 */ 
public class ResourceDirectory extends CoapServer implements Runnable {

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	/* the class instance */
	private static final ResourceDirectory resourceDirectory = new ResourceDirectory();

	/* CoAP default port */
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	/* Map containing the <identifier, context> pair for each registered node */
	private Map<String, String> contexts = new ConcurrentHashMap<String, String>();
	/* Map containing the <resource, node> pair for each registered resource */
	private Map<String, String> resources = new ConcurrentHashMap<String, String>();
	/* Map containing the <resource, type> pair for each registered resource */
	private Map<String, String> resourceType = new ConcurrentHashMap<String, String>();
	/* Map containing the <resource, interface> pair for each registered resource */
	private Map<String, String> interfaceDescription = new ConcurrentHashMap<String, String>();
	/* Map containing the <resource, path> pair for each registered resource */
	private Map<String, String> paths = new ConcurrentHashMap<String, String>();

	/*
	 * Instantiates a new Resource Directory and adds to it the <i>/rd</i> resource.
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
	 * @param nodeID node identifier
	 * @param context node context
	 */
	public synchronized void addNode(String nodeID, String context) {

		contexts.put(nodeID, context);

	}

	/**
	 * Returns the context to which the specified node is mapped,
	 * or null if this map contains no mapping for the node identifier
	 * 
	 * @param nodeID the node identifier
	 * @return the node context, if present
	 */
	public synchronized String getContext(String nodeID) {

		return contexts.get(nodeID);

	}

	/**
	 * Returns the node context starting from a specified resource path.
	 * 
	 * @param resource the resource path
	 * @return the node context
	 */
	public synchronized String getContextFromResource(String resource) {

		String node = resources.get(resource);

		return getContext(node);

	}

	/**
	 * Removes the mapping for a node identifier from this map if it is present.
	 * Informs the resource map about the node removal.
	 * 
	 * @param nodeID the node identifier
	 */
	public synchronized void removeNode(String nodeID) {

		contexts.remove(nodeID);
		removeEntries(nodeID);

	}

	/**
	 * Associates the specified resource with the specified node 
	 * in the resource map.
	 * Adds the node context if not present or updates it if was changed.
	 * Then, the methods informs the {@link AJObjectManagerApp} about the
	 * arrival of a new resource.
	 * 
	 * @param node the node resources
	 * @param resource the new registered resource
	 */
	public synchronized void addEntry(RDNodeResource node, CoapResource resource) {

		// TODO store the entry in the database, if implemented

		// put the <resource, node> and <node, context> pairs to the hash maps
		resources.put(resource.getURI(), node.getEndpointIdentifier());
		addNode(node.getEndpointIdentifier(), node.getContext());

		/* 
		 * put the <uri, path> pair to the hash map:
		 * the uri is the resource path within the RD;
		 * the path is the resource path within the node
		 */
		String path = resource.getURI().substring(node.getURI().length());
		paths.put(resource.getURI(), path);

		/*
		 * if present, put the resource types in the map
		 */
		String type = null;
		if(!resource.getAttributes().getResourceTypes().isEmpty()) {
			type = resource.getAttributes().getResourceTypes().get(0);
		}
		if (type != null) {
			resourceType.put(resource.getURI(), type);
		}

		/*
		 * if present, put the interface description in the map
		 */
		String interfaceDes = null;
		if(!resource.getAttributes().getResourceTypes().isEmpty()) {
			interfaceDes = resource.getAttributes().getInterfaceDescriptions().get(0);
		}
		if (interfaceDes != null) {
			interfaceDescription.put(resource.getURI(), interfaceDes);
		}

		// inform the Object Manager about the new resource
		AJObjectManagerApp.getInstance().addResource(resource.getURI());

	}

	/**
	 * Removes the mapping for the resources with a specific associated node.
	 * For each removed resource, the method informs the {@link AJObjectManagerApp} about
	 * the resource removal.
	 * 
	 * @param nodeID the node identifier
	 */
	public synchronized void removeEntries(String nodeID) {

		//TODO delete entries from the database, if implemented

		for(Map.Entry<String, String> e : resources.entrySet()) {
			if(e.getValue().equals(nodeID)) {
				resources.remove(e.getKey());
				paths.remove(e.getKey());
				resourceType.remove(e.getKey());

				// inform the Object Manager about the resource removal
				AJObjectManagerApp.getInstance().removeResource(e.getKey());
			}
		}
	}

	/**
	 * Returns the resource path within its parent node starting
	 * from the resource path within the <tt>ResourceDirectory</tt>.
	 * 
	 * @param path the resource uri
	 * @return the resource path
	 */
	public synchronized String getResourcePath(String path) {

		return paths.get(path);

	}

	/**
	 * Prints all the entries for both the context map and the resource map.
	 */
	public synchronized void printMaps() {

		System.out.println("Context map:");
		for(Map.Entry<String,String> e : contexts.entrySet()) {
			System.out.println(e.getKey()+" - "+e.getValue());
		}
		System.out.println("\nResource map:");
		for(Map.Entry<String,String> e : resources.entrySet()) {
			System.out.println(e.getKey()+" - "+e.getValue());
		}

	}

	public void run() {

		// add endpoints on all IP addresses
		resourceDirectory.addEndpoints();

		resourceDirectory.start(); 

		LOGGER.info(ResourceDirectory.class.getSimpleName() + " listening on port "+resourceDirectory.getEndpoints().get(0).getAddress().getPort());

	}

}