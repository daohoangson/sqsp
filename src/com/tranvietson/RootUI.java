package com.tranvietson;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public abstract class RootUI extends JFrame {
	private static final long serialVersionUID = -3645463319300052042L;
	private static RootUI instance = null;

	public RootUI() {
		RootUI.instance = this;
		JFrame.setDefaultLookAndFeelDecorated(true);

		BufferedImage icon = null;
		try {
			File imageFile = new File("images\\FrameIcon.png");
			icon = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIconImage(icon);
	}

	public static void error(String message) {
		JOptionPane.showMessageDialog(RootUI.getInstance(), message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void info(String message) {
		JOptionPane.showMessageDialog(RootUI.getInstance(), message,
				"Information", JOptionPane.INFORMATION_MESSAGE);
	}

	public static Component getInstance() {
		return RootUI.instance;
	}
}
