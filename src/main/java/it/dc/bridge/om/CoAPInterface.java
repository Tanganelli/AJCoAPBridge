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
 * <li> {@link #get(RequestMessage)} the GET method. </li>
 * <li> {@link #post(RequestMessage)} the POST method. </li>
 * <li> {@link #delete()} the DELETE method. </li>
 * </ul>
 * <p>
 * In addition to them, the interface implements the methods and the signal
 * for the observing service:
 * <ul>
 * <li> {@link #registration()} registration to the resource observing service. </li>
 * <li> {@link #cancellation()} cancellation from the resource observing service. </li>
 * <li> {@link #notification(ResponseMessage)} signal that represents a resource notification. </li>
 * </ul>
 * <p>
 * The signatures of the methods parameters reflect the attributes inside
 * the {@link RequestMessage} and the {@link ResponseMessage} classes.
 * 
 * @see CoAPRequestMessage
 * @see CoAPResponseMessage
 * @see RequestMessage
 * @see ResponseMessage
 */
@BusInterface (name="com.coap.rest", announced="true")
public interface CoAPInterface {

	/**
	 * The GET method. It executes a GET method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @param request the request message
	 * @return response the response message. If the request is fulfilled,
	 * the message contains 2.05 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="get", signature="r", replySignature="r")
	public ResponseMessage get(RequestMessage request) throws BusException;

	/**
	 * The POST method. It executes a POST method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @param request the request message
	 * @return response the response message. If the request is fulfilled,
	 * the message contains 2.04 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="post", signature="r", replySignature="r")
	public ResponseMessage post(RequestMessage request) throws BusException;

	/**
	 * The DELETE method. It executes a DELETE method on the
	 * RESTful CoAP interface of the resource represented by
	 * the AJ object that implements this interface.
	 *
	 * @return response the response message. If the request is fulfilled,
	 * the message contains 2.02 as response code.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="delete", replySignature="r")
	public ResponseMessage delete() throws BusException;

	/**
	 * The registration method is invoked by an AJ application
	 * that wants to registers to a resource notification.
	 *
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="registration")
	public void registration() throws BusException;

	/**
	 * The cancellation method is invoked by an AJ application
	 * that wants to unregister from a resource notification.
	 *
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="cancellation")
	public void cancellation() throws BusException;

	/**
	 * Notification is a signal sent by the AJ object when it
	 * receive a new notification from a CoAP resource.
	 *
	 * @param message the notification message. Its payload contains
	 * the resource representation.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusSignal (name="notification", signature="r")
	public void notification(ResponseMessage message) throws BusException;

}