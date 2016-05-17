package it.dc.bridge.om;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

import it.dc.bridge.om.CoAP.RequestCode;
import it.dc.bridge.om.CoAP.ResponseCode;
import it.dc.bridge.proxy.CoAPProxy;

/**
 * AJObjectManager is the AllJoyn class that provides
 * the CoAP resources to the AJ network. It takes new
 * resource registrations and registers AJ objects representing
 * that resources.
 * <p>
 * The class also deals with the method calls:
 * it receives calls from the AllJoyn network and then sends the
 * method call as a <tt>CoapRequest</tt> to the CoAP Server.
 */
public class AJObjectManagerApp implements Runnable {

	static {
		// load the AllJoyn library
		System.loadLibrary("alljoyn_java");
	}

	private static final short CONTACT_PORT=42;

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	/* the class instance */
	private static final AJObjectManagerApp objectManager = new AJObjectManagerApp();

	/* map containing the <object path, AJ object> pair for each registered object */
	private static Map<String, CoAPResource> resources = new ConcurrentHashMap<String, CoAPResource>();

	/* map containing the <object path, uniqueName, signal emitter> tuple for each object */
	private static Map<String, Map<String, SignalEmitter>> emitters = new ConcurrentHashMap<String, Map<String, SignalEmitter>>();

	/* map containing the <uniqueName, sessionId> for each joiner */
	private static Map<String, Integer> sessions = new ConcurrentHashMap<String, Integer>();

	/* a connection to the message bus */
	private static BusAttachment mBus;

	/* the bus listener */
	private static BusListener busListener;

	/* the CoAP interface for send signals */
	private static CoAPInterface objectInterface;

	/* the About object to send the About data */
	AboutObj aboutObj;

	/**
	 * The thread will run on application closing.
	 * It deals with objects unregistration and bus disconnection.
	 */
	private class OnCloseThread extends Thread {

		public void run() {

			Status status;

			// unannounce About object
			status = aboutObj.unannounce();
			if (status != Status.OK) {
				LOGGER.warning("Error unannouncing About object.");
			}

			// unregister objects
			for(Entry<String, CoAPResource> e : resources.entrySet()) {
				mBus.unregisterBusObject(e.getValue());
			}

			// unregister bus listener
			mBus.unregisterBusListener(busListener);

			// cancel the advertise name
			status = mBus.cancelAdvertiseName("com.bridge.coap", SessionOpts.TRANSPORT_ANY);
			if (status != Status.OK) {
				LOGGER.warning("Error canceling advertised name.");
			}

			// disconnect from the Bus
			mBus.disconnect();

		}
	}

	/*
	 * Since the class is a singleton, the constructor must be private
	 */
	private AJObjectManagerApp() {

	}

	/**
	 * The AJ Object Manager is a Singleton.
	 * This method returns the class instance.
	 * 
	 * @return the class instance
	 */
	public static AJObjectManagerApp getInstance() {

		return objectManager;

	}

	/**
	 * Creates a new AllJoyn {@link CoAPResource} and registers it
	 * to the AllJoyn Bus.
	 * 
	 * @param objectPath the object path
	 * @param resourceType the resource type (can be null)
	 * @param interfaceDescription the interface description (can be null)
	 */
	public synchronized void addResource(String objectPath, String resourceType, String interfaceDescription) {

		CoAPResource resource = new CoAPResource(objectPath, resourceType, interfaceDescription);

		// register the new object to the bus
		Status status = mBus.registerBusObject(resource, objectPath);
		if (Status.OK != status) {
			LOGGER.warning("BusAttachment.registerBusObject() failed: " + status);

			return;
		}

		LOGGER.info("Registered bus object: "+objectPath);

		// put the new object in the resources map
		resources.put(objectPath, resource);



		// announce the new object to the AJ network
		announce();

	}

	/**
	 * Removes and unregisters an AllJoyn {@link CoAPResource}.
	 * 
	 * @param objectPath location of the resource
	 */
	public synchronized void removeResource(String objectPath) {

		// unregister the object from the bus
		mBus.unregisterBusObject(resources.get(objectPath));

		// remove the object from the resource map
		resources.remove(objectPath);

		// remove all the signal emitter associated to the specified object
		emitters.remove(objectPath);

		// send the about data
		announce();

	}

	/**
	 * Send the method call to the CoAP Proxy.
	 * The request message to be sent to the Proxy is the Californium <tt>Request</tt>.
	 * So, the method creates the Request starting from a message implementing
	 * the {@link CoAPRequestMessage} interface.
	 * The method call response from the Proxy is a Californium <tt>Response</tt>.
	 * This method translates the response message into a {@link CoAPResponseMessage}.
	 * 
	 * @param path the URI path
	 * @param code the request code
	 * @param request a message implementing the request interface
	 * @return the response message
	 */
	public synchronized ResponseMessage callMethod(final String path, final RequestCode code, final CoAPRequestMessage request) {

		LOGGER.info("Object Manager received a "+code+" method call on the object "+path);

		// create a Californium request from the CoAPRequestMessage request
		Request coapRequest = getRequest(code, request);

		// send the method call to the Proxy
		Response coapResponse = CoAPProxy.getInstance().callMethod(path, coapRequest);

		// create a ResponseMessage from the Californium Response
		ResponseMessage response = getResponse(coapResponse);

		return response;

	}

	/**
	 * Sends to the <tt>CoAPProxy</tt> a request in order to receive
	 * future notifications from that resource.
	 * 
	 * @param objectPath the object path of the observable resource
	 * @param request the request message
	 * @return status code
	 */
	public synchronized Status register(String uniqueName, String objectPath, CoAPRequestMessage request) {

		Status status = Status.OK;

		Request coapRequest = getRequest(RequestCode.GET, request);

		CoAPResource resource = resources.get(objectPath);
		Integer sessionId = sessions.get(uniqueName);

		// create a signal emitter and associate it to the object
		SignalEmitter emitter = new SignalEmitter(resource, uniqueName, sessionId, SignalEmitter.GlobalBroadcast.Off);

		// if the observer is the first one, send registration to the CoAP server
		Map<String, SignalEmitter> tmpEmitters = emitters.get(objectPath);
		if (tmpEmitters == null) {
			tmpEmitters = new ConcurrentHashMap<String, SignalEmitter>();

			status = CoAPProxy.getInstance().register(objectPath, coapRequest);
		}

		// if the registration does not fail and the observer is not already registered, add it
		if (status == Status.OK && !tmpEmitters.containsKey(uniqueName)) {
			tmpEmitters.put(uniqueName, emitter);

			emitters.put(objectPath, tmpEmitters);

			LOGGER.info("Added signal emitter associated to: uniqueName="+uniqueName+" object="+objectPath);
		}

		return status;

	}

	/**
	 * Informs the <tt>CoAPProxy</tt> to stop receiving notifications.
	 * 
	 * @param objectPath the object path
	 */
	public synchronized void cancel(String uniqueName, String objectPath) {

		// remove the emitter with key <uniqueName, objectPath>
		Map<String, SignalEmitter> tmpEmitters = emitters.get(objectPath);
		if (tmpEmitters == null)
			return;
		tmpEmitters.remove(uniqueName);

		if (tmpEmitters.isEmpty()) {
			emitters.remove(objectPath);

			// there are no more observers for that resource
			CoAPProxy.getInstance().cancel(objectPath);
		} else {
			emitters.put(objectPath, tmpEmitters);
		}

		LOGGER.info("Removed signal emitter associated to: uniqueName="+uniqueName+" object="+objectPath);

	}

	/**
	 * Sends a notification for the specific object to the AllJoyn network.
	 * The method receives a CoAP message, translates it into a
	 * {@link ResponseMessage} and sends this one as an AllJoyn signal.
	 * <p>
	 * If a client wants to receive notifications, after it calls the 
	 * {@link CoAPInterface #registration(String)} method, it must add the match rule:
	 * <tt>addMatch(rule)</tt>, where <tt>rule</tt> is a string following the DBus specification.
	 * The rule has to specify three fields:
	 * <ul>
	 * <li><tt>interface</tt>: com.bridge.Coap</li>
	 * <li><tt>path</tt>: the object path of the resource it registered</li>
	 * </ul>
	 * If the rule is not added or not all the three fields are correctly set
	 * the client will not receive notifications.
	 * 
	 * @param objectPath the object path
	 * @param coapMessage the CoAP message to notify
	 */
	public void notify(String objectPath, Response coapMessage) {

		LOGGER.info("A notification arrived from object "+objectPath+" with code "+coapMessage.getCode());
		// create a ResponseMessage from a Californium Response
		ResponseMessage message = getResponse(coapMessage);

		// get the object signal emitters
		Map<String, SignalEmitter> tmpEmitters = emitters.get(objectPath);

		// for each emitter associated to the object, send the notification
		for(Entry<String, SignalEmitter> e : tmpEmitters.entrySet()) {

			SignalEmitter emitter = e.getValue();
			objectInterface = emitter.getInterface(CoAPInterface.class);

			try {
				// send the notification
				objectInterface.notification(message);
			} catch (BusException exception) {
				LOGGER.severe("AllJoyn BusException during notification.");
			}
		}

	}

	/**
	 * Starting from a {@link CoAPRequestMessage}, the method fills a new Californium
	 * <tt>Request</tt> message.
	 * 
	 * @param code the request code
	 * @param request the CoAP request
	 * @return the Californium CoAP request
	 */
	private Request getRequest(final RequestCode code, final CoAPRequestMessage request) {

		// create the request
		Request coapRequest = new Request(Code.valueOf(code.value));

		// set confirmable
		coapRequest.setConfirmable(true);
		coapRequest.setToken(new byte[0]);

		// copy the payload
		coapRequest.setPayload(request.getPayload());

		Options options = request.getOptions();

		// copy the options
		OptionSet coapOpt = new OptionSet();
		if(options.hasContentFormat())
			coapOpt.setContentFormat(options.getContentFormat());
		for(byte[] e : options.getEtag())
			coapOpt.addETag(e);
		if(options.hasAccept())
			coapOpt.setAccept(options.getAccept());
		for(byte[] e : options.getIfMatch())
			coapOpt.addIfMatch(e);
		coapOpt.setIfNoneMatch(options.getIfNoneMatch());
		if(options.getSize1() != null)
			coapOpt.setSize1(options.getSize1());

		// copy the query attributes
		Map<String,String> attributes = request.getAttributes();
		if(!attributes.isEmpty()) {
			List<String> queryAttrs = new ArrayList<String>();
			for(Map.Entry<String, String> entry : attributes.entrySet()) {
				queryAttrs.add(entry.getKey()+"="+entry.getValue());
			}
			StringBuilder builder = new StringBuilder();
			for(String s : queryAttrs){
				builder.append(s).append("&");
			}
			if (builder.length() > 0){
				builder.delete(builder.length() - 1, builder.length());
			}
			coapOpt.setUriQuery(builder.toString());
		}

		// set request options
		coapRequest.setOptions(coapOpt);

		return coapRequest;

	}

	/**
	 * Starting from a Californium <tt>Response</tt>, the method fills a new
	 * {@link CoAPResponseMessage}.
	 * 
	 * @param coapResponse the Californium CoAP response message
	 * @return the CoAPResponse message
	 */
	private ResponseMessage getResponse(Response coapResponse) {

		org.eclipse.californium.core.coap.CoAP.ResponseCode code = coapResponse.getCode();

		// create the response
		ResponseMessage response = new ResponseMessage(ResponseCode.valueOf(code.value));

		// copy the options
		OptionSet coapOpt = coapResponse.getOptions();
		Options options = new Options();
		if(coapOpt.hasContentFormat())
			options.setContentFormat(coapOpt.getContentFormat());
		options.setEtag(coapOpt.getETags());
		if(coapOpt.hasAccept())
			options.setAccept(coapOpt.getAccept());
		options.setIfMatch(coapOpt.getIfMatch());
		options.setIfNoneMatch(coapOpt.hasIfNoneMatch());
		options.setSize1(coapOpt.getSize1());

		response.setOptions(options);

		// copy the payload
		response.setPayload(coapResponse.getPayload());

		return response;
	}

	/**
	 * Starts the AllJoyn Object Manager application.
	 * The method does the follow:
	 * <ul>
	 * <li>Connects the Object Manager to the AllJoyn Bus</li>
	 * <li>Requests and advertises a well known name</li>
	 * <li>Binds the session port</li>
	 * </ul>
	 */
	public void start() {

		LOGGER.info("Starting AllJoyn server");

		Status status;

		mBus = new BusAttachment("CoAPBridge");

		// register bus listener
		busListener = new BusListener();
		mBus.registerBusListener(busListener);

		// connect to the bus
		status = mBus.connect();
		if (status != Status.OK) {
			LOGGER.warning("BusAttachment.connect() failed: " + status);
			System.exit(0);
			return;
		}
		LOGGER.info("BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));

		// create the About object
		aboutObj = new AboutObj(mBus);

		// request a well known name
		int flags = 0; //no request name flags
		status = mBus.requestName("com.bridge.coap", flags);
		if (status != Status.OK) {
			LOGGER.warning("BusAttachment.requestName failed: " + status);
			System.exit(0);
			return;
		}
		LOGGER.info("BusAttachment.request 'com.bridge.coap' successful");

		// advertise the well known name
		status = mBus.advertiseName("com.bridge.coap", SessionOpts.TRANSPORT_ANY);
		if (status != Status.OK) {
			LOGGER.warning("Status = " + status);
			mBus.releaseName("com.bridge.coap");
			return;
		}
		LOGGER.info("BusAttachment.advertiseName 'com.bridge.coap' successful");

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = true;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		// bind session port with the session options
		objectManager.bindSessionPort(contactPort, sessionOpts);

		status = aboutObj.announce(contactPort.value, new BridgeAboutData());
		if (status != Status.OK) {
			LOGGER.warning("Announce failed " + status.toString());
			return;
		}
		LOGGER.info("Announce called announcing SessionPort: " + contactPort.value);

		// add the OnCloseThread at the shutdown
		// TODO actually, it's not possible to exit from the Bridge
		Runtime.getRuntime().addShutdownHook(new OnCloseThread());

	}

	/*
	 * Bind to the session port with the given session options
	 */
	private void bindSessionPort(Mutable.ShortValue contactPort, SessionOpts sessionOpts) {

		Status status = mBus.bindSessionPort(contactPort, sessionOpts, 
				new SessionPortListener() {
			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				LOGGER.info("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}
			public void sessionJoined(short sessionPort, int id, String joiner) {
				LOGGER.info(String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
				sessions.put(joiner, id);
			}
		});
		if (status != Status.OK) {
			return;
		}
		LOGGER.info("BusAttachment.bindSessionPort successful");

	}

	/*
	 * Sends the about data to announce the new registered object.
	 * Before, it unannounces the previous About data.
	 */
	private void announce() {

		Status status;
		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		aboutObj.unannounce();

		status = aboutObj.announce(contactPort.value, new BridgeAboutData());
		if (status != Status.OK) {
			LOGGER.warning("Announce failed " + status.toString());
			return;
		}
		LOGGER.info("Announce called announcing SessionPort: " + contactPort.value);

	}

	public void run() {

		objectManager.start();

		try {
			synchronized(this){
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
