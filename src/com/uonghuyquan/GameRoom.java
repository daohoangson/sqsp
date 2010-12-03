package com.uonghuyquan;

import java.io.IOException;

import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameRoom extends GameUserList implements Runnable {
	private static int count = 0;
	private int id;
	@SuppressWarnings("unused")
	private GameServer server;
	private GameUser host;
	private int size;
	private int status;
	private boolean active = true;

	public GameRoom(GameServer server, GameUser host, int size) {
		id = ++GameRoom.count;
		this.server = server;
		this.host = host;
		this.size = size;

		server.addRoom(this);

		new Thread(this).start();

		GameIO.debug("GameRoom created", 2);
	}

	public int getId() {
		return id;
	}

	public GameUser getHost() {
		return host;
	}

	public int getSize() {
		return size;
	}

	public int getStatus() {
		return status;
	}

	public void buildRoomInfoMessage(GameMessage m) {
		m.addParam("RoomID", getId());
		m.addParam("Room-Size", getSize());
		m.addParam("Host", getHost().getUsername());
		m.addParam("Users", getUsers());
		GameUser[] users = this.users.values().toArray(new GameUser[0]);
		for (int i = 0; i < users.length; i++) {
			GameUser user = users[i];
			if (user != null) {
				m.addParam("User" + i, user.getUsername());
				m.addParam("Ready" + i, user.isReady() ? 1 : 0);
			}
		}
		m.addParam("Status", getStatus());
	}

	private void broadcast(GameMessage broadcastMessage) {
		synchronized (users) {
			GameUser[] users = this.users.values().toArray(new GameUser[0]);
			for (int i = 0; i < users.length; i++) {
				try {
					users[i].io.write(broadcastMessage);
					users[i].io.read();
				} catch (IOException e) {
					users[i].byebye();
				}
			}
		}
	}

	public void run() {
		while (true) {
			if (!active) {
				return;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				GameIO.debug(e.toString());
			}
			if (users.size() == 0) {
				continue;
			}
			GameMessage m = new GameMessage(GameMessage.ROOM_STATE);
			buildRoomInfoMessage(m);
			broadcast(m);
		}
	}

	@Override
	public String toString() {
		return "Room #" + id;
	}
}
