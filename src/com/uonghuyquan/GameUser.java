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
			GameIO.debug(e.toString());
			GameIO.debug("Connection lost. Cleaning stuff from the Server", 1);
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

	private void loop() throws IOException, GameException {
		while (true) {
			GameIO.debug("Waiting in loop", 4);
			GameMessage m = io.read();
			GameMessage response = null;

			switch (m.getCode()) {
			case GameMessage.ROOMS:
				int rooms = server.getRooms();
				response = new GameMessage(GameMessage.OK);
				response.addParam("Rooms", rooms);
				io.write(response);
				break;
			case GameMessage.ROOM_MAKE:
				GameRoom room = new GameRoom(server);
				if (this.room != null) {
					room.removeUser(this);
				}
				room.addUser(this);
				response = new GameMessage(GameMessage.OK);
				response.addParam("RoomID", room.getId());
				io.write(response);
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
