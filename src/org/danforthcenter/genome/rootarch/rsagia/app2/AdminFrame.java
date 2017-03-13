/*
 *  Copyright 2012 vp23.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author vp23
 */
public class AdminFrame extends JFrame implements ActionListener {
	static class AdminFrameException extends RuntimeException {

		public AdminFrameException(String msg) {
			super(msg);
		}

		public AdminFrameException(Throwable th) {
			super(th);
		}

		public AdminFrameException(String msg, Throwable th) {
			super(msg, th);
		}
	}

	class OpenUrlAction implements ActionListener {

		URI uri;
		final String wiki = "http://mk42ws.biology.duke.edu:8000/wiki/010-BenfeyLab/120-BioBusch/030-RootArch/120-AppScr/010-GiaGUI/020-Versions";

		public void actionPerformed(ActionEvent e) {
			setURI(wiki);
			AdminFrame.open(uri);
		}

		private void setURI(String value) {
			try {
				uri = new URI(value);
			} catch (Exception e) {
				throw new AdminFrameException(e);
			}
		}

		OpenUrlAction() {
			uri = null;
		}
	}

	public static class AdminSettings {

		static int MaxProcessesDefault = 4;
		static int MaxProcesses = Integer.MIN_VALUE;

		public static int getMaxProcessesDefault() {
			return MaxProcessesDefault;
		}

		public static int getMaxProcesses() {
			return MaxProcesses;
		}

		public static void setMaxProcesses(int value) {
			MaxProcesses = value;
		}

		public AdminSettings() {
		}
	}

	// ============================<editor-fold desc="Variables declaration">{{{
	private JPanel jPanelMain;
	private JPanel jPanelN;
	private JPanel jPanelGrid;
	private JPanel jPanelLabel1;
	private JLabel lbl_1;
	private JPanel jPanelLabel2;
	private JLabel lbl_2;
	private JPanel jPanelLabelgotoBenfeyLab;
	private JButton gotoBenfeyLab;
	private JPanel jPanelLabel3;
	private JLabel lbl_3;
	private JPanel jPanelS;
	private JPanel jPanelInputDef;
	private JLabel lbl_4;
	private JLabel lbl_5;
	private JPanel jPanelInput;
	private JLabel lbl_6;
	private JTextField maxProcessesField;
	private JPanel jPanelSbuttons;
	private JButton closeButton;

	// End of variables declaration...........................}}}</editor-fold>

	public AdminFrame() {
		initComponents();
		initiliazeMaxProcessesField();
		closeButton.addActionListener(this);
		gotoBenfeyLab.addActionListener(new OpenUrlAction());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton) {
			if (verify()) {
				String s = maxProcessesField.getText();
				AdminSettings.setMaxProcesses(Integer.parseInt(s));
				dispose();
			} else {
				String Msg = "Put a positive integer";
				JOptionPane.showMessageDialog(this, Msg);
			}
		}
		if (e.getSource() != gotoBenfeyLab)
			;
	}

	private void initiliazeMaxProcessesField() {
		int MaxProcessesDefault = AdminSettings.getMaxProcessesDefault();
		if (AdminSettings.getMaxProcesses() == Integer.MIN_VALUE) {
			AdminSettings.setMaxProcesses(MaxProcessesDefault);
			maxProcessesField.setText(String.valueOf(MaxProcessesDefault));
			maxProcessesField.requestFocus();
		}
	}

	private boolean verify() {
		String s = maxProcessesField.getText();
		return isParsableToInt(s) && Integer.parseInt(s) > 0;
	}

	private static boolean isParsableToInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException exp) {
			return false;
		}
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				throw new AdminFrameException(e);
			}
		} else {
			String Msg = (new StringBuilder())
					.append("Desktop.isDesktopSupported()=")
					.append(Desktop.isDesktopSupported()).toString();
			throw new AdminFrameException(Msg);
		}
	}

	private void initComponents() {
		setDefaultCloseOperation(2);
		setTitle("Admin");

		jPanelMain = new JPanel(new BorderLayout());
		jPanelN = new JPanel(new BorderLayout());
		jPanelGrid = new JPanel(new GridLayout(4, 0));
		jPanelLabel1 = new JPanel(new FlowLayout());
		lbl_1 = new JLabel();
		Dimension dim1 = new Dimension(530, 14);
		lbl_1.setText("Do not change the Max Processes setting unless absolutely necessary.");
		lbl_1.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_1.setPreferredSize(dim1);
		jPanelLabel1.add(lbl_1);
		jPanelGrid.add(jPanelLabel1);
		jPanelLabel2 = new JPanel(new FlowLayout());
		lbl_2 = new JLabel();
		Dimension dim2 = new Dimension(530, 14);
		lbl_2.setText("To learn more about this setting, read the section Version 3.0.7 at");
		lbl_2.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_2.setPreferredSize(dim2);
		jPanelLabel2.add(lbl_2);
		jPanelGrid.add(jPanelLabel2);
		jPanelLabelgotoBenfeyLab = new JPanel(new FlowLayout());
		gotoBenfeyLab = new JButton();
		Dimension dim3 = new Dimension(200, 14);
		gotoBenfeyLab.setBackground(new Color(237, 233, 227));
		gotoBenfeyLab.setForeground(Color.blue);
		gotoBenfeyLab
				.setText("<HTML><FONT color=\"#000099\"><U> Benfey Lab Wiki</U></FONT></HTML>");
		gotoBenfeyLab.setBorderPainted(false);
		gotoBenfeyLab.setHorizontalTextPosition(SwingConstants.CENTER);
		gotoBenfeyLab.setPreferredSize(dim3);
		jPanelLabelgotoBenfeyLab.add(gotoBenfeyLab);
		jPanelGrid.add(jPanelLabelgotoBenfeyLab);
		jPanelLabel3 = new JPanel(new FlowLayout());
		lbl_3 = new JLabel();
		Dimension dim4 = new Dimension(530, 14);
		lbl_3.setText("If changed, the new setting is kept only for the current session.");
		lbl_3.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_3.setPreferredSize(dim4);
		jPanelLabel3.add(lbl_3);
		jPanelGrid.add(jPanelLabel3);
		jPanelN.add(jPanelGrid, BorderLayout.CENTER);
		jPanelMain.add(jPanelN, BorderLayout.NORTH);

		jPanelS = new JPanel(new BorderLayout());
		jPanelInputDef = new JPanel(new FlowLayout());
		lbl_4 = new JLabel();
		lbl_4.setText("Max Processes Default:");
		lbl_4.setMaximumSize(new Dimension(155, 17));
		lbl_4.setMinimumSize(new Dimension(155, 17));
		lbl_4.setPreferredSize(new Dimension(180, 17));
		jPanelInputDef.add(lbl_4);
		lbl_5 = new JLabel();
		lbl_5.setText("4");
		jPanelInputDef.add(lbl_5);
		jPanelInput = new JPanel(new FlowLayout());
		lbl_6 = new JLabel();
		lbl_6.setText("Max Processes:");
		jPanelInput.add(lbl_6);
		maxProcessesField = new JTextField();
		maxProcessesField.setPreferredSize(new Dimension(30, 17));
		jPanelInput.add(maxProcessesField);
		jPanelS.add(jPanelInputDef, BorderLayout.NORTH);
		jPanelS.add(jPanelInput, BorderLayout.CENTER);
		jPanelMain.add(jPanelS, BorderLayout.CENTER);

		jPanelSbuttons = new JPanel(new FlowLayout());
		closeButton = new JButton();
		closeButton.setText("Close");
		jPanelSbuttons.add(closeButton);
		jPanelMain.add(jPanelSbuttons, BorderLayout.SOUTH);

		this.getContentPane().add(jPanelMain);

		pack();
	}

	private void gotoBenfeyLabActionPerformed(ActionEvent actionevent) {
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				(new AdminFrame()).setVisible(true);
			}

		});
	}

}
