package com.uonghuyquan;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.daohoangson.GameIO;

public class GameServer extends GameUserList {
	private List<GameRoom> rooms = new ArrayList<GameRoom>();

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

	public void addRoom(GameRoom room) {
		rooms.add(room);
	}

	public boolean removeRoom(GameRoom room) {
		if (rooms.contains(room)) {
			rooms.remove(room);
			return true;
		}

		return false;
	}

	public GameRoom getRoom(int roomId) {
		Iterator<GameRoom> i = rooms.iterator();
		while (i.hasNext()) {
			GameRoom room = i.next();
			if (room.getId() == roomId) {
				return room;
			}
		}

		return null;
	}

	public GameRoom getRoomByOffset(int roomOffset) {
		if (rooms.size() > roomOffset) {
			return rooms.get(roomOffset);
		}

		return null;
	}

	public int getRooms() {
		return rooms.size();
	}

	@Override
	public String toString() {
		return "Server";
	}
}
