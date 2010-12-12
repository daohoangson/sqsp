/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on Nov 27, 2010, 6:58:32 PM
 */

package com.tranvietson;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * 
 * @author Tran Viet Son
 */
public class LoginUI extends RootUI implements ActionListener {
	private static final long serialVersionUID = 2824207873393605683L;
	private final UIManager manager;
	private JPasswordField txtPassword;
	private JTextField txtUsername;

	public LoginUI(UIManager manager) {
		this.manager = manager;
		initComponents();
	}

	private void initComponents() {
		txtUsername = new JTextField();
		txtPassword = new JPasswordField();
		JLabel lbUsername = new JLabel();
		JLabel lbPassword = new JLabel();
		JButton btnLogIn = new JButton();
		JPanel pnInner = new JPanel();
		JPanel pnOutter = new JPanel();

		pnInner.setLayout(new GridLayout(2, 2));
		lbUsername.setText("Username");
		pnInner.add(lbUsername);
		txtUsername.addActionListener(this);
		pnInner.add(txtUsername);

		lbPassword.setText("Password");
		pnInner.add(lbPassword);
		txtPassword.addActionListener(this);
		txtPassword.setEnabled(false);
		pnInner.add(txtPassword);

		btnLogIn.setText("Log In");
		btnLogIn.addActionListener(this);
		btnLogIn.setAlignmentX(Component.CENTER_ALIGNMENT);

		pnOutter.setLayout(new BoxLayout(pnOutter, BoxLayout.Y_AXIS));
		pnOutter.add(pnInner);
		pnOutter.add(Box.createVerticalStrut(25));
		pnOutter.add(btnLogIn);

		setTitle("Log In");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		BufferedImage icon = null;
		try {
			File imageFile = new File("images\\card.jpg");
			icon = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIconImage(icon);

		setResizable(false);
		add(pnOutter);
		pack();
		setSize(500, getHeight());
		setLocationRelativeTo(null);
	}

	public String getUsername() {
		return txtUsername.getText();
	}

	public String getPassword() {
		if (txtPassword.isEnabled()) {
			return new String(txtPassword.getPassword());
		} else {
			return "password";
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		manager.onLogin(getUsername(), getPassword());
	}

}
