package com.uonghuyquan;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameServer {
	private HashMap<String, GameUser> users = new HashMap<String, GameUser>();

	public GameServer(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				GameIO.debug("Waiting for connection...");
				try {
					Socket socket = serverSocket.accept();
					GameIO io = new GameIO(socket);

					new Thread(new GameThread(this, io)).start();
				} catch (Exception e) {
					GameIO.debug(e.toString());
				}
			}
		} catch (Exception e) {
			GameIO.debug(e.toString());
			System.exit(1);
		}
	}

	public void addUser(GameUser user) throws GameException {
		String username = user.getUsername();
		if (users.containsKey(username)) {
			throw new GameException(GameMessage.E_LOGGEDIN_USERNAME);
		}
		users.put(username, user);
		GameIO.debug("Added " + user);
	}

	public boolean removeUser(GameUser user) {
		String username = user.getUsername();
		if (username != null) {
			if (users.containsKey(username)) {
				users.remove(username);
				GameIO.debug("Removed " + user);
				return true;
			}
		}

		return false;
	}

	public GameUser getUser(String username) {
		if (users.containsKey(username)) {
			return users.get(username);
		} else {
			return null;
		}
	}
}
