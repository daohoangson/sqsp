package com.tranvietson;

public class PlayUI_Player {
	private String username;
	private int score;
	private boolean isThisUser = false;
	private boolean inTurn = false;

	public PlayUI_Player(String username, int score, boolean isThisUser) {
		this.username = username;
		this.score = score;
		this.isThisUser = isThisUser;
	}

	public String getUsername() {
		return new String(username);
	}

	public void setTurn(boolean inTurn) {
		this.inTurn = inTurn;
	}

	public boolean isInTurn() {
		return inTurn;
	}

	@Override
	public String toString() {
		return username + (isThisUser ? " (You)" : "") + ": " + score;
	}
}
