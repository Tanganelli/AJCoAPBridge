package it.dc.bridge.om;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import it.dc.bridge.om.CoAP.RequestCode;

/**
 * The Class CoAPResource is an AllJoyn object representing the CoAP resource.
 * It implements the CoAPInterface interface which offers the CoAP RESTful methods
 * and the observing service methods.
 * <p>
 * The class implements also the AllJoyn BusObject interface.
 * <p>
 * In addition to the CoAPInterface methods, CoAPResource implements the following methods for
 * the object path management:
 * <ul>
 * <li> {@link #getPath()} returns the object path. </li>
 * <li> {@link #setPath(String)} sets the object path. </li>
 * </ul>
 * 
 * @see CoAPInterface
 */
public class CoAPResource implements CoAPInterface, BusObject{

	/** The object path. */
	private String objectPath;

	private int observers;

	/**
	 * Instantiates a new CoAP resource with an object path.
	 *
	 * @param path the object path
	 */
	public CoAPResource(String path) {

		this.objectPath = path;

		observers = 0;

	}

	/**
	 * Gets the object path.
	 *
	 * @return the object path
	 */
	public String getPath() {

		return objectPath;

	}

	/**
	 * Sets the object path.
	 *
	 * @param path the new object path
	 */
	public void setPath(String path) {

		this.objectPath = path;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Get(it.dc.bridge.om.RequestMessage)
	 */
	public ResponseMessage get(final RequestMessage request) throws BusException {

		ResponseMessage response = AJObjectManagerApp.getInstance().callMethod(objectPath, RequestCode.GET, request);

		return response;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Post(it.dc.bridge.om.RequestMessage)
	 */
	public ResponseMessage post(final RequestMessage request) throws BusException {

		ResponseMessage response = AJObjectManagerApp.getInstance().callMethod(objectPath, RequestCode.POST, request);

		return response;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Delete()
	 */
	public ResponseMessage delete() throws BusException {

		RequestMessage request = new RequestMessage();
		ResponseMessage response = AJObjectManagerApp.getInstance().callMethod(objectPath, RequestCode.DELETE, request);

		return response;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Registration()
	 */
	public void registration() throws BusException {

		// TODO check if the caller is already an observer
		if (observers++ == 0) {
			AJObjectManagerApp.getInstance().addObserver(objectPath);
		}

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Cancellation()
	 */
	public void cancellation() throws BusException {

		// TODO check if the caller was an observer
		if (--observers == 0) {
			AJObjectManagerApp.getInstance().removeObserver(objectPath);
		}

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Notification()
	 */
	public void notification(ResponseMessage message) throws BusException {

		// No code needed here

	}

}