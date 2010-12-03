package com.daohoangson;

import java.util.HashMap;

/**
 * Very basic list providing standard methods to manage parameters (key and
 * value pair)
 * 
 * @author Dao Hoang Son
 * 
 */
public class GameParamList {
	/**
	 * Parameter hashmap
	 */
	protected HashMap<String, String> params = new HashMap<String, String>();

	/**
	 * Adds/replaces a parameter by key
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addParam(String key, String value) {
		params.put(key, value);
		GameIO.debug("GameMessage addParam " + key + " " + value, 4);
	}

	/**
	 * Adds/replaces a integer parameter by key. This is a wrapper method
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the integer value
	 */
	public void addParam(String key, int value) {
		params.put(key, "" + value);
	}

	/**
	 * Gets a parameter by key. Will return null if the requested key is not
	 * existed
	 * 
	 * @param key
	 *            the requested key
	 * @return parameter value or null
	 */
	public String getParam(String key) {
		return params.get(key);
	}

	/**
	 * Gets an integer parameter by key. Will return defaultValue if the
	 * requested key is not existed. This is a wrapper method
	 * 
	 * @param key
	 *            the requested key
	 * @param defaultValue
	 *            the default value
	 * @return parameter value or default value
	 */
	public int getParamAsInt(String key, int defaultValue) {
		String value = params.get(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.valueOf(value);
		}
	}

	/**
	 * Gets an integer parameter by key. Will return 0 if the requested key is
	 * not existed. This is a wrapper method
	 * 
	 * @param key
	 *            the requested key
	 * @return parameter value or 0
	 */
	public int getParamAsInt(String key) {
		return getParamAsInt(key, 0);
	}

	/**
	 * Gets the number of parameters stored in the list
	 * 
	 * @return number of parameters
	 */
	public int getParamsCount() {
		return params.size();
	}
}
