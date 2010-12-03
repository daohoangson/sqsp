package com.daohoangson;

import java.io.IOException;
import java.net.Socket;

import com.daohoangson.event.GameEvent;
import com.daohoangson.event.GameEventSource;

/**
 * A game client
 * 
 * @author Dao Hoang Son
 * 
 */
public class GameClient extends GameEventSource implements Runnable {
	/**
	 * The IO manager for the connected socket (created in constructor)
	 */
	private GameIO io;
	/**
	 * The username associated and verified with client
	 */
	private String username = null;
	private int roomId = 0;
	private boolean host = false;
	private boolean ready = false;
	private GameParamList roomInfo = null;

	/**
	 * Constructs
	 * 
	 * @param host
	 *            the host address
	 * @param port
	 *            the host port number
	 */
	public GameClient(String host, int port) {
		super();

		try {
			Socket socket = new Socket(host, port);
			io = new GameIO(socket);

			io.write(new GameMessage(GameMessage.HELLO));
			if (io.read().is(GameMessage.OK)) {
				GameIO.debug("Connection validated", 1);
			}

			new Thread(this).start();
		} catch (Exception e) {
			GameIO.debug(e.toString());
			System.exit(1);
		}
	}

	public synchronized void run() {
		while (true) {
			try {
				GameIO.debug("Waiting in loop", 5);
				GameMessage m = io.read();

				GameIO.debug("Received a message: " + m.getCode(), 4);

				switch (m.getCode()) {
				case GameMessage.ROOM_STATE:
					if (m.getParamAsInt("RoomID") == roomId) {
						GameMessage myState = new GameMessage(GameMessage.OK);
						myState.addParam("Username", username);
						myState.addParam("Ready", ready ? 1 : 0);
						io.write(myState);
						readRoomInfoMessage(m);
					} else {
						GameIO.debug("ROOM_STATE INVALID: "
								+ m.getParamAsInt("RoomID"), 4);
						io.writeError(GameMessage.E_INVALID);
					}
					continue;
				}
			} catch (IOException e) {
				fireGameEvent(GameEvent.IOException);
				GameIO.debug(e.toString());
				return;
			}
		}
	}

	private void write(GameMessage m) {
		try {
			io.write(m);
		} catch (IOException e) {
			GameIO.debug(e.toString());
		}
	}

	private GameMessage writeRead(GameMessage m) {
		synchronized (io) {
			GameMessage previousReceived = io.lastReceivedMessage;
			write(m);
			while (io.lastReceivedMessage == previousReceived) {
				// wait
			}
			return io.lastReceivedMessage;
		}
	}

	/**
	 * Sends and processes login request
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return true if server accepts login request, false otherwise
	 */
	public boolean login(String username, String password) {
		GameMessage login = new GameMessage(GameMessage.LOGIN);
		login.addParam("Username", username);
		login.addParam("Password", password);
		GameMessage response = writeRead(login);

		if (isOK(response)) {
			this.username = username;
			fireGameEvent(GameEvent.LOGGED_IN);
			return true;
		}

		return false;
	}

	/**
	 * Gets number of rooms from server using {@link GameMessage#ROOMS} message
	 * type
	 * 
	 * @return number of rooms or 0 if failed
	 */
	public int rooms() {
		GameMessage rooms = new GameMessage(GameMessage.ROOMS);
		GameMessage response = writeRead(rooms);

		if (isOK(response)) {
			return response.getParamAsInt("Rooms");
		}

		return 0;
	}

	/**
	 * Tries to make a room in server using {@link GameMessage#ROOM_MAKE}
	 * message type
	 * 
	 * @param size
	 *            the size of the room
	 * @return the room ID
	 */
	public int roomMake(int size) {
		GameMessage roomMake = new GameMessage(GameMessage.ROOM_MAKE);
		roomMake.addParam("Room-Size", size);
		ready = false;
		GameMessage response = writeRead(roomMake);

		if (isOK(response)) {
			readRoomInfoMessage(response);
			fireGameEvent(GameEvent.JOINED_ROOM);
			return roomId;
		}

		return 0;
	}

	/**
	 * Gets information of a room from server using
	 * {@link GameMessage#ROOM_INFO} message type
	 * 
	 * @param roomOffset
	 *            the room offset (starting from 0)
	 * @return a {@linkplain GameParamList parameter list} or null if error
	 *         occurs
	 */
	public GameParamList roomInfo(int roomOffset) {
		GameMessage roomInfo = new GameMessage(GameMessage.ROOM_INFO);
		roomInfo.addParam("Room-Offset", roomOffset);
		GameMessage response = writeRead(roomInfo);

		if (isOK(response)) {
			return response;
		}

		return null;
	}

	/**
	 * Tries to join a room in server using {@link GameMessage#ROOM_JOIN}
	 * message type
	 * 
	 * @param roomId
	 *            the room id
	 * @return a {@linkplain GameParamList parameter list} or null if error
	 *         occurs
	 */
	public GameParamList roomJoin(int roomId) {
		GameMessage roomJoin = new GameMessage(GameMessage.ROOM_JOIN);
		roomJoin.addParam("RoomID", roomId);
		ready = false;
		GameMessage response = writeRead(roomJoin);

		if (isOK(response)) {
			readRoomInfoMessage(response);
			fireGameEvent(GameEvent.JOINED_ROOM);
			return response;
		}

		return null;
	}

	private void readRoomInfoMessage(GameMessage m) {
		int tmp = m.getParamAsInt("RoomID");
		if (tmp > 0) {
			roomId = tmp;
			host = m.getParam("Host").equals(username);
			GameParamList oldRoomInfo = roomInfo;
			roomInfo = new GameParamList(m);

			if (oldRoomInfo != null) {
				int statusOld = oldRoomInfo.getParamAsInt("Status");
				int statusNew = roomInfo.getParamAsInt("Status");

				if (statusOld != statusNew) {
					if (statusNew == GameMessage.RS_PLAYING) {
						fireGameEvent(GameEvent.STARTED);
					}
				}
			}

			GameIO.debug("Updated room info", 4);
		} else {
			GameIO.debug("readRoomInfoMessage invalid", 4);
		}
	}

	/**
	 * Checks if a message is an OK message
	 * 
	 * @param m
	 *            the message needs checking
	 * @return true if it's OK, false otherwise
	 */
	private boolean isOK(GameMessage m) {
		return m != null && m.is(GameMessage.OK);
	}

	/**
	 * Gets the username associated with the client
	 * 
	 * @return the username or null if not associated yet
	 */
	public String getUsername() {
		return username;
	}

	public int getRoomId() {
		return roomId;
	}

	public boolean isHost() {
		return host;
	}

	public void setReady(boolean ready) {
		if (host) {
			this.ready = true;
		} else {
			this.ready = ready;
		}
	}

	public boolean isReady() {
		return ready;
	}

	private String getRoomInfo(String paramKey) {
		if (roomInfo != null) {
			return roomInfo.getParam(paramKey);
		} else {
			return "";
		}
	}

	public int getRoomUsers() {
		return Integer.valueOf(getRoomInfo("Users"));
	}

	public String getRoomUsername(int offset) {
		return getRoomInfo("User" + offset);
	}

	public boolean getRoomReady(int offset) {
		return Integer.valueOf(getRoomInfo("Ready" + offset)) == 1;
	}
}
