package com.uonghuyquan;

import java.io.IOException;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameUser {
	private GameServer server;
	private GameIO io;
	private GameRoom room = null;
	private String username = null;

	public GameUser(GameServer server, GameIO io) {
		this.server = server;
		this.io = io;

		try {
			try {
				GameIO.debug("GameUser created", 3);
				if (io.read().is(GameMessage.HELLO)) {
					io.writeOK();
				}
				GameIO.debug("Connection validated", 1);
				login();
				GameIO.debug("Logged In");
				loop();
			} catch (GameException ge) {
				io.writeError(ge.getErrorCode());
			}
		} catch (Exception e) {
			// TODO: make sure to clean up stuff here, this is the last barrier
			// so.. don't mess up
			GameIO.debug(e.toString());
			GameIO.debug("Connection lost. Cleaning stuff from the Server", 1);
			if (room != null) {
				room.removeUser(this);
			}
			server.removeUser(this);
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

	private void loop() throws IOException, GameException {
		while (true) {
			GameIO.debug("Waiting in loop", 4);
			GameMessage m = io.read();
			GameMessage response = null;

			switch (m.getCode()) {
			case GameMessage.ROOMS:
				response = new GameMessage(GameMessage.OK);
				response.addParam("Rooms", server.getRooms());
				io.write(response);
				break;
			case GameMessage.ROOM_MAKE:
				GameRoom room = new GameRoom(server, m
						.getParamAsInt("Room-Size"));
				joinRoom(room);

				response = new GameMessage(GameMessage.OK);
				response.addParam("RoomID", room.getId());
				io.write(response);
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
					response.addParam("RoomID", roomInfo.getId());
					response.addParam("Room-Size", roomInfo.getSize());
					response.addParam("Users", roomInfo.getUsers());
					for (int i = 0; i < roomInfo.getUsers(); i++) {
						GameUser user = roomInfo.getUserByOffset(i);
						if (user != null) {
							response.addParam("User" + (i + 1), user
									.getUsername());
						}
					}
					response.addParam("Status", roomInfo.getStatus());
					io.write(response);
				} else {
					io.writeError(GameMessage.E_ROOM_NOT_FOUND);
				}
				break;
			}
		}
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "[USER:" + (username == null ? "<not-logged-in-yet>" : username)
				+ "]";
	}
}
