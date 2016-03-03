package it.dc.bridge.rd;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.tools.resources.LinkAttribute;

/**
 * An <tt>RDNodeResource</tt> represents the Endpoint.
 * It is created after a node registration and its children are
 * the resources offer by the Endpoint.
 * The resource is only for the purpose of the Update (POST) and
 * Removal (DELETE), and must not implement GET or PUT methods.
 */
public class RDNodeResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(RDNodeResource.class.getCanonicalName());

	/*
	 * After the lifetime expires, the endpoint has RD_VALIDATION_TIMEOUT seconds
	 * to update its entry before the RD enforces validation and removes the endpoint
	 * if it does not respond.
	 */
	private Timer lifetimeTimer;

	private int lifeTime;

	private String endpointIdentifier;
	private String endpointName;
	private String domain;
	private String endpointType;
	private String context;

	public RDNodeResource(String endpointID, String endpointName, String domain) {
		super(endpointID);		
		this.endpointIdentifier = endpointID;
		this.endpointName = endpointName;
		this.domain = domain;

	}

	/**
	 * Updates the endpoint parameters from POST requests.
	 * The parameters that can be changed are the Endpoint lifetime <i>lt</i>
	 * and the Endpoint context <i>con</i>, the only two parameters allowed
	 * in the registration update.
	 * 
	 * @param request A POST request with a {?lt,con} URI Template query
	 * 			and a Link Format payload.
	 * @return the result of the resource update function
	 */
	public boolean setParameters(Request request) {

		LinkAttribute attr;

		int newLifeTime = 86400;
		String newContext = "";

		/*
		 * get lifetime from option query - only for PUT request.
		 */
		List<String> query = request.getOptions().getUriQuery();
		for (String q : query) {

			attr = LinkAttribute.parse(q);

			if (attr.getName().equals(LinkFormat.LIFE_TIME)) {
				newLifeTime = attr.getIntValue();

				if (newLifeTime < 60) {
					LOGGER.info("Enforcing minimal RD lifetime of 60 seconds (was "+newLifeTime+")");
					newLifeTime = 60;
				}
			}

			if (attr.getName().equals(LinkFormat.CONTEXT)){
				newContext = attr.getValue();
			}
		}

		setLifeTime(newLifeTime);

		try {
			URI check;
			if (newContext.equals("")) {
				check = new URI("coap", "", request.getSource().getHostName(), request.getSourcePort(), "", "", ""); // required to set port
				context = check.toString().replace("@", "").replace("?", "").replace("#", ""); // URI is a silly class
			} else {
				check = new URI(context);
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
			return false;
		}

		return updateEndpointResources(request.getPayloadString());
	}

	/**
	 * Add a new resource to the node. E.g. the resource temperature or
	 * humidity. If the path is /readings/temp, temp will be a subResource
	 * of readings, which is a subResource of the node.
	 * 
	 * @param path the resource path
	 * @return the created resource
	 */
	public CoapResource addNodeResource(String path) {
		Scanner scanner = new Scanner(path);
		scanner.useDelimiter("/");
		String next = "";
		boolean resourceExist = false;
		Resource resource = this; // It's the resource that represents the endpoint

		CoapResource subResource = null;
		while (scanner.hasNext()) {
			resourceExist = false;
			next = scanner.next();
			for (Resource res : resource.getChildren()) {
				if (res.getName().equals(next)) {
					subResource = (CoapResource) res;
					resourceExist = true;
				}
			}
			if (!resourceExist) {
				subResource = new RDTagResource(next,true, this);
				resource.add(subResource);
			}
			resource = subResource;
		}
		subResource.setPath(resource.getPath());
		subResource.setName(next);
		scanner.close();
		return subResource;
	}

	@Override
	public void delete() {

		LOGGER.info("Removing endpoint: "+getContext());

		if (lifetimeTimer!=null) {
			lifetimeTimer.cancel();
		}

		super.delete();
		ResourceDirectory.getInstance().removeNode(this.getEndpointIdentifier());

	}

	/*
	 * GET only debug return endpoint identifier
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "RD update handle");
	}

	/**
	 * The update interface is used by an endpoint to refresh or update its
	 * registration with an RD.  To use the interface, the endpoint sends a
	 * POST request to the resource returned in the Location option in the
	 * response to the first registration. An update may update the
	 * lifetime or context parameters if they have changed since the last
	 * registration or update.
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {

		if (lifetimeTimer != null) {
			lifetimeTimer.cancel();
		}

		LOGGER.info("Updating endpoint: "+getContext());

		setParameters(exchange.advanced().getRequest());

		// complete the request
		exchange.respond(ResponseCode.CHANGED);

	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN);
	}

	/**
	 * Handles the DELETE request in the given CoAPExchange.
	 * DELETEs this node resource.
	 * The response code to a DELETE request should be a Deleted (2.02).
	 */
	@Override
	public void handleDELETE(CoapExchange exchange) {

		delete();

		exchange.respond(ResponseCode.DELETED);
	}

	/**
	 * Creates a new subResource for each resource the node wants
	 * register. Each resource is separated by ",". E.g. A node can
	 * register a resource for reading the temperature and another one
	 * for reading the humidity.
	 * @param linkFormat The registration payload
	 */
	private boolean updateEndpointResources(String linkFormat) {

		Scanner scanner = new Scanner(linkFormat);

		scanner.useDelimiter(",");
		List<String> pathResources = new ArrayList<String>();
		while (scanner.hasNext()) {
			pathResources.add(scanner.next());
		}
		for (String p : pathResources) {
			scanner = new Scanner(p);

			/*
			 * get the path of the endpoint's resource. E.g. from
			 * </readings/temp> it will select /readings/temp.
			 */
			String path = "", pathTemp = "";
			if ((pathTemp = scanner.findInLine("</.*?>")) != null) {
				path = pathTemp.substring(1, pathTemp.length() - 1);
			} else {
				scanner.close();
				return false;
			}

			CoapResource resource = addNodeResource(path);

			/*
			 * Since created the subResource, get all the attributes from
			 * the payload. Each parameter is separated by a ";".
			 */
			scanner.useDelimiter(";");
			//Clear attributes to make registration idempotent
			for(String attribute:resource.getAttributes().getAttributeKeySet()){
				resource.getAttributes().clearAttribute(attribute);
			}
			while (scanner.hasNext()) {
				LinkAttribute attr = LinkAttribute.parse(scanner.next());
				if (attr.getValue() == null)
					resource.getAttributes().addAttribute(attr.getName());
				else resource.getAttributes().addAttribute(attr.getName(), attr.getValue());
			}
			resource.getAttributes().addAttribute(LinkFormat.END_POINT, getEndpointIdentifier());

			ResourceDirectory.getInstance().addEntry(this, resource);

		}
		scanner.close();

		return true;
	}

	/*
	 * the following three methods are used to print the right string to put in
	 * the payload to respond to the GET request.
	 */
	public String toLinkFormat(List<String> query) {

		// Create new StringBuilder
		StringBuilder builder = new StringBuilder();

		// Build the link format
		buildLinkFormat(this, builder, query);

		// Remove last delimiter
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	public String toLinkFormatItem(Resource resource) {
		StringBuilder linkFormat = new StringBuilder();

		linkFormat.append("<"+getContext());
		linkFormat.append(resource.getURI().substring(this.getURI().length()));
		linkFormat.append(">");

		return linkFormat.append( LinkFormat.serializeResource(resource).toString().replaceFirst("<.+>", "") ).toString();
	}


	private void buildLinkFormat(Resource resource, StringBuilder builder, List<String> query) {
		if (resource.getChildren().size() > 0) {

			// Loop over all sub-resources
			for (Resource res : resource.getChildren()) {
				if (LinkFormat.matches(res, query)) {

					// Convert Resource to string representation and add
					// delimiter
					builder.append(toLinkFormatItem(res));
					builder.append(',');
				}
				// Recurse
				buildLinkFormat(res, builder, query);
			}
		}
	}



	/*
	 * Setter And Getter
	 */

	/**
	 * Returns the Endpoint identifier.
	 * This field is generated during registration.
	 * @return the endpoint identifier
	 */
	public String getEndpointIdentifier() {
		return endpointIdentifier;
	}

	/**
	 * Returns the Endpoint name <i>ep</i>.
	 * This field is mandatory during registration.
	 * @return the endpoint name
	 */
	public String getEndpointName() {
		return endpointName;
	}

	/**
	 * Returns the endpoint domain <i>d</i>.
	 * This field is optional during registration.
	 * If not specified, the domain is set to local.
	 * @return the endpoint domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Returns the Endpoint type <i>et</i>.
	 * This field is optional during registration.
	 * @return the endpoint type
	 */
	public String getEndpointType() {
		return endpointType;
	}

	/**
	 * Sets the Endpoint type to the specified value.
	 * @param endpointType the endpoint type
	 */
	public void setEndpointType(String endpointType) {
		this.endpointType = endpointType;
	}

	/**
	 * Returns the Endpoint context <i>con</i>.
	 * This field is optional during registration.
	 * If not specified, the context is set to the source address and port.
	 * @return the endpoint context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the Endpoint context to the specified value.
	 * @param context the endpoint context
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Returns the Endpoint lifetime <i>lt</i>.
	 * This field is optional during registration.
	 * If not specified, it is set to the default value 86400.
	 * @return the endpoint lifetime
	 */
	public int getLifetime() {
		return lifeTime;
	}

	/**
	 * Set either a new lifetime (for new resources, POST request) or update
	 * the lifetime (for PUT request)
	 * @param newLifeTime the new lifetime
	 */
	public void setLifeTime(int newLifeTime) {

		lifeTime = newLifeTime;

		if (lifetimeTimer != null) {
			lifetimeTimer.cancel();
		}

		lifetimeTimer = new Timer();
		lifetimeTimer.schedule(new ExpiryTask(this), lifeTime * 1000 + 2000);// from sec to ms

	}

	/**
	 * The timer task. When the time expires, the resource is deleted.
	 */
	class ExpiryTask extends TimerTask {
		RDNodeResource resource;

		public ExpiryTask(RDNodeResource resource) {
			super();
			this.resource = resource;
		}

		@Override
		public void run() {
			delete();
		}
	}

}

