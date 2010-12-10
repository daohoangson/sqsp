package test;

import com.tranvietson.PlayUI;
import com.tranvietson.UIManager;

public class PlayUITest {
	public static PlayUI ui;

	public static void main(String[] args) {
		PlayUITest.ui = new PlayUI(new UIManager() {

			@Override
			public void onFlip(int cardId) {
				System.out.println(cardId);
			}

			@Override
			public void onLogin(String username, String password) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReadyChange(boolean ready) {
				// TODO Auto-generated method stub

			}

		}, 400);
		PlayUITest.ui.setVisible(true);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 25; i++) {
			System.out.println(i);
			PlayUITest.ui.flipCard("Anon",i, 1);
		}
	}
}
