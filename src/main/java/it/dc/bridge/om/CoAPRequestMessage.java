package it.dc.bridge.om;

import java.util.Map;

/**
 * The CoAP request message interface.
 * The class that represents a request message implements this interface
 * and all methods it contains.
 * <p>
 * The following methods are implemented:
 * <ul>
 * <li> {@link #getOptions()} get the option fields </li>
 * <li> {@link #setOptions(Options)} set the option fields</li>
 * <li> {@link #getAttributes()} get the query attributes </li>
 * <li> {@link #setAttributes(Map)} set the query attributes </li>
 * <li> {@link #getPayload()} get the message payload </li>
 * <li> {@link #getPayloadString()} get the message payload as a string</li>
 * <li> {@link #setPayload(byte[])} set the message payload from raw data </li>
 * <li> {@link #setPayload(String)} set the message payload from a string </li>
 * </ul>
 * @see CoAPResponseMessage
 */
public interface CoAPRequestMessage {

	/**
	 * Returns a class containing the option fields.
	 * @see Options
	 * @return the option fields
	 */
	public Options getOptions();
	
	/**
	 * Sets the options fields.
	 * @see Options
	 * @param options Options class containing the option fields.
	 */
	public void setOptions(Options options);
	
	/**
	 * The query attributes are used to perform query filtering.
	 * The query serves to further parameterize the resource.
	 * It consists in a sequence of arguments in the form of a ”key=value” pair.
	 * <p>
	 * An AllJoyn application specifies zero or more query options, 
	 * the Bridge composes the URI from them, and then it sends the message to the CoAP Server.
	 * <p>
	 * This method get the query attributes of a request message.
	 * @return a dictionary of key-value pairs
	 */
	public Map<String,String> getAttributes();
	
	/**
	 * Sets the query attributes of a requet message.
	 * @param attributes dictionary of key-value pairs
	 */
	public void setAttributes(Map<String,String> attributes);
	
	/**
	 * Gets the raw payload.
	 *
	 * @return the payload
	 */
	public byte[] getPayload();
	
	/**
	 * Gets the payload in the form of a string. Returns an empty string if no
	 * payload is defined.
	 * 
	 * @return the payload as string
	 */
	public String getPayloadString();
	
	/**
	 * Sets the payload from raw data.
	 *
	 * @param payload the raw payload
	 */
	public void setPayload(byte[] payload);
	
	/**
	 * Sets the UTF-8 bytes from the specified string as payload.
	 * 
	 * @param payload the payload as sting
	 */
	public void setPayload(String payload);
	
}
