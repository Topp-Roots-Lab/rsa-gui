/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImageToolsPanel.java
 *
 * Created on May 10, 2010, 2:08:29 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import javax.swing.JComboBox;
import javax.swing.JToggleButton;

/**
 *
 * @author bm93
 */
public class ImageToolsPanel extends javax.swing.JPanel {

	/** Creates new form ImageToolsPanel */
	public ImageToolsPanel() {
		initComponents();

		lockTopButton.setVisible(false);
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

		zoomComboBox = new javax.swing.JComboBox();
		jLabel1 = new javax.swing.JLabel();
		lockTopButton = new javax.swing.JToggleButton();

		setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));

		zoomComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "25%", "50%", "75%", "100%", "125%", "150%",
						"175%", "200%" }));

		jLabel1.setText("Zoom:");

		// lockTopButton.setIcon(new
		// javax.swing.ImageIcon(getClass().getResource("/resources/dash-square.png")));
		// // NOI18N
		// tw 2014july9
		// lockTopButton.setIcon(new
		// javax.swing.ImageIcon(getClass().getResource("/dash-square.png")));
		// // NOI18N
		lockTopButton.setSelected(true);
		lockTopButton.setToolTipText("Lock the top of the rectangle");
		// lockTopButton.setSelectedIcon(new
		// javax.swing.ImageIcon(getClass().getResource("/top-square.png"))); //
		// NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														zoomComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														122,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel1)
												.addComponent(
														lockTopButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														34,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(55, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(5, 5, 5)
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(zoomComboBox,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lockTopButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										33,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(396, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JToggleButton lockTopButton;
	private javax.swing.JComboBox zoomComboBox;

	// End of variables declaration//GEN-END:variables

	public JToggleButton getLockTopButton() {
		return lockTopButton;
	}

	public JComboBox getZoomComboBox() {
		return zoomComboBox;
	}

	public void setTitle(String title) {
		jLabel1.setText(title);
	}

	public double getZoom() {
		return 0.01 * Double.parseDouble(zoomComboBox.getSelectedItem()
				.toString().replace("%", ""));
	}
}
