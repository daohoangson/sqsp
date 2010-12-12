package com.tranvietson;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.daohoangson.GameParamList;
import com.uonghuyquan.GameRoom;

public class PlayUI extends RootUI implements ActionListener {
	private static final long serialVersionUID = 6326821831738404168L;
	private UIManager manager;
	private int size;

	private JList lPlayers;
	private JTextField txtMessage;
	private JButton btnSend;
	private DefaultListModel players = new DefaultListModel();
	private DefaultListModel messages = new DefaultListModel();
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
		JList lChat = new JList(messages);
		lChat.setCellRenderer(new PlayUI_MessageRenderer());
		JScrollPane spnChat = new JScrollPane(lChat);
		spnChat.setPreferredSize(new Dimension(250, 250));
		spnChat.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable().setValue(
								e.getAdjustable().getMaximum());
					}
				});
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

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		Dimension leftSize = pnLeft.getSize();
		int leftWidthDelta = Math.min(leftSize.height - leftSize.width,
				screenSize.width - frameSize.width);
		if (leftWidthDelta > 0) {
			setSize(frameSize.width + leftWidthDelta, frameSize.height);
		}

		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o instanceof PlayUI_Card) {
			PlayUI_Card card = (PlayUI_Card) o;
			int cardId = card.getCardId();
			manager.onFlip(cardId);
			txtMessage.requestFocus();
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
			if (scores[i] < 0) {
				// invalid score
				continue;
			}
			players.addElement(new PlayUI_Player(lPlayers, usernames[i],
					scores[i], usernames[i].equals(manager.getUsername())));
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

	public void displayChat(GameParamList params) {
		messages.addElement(new PlayUI_Message(params));
	}
}

class PlayUI_Player implements Runnable {
	private Component container;
	private String username;
	private int score;
	private boolean isThisUser = false;
	private boolean inTurn = false;
	private int timer = 0;

	public PlayUI_Player(Component container, String username, int score,
			boolean isThisUser) {
		this.container = container;
		this.username = username;
		this.score = score;
		this.isThisUser = isThisUser;
	}

	public String getUsername() {
		return new String(username);
	}

	public void setTurn(boolean inTurn) {
		this.inTurn = inTurn;
		if (inTurn) {
			boolean newThread = true;
			if (timer > 0) {
				newThread = false;
			}
			timer = GameRoom.getTurnInterval();
			if (newThread) {
				new Thread(this).start();
			}
		} else {
			timer = 0;
		}
	}

	public boolean isInTurn() {
		return inTurn;
	}

	@Override
	public String toString() {
		return (isThisUser ? "[You] " + username : username) + ": " + score
				+ (timer > 0 ? " (remaining time: " + timer / 1000 + "s)" : "");
	}

	public void run() {
		timer = GameRoom.getTurnInterval();
		try {
			while (timer > 0 && inTurn) {
				timer -= 1000;
				Thread.sleep(1000);
				container.repaint();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer = 0;
	}
}

class PlayUI_PlayerRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 5155301481444727058L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof PlayUI_Player) {
			PlayUI_Player p = (PlayUI_Player) value;
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setText(p.toString());
			if (p.isInTurn()) {
				setForeground(Color.BLACK);
				setBackground(Color.YELLOW);
				setFont(getFont().deriveFont(Font.BOLD));
			} else {
				setFont(getFont().deriveFont(Font.PLAIN));
			}
		} else {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		return this;
	}
}

class PlayUI_Message {
	private String username;
	private String message;
	private boolean system = false;

	public PlayUI_Message(GameParamList params) {
		username = params.getParam("Username");
		message = params.getParam("Content");
		system = params.getParamAsInt("From-System") == 1;
	}

	public boolean isSystemMessage() {
		return system;
	}

	@Override
	public String toString() {
		if (system) {
			return new String(message);
		} else {
			return username + ": " + message;
		}
	}
}

class PlayUI_MessageRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 5155301481444727058L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof PlayUI_Message) {
			PlayUI_Message m = (PlayUI_Message) value;
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setText(m.toString());
			if (m.isSystemMessage()) {
				setForeground(Color.RED);
				setFont(getFont().deriveFont(Font.ITALIC));
			} else {
				setFont(getFont().deriveFont(Font.PLAIN));
			}
		} else {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		return this;
	}
}
