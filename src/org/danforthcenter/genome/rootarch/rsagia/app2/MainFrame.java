/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on Jul 1, 2010, 11:48:22 AM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.ConnectDb;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.FillDb;
import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.Crop;
import org.danforthcenter.genome.rootarch.rsagia2.Export;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2D;
import org.danforthcenter.genome.rootarch.rsagia2.IApplication;
import org.danforthcenter.genome.rootarch.rsagia2.ISecurityManager;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.danforthcenter.genome.rootarch.rsagia2.Scale;
import org.danforthcenter.genome.rootarch.rsagia2.SimpleSecurityManager;
import org.danforthcenter.genome.rootarch.rsagia2.StringPairFilter;

/**
 * 
 * @author bm93
 */
public class MainFrame extends javax.swing.JFrame implements
		java.awt.event.ActionListener, java.beans.PropertyChangeListener {

	protected RsaInputTable rsaTable;
	protected ArrayList<RsaImageSet> riss;
	protected ISecurityManager ism;
	protected ApplicationManager am;
	protected RisFilterFrame rff;
	protected AdminFrameNew admin;

	protected static File baseDir;
	protected File userFile;

	protected static final String PROCESSED_IMAGES = "processed_images";

	/** Creates new form MainFrame */
	public MainFrame(File baseDir, ISecurityManager ism, ApplicationManager am,
			ArrayList<StringPairFilter> speciesFilter,
			ArrayList<StringPairFilter> experimentFilter,
			ArrayList<StringPairFilter> plantFilter,
			ArrayList<StringPairFilter> imagingDayFilter,
			ArrayList<StringPairFilter> imagingDayPlant_Filter, File userFile,
			ArrayList<String> userCols) {
		initComponents();

        // System.out.println(this.getClass() + " " + baseDir);


		this.baseDir = baseDir;
		this.ism = ism;
		this.am = am;
		riss = RsaImageSet.getAll(baseDir, ism, speciesFilter,
				experimentFilter, plantFilter, imagingDayFilter,
				imagingDayPlant_Filter);
        // System.out.println(this.getClass() + " " + riss.size()
        //                   + " " + riss.get(0));

		///////////////////////////////
		ConnectDb cdb = new ConnectDb();
		FillDb cfdb = null;
		try {
			cfdb = new FillDb();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (cfdb != null) {
			//cfdb.fillTables1();
			//cfdb.convertTemplatestoJson();
			//cfdb.fillSavedConfigTableGia2d();
			//cfdb.getUser();
			//cfdb.fillSavedConfigTableGia3dv2();
			//cfdb.fillSavedConfigTable();
			//cfdb.fillProgramRunTable(riss,am);
		}
		////////////////////////////////////

		rsaTable = new RsaInputTable(am);
		rsaTable.setData(riss);
		ArrayList<String> cols = new ArrayList<String>();
		cols.add(RsaInputTable.SPECIES);
		cols.add(RsaInputTable.EXPERIMENT);
		cols.add(RsaInputTable.PLANT);
		cols.add(RsaInputTable.IMAGING_DAY);

		if (userCols.size() > 0) {
			rsaTable.setColumns(userCols);
		} else {
			rsaTable.setColumns(cols);
		}
		this.tableScrollPane.add(rsaTable);

		this.tableScrollPane.setViewportView(rsaTable);
		this.nextButton.addActionListener(this);
		this.editColsButton.addActionListener(this);
		this.filterButton.addActionListener(this);
		this.adminButton.addActionListener(this);

		rff = new RisFilterFrame(StringPairFilter.toString(speciesFilter),
				StringPairFilter.toString(experimentFilter),
				StringPairFilter.toString(plantFilter),
				StringPairFilter.toString(imagingDayFilter),
				StringPairFilter.toString(imagingDayPlant_Filter));
		rff.addPropertyChangeListener(this);
		this.userFile = userFile;
		this.admin = new AdminFrameNew();
		// this.runTree.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == nextButton) {
			ArrayList<RsaImageSet> inputSet = rsaTable.getSelectedData();

			if (inputSet.size() == 0) {
				JOptionPane.showMessageDialog(this,
						"Please select at least one image set.");
			} else {
				ArrayList<String> appNames = new ArrayList<String>();
				if (scaleToggle.isSelected()) {
					appNames.add(am.getScale().getName());
				}
				if (cropToggle.isSelected()) {
					appNames.add(am.getCrop().getName());
				}
				if (recropToggle.isSelected()) {
					appNames.add(am.getRecrop().getName());
				}
				if (gia2DToggle.isSelected()) {
					appNames.add(am.getGiaRoot2D().getName());
				}
				if (rootwork3DToggle.isSelected()) {
					// throw new
					// UnsupportedOperationException("Rootwork not implemented yet");
					// appNames.add(new Rootwork3D()).
					appNames.add(am.getRootwork3D().getName());
				}
                if (rootwork3DPersToggle.isSelected()) {
                    // throw new
                    // UnsupportedOperationException("Rootwork not implemented yet");
                    // appNames.add(new Rootwork3D()).
                    appNames.add(am.getRootwork3DPers().getName());
                }
				if (reviewToggle.isSelected()) {
					appNames.add("review");
				}
				if (exportToggle.isSelected()) {
					appNames.add("export");
				}
				if (gia3DToggle.isSelected()) {
					appNames.add(am.getGiaRoot3D().getName());
				}
				if (gia3D_v2Toggle.isSelected()) {
					boolean add = appNames.add(am.getGia3D_v2().getName());
				}
				if (qcToggle.isSelected()) {
					appNames.add("qc");
				}
				if (qc2Toggle.isSelected()) {
					appNames.add("qc2");
				}
				if (qc3Toggle.isSelected()) {
					appNames.add("qc3");
				}
				if (appNames.size() == 0) {
					JOptionPane
							.showMessageDialog(this,
									"Please specify at least one operation (e.g., Crop, Scale).");
				} else {
					// make sure that we aren't trying to run an operation on an
					// image set
					// that hasn't had the required prior processing
					// for instance, to run GiaRoots we need to make sure there
					// is a
					// saved crop

					/*
					 * RisPreprocessFrame rpf = new RisPreprocessFrame();
					 * rpf.setCount(1, inputSet.size()); rpf.setVisible(true);
					 * for (int i = 0; i < inputSet.size(); i++) {
					 * inputSet.get(i).preprocess(); rpf.setCount(i,
					 * inputSet.size()); } rpf.dispose();
					 */
					boolean canRun = true;
					for (String s : appNames) {
						MissingInputsFrame minf = new MissingInputsFrame(rsaTable, s, am);
						boolean cancel = minf.getCancel();

						if (cancel == true) {
							canRun = false;
						} else {
							inputSet = rsaTable.getSelectedData();
							if (inputSet.size() == 0) {
								JOptionPane.showMessageDialog(this,
										"Please select at least one image set.");
								canRun = false;
							}
						}

						if (!canRun) {
							break;
						}
					}

					if (canRun) {
						ApplicationFrameManager afm = new ApplicationFrameManager(
								this, am, appNames, inputSet);
						afm.addPropertyChangeListener("update", this);
						afm.run();
					}
				}
			}
		} else if (e.getSource() == this.editColsButton) {
			EditColumnsFrame ecf = new EditColumnsFrame(rsaTable, userFile);
			ecf.setVisible(true);
		} else if (e.getSource() == this.filterButton) {
			rff.setVisible(true);
		} else if (e.getSource() == this.adminButton) {
			admin.setVisible(true);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("update")) {
			rsaTable.updateRows(rsaTable.getCheckedRowIndexes());
		}

		if (evt.getSource() == rff && evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			rff.setVisible(false);
			riss = RsaImageSet.getAll(baseDir, ism, rff.getSpeciesFilter(),
					rff.getExperimentFilter(), rff.getPlantFilter(),
					rff.getImagingDayFilter(), rff.getImagingDay_PlantFilter());
			rsaTable.setData(riss);
			Properties props = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(userFile);
				props.load(fis);
			} catch (IOException e) {
				throw new MainFrameException("Could not load file:"
						+ userFile.getAbsolutePath(), e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {

					}
				}
			}

			props.setProperty("species_filter",
					StringPairFilter.toString(rff.getSpeciesFilter()));
			props.setProperty("experiment_filter",
					StringPairFilter.toString(rff.getExperimentFilter()));
			props.setProperty("plant_filter",
					StringPairFilter.toString(rff.getPlantFilter()));
			props.setProperty("imaging_day_filter",
					StringPairFilter.toString(rff.getImagingDayFilter()));
			props.setProperty("imaging_plant_day_filter",
					StringPairFilter.toString(rff.getImagingDay_PlantFilter()));

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(userFile);
				props.store(fos, null);
			} catch (IOException e) {
				throw new MainFrameException("Could not store to file: "
						+ userFile.getAbsolutePath(), e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {

					}
				}
			}
		}
	}

	public static File getProcessedImages() {
		return new File(baseDir.getAbsolutePath() + File.separator
				+ PROCESSED_IMAGES);
	}

	protected static class MainFrameException extends RuntimeException {
		public MainFrameException(String msg, Throwable th) {
			super(msg, th);
		}
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

		tableScrollPane = new javax.swing.JScrollPane();
		nextButton = new javax.swing.JButton();
		jToolBar1 = new javax.swing.JToolBar();
		scaleToggle = new javax.swing.JToggleButton();
		cropToggle = new javax.swing.JToggleButton();
		recropToggle = new javax.swing.JToggleButton();
		gia2DToggle = new javax.swing.JToggleButton();
		rootwork3DToggle = new javax.swing.JToggleButton();
        rootwork3DPersToggle = new javax.swing.JToggleButton();
		gia3DToggle = new javax.swing.JToggleButton();
		gia3D_v2Toggle = new javax.swing.JToggleButton();
		reviewToggle = new javax.swing.JToggleButton();
		exportToggle = new javax.swing.JToggleButton();
		qcToggle = new javax.swing.JToggleButton();
		qc2Toggle = new javax.swing.JToggleButton();
		qc3Toggle = new javax.swing.JToggleButton();
		jToolBar2 = new javax.swing.JToolBar();
		editColsButton = new javax.swing.JButton();
		filterButton = new javax.swing.JButton();
		adminButton = new javax.swing.JButton();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		jMenu2 = new javax.swing.JMenu();
		jMenu3 = new javax.swing.JMenu();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("rsa-gia 3.1.0");
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

		nextButton.setText("Next");

		jToolBar1.setFloatable(false);
		jToolBar1.setRollover(true);

		scaleToggle.setText("Scale");
		scaleToggle.setFocusable(false);
		scaleToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		scaleToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(scaleToggle);

		cropToggle.setText("Crop");
		cropToggle.setFocusable(false);
		cropToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		cropToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(cropToggle);

		recropToggle.setText("Recrop");
		recropToggle.setFocusable(false);
		recropToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		recropToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(recropToggle);

		gia2DToggle.setText("Gia2D");
		gia2DToggle.setFocusable(false);
		gia2DToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		gia2DToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(gia2DToggle);

		rootwork3DToggle.setText("Rootwork");
		rootwork3DToggle.setFocusable(false);
		rootwork3DToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		rootwork3DToggle
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(rootwork3DToggle);

        rootwork3DPersToggle.setText("RootworkPers");
        rootwork3DPersToggle.setFocusable(false);
        rootwork3DPersToggle
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rootwork3DPersToggle
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(rootwork3DPersToggle);



		gia3DToggle.setText("Gia3D");
//		gia3DToggle.setFocusable(false);
//		gia3DToggle
//				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
//		gia3DToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
//		jToolBar1.add(gia3DToggle);


		gia3D_v2Toggle.setText("3D_traits");
		gia3D_v2Toggle.setFocusable(false);
		gia3D_v2Toggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		gia3D_v2Toggle
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(gia3D_v2Toggle);

		reviewToggle.setText("Review");
		reviewToggle.setFocusable(false);
		reviewToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		reviewToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(reviewToggle);

		exportToggle.setText("Export");
		exportToggle.setFocusable(false);
		exportToggle
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		exportToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(exportToggle);

		qcToggle.setText("QC");
		qcToggle.setFocusable(false);
		qcToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		qcToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(qcToggle);

		qc2Toggle.setText("QC2");
		qc2Toggle.setFocusable(false);
		qc2Toggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		qc2Toggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(qc2Toggle);

		qc3Toggle.setText("QC3");
		qc3Toggle.setFocusable(false);
		qc3Toggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		qc3Toggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar1.add(qc3Toggle);

		jToolBar2.setFloatable(false);
		jToolBar2.setRollover(true);

		editColsButton.setText("Edit Columns...");
		editColsButton.setFocusable(false);
		editColsButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		editColsButton
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar2.add(editColsButton);

		filterButton.setText("Filter...");
		filterButton.setFocusable(false);
		filterButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		filterButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar2.add(filterButton);

		adminButton.setText("Admin");
		adminButton.setFocusable(false);
		adminButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		adminButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jToolBar2.add(adminButton);

		jMenu1.setText("File");
		jMenu1.setEnabled(false);
		jMenuBar1.add(jMenu1);

		jMenu2.setText("Window");
		jMenu2.setEnabled(false);
		jMenuBar1.add(jMenu2);

		jMenu3.setText("Help");
		jMenu3.setEnabled(false);
		jMenuBar1.add(jMenu3);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		jToolBar1,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jToolBar2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addContainerGap()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						nextButton,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						84,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						tableScrollPane,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						892,
																						Short.MAX_VALUE))))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jToolBar2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														25,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														jToolBar1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														25,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(tableScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										345, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(nextButton).addContainerGap()));

		getAccessibleContext().setAccessibleName(null);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				File f = new File("./test-resources/gui-tmp/rsa");
				// // tw 2014nov13
				// SimpleSecurityManager ssm = new
				// SimpleSecurityManager("a=rwX",
				// "staff", "ug=rw,o=r", "staff");
				SimpleSecurityManager ssm = new SimpleSecurityManager("staff",
						"staff");
				Scale scale = new Scale();
				Crop crop = new Crop();
				String desc = "averagerootwidthfeaturevalue;bushinessfeaturevalue;depthfeaturevalue;ellipseaxesaspectratiofeaturevalue;lengthdistrfeaturevalue;majorellipseaxesfeaturevalue;maximumnumberofrootsfeaturevalue;maxwidthfeaturevalue;mediannumberofrootsfeaturevalue;minorellipseaxesfeaturevalue;networkareafeaturevalue;networkconvexareafeaturevalue;perimeterfeaturevalue;solidityfeaturevalue;specificrootlengthfeaturevalue;surfaceareafeaturevalue;totallengthfeaturevalue;widthdepthratiofeaturevalue";
				GiaRoot2D gia = new GiaRoot2D(new File(
						"./test-resources/templates"),
						"/scratch/gia2/gia/interpreter", new File(
								"/scratch/gia2/gia"), desc,
                        new SimpleSecurityManager("rootarch", "rootarch"));
				Export export = new Export(null, scale, gia, null, null, null);
				// new MainFrame(f,ssm, new ApplicationManager(ssm, scale, crop,
				// gia, export)).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton adminButton;
	private javax.swing.JToggleButton cropToggle;
	private javax.swing.JButton editColsButton;
	private javax.swing.JToggleButton exportToggle;
	private javax.swing.JButton filterButton;
	private javax.swing.JToggleButton gia2DToggle;
	private javax.swing.JToggleButton gia3DToggle;
	private javax.swing.JToggleButton gia3D_v2Toggle;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenu jMenu3;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JToolBar jToolBar2;
	private javax.swing.JButton nextButton;
	private javax.swing.JToggleButton qc2Toggle;
	private javax.swing.JToggleButton qc3Toggle;
	private javax.swing.JToggleButton qcToggle;
	private javax.swing.JToggleButton recropToggle;
	private javax.swing.JToggleButton reviewToggle;
	private javax.swing.JToggleButton rootwork3DToggle;
    private javax.swing.JToggleButton rootwork3DPersToggle;
	private javax.swing.JToggleButton scaleToggle;
	private javax.swing.JScrollPane tableScrollPane;
	// End of variables declaration//GEN-END:variables

}