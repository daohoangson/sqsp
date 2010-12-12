package com.tranvietson;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 * @author Tran Viet Son
 */

public class WaitUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8589567281000101357L;

	private final UIManager manager;
	private final JButton btn1, btn2;
	private final JLabel lbl;
	private final JPanel panel;
	private final JList lst1, lst2;
	private final JScrollPane scrPn1, scrPn2;
	DefaultListModel mdl1 = new DefaultListModel();
	DefaultListModel mdl2 = new DefaultListModel();

	public WaitUI(UIManager manager, boolean host) {
		super("Waiting Room");
		this.manager = manager;

		// Set JList
		lst1 = new JList(mdl1);
		lst2 = new JList(mdl2);

		lst1.setForeground(Color.lightGray);

		// Set scrollPanel
		scrPn1 = new JScrollPane();
		scrPn1.getViewport().add(lst1);
		scrPn1.setBounds(40, 50, 140, 100);

		scrPn2 = new JScrollPane();
		scrPn2.getViewport().add(lst2);
		scrPn2.setBounds(180, 50, 70, 100);

		// Set label
		lbl = new JLabel();
		lbl.setText("PLAYERS IN GAME:");
		lbl.setFont(new Font("Rod", Font.BOLD, 15));
		lbl.setForeground(Color.black);
		lbl.setBounds(10, 10, 300, 30);

		// Set Button
		btn1 = new JButton();
		btn1.setBackground(Color.WHITE);
		btn1.setBounds(260, 80, 120, 50);
		btn1.setIcon(new ImageIcon("images\\ok.png"));
		if (host == true) {
			btn1.setText("START");
			btn1.setToolTipText("Let's Start Game");
		} else {
			btn1.setText("READY");
			btn1.setToolTipText("Click To Ready");
		}
		btn1.addActionListener(this);

		btn2 = new JButton();
		btn2.setText("Quit");
		btn2.setBackground(Color.WHITE);
		btn2.setIcon(new ImageIcon("images\\quit.png"));
		btn2.setBounds(20, 160, 100, 25);
		btn2.addActionListener(this);

		// Set Panel
		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(null);
		panel.add(scrPn1);
		panel.add(scrPn2);
		panel.add(btn1);
		panel.add(btn2);
		panel.add(lbl);
		panel.setBackground(Color.WHITE);

		// Set JFrame
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 230);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (d.width - w) / 2;
		int y = (d.height - h) / 2;
		this.setLocation(x, y);

		BufferedImage icon = null;
		try {
			File imageFile = new File("images\\card.jpg");
			icon = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIconImage(icon);

		setResizable(false);
		add(panel);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn2) {
			System.exit(0);
		} else {
			manager.onReadyChange(true);
			btn1.setEnabled(false);
		}
	}

	/**
	 * updateReady method: update users' status
	 * 
	 * @param username
	 * @param ready
	 */
	public void updateReady(String[] username, boolean[] ready) {
		String[] s1 = new String[username.length];
		String[] s2 = new String[username.length];

		mdl1.clear();
		mdl2.clear();

		for (int i = 0; i < ready.length; i++) {
			s1[i] = "  *     " + username[i];
			if (ready[i] == true) {
				s2[i] = "READY";
			} else {
				s2[i] = "NOT READY";
			}

			mdl1.addElement(s1[i]);
			mdl2.addElement(s2[i]);
		}
	}
}