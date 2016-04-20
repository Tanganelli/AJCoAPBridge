package it.dc.bridge.om;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

/**
 * The Options class is a collection of CoAP option fields.
 * Both requests and responses may include a list of one or more options.
 * This class contains only the options used in the AllJoyn network.
 */
public class Options {
	
	/** the undefined value */
	private static final int UNDEFINED = -1;
	
	/** Representation format of the message payload. */
	@Position(0)
	@Signature("i")
	public int contentFormat;

	/** Resource-local identifier for differentiating
	 * between representations of the same resource that vary over time. */
	@Position(1)
	@Signature("ar")
	public ETag[] etags;

	/** Which Content-Format is acceptable to the client. */
	@Position(2)
	@Signature("i")
	public int accept;

	/** To make a request conditional on the current
	 * existence or the value of an ETag. */
	@Position(3)
	@Signature("ar")
	public ETag[] ifMatch;

	/** To make a request conditional on the nonexistence of the target resource. */
	@Position(4)
	@Signature("b")
	public boolean ifNoneMatch;

	/** Size information about the resource representation. */
	@Position(5)
	@Signature("i")
	public int size1;

	/**
	 * Instantiates a new option set.
	 */
	public Options() {
		
		setContentFormat(null);
		setEtag(null);
		setAccept(null);
		setIfMatch(null);
		setIfNoneMatch(false);
		setSize1(-1);
		
	}

	/**
	 * Instantiates a new option set equal to the specified one by deep-copying it.
	 * 
	 * @param origin the origin to be copied
	 */
	public Options(Options origin) {
		
		setContentFormat(origin.getContentFormat());
		setEtag(origin.getEtag());
		setAccept(origin.getAccept());
		setIfMatch(origin.getIfMatch());
		setIfNoneMatch(origin.getIfNoneMatch());
		setSize1(origin.getSize1());
		
	}

	/**
	 * Returns the Content-Format Identifier of the Content-Format option.
	 * 
	 * @return the ID as int or -1 if undefined
	 */
	public int getContentFormat() {
		
		return contentFormat;
		
	}

	/**
	 * Checks if the Content-Format option is present.
	 * 
	 * @return true if present
	 */
	public boolean hasContentFormat() {
		
		return contentFormat != UNDEFINED;
		
	}

	/**
	 * Sets the Content-Format ID of the Content-Format option.
	 * 
	 * @param contentFormat the content format ID
	 */
	public void setContentFormat(Integer contentFormat) {
		
		this.contentFormat = (contentFormat != null) ? contentFormat : UNDEFINED;
		
	}

	/**
	 * Returns the list of ETags.
	 * AllJoyn does not understand List type during marshalling,
	 * so ETags are maintained into an array.
	 * Instead, List is useful for Californium interoperation.
	 * 
	 * @return the ETag as a list
	 */
	public List<byte[]> getEtag() {
		
		List<byte[]> ret = new ArrayList<byte[]>();
		
		for (ETag e : etags)
			ret.add(e.getEtag());
		
		return ret;
		
	}

	/**
	 * Returns the number of ETag options.
	 * 
	 * @return the count
	 */
	public int getETagCount() {
		
		return getEtag().size();
		
	}

	/**
	 * Sets the ETag.
	 * AllJoyn does not understand List type during marshalling,
	 * so ETags are maintained into an array.
	 * Instead, List is useful for Californium interoperation.
	 * 
	 * @param etag list of ETag to set
	 */
	public void setEtag(List<byte[]> etag) {
		
		if(etag == null){
			// AJ does not allow null value (signature is "aay")
			this.etags = new ETag[]{};
		}else {
			this.etags = new ETag[etag.size()];
			for (int i = 0; i < etag.size(); i++) {
				this.etags[i] = new ETag(etag.get(i));
			}
		}
		
	}

	/**
	 * Returns the Content-Format Identifier of the Accept option.
	 * 
	 * @return the ID as int or -1 if undefined
	 */
	public int getAccept() {
		
		return accept;
		
	}

	/**
	 * Checks if the Accept option is present.
	 * 
	 * @return true if present
	 */
	public boolean hasAccept() {
		
		return accept != UNDEFINED;
		
	}

	/**
	 * Sets the Content-Format ID of the Accept option.
	 * 
	 * @param accept the Content-Format ID
	 */
	public void setAccept(Integer accept) {
		
		this.accept = (accept != null) ? accept : UNDEFINED;
		
	}

	/**
	 * Returns the If-Match ETag.
	 * AllJoyn does not understand List type during marshalling,
	 * so If-Match ETags are maintained into an array.
	 * Instead, List is useful for Californium interoperation.
	 * 
	 * @return the If-Match ETag as a list
	 */
	public List<byte[]> getIfMatch() {
		
		List<byte[]> ret = new ArrayList<byte[]>();
		
		for (ETag e : ifMatch)
			ret.add(e.getEtag());
		
		return ret;
		
	}

	/**
	 * Returns the number of If-Match options.
	 * 
	 * @return the count
	 */
	public int getIfMatchCount() {
		
		return getIfMatch().size();
		
	}

	/**
	 * Sets the If-Match options to an ETag.
	 * AllJoyn does not understand List type during marshalling,
	 * so If-Match ETags are maintained into an array.
	 * Instead, List is useful for Californium interoperation.
	 * 
	 * @param ifMatch the list of If-Match ETags to set
	 */
	public void setIfMatch(List<byte[]> ifMatch) {

		if(ifMatch == null){
			// AJ does not allow null value (signature is "aay")
			this.ifMatch = new ETag[]{};
		}else {
			this.ifMatch = new ETag[ifMatch.size()];
			for (int i = 0; i < ifMatch.size(); i++) {
				this.ifMatch[i] = new ETag(ifMatch.get(i));
			}
		}
		
	}

	/**
	 * Checks if the If-None-Match option is present.
	 * 
	 * @return true if present
	 */
	public boolean getIfNoneMatch() {
		
		return ifNoneMatch;
		
	}

	/**
	 * Sets or unsets the If-None-Match option.
	 * 
	 * @param ifNoneMatch the presence of the option
	 */
	public void setIfNoneMatch(Boolean ifNoneMatch) {
		
		this.ifNoneMatch = ifNoneMatch;
		
	}

	/**
	 * Returns the uint value of the Size1 option.
	 * 
	 * @return the Size1 value or null if the option is not present
	 */
	public Integer getSize1() {
		
		return (size1 > -1) ? size1 : null;
		
	}

	/**
	 * Sets the Size1 option value.
	 * 
	 * @param size1 the size of the request body
	 */
	public void setSize1(Integer size1) {
		
		this.size1 = (size1 != null) ? size1 : -1;
		
	}

}