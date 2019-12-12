/*
 *  Copyright 2013 vp23.
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
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.FileUtil;
import org.danforthcenter.genome.rootarch.rsagia2.NameSubstringFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.RsaPipelineDirUtil;

/**
 *
 * @author vp23
 */
public class SelectQc3OutputFrame extends javax.swing.JFrame implements
		java.awt.event.ActionListener {
	protected ApplicationManager am;

	// ============================<editor-fold desc="constractor">{{{
	/** Creates new form SelectQc3OutputFrame */
	public SelectQc3OutputFrame(File[] input, ApplicationManager am) {
		this.am = am;
		this.input = input;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		super.setTitle("Qc3");
		initComponents();
		// default values
		reset();
		// buttons listeners
		nextButton.addActionListener(this);
		doneButton.addActionListener(this);
		showButton.addActionListener(this);
	}

	// End of constractor...........................}}}</editor-fold>

	// ============================<editor-fold desc="Variables declaration">{{{
	public static final String ANGLESTOP_MOSAIC_FILE = "anglestop_mosaic.png";

	private final int NUM_ROWS_CSV = 8;
	private static final int SCALE_QC = 1;
	private final String QC3_FILE = "anglestop_qc3.csv";
	private JPanel jPanelMain;
	private JPanel jPanelN;
	private JPanel jPanelS;
	private NoneSelectedButtonGroup bg_rbts;
	private JPanel jPanelBtns;
	private JButton doneButton;
	private JButton nextButton;
	private JPanel holderPanel;
	private JPanel dashTopPanel;
	private JLabel jLabel1;
	private JButton showButton;

	private JRadioButton passedRdButton;
	private JRadioButton failedRdButton;
	private JRadioButton otherRdButton;

	private ImageToolsPanel itp;
	private ManipImagePanel mip;

	private String specie;
	private String exp;
	private String plant;
	private String day;

	private File[] input;
	private int count;

	private static ArrayList<String[]> sheet;

	private RsaPipelineDirUtil dirUtil = new RsaPipelineDirUtil();

	// End of variables declaration......................}}}</editor-fold>

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showButton) {
			doShowFolder();
		} else if (e.getSource() == doneButton) {
			doDone();
		} else if (e.getSource() == itp.getZoomComboBox()) {
			mip.setScale(itp.getZoom(), itp.getZoom());
		}
	}

	protected void doShowFolder() {
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(input[count]);
		jfc.setPreferredSize(new Dimension(1200, 400));
		jfc.setDialogTitle(input[count].getAbsolutePath());
		jfc.setControlButtonsAreShown(false);
		int v = jfc.showDialog(null, "Continue");
	}

	protected void doDone() {
		if (!verifyRbts()) {
			JOptionPane.showMessageDialog(null,
					"To continue, select 'Passed','Failed', or 'Other'",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		updateSheetCsv();
		// create qc3 folder
		File qc3_csv_per_set = createQc3DirAndFile();
		// and write thresholding_qc3.csv
		doQc3SaveCsvPerSet(qc3_csv_per_set);

		if (count == input.length - 1) {
			//
			cleanUp();
			//
			firePropertyChange("done", false, true);
			//
			sheet.clear();
			initSheetCsv();
		}
		doNext();
	}

	protected void cleanUp() {
		for (File inp : input) {
			File tmp = new File(inp.getAbsolutePath() + File.separator + "tmp");
			NameSubstringFileFilter nsff = new NameSubstringFileFilter(
					ANGLESTOP_MOSAIC_FILE);
			File[] files = tmp.listFiles(nsff);
			// there should be only one image after applying filter nsff
			if (files != null && files.length == 1) {
				File file = files[0];
				FileUtil.deleteRecursively(file);
			}
			// delete tmp
			FileUtil.deleteRecursively(tmp);
		}
	}

	protected void doNext() {
		count++;
		if (count >= input.length)
			return;

		File file = input[count];
		loadAndShowImage(file);
		setWindowTitle(file);
		reset();
	}

	// ============================<editor-fold desc="getProcessedImages">{{{
	private File getProcessedImages() {

		return MainFrame.getProcessedImages();
	}

	// End of getProcessedImages...........................}}}</editor-fold>

	// ============================<editor-fold desc="createQc3Dir">{{{
	private File createQc3DirAndFile() {

		// go two levels up from the current giaroot_2d folder
		File parent = new File(new File(input[count].getParent()).getParent());
		// initialize qc3 subdirectory
		File qc3_dir = new File(parent.getAbsolutePath() + File.separator
				+ "qc3");
		// initialize qc3_userstamp subdirectory
		File qc3_userstamp_dir = new File(qc3_dir.getAbsolutePath()
				+ File.separator + input[count].getName());
		// initialize thresholding qc3 csv file per set
		File qc3 = new File(qc3_userstamp_dir.getAbsolutePath()
				+ File.separator + QC3_FILE);
		// create the qc3 subdirectory
		if (!qc3_dir.exists()) {
			if (!qc3_dir.mkdir()) {
				throw new SelectQc3OutputFrameException(
						"Could not create directory: "
								+ qc3_dir.getAbsolutePath(), null);
			}

			// // tw 2014nov12
			// am.getIsm().setDirectoryPermissions(qc3_dir);
			am.getIsm().setPermissions(qc3_dir, false);
		}
		// create the qc3_userstamp subdirectory
		if (!qc3_userstamp_dir.exists()) {
			if (!qc3_userstamp_dir.mkdir()) {
				throw new SelectQc3OutputFrameException(
						"Could not create directory: "
								+ qc3_userstamp_dir.getAbsolutePath(), null);
			}
			// // tw 2014nov12
			// am.getIsm().setDirectoryPermissions(qc3_userstamp_dir);
			am.getIsm().setPermissions(qc3_userstamp_dir, false);
		}

		return qc3;
	}

	// End of createQc3Dir...........................}}}</editor-fold>

	// ============================<editor-fold desc="initComponents">{{{
	private void initComponents() {

		// Vladimir's mosaic : w=174 (2x87) h=292 (2x146) for scale=1 in Paul's
		// script
		int w = 174;
		int h = 292;

		// jLabel1 = new JLabel();
		doneButton = new JButton();
		showButton = new JButton();
		nextButton = new JButton();

		passedRdButton = new JRadioButton();
		failedRdButton = new JRadioButton();
		otherRdButton = new JRadioButton();

		// jLabel1.setText("Estimate quality of thresholding");

		nextButton.setText("Next");
		doneButton.setText("Done");
		showButton.setText("Show Folder");
		passedRdButton.setText("Passed");
		failedRdButton.setText("Failed");
		otherRdButton.setText("Other");
		jPanelMain = new JPanel(new BorderLayout());
		jPanelN = new JPanel(new BorderLayout());
		dashTopPanel = new JPanel(new BorderLayout());
		itp = new ImageToolsPanel();
		// get the "Zoom" name away - no need, just consumes space
		itp.setTitle("");
		itp.setPreferredSize(new Dimension(140, 40));
		itp.getZoomComboBox().addActionListener(this);
		dashTopPanel.add(itp, BorderLayout.WEST);
		jPanelBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		// radio buttons
		bg_rbts = new NoneSelectedButtonGroup();
		bg_rbts.add(passedRdButton);
		jPanelBtns.add(passedRdButton);
		bg_rbts.add(failedRdButton);
		jPanelBtns.add(failedRdButton);
		bg_rbts.add(otherRdButton);
		jPanelBtns.add(otherRdButton);

		// buttons
		jPanelBtns.add(doneButton);
		jPanelBtns.add(showButton);

		dashTopPanel.add(jPanelBtns, BorderLayout.EAST);

		jPanelN.add(dashTopPanel, BorderLayout.NORTH);
		// jPanelN.add(jLabel1,BorderLayout.NORTH);

		holderPanel = new JPanel(new BorderLayout());
		mip = new ManipImagePanel();
		// int wimg = w/2+10;
		// int himg = h/2+125;
		int wimg = w / 2 + 210;
		int himg = h / 2 + 200;

		mip.setPreferredSize(new Dimension(wimg, himg));
		mip.setPreferredScrollableViewportSize(new Dimension(wimg, himg));
		holderPanel.add(new JScrollPane(mip), BorderLayout.CENTER);

		// jPanelS = new JPanel(new BorderLayout());
		jPanelMain.add(jPanelN, BorderLayout.NORTH);
		jPanelMain.add(holderPanel, BorderLayout.CENTER);
		// jPanelMain.add(jPanelS,BorderLayout.SOUTH);

		// this.getContentPane().setPreferredSize(new Dimension(1050,400));
		// int wborder = 10;
		// int hborder = 50;
		this.getContentPane().setPreferredSize(new Dimension(wimg + 300, himg));
		this.getContentPane().add(jPanelMain);
		pack();
	}

	// End of initComponents...........................}}}</editor-fold>

	// ============================<editor-fold desc="loadAndShowImage">{{{
	private void loadAndShowImage(File imgdir) {
		// load image after constructing the whole frame
		// (otherwise Graphics2D g2d = (Graphics2D)getGraphics(); returns null)
		// String path =
		// "/localhome/vp23/tmp/Is~cyl~a0.01-b0.01-h1-top0-topr0-x0y0z0-eps0.1-pi12~orthogonal_vp23_2012-12-11_15-03-18_template_insilico_test_scale_4_thresholded_composite.png";

		File tmp = new File(imgdir.getAbsolutePath() + File.separator + "tmp");
		NameSubstringFileFilter nsff = new NameSubstringFileFilter(
				ANGLESTOP_MOSAIC_FILE);
		File[] files = tmp.listFiles(nsff);
		if (files == null || files.length != 1) {
			String Msg = "Mosaic not found:\n " + tmp + File.separator
					+ ANGLESTOP_MOSAIC_FILE;
			JOptionPane.showMessageDialog(this, Msg);
			throw new SelectQc3OutputFrameException(Msg, null);
		}

		BufferedImage image = null;
		// there should be only one image after applying filter nsff
		File file = files[0];
		// set window title
		setWindowTitle(file);
		// load the first image
		try {
			image = ImageIO.read(file);
		} catch (IOException exp) {
			String Msg = "Image:" + file.getAbsolutePath()
					+ " could not be loaded.";
			JOptionPane.showMessageDialog(this, Msg);
			throw new SelectQc3OutputFrameException(Msg, null);
		}
		mip.setImage(image);
		// set default scale 100% <--> indx=3
		// (Note: image size would be changed via actionPerformed)
		itp.getZoomComboBox().setSelectedIndex(3);
	}

	// End of loadAndShowImage...........................}}}</editor-fold>

	// ============================<editor-fold desc="reset">{{{
	private void reset() {
		// default selection ( leave Zoom as it is)
		//
		// does not work
		// passedRdButton.setSelected(false);
		// failedRdButton.setSelected(false);
		// otherRdButton.setSelected(false);
		//
		// need to extend ButtonGroup and use the following
		bg_rbts.setSelected(bg_rbts.getSelection(), false);

		// jSlider.setValue(0);
	}

	// End of reset...........................}}}</editor-fold>

	// ============================<editor-fold desc="verifyRbts">{{{
	private boolean verifyRbts() {

		boolean passed = passedRdButton.isSelected()
				|| failedRdButton.isSelected() || otherRdButton.isSelected();

		return passed;
	}

	// End of verifyRbts...........................}}}</editor-fold>

	// ============================<editor-fold desc="setWindowTitle">{{{
	private void setWindowTitle(File file) {
		specie = dirUtil.getSpecies(file);
		exp = dirUtil.getExp(file);
		plant = dirUtil.getPlant(file);
		day = dirUtil.getDay(file);
		// template = getTemplateName(input[count]);

		// set window title
		this.setTitle(specie + " : " + exp + " : " + plant + " : " + day);
	}

	// End of setWindowTitle...........................}}}</editor-fold>

	// ============================<editor-fold desc="startQc3">{{{
	private void startQc3() {
		if (input == null || input.length == 0) {
			// in theory, it never happens, but an error message just in case
			throw new SelectQc3OutputFrameException("input is null or empty",
					null);
		}

		// now Qc3 files are generated in the tmp folder for every set
		// just in case set the pipeline file permisions, so everbody could
		// access the file
		setPermisionsForQc3File();

		count = 0;
		loadAndShowImage(input[count]);

		// set default scale 100% <--> indx=3
		// (Note: image size would be changed via actionPerformed)
		itp.getZoomComboBox().setSelectedIndex(3);
	}

	// End of startQc3...........................}}}</editor-fold>

	// ==========================<editor-fold
	// desc="setPermisionsForQc3File()">{{{
	private void setPermisionsForQc3File() {
		for (File inp : input) {
			File tmp = new File(inp.getAbsolutePath() + File.separator + "tmp");
			NameSubstringFileFilter nsff = new NameSubstringFileFilter(
					ANGLESTOP_MOSAIC_FILE);
			File[] files = tmp.listFiles(nsff);
			// there should be only one image after applying filter nsff
			if (files != null && files.length == 1) {
				File file = files[0];

				// // tw 2014nov12
				// am.getIsm().setFilePermissions(file);
				am.getIsm().setPermissions(file, false);
			}
		}
	}

	// End of
	// setPermisionsForQc3File...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc3SaveCsvPerSet()">{{{
	private void doQc3SaveCsvPerSet(File outFile) {
		BufferedWriter bw = null;
		try {
			// no need to delete the existing file - it would be rewritten if
			// not open
			// if(outFile.exists())FileUtil.deleteRecursively(outFile);

			bw = new BufferedWriter(new FileWriter(outFile));
			// get header
			String[] row = sheet.get(0);
			// write header
			bw.write(row[0]);
			for (int j = 1; j < row.length; j++) {
				bw.write("," + row[j]);
			}
			bw.write("\n");
			// write data
			// at this point, sheet has count+1 elements - this is the last
			String[] row2 = sheet.get(count + 1);
			// write header
			bw.write(row2[0]);
			for (int j = 1; j < row2.length; j++) {
				bw.write("," + row2[j]);
			}
			bw.write("\n");
		} catch (IOException e) {
			throw new SelectQc3OutputFrameException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}

		// // tw 2014nov12
		// am.getIsm().setFilePermissions(outFile);
		am.getIsm().setPermissions(outFile, false);
	}

	// End of doQc3SaveCsvPerSet()...........................}}}</editor-fold>

	// ============================<editor-fold desc="doStartQc3">{{{
	public void doStartQc3() {
		startQc3();
	}

	// End of doStartQc3...........................}}}</editor-fold>

	// ============================<editor-fold desc="getScaleQc">{{{
	public static int getScaleQc() {

		return SCALE_QC;
	}

	// End of getScaleQc...........................}}}</editor-fold>

	// ============================<editor-fold desc="initSheetCsv">{{{
	public void initSheetCsv() {

		sheet = new ArrayList<String[]>();

		String[] col_names = new String[NUM_ROWS_CSV];
		col_names[0] = "Species";
		col_names[1] = "Experiment";
		col_names[2] = "Plant";
		col_names[3] = "ImagingDay";
		col_names[4] = "Passed";
		col_names[5] = "Failed";
		col_names[6] = "Other";
		col_names[7] = "Folder Path";

		sheet.add(col_names);
	}

	// End of initSheetCsv...........................}}}</editor-fold>

	// ============================<editor-fold desc="updateSheetCsv">{{{
	public void updateSheetCsv() {

		if (sheet == null)
			initSheetCsv();

		String[] row = new String[NUM_ROWS_CSV];
		row[0] = dirUtil.getSpecies(input[count]);
		row[1] = dirUtil.getExp(input[count]);
		row[2] = dirUtil.getPlant(input[count]);
		row[3] = dirUtil.getDay(input[count]);
		row[4] = passedRdButton.isSelected() ? "1" : "0";
		row[5] = failedRdButton.isSelected() ? "1" : "0";
		row[6] = otherRdButton.isSelected() ? "1" : "0";
		row[7] = input[count].getAbsolutePath();

		sheet.add(row);
	}

	// End of updateSheetCsv...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc3SaveCsv()">{{{
	public static void doQc3SaveCsv(File outFile) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new SelectQc3OutputFrameException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}

	// End of doQc3SaveCsv()...........................}}}</editor-fold>

	// ============================<editor-fold
	// desc="NoneSelectedButtonGroup()">{{{
	public class NoneSelectedButtonGroup extends ButtonGroup {

		@Override
		public void setSelected(ButtonModel model, boolean selected) {
			if (selected) {
				super.setSelected(model, selected);
			} else {
				clearSelection();
			}
		}
	}

	// End of
	// NoneSelectedButtonGroup()...........................}}}</editor-fold>

	// ====================<editor-fold
	// desc="SelectQc3OutputFrameException()">{{{
	protected static class SelectQc3OutputFrameException extends
			RuntimeException {
		public SelectQc3OutputFrameException(Throwable th) {
			super(th);
		}

		public SelectQc3OutputFrameException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of
	// SelectQc3OutputFrameException()....................}}}</editor-fold>
}
