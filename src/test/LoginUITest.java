package test;

import com.tranvietson.LoginUI;
import com.tranvietson.UIManager;

public class LoginUITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoginUI ui = new LoginUI(new UIManager() {

			@Override
			public void onFlip(int cardId) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLogin(String username, String password) {
				System.out.println(username);
				System.out.println(password);
			}

			@Override
			public void onReadyChange(boolean ready) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getUsername() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onChat(String message) {
				// TODO Auto-generated method stub

			}

		});
		ui.setVisible(true);
	}
}
