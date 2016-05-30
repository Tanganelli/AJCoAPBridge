package it.dc.bridge.om;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
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
 * <li> {@link #registration(String, RequestMessage)} registration to the resource observing service. </li>
 * <li> {@link #cancellation(String)} cancellation from the resource observing service. </li>
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
@BusInterface (name="com.bridge.Coap", announced="true", descriptionLanguage="en", description="RESTful CoAP interface")
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
	@BusMethod (name="get", signature="r", replySignature="r", description="Send a GET method call")
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
	@BusMethod (name="post", signature="r", replySignature="r", description="Send a POST method call")
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
	@BusMethod (name="delete", replySignature="r", description="Send a DELETE method call")
	public ResponseMessage delete() throws BusException;

	/**
	 * The registration method is invoked by an AJ application
	 * that wants to registers to a resource notification.
	 *
	 * @param uniqueName the unique name of the AllJoyn client
	 * @param request the request message
	 * @return the status of the registration
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="registration", signature="sr", replySignature="i", description="Start to observe the resource")
	public Status registration(String uniqueName, RequestMessage request) throws BusException;

	/**
	 * The cancellation method is invoked by an AJ application
	 * that wants to unregister from a resource notification.
	 *
	 * @param uniqueName the unique name of the AllJoyn client
	 * @throws BusException AllJoyn bus exception
	 */
	@BusMethod (name="cancellation", signature="s", description="Stop to observe the resource")
	public void cancellation(String uniqueName) throws BusException;

	/**
	 * Notification is a signal sent by the AJ object when it
	 * receive a new notification from a CoAP resource.
	 *
	 * @param message the notification message. Its payload contains
	 * the resource representation.
	 * @throws BusException AllJoyn bus exception
	 */
	@BusSignal (name="notification", signature="r", description="A notification arrived")
	public void notification(ResponseMessage message) throws BusException;
	
	/**
	 * The Resource Type <i>rt</i> attribute is an opaque string used to assign
	 * an application-specific semantic type to a resource.
	 * 
	 * @return the Resource Type field
	 * @throws BusException AllJoyn bus exception
	 */
	@BusProperty (name="getResourceType", description="Returns the Resource Type field")
	public String getResourceType() throws BusException;
	
	/**
	 * The Interface Description <i>if</i> attribute is an opaque string used to
	 * provide a name or URI indicating a specific interface definition used
	 * to interact with the target resource.
	 * 
	 * @return the Interface Description field
	 * @throws BusException AllJoyn bus exception
	 */
	@BusProperty (name="getInterfaceDescription", description="Returns the Interface Description field")
	public String getInterfaceDescription() throws BusException;
	
	/**
	 * The Endpoint Name <i>ep</i> attribute is an opaque string used by
	 * a device during its registration on the RD.
	 * It must be unique within the CoAP network.
	 * 
	 * @return the Endpoint field
	 * @throws BusException AllJoyn bus exception
	 */
	@BusProperty (name="getEndpoint", description="Returns the Endpoint name")
	public String getEndpoint() throws BusException;

}