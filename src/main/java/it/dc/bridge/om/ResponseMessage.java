package it.dc.bridge.om;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

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
	Options opt;
	
	/** The payload. */
	@Position(2)
	@Signature("ay")
	byte[] payload;
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getCode()
	 */
	public ResponseCode getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setCode(it.dc.bridge.om.CoAP.ResponseCode)
	 */
	public void setCode(ResponseCode code) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getOptions()
	 */
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setOptions(it.dc.bridge.om.Options)
	 */
	public void setOptions(Options options) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getPayload()
	 */
	public byte[] getPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#getPayloadString()
	 */
	public String getPayloadString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(byte[])
	 */
	public void setPayload(byte[] payload) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(java.lang.String)
	 */
	public void setPayload(String payload) {
		// TODO Auto-generated method stub
		
	}

}
