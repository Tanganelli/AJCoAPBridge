package it.dc.bridge;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.tools.resources.RDResource;

/**
 * The class ResourceDirectory provides a RD that implements
 * the functions required to register, maintain and remove the
 * CoAP resources. It does not implement resource lookup.
 * 
 * For the management of the resources, it uses the classes provided
 * by the cf-rd package.
 */ 
public class ResourceDirectory extends CoapServer{

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

	public ResourceDirectory() {

		RDResource rdResource = new RDResource(); 

		// add the rd resource to the server 
		add(rdResource); 

	}

	/**
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

	public static final void main(String[] args) {

		// create resource directory
		ResourceDirectory resourceDirectory = new ResourceDirectory();

		// add endpoints on all IP addresses
		resourceDirectory.addEndpoints();

		resourceDirectory.start(); 

		System.out.printf(ResourceDirectory.class.getSimpleName() + " listening on port %d.\n", resourceDirectory.getEndpoints().get(0).getAddress().getPort());
	}

}
