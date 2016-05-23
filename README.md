[![Build Status](https://travis-ci.org/cstdvd/AJCoAPBridge.svg?branch=master)](https://travis-ci.org/cstdvd/AJCoAPBridge)
# AJCoAPBridge
**AJCoAPBridge** is an application that enables AllJoyn applications to interact with CoAP devices.

## Configuration
### Obtain the AllJoyn source
The bridge requires the AllJoyn framework to run.
The framework source can be obtained in the AllSeen Alliance [download page](https://allseenalliance.org/framework/download).
The bridge was developed using AllJoyn v15.09 and it requires that version or later ones.

### Building the AllJoyn Framework
Depending on the target platform, check out the recommended steps to build AllJoyn following the [building documentation](https://allseenalliance.org/framework/documentation/develop/building).
**AJCoAPBridge** was tested on Linux, Windows and Mac OS X.

The default SCons script tries to build all of the language bindings by default.
We are only interested in the Java language binding, so the BINDINGS option can be used to select the language of interest.
```
$ scons BINDINGS=java
```

What **AJCoAPBridge** needs are the _alljoyn.jar_ file and the _liballjoyn_java.so_ (or _liballjoyn_java.dylib_ or _liballjoyn_java.dll_) libraries.

## Installation
In order to install **AJCoAPBridge** you have to compile the source with Maven.
Before it, you need to include the AllJoyn library. Starting from the root directory (where the _pom.xml_ file is), put _alljoyn.jar_ in the _/jar_ directory and the _liballjoyn.so_ file in the _/lib_ directory.
Then, run the Maven installer:
```
$ mvn clean install
```

## Execution
The executable JAR file is in the _/target_ directory. In order to include the AllJoyn native library, run it with the _java.library.path_ option set to the _/lib_ folder:
```
$ java -Djava.library.path=lib -jar target/bridge-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## User Guide
### CoAP Server
A CoAP device that wants to provide its resources to the AllJoyn network has to register on the bridge using its Resource Directory.
The resources registration is done by sending a *POST* request with */{+rd}{?ep,lt,con}* as URI template, where:
- *ep* is the endpoint name (mandatory)
- *lt* is the lifetime on the bridge (optional)
- *con* is the endpoint context, if different from the source IP address and port (optional)

In the payload the CoAP server should include the resources it wants to register as shown in the following example:
```
</sensors/temp>;ct=41;rt="temperature-c";if="sensor",
</sensors/light>;ct=41;rt="light-lux";if="sensor"
```
The Resource Directory then returns the resources location.
An endpoint must use that location when refreshing or deleting registrations using this interface.

### AllJoyn Client Application
#### Discovery
The bridge sends out about data every time a new CoAP resource is made available or removed. The about data is described in the [About definition](https://allseenalliance.org/framework/documentation/learn/core/about-announcement/interface)
and it contains all the AllJoyn paths of the objects provided by the bridge. Each of these object represent a CoAP resource.

An AllJoyn client can obtain the bridge about data by looking for the _com.bridge.Coap_ interface:
```
Status status = mBus.whoImplements("com.bridge.Coap");
```

#### AllJoyn Objects
An AllJoyn client can interact with CoAP resources using a proxy object (a representation of the _CoAPResource_ object provided by the bridge) on which it can call methods.
In order to obtain a proxy object, the object path has to be specified (known or taken from the about data):
```
mProxyObj =  mBus.getProxyBusObject("com.bridge.coap",objectPath,sessionId.value,new Class<?>[] { CoAPInterface.class, Properties.class, AllSeenIntrospectable.class});
```
The AllJoyn objects implement the _com.bridge.Coap_ interface and implement the RESTful methods:
- _get_
- _post_
- _delete_

Request and response messages (function parameter and returned value, respectively) are represented by the _RequestMessage_ and the _ResponseMessage_ classes.

The interface allows the client to read the resource attributes via its property functions:
- _getInterfaceDescription_
- _getResourceType_

#### Observing Service
**AJCoAPBridge** also implements the observing service. An AllJoyn application interested into observing a resource calls the _registration_ method on the object representing that resource.
Then, it can stop receiving notifications using the _cancellation_ method.

The bridge forwards notifications using signals. In order to receive these notifications a client application must register the interest in a signal by calling the _AddMatch_ method, in which it specifies the object interface and path:
```
Status status = mBus.addMatch("interface='com.bridge.Coap',path='/rd/4521/sensors/temp'");
```

The client must implement a signal handler to respond to the signal for which it has registered.
It is done implementing a class that contains a method with the _@BusSignalHandler_ annotation.
```
public static class MySignalHandler  {
    @BusSignalHandler(iface="it.dc.ajtest.CoAPInterface", signal="notification")
		public void notification(ResponseMessage message) {
			// do something
		}
	}
```
Then, the client must register the implemented signal handler:
```
status = mBus.registerSignalHandlers(mySignalHandler);
```
