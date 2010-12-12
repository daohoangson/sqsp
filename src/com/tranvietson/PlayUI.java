package com.tranvietson;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class PlayUI extends RootUI implements ActionListener {
	private static final long serialVersionUID = 6326821831738404168L;
	private UIManager manager;
	private int size;

	private JList lPlayers;
	private JTextArea txtChat;
	private JTextField txtMessage;
	private JButton btnSend;
	private DefaultListModel players = new DefaultListModel();
	private PlayUI_Card[] cards;

	public PlayUI(UIManager manager, int size) {
		this.manager = manager;
		this.size = size;

		initComponents();
	}

	private void initComponents() {
		JPanel pnLeft = new JPanel();
		JPanel pnRight = new JPanel();
		JPanel pnPlayers = new JPanel();
		JPanel pnChat = new JPanel();
		lPlayers = new JList(players);
		lPlayers.setCellRenderer(new PlayUI_PlayerRenderer());
		JScrollPane spnPlayers = new JScrollPane(lPlayers);
		txtChat = new JTextArea();
		txtChat.setEditable(false);
		txtChat.setPreferredSize(new Dimension(250, 250));
		JScrollPane spnChat = new JScrollPane(txtChat);
		btnSend = new JButton("Send");
		btnSend.addActionListener(this);
		txtMessage = new JTextField();
		txtMessage.addActionListener(this);

		int sizeSqrt = (int) Math.ceil(Math.sqrt(size));
		pnLeft.setLayout(new GridLayout(sizeSqrt, sizeSqrt));
		cards = new PlayUI_Card[size];
		for (int i = 0; i < size; i++) {
			PlayUI_Card card = new PlayUI_Card(i);
			card.addActionListener(this);
			pnLeft.add(card);

			cards[i] = card;
		}
		pnLeft.setBorder(BorderFactory.createTitledBorder("Play"));

		pnPlayers.setLayout(new BorderLayout(10, 10));
		pnPlayers.add(spnPlayers, BorderLayout.CENTER);
		pnPlayers.setBorder(BorderFactory.createTitledBorder("Players"));

		pnChat.setLayout(new BorderLayout(10, 10));
		pnChat.add(spnChat, BorderLayout.CENTER);
		JPanel pnChatBottom = new JPanel(new BorderLayout(5, 5));
		pnChatBottom.add(txtMessage, BorderLayout.CENTER);
		pnChatBottom.add(btnSend, BorderLayout.LINE_END);
		pnChat.add(pnChatBottom, BorderLayout.PAGE_END);
		pnChat.setBorder(BorderFactory.createTitledBorder("Chat"));

		pnRight.setLayout(new GridLayout(2, 1));
		pnRight.add(pnPlayers);
		pnRight.add(pnChat);

		Container pane = getContentPane();
		pane.setLayout(new BorderLayout(10, 10));
		pane.add(pnLeft, BorderLayout.CENTER);
		pane.add(pnRight, BorderLayout.LINE_END);

		setTitle("Play");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o instanceof PlayUI_Card) {
			PlayUI_Card card = (PlayUI_Card) o;
			int cardId = card.getCardId();
			manager.onFlip(cardId);
		} else if (o == txtMessage || o == btnSend) {
			String message = txtMessage.getText();
			txtMessage.setText("");
			manager.onChat(message);
		}
	}

	public void flipCard(String username, int cardId, int pictureId) {
		cards[cardId].open(pictureId);
	}

	public void flipCards() {
		for (int i = 0; i < size; i++) {
			cards[i].close();
		}
	}

	public void destroyCards(int cardId1, int cardId2) {
		cards[cardId1].destroy();
		cards[cardId2].destroy();
	}

	public void updateScores(String[] usernames, int[] scores) {
		players.clear();
		for (int i = 0; i < usernames.length; i++) {
			players.addElement(new PlayUI_Player(usernames[i], scores[i],
					usernames[i].equals(manager.getUsername())));
		}
	}

	public void updateTurn(String username) {
		for (int i = 0; i < players.size(); i++) {
			Object o = players.get(i);
			if (o instanceof PlayUI_Player) {
				PlayUI_Player p = (PlayUI_Player) o;
				if (p.getUsername().equals(username)) {
					p.setTurn(true);
				} else {
					p.setTurn(false);
				}
			}
		}
		lPlayers.repaint();
	}

	public void displayChat(String username, String message) {
		txtChat.append(username + ": " + message + "\n");
	}
}
