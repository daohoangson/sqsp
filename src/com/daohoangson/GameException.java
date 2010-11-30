package com.daohoangson;

public class GameException extends Exception {
	private static final long serialVersionUID = -4054102834198253737L;
	private int errorCode;

	public GameException(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
