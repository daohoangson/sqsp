package com.tranvietson;

import javax.swing.JList;

/**
 * 
 * @author Tran Viet Son
 */
public class WaitUI extends javax.swing.JFrame {
	private static final long serialVersionUID = -692026364192635696L;

	private final javax.swing.JButton button;
	private final javax.swing.JPanel panel;
	private final javax.swing.JLabel l1;
	private final javax.swing.JLabel l2;

	private JList jl;

	// private final String[] a = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
	// "J" };

	/** Constructor of WaitUI */
	public WaitUI(boolean host) {
		panel = new javax.swing.JPanel();
		button = new javax.swing.JButton();
		l1 = new javax.swing.JLabel();
		l2 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		if (host == true) {
			button.setText("START");
			button.setToolTipText("Let's Start Game");
		} else {
			button.setText("READY");
			button.setToolTipText("Click to Ready");
		}

		l1.setText("Room No:");

		l2.setText("Players in game:");

		// jl = new JList();

		javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
		panel.setLayout(panelLayout);
		panelLayout
				.setHorizontalGroup(panelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelLayout
										.createSequentialGroup()
										.addGroup(
												panelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(l1)
														.addGroup(
																panelLayout
																		.createSequentialGroup()
																		.addGap(
																				26,
																				26,
																				26)
																		.addGroup(
																				panelLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelLayout
																										.createSequentialGroup()
																										.addComponent(
																												l2)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												187,
																												Short.MAX_VALUE))
																						.addGroup(
																								panelLayout
																										.createSequentialGroup()
																										.addComponent(
																												jl)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																												157,
																												Short.MAX_VALUE)
																										.addComponent(
																												button,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												75,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(
																												22,
																												22,
																												22)))))
										.addContainerGap()));
		panelLayout
				.setVerticalGroup(panelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelLayout
										.createSequentialGroup()
										.addComponent(l1)
										.addGap(39, 39, 39)
										.addComponent(l2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												panelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																button,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																73,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jl))
										.addContainerGap(42, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						panel, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		pack();
	}// </editor-fold>

	@SuppressWarnings("deprecation")
	public static void main(String arg[]) {
		// String[] u = { "fuck1", "fuck2" };
		// boolean[] r = { true, false };
		WaitUI w = new WaitUI(true);
		w.show();
		// w.updateReady(u, r);
		// w.show();
	}

	@SuppressWarnings("null")
	public void updateReady(String[] username, boolean[] ready) {
		String[] status = null;
		for (int i = 0; i < ready.length; i++) {
			if (ready[i] == true) {
				status[i] = "- " + username[i] + " (READY)";
			} else {
				status[i] = "- " + username[i] + " (NOT READY)";
			}
		}
		jl = new JList(status);

	}
}
