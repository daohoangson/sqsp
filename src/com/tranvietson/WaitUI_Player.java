package com.tranvietson;

public class WaitUI_Player {
	private String username;
	private boolean ready;

	public WaitUI_Player(String username, boolean ready) {
		this.username = username;
		this.ready = ready;
	}

	public String getUsername() {
		return new String(username);
	}

	public boolean isReady() {
		return ready;
	}

	@Override
	public String toString() {
		return getUsername() + " (" + (ready ? "Ready" : "Not Ready") + ")";
	}
}
