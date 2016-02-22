package it.dc.bridge.om;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

import it.dc.bridge.om.CoAP.ResponseCode;

public class ResponseMessage implements CoAPResponseMessage{

	@Position(0)
	@Signature("i")
	ResponseCode code;
	
	@Position(1)
	@Signature("r")
	Options opt;
	
	@Position(2)
	@Signature("ay")
	byte[] payload;
	
	public ResponseCode getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCode(ResponseCode code) {
		// TODO Auto-generated method stub
		
	}

	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOptions(Options options) {
		// TODO Auto-generated method stub
		
	}

	public byte[] getPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPayloadString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPayload(byte[] payload) {
		// TODO Auto-generated method stub
		
	}

	public void setPayload(String payload) {
		// TODO Auto-generated method stub
		
	}

}
