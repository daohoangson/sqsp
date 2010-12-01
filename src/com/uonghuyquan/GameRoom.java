package com.uonghuyquan;

public class GameRoom extends GameUserList {
	private static int count = 0;
	private int id;
	private GameServer server;

	public GameRoom(GameServer server) {
		id = ++GameRoom.count;
		this.server = server;

		server.addRoom(this);
	}

	public int getId() {
		return id;
	}
}
