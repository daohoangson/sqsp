package com.daohoangson;

import java.io.IOException;
import java.net.Socket;

public class GameClient {
	private GameIO io;
	private String username = null;

	public GameClient(String host, int port) {
		try {
			Socket socket = new Socket(host, port);
			io = new GameIO(socket);

			io.write(new GameMessage(GameMessage.HELLO));
			if (io.read().is(GameMessage.OK)) {
				GameIO.debug("Connection validated", 1);
			}
		} catch (Exception e) {
			GameIO.debug(e.toString());
			System.exit(1);
		}
	}

	public boolean login(String username, String password) {
		GameMessage login = new GameMessage(GameMessage.LOGIN);
		login.addParam("Username", username);
		login.addParam("Password", password);
		try {
			io.write(login);
			GameMessage response = io.read();
			if (response.is(GameMessage.OK)) {
				this.username = username;
				GameIO.debug("Logged In");
				return true;
			} else if (response.is(GameMessage.ERROR)) {
				GameIO.debug("Error: " + response.getParam("Error-Code"));
			}
		} catch (IOException e) {
			GameIO.debug(e.toString());
		}

		return false;
	}

	public String getUsername() {
		return username;
	}
}
