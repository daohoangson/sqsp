package test;

import com.daohoangson.GameMessage;

public class GameMessageTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameMessage m = new GameMessage();
		m.setCode(GameMessage.HELLO);
		System.out.println(m);

		m = new GameMessage();
		m.setCode(GameMessage.GO);
		m.addParam("Cell", 10);
		System.out.println(m);
	}

}
