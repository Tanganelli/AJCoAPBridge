package it.dc.bridge.rd;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.tools.resources.LinkAttribute;

/**
 * The Class RDResource.
 */
public class RDResource extends CoapResource { 

	/**
	 * Instantiates a new RD resource.
	 */
	public RDResource() { 
		this("rd"); 
	} 

	/**
	 * Instantiates a new RD resource.
	 *
	 * @param resourceIdentifier the resource identifier
	 */
	public RDResource(String resourceIdentifier) { 
		super(resourceIdentifier); 
		getAttributes().addResourceType("core.rd"); 
	} 

	/**
	 * POSTs a new sub-resource to this resource. The name of the new 
	 * sub-resource is a random number if not specified in the Option-query.
	 * The registration can have a {?ep,d,et,lt,con} URI Template query.
	 * {ep,d,et} are set here, since they are allowed only during registration.
	 * {lt,con} are set in the <tt>setParameters</tt> function, used by the
	 * registration update too.
	 *
	 * @param exchange the exchange
	 */ 
	@Override 
	public void handlePOST(CoapExchange exchange) { 

		// get name and lifetime from option query 
		LinkAttribute attr; 
		String endpointName = "";
		String domain = "local";
		String endpointType = "";

		RDNodeResource resource = null; 

		ResponseCode responseCode; 

		LOGGER.info("Registration request: "+exchange.getSourceAddress()); 

		List<String> query = exchange.getRequestOptions().getUriQuery(); 
		for (String q:query) { 

			attr = LinkAttribute.parse(q); 

			if (attr.getName().equals(LinkFormat.END_POINT)) { 
				endpointName = attr.getValue(); 
			} 

			if (attr.getName().equals(LinkFormat.DOMAIN)) { 
				domain = attr.getValue(); 
			} 

			if (attr.getName().equals(LinkFormat.END_POINT_TYPE)) {
				endpointType = attr.getValue();
			}

		} 

		// the endpoint name is mandatory during registration
		if (endpointName.equals("")) { 
			exchange.respond(ResponseCode.BAD_REQUEST, "Missing endpoint (?ep)"); 
			LOGGER.info("Missing endpoint: "+exchange.getSourceAddress()); 
			return; 
		} 

		// the endpoint name is an identifier that must be unique within a domain
		for (Resource node : getChildren()) { 
			if (((RDNodeResource) node).getEndpointName().equals(endpointName) && ((RDNodeResource) node).getDomain().equals(domain)) { 
				resource = (RDNodeResource) node; 
			} 
		} 

		if (resource==null) { 

			// generate a random identifier for the resource
			String randomName; 
			do { 
				randomName = Integer.toString((int) (Math.random() * 10000)); 
			} while (getChild(randomName) != null);

			resource = new RDNodeResource(randomName, endpointName, domain);
			resource.setEndpointType(endpointType);

			add(resource);

			responseCode = ResponseCode.CREATED; 
		} else { 
			responseCode = ResponseCode.CHANGED; 
		} 

		// set parameters of the resource: {lt,con}
		if (!resource.setParameters(exchange.advanced().getRequest())) { 
			resource.delete(); 
			exchange.respond(ResponseCode.BAD_REQUEST); 
			return; 
		} 

		LOGGER.info("Adding new endpoint: "+resource.getContext());

		String location = resource.getURI();

		ResourceDirectory.getInstance().addNode(resource.getEndpointIdentifier(), resource.getContext());

		// inform client about the location of the new resource 
		exchange.setLocationPath(location); 

		// complete the request 
		exchange.respond(responseCode); 
	} 

}