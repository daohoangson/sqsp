package com.daohoangson.event;

import java.util.EventObject;

import com.daohoangson.GameMessage;

public class GameEvent extends EventObject {
	private static final long serialVersionUID = -7131351869818942706L;
	private int type;
	public GameMessage gameMessage = null;

	public final static int LOGGED_IN = 1;
	public final static int JOINED_ROOM = 2;
	public final static int WAITING = 3;
	public final static int STARTED = 4;
	public final static int IOException = 9999;

	public GameEvent(Object source, int type, GameMessage m) {
		super(source);
		this.type = type;
		gameMessage = m;
	}

	public int getType() {
		return type;
	}
}
