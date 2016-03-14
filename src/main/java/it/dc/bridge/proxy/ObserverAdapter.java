package it.dc.bridge.proxy;

import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Response;

import it.dc.bridge.om.AJObjectManagerApp;

/**
 * The <tt>ObserverAdapter</tt> class extends the <tt>MessageObserverAdapter</tt>
 * class and overrides its <tt>onResponse(Response)</tt> method.
 * It is used to inform the <tt>AJObjectManagerApp</tt> about the arrival of a
 * notification.
 */
public class ObserverAdapter extends MessageObserverAdapter {

	private String path;

	/**
	 * Instantiates a new ObserverAdapter with the specific path.
	 * The path field is used to recognize the resource that sent the notification.
	 * 
	 * @param path the resource path
	 */
	public ObserverAdapter(String path) {

		this.path = path;

	}

	/**
	 * Invoked when a response arrives.
	 * The method informs the <tt>AJObjectManagerApp</tt> about the arrival
	 * of a new notification.
	 * 
	 * @param response the notification message
	 */
	@Override
	public void onResponse(final Response response) {

		AJObjectManagerApp.getInstance().notify(path, response);

	}

}