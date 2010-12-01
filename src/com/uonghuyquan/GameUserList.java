package com.uonghuyquan;

import java.util.HashMap;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameUserList {
	private HashMap<String, GameUser> users = new HashMap<String, GameUser>();

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
