package org.sqsp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;
import com.daohoangson.GameParamList;
import com.daohoangson.event.GameEvent;
import com.daohoangson.event.GameEventListener;
import com.tranvietson.LoginUI;

public class Client implements ActionListener, GameEventListener {
	private GameClient gameClient;
	private LoginUI loginUI = null;

	public Client(String host, int port) {
		gameClient = new GameClient(host, port);
		gameClient.addGameEventListener(this);
	}

	public void doLogin() {
		GameIO.debug("Client.doLogin()");
		loginUI = new LoginUI();
		loginUI.setVisible(true);
		loginUI.onLogin(this);
	}

	public void doWait() {
		GameIO.debug("Client.doWait()");
	}

	public static void main(String[] args) {
		Client client;
		if (args.length == 0) {
			client = new Client("localhost", GameIO.DEFAULT_PORT);
		} else if (args.length == 1) {
			client = new Client(args[0], GameIO.DEFAULT_PORT);
		} else {
			client = new Client(args[1], Integer.valueOf(args[1]));
		}
		client.doLogin();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (loginUI != null) {
			String username = loginUI.getUsername();
			String password = loginUI.getPassword();

			if (gameClient.login(username, password)) {
				loginUI.setVisible(false);
			}
		}
	}

	@Override
	public void handleGameEvent(GameEvent ge) {
		switch (ge.getType()) {
		case GameEvent.LOGGED_IN:
			// TODO: Room selecting?
			int rooms = gameClient.rooms();
			if (rooms == 0) {
				gameClient.roomMake(400);
			} else {
				GameParamList roomInfo = gameClient.roomInfo(0);
				gameClient.roomJoin(roomInfo.getParamAsInt("RoomID"));
			}
			break;
		case GameEvent.JOINED_ROOM:
			doWait();
			break;
		}
	}
}
