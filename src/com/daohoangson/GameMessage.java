package com.daohoangson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A message to transfer or receive via socket connections. It makes use of
 * {@link GameParamList} to manage parameters in a message
 * 
 * @author Dao Hoang Son
 * 
 */
public class GameMessage extends GameParamList {
	/**
	 * Message code. There are quite a few constants of this class are supposed
	 * for code
	 */
	private int code = 0;

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
	public final static int ROOMS = 8;
	public final static int ROOM_STATE = 9;
	public final static int READY = 10;
	public final static int NOT_READY = 11;

	public final static int TURN = 20;
	public final static int GO = 21;
	public final static int GO_MOVED = 921;
	public final static int GO_DONE = 922;
	public final static int SCORED = 923;
	public final static int SCORES = 924;
	public final static int WON = 930;

	public final static int CHAT = 100;
	public final static int CHATTED = 9100;
	/* Message Code - END */

	/* Error Code */
	public final static int E_INVALID = 9999;
	public final static int E_INVALID_USERNAME = 101;
	public final static int E_INVALID_PASSWORD = 102;
	public final static int E_LOGGEDIN_USERNAME = 103;
	public final static int E_ROOM_NOT_FOUND = 104;
	public final static int E_INVALID_ROOM_SIZE = 201;

	/* Error Code - END */

	public final static int RS_WAITING = 0;
	public final static int RS_PLAYING = 1;

	/**
	 * Constructs a blank message
	 */
	public GameMessage() {
		// do something here?
	}

	/**
	 * Constructs a message with the specified code
	 * 
	 * @param code
	 *            the message code
	 */
	public GameMessage(int code) {
		this.code = code;
	}

	/**
	 * Sets message code
	 * 
	 * @param code
	 *            the message code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Sets message code by code's text version. This method will use
	 * {@link GameMessage#lookupCodeFromString(String)} to find the
	 * correspondent message code
	 * 
	 * @param code
	 *            the code (text version)
	 */
	public void setCode(String code) {
		setCode(GameMessage.lookupCodeFromString(code));
	}

	/**
	 * Gets message code
	 * 
	 * @return the message code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Checks if this message has the requested message code
	 * 
	 * @param code
	 *            the requested message code
	 * @return true if it is, false otherwise
	 */
	public boolean is(int code) {
		return this.code == code;
	}

	/**
	 * Lookups code (integer) from code (text)
	 * 
	 * @param code
	 *            the message code (in text)
	 * @return the message code (integer value)
	 * @see #lookupCodeToString(int)
	 */
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
		if (code.equals("ROOMS")) {
			return GameMessage.ROOMS;
		}
		if (code.equals("ROOM_STATE")) {
			return GameMessage.ROOM_STATE;
		}
		if (code.equals("READY")) {
			return GameMessage.READY;
		}
		if (code.equals("NOT_READY")) {
			return GameMessage.NOT_READY;
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
		if (code.equals("GO_DONE")) {
			return GameMessage.GO_DONE;
		}
		if (code.equals("SCORED")) {
			return GameMessage.SCORED;
		}
		if (code.equals("SCORES")) {
			return GameMessage.SCORES;
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

	/**
	 * Translates message code to text
	 * 
	 * @param code
	 *            the message code
	 * @return the text represents the code or UNKNOWN if code not found
	 * @see #lookupCodeFromString(String)
	 */
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
		case ROOMS:
			return "ROOMS";
		case ROOM_STATE:
			return "ROOM_STATE";
		case READY:
			return "READY";
		case NOT_READY:
			return "NOT_READY";

		case TURN:
			return "TURN";
		case GO:
			return "GO";
		case GO_MOVED:
			return "GO_MOVED";
		case GO_DONE:
			return "GO_DONE";
		case SCORED:
			return "SCORED";
		case SCORES:
			return "SCORES";
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

	/**
	 * Adds parameter from header (via socket). This should be in the format of
	 * <code>Key: Value</code>. This is a wrapper method, it makes use of
	 * {@link GameParamList#addParam(String, String)}
	 * 
	 * @param line
	 *            the line
	 */
	public void addParam(String line) {
		String[] parts = line.split(":");
		if (parts.length == 2) {
			addParam(parts[0].trim(), GameIO.fromUtf8(parts[1].trim()));
		}
	}

	/**
	 * Prepares the message to be sent via socket
	 * 
	 * @param forceCount
	 *            the flag to always include Params-Count (not recommend)
	 * @return the {@linkplain String} ready to be sent
	 */
	public String prepare(boolean forceCount, boolean timestamp) {
		String result = "";

		result += GameMessage.lookupCodeToString(code) + " "
				+ GameMessage.VERSION_STRING + "\n";

		int count = params.size();

		if (timestamp) {
			result += "Timestamp: "
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(new Date()) + "\n";
			count++;
		}

		if (!params.containsKey("Params-Count")
				&& (params.size() > 0 || forceCount)) {
			count++;
			result += "Params-Count: " + count + "\n";
		}

		Iterator<Entry<String, String>> i = params.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, String> e = i.next();
			result += e.getKey() + ": "
					+ GameIO.toUtf8(e.getValue().replace(':', ';')) + "\n";
		}
		result += "\n";

		return result;
	}

	/**
	 * Prepares the message to be sent via socket. Automatically ignore
	 * Params-Count if not neccessary
	 * 
	 * @param flagTimestamp
	 * @param b
	 * 
	 * @return the {{@linkplain String} ready to be sent
	 */
	public String prepare() {
		return prepare(false, false);
	}

	@Override
	public String toString() {
		return "<MESSAGE START>\n" + prepare().replaceAll("\n", "¶\n")
				+ "<MESSAGE END>";
	}
}
