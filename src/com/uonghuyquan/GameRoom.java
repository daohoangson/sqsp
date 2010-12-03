package com.uonghuyquan;

import java.io.IOException;

import com.daohoangson.GameException;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameRoom extends GameUserList {
	private static int count = 0;
	private int id;
	private GameServer server;
	private GameUser host;
	private int size;
	private int status;

	public GameRoom(GameServer server, GameUser host, int size) {
		id = ++GameRoom.count;
		this.server = server;
		this.host = host;
		this.size = size;

		GameIO.debug("GameRoom created", 2);

		server.addRoom(this);
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

	@Override
	public void addUser(GameUser user) throws GameException {
		super.addUser(user);

		GameMessage broadcastMessage = new GameMessage(GameMessage.ROOM_STATE);
		buildRoomInfoMessage(broadcastMessage);
		broadcast(broadcastMessage);
	}

	@Override
	public boolean removeUser(GameUser user) {
		boolean b = super.removeUser(user);

		if (b) {
			GameMessage broadcastMessage = new GameMessage(
					GameMessage.ROOM_STATE);
			buildRoomInfoMessage(broadcastMessage);
			broadcast(broadcastMessage);
		}

		return b;
	}

	private void broadcast(GameMessage broadcastMessage) {
		GameUser[] users = this.users.values().toArray(new GameUser[0]);
		for (int i = 0; i < users.length; i++) {
			try {
				users[i].io.write(broadcastMessage);
			} catch (IOException e) {
				GameIO.debug(e.toString(), 2);
			}
		}
	}

	public synchronized boolean waitFor(GameUser user) {
		return false;
	}

	public synchronized void play(GameUser user) {
		// TODO
	}

	@Override
	public String toString() {
		return "Room #" + id;
	}
}
