package it.dc.bridge.om;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

/**
 * In order to avoid arrays of arrays of bytes, this class
 * represents an ETag and it is included inside the message options.
 * <p>
 * Since AllJoyn cannot marshal subclasses, this ETag class is not
 * a subclass of the options one.
 */
public class ETag {
	
	/** Resource-local identifier for differentiating
	 * between representations of the same resource that vary over time. */
	@Position(0)
	@Signature("ay")
	public byte[] etag;
	
	/**
	 * Instantiates a new ETag as an empty byte array.
	 */
	public ETag() {
		
		this.etag = new byte[] {};
		
	}
	
	/**
	 * Instantiates a new ETag starting from an ETag as parameter.
	 * 
	 * @param etag an ETag
	 */
	public ETag(byte[] etag) {
		
		if (etag == null)
			this.etag = new byte[] {};
		else
			this.etag = etag;
		
	}
	
	/**
	 * Returns the ETag.
	 * 
	 * @return the ETag
	 */
	public byte[] getEtag() {
		
		return this.etag;
		
	}
	
}
