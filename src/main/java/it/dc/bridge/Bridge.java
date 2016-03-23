package it.dc.bridge;

import it.dc.bridge.om.AJObjectManagerApp;
import it.dc.bridge.rd.ResourceDirectory;

/** 
 * The application connects an AllJoyn network with a CoAP network.
 * It provides to the AllJoyn devices some objects representing CoAP resources,
 * which allow the user to interact with them in a user-friendly manner.
 * Any CoAP resource that wants to be available for the AllJoyn network
 * must register its resources to the <tt>Bridge</tt>.
 * <p>
 * The <tt>Bridge</tt> is the main class of the application.
 * It instantiates and runs both the {@link ResourceDirectory} and the
 * {@link AJObjectManagerApp}, the components that interact, respectively,
 * with the CoAP network and the AllJoyn network.
 * <p>
 * AllJoyn applications can find the <tt>Bridge</tt> by its well-known name
 * <i>"com.bridge.coap"</i>.
 * <p>
 * A CoAP Server that wants to provide its resources to the AllJoyn network has to
 * register itself to the <tt>Bridge</tt>, in particular, it registers to the
 * <tt>ResourceDirectory</tt>.
 * An AllJoyn client application can find CoAP resources looking for AllJoyn objects
 * that implement the <i>"com.coap.rest"</i> interface.
 * Then, it can interact with CoAP servers using the two message classes:
 * <ul>
 * <li><tt>RequestMessage</tt></li>
 * <li><tt>ResponseMessage</tt></li>
 * </ul>
 * <p>
 * The <tt>CoAPInterface</tt> (the <i>"com.coap.rest"</i> interface mentioned before)
 * allows the application to call the RESTful methods on the object (GET, POST, DELETE)
 * and to register/unregister from its notifications.
 */
public class Bridge {

	public static void main(String[] args) {

		ResourceDirectory resourceDirectory = ResourceDirectory.getInstance();
		AJObjectManagerApp objectManager = AJObjectManagerApp.getInstance();

		resourceDirectory.run();

		objectManager.run();		

	}

}
