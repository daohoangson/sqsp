package com.daohoangson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class GameMessage {
	private int code = 0;
	private HashMap<String, String> params = new HashMap<String, String>();

	public final static String VERSION_STRING = "SQSP/1.0";

	/* Message Code */
	public final static int OK = 1000;
	public final static int ERROR = 1001;
	public final static int HELLO = 1;
	public final static int LOGIN = 2;
	public final static int LOGOUT = 3;
	public final static int LOST = 903;

	public final static int ROOM_INFO = 4;
	public final static int ROOM_MAKE = 5;
	public final static int ROOM_JOIN = 6;
	public final static int ROOM_JOINED = 906;
	public final static int ROOM_CLOSED = 907;
	public final static int ROOMS = 8;

	public final static int GAME_START = 10;
	public final static int GAME_STARTED = 910;
	public final static int GAME_ENDED = 911;

	public final static int READY = 12;
	public final static int READY_CHECKED = 912;
	public final static int READY_NOT = 13;
	public final static int READY_UNCHECKED = 913;

	public final static int TURN = 920;
	public final static int GO = 21;
	public final static int GO_MOVED = 921;
	public final static int SCORED = 922;
	public final static int WON = 923;

	public final static int CHAT = 30;
	public final static int CHATTED = 930;
	/* Message Code - END */

	/* Error Code */
	public final static int E_INVALID = 9999;
	public final static int E_INVALID_USERNAME = 101;
	public final static int E_INVALID_PASSWORD = 102;
	public final static int E_LOGGEDIN_USERNAME = 103;

	/* Error Code - END */

	public GameMessage() {
		// do something here?
	}

	public GameMessage(int code) {
		this.code = code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCode(String code) {
		setCode(GameMessage.lookupCodeFromString(code));
	}

	public int getCode() {
		return code;
	}

	public boolean is(int code) {
		return this.code == code;
	}

	public static int lookupCodeFromString(String code) {
		if (code.equals("OK")) {
			return GameMessage.OK;
		}
		if (code.equals("ERROR")) {
			return GameMessage.ERROR;
		}
		if (code.equals("HELLO")) {
			return GameMessage.HELLO;
		}
		if (code.equals("LOGIN")) {
			return GameMessage.LOGIN;
		}
		if (code.equals("LOGOUT")) {
			return GameMessage.LOGOUT;
		}
		if (code.equals("LOST")) {
			return GameMessage.LOST;
		}

		if (code.equals("ROOM_INFO")) {
			return GameMessage.ROOM_INFO;
		}
		if (code.equals("ROOM_MAKE")) {
			return GameMessage.ROOM_MAKE;
		}
		if (code.equals("ROOM_JOIN")) {
			return GameMessage.ROOM_JOIN;
		}
		if (code.equals("ROOM_JOINED")) {
			return GameMessage.ROOM_JOINED;
		}
		if (code.equals("ROOM_CLOSED")) {
			return GameMessage.ROOM_CLOSED;
		}
		if (code.equals("ROOMS")) {
			return GameMessage.ROOMS;
		}

		if (code.equals("GAME_START")) {
			return GameMessage.GAME_START;
		}
		if (code.equals("GAME_STARTED")) {
			return GameMessage.GAME_STARTED;
		}
		if (code.equals("GAME_ENDED")) {
			return GameMessage.GAME_ENDED;
		}

		if (code.equals("READY")) {
			return GameMessage.READY;
		}
		if (code.equals("READY_CHECKED")) {
			return GameMessage.READY_CHECKED;
		}
		if (code.equals("READY_NOT")) {
			return GameMessage.READY_NOT;
		}
		if (code.equals("READY_UNCHECKED")) {
			return GameMessage.READY_UNCHECKED;
		}

		if (code.equals("TURN")) {
			return GameMessage.TURN;
		}
		if (code.equals("GO")) {
			return GameMessage.GO;
		}
		if (code.equals("GO_MOVED")) {
			return GameMessage.GO_MOVED;
		}
		if (code.equals("SCORED")) {
			return GameMessage.SCORED;
		}
		if (code.equals("WON")) {
			return GameMessage.WON;
		}

		if (code.equals("CHAT")) {
			return GameMessage.CHAT;
		}
		if (code.equals("CHATTED")) {
			return GameMessage.CHATTED;
		}

		return 0;
	}

	public static String lookupCodeToString(int code) {
		switch (code) {
		case OK:
			return "OK";
		case ERROR:
			return "ERROR";
		case HELLO:
			return "HELLO";
		case LOGIN:
			return "LOGIN";
		case LOGOUT:
			return "LOGOUT";
		case LOST:
			return "LOST";

		case ROOM_INFO:
			return "ROOM_INFO";
		case ROOM_MAKE:
			return "ROOM_MAKE";
		case ROOM_JOIN:
			return "ROOM_JOIN";
		case ROOM_JOINED:
			return "ROOM_JOINED";
		case ROOM_CLOSED:
			return "ROOM_CLOSED";
		case ROOMS:
			return "ROOMS";

		case GAME_START:
			return "GAME_START";
		case GAME_STARTED:
			return "GAME_STARTED";
		case GAME_ENDED:
			return "GAME_ENDED";

		case READY:
			return "READY";
		case READY_CHECKED:
			return "READY_CHECKED";
		case READY_NOT:
			return "READY_NOT";
		case READY_UNCHECKED:
			return "READY_UNCHECKED";

		case TURN:
			return "TURN";
		case GO:
			return "GO";
		case GO_MOVED:
			return "GO_MOVED";
		case SCORED:
			return "SCORED";
		case WON:
			return "WON";

		case CHAT:
			return "CHAT";
		case CHATTED:
			return "CHATTED";
		default:
			return "UNKNOWN";
		}
	}

	public void addParam(String key, String value) {
		params.put(key, value);
		GameIO.debug("GameMessage addParam " + key + " " + value, 1);
	}

	public void addParam(String key, int value) {
		params.put(key, "" + value);
	}

	public void addParam(String line) {
		String[] parts = line.split(":");
		if (parts.length == 2) {
			addParam(parts[0].trim(), parts[1].trim());
		}
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

	public String prepare(boolean forceCount) {
		String result = "";

		result += GameMessage.lookupCodeToString(code) + " "
				+ GameMessage.VERSION_STRING + "\n";

		if (!params.containsKey("Params-Count")
				&& (params.size() > 0 || forceCount)) {
			result += "Params-Count: " + (1 + params.size()) + "\n";
		}

		Iterator<Entry<String, String>> i = params.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, String> e = i.next();
			result += e.getKey() + ": " + e.getValue() + "\n";
		}
		result += "\n";

		return result;
	}

	public String prepare() {
		return prepare(false);
	}

	@Override
	public String toString() {
		return "<MESSAGE START>\n" + prepare().replaceAll("\n", "¶\n")
				+ "<MESSAGE END>";
	}
}
