package test;

import java.util.Scanner;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;

public class ClientTest {
	public static void main(String[] args) {
		GameClient gc = new GameClient("localhost", GameIO.DEFAULT_PORT);
		if (gc.login(args[0], "123456")) {
			int rooms = gc.rooms();
			System.out.println("Rooms: " + rooms);
			if (rooms == 0) {
				gc.roomMake(40);
			} else {
				gc.roomJoin(1);
			}
		}

		Scanner sc = new Scanner(System.in);
		sc.nextLine();
	}
}
