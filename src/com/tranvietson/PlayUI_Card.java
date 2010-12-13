package com.tranvietson;

import images.SpriteManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

public class PlayUI_Card extends JButton {
	private static final long serialVersionUID = 5087893011852318911L;

	private int cardId;
	private int pictureId = -1;

	static int CFG_BORDER = 3;

	public PlayUI_Card(int cardId) {
		this.cardId = cardId;

		setMinimumSize(new Dimension(50, 50));
		setBackground(Color.LIGHT_GRAY);
	}

	public void open(int pictureId) {
		this.pictureId = pictureId;
		repaint();
	}

	public void close() {
		pictureId = -1;
		repaint();
	}

	public void destroy() {
		setEnabled(false);
		setBackground(Color.WHITE);
	}

	public int getCardId() {
		return cardId;
	}

	public boolean isOpened() {
		return isEnabled() && pictureId != -1;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (isEnabled()) {
			// not destroyed
			if (pictureId == -1) {
				// closed
			} else {
				// opened
				Image sprite = SpriteManager.getSprite(pictureId);
				if (sprite != null) {
					int w = getWidth();
					int h = getHeight();
					int s = Math
							.max(0, Math.min(w, h) - PlayUI_Card.CFG_BORDER);
					if (s > 0) {
						int dx1 = (w - s) / 2;
						int dy1 = (h - s) / 2;
						int dx2 = dx1 + s;
						int dy2 = dy1 + s;
						g.drawImage(sprite, dx1, dy1, dx2, dy2, 0, 0, sprite
								.getWidth(null), sprite.getHeight(null), null);
					}
				}
			}
		}
	}
}
