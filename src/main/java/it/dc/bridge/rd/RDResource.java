package it.dc.bridge.rd;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.tools.resources.LinkAttribute;

import it.dc.bridge.om.AJObjectManagerApp;

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
	 *
	 * @param exchange the exchange
	 */ 
	@Override 
	public void handlePOST(CoapExchange exchange) { 

		// get name and lifetime from option query 
		LinkAttribute attr; 
		String endpointIdentifier = ""; 
		String domain = "local"; 
		RDNodeResource resource = null; 

		ResponseCode responseCode; 

		LOGGER.info("Registration request: "+exchange.getSourceAddress()); 

		List<String> query = exchange.getRequestOptions().getUriQuery(); 
		for (String q:query) { 
			// FIXME Do not use Link attributes for URI template variables 
			attr = LinkAttribute.parse(q); 

			if (attr.getName().equals(LinkFormat.END_POINT)) { 
				endpointIdentifier = attr.getValue(); 
			} 

			if (attr.getName().equals(LinkFormat.DOMAIN)) { 
				domain = attr.getValue(); 
			} 
		} 

		if (endpointIdentifier.equals("")) { 
			exchange.respond(ResponseCode.BAD_REQUEST, "Missing endpoint (?ep)"); 
			LOGGER.info("Missing endpoint: "+exchange.getSourceAddress()); 
			return; 
		} 

		for (Resource node : getChildren()) { 
			System.out.println("RDResource: name: "+node.getName()+" path: "+node.getPath());
			if (((RDNodeResource) node).getEndpointIdentifier().equals(endpointIdentifier) && ((RDNodeResource) node).getDomain().equals(domain)) { 
				resource = (RDNodeResource) node; 
			} 
		} 

		if (resource==null) { 

			String randomName; 
			do { 
				randomName = Integer.toString((int) (Math.random() * 10000)); 
			} while (getChild(randomName) != null); 

			resource = new RDNodeResource(endpointIdentifier, domain); 
			add(resource);
			System.out.println("RDResource: res name: "+resource.getName()+" res path: "+resource.getPath());

			responseCode = ResponseCode.CREATED; 
		} else { 
			responseCode = ResponseCode.CHANGED; 
		} 

		// set parameters of resource 
		if (!resource.setParameters(exchange.advanced().getRequest())) { 
			resource.delete(); 
			exchange.respond(ResponseCode.BAD_REQUEST); 
			return; 
		} 

		LOGGER.info("Adding new endpoint: "+resource.getContext()); 
		
		String location = resource.getURI();
		System.out.println("Location: "+location);

		// inform AJ Object Manager Application about the registration of a new resource
		AJObjectManagerApp objectManager = AJObjectManagerApp.getInstance();
		//objectManager.addResource(location, resource.getPath());
		
		objectManager.printResources();
		
		// inform client about the location of the new resource 
		exchange.setLocationPath(location); 

		// complete the request 
		exchange.respond(responseCode); 
	} 

}