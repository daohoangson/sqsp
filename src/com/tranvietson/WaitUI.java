package com.tranvietson;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
			btReady.setIcon(new ImageIcon("images\\unready.png"));
		} else {
			btReady.setText("Ready");
			btReady.setIcon(new ImageIcon("images\\ready.png"));
		}
	}
}