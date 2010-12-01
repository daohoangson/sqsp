package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoginUI ui = new LoginUI();
		ui.show();
		ui.onLogin(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = ui.getUsername();
				String password = ui.getPassword();
				System.out.println("Username: " + username);
				System.out.println("Password: " + password);
			}
		});
	}

	public void onLogin(ActionListener al) {
		bt.addActionListener(al);
	}
}
