package it.dc.bridge.om;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

/**
 * The CoAPInterface is an AllJoyn interface implemented by
 * the AllJoyn objects representing a CoAP resource.
 * It implements the following RESTful methods:
 * <ul>
 * <li> {@link #Get(CoAPRequestMessage,CoAPResponseMessage)} the GET method. </li>
 * <li> {@link #Post(CoAPRequestMessage,CoAPResponseMessage)} the POST method. </li>
 * <li> {@link #Delete(CoAPResponseMessage)} the DELETE method. </li>
 * </ul>
 * <p>
 * In addition to them, the interface implements the methods and the signal
 * for the observing service:
 * <ul>
 * <li> {@link #Registration()} registration to the resource observing service. </li>
 * <li> {@link #Cancellation()} cancellation from the resource observing service. </li>
 * <li> {@link #Notification(CoAPResponseMessage)} signal that represents a resource notification. </li>
 * </ul>
 * @see CoAPRequestMessage
 * @see CoAPResponseMessage
 */
@BusInterface (name="com.coap.rest")
public interface CoAPInterface {

	/**
	 * The GET method. It executes a GET method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @param request the request message
	 * @param response the response message. If the request is fulfilled,
	 * the message contains 2.05 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod(signature="rr")
	public void Get(CoAPRequestMessage request, CoAPResponseMessage response) throws BusException;

	/**
	 * The POST method. It executes a POST method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @param request the request message
	 * @param response the response message. If the request is fulfilled,
	 * the message contains 2.04 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod(signature="rr")
	public void Post(CoAPRequestMessage request, CoAPResponseMessage response) throws BusException;

	/**
	 * The DELETE method. It executes a DELETE method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @param response the response message. If the request is fulfilled,
	 * the message contains 2.02 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod(signature="r")
	public void Delete(CoAPResponseMessage response) throws BusException;

	/**
	 * The registration method is invoked by an AJ application
	 * that wants to registers to a resource notification.
	 *
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod
	public void Registration() throws BusException;

	/**
	 * The cancellation method is invoked by an AJ application
	 * that wants to deregister from a resource notification.
	 *
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod
	public void Cancellation() throws BusException;

	/**
	 * Notification is a signal sent by the AJ object when it
	 * receive a new notification from a CoAP resource.
	 *
	 * @param message the notification message. Its payload contains
	 * the resource representation.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusSignal(signature="r")
	public void Notification(CoAPResponseMessage message) throws BusException;

}
