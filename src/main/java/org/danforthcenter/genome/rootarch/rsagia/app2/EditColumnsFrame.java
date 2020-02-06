/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditColumnsFrame.java
 *
 * Created on Jul 23, 2010, 3:35:25 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;

/**
 *
 * @author bm93
 */
public class EditColumnsFrame extends javax.swing.JFrame implements
		java.awt.event.ActionListener, java.awt.event.WindowListener {
	protected HashSet<String> hideSet;
	protected HashSet<String> showSet;
	protected RsaInputTable rit;
	protected File userFile;

	/** Creates new form EditColumnsFrame */
	public EditColumnsFrame(RsaInputTable rit, File userFile) {
		initComponents();

		this.setTitle("Edit Data Table Columns");
		this.rit = rit;
		this.userFile = userFile;

		DefaultListModel dlm = new DefaultListModel();
		showList.setModel(dlm);
		// DefaultListModel dlm = (DefaultListModel)showList.getModel();
		// dlm.removeAllElements();

		HashSet<String> savedCols = rit.getSavedColumns();
		showSet = new HashSet<String>();
		for (int i = rit.getSavedColumnCount(); i < rit.getColumnCount(); i++) {
			String s = rit.getColumnName(i);
			showSet.add(s);
			dlm.addElement(rit.getColumnName(i));
		}

		DefaultListModel dlm2 = new DefaultListModel();// (DefaultListModel)hideList.getModel();
		hideList.setModel(dlm2);
		// dlm2.removeAllElements();
		hideSet = new HashSet<String>();
		for (String s : rit.getAllPossibleColumns()) {
			if (!showSet.contains(s) && !savedCols.contains(s)) {
				hideSet.add(s);
				dlm2.addElement(s);
			}
		}

		toShowButton.addActionListener(this);
		toHideButton.addActionListener(this);
		upButton.addActionListener(this);
		downButton.addActionListener(this);

		this.addWindowListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toShowButton) {
			if (!hideList.isSelectionEmpty()) {
				DefaultListModel dlm = (DefaultListModel) hideList.getModel();
				DefaultListModel dlm2 = (DefaultListModel) showList.getModel();

				// // tw 2014nov13 getSelectedValues is deprecated
				// // update method and change array to list
				// Object[] vals = hideList.getSelectedValues();
				List<String> vals = new ArrayList<String>();
				vals = hideList.getSelectedValuesList();
				for (Object v : vals) {
					String s = (String) v;
					hideSet.remove(s);
					dlm.removeElement(v);

					showSet.add(s);
					dlm2.addElement(v);
				}
			}
		} else if (e.getSource() == toHideButton) {
			if (!showList.isSelectionEmpty()) {
				DefaultListModel dlm = (DefaultListModel) showList.getModel();
				DefaultListModel dlm2 = (DefaultListModel) hideList.getModel();

				// // tw 2014nov13 getSelectedValues is deprecated
				// // update method and change array to list
				// Object[] vals = showList.getSelectedValues();
				List<String> vals = new ArrayList<String>();
				vals = showList.getSelectedValuesList();

				for (Object v : vals) {
					String s = (String) v;
					showSet.remove(s);
					dlm.removeElement(v);

					hideSet.add(s);
					dlm2.addElement(v);
				}
			}
		} else if (e.getSource() == upButton) {
			if (!showList.isSelectionEmpty()) {
				int i = showList.getSelectedIndex();
				if (i > 0) {
					DefaultListModel dlm = (DefaultListModel) showList
							.getModel();
					Object o = dlm.elementAt(i - 1);
					dlm.remove(i - 1);
					dlm.add(i, o);
				}
			}
		} else if (e.getSource() == downButton) {
			if (!showList.isSelectionEmpty()) {
				int i = showList.getSelectedIndex();
				if (i < showList.getModel().getSize() - 2) {
					DefaultListModel dlm = (DefaultListModel) showList
							.getModel();
					Object o = dlm.elementAt(i);
					dlm.remove(i);
					dlm.add(i + 1, o);
					showList.setSelectedIndex(i + 1);
				}
			}
		}
	}

	public void windowOpened(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowIconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeiconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosing(WindowEvent e) {
		ArrayList<String> cols = new ArrayList<String>();
		for (int i = 0; i < showList.getModel().getSize(); i++) {
			cols.add((String) showList.getModel().getElementAt(i));
		}
		rit.setColumns(cols);
		saveColumns(cols);
		dispose();
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	protected void saveColumns(ArrayList<String> cols) {
		Properties props = new Properties();

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			try {
				br = new BufferedReader(new FileReader(userFile));
				props.load(br);
			} finally {
				if (br != null) {
					br.close();
				}
			}
		} catch (IOException e) {
			throw new EditColumnsFrameException(null, e);
		}
		try {
			try {
				bw = new BufferedWriter(new FileWriter(userFile));
				String s = cols.get(0);
				for (int i = 1; i < cols.size(); i++) {
					s += "," + cols.get(i);
				}
				props.setProperty("user_cols", s);
				props.store(bw, "");
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (IOException e) {
			throw new EditColumnsFrameException(null, e);
		}
	}

	public void windowDeactivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosed(WindowEvent e) {
		// dispose();
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowActivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		hideList = new javax.swing.JList();
		toShowButton = new javax.swing.JButton();
		toHideButton = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		showList = new javax.swing.JList();
		upButton = new javax.swing.JButton();
		downButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		hideList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(hideList);

		toShowButton.setText(">>");

		toHideButton.setText("<<");

		showList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane2.setViewportView(showList);

		upButton.setText("UP");

		downButton.setText("DOWN");

		jLabel1.setText("Hide Columns");

		jLabel2.setText("Show Columns");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jScrollPane1,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		133,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						toHideButton)
																				.addComponent(
																						toShowButton)))
												.addComponent(jLabel1))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jScrollPane2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		130,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				false)
																				.addComponent(
																						upButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						downButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)))
												.addComponent(jLabel2))));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(
														layout.createSequentialGroup()
																.addGap(6, 6, 6)
																.addComponent(
																		jLabel1)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jScrollPane1,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		269,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
																.addGroup(
																		layout.createSequentialGroup()
																				.addGap(95,
																						95,
																						95)
																				.addComponent(
																						toShowButton)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																				.addComponent(
																						toHideButton))
																.addGroup(
																		layout.createSequentialGroup()
																				.addGap(96,
																						96,
																						96)
																				.addComponent(
																						upButton)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																				.addComponent(
																						downButton))
																.addGroup(
																		javax.swing.GroupLayout.Alignment.TRAILING,
																		layout.createSequentialGroup()
																				.addGap(6,
																						6,
																						6)
																				.addComponent(
																						jLabel2)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(
																						jScrollPane2,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						269,
																						Short.MAX_VALUE))))
								.addContainerGap()));

		pack();
	}// </editor-fold>

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// new EditColumnsFrame(null).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JButton downButton;
	private javax.swing.JList hideList;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JList showList;
	private javax.swing.JButton toHideButton;
	private javax.swing.JButton toShowButton;
	private javax.swing.JButton upButton;

	// End of variables declaration

	protected static class EditColumnsFrameException extends RuntimeException {
		public EditColumnsFrameException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
