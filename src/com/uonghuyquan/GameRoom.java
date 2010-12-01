package com.uonghuyquan;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;

public class GameRoom extends GameUserList {
	private static int count = 0;
	private int id;
	private GameServer server;
	private int size;
	private int status;

	public GameRoom(GameServer server, int size) {
		id = ++GameRoom.count;
		this.server = server;
		this.size = size;

		GameIO.debug("GameRoom created", 2);

		server.addRoom(this);
	}

	public int getId() {
		return id;
	}

	public int getSize() {
		return size;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public void addUser(GameUser user) throws GameException {
		super.addUser(user);

		GameUser[] users = this.users.values().toArray(new GameUser[0]);
		for (int i = 0; i < users.length; i++) {

		}
	}

	@Override
	public String toString() {
		return "Room #" + id;
	}
}
