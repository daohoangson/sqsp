package com.uonghuyquan.backup;

import java.io.IOException;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameUser implements Runnable {
	public GameServer server;
	public GameIO io;
	public GameRoom room = null;
	private String username = null;
	private boolean ready = false;
	private int score = 0;
	private boolean active = true;

	public GameUser(GameServer server, GameIO io) {
		this.server = server;
		this.io = io;

		new Thread(this).start();

		GameIO.debug("GameUser created", 3);
	}

	public void byebye() {
		if (!active) {
			return;
		}

		GameIO.debug("Connection lost. Cleaning stuff from the Server", 1);

		// order is important!
		active = false;
		io.disable();
		leaveRoom();
		server.removeUser(this);
	}

	public void leaveRoom() {
		if (room != null) {
			room.removeUser(this);
			room.broadcastState();
			room = null;
		}
	}

	private void login() throws IOException, GameException {
		while (username == null) {
			GameMessage m = io.read();
			if (m.is(GameMessage.LOGIN)) {
				String username = m.getParam("Username");
				String password = m.getParam("Password");

				if (username.length() == 0) {
					io.writeError(GameMessage.E_INVALID_USERNAME);
					continue;
				}

				if (password.length() == 0) {
					io.writeError(GameMessage.E_INVALID_PASSWORD);
					continue;
				}

				if (server.getUser(username) != null) {
					io.writeError(GameMessage.E_LOGGEDIN_USERNAME);
					continue;
				}

				// TODO: check password?

				this.username = username;
				server.addUser(this);
				io.writeOK();
				GameIO.debug("Logged In");
			} else {
				io.writeError(GameMessage.E_INVALID);
			}
		}
	}

	private void joinRoom(GameRoom room) throws GameException {
		if (this.room != null) {
			this.room.removeUser(this);
		}
		room.addUser(this);
		this.room = room;
	}

	public void prepareToPlay() {
		resetScore();
		ready = false;
	}

	private void loop() throws IOException, GameException {
		while (true) {
			GameIO.debug("Waiting in loop", 4);
			GameMessage m = io.read();
			GameMessage response = null;

			switch (m.getCode()) {
			/* PENDING */
			case GameMessage.ROOMS:
				response = new GameMessage(GameMessage.OK);
				response.addParam("Rooms", server.getRooms());
				io.write(response);
				break;
			case GameMessage.ROOM_MAKE:
				GameRoom room = new GameRoom(server, this, m
						.getParamAsInt("Room-Size"));
				joinRoom(room);

				response = new GameMessage(GameMessage.OK);
				room.buildRoomInfoMessage(response);
				io.write(response);

				this.room.broadcastState();
				break;
			case GameMessage.ROOM_INFO:
			case GameMessage.ROOM_JOIN:
				GameRoom roomInfo;
				if (m.getCode() == GameMessage.ROOM_INFO) {
					int roomOffset = m.getParamAsInt("Room-Offset");
					roomInfo = server.getRoomByOffset(roomOffset);
				} else {
					int roomId = m.getParamAsInt("RoomID");
					roomInfo = server.getRoom(roomId);
					if (roomInfo != null) {
						joinRoom(roomInfo);
					}
				}

				if (roomInfo != null) {
					response = new GameMessage(GameMessage.OK);
					roomInfo.buildRoomInfoMessage(response);
					io.write(response);

					if (this.room != null) {
						this.room.broadcastState();
					}
				} else {
					io.writeError(GameMessage.E_ROOM_NOT_FOUND);
				}
				break;
			/* WAITING */
			case GameMessage.READY:
				ready = true;
				roomNotify();
				break;
			case GameMessage.NOT_READY:
				ready = false;
				roomNotify();
				break;
			/* PLAYING */
			case GameMessage.GO:
				roomGo(m.getParamAsInt("Location"));
				break;
			}
		}
	}

	private void roomNotify() {
		if (room != null) {
			room.onUserChanged(this);
		}
	}

	private void roomGo(int location) {
		if (room != null) {
			room.onUserGo(this, location);
		}
	}

	public String getUsername() {
		return username;
	}

	public boolean isReady() {
		return ready;
	}

	public void setScore(int score) {
		if (room != null) {
			this.score = score;
			room.broadcastScores();
		}
	}

	public void increaseScore(int delta) {
		setScore(score + delta);
	}

	public void resetScore() {
		score = 0;
	}

	public int getScore() {
		if (room != null) {
			return score;
		} else {
			return 0;
		}
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		return "[USER:" + (username == null ? "<not-logged-in-yet>" : username)
				+ "]";
	}

	@Override
	public void run() {
		try {
			try {
				if (io.read().is(GameMessage.HELLO)) {
					io.writeOK();
				}
				GameIO.debug("Connection validated", 1);
				login();
				loop();
			} catch (GameException ge) {
				io.writeError(ge.getErrorCode());
			}
		} catch (Exception e) {
			// TODO: make sure to clean up stuff here, this is the last barrier
			// so... please don't mess up
			if (e instanceof NullPointerException) {
				e.printStackTrace();
			}
			GameIO.debug(e.toString());
			byebye();
		}
	}
}
