package it.dc.bridge.om;

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
	public ResponseCode code;

	/** The opt. */
	public Options options;

	/** The payload. */
	public byte[] payload;

	/**
	 * Instantiates a new response message.
	 */
	public ResponseMessage() {

		// error code if not specified
		this.code = ResponseCode.INTERNAL_SERVER_ERROR;
		this.options = new Options();
		// AJ does not allow null value (signature is "ay")
		this.payload = new byte[]{};

	}

	/**
	 * Instantiates a new response with the specified response code.
	 *
	 * @param code the response code
	 */
	public ResponseMessage(ResponseCode code) {

		this.code = code;
		this.options = new Options();
		// AJ does not allow null value (signature is "ay")
		this.payload = new byte[]{};

	}

	public ResponseMessage(ResponseCode code, Options options, byte[] payload) {

		this.code = code;
		this.options = new Options(options);
		this.payload = payload;

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

		if (payload.length == 0)
			return "";
		return new String(payload, CoAP.UTF8_CHARSET);

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(byte[])
	 */
	public void setPayload(byte[] payload) {

		if (payload == null) {
			// AJ does not allow null value (signature is "ay")
			this.payload = new byte[]{};
		} else {
			this.payload = payload;
		}

	}

	/* (non-Javadoc)
	 * @see it.dc.bridge.om.CoAPResponseMessage#setPayload(java.lang.String)
	 */
	public void setPayload(String payload) {

		if (payload == null) {
			// AJ does not allow null value (signature is "ay")
			this.payload = new byte[]{};
		} else {
			setPayload(payload.getBytes(CoAP.UTF8_CHARSET));
		}

	}

}