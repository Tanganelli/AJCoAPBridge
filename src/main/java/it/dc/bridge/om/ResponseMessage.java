package it.dc.bridge.om;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;
import org.eclipse.californium.core.coap.CoAP;

import it.dc.bridge.om.CoAP.ResponseCode;

/**
 * The Class ResponseMessage is used in the AllJoyn message exchanges.
 * It represents the CoAP response message and implements the {@link CoAPResponseMessage} interface.
 * 
 * @see CoAPResponseMessage
 * @see ResponseCode
 * @see Options
 */
public class ResponseMessage implements CoAPResponseMessage{

	/** The code. */
	@Position(0)
	@Signature("i")
	ResponseCode code;
	
	/** The opt. */
	@Position(1)
	@Signature("r")
	Options options;
	
	/** The payload. */
	@Position(2)
	@Signature("ay")
	byte[] payload;
	
	/**
	 * Instantiates a new response with the specified response code.
	 *
	 * @param code the response code
	 */
	public ResponseMessage(ResponseCode code) {
		
		this.code = code;
		
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getCode()
	 */
	public ResponseCode getCode() {

		return this.code;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setCode(it.dc.bridge.om.CoAP.ResponseCode)
	 */
	public void setCode(ResponseCode code) {

		this.code = code;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getOptions()
	 */
	public Options getOptions() {

		return this.options;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setOptions(it.dc.bridge.om.Options)
	 */
	public void setOptions(Options options) {

		this.options = options;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getPayload()
	 */
	public byte[] getPayload() {

		return this.payload;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getPayloadString()
	 */
	public String getPayloadString() {

		if (payload==null)
			return "";
		return new String(payload, CoAP.UTF8_CHARSET);
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(byte[])
	 */
	public void setPayload(byte[] payload) {

		this.payload = payload;
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(java.lang.String)
	 */
	public void setPayload(String payload) {

		if (payload == null) {
			this.payload = null;
		} else {
			setPayload(payload.getBytes(CoAP.UTF8_CHARSET));
		}
		
	}

}
