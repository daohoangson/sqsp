package com.daohoangson.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.daohoangson.GameMessage;

public class GameEventSource {
	private List<GameEventListener> listeners = new ArrayList<GameEventListener>();

	public synchronized void addGameEventListener(GameEventListener gel) {
		listeners.add(gel);
	}

	public synchronized void removeGameEventListener(GameEventListener gel) {
		listeners.remove(gel);
	}

	protected synchronized void fireGameEvent(int type, GameMessage m) {
		GameEvent ge = new GameEvent(this, type, m);
		fireGameEvent(ge);
	}

	protected synchronized void fireGameEvent(int type) {
		GameEvent ge = new GameEvent(this, type);
		fireGameEvent(ge);
	}

	protected synchronized void fireGameEvent(GameEvent ge) {
		Iterator<GameEventListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().handleGameEvent(ge);
		}
	}
}
