package com.tranvietson;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class PlayUI_PlayerRenderer extends DefaultListCellRenderer {
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
