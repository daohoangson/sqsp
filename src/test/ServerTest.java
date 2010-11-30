package test;

import com.daohoangson.GameIO;
import com.uonghuyquan.GameServer;

public class ServerTest {
	public static void main(String[] args) {
		new GameServer(GameIO.DEFAULT_PORT);
	}
}
