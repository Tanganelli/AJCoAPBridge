package it.dc.bridge.om;

import java.util.ArrayList;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Status;

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

	/** The Resource Type */
	private String resourceType;

	/** The Interface Description */
	private String interfaceDescription;

	private ArrayList<String> observers = new ArrayList<String>();

	/**
	 * Instantiates a new CoAP resource with an object path.
	 *
	 * @param path the object path
	 * @param resourceType the Resource Type
	 * @param interfaceDescription the Interface Description
	 */
	public CoAPResource(String path, String resourceType, String interfaceDescription) {

		this.objectPath = path;
		this.resourceType = resourceType;
		this.interfaceDescription = interfaceDescription;

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
	public Status registration(String uniqueName, final RequestMessage request) throws BusException {

		// TODO extend observing as in 1.4 RFC 7641
		Status status = null;

		// if the list is empty register to the resource
		// FIXME it depends also on the request message fields
		if (observers.isEmpty()) {
			status = AJObjectManagerApp.getInstance().register(objectPath, request);
		}

		// if the client is not present, add it to the observer list
		if (status == Status.OK && !observers.contains(uniqueName)) {
			observers.add(uniqueName);
		}

		return status;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Cancellation()
	 */
	public void cancellation(String uniqueName) throws BusException {

		// remove the client from the list and if the list remains empty unregister
		if (observers.remove(uniqueName) && observers.isEmpty()) {
			AJObjectManagerApp.getInstance().cancel(objectPath);
		}

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#Notification()
	 */
	public void notification(ResponseMessage message) throws BusException {

		// No code needed here

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#getResourceType()
	 */
	public String getResourceType() {

		return resourceType;

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPInterface#getInterfaceDescription
	 */
	public String getInterfaceDescription() {

		return interfaceDescription;

	}

}