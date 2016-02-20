package it.dc.bridge.om;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name="com.coap.rest")
public interface CoAPInterface {

	@BusMethod(signature="rr")
	public void Get(CoAPRequestMessage request, CoAPResponseMessage response) throws BusException;

	@BusMethod(signature="rr")
	public void Post(CoAPRequestMessage request, CoAPResponseMessage response) throws BusException;

	@BusMethod(signature="r")
	public void Delete(CoAPResponseMessage response) throws BusException;

	@BusMethod
	public void Registration() throws BusException;

	@BusMethod
	public void Cancellation() throws BusException;

	@BusSignal(signature="r")
	public void Notification(CoAPResponseMessage message) throws BusException;

}
