package com.daohoangson.event;

import java.util.EventObject;

import com.daohoangson.GameMessage;

public class GameEvent extends EventObject {
	private int type;
	public GameMessage gameMessage = null;

	public final static int LOGGED_IN = 1;

	public GameEvent(Object source, int type, GameMessage m) {
		super(source);
		this.type = type;
		gameMessage = m;
	}

	public GameEvent(Object source, int type) {
		super(source);
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
