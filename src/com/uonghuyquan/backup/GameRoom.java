package com.uonghuyquan.backup;

import java.io.IOException;
import java.util.Random;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameRoom extends GameUserList implements Runnable {
	private static int count = 0;
	private int id;
	@SuppressWarnings("unused")
	private GameServer server;
	private GameUser host;
	private int gameId = 0;
	private int size;
	private int status;
	private boolean active = true;

	private int[] cardCodesArray = null;
	private GameUser[] usersArray = null;
	private int lastUserId = 0;
	private GameUser inTurnUser = null;
	private int inTurnLocation = -1;

	static int CFG_SCORE_PER_MATCHED = 1;
	static int CFG_CARD_CLOSE_TIME = 2500;
	static int CFG_GAME_END_INTERVAL = 2500;
	static int CFG_TURN_INTERVAL = 15000;
	static int CFG_TURN_INTERVAL_STEP = 1000;

	public GameRoom(GameServer server, GameUser host, int size) {
		id = ++GameRoom.count;
		this.server = server;
		this.host = host;
		this.size = size;

		server.addRoom(this);

		new Thread(this).start();

		GameIO.debug("GameRoom created (size = " + size + ")", 2);
	}

	public int getId() {
		return id;
	}

	public GameUser getHost() {
		return host;
	}

	public int getSize() {
		return size;
	}

	public int getStatus() {
		return status;
	}

	public void buildRoomInfoMessage(GameMessage m) {
		m.addParam("RoomID", getId());
		m.addParam("Room-Size", getSize());
		if (host != null) {
			m.addParam("Host", host.getUsername());
		}
		m.addParam("Users", getUsers());
		GameUser[] users = getUsersArray();
		for (int i = 0; i < users.length; i++) {
			GameUser user = users[i];
			if (user != null) {
				m.addParam("User" + i, user.getUsername());
				m.addParam("Ready" + i, user.isReady() ? 1 : 0);
			}
		}
		m.addParam("Status", getStatus());
	}

	public void buildScoresMessage(GameMessage m, boolean isWonMessage) {
		GameUser top = null;
		GameUser[] users = getUsersArray();
		m.addParam("Users", users.length);
		for (int i = 0; i < users.length; i++) {
			m.addParam("User" + i, users[i].getUsername());
			m.addParam("Score" + i, users[i].getScore());
			if (top == null || top.getScore() < users[i].getScore()) {
				top = users[i];
			}
		}
		if (isWonMessage) {
			m.addParam("Username", top.getUsername());
		}
	}

	private synchronized GameUser[] getUsersArray() {
		if (usersArray != null) {
			return usersArray;
		}
		return users.values().toArray(new GameUser[0]);
	}

	private void broadcast(GameMessage broadcastMessage) {
		GameUser[] users = getUsersArray();
		for (int i = 0; i < users.length; i++) {
			try {
				users[i].io.write(broadcastMessage);
			} catch (IOException e) {
				users[i].byebye();
			}
		}
	}

	public void broadcastState() {
		GameMessage state = new GameMessage(GameMessage.ROOM_STATE);
		buildRoomInfoMessage(state);
		broadcast(state);
	}

	public void broadcastTurn(String username, int i) {
		GameMessage turn = new GameMessage(GameMessage.TURN);
		turn.addParam("Turn", username);
		turn.addParam("Turn-Number", i + 1);
		broadcast(turn);
	}

	public void broadcastMoved(String username, int location, int code) {
		GameMessage goMoved = new GameMessage(GameMessage.GO_MOVED);
		goMoved.addParam("Username", username);
		goMoved.addParam("Location", location);
		goMoved.addParam("Code", code);
		broadcast(goMoved);
	}

	public void broadcastGoDone(String username) {
		GameMessage goDone = new GameMessage(GameMessage.GO_DONE);
		goDone.addParam("Username", username);
		broadcast(goDone);
	}

	public void broadcastScores() {
		GameMessage scored = new GameMessage(GameMessage.SCORED);
		buildScoresMessage(scored, false);
		broadcast(scored);
	}

	private boolean isEveryoneReady() {
		GameUser[] users = getUsersArray();
		for (int i = 0; i < users.length; i++) {
			if (!users[i].isReady()) {
				return false;
			}
		}
		return true;
	}

	private void prepareToPlay() {
		// increase gameId
		gameId++;

		// generate array of card codes
		Random rand = new Random();
		int cardTypes = Math.min(40, size / 2);
		cardCodesArray = new int[size];
		for (int i = 0; i < size; i++) {
			cardCodesArray[i] = -1;
		}
		for (int i = 0; i < cardTypes; i++) {
			for (int j = 0; j < size / cardTypes; j++) {
				int k;
				do {
					k = rand.nextInt(size);
				} while (cardCodesArray[k] != -1);
				cardCodesArray[k] = i;
			}
		}

		String superDebug = this + " (game #" + gameId + "):";
		for (int i = 0; i < size; i++) {
			superDebug += " " + cardCodesArray[i];
		}
		GameIO.debug(superDebug);

		// cache array of users
		usersArray = getUsersArray();

		for (int i = 0; i < usersArray.length; i++) {
			usersArray[i].prepareToPlay();
		}

		// clear last user played
		lastUserId = -1;

		// update status
		status = GameMessage.RS_PLAYING;
	}

	private boolean isFinished() {
		for (int i = 0; i < cardCodesArray.length; i++) {
			if (cardCodesArray[i] > -1) {
				return false;
			}
		}
		return true;
	}

	private int nextUserId() {
		int next = lastUserId;
		int count = 0;

		do {
			if (next == -1) {
				// no body yet
				next = 0;
			} else if (next == usersArray.length - 1) {
				// last user hit, come back to the first
				next = 0;
			} else {
				// next user
				next = next + 1;
			}

			if (usersArray[next].isActive()) {
				break;
			}

			count++;
		} while (count < usersArray.length);

		if (next > -1 && !usersArray[next].isActive()) {
			next = -1;
		}

		return next;
	}

	private void playUser(GameUser user) {
		inTurnUser = user;

		GameIO.debug(this + ": Starting turn for " + user.getUsername(), 2);
		int[] openedLocations = new int[2];
		int[] openedCodes = new int[2];
		for (int i = 0; i < openedCodes.length; i++) {
			inTurnLocation = -1;
			broadcastTurn(user.getUsername(), i);
			// broadcasted, we now wait...

			int timer = GameRoom.CFG_TURN_INTERVAL;
			do {
				try {
					Thread.sleep(GameRoom.CFG_TURN_INTERVAL_STEP);
				} catch (InterruptedException e) {
					// ignore
				}
				timer -= GameRoom.CFG_TURN_INTERVAL_STEP;
				GameIO.debug(this + ": Timer for " + user + " = " + timer, 5);
			} while (timer > 0 && inTurnLocation == -1 && user.isActive());

			if (inTurnLocation != -1) {
				// a GO has been received
				openedLocations[i] = inTurnLocation;
				if (inTurnLocation < cardCodesArray.length) {
					openedCodes[i] = cardCodesArray[inTurnLocation];
				} else {
					openedCodes[i] = -1;
					break;
				}
				GameIO.debug(this + ": " + user.getUsername() + " opened "
						+ openedLocations[i] + " ~> " + openedCodes[i], 2);
				// broadcast the opened card
				broadcastMoved(user.getUsername(), inTurnLocation,
						openedCodes[i]);
			} else {
				// nothing has been received
				openedCodes[i] = -1;
				break;
			}
		}

		// cool down, let people see opened cards
		if (openedCodes[0] > -1 && openedCodes[0] > -1) {
			try {
				Thread.sleep(GameRoom.CFG_CARD_CLOSE_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		// broadcast GO_DONE
		broadcastGoDone(user.getUsername());

		if (openedCodes[0] > -1 && openedCodes[0] > -1) {
			// valid codes
			if (openedLocations[0] != openedLocations[1]) {
				// valid locations
				if (openedCodes[0] == openedCodes[1]) {
					// scored!
					user.increaseScore(GameRoom.CFG_SCORE_PER_MATCHED);
					// mark done for cards
					cardCodesArray[openedLocations[0]] = -1;
					cardCodesArray[openedLocations[1]] = -1;

					GameIO.debug(user.getUsername() + " scored!", 3);

					if (!isFinished()) {
						// let the current player continue
						playUser(user);
					}
				}
			}
		}

		inTurnUser = null;
	}

	private void cleanUp() {
		cardCodesArray = null;
		usersArray = null;

		// update status
		status = GameMessage.RS_WAITING;
	}

	public void onUserChanged(GameUser user) {
		if (users.size() > 1 && isEveryoneReady()) {
			prepareToPlay();
		}
		broadcastState();
	}

	public void onUserGo(GameUser user, int location) {
		if (user == inTurnUser) {
			inTurnLocation = location;
		}
	}

	public void onUserChat(GameUser user, String message) {
		GameMessage bm = new GameMessage(GameMessage.CHATTED);
		bm.addParam("Username", user.getUsername());
		bm.addParam("Content", message);
		broadcast(bm);
	}

	public void run() {
		while (true) {
			if (!active) {
				return;
			}

			if (status == GameMessage.RS_PLAYING) {
				while (!isFinished()) {
					int userId = nextUserId();
					if (userId > -1) {
						GameUser user = usersArray[userId];
						playUser(user);
						lastUserId = userId;
					} else {
						break;
					}
				}
				if (isFinished()) {
					GameMessage won = new GameMessage(GameMessage.WON);
					buildScoresMessage(won, true);
					broadcast(won);

					GameIO.debug(this + " ended a game");
				}
				cleanUp();
				try {
					Thread.sleep(GameRoom.CFG_GAME_END_INTERVAL);
				} catch (InterruptedException e) {
					GameIO.debug(e.toString());
				}
				broadcastState();
			}
		}
	}

	@Override
	public String toString() {
		return "Room #" + id;
	}

	@Override
	public void addUser(GameUser user) throws GameException {
		super.addUser(user);

		if (users.size() == 1) {
			host = getUserByOffset(0);
		}
	}

	@Override
	public boolean removeUser(GameUser user) {
		boolean removed = super.removeUser(user);

		if (removed && host == user) {
			if (users.size() > 0) {
				host = getUserByOffset(0);
			} else {
				host = null;
			}
		}

		return removed;
	}
}
