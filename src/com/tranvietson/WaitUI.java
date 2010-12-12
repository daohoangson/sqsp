package com.tranvietson;

import images.SpriteManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class WaitUI extends RootUI implements ActionListener {
	private static final long serialVersionUID = -8589567281000101357L;
	private final UIManager manager;
	private JButton btReady;
	private final DefaultListModel players = new DefaultListModel();

	public WaitUI(UIManager manager) {
		this.manager = manager;
		initComponents();
	}

	private void initComponents() {
		JList lPlayers = new JList(players);
		lPlayers.setCellRenderer(new WaitUI_PlayerRenderer());
		JScrollPane spnPlayers = new JScrollPane(lPlayers);
		JPanel pnRight = new JPanel();
		JLabel lbLoggedIn = new JLabel("Logged In As: " + manager.getUsername());
		btReady = new JButton("Ready");

		spnPlayers.setPreferredSize(new Dimension(250, 150));

		pnRight.setLayout(new BorderLayout(5, 5));
		pnRight.add(lbLoggedIn, BorderLayout.PAGE_START);
		btReady.addActionListener(this);
		pnRight.add(btReady, BorderLayout.CENTER);

		Container pane = getContentPane();
		pane.setLayout(new BorderLayout(10, 10));
		pane.add(spnPlayers, BorderLayout.CENTER);
		pane.add(pnRight, BorderLayout.LINE_END);

		setTitle("Wait for other players");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setSize(500, getHeight());
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		manager.onReadyChange(!manager.isReady());
	}

	/**
	 * updateReady method: update users' status
	 * 
	 * @param usernames
	 * @param readys
	 */
	public void updateReady(String[] usernames, boolean[] readys) {
		players.clear();
		for (int i = 0; i < readys.length; i++) {
			players.addElement(new WaitUI_Player(usernames[i], readys[i]));
		}

		if (manager.isReady()) {
			btReady.setText("Unready");
			btReady.setIcon(new ImageIcon(SpriteManager
					.getSprite("unready.png")));
		} else {
			btReady.setText("Ready");
			btReady
					.setIcon(new ImageIcon(SpriteManager.getSprite("ready.png")));
		}
	}
}

class WaitUI_Player {
	private String username;
	private boolean ready;

	public WaitUI_Player(String username, boolean ready) {
		this.username = username;
		this.ready = ready;
	}

	public String getUsername() {
		return new String(username);
	}

	public boolean isReady() {
		return ready;
	}

	@Override
	public String toString() {
		return getUsername() + " (" + (ready ? "Ready" : "Not Ready") + ")";
	}
}

class WaitUI_PlayerRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 6107692177187617514L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof WaitUI_Player) {
			WaitUI_Player p = (WaitUI_Player) value;
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setText(p.toString());
			setForeground(p.isReady() ? Color.BLACK : Color.WHITE);
			setBackground(p.isReady() ? Color.GREEN : Color.RED);
		} else {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		return this;
	}
}