package it.dc.bridge.om;

/**
 * The CoAP response message interface.
 * The class that represents a response message implements this interface
 * and all methods it contains.
 * <p>
 * The following methods are implemented:
 * <ul>
 * <li> {@link #getCode()} get the response code </li>
 * <li> {@link #setCode(CoAP.ResponseCode)} set the response code </li>
 * <li> {@link #getOptions()} get the option fields </li>
 * <li> {@link #setOptions(Options)} set the option fields</li>
 * <li> {@link #getPayload()} get the message payload </li>
 * <li> {@link #getPayloadString()} get the message payload as a string</li>
 * <li> {@link #setPayload(byte[])} set the message payload from raw data </li>
 * <li> {@link #setPayload(String)} set the message payload from a string </li>
 * </ul>
 * @see CoAPRequestMessage
 */
public interface CoAPResponseMessage {

	/**
	 * Returns the response code of the message.
	 * @return the response code
	 * @see CoAP
	 */
	public CoAP.ResponseCode getCode();
	
	/**
	 * Sets the message response code.
	 * @param code the response code
	 * @see CoAP
	 */
	public void setCode(CoAP.ResponseCode code);

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
