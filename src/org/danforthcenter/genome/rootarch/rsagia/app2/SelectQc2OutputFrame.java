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
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.FileUtil;
import org.danforthcenter.genome.rootarch.rsagia2.NameSubstringFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.RsaPipelineDirUtil;

/**
 *
 * @author vp23
 */
public class SelectQc2OutputFrame extends javax.swing.JFrame implements
		java.awt.event.ActionListener {
	protected ApplicationManager am;

	// ============================<editor-fold desc="constractor">{{{
	/** Creates new form SelectQc2OutputFrame */
	public SelectQc2OutputFrame(File[] input, ApplicationManager am) {
		this.am = am;
		this.input = input;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		super.setTitle("Qc2");
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
	private final String THRESHOLDED_COMPOSITE_FILTER = "_thresholded_composite.png";
	private final int NUM_ROWS_CSV = 17;
	private static final int SCALE_QC = 4;
	private final String TEMPLATE = "_template_";
	private final String SCALE = "_scale_";
	private final String QC2_FILE = "thresholding_qc2.csv";

	private JPanel jPanelMain;
	private JPanel jPanelN;
	private JPanel jPanelC;
	private JPanel jPanelS;
	private JPanel jPanelSchkboxes;
	private JPanel jPanelSrbts;
	private NoneSelectedButtonGroup bg_rbts;
	private JPanel jPanelSbuttons;
	private JPanel jPanelSlider;
	private JButton doneButton;
	private JButton nextButton;
	private JPanel holderPanel;
	private JPanel dashTopPanel;
	private JLabel jLabel1;
	private JButton showButton;

	private JCheckBox recropTopChk;
	private JCheckBox recropBottomChk;
	private JCheckBox recropSidesChk;

	private JCheckBox noiseRootsChk;
	private JCheckBox noiseSpotsChk;
	private JCheckBox noiseConeChk;
	private JCheckBox noiseOtherChk;

	private JRadioButton passedRdButton;
	private JRadioButton failedRdButton;
	private JRadioButton otherRdButton;

	private JSlider jSlider;
	private JLabel jSliderLabel;

	private ImageToolsPanel itp;
	private ManipImagePanel mip;

	private String specie;
	private String exp;
	private String plant;
	private String day;
	private String template;

	private File[] input;
	private int count;

	private static ArrayList<String[]> sheet;
	private RsaPipelineDirUtil dirUtil = new RsaPipelineDirUtil();

	// End of variables declaration...........................}}}</editor-fold>

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
					"To continue, select 'Passed', 'Failed', or 'Other'",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		updateSheetCsv();
		// create qc2 folder
		File qc2_csv_per_set = createQc2DirAndFile();
		// and write thresholding_qc2.csv
		doQc2SaveCsvPerSet(qc2_csv_per_set);

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
					THRESHOLDED_COMPOSITE_FILTER);
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

	// ============================<editor-fold desc="createQc2Dir">{{{
	private File createQc2DirAndFile() {

		// go two levels up from the current giaroot_2d folder
		File parent = new File(new File(input[count].getParent()).getParent());
		// initialize qc2 subdirectory
		File qc2_dir = new File(parent.getAbsolutePath() + File.separator
				+ "qc2");
		// initialize qc2_userstamp subdirectory
		File qc2_userstamp_dir = new File(qc2_dir.getAbsolutePath()
				+ File.separator + input[count].getName());
		// initialize thresholding qc2 csv file per set
		File qc2 = new File(qc2_userstamp_dir.getAbsolutePath()
				+ File.separator + QC2_FILE);
		// create the qc2 subdirectory
		if (!qc2_dir.exists()) {
			if (!qc2_dir.mkdir()) {
				throw new SelectQc2OutputFrameException(
						"Could not create directory: "
								+ qc2_dir.getAbsolutePath(), null);
			}
			// // tw 2014nov12
			// am.getIsm().setDirectoryPermissions(qc2_dir);
			am.getIsm().setPermissions(qc2_dir, false);
		}
		// create the qc2_userstamp subdirectory
		if (!qc2_userstamp_dir.exists()) {
			if (!qc2_userstamp_dir.mkdir()) {
				throw new SelectQc2OutputFrameException(
						"Could not create directory: "
								+ qc2_userstamp_dir.getAbsolutePath(), null);
			}
			// // tw 2014nov12
			// am.getIsm().setDirectoryPermissions(qc2_userstamp_dir);
			am.getIsm().setPermissions(qc2_userstamp_dir, false);
		}

		return qc2;
	}

	// End of createQc2Dir...........................}}}</editor-fold>
	// ============================<editor-fold desc="getTemplateName">{{{
	private String getTemplateName(File f) {

        System.out.println(this.getClass() + " getTemplateName " + f.toString());

		String templatename = "";
		File tmp = new File(input[count].getAbsolutePath() + File.separator
				+ "tmp");
		NameSubstringFileFilter nsff = new NameSubstringFileFilter(
				THRESHOLDED_COMPOSITE_FILTER);
		File[] files = tmp.listFiles(nsff);
		// there should be only one image after applying filter nsff
		if (files != null && files.length == 1) {
			// a sample:
			// fn=Is~cyl~a0.01-b0.01-h1-top0-topr0-x0y0z0-eps0.1-pi12~orthogonal_vp23_2012-12-11_15-03-18_template_insilico_test_scale_4_thresholded_composite
			String fn = files[0].getName();
			int ind = fn.indexOf(TEMPLATE);
			int ind2 = fn.indexOf(SCALE);
			templatename = fn.substring(ind + TEMPLATE.length(), ind2);
		}
		return templatename;
	}

	// End of getTemplateName...........................}}}</editor-fold>

	// ============================<editor-fold desc="initComponents">{{{
	private void initComponents() {

		// Paul's mosaic : w=2960 h=1945 (for scale=4 in Paul's script)
		int w = 2960;
		int h = 1945;

		// jLabel1 = new JLabel();
		doneButton = new JButton();
		showButton = new JButton();
		nextButton = new JButton();
		recropTopChk = new JCheckBox();
		recropBottomChk = new JCheckBox();
		recropSidesChk = new JCheckBox();
		noiseRootsChk = new JCheckBox();
		noiseSpotsChk = new JCheckBox();
		noiseConeChk = new JCheckBox();
		noiseOtherChk = new JCheckBox();

		passedRdButton = new JRadioButton();
		failedRdButton = new JRadioButton();
		otherRdButton = new JRadioButton();

		// jLabel1.setText("Estimate quality of thresholding");

		nextButton.setText("Next");
		doneButton.setText("Done");
		showButton.setText("Show Folder");

		recropTopChk.setText("Recrop Top");
		recropBottomChk.setText("Recrop Bottom");
		recropSidesChk.setText("Recrop Sides");
		noiseRootsChk.setText("Noise Roots");
		noiseSpotsChk.setText("Noise Spots");
		noiseConeChk.setText("Noise Cone");
		noiseOtherChk.setText("Noise Other");

		passedRdButton.setText("Passed");
		failedRdButton.setText("Failed");
		otherRdButton.setText("Other");

		// add checkboxes listeners
		recropTopChk.addActionListener(this);
		recropBottomChk.addActionListener(this);
		recropSidesChk.addActionListener(this);
		noiseRootsChk.addActionListener(this);
		noiseSpotsChk.addActionListener(this);
		noiseConeChk.addActionListener(this);
		noiseOtherChk.addActionListener(this);

		jPanelMain = new JPanel(new BorderLayout());
		jPanelN = new JPanel(new BorderLayout());
		dashTopPanel = new JPanel(new BorderLayout());
		itp = new ImageToolsPanel();
		// get the "Zoom" name away - no need, just consumes space
		itp.setTitle("");
		itp.setPreferredSize(new Dimension(140, 40));
		itp.getZoomComboBox().addActionListener(this);
		dashTopPanel.add(itp, BorderLayout.WEST);
		jPanelSchkboxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		jPanelSchkboxes.add(recropTopChk);
		jPanelSchkboxes.add(recropBottomChk);
		jPanelSchkboxes.add(recropSidesChk);
		jPanelSchkboxes.add(noiseRootsChk);
		jPanelSchkboxes.add(noiseSpotsChk);
		jPanelSchkboxes.add(noiseConeChk);
		jPanelSchkboxes.add(noiseOtherChk);
		jPanelSchkboxes.add(passedRdButton);
		// bg_rbts = new NoneSelectedButtonGroup();
		// bg_rbts.add(passedRdButton);
		// jPanelSchkboxes.add(passedRdButton);
		// bg_rbts.add(failedRdButton);
		// jPanelSchkboxes.add(failedRdButton);
		// bg_rbts.add(otherRdButton);
		// jPanelSchkboxes.add(otherRdButton);
		dashTopPanel.add(jPanelSchkboxes, BorderLayout.CENTER);
		jPanelSlider = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		jSliderLabel = new JLabel("<html>Bad images: "
				+ "<font color=RED>0&nbsp;</color></html>");
		jPanelSlider.add(jSliderLabel);
		jSlider = new JSlider(0, 40);
		jSlider.setValue(0);
		jSlider.setBorder(BorderFactory.createTitledBorder(""));
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		table.put(0, new JLabel("O"));
		table.put(10, new JLabel("10"));
		table.put(20, new JLabel("20"));
		table.put(30, new JLabel("30"));
		table.put(40, new JLabel("40"));
		jSlider.setLabelTable(table);
		jSlider.setPaintLabels(true);
		jSlider.setSnapToTicks(true);
		jSlider.setPaintTicks(true);
		jSlider.setMajorTickSpacing(10);
		jSlider.setMinorTickSpacing(1);
		jSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				JSlider d = (JSlider) arg0.getSource();
				if (!d.getValueIsAdjusting()) {
					String value = String.valueOf(d.getValue());
					// to avoid "flickering", pad with space
					if (value.length() == 1)
						value = value + "&nbsp;";
					jSliderLabel.setText("<html>Bad images: "
							+ "<font color=RED>" + value + "</color></html>");
					// System.out.println("the selection is: "+value);
				}
			}
		});
		jPanelSlider.add(jSlider);

		// radio buttons
		bg_rbts = new NoneSelectedButtonGroup();
		bg_rbts.add(passedRdButton);
		jPanelSlider.add(passedRdButton);
		bg_rbts.add(failedRdButton);
		jPanelSlider.add(failedRdButton);
		bg_rbts.add(otherRdButton);
		jPanelSlider.add(otherRdButton);

		// buttons
		jPanelSlider.add(doneButton);
		jPanelSlider.add(showButton);

		dashTopPanel.add(jPanelSlider, BorderLayout.EAST);

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

		jPanelS = new JPanel(new BorderLayout());
		jPanelMain.add(jPanelN, BorderLayout.NORTH);
		jPanelMain.add(holderPanel, BorderLayout.CENTER);
		// jPanelMain.add(jPanelS,BorderLayout.SOUTH);

		// this.getContentPane().setPreferredSize(new Dimension(1050,400));
		// int wborder = 10;
		// int hborder = 50;
		this.getContentPane().setPreferredSize(new Dimension(wimg, himg));
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
        System.out.println(this.getClass() + " loadAndShowImage " + tmp.toString());

		NameSubstringFileFilter nsff = new NameSubstringFileFilter(
				THRESHOLDED_COMPOSITE_FILTER);
		File[] files = tmp.listFiles(nsff);
		if (files == null || files.length != 1) {
			String Msg = "File not found:\n " + tmp + File.separator + "*"
					+ THRESHOLDED_COMPOSITE_FILTER;
			JOptionPane.showMessageDialog(this, Msg);
			throw new SelectQc2OutputFrameException(Msg, null);
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
			throw new SelectQc2OutputFrameException(Msg, null);
		}
		mip.setImage(image);
		// set default scale 50%
		double s = 0.5;
		mip.setScale(s, s);
		// set default scale 50% <--> indx=1
		// (Note: image size would be changed via actionPerformed)
		itp.getZoomComboBox().setSelectedIndex(1);
	}

	// End of loadAndShowImage...........................}}}</editor-fold>

	// ============================<editor-fold desc="reset">{{{
	private void reset() {
		// default selection ( leave Zoom as it is)
		//
		recropTopChk.setSelected(false);
		recropBottomChk.setSelected(false);
		recropSidesChk.setSelected(false);
		noiseRootsChk.setSelected(false);
		noiseSpotsChk.setSelected(false);
		noiseConeChk.setSelected(false);
		noiseOtherChk.setSelected(false);

		// does not work
		// passedRdButton.setSelected(false);
		// failedRdButton.setSelected(false);
		// otherRdButton.setSelected(false);
		//
		// need to extend ButtonGroup and use the following
		bg_rbts.setSelected(bg_rbts.getSelection(), false);

		jSlider.setValue(0);
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
		template = getTemplateName(input[count]);

		// set window title
		this.setTitle(specie + " : " + exp + " : " + plant + " : " + day
				+ " --- " + "template=" + template);
	}

	// End of setWindowTitle...........................}}}</editor-fold>

	// ============================<editor-fold desc="startQc2">{{{
	private void startQc2() {

        System.out.println(this.getClass() + " startQc2 " + input[0].toString());

		if (input == null || input.length == 0) {
			// in theory, it never happens, but an error message just in case
			throw new SelectQc2OutputFrameException("input is null or empty",
					null);
		}

		// now Qc2 files are generated in the tmp folder for every set
		// just in case set the pipeline file permisions, so everbody could
		// access the file
		// debug
		// setPermisionsForQc2File();

		count = 0;
		loadAndShowImage(input[count]);
		// set default scale 50%
		double s = 0.5;
		mip.setScale(s, s);
		// set default scale 50% <--> indx=1
		// (Note: image size would be changed via actionPerformed)
		itp.getZoomComboBox().setSelectedIndex(1);
	}

	// End of startQc2...........................}}}</editor-fold>

	// ==========================<editor-fold
	// desc="setPermisionsForQc2File()">{{{
	private void setPermisionsForQc2File() {
		for (File inp : input) {
			File tmp = new File(inp.getAbsolutePath() + File.separator + "tmp");
			NameSubstringFileFilter nsff = new NameSubstringFileFilter(
					THRESHOLDED_COMPOSITE_FILTER);
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
	// setPermisionsForQc2File...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc2SaveCsvPerSet()">{{{
	private void doQc2SaveCsvPerSet(File outFile) {
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
			throw new SelectQc2OutputFrameException("Error writing to file: "
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

	// End of doQc2SaveCsvPerSet()...........................}}}</editor-fold>

	// ============================<editor-fold desc="doStartQc2">{{{
	public void doStartQc2() {
		startQc2();
	}

	// End of doStartQc2...........................}}}</editor-fold>

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
		col_names[7] = "Recrop Top";
		col_names[8] = "Recrop Bottom";
		col_names[9] = "Recrop Sides";
		col_names[10] = "Noise Roots";
		col_names[11] = "Noise Spots";
		col_names[12] = "Noise Cone";
		col_names[13] = "Noise Other";
		col_names[14] = "Bad Images %";
		col_names[15] = "Folder Path";
		col_names[16] = "Template";

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
		row[7] = recropTopChk.isSelected() ? "1" : "0";
		row[8] = recropBottomChk.isSelected() ? "1" : "0";
		row[9] = recropSidesChk.isSelected() ? "1" : "0";
		row[10] = noiseRootsChk.isSelected() ? "1" : "0";
		row[11] = noiseSpotsChk.isSelected() ? "1" : "0";
		row[12] = noiseConeChk.isSelected() ? "1" : "0";
		row[13] = noiseOtherChk.isSelected() ? "1" : "0";
		double percentage = (double) jSlider.getValue()
				/ (double) jSlider.getMaximum();
		row[14] = String.valueOf((int) (percentage * 100));
		row[15] = input[count].getAbsolutePath();
		row[16] = getTemplateName(input[count]);

		sheet.add(row);
	}

	// End of updateSheetCsv...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc2SaveCsv()">{{{
	public static void doQc2SaveCsv(File outFile) {
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
			throw new SelectQc2OutputFrameException("Error writing to file: "
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

	// End of doQc2SaveCsv()...........................}}}</editor-fold>

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
	// desc="SelectQc2OutputFrameException()">{{{
	protected static class SelectQc2OutputFrameException extends
			RuntimeException {
		public SelectQc2OutputFrameException(Throwable th) {
			super(th);
		}

		public SelectQc2OutputFrameException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of
	// SelectQc2OutputFrameException()....................}}}</editor-fold>
}
