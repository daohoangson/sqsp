package com.tranvietson;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class WaitUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8589567281000101357L;
	private final UIManager manager;
	private final JButton button;
	private final JLabel label;
	private final JPanel panel;
	private final JList list;
	private final JScrollPane scroll;
	DefaultListModel model = new DefaultListModel();

	private String[] status = null;

	public WaitUI(UIManager manager, boolean host) {
		super("Waiting Room");

		this.manager = manager;

		// Set JList
		list = new JList(model);
		for (int i = 0; i < status.length; i++) {
			model.addElement(status[i]);
		}

		// Set Scroll
		scroll = new JScrollPane();
		scroll.getViewport().add(list);
		scroll.setBounds(20, 110, 230, 130);

		// Set label
		label = new JLabel();
		label.setText("Players in game:");
		label.setBounds(10, 80, 100, 30);

		// Set Button
		button = new JButton();
		button.setBounds(270, 170, 90, 90);

		if (host == true) { // set Button text
			button.setText("START");
			button.setToolTipText("Let's Start Game");
		} else {
			button.setText("READY");
			button.setToolTipText("Click To Ready");
		}

		button.addActionListener(this);

		// Set Panel
		panel = new JPanel();
		panel.setLayout(null);
		panel.add(scroll);
		panel.add(button);
		panel.add(label);

		// Set JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 320);
		add(panel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		manager.onReadyChange(true);
	}

	/**
	 * updateReady method: update users' status
	 * 
	 * @param username
	 * @param ready
	 */
	@SuppressWarnings("null")
	public void updateReady(String[] username, boolean[] ready) {
		String[] s = null;
		for (int i = 0; i < ready.length; i++) {
			if (ready[i] == true) {
				s[i] = "- " + username[i] + " (READY)";
			} else
				s[i] = "- " + username[i] + " (NOT READY)";
		}
		status = s;
	}
}