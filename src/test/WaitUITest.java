package test;

import com.tranvietson.UIManager;
import com.tranvietson.WaitUI;

public class WaitUITest {
	public static WaitUI ui;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WaitUITest.ui = new WaitUI(new UIManager() {

			@Override
			public void onFlip(int cardId) {

			}

			@Override
			public void onLogin(String username, String password) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReadyChange(boolean ready) {
				// TODO Auto-generated method stub

			}

		}, true);
		WaitUITest.ui.setVisible(true);
		String[] usernames = { "User1", "User2" };
		boolean[] readys = { true, true };
		WaitUITest.ui.updateReady(usernames, readys);
	}
}
