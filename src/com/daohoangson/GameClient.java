package com.daohoangson;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

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
	private boolean ready = false;
	private HashMap<String, Integer> players = new HashMap<String, Integer>();

	/**
	 * Constructs
	 * 
	 * @param host
	 *            the host address
	 * @param port
	 *            the host port number
	 */
	public GameClient(String host, int port) {
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
				GameIO.debug("Waiting in loop", 4);
				GameMessage m = io.read();
				GameMessage sent = io.lastSentMessage;
				GameMessage response = null;

				if (sent != null) {
					switch (sent.getCode()) {
					case GameMessage.LOGIN:
						if (isOK(response)) {
							fireGameEvent(GameEvent.LOGGED_IN);
							GameIO.debug("Logged In");
							continue;
						}
					}
				}
				switch (m.getCode()) {
				case GameMessage.ROOM_STATE:

					break;
				}
			} catch (IOException e) {
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
		GameMessage previousReceived = io.lastReceivedMessage;
		write(m);
		while (io.lastReceivedMessage == previousReceived) {
			break;
		}
		return io.lastReceivedMessage;
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
	public void login(String username, String password) {
		GameMessage login = new GameMessage(GameMessage.LOGIN);
		login.addParam("Username", username);
		login.addParam("Password", password);
		write(login);
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

	private void readRoomInfoMessage(GameMessage m) {
		roomId = m.getParamAsInt("RoomID");
		players.clear();
		int users = m.getParamAsInt("Users");
		for (int i = 0; i < users; i++) {
			String username = m.getParam("User" + i);
			int ready = m.getParamAsInt("Ready" + i);
			players.put(username, ready);
		}
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
		GameMessage response = writeRead(roomMake);

		if (isOK(response)) {
			readRoomInfoMessage(response);
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
		GameMessage response = writeRead(roomJoin);

		if (isOK(response)) {
			readRoomInfoMessage(response);
			return response;
		}

		return null;
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
}
