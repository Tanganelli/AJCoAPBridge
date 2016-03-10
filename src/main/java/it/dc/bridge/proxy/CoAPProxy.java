package it.dc.bridge.proxy;

import java.util.logging.Logger;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;

import it.dc.bridge.rd.ResourceDirectory;

/**
 * The <tt>CoAPProxy</tt> class receives method calls from
 * the <tt>AJObjectManagerApp</tt> and sends the specific request
 * messages to the CoAP Server. It interacts also with the {@link ResourceDirectory}
 * in order to know the CoAP Server information (i.e., the resource path,
 * the node IP address, the node port).
 * <p>
 * The <tt>CoAPProxy</tt> implements a cache inside itself. When it receives a request,
 * first it check the cache for a valid response. If there is not a valid response for that
 * request, then the proxy sends a method call to the CoAP Server.
 * <p>
 * The cache is implemented the <tt>ProxyCacheResource</tt> class and the <tt>CacheResource</tt>
 * interface, provided by the Californium <i>cf-rd</i> package. The class has been re-implemented
 * because of incompleteness.
 */
public class CoAPProxy implements Runnable {

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	/* the timeout value */
	private static final int TIMEOUT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.ACK_TIMEOUT);

	/* the class instance */
	private static final CoAPProxy proxy = new CoAPProxy();

	/* the cache */
	private final ProxyCacheResource cache = new ProxyCacheResource(true);

	/*
	 * Since the CoAPProxy is a singleton,
	 * the constructor must be private.
	 */
	private CoAPProxy() {

	}

	/**
	 * The CoAP Proxy is a Singleton.
	 * This method returns the class instance.
	 * 
	 * @return the class instance
	 */
	public static CoAPProxy getInstance() {

		return proxy;

	}

	/**
	 * Obtains the context associated to the specific resource path
	 * and checks the cache for a valid response for the specific request.
	 * If the cache does not have a valid response, the method sends the
	 * specific request message to the CoAP Server and returns the received response.
	 * 
	 * @param RDPath the resource path inside the RD
	 * @param request the request message
	 * @return the response message
	 */
	public Response callMethod(final String RDPath, final Request request) {

		// take the node context from the RD (the path is unique within the RD)
		String context = ResourceDirectory.getInstance().getContextFromResource(RDPath);

		// take the resource path within the CoAP Server from the RD
		String path = ResourceDirectory.getInstance().getResourcePath(RDPath);

		request.setURI(context+path);

		// set uri-host and uri-port options
		OptionSet options = new OptionSet(request.getOptions());
		options.setUriHost(request.getDestination().getHostAddress());
		options.setUriPort(request.getDestinationPort());
		request.setOptions(options);

		Response response = null;

		// check the cache for a valid response
		response = cache.getResponse(request);
		if (response != null) {
			LOGGER.fine("Cache returned "+response);
			return response;
		}

		LOGGER.info("CoAP Proxy sends a "+request.getCode()+" method call to "+context+" on the resource "+path);

		// send request
		request.send();

		// wait for response
		try {
			response = request.waitForResponse(TIMEOUT);

			// timeout
			if (response == null) {
				LOGGER.warning("No response received.");
				return new Response(ResponseCode.GATEWAY_TIMEOUT);
			}
		} catch (InterruptedException e) {
			LOGGER.severe("Receiving of response interrupted: " + e.getMessage());
			return new Response(ResponseCode.INTERNAL_SERVER_ERROR);
		}

		// set the response timestamp
		long timestamp = System.nanoTime();
		response.setTimestamp(timestamp);

		request.setResponse(response);

		// cache response
		cache.cacheResponse(request, response);

		return response;

	}

	public void run() {
		// TODO Auto-generated method stub

	}

}