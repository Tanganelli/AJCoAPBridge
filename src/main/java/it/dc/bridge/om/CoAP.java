package it.dc.bridge.om;

/**
 * CoAP defines the ResponseCode constants.
 * It is similar to the CoAP class inside Californium,
 * but it provides only the ResponseCode enumerator.
 */
public class CoAP {

	/**
	 * The enumeration of response codes.
	 */
	public enum ResponseCode {

		/** The created. */
		CREATED(65),

		/** The deleted. */
		DELETED(66),

		/** The valid. */
		VALID(67),

		/** The changed. */
		CHANGED(68),

		/** The content. */
		CONTENT(69),

		/** The continue. */
		CONTINUE(95),

		/** The bad request. */
		// Client error: 128--159
		BAD_REQUEST(128),

		/** The unauthorized. */
		UNAUTHORIZED(129),

		/** The bad option. */
		BAD_OPTION(130),

		/** The forbidden. */
		FORBIDDEN(131),

		/** The not found. */
		NOT_FOUND(132),

		/** The method not allowed. */
		METHOD_NOT_ALLOWED(133),

		/** The not acceptable. */
		NOT_ACCEPTABLE(134),

		/** The precondition failed. */
		PRECONDITION_FAILED(140),

		/** The request entity too large. */
		REQUEST_ENTITY_TOO_LARGE(141), 

		/** The unsupported content format. */
		UNSUPPORTED_CONTENT_FORMAT(143),

		/** The internal server error. */
		// Server error: 160--192
		INTERNAL_SERVER_ERROR(160),

		/** The not implemented. */
		NOT_IMPLEMENTED(161),

		/** The bad gateway. */
		BAD_GATEWAY(162),

		/** The service unavailable. */
		SERVICE_UNAVAILABLE(163),

		/** The gateway timeout. */
		GATEWAY_TIMEOUT(164),

		/** The proxy not supported. */
		PROXY_NOT_SUPPORTED(165);

		/** The code value. */
		public final int value;

		/**
		 * Instantiates a new response code with the specified integer value.
		 *
		 * @param value the integer value
		 */
		private ResponseCode(int value) {
			this.value = value;
		}

		/**
		 * Converts the specified integer value to a response code.
		 *
		 * @param value the value
		 * @return the response code
		 * @throws IllegalArgumentException if integer value is not recognized
		 */
		public static ResponseCode valueOf(int value) {
			switch (value) {
			// CoAPTest.testResponseCode ensures we keep this up to date 
			case 65: return CREATED;
			case 66: return DELETED;
			case 67: return VALID;
			case 68: return CHANGED;
			case 69: return CONTENT;
			case 95: return CONTINUE;
			case 128: return BAD_REQUEST;
			case 129: return UNAUTHORIZED;
			case 130: return BAD_OPTION;
			case 131: return FORBIDDEN;
			case 132: return NOT_FOUND;
			case 133: return METHOD_NOT_ALLOWED;
			case 134: return NOT_ACCEPTABLE;
			case 140: return PRECONDITION_FAILED;
			case 141: return REQUEST_ENTITY_TOO_LARGE;
			case 143: return UNSUPPORTED_CONTENT_FORMAT;
			case 160: return INTERNAL_SERVER_ERROR;
			case 161: return NOT_IMPLEMENTED;
			case 162: return BAD_GATEWAY;
			case 163: return SERVICE_UNAVAILABLE;
			case 164: return GATEWAY_TIMEOUT;
			case 165: return PROXY_NOT_SUPPORTED;
			// codes unknown at release time
			default:
				// Fallback to class
				if (value/32 == 4) return BAD_REQUEST;
				else if (value/32 == 5) return INTERNAL_SERVER_ERROR;
				/// Undecidable
				else throw new IllegalArgumentException("Unknown CoAP response code "+value);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		public String toString() {
			return String.format("%d.%02d", this.value/32, this.value%32);
		}

		/**
		 * Checks if is success.
		 *
		 * @param code the code
		 * @return true, if is success
		 */
		public static boolean isSuccess(ResponseCode code) {
			return 64 <= code.value && code.value < 96;
		}

		/**
		 * Checks if is client error.
		 *
		 * @param code the code
		 * @return true, if is client error
		 */
		public static boolean isClientError(ResponseCode code) {
			return 128 <= code.value && code.value < 160;
		}

		/**
		 * Checks if is server error.
		 *
		 * @param code the code
		 * @return true, if is server error
		 */
		public static boolean isServerError(ResponseCode code) {
			return 160 <= code.value && code.value < 192;
		}
	}
}
