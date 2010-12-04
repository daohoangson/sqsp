package com.uonghuyquan.backup;

import com.daohoangson.GameIO;

public class GameThread implements Runnable {
	private GameUser user;

	public GameThread(GameUser user) {
		this.user = user;

		new Thread(this).start();
	}

	public GameThread(GameServer server, GameIO io) {
		this(new GameUser(server, io));
	}

	@Override
	public void run() {
		user.start();
	}
}
