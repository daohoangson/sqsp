package org.sqsp;

import java.util.Random;

import com.daohoangson.GameIO;
import com.daohoangson.event.GameEvent;

public class ClientAuto extends Client {
	public ClientAuto(String host, int port) {
		super(host, port);
	}

	@Override
	public void doLogin() {
		Random rand = new Random();
		onLogin("AutoUser" + rand.nextInt(), "password");
	}

	@Override
	public void doWait() {
		onReadyChange(true);
	}

	@Override
	public void doPlay() {
		// do nothing
	}

	@Override
	public void handleGameEvent(GameEvent ge) {
		switch (ge.getType()) {
		case GameEvent.TURN:
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			onFlip(gameClient.aiThink());
			break;
		default:
			super.handleGameEvent(ge);
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			new ClientAuto("localhost", GameIO.DEFAULT_PORT);
		} else if (args.length == 1) {
			new ClientAuto(args[0], GameIO.DEFAULT_PORT);
		} else {
			new ClientAuto(args[0], Integer.valueOf(args[1]));
		}
	}
}
