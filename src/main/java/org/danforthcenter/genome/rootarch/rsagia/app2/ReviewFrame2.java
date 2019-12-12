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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.ReviewFrameDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.RsaImageSetDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author vp23
 *
 *         This class is now used instead of ReviewFrame (provides a better
 *         layout compared with ReviewFrame)
 *
 */
public final class ReviewFrame2 extends javax.swing.JDialog implements
		java.awt.event.ActionListener {
	protected ChooseOutputPanel2 cop;
	protected ApplicationManager am;
	protected ArrayList<RsaImageSet> riss;

	/** Creates new form ReviewFrame2 */
	public ReviewFrame2(ArrayList<RsaImageSet> riss, ApplicationManager am) {
		super(null, "Review Window", ModalityType.APPLICATION_MODAL);

		this.riss = riss;
		this.am = am;

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		initCop(riss, false, false);
		initComponents();

		// checkboxes listeners
		showScaleCheckbox.addActionListener(this);
		showCropCheckbox.addActionListener(this);
		showGiaroot_2dCheckbox.addActionListener(this);
		showGiaroot_3dCheckbox.addActionListener(this);
		showRootwork_3dCheckbox.addActionListener(this);
		showRootWork3dPersCheckbox.addActionListener(this);
		showGia3d_v2Checkbox.addActionListener(this);
		showRedCheckbox.addActionListener(this);

		// default selection
		showSavedCheckbox.setSelected(false);
		showScaleCheckbox.setSelected(true);
		showCropCheckbox.setSelected(true);
		showGiaroot_2dCheckbox.setSelected(true);
		showGiaroot_3dCheckbox.setSelected(true);
		showRootwork_3dCheckbox.setSelected(true);
		showRootWork3dPersCheckbox.setSelected(true);
		showGia3d_v2Checkbox.setSelected(true);
		showRedCheckbox.setSelected(false);

		// buttons listeners
		deleteButton.addActionListener(this);
		saveButton.addActionListener(this);
		// close button is better than cancel,
		// because the button did not cancel, for instance,
		// Save or Delete operations
		closeButton.addActionListener(this);
		showSavedCheckbox.addActionListener(this);
	}

	// ============================<editor-fold desc="Variables declaration">{{{
	private javax.swing.JPanel jPanelMain;
	private javax.swing.JPanel jPanelN;
	private javax.swing.JPanel jPanelC;
	private javax.swing.JPanel jPanelS;
	private javax.swing.JPanel jPanelSchkboxes;
	private javax.swing.JPanel jPanelSbuttons;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JPanel holderPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton saveButton;
	private javax.swing.JCheckBox showCropCheckbox;
	private javax.swing.JCheckBox showGiaroot_2dCheckbox;
	private javax.swing.JCheckBox showGiaroot_3dCheckbox;
	private javax.swing.JCheckBox showRedCheckbox;
	private javax.swing.JCheckBox showRootwork_3dCheckbox;
	private javax.swing.JCheckBox showRootWork3dPersCheckbox;
	private javax.swing.JCheckBox showGia3d_v2Checkbox;
	private javax.swing.JCheckBox showSavedCheckbox;
	private javax.swing.JCheckBox showScaleCheckbox;

	// End of variables declaration...........................}}}</editor-fold>

	protected void initCop(ArrayList<RsaImageSet> riss, boolean doSaved,
			boolean red) {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> inputs = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		ArrayList<String> filters = getFilters();
		for (RsaImageSet ris : riss) {
			ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris,
					doSaved, true, filters, red);
			inputs.put(ris, ois);
		}
		cop = new ChooseOutputPanel2(inputs, false, am, false);
		if (this.holderPanel != null)
			this.holderPanel.add(cop);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == deleteButton) {
			doDelete();
		} else if (e.getSource() == saveButton) {
			doSave();
		} else if (e.getSource() == closeButton) {
			firePropertyChange("done", false, true);
		}
		// else if (e.getSource() == showSavedCheckbox)
		else if (e.getSource().getClass().equals(javax.swing.JCheckBox.class)) {
			this.holderPanel.remove(cop);
			initCop(riss, showSavedCheckbox.isSelected(),
					showRedCheckbox.isSelected());
			this.pack();
		}
	}

	protected void doSave() {
		ArrayList<OutputInfo> ois = cop.getFinalOutputs();
		if (ois.size() == 0) {
			JOptionPane.showMessageDialog(this,
					"Please select at least one working output");
		} else {
			int v = JOptionPane.showConfirmDialog(this,
					"These files will be saved.  Are you sure?", "Save Files?",
					JOptionPane.YES_NO_OPTION);
			if (v == JOptionPane.YES_OPTION) {
				for (OutputInfo oi : ois) {
                    // tw 2015jan8 added try catch
                    try {
                        OutputInfo.moveToSaved(oi, am);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    int runID = oi.getRunID();
					ReviewFrameDBFunctions rfdbf = new ReviewFrameDBFunctions();
					rfdbf.changeToSavedinDB(runID);
					RsaImageSet ris = oi.getRis();
					ris.updateCountsOfApp(oi.getAppName());
				}
				cop.removeFinalOutputs();
			}
		}
	}

	protected void doDelete() {
		ArrayList<OutputInfo> ois = cop.getFinalOutputs();
		if (ois.size() == 0) {
			JOptionPane.showMessageDialog(this,
					"Please select at least one working output");
		} else {
			int v = JOptionPane.showConfirmDialog(this,
					"These files will be permanently deleted.  Are you sure?",
					"Delete Files?", JOptionPane.YES_NO_OPTION);
			if (v == JOptionPane.YES_OPTION) {
				for (OutputInfo oi : ois) {
					OutputInfo.delete(oi, am);
					int runID = oi.getRunID();
					ReviewFrameDBFunctions rfdbf = new ReviewFrameDBFunctions();
					rfdbf.deleteRun(runID);
					RsaImageSet ris = oi.getRis();
					ris.updateCountsOfApp(oi.getAppName());
				}
				cop.removeFinalOutputs();
			}
		}
	}

	// ============================<editor-fold desc="getFilters">{{{
	protected ArrayList<String> getFilters() {

		ArrayList<String> filters = new ArrayList<String>();
		if (showScaleCheckbox == null || showScaleCheckbox.isSelected())
			filters.add("scale");
		if (showCropCheckbox == null || showCropCheckbox.isSelected())
			filters.add("crop");
		if (showGiaroot_2dCheckbox == null
				|| showGiaroot_2dCheckbox.isSelected())
			filters.add("giaroot_2d");
		if (showGiaroot_3dCheckbox == null
				|| showGiaroot_3dCheckbox.isSelected())
			filters.add("giaroot_3d");
		if (showRootwork_3dCheckbox == null
				|| showRootwork_3dCheckbox.isSelected())
			filters.add("rootwork_3d");
		if (showRootWork3dPersCheckbox == null || showRootWork3dPersCheckbox.isSelected())
			filters.add("rootwork_3d_perspective");
		if (showGia3d_v2Checkbox == null || showGia3d_v2Checkbox.isSelected())
			filters.add("gia3d_v2");

		return filters;
	}

	// End of ImageFileFilter...........................}}}</editor-fold>

	// ============================<editor-fold desc="initComponents">{{{
	protected void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		closeButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		showSavedCheckbox = new javax.swing.JCheckBox();
		showScaleCheckbox = new javax.swing.JCheckBox();
		showCropCheckbox = new javax.swing.JCheckBox();
		showGiaroot_2dCheckbox = new javax.swing.JCheckBox();
		showGiaroot_3dCheckbox = new javax.swing.JCheckBox();
		showRootwork_3dCheckbox = new javax.swing.JCheckBox();
		showRootWork3dPersCheckbox = new javax.swing.JCheckBox();
		showGia3d_v2Checkbox = new javax.swing.JCheckBox();
		showRedCheckbox = new javax.swing.JCheckBox();

		jLabel1.setText("Move working outputs to the right and select the desired operation.");
		closeButton.setText("Close");
		saveButton.setText("Save...");
		deleteButton.setText("Delete...");
		showSavedCheckbox.setText("Also show saved");
		showScaleCheckbox.setText("scale");
		showCropCheckbox.setText("crop");
		showGiaroot_2dCheckbox.setText("giaroot_2d");
//		showGiaroot_3dCheckbox.setText("giaroot_3d");
		showRootwork_3dCheckbox.setText("rootwork_3d");
		showRootWork3dPersCheckbox.setText("rootwork_3d_perspective");
		showGia3d_v2Checkbox.setText("gia3d_v2");
		showRedCheckbox
				.setText("<html><font color=#8B0000>red</font> only</html>");

		jPanelMain = new JPanel(new BorderLayout());
		jPanelN = new JPanel(new BorderLayout());
		jPanelN.add(jLabel1, BorderLayout.NORTH);

		holderPanel = new JPanel(new BorderLayout());
		holderPanel.add(cop, BorderLayout.CENTER);

		jPanelS = new JPanel(new BorderLayout());
		jPanelSchkboxes = new JPanel(new FlowLayout());
		jPanelSchkboxes.add(showSavedCheckbox);
		jPanelSchkboxes.add(showScaleCheckbox);
		jPanelSchkboxes.add(showCropCheckbox);
		jPanelSchkboxes.add(showGiaroot_2dCheckbox);
//		jPanelSchkboxes.add(showGiaroot_3dCheckbox);
		jPanelSchkboxes.add(showRootwork_3dCheckbox);
		jPanelSchkboxes.add(showRootWork3dPersCheckbox);
		jPanelSchkboxes.add(showGia3d_v2Checkbox);
		jPanelSchkboxes.add(showRedCheckbox);
		jPanelSchkboxes.add(deleteButton);
		jPanelSchkboxes.add(saveButton);
		jPanelSchkboxes.add(closeButton);
		jPanelS.add(jPanelSchkboxes, BorderLayout.WEST);

		jPanelSbuttons = new JPanel(new FlowLayout());
		jPanelSbuttons.add(deleteButton);
		jPanelSbuttons.add(saveButton);
		jPanelSbuttons.add(closeButton);
		jPanelS.add(jPanelSbuttons, BorderLayout.EAST);

		jPanelMain.add(jPanelN, BorderLayout.NORTH);
		jPanelMain.add(holderPanel, BorderLayout.CENTER);
		jPanelMain.add(jPanelS, BorderLayout.SOUTH);

		this.getContentPane().setPreferredSize(new Dimension(1300, 400));
		this.getContentPane().add(jPanelMain);
		pack();
	}
	// End of initComponents...........................}}}</editor-fold>
}
