package it.dc.bridge.om;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.Version;

/**
 * The class implements the <tt>AboutDataListener</tt> interface,
 *  that supplies the list of properties required for Announce signal payload.
 * The about data contains the basic app information like app name,
 * device name, manufacturer, and model number.
 * <p>
 * The method {@link #getAnnouncedAboutData()} is called by the AllJoyn framework
 * to get a Map where the key is a String and the value is a Variant.
 * <p>
 * The {@link #getAboutData(String)} method is called by the AllJoyn framework when
 * a <tt>AboutProxy</tt> object calls <tt>AboutProxy.getAboutData()</tt> method.
 */
public class BridgeAboutData implements AboutDataListener {

	/* the logger */
	private static final Logger LOGGER = Logger.getGlobal();

	/**
	 * Get the Dictionary that is returned when a user calls <tt>org.alljoyn.About.GetAboutData</tt>.
	 * The returned Dictionary must contain the AboutData dictionary for the language specified.
	 * If the language parameter is null or an empty string the about data for the default language will be returned.
	 * 
	 * @param language a tag that specifies the required language of the data
	 * @return a map containing the (key, value) pairs for the app properties
	 * @throws ErrorReplyBusException if the requested language is not supported or
	 * if unable to return the <tt>AboutData</tt> because one or more required field values can not be obtained
	 */
	public Map<String, Variant> getAboutData(String language) throws ErrorReplyBusException {

		LOGGER.fine("MyAboutData.getAboutData was called for `" + language + "` language.");

		if (!language.equalsIgnoreCase("en")) {
			LOGGER.fine("Language not supported. Default language (en) will be used.");
		}

		// FIXME data values
		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
		aboutData.put("DeviceId", new Variant(new String("738c9f4a-ec3e-44f3-b1dc-0e6e3cb57383")));
		aboutData.put("ModelNumber", new Variant("Bridge"));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("SupportedLanguages", new Variant(new String[] { "en" }));
		aboutData.put("DeviceName", new Variant("Bridge device"));
		aboutData.put("AppName", new Variant("CoAPBridge"));
		aboutData.put("Manufacturer", new Variant(new String("David Costa")));
		aboutData.put("Description", new Variant( "An application to interconnect AllJoyn and CoAP devices"));
		aboutData.put("SoftwareVersion", new Variant(new String("1.0")));
		aboutData.put("AJSoftwareVersion", new Variant(Version.get()));

		return aboutData;
	}

	/**
	 * Return a Dictionary containing the AboutData. The Dictionary will always be the default language
	 * and will only contain the fields that are announced.
	 * <p>
	 * The fields required to be part of the announced data are:
	 * <ul>
	 * <li>AppId</li>
	 * <li>DefaultLanguage</li>
	 * <li>DeviceId</li>
	 * <li>AppName</li>
	 * <li>Manufacturer</li>
	 * <li>ModelNumber</li>
	 * <li>SoftwareVersion</li>
	 * </ul>
	 * 
	 * @return a map containing the (key, value) pairs for the app properties
	 * @throws ErrorReplyBusException if unable to return the announced <tt>AboutData</tt>
	 *  because one or more required field values can not be obtained
	 */
	public Map<String, Variant> getAnnouncedAboutData() throws ErrorReplyBusException {

		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
		aboutData.put("DeviceId", new Variant(new String("738c9f4a-ec3e-44f3-b1dc-0e6e3cb57383")));
		aboutData.put("ModelNumber", new Variant("Bridge"));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("AppName", new Variant("CoAPBridge"));
		aboutData.put("DeviceName", new Variant("Bridge device"));
		aboutData.put("Manufacturer", new Variant(new String("David Costa")));
		aboutData.put("SoftwareVersion", new Variant(new String("1.0")));

		return aboutData;

	}

}