package com.tranvietson;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;

public class PlayUI extends javax.swing.JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final UIManager manager;
	private final int size;
	private final Card[] cards;

	private final javax.swing.JPanel gamePn;
	private final javax.swing.JPanel menuPn;
	private final javax.swing.JButton btnSkip;
	private final javax.swing.JLabel lbl1;
	private final javax.swing.JLabel lbl2;
	private final javax.swing.JScrollPane scrollPn1;
	private final javax.swing.JScrollPane scrollPn2;
	private final javax.swing.JTextArea chatBox;
	private final javax.swing.JList gameTurn;
	private final javax.swing.JTextField txt;
	private final javax.swing.JButton btnSend;
	DefaultListModel model = new DefaultListModel();

	public PlayUI(UIManager manager, int size) {
		this.manager = manager;
		this.size = size;

		gamePn = new javax.swing.JPanel();
		menuPn = new javax.swing.JPanel();
		scrollPn1 = new javax.swing.JScrollPane();
		scrollPn2 = new javax.swing.JScrollPane();
		chatBox = new javax.swing.JTextArea();
		btnSend = new javax.swing.JButton();
		btnSkip = new javax.swing.JButton();
		txt = new javax.swing.JTextField();
		lbl1 = new javax.swing.JLabel();
		lbl2 = new javax.swing.JLabel();
		gameTurn = new javax.swing.JList(model);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setFocusableWindowState(true);
		setTitle("Game Play");
		setMinimumSize(new java.awt.Dimension(980, 680));

		gamePn.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Game Play"));
		gamePn.setMaximumSize(new java.awt.Dimension(640, 640));
		gamePn.setMinimumSize(new java.awt.Dimension(640, 640));

		javax.swing.GroupLayout gamePnLayout = new javax.swing.GroupLayout(
				gamePn);
		gamePn.setLayout(gamePnLayout);
		gamePnLayout.setHorizontalGroup(gamePnLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 555,
				Short.MAX_VALUE));
		gamePnLayout.setVerticalGroup(gamePnLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 575,
				Short.MAX_VALUE));

		menuPn.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Game Board"));

		chatBox.setColumns(10);
		chatBox.setEditable(false);
		chatBox.setRows(5);
		scrollPn1.setViewportView(chatBox);

		btnSend.setText("Send");
		btnSend.setMaximumSize(new java.awt.Dimension(63, 23));
		btnSend.setMinimumSize(new java.awt.Dimension(63, 23));

		lbl2.setText("Chat Box");

		scrollPn2.getViewport().add(gameTurn);

		btnSkip.setText("Skip Your Turn");

		javax.swing.GroupLayout menuPnLayout = new javax.swing.GroupLayout(
				menuPn);
		menuPn.setLayout(menuPnLayout);
		menuPnLayout
				.setHorizontalGroup(menuPnLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								menuPnLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												menuPnLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																menuPnLayout
																		.createSequentialGroup()
																		.addComponent(
																				lbl2)
																		.addContainerGap(
																				259,
																				Short.MAX_VALUE))
														.addGroup(
																menuPnLayout
																		.createSequentialGroup()
																		.addGroup(
																				menuPnLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								btnSend,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								txt,
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								scrollPn1,
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								btnSkip,
																								javax.swing.GroupLayout.Alignment.LEADING,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.LEADING,
																								menuPnLayout
																										.createSequentialGroup()
																										.addComponent(
																												lbl1)
																										.addGroup(
																												menuPnLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														// .addComponent(
																														// jLabel1)
																														.addComponent(
																																scrollPn2,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																294,
																																javax.swing.GroupLayout.PREFERRED_SIZE))))
																		.addContainerGap(
																				18,
																				Short.MAX_VALUE)))));
		menuPnLayout
				.setVerticalGroup(menuPnLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								menuPnLayout
										.createSequentialGroup()
										// .addComponent(jLabel1)
										.addGroup(
												menuPnLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																menuPnLayout
																		.createSequentialGroup()
																		.addGap(27,
																				27,
																				27)
																		.addComponent(
																				lbl1))
														.addGroup(
																menuPnLayout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				scrollPn2,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				175,
																				Short.MAX_VALUE)))
										.addGap(18, 18, 18)
										.addComponent(btnSkip)
										.addGap(11, 11, 11)
										.addComponent(
												lbl2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												28,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												scrollPn1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												204,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(
												txt,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(
												btnSend,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(gamePn,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										567, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(menuPn,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(24, 24, 24)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												false)
												.addComponent(
														gamePn,
														javax.swing.GroupLayout.Alignment.LEADING,
														0, 602, Short.MAX_VALUE)
												.addComponent(
														menuPn,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();

		/**
		 * Draw cards in playboard
		 */
		cards = new Card[size];
		gamePn.setLayout(new GridLayout(20, 20, 1, 1));
		for (int i = 0; i < size; i++) {
			Card card = new Card(i);
			card.setMaximumSize(new Dimension(48, 48));
			card.setMinimumSize(new Dimension(48, 48));
			gamePn.add(card);

			cards[i] = card;

			card.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Card card = (Card) e.getSource();
		int cardId = card.getCardId();
		manager.onFlip(cardId);
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
		cards[cardId1].setEnabled(false);
		cards[cardId2].setEnabled(false);
	}

	/**
	 * updateGameTurn method: update players' turn, show in Game Board
	 * 
	 * @param username
	 * @param go
	 */
	public void updateGameTurn(String[] username, boolean[] go) {
		String[] s = new String[username.length];
		model.clear();
		for (int i = 0; i < go.length; i++) {
			if (go[i] == true) {
				s[i] = "  --    " + username[i] + "               is flipping";
			} else {
				s[i] = "  --    " + username[i];
			}
			model.addElement(s[i]);
		}
	}
}
