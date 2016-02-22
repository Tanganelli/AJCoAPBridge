package it.dc.bridge.om;

import java.util.Map;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

public class RequestMessage implements CoAPRequestMessage{

	@Position(0)
	@Signature("r")
	Options opt;
	
	@Position(1)
	@Signature("a{ss}")
	Map<String,String> attr;
	
	@Position(2)
	@Signature("ay")
	byte[] payload;
	
	public Options getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setOptions(Options options) {
		// TODO Auto-generated method stub
		
	}
	public Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setAttributes(Map<String, String> attributes) {
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
