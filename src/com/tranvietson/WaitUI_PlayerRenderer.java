package com.tranvietson;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class WaitUI_PlayerRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 6107692177187617514L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof WaitUI_Player) {
			WaitUI_Player p = (WaitUI_Player) value;
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			setText(p.toString());
			setForeground(p.isReady() ? Color.BLACK : Color.WHITE);
			setBackground(p.isReady() ? Color.GREEN : Color.RED);
		} else {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		return this;
	}
}
