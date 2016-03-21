package it.dc.bridge.proxy;

import java.util.logging.Logger;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import it.dc.bridge.om.AJObjectManagerApp;

/**
 * <tt>ObserverThread</tt> class is a thread that receives notifications from 
 * a specific resource. A thread is identified by the resource it observes.
 * <p>
 * After receiving a notification, the thread caches the response for the 
 * specific request and then it sends the notification to the proxy.
 * <p>
 * The {@link CoAPProxy} runs one thread for each resource it needs to observe.
 */
public class ObserverThread extends Thread {

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	private boolean run = true;
	
	/* the observer resource */
	private String resource;

	/* the request message */
	private Request request;

	/**
	 * Instantiates a new thread identified by the resource it observes.
	 * 
	 * @param resource the resource it observes
	 * @param request the request it sends for receiving notifications.
	 */
	public ObserverThread(String resource, Request request) {

		super(resource);

		this.resource = resource;
		this.request = request;

	}
	
	public void stopRunning() {
		run = false;
	}

	/**
	 * During its execution, the <tt>ObserverThread</tt> waits for a notification,
	 * inserts it into the cache and sends the notification to the <tt>AJObjectManagerApp</tt>.
	 */
	public void run() {

		Response response = null;

		while(run) {

			response = null;

			try {
				response = request.waitForResponse();
			} catch (InterruptedException e) {
				LOGGER.severe("Exception while observing resource "+resource);
			}
			if(response != null) {
				CoAPProxy.getInstance().cacheResponse(request, response);
				AJObjectManagerApp.getInstance().notify(resource, response);
			}

		}
	}

}
