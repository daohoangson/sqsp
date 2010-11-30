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
				server.addUser(this);
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

	private void login() throws IOException {
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
				io.writeOK();
			} else {
				io.writeError(GameMessage.E_INVALID);
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
