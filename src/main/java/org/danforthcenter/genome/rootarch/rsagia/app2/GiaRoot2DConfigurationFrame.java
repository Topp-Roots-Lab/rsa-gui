/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GiaRootConfigurationFrame.java
 *
 * Created on Jul 1, 2010, 12:40:14 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2D;
import org.danforthcenter.genome.rootarch.rsagia2.SimpleSecurityManager;

/**
 *
 * @author bm93
 */
public class GiaRoot2DConfigurationFrame extends javax.swing.JFrame implements
		java.awt.event.ActionListener {
	protected final static String SELECT_ONE = "-- SELECT ONE --";

	/** Creates new form GiaRootConfigurationFrame */
	public GiaRoot2DConfigurationFrame(GiaRoot2D giaRoot2D, String descriptors) {
		initComponents();
		this.setLocationRelativeTo(null);

		descriptorsTextField.setText(descriptors);
		configComboBox.removeAllItems();
		configComboBox.addItem(SELECT_ONE);
		ArrayList<String> ss = giaRoot2D.getSavedConfigs();
		for (String s : ss) {
			configComboBox.addItem(s);
		}
		configComboBox.setSelectedItem(SELECT_ONE);

		nextButton.addActionListener(this);
		cancelButton.addActionListener(this);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == nextButton) {
			if (configComboBox.getSelectedItem().equals(SELECT_ONE)) {
				JOptionPane.showMessageDialog(this,
						"Please select a configuration to use.");
			} else {
				firePropertyChange("done", false, true);
			}
		} else if (e.getSource() == cancelButton) {
			// TODO: clean up first if needed
			cancel();

			// close this window
			closeWindow();
		}
	}

	public String getDescriptors() {
		return descriptorsTextField.getText();
	}

	public String getGiaTemplate() {
		return (String) configComboBox.getSelectedItem();
	}

	private void cancel() {
		// TODO: clean up
	}

	private void closeWindow() {
		dispose();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		configComboBox = new javax.swing.JComboBox();
		nextButton = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		descriptorsTextField = new javax.swing.JTextField();
		cancelButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jLabel1.setText("Please choose one of the following configurations:");

		configComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		nextButton.setText("Next");

		jLabel2.setText("(Optional) Edit list of descriptors:");

		descriptorsTextField.setText("jTextField1");

		cancelButton.setText("Cancel");

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
																		jLabel1)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		configComboBox,
																		0,
																		251,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLabel2)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		descriptorsTextField,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		363,
																		Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		nextButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		85,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancelButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		85,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1)
												.addComponent(
														configComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										32, Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														descriptorsTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel2))
								.addGap(27, 27, 27)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(nextButton)
												.addComponent(cancelButton))
								.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				GiaRoot2D gia = new GiaRoot2D(new File(
						"./test-resources/templates"),
						"/scratch/gia/interpreter", new File("/scratch/gia"),
						"averagerootwidthfeaturevalue;bushinessfeaturevalue",
                        new SimpleSecurityManager("rootarch", "rootarch"));
				new GiaRoot2DConfigurationFrame(gia,
						"averagerootwidthfeaturevalue;bushinessfeaturevalue")
						.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JComboBox<String> configComboBox;
	private javax.swing.JTextField descriptorsTextField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JButton nextButton;
	// End of variables declaration//GEN-END:variables

}
