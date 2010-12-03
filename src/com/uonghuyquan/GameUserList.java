package com.uonghuyquan;

import java.util.HashMap;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public abstract class GameUserList {
	protected HashMap<String, GameUser> users = new HashMap<String, GameUser>();

	public void addUser(GameUser user) throws GameException {
		String username = user.getUsername();
		if (users.containsKey(username)) {
			throw new GameException(GameMessage.E_LOGGEDIN_USERNAME);
		}
		users.put(username, user);
		debug("Added " + user);
	}

	public boolean removeUser(GameUser user) {
		String username = user.getUsername();
		if (username != null) {
			if (users.containsKey(username)) {
				users.remove(username);
				debug("Removed " + user);
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

	public GameUser getUserByOffset(int userOffset) {
		if (userOffset < users.size()) {
			return users.values().toArray(new GameUser[0])[userOffset];
		} else {
			return null;
		}
	}

	public int getUsers() {
		return users.size();
	}

	private void debug(String message) {
		GameIO.debug("[" + this + "] " + message);
	}
}
