package com.tranvietson;

import javax.swing.JButton;

public class Card extends JButton {
	private static final long serialVersionUID = 5087893011852318911L;

	private int cardId;

	public Card(int cardId) {
		super("?");
		this.cardId = cardId;
	}

	public void open(int pictureId) {
		setText("#" + pictureId);
	}

	public void close() {
		setText("?");
	}

	public int getCardId() {
		return cardId;
	}
}
