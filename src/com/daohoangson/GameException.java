package com.daohoangson;

/**
 * A game exception containing a game error
 * 
 * @author Dao Hoang Son
 * 
 */
public class GameException extends Exception {
	private static final long serialVersionUID = -4054102834198253737L;
	private int errorCode;

	/**
	 * Constructs the exception with an error code
	 * 
	 * @param errorCode
	 *            the error code
	 */
	public GameException(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error code stored in the exception
	 * 
	 * @return the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}
}
