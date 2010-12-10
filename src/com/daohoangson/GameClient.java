package com.daohoangson;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import com.daohoangson.event.GameEvent;
import com.daohoangson.event.GameEventSource;

/**
 * A game client (core)
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
	private boolean turn = false;
	private int[] openedLocations = null;
	private GameParamList roomInfo = null;
	private GameParamList scores = null;

	private int[] aiCodes = null;
	private final int[] aiLastTwo = new int[2];
	public final static int AI_UNKNOWN = -2;
	public final static int AI_DONE = -1;

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

	/**
	 * Runs indepedently and read messages from the socket. This will be ran as
	 * a new thread via constructor
	 */
	public synchronized void run() {
		while (true) {
			try {
				GameIO.debug("Waiting in loop", 5);
				GameMessage m = io.read();

				// just to make sure turn is off. Dirty trick
				turn = false;

				switch (m.getCode()) {
				case GameMessage.ROOM_STATE:
					// got a ROOM_STATE, auto announce client's state
					if (roomId == 0 || m.getParamAsInt("RoomID") == roomId) {
						GameMessage myState = new GameMessage(GameMessage.OK);
						myState.addParam("Username", username);
						myState.addParam("Ready", ready ? 1 : 0);
						io.write(myState);
						
						if (roomId > 0) {
							readRoomInfoMessage(m);
						}
					} else {
						GameIO.debug("ROOM_STATE INVALID: "
								+ m.getParamAsInt("RoomID"), 4);
						io.writeError(GameMessage.E_INVALID);
					}
					continue;
				case GameMessage.TURN:
					// got a TURN, prepare to GO
					String turnUsername = m.getParam("Turn");
					if (turnUsername.equals(username)) {
						turn();
						fireGameEvent(GameEvent.TURN);
					}
					continue;
				case GameMessage.GO_MOVED:
					aiUpdate(m);
					fireGameEvent(GameEvent.GO_MOVED, m);
					continue;
				case GameMessage.GO_DONE:
					fireGameEvent(GameEvent.GO_DONE, m);
					continue;
				case GameMessage.SCORED:
					aiUpdate(m);
					readScoreMessage(m);
					fireGameEvent(GameEvent.SCORED, m);
					continue;
				case GameMessage.WON:
					readScoreMessage(m);
					fireGameEvent(GameEvent.WON, m);
					continue;
				}
			} catch (IOException e) {
				fireGameEvent(GameEvent.IOException);
				GameIO.debug(e.toString());
				return;
			}
		}
	}

	/**
	 * Writes a message safely (no exception)
	 * 
	 * @param m
	 *            the message to be sent
	 */
	private void write(GameMessage m) {
		try {
			io.write(m);
		} catch (IOException e) {
			GameIO.debug(e.toString());
		}
	}

	/**
	 * Writes a message and reads back one safely (no exception)
	 * 
	 * @param m
	 *            the message to be sent
	 * @return the response message
	 */
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

	/**
	 * Prepares for client's turn
	 */
	private void turn() {
		turn = true;

		if (openedLocations == null || openedLocations[1] > -1) {
			openedLocations = new int[2];
			for (int i = 0; i < openedLocations.length; i++) {
				openedLocations[i] = -1;
			}
		}
	}

	/**
	 * Sends the GO message
	 * 
	 * @param location
	 *            the selected card's location (zero based)
	 */
	public void go(int location) {
		if (!turn) {
			GameIO.debug("Skipped sending a GO (" + location + ")", 5);
			return;
		}
		GameMessage go = new GameMessage(GameMessage.GO);
		go.addParam("Username", username);
		go.addParam("Location", location);
		write(go);

		for (int i = 0; i < openedLocations.length; i++) {
			if (openedLocations[i] == -1) {
				openedLocations[i] = location;
				break;
			}
		}
	}

	/**
	 * Gets the last 2 selected (and opened) locations
	 * 
	 * @return array of 2 integers for locations
	 */
	public int[] getOpenedLocations() {
		return openedLocations;
	}

	private void prepareToPlay() {
		ready = false;
		turn = false;
		openedLocations = null;
		scores = null;
	}

	/**
	 * Parse room-info type messages
	 * 
	 * @param m
	 *            the received message
	 */
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
						aiStart();
					} else if (statusNew == GameMessage.RS_WAITING) {
						fireGameEvent(GameEvent.WAITING);
						aiStop();
						prepareToPlay();
					}
				}
			} else {
				prepareToPlay();
			}

			fireGameEvent(GameEvent.ROOM_STATE);

			GameIO.debug("Updated room info", 4);
		} else {
			GameIO.debug("readRoomInfoMessage invalid", 4);
		}
	}

	/**
	 * Parses SCORED message
	 * 
	 * @param m
	 *            the SCORED message
	 */
	private void readScoreMessage(GameMessage m) {
		if (m.is(GameMessage.SCORED) || m.is(GameMessage.WON)) {
			GameParamList oldScores = scores;
			scores = new GameParamList(m);

			if (oldScores != null) {
				int oldUserId = findUserId(oldScores, username);
				int oldScore = -1;
				if (oldUserId > -1) {
					oldScore = oldScores.getParamAsInt("Score" + oldUserId);
				}

				int newUserId = findUserId(scores, username);
				int newScore = -1;
				if (newUserId > -1) {
					newScore = scores.getParamAsInt("Score" + newUserId);
				}

				if (newScore != oldScore) {
					fireGameEvent(GameEvent.OWN_SCORED);
				}
			}

			GameIO.debug("Updated scores", 3);
		} else {
			GameIO.debug("readScoreMessage invalid", 4);
		}
	}

	/**
	 * Gets the offset number of a username from a room-info message or SCORED
	 * message
	 * 
	 * @param params
	 *            the stored information
	 * @param username
	 *            the needed username
	 * @return offset or -1 if not found
	 */
	private int findUserId(GameParamList params, String username) {
		int users = params.getParamAsInt("Users");
		int userId = -1;
		if (params != null) {
			for (int i = 0; i < users; i++) {
				if (params.getParam("User" + i).equals(username)) {
					userId = i;
					break;
				}
			}
		}
		return userId;
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
		this.ready = ready;
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

	public boolean getRoomReady(String username) {
		int userId = findUserId(roomInfo, username);
		if (userId > -1) {
			return Integer.valueOf(getRoomInfo("Ready" + userId)) == 1;
		} else {
			return false;
		}
	}

	public int getRoomScore(String username) {
		int userId = findUserId(scores, username);
		if (userId > -1) {
			return scores.getParamAsInt("Score" + userId);
		} else {
			return 0;
		}
	}

	public int getRoomSize() {
		return Integer.valueOf(getRoomInfo("Room-Size"));
	}

	public int getRoomStatus() {
		return Integer.valueOf(getRoomInfo("Status"));
	}

	private void aiStart() {
		if (roomInfo != null) {
			int size = roomInfo.getParamAsInt("Room-Size");
			aiCodes = new int[size];
			for (int i = 0; i < size; i++) {
				aiCodes[i] = GameClient.AI_UNKNOWN;
			}
			for (int i = 0; i < aiLastTwo.length; i++) {
				aiLastTwo[i] = -1;
			}
		}
	}

	private void aiStop() {
		aiCodes = null;
	}

	private void aiUpdate(GameMessage m) {
		if (m == null || aiCodes == null) {
			return;
		}

		if (m.is(GameMessage.GO_MOVED)) {
			int location = m.getParamAsInt("Location");
			int code = m.getParamAsInt("Code");
			if (location >= 0 && location < aiCodes.length && code >= 0) {
				aiCodes[location] = code;
			}
			aiLastTwo[0] = aiLastTwo[1];
			aiLastTwo[1] = location;
		} else if (m.is(GameMessage.SCORED)) {
			for (int i = 0; i < aiLastTwo.length; i++) {
				int location = aiLastTwo[i];
				if (location >= 0 && location < aiCodes.length) {
					aiCodes[location] = GameClient.AI_DONE;
				}
			}
		}
	}

	private int aiNext(int location) {
		if (aiCodes != null) {
			/*
			 * int count = 0; int i; Random rand = new Random(); do { i =
			 * rand.nextInt(aiCodes.length); count++; } while (i != location &&
			 * aiCodes[i] >= 0 && count < aiCodes.length);
			 * 
			 * GameIO.debug("AI suggests " + i); return i;
			 */

			int notDone = 0;
			for (int i = 0; i < aiCodes.length; i++) {
				if (i == location) {
					continue;
				}
				if (aiCodes[i] == GameClient.AI_UNKNOWN) {
					GameIO.debug("AI suggests " + i);
					return i;
				}
				if (aiCodes[i] != GameClient.AI_DONE) {
					notDone = i;
				}
			}

			GameIO.debug("AI stuck");
			return notDone;
		} else {
			Random rand = new Random();
			return rand.nextInt();
		}
	}

	private int aiNext(int location, int code) {
		if (aiCodes != null) {
			for (int i = 0; i < aiCodes.length; i++) {
				if (i != location && aiCodes[i] == code) {
					GameIO.debug("AI+ suggests " + i);
					return i;
				}
			}
		}

		return aiNext(location);
	}

	public int aiThink() {
		int[] locations = getOpenedLocations();
		if (locations != null) {
			GameIO.debug("AI locations: " + locations[0] + "," + locations[1]);
			if (locations[1] > -1) {
				// turn #2 is done
				return aiNext(-1);
			} else if (locations[0] > -1) {
				// only turn #1 is done
				return aiNext(locations[0], aiCodes[locations[0]]);
			} else {
				// no turn is done
				return aiNext(-1);
			}
		} else {
			// no clue?
			return aiNext(-1);
		}
	}
}
