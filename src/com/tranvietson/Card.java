package com.tranvietson;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Card extends JButton {
	private static final long serialVersionUID = 5087893011852318911L;

	private final int cardId;

	public Card(int cardId) {
		super(new ImageIcon("images\\card.jpg"));
		this.cardId = cardId;
	}

	public void open(int pictureId) {
		switch (pictureId) {
		case 0:
			setIcon(new ImageIcon("images\\0.png"));
			break;
		case 1:
			setIcon(new ImageIcon("images\\1.png"));
			break;
		case 2:
			setIcon(new ImageIcon("images\\2.png"));
			break;
		case 3:
			setIcon(new ImageIcon("images\\3.png"));
			break;
		case 4:
			setIcon(new ImageIcon("images\\4.png"));
			break;
		case 5:
			setIcon(new ImageIcon("images\\5.png"));
			break;
		case 6:
			setIcon(new ImageIcon("images\\6.png"));
			break;
		case 7:
			setIcon(new ImageIcon("images\\7.png"));
			break;
		case 8:
			setIcon(new ImageIcon("images\\8.png"));
			break;
		case 9:
			setIcon(new ImageIcon("images\\9.png"));
			break;
		default:
			setText("?");
			break;
		}
	}

	public void close() {
		setIcon(new ImageIcon("images\\card.jpg"));
	}

	public int getCardId() {
		return cardId;
	}
}
