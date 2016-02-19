package it.dc.bridge.om;

public interface CoAPResponseMessage {

	public CoAP.ResponseCode getCode();
	public void setCode(CoAP.ResponseCode code);

	public Options getOptions();
	public void setOptions(Options options);

	public byte[] getPayload();
	public void setPayload(byte[] payload);

}
