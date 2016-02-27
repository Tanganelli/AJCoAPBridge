package it.dc.bridge.proxy;

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
	public CoAPProxy getInstance() {
		
		return proxy;
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
