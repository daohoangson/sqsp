package test;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;

public class ClientTest {
	public static void main(String[] args) {
		GameClient gc = new GameClient("localhost", GameIO.DEFAULT_PORT);
		if (gc.login("User1", "123456")) {

		}
	}
}
