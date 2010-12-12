package org.sqsp;

import com.daohoangson.GameIO;
import com.uonghuyquan.GameServer;

public class Server {
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		GameServer server;
		if (args.length == 0) {
			server = new GameServer(GameIO.DEFAULT_PORT);
		} else if (args.length == 1) {
			server = new GameServer(Integer.valueOf(args[0]));
		}
	}
}