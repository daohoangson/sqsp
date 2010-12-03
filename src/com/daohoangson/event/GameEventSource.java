package com.daohoangson.event;

import java.util.ArrayList;
import java.util.List;

import com.daohoangson.GameIO;
import com.daohoangson.GameMessage;

public class GameEventSource {
	private List<GameEventListener> listeners = new ArrayList<GameEventListener>();

	public synchronized void addGameEventListener(GameEventListener gel) {
		listeners.add(gel);
	}

	public synchronized void removeGameEventListener(GameEventListener gel) {
		listeners.remove(gel);
	}

	protected void fireGameEvent(int type, GameMessage m) {
		fireGameEvent(new GameEvent(this, type, m));
	}

	protected void fireGameEvent(int type) {
		fireGameEvent(new GameEvent(this, type, null));
	}

	protected void fireGameEvent(GameMessage m) {
		fireGameEvent(new GameEvent(this, 0, m));
	}

	private void fireGameEvent(GameEvent ge) {
		GameMessage m = ge.gameMessage;
		if (m != null) {
			GameIO.debug("GameEvent fired: "
					+ GameMessage.lookupCodeToString(ge.gameMessage.getCode()),
					4);
		} else {
			GameIO.debug("GameEvent fired: #" + ge.getType(), 4);
		}

		GameEventQueue.getInstance().add(ge, listeners.iterator());
	}
}
