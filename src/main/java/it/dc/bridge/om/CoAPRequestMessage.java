package it.dc.bridge.om;

import java.util.Map;

public interface CoAPRequestMessage {

	public Options getOptions();
	public void setOptions(Options options);
	
	public Map<String,String> getAttributes();
	public void setAttributes(Map<String,String> attributes);
	
	public byte[] getPayload();
	public void setPayload(byte[] payload);
}
