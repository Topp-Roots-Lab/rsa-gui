/*
 *  Copyright 2011 vp23.
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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2D;
import org.danforthcenter.genome.rootarch.rsagia2.InputOutputTypes;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.QualityControl;
import org.danforthcenter.genome.rootarch.rsagia2.Rootwork3D;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author vp23
 */
public class QualityControlFrameManager implements
		java.beans.PropertyChangeListener {
	protected GiaRoot2D giaRoot;
	protected Rootwork3D rootWork3d;
	protected ArrayList<RsaImageSet> inputs;
	protected ApplicationManager am;
	protected ChooseOutputFrame cofGia;
	protected SelectQcOutputFrame cofQc;
	protected SelectQc2OutputFrame cofQc2;
	protected SelectQc3OutputFrame cofQc3;
	protected QualityControl qc;
	protected QualityControl qc2;
	protected QualityControl qc3;
	protected File[] input;
	protected JDialog dialog;

	ArrayList<OutputInfo> scales;
	ArrayList<OutputInfo> gias;

	// ============================<editor-fold desc="Constructor">{{{
	public QualityControlFrameManager(QualityControl qc, QualityControl qc2,
			QualityControl qc3, GiaRoot2D giaRoot, Rootwork3D rootWork3d,
			ArrayList<RsaImageSet> inputs, ApplicationManager am) {
		this.qc = qc;
		this.qc2 = qc2;
		this.qc3 = qc3;
		this.giaRoot = giaRoot;
		this.rootWork3d = rootWork3d;
		this.inputs = inputs;
		this.am = am;
	}

	// End of Constructor...........................}}}</editor-fold>

	// ============================<editor-fold desc="run">{{{
	public void run() {
		doGias();
	}

	// End of run...........................}}}</editor-fold>

	// ============================<editor-fold desc="doGias">{{{
	protected void doGias() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();

        System.out.println(this.getClass() + " doGias");

		for (int i = 0; i < inputs.size(); i++) {
			// allow both "saved" and "sandbox"
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, true, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (qc3 == null) {
                    System.out.println(this.getClass() + " doGias qc3=null");
                    boolean oiVal = oi.isValid();
					if ( oiVal//oi.isValid()
							&& (oi.getOutputs() & InputOutputTypes.DESCRIPTORS_2D) > 0) {
						ss.add(oi);
                        System.out.println(this.getClass() + " doGias qc3=null oi isValid");
					}
				} else {
                    System.out.println(this.getClass() + " doGias qc3!=null");
					if (oi.isValid()
							&& (oi.getOutputs() & InputOutputTypes.VOLUME_3D) > 0) {
						ss.add(oi);
                        System.out.println(this.getClass() + " doGias qc3!=null oi isValid");
					}
				}
			}

			gias.add(null);
			multiGias.put(inputs.get(i), ss);
		}

        System.out.println(this.getClass() + " doGias multiGias " + multiGias.size());

		cofGia = new ChooseOutputFrame(multiGias, false, am, true, false);
		cofGia.setInfoText("Please select a single 2D descriptor set for the following:");
		cofGia.addPropertyChangeListener("done", this);
		cofGia.setVisible(true);
	}

	// End of doGias...........................}}}</editor-fold>

	// ============================<editor-fold desc="doRootwork3Ds">{{{
	protected void doRootwork3Ds() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();
		for (int i = 0; i < inputs.size(); i++) {
			// allow both "saved" and "sandbox"
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, true, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.DESCRIPTORS_2D) > 0) {
					ss.add(oi);
				}
			}

			gias.add(null);
			multiGias.put(inputs.get(i), ss);
		}

		cofGia = new ChooseOutputFrame(multiGias, false, am, true, false);
		cofGia.setInfoText("Please select a single 2D descriptor set for the following:");
		cofGia.addPropertyChangeListener("done", this);
		cofGia.setVisible(true);
	}

	// End of doRootwork3Ds...........................}}}</editor-fold>

	// ============================<editor-fold desc="doSelectQc">{{{
	protected void doSelectQc(ArrayList<OutputInfo> final_outputs) {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();

		ArrayList<File> buffer = new ArrayList<File>();

		input = new File[final_outputs.size()];

		for (int i = 0; i < final_outputs.size(); i++) {
			File giaroot_2d_set = final_outputs.get(i).getDir();
			input[i] = giaroot_2d_set;
		}

		cofQc = new SelectQcOutputFrame();
		cofQc.setInfoText("Please select options for Quality Control:");
		cofQc.addPropertyChangeListener("done", this);
		cofQc.setVisible(true);
	}

	// End of doSelectQc...........................}}}</editor-fold>

	// ============================<editor-fold desc="doSelectQc2">{{{
	protected void doSelectQc2(File[] input) {
        System.out.println(this.getClass() + " input " + input[0].toString());
		cofQc2 = new SelectQc2OutputFrame(input, am);
		cofQc2.addPropertyChangeListener("done", this);
	}

	// End of doSelectQc2...........................}}}</editor-fold>

	// ============================<editor-fold desc="doSelectQc3">{{{
	protected void doSelectQc3(File[] input) {
		cofQc3 = new SelectQc3OutputFrame(input, am);
		cofQc3.addPropertyChangeListener("done", this);
	}

	// End of doSelectQc3...........................}}}</editor-fold>

	// ============================<editor-fold desc="getInput">{{{
	protected void getInput(ArrayList<OutputInfo> final_outputs) {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();

		ArrayList<File> buffer = new ArrayList<File>();

		input = new File[final_outputs.size()];

		for (int i = 0; i < final_outputs.size(); i++) {
			File giaroot_2d_set = final_outputs.get(i).getDir();
			input[i] = giaroot_2d_set;
		}
	}

	// End of getInput...........................}}}</editor-fold>

	// ============================<editor-fold desc="getInput3">{{{
	protected void getInput3(ArrayList<OutputInfo> final_outputs) {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();

		ArrayList<File> buffer = new ArrayList<File>();

		input = new File[final_outputs.size()];

		for (int i = 0; i < final_outputs.size(); i++) {
			File giaroot_3d_set = final_outputs.get(i).getDir();
			input[i] = giaroot_3d_set;
		}
	}

	// End of getInput3...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc">{{{
	protected void doQc() {
		javax.swing.JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Choose base file name for saving Quality Control results");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int v = jfc.showDialog(null, "Continue");
		if (v == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			ArrayList<Integer> options = SelectQcOutputFrame.getOptions();
			int scaleQc = SelectQcOutputFrame.getScaleQc();
			// null indicates that this call is for the Qc
			qc.setGiaroot2dQcParams(null, input, f, scaleQc, options, am);
			// Schedule a job for the event-dispatching thread:
			// creating and showing this application's GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowProgressBarDialog(qc);
				}
			});

			// return to default settings
			SelectQcOutputFrame.resetOptions();
		}
	}

	// End of doQc...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc2">{{{
	protected void doQc2() {
		File f = null;
		ArrayList<Integer> options = new ArrayList<Integer>();
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(1));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));

		int scaleQc = SelectQc2OutputFrame.getScaleQc();
		// not null cofQc2 indicates that this call is for the Qc2
		qc2.setGiaroot2dQcParams(cofQc2, input, f, scaleQc, options, am);
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowProgressBarDialog(qc2);
			}
		});
	}

	// End of doQc2...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc3">{{{
	// TODO
	protected void doQc3() {
		File f = null;
		ArrayList<Integer> options = new ArrayList<Integer>();
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(1));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));
		options.add(Integer.valueOf(0));

		int scaleQc = SelectQc3OutputFrame.getScaleQc();
		// not null cofQc3 indicates that this call is for the Qc3
		qc3.setRootwork3dQcParams(cofQc3, input, f, scaleQc, options, am);
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowProgressBarDialog(qc3);
			}
		});
	}

	// End of doQc3...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc2SaveCsv">{{{
	protected void doQc2SaveCsv() {
		javax.swing.JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Choose base file name for saving Qc2 results");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int v = jfc.showDialog(null, "Continue");
		if (v == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			// remove a file extension
			if (f.getName().contains(".")) {
				String s = f.getAbsolutePath();
				f = new File(s.substring(0, s.lastIndexOf(".")));
			}
			File f2 = new File(f.getAbsolutePath() + ".csv");
			// save Csv to file
			SelectQc2OutputFrame.doQc2SaveCsv(f2);
			JOptionPane.showMessageDialog(null, "DONE!");
		}
	}

	// End of doQc2SaveCsv...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc3SaveCsv">{{{
	protected void doQc3SaveCsv() {
		javax.swing.JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Choose base file name for saving Qc3 results");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int v = jfc.showDialog(null, "Continue");
		if (v == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			// remove a file extension
			if (f.getName().contains(".")) {
				String s = f.getAbsolutePath();
				f = new File(s.substring(0, s.lastIndexOf(".")));
			}
			File f2 = new File(f.getAbsolutePath() + ".csv");
			// save Csv to file
			SelectQc3OutputFrame.doQc3SaveCsv(f2);
			JOptionPane.showMessageDialog(null, "DONE!");
		}
	}

	// End of doQc2SaveCsv...........................}}}</editor-fold>

	// ============================<editor-fold desc="propertyChange">{{{
	/**
	 * Implementation of PropertyChangeListener interface
	 *
	 * Invoked when task's done property changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == cofGia && evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofGia.getOutputs();
			HashMap<RsaImageSet, OutputInfo> map = new HashMap<RsaImageSet, OutputInfo>();
			for (OutputInfo oi : tmp) {
				map.put(oi.getRis(), oi);
			}
			for (int i = 0; i < gias.size(); i++) {
				if (gias.get(i) == null) {
					gias.remove(i);
					gias.add(i, map.get(inputs.get(i)));
				}
			}
			// Qc
			if (qc != null && qc2 == null && qc3 == null)
				doSelectQc(tmp);
			// Qc2
			if (qc == null && qc2 != null && qc3 == null) {
				getInput(tmp);
				doSelectQc2(input);
				doQc2();
			}
			if (qc == null && qc2 == null && qc3 != null) {
				getInput(tmp);
				doSelectQc3(input);
				doQc3();
			}
			cofGia.dispose();
			cofGia = null;
		} else if (evt.getSource() == cofQc
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			cofQc.dispose();
			cofQc = null;
			doQc();
		} else if (evt.getSource() == cofQc2
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			cofQc2.dispose();
			cofQc2 = null;
			doQc2SaveCsv();
		} else if (evt.getSource() == cofQc3
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			cofQc3.dispose();
			cofQc3 = null;
			doQc3SaveCsv();
		}
	}

	// End of propertyChange...........................}}}</editor-fold>

	// ======================<editor-fold
	// desc="createAndShowProgressBarDialog">{{{
	/**
	 * Create the GUI and show it.
	 *
	 * As with all GUI code, this must run on the event-dispatching thread.
	 */
	private void createAndShowProgressBarDialog(QualityControl qc) {

		// it is default
		// frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// for modal dialog - no need for now
		// in our case, it would be owner=MainFrame;
		// need some additional work for passing the reference
		// JFrame mainframe = ...
		// JDialog dialog = new JDialog(owner, true);

		// not a modal dialog
		// NOTE 1: One potential caveat here is that some operations
		// within GUI might confuse the application
		// (with unpredictable ramifications, harmless though)
		// For instance, moving giaroot_2d runs from 'sandbox' to 'saved'
		// or generating new giaroot_2d runs, etc.,
		// while the QC working,
		// because the QC processing might not find the files needed when
		// at the time when it needs it.
		//
		// NOTE 2: Cancel is not implemented. Clicking on the x button
		// would result aboritng the QC processing at the system will,
		// in reality it happens quickly; the files that has been
		// already generated will be left untoucned.
		//
		String title = "Quality Control processing ...";
		dialog = new JDialog();
		// use setPreferredSize, because setSize does not work as needed here
		// dialog.setSize(500, 50);
		dialog.setPreferredSize(new Dimension(300, 100));
		dialog.setTitle(title);

		// Create and set up the progBar wrapped in SwingWorker.
		JComponent progBar = qc.new ProgressBarGiaroot2dQc(dialog);
		progBar.setOpaque(true); // content panes must be opaque
		dialog.setContentPane(progBar);

		// Display the window.
		dialog.pack();
		dialog.setVisible(true);

		// does not work as it might be expected
		// dialog.setModal(true);
		// this makes a trick - the progress bar is always visible and on the
		// top
		dialog.setAlwaysOnTop(true);
	}
	// End of
	// createAndShowProgressBarDialog......................}}}</editor-fold>
}
