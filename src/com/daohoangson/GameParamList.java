package com.daohoangson;

import java.util.HashMap;

public class GameParamList {
	protected HashMap<String, String> params = new HashMap<String, String>();

	public void addParam(String key, String value) {
		params.put(key, value);
		GameIO.debug("GameMessage addParam " + key + " " + value, 4);
	}

	public void addParam(String key, int value) {
		params.put(key, "" + value);
	}

	public String getParam(String key) {
		return params.get(key);
	}

	public int getParamAsInt(String key, int defaultValue) {
		String value = params.get(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.valueOf(value);
		}
	}

	public int getParamAsInt(String key) {
		return getParamAsInt(key, 0);
	}

	public int getParamsCount() {
		return params.size();
	}
}
