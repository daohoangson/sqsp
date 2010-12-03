package org.sqsp;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;
import com.daohoangson.GameParamList;
import com.daohoangson.event.GameEvent;
import com.daohoangson.event.GameEventListener;
import com.tranvietson.LoginUI;
import com.tranvietson.UIManager;

public class Client implements GameEventListener, UIManager {
	private GameClient gameClient;
	private LoginUI loginUI = null;

	public Client(String host, int port) {
		gameClient = new GameClient(host, port);
		gameClient.addGameEventListener(this);
	}

	public void doLogin() {
		if (gameClient.getUsername() == null) {
			// only start login procedure if the user hasn't logged in yet
			GameIO.debug("Client.doLogin()");
			loginUI = new LoginUI();
			loginUI.setVisible(true);
		}
	}

	public void doWait() {
		if (gameClient.getRoomId() > 0
				&& gameClient.getRoomStatus() == GameMessage.RS_WAITING) {
			GameIO.debug("Client.doWait()");
		}
	}

	public void doPlay() {
		GameIO.debug("Client.doPlay()");
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
			gameClient.setReady(true);
			break;
		case GameEvent.JOINED_ROOM:
		case GameEvent.WAITING:
			doWait();
			break;
		case GameEvent.STARTED:
			doPlay();
			break;
		}
	}

	@Override
	public void onFlip(int cardId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLogin(String username, String password) {
		if (loginUI != null && loginUI.isVisible()
				&& gameClient.login(username, password)) {
			// only login when the loginUI is visible
			loginUI.setVisible(false);
			loginUI = null;
		}
	}

	@Override
	public void onReadyChange(boolean ready) {
		if (gameClient.getRoomId() > 0) {
			// only set ready if we are in a room
			gameClient.setReady(ready);
		}
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
}
