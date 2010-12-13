package test;

import com.daohoangson.GameParamList;
import com.tranvietson.PlayUI;
import com.tranvietson.UIManager;

public class PlayUITest {
	public static PlayUI ui;
	static int z = 1;

	public static void main(String[] args) {
		PlayUITest.ui = new PlayUI(new UIManager() {

			@Override
			public void onFlip(int cardId) {
				if (PlayUITest.z == 1) {
					PlayUITest.ui.updateTurn("User1");
					PlayUITest.z = 0;
				} else {
					PlayUITest.ui.updateTurn("User2");
					PlayUITest.z = 1;
				}

				GameParamList params = new GameParamList();
				params.addParam("Username", "User1");
				params
						.addParam("Content",
								"Blah blah blah bal klajs dflkj alskdjf lkasjdf lkasjd flkjasd fkj");
				params.addParam("From-System", 1);
				PlayUITest.ui.displayChat(params);

				PlayUITest.ui.flipCard("User1", cardId, 2);
			}

			@Override
			public void onLogin(String username, String password) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReadyChange(boolean ready) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getUsername() {
				// TODO Auto-generated method stub
				return "User1";
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

		}, 36);
		PlayUITest.ui.setVisible(true);
		PlayUITest.ui.updateScores(new String[] { "User1", "User2" },
				new int[] { 0, 1 });

	}
}
