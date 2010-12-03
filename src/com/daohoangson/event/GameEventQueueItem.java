package com.daohoangson.event;

import java.util.Iterator;

public class GameEventQueueItem {
	public GameEvent ge;
	public Iterator<GameEventListener> i;

	public GameEventQueueItem(GameEvent ge, Iterator<GameEventListener> i) {
		this.ge = ge;
		this.i = i;
	}
}
