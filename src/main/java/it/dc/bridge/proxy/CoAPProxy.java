package it.dc.bridge.proxy;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import it.dc.bridge.rd.ResourceDirectory;

public class CoAPProxy implements Runnable {

	private static final CoAPProxy proxy = new CoAPProxy();
	
	/*
	 * Since the CoAPProxy is a singleton,
	 * the constructor must be private.
	 */
	private CoAPProxy() {}
	
	/**
	 * The CoAP Proxy is a Singleton.
	 * This method returns the class instance.
	 * 
	 * @return the class instance
	 */
	public static CoAPProxy getInstance() {
		
		return proxy;
		
	}
	
	public static Response callMethod(final String path, final Request request) {
		
		/*String node = ResourceDirectory.getInstance().getContext(null);
		System.out.println("CoAP Server context:"+node);
		CoapClient client = new CoapClient(node+"/.well-known/core");
		
		CoapResponse response = client.advanced(request);
		if (response!=null) {

			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());

		} else {
			System.out.println("No response received.");
		}*/
		
		Response pippo = new Response(null);
		return pippo;
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
