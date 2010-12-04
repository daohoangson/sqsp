package com.uonghuyquan.backup;

import java.io.IOException;
import java.util.Random;

import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameRoom extends GameUserList implements Runnable {
	private static int count = 0;
	private int id;
	@SuppressWarnings("unused")
	private GameServer server;
	private GameUser host;
	private int size;
	private int status;
	private boolean active = true;

	private int[] cardCodesArray = null;
	private GameUser[] usersArray = null;
	private int lastUserId = 0;
	private int scorePerMatchedPair = 1;

	public GameRoom(GameServer server, GameUser host, int size) {
		id = ++GameRoom.count;
		this.server = server;
		this.host = host;
		this.size = size;

		server.addRoom(this);

		new Thread(this).start();

		GameIO.debug("GameRoom created", 2);
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
		m.addParam("Host", getHost().getUsername());
		m.addParam("Users", getUsers());
		GameUser[] users = this.users.values().toArray(new GameUser[0]);
		for (int i = 0; i < users.length; i++) {
			GameUser user = users[i];
			if (user != null) {
				m.addParam("User" + i, user.getUsername());
				m.addParam("Ready" + i, user.isReady() ? 1 : 0);
			}
		}
		m.addParam("Status", getStatus());
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

				if (broadcastMessage.is(GameMessage.ROOM_STATE)) {
					GameMessage m = users[i].io.read();
					if (m.is(GameMessage.OK)) {
						if (m.getParam("Username").equals(
								users[i].getUsername())) {
							users[i].setReady(m.getParamAsInt("Ready") == 1);
						}
					} else {
						removeUser(users[i]);
					}
				}
			} catch (IOException e) {
				users[i].byebye();
			}
		}
	}

	public void broadcastScores() {
		GameUser[] users = getUsersArray();
		GameMessage scored = new GameMessage(GameMessage.SCORED);
		scored.addParam("Users", users.length);
		for (int i = 0; i < users.length; i++) {
			scored.addParam("User" + i, users[i].getUsername());
			scored.addParam("Score" + i, users[i].getScore());
		}
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
		// generate array of card codes
		cardCodesArray = new int[size];
		int cardTypes = Math.min(40, Math.max(5, size / 2));
		cardTypes = 1;
		// TODO: DEBUG
		for (int i = 0; i < size; i++) {
			cardCodesArray[i] = -1;
		}
		Random rand = new Random();
		for (int i = 0; i < cardTypes; i++) {
			for (int j = 0; j < size / cardTypes; j++) {
				int k;
				do {
					k = rand.nextInt(size);
				} while (cardCodesArray[k] != -1);
				cardCodesArray[k] = i;
			}
		}

		// cache array of users
		usersArray = getUsersArray();

		for (int i = 0; i < usersArray.length; i++) {
			usersArray[i].resetScore();
		}

		// clear last user played
		lastUserId = -1;
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
		try {
			GameIO.debug(this + ": Starting turn for " + user.getUsername(), 3);
			int[] openedLocations = new int[2];
			int[] openedCodes = new int[2];
			for (int i = 0; i < openedCodes.length; i++) {
				GameMessage turn = new GameMessage(GameMessage.TURN);
				turn.addParam("Turn", user.getUsername());
				user.io.write(turn);

				GameMessage go = user.io.readWithTimeout(15000);

				if (go != null) {
					if (go.is(GameMessage.GO)) {
						int location = go.getParamAsInt("Location");
						openedLocations[i] = location;
						if (location < cardCodesArray.length) {
							openedCodes[i] = cardCodesArray[location];
						} else {
							openedCodes[i] = -1;
						}
						GameIO.debug(this + ": " + user.getUsername()
								+ " opened " + openedLocations[i] + " ~> "
								+ openedCodes[i], 2);
						if (openedCodes[i] != -1) {
							// only broadcast if user opened a valid card
							GameMessage goMoved = new GameMessage(
									GameMessage.GO_MOVED);
							buildRoomInfoMessage(goMoved);
							goMoved.addParam("Username", user.getUsername());
							goMoved.addParam("Location", location);
							goMoved.addParam("Code", openedCodes[i]);
							broadcast(goMoved);
						}
					}
				} else {
					openedCodes[i] = -1;
					break;
				}
			}

			GameMessage goDone = new GameMessage(GameMessage.GO_DONE);
			goDone.addParam("Username", user.getUsername());
			broadcast(goDone);

			if (openedCodes[0] > -1 && openedCodes[0] > -1) {
				// valid codes
				if (openedLocations[0] != openedLocations[1]) {
					// valid locations
					if (openedCodes[0] == openedCodes[1]) {
						// scored!
						user.setScore(user.getScore() + scorePerMatchedPair);
						cardCodesArray[openedLocations[0]] = -1;
						cardCodesArray[openedLocations[1]] = -1;

						GameIO.debug(user.getUsername() + " scored!", 3);

						playUser(user); // continue playing
					} else {
						// hmm
					}
				}
			}
		} catch (IOException e) {
			user.byebye();
		}
	}

	public void run() {
		while (true) {
			if (!active) {
				return;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				GameIO.debug(e.toString());
			}
			if (users.size() == 0) {
				continue;
			}

			if (status == GameMessage.RS_WAITING) {
				if (getUsers() > 1 && isEveryoneReady()) {
					status = GameMessage.RS_PLAYING;
					prepareToPlay();
				}

				GameMessage m = new GameMessage(GameMessage.ROOM_STATE);
				buildRoomInfoMessage(m);
				broadcast(m);
			}
			if (status == GameMessage.RS_PLAYING) {
				while (!isFinished()) {
					int userId = nextUserId();
					if (userId > -1) {
						GameUser user = usersArray[userId];
						playUser(user);
						lastUserId = userId;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Room #" + id;
	}
}
