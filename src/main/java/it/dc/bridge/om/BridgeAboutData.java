package it.dc.bridge.om;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.Version;

public class BridgeAboutData implements AboutDataListener {

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	public Map<String, Variant> getAboutData(String language) throws ErrorReplyBusException {

		LOGGER.fine("MyAboutData.getAboutData was called for `" + language + "` language.");

		if (!language.equalsIgnoreCase("en")) {
			LOGGER.fine("Language not supported. Default language (en) will be used.");
		}

		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
		aboutData.put("DeviceId", new Variant(new String("93c06771-c725-48c2-b1ff-6a2a59d445b8")));
		aboutData.put("ModelNumber", new Variant("A1B2C3"));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("SupportedLanguages", new Variant(new String[] { "en" }));
		aboutData.put("AppName", new Variant("CoAPBridge"));
		aboutData.put("Manufacturer", new Variant(new String("David Costa")));
		aboutData.put("Description", new Variant( "An application to interconnect AllJoyn and CoAP devices"));
		aboutData.put("SoftwareVersion", new Variant(new String("1.0")));
		aboutData.put("AJSoftwareVersion", new Variant(Version.get()));

		return aboutData;
	}

	public Map<String, Variant> getAnnouncedAboutData() throws ErrorReplyBusException {

		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
		aboutData.put("DeviceId", new Variant(new String("93c06771-c725-48c2-b1ff-6a2a59d445b8")));
		aboutData.put("ModelNumber", new Variant("A1B2C3"));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("AppName", new Variant("CoAPBridge"));
		aboutData.put("Manufacturer", new Variant(new String("David Costa")));
		aboutData.put("SoftwareVersion", new Variant(new String("1.0")));

		return aboutData;

	}

}