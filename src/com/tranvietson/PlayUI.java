package com.tranvietson;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class PlayUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 2452343912570106383L;

	private final UIManager manager;
	private final int size;

	public PlayUI(UIManager manager, int size) {
		this.manager = manager;
		this.size = size;

		int gridColumn = (int) Math.ceil(Math.sqrt(size));
		setLayout(new GridLayout(0, gridColumn));
		for (int i = 0; i < size; i++) {
			Card card = new Card(i);
			card.setPreferredSize(new Dimension(100, 100));
			// card.setActionCommand("Card" + i);
			card.addActionListener(this);
			add(card);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Card card = (Card) e.getSource();
		int cardId = card.getCardId();
		manager.onFlip(cardId);
	}

	public void flipCard(int cardId, int pictureId) {
	}

	public void flipCards() {
	}

	public void destroyCards(int cardId1, int cardId2) {

	}
}
