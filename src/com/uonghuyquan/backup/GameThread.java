package com.uonghuyquan.backup;

import com.daohoangson.GameIO;

public class GameThread implements Runnable {
	private GameServer server;
	private GameIO io;

	public GameThread(GameServer server, GameIO io) {
		this.server = server;
		this.io = io;
	}

	@Override
	public void run() {
		new GameUser(server, io);
	}
}
