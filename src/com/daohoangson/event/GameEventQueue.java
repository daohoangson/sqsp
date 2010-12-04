package com.daohoangson.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEventQueue implements Runnable {
	private static GameEventQueue instance = null;
	private List<GameEventQueueItem> queue = new ArrayList<GameEventQueueItem>();

	public GameEventQueue() {
		new Thread(this).start();
	}

	public static GameEventQueue getInstance() {
		if (GameEventQueue.instance == null) {
			GameEventQueue.instance = new GameEventQueue();
		}

		return GameEventQueue.instance;
	}

	public void add(GameEvent ge, Iterator<GameEventListener> i) {
		queue.add(new GameEventQueueItem(ge, i));
	}

	public void run() {
		synchronized (this) {
			while (true) {
				while (queue.size() == 0) {
					// wait
				}
				GameEventQueueItem q = queue.get(0);
				if (q != null) {
					while (q.i.hasNext()) {
						q.i.next().handleGameEvent(q.ge);
					}
					queue.remove(0);
				}
			}
		}
	}
}
