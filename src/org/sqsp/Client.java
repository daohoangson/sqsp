package org.sqsp;

import com.daohoangson.GameClient;
import com.daohoangson.GameIO;
import com.daohoangson.GameParamList;
import com.daohoangson.event.GameEvent;
import com.daohoangson.event.GameEventListener;
import com.tranvietson.LoginUI;
import com.tranvietson.PlayUI;
import com.tranvietson.UIManager;
import com.tranvietson.WaitUI;

public class Client implements GameEventListener, UIManager {
	private GameClient gameClient;
	private LoginUI loginUI = null;
	private WaitUI waitUI = null;
	private PlayUI playUI = null;

	public Client(String host, int port) {
		gameClient = new GameClient(host, port);
		gameClient.addGameEventListener(this);
	}

	public void doLogin() {
		if (gameClient.getUsername() == null) {
			// only start login procedure if the user hasn't logged in yet
			GameIO.debug("Client.doLogin()");
			loginUI = new LoginUI(this);
			loginUI.setVisible(true);
		}
	}

	public void doWait() {
		GameIO.debug("Client.doWait()");
		waitUI = new WaitUI(this, gameClient.isHost());
		waitUI.setVisible(true);
	}

	public void doPlay() {
		GameIO.debug("Client.doPlay()");
		playUI = new PlayUI(this, gameClient.getRoomSize());
		playUI.setVisible(true);
	}

	@Override
	public void handleGameEvent(GameEvent ge) {
		switch (ge.getType()) {
		case GameEvent.LOGGED_IN:
			// TODO: Implement the room selecting feature
			int rooms = gameClient.rooms();
			if (rooms == 0) {
				gameClient.roomMake(2);
			} else {
				GameParamList roomInfo = gameClient.roomInfo(0);
				gameClient.roomJoin(roomInfo.getParamAsInt("RoomID"));
			}
			break;
		case GameEvent.JOINED_ROOM:
		case GameEvent.WAITING:
			if (playUI != null) {
				playUI.setVisible(false);
				playUI = null;
			}
			doWait();
			break;
		case GameEvent.ROOM_STATE:
			if (waitUI != null) {
				int count = gameClient.getRoomUsers();
				String[] usernames = new String[count];
				boolean[] readys = new boolean[count];
				for (int i = 0; i < count; i++) {
					usernames[i] = gameClient.getRoomUsername(i);
					readys[i] = gameClient.getRoomReady(usernames[i]);
				}
				waitUI.updateReady(usernames, readys);
			}
			break;
		case GameEvent.STARTED:
			if (waitUI != null) {
				waitUI.setVisible(false);
				waitUI = null;
			}
			doPlay();
			break;
		case GameEvent.TURN:
			if (playUI != null) {
				playUI.setTitle("GO!");
			}
			break;
		case GameEvent.GO_MOVED:
			String username = ge.gameMessage.getParam("Username");
			int location = ge.gameMessage.getParamAsInt("Location");
			int code = ge.gameMessage.getParamAsInt("Code");
			if (playUI != null) {
				playUI.flipCard(username, location, code);
				playUI.setTitle("");
			}
			break;
		case GameEvent.GO_DONE:
			if (playUI != null) {
				playUI.flipCards();
			}
			break;
		case GameEvent.SCORED:
			if (playUI != null) {
				int[] locations = gameClient.getOpenedLocations();
				playUI.destroyCards(locations[0], locations[1]);
			}
			break;
		case GameEvent.OWN_SCORED:
			System.out.println("SCORED! "
					+ gameClient.getRoomScore(gameClient.getUsername()));
			break;
		case GameEvent.WON:
			System.out.println("GAME ENDED");
			break;
		}
	}

	@Override
	public void onFlip(int cardId) {
		gameClient.go(cardId);
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
		if (waitUI != null && gameClient.getRoomId() > 0) {
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
