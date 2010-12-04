package com.tranvietson;

import javax.swing.JButton;

public class Card extends JButton {
	private static final long serialVersionUID = 5087893011852318911L;

	private int cardId;

	public Card() {
		super("Card");
	}

	public Card(int cardId) {
		this.cardId = cardId;
	}

	public void open(int pictureId) {
		setText("Card #" + pictureId);
	}

	public void flip() {
		setText("Card");
	}
}
