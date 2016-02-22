package it.dc.bridge.om;

import java.util.Map;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

/**
 * The Class RequestMessage is used in the AllJoyn message exchanges.
 * It represents the CoAP request message and implements the {@link CoAPRequestMessage} interface.
 * 
 * @see CoAPRequestMessage
 * @see Options
 */
public class RequestMessage implements CoAPRequestMessage{

	/** The option fields. */
	@Position(0)
	@Signature("r")
	Options opt;
	
	/** The query attributes */
	@Position(1)
	@Signature("a{ss}")
	Map<String,String> attr;
	
	/** The message payload. */
	@Position(2)
	@Signature("ay")
	byte[] payload;
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#getOptions()
	 */
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#setOptions(it.dc.bridge.om.Options)
	 */
	public void setOptions(Options options) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#getAttributes()
	 */
	public Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#setAttributes(java.util.Map)
	 */
	public void setAttributes(Map<String, String> attributes) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#getPayload()
	 */
	public byte[] getPayload() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#getPayloadString()
	 */
	public String getPayloadString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#setPayload(byte[])
	 */
	public void setPayload(byte[] payload) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPRequestMessage#setPayload(java.lang.String)
	 */
	public void setPayload(String payload) {
		// TODO Auto-generated method stub
		
	}
	
	
}
