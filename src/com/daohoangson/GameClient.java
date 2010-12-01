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
		GameMessage response = sendMessage(login);

		if (isOK(response)) {
			this.username = username;
			GameIO.debug("Logged In");
			return true;
		}

		return false;
	}

	public int rooms() {
		GameMessage rooms = new GameMessage(GameMessage.ROOMS);
		GameMessage response = sendMessage(rooms);

		if (isOK(response)) {
			return response.getParamAsInt("Rooms");
		}

		return 0;
	}

	public int roomMake() {
		GameMessage roomMake = new GameMessage(GameMessage.ROOM_MAKE);
		GameMessage response = sendMessage(roomMake);

		if (isOK(response)) {
			return response.getParamAsInt("RoomID");
		}

		return 0;
	}

	private GameMessage sendMessage(GameMessage m) {
		try {
			io.write(m);
			m = io.read();
			if (m.is(GameMessage.ERROR)) {
				GameIO.debug("Error: " + m.getParam("Error-Code"));
				return null;
			}
			return m;
		} catch (IOException e) {
			GameIO.debug(e.toString());
			return null;
		}
	}

	private boolean isOK(GameMessage m) {
		return m != null && m.is(GameMessage.OK);
	}

	public String getUsername() {
		return username;
	}
}
