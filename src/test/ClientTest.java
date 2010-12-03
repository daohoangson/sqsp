package test;

import java.util.Scanner;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;
import com.daohoangson.GameParamList;

public class ClientTest {
	public static void main(String[] args) {
		GameClient gc = new GameClient("192.168.1.104", GameIO.DEFAULT_PORT);
		if (gc.login(args[0], "123456")) {
			System.out.println("Logged In");
			int rooms = gc.rooms();
			System.out.println("Rooms: " + rooms);
			if (rooms == 0) {
				gc.roomMake(40);
			} else {
				GameParamList roomInfo = gc.roomInfo(0);
				gc.roomJoin(roomInfo.getParamAsInt("RoomID"));
			}
			if (gc.getRoomId() > 0) {
				// joined a room
				// gc.waiting();
			}
		}

		Scanner sc = new Scanner(System.in);
		sc.nextLine();
	}
}
