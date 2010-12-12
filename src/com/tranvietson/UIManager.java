package com.tranvietson;

public interface UIManager {
	/**
	 * Starts login process with the specified username and password
	 * 
	 * @param username
	 *            the username to login with
	 * @param password
	 *            the password to login with
	 */
	public void onLogin(String username, String password);

	public String getUsername();

	/**
	 * Changes ready state of current user
	 * 
	 * @param ready
	 *            the new state. A boolean value of <code>true</code> means the
	 *            user is ready, otherwise not ready
	 */
	public void onReadyChange(boolean ready);

	public boolean isReady();

	/**
	 * Sends the request to flip the specific card for current user
	 * 
	 * @param cardId
	 *            the id of the card, actually the offset value (zero based)
	 */
	public void onFlip(int cardId);

	public void onChat(String message);
}
