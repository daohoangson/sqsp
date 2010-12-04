package com.tranvietson;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class PlayUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 2452343912570106383L;

	private final UIManager manager;
	private final int size;
	private Card[] cards;

	public PlayUI(UIManager manager, int size) {
		this.manager = manager;
		this.size = size;

		cards = new Card[size];
		int gridColumn = (int) Math.ceil(Math.sqrt(size));
		setLayout(new GridLayout(0, gridColumn));
		for (int i = 0; i < size; i++) {
			Card card = new Card(i);
			card.setPreferredSize(new Dimension(100, 100));
			card.addActionListener(this);
			add(card);

			cards[i] = card;
		}

		pack();
		setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Card card = (Card) e.getSource();
		int cardId = card.getCardId();
		manager.onFlip(cardId);
	}

	public void flipCard(int cardId, int pictureId) {
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
}
