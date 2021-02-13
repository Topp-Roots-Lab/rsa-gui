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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.*;

import org.danforthcenter.genome.rootarch.rsagia.db.enums.UserAccessLevel;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.FillDb;
import org.danforthcenter.genome.rootarch.rsagia2.*;

/**
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
    protected File csvTemplateDir;
    protected File userFile;

    protected ArrayList<StringPairFilter> speciesFilter;
    protected ArrayList<StringPairFilter> experimentFilter;
    protected ArrayList<StringPairFilter> plantFilter;
    protected ArrayList<StringPairFilter> imagingDayFilter;
    protected ArrayList<StringPairFilter> imagingDayPlant_Filter;

    protected static final String PROCESSED_IMAGES = "processed_images";

    /**
     * Creates new form MainFrame
     */
    public MainFrame(File baseDir, File csvTemplateDir, ISecurityManager ism, ApplicationManager am,
                     ArrayList<StringPairFilter> speciesFilter,
                     ArrayList<StringPairFilter> experimentFilter,
                     ArrayList<StringPairFilter> plantFilter,
                     ArrayList<StringPairFilter> imagingDayFilter,
                     ArrayList<StringPairFilter> imagingDayPlant_Filter,
                     File userFile,
                     ArrayList<String> userCols) {
        initComponents();
        setLocationRelativeTo(null);

        this.baseDir = baseDir;
        this.csvTemplateDir = csvTemplateDir;
        this.ism = ism;
        this.am = am;
        this.speciesFilter = speciesFilter;
        this.experimentFilter = experimentFilter;
        this.plantFilter = plantFilter;
        this.imagingDayFilter = imagingDayFilter;
        this.imagingDayPlant_Filter = imagingDayPlant_Filter;


        // Set application icon
        try {
            Image iconImg = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("img/rsa-gia.png"));
            this.setIconImage(iconImg);
        } catch (Exception e) {
            System.out.println(e);
        }

        riss = RsaImageSet.getAll(baseDir, ism, speciesFilter,
                experimentFilter, plantFilter, imagingDayFilter,
                imagingDayPlant_Filter);
        // System.out.println(this.getClass() + " " + riss.size()
        //                   + " " + riss.get(0));

        ///////////////////////////////
        FillDb cfdb = null;
        try {
            //cfdb = new FillDb(baseDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cfdb != null) {
            ArrayList<StringPairFilter> emptyFilter = new ArrayList<>();
            riss = RsaImageSet_old.getAll(baseDir, ism, emptyFilter,
                    emptyFilter, emptyFilter, emptyFilter,
                    emptyFilter);
            cfdb.refillAllTables(riss, am);
            System.exit(0);
        }
        ////////////////////////////////////

        rsaTable = new RsaInputTable(am);
        rsaTable.setData(riss);
        ArrayList<String> cols = new ArrayList<String>();
        cols.add(RsaInputTable.SPECIES);
        cols.add(RsaInputTable.EXPERIMENT);
        cols.add(RsaInputTable.PLANT);
        cols.add(RsaInputTable.IMAGING_DAY);

        if (userCols != null) {
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
        this.addOrganism.addActionListener(this);
        this.addExperiment.addActionListener(this);
        this.addSeed.addActionListener(this);
        this.addGenotype.addActionListener(this);
        this.addGia2DConfig.addActionListener(this);
        this.editOrganism.addActionListener(this);
        this.editExperiment.addActionListener(this);
        this.editSeed.addActionListener(this);
        this.editGenotype.addActionListener(this);
        this.editGia2DConfig.addActionListener(this);
        this.uploadImages.addActionListener(this);
        this.uploadSeedsCSV.addActionListener(this);
        this.addUser.addActionListener(this);
        this.editUser.addActionListener(this);

        this.jMenu1.addActionListener(this);
        this.jMenu2.addActionListener(this);
        this.jMenu3.addActionListener(this);
        this.jMenu4.addActionListener(this);
        this.jMenu5.addActionListener(this);

        this.helpMenu.addActionListener(this);
        this.about.addActionListener(this);

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
            ecf.setLocationRelativeTo(null);
            ecf.setVisible(true);
        } else if (e.getSource() == this.filterButton) {
            rff.setLocationRelativeTo(null);
            rff.setVisible(true);
        } else if (e.getSource() == this.adminButton) {
            admin.setLocationRelativeTo(null);
            admin.setVisible(true);
        } else if (e.getSource() == this.editOrganism) {
            SelectOrganismFrame selOrg = new SelectOrganismFrame(am.getDirRename(), baseDir);
            selOrg.addPropertyChangeListener("getall", this);
            selOrg.setLocationRelativeTo(null);
            selOrg.setVisible(true);
        } else if (e.getSource() == this.editExperiment) {
            SelectExperimentFrame selExp = new SelectExperimentFrame(am.getDirRename(), baseDir);
            selExp.addPropertyChangeListener("getall", this);
            selExp.setLocationRelativeTo(null);
            selExp.setVisible(true);
        } else if (e.getSource() == this.editSeed) {
            SelectSeedFrame selSeed = new SelectSeedFrame(am.getDirRename(), baseDir);
            selSeed.addPropertyChangeListener("getall", this);
            selSeed.setLocationRelativeTo(null);
            selSeed.setVisible(true);
        }else if (e.getSource() == this.editGenotype) {
            SelectGenotypeFrame selGenotype = new SelectGenotypeFrame();
            selGenotype.addPropertyChangeListener("getall", this);
            selGenotype.setLocationRelativeTo(null);
            selGenotype.setVisible(true);
        }else if (e.getSource() == this.editGia2DConfig) {
            SelectGia2DConfigFrame selGia2DConfig = new SelectGia2DConfigFrame();
            selGia2DConfig.addPropertyChangeListener("getall", this);
            selGia2DConfig.setLocationRelativeTo(null);
            selGia2DConfig.setVisible(true);
        }
        else if (e.getSource() == this.uploadImages) {
            UploadImages ui = new UploadImages(am.getImport(), baseDir);
            ui.addPropertyChangeListener("getall", this);
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
        } else if (e.getSource() == this.addOrganism) {
            if (UserAccess.getCurrentAccessLevel() == UserAccessLevel.Admin) {
                AddOrganismFrame aof = new AddOrganismFrame();
                aof.setLocationRelativeTo(null);
                aof.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(null, "You don't have the permission to add organism.", null, JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == this.addExperiment) {
            AddExperimentFrame aef = new AddExperimentFrame();
            aef.setLocationRelativeTo(null);
            aef.setVisible(true);
        } else if (e.getSource() == this.addSeed) {
            AddSeedFrame asf = new AddSeedFrame();
            asf.setLocationRelativeTo(null);
            asf.setVisible(true);
        } else if (e.getSource() == this.addGenotype) {
            AddGenotypeFrame agf = new AddGenotypeFrame();
            agf.setLocationRelativeTo(null);
            agf.setVisible(true);
        } else if (e.getSource() == this.addGia2DConfig) {
            AddGia2DConfigFrame agf = new AddGia2DConfigFrame();
            agf.setLocationRelativeTo(null);
            agf.setVisible(true);
        } else if (e.getSource() == this.uploadSeedsCSV) {
            BatchSeedImport bsi = new BatchSeedImport(csvTemplateDir);
            bsi.addPropertyChangeListener(this);
            bsi.setLocationRelativeTo(null);
            bsi.setVisible(true);
        } else if (e.getSource() == this.addUser) {
            if (UserAccess.getCurrentAccessLevel() == UserAccessLevel.Admin) {
                AddUserFrame auf = new AddUserFrame();
                auf.setLocationRelativeTo(null);
                auf.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "You don't have the permission to add user.", null, JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == this.editUser) {
            SelectUserFrame selUser = new SelectUserFrame();
            selUser.setLocationRelativeTo(null);
            selUser.setVisible(true);
        }
        else if (e.getSource() == this.about) {
            AboutFrame aboutFrame = new AboutFrame();
            aboutFrame.setLocationRelativeTo(null);
            aboutFrame.setVisible(true);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("update")) {
            rsaTable.updateRows(rsaTable.getCheckedRowIndexes());
        }
        if (evt.getPropertyName().equals("getall")) {
            riss = RsaImageSet.getAll(baseDir, ism, this.speciesFilter,
                    this.experimentFilter, this.plantFilter,
                    this.imagingDayFilter, this.imagingDayPlant_Filter);
            rsaTable.setData(riss);
        }

        if (evt.getSource() == rff && evt.getPropertyName().equals("done")
                && (Boolean) evt.getNewValue()) {
            rff.setVisible(false);
            this.speciesFilter = rff.getSpeciesFilter();
            this.experimentFilter = rff.getExperimentFilter();
            this.plantFilter = rff.getPlantFilter();
            this.imagingDayFilter = rff.getImagingDayFilter();
            this.imagingDayPlant_Filter = rff.getImagingDay_PlantFilter();
            riss = RsaImageSet.getAll(baseDir, ism, this.speciesFilter,
                    this.experimentFilter, this.plantFilter,
                    this.imagingDayFilter, this.imagingDayPlant_Filter);
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
                    StringPairFilter.toString(this.speciesFilter));
            props.setProperty("experiment_filter",
                    StringPairFilter.toString(this.experimentFilter));
            props.setProperty("plant_filter",
                    StringPairFilter.toString(this.plantFilter));
            props.setProperty("imaging_day_filter",
                    StringPairFilter.toString(this.imagingDayFilter));
            props.setProperty("imaging_plant_day_filter",
                    StringPairFilter.toString(this.imagingDayPlant_Filter));

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
        addOrganism = new javax.swing.JMenuItem();
        addExperiment = new javax.swing.JMenuItem();
        addSeed = new javax.swing.JMenuItem();
        addGenotype = new javax.swing.JMenuItem();
        addGia2DConfig = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        editOrganism = new javax.swing.JMenuItem();
        editExperiment = new javax.swing.JMenuItem();
        editSeed = new javax.swing.JMenuItem();
        editGenotype = new javax.swing.JMenuItem();
        editGia2DConfig = new javax.swing.JMenuItem();

        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        uploadImages = new javax.swing.JMenuItem();
        uploadSeedsCSV = new javax.swing.JMenuItem();
        addUser = new javax.swing.JMenuItem();
        editUser = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        about = new javax.swing.JMenuItem();
        documentation = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("rsa-gia 4.1.1");
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
        // qcToggle.setEnabled(false);
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
        // qc3Toggle.setEnabled(false);
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

        jMenu1.setText("Add Metadata");
        jMenu1.setEnabled(true);
        jMenuBar1.add(jMenu1);
        addOrganism.setText("Add Organism");
        jMenu1.add(addOrganism);
        addExperiment.setText("Add Experiment");
        jMenu1.add(addExperiment);
        addSeed.setText("Add Seed");
        jMenu1.add(addSeed);
        addGenotype.setText("Add Genotype");
        jMenu1.add(addGenotype);
        addGia2DConfig.setText("Add Gia2D Config");
        jMenu1.add(addGia2DConfig);
        jMenu2.setText("Edit Metadata");
        jMenu2.setEnabled(true);
        jMenuBar1.add(jMenu2);
        editOrganism.setText("Edit/View Organism");
        jMenu2.add(editOrganism);
        editExperiment.setText("Edit/View Experiment");
        jMenu2.add(editExperiment);
        editSeed.setText("Edit/View Seed");
        jMenu2.add(editSeed);
        editGenotype.setText("Edit/View Genotype");
        jMenu2.add(editGenotype);
        editGia2DConfig.setText("Edit/View Gia2D Config");
        jMenu2.add(editGia2DConfig);

        jMenu3.setText("Import Seed Metadata");
        jMenu3.setEnabled(true);
        jMenuBar1.add(jMenu3);
        uploadSeedsCSV.setText("Upload CSV File");
        jMenu3.add(uploadSeedsCSV);

        jMenu4.setText("Add Dataset");
        jMenu4.setEnabled(true);

        jMenuBar1.add(jMenu4);
        uploadImages.setText("Upload Images");
        jMenu4.add(uploadImages);

        jMenu5.setText("Manage Users");
        jMenu5.setEnabled(true);
        addUser.setText("Add User");
        jMenu5.add(addUser);

        jMenu5.add(editUser);
        jMenu5.setEnabled(true);
        editUser.setText("Edit/View User");
        jMenu5.add(editUser);

        jMenuBar1.add(jMenu5);

        helpMenu.setText("Help");
        helpMenu.setEnabled(true);
        documentation.setText("Documentation");
        helpMenu.add(documentation);
        helpMenu.addSeparator();
        about.setText("About");
        helpMenu.setEnabled(false); // Not yet fully implemented
        helpMenu.add(about);
        jMenuBar1.add(helpMenu);

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
     * @param args the command line arguments
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
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuItem addOrganism;
    private javax.swing.JMenuItem addExperiment;
    private javax.swing.JMenuItem addSeed;
    private javax.swing.JMenuItem addGenotype;
    private javax.swing.JMenuItem addGia2DConfig;
    private javax.swing.JMenuItem editOrganism;
    private javax.swing.JMenuItem editExperiment;
    private javax.swing.JMenuItem editSeed;
    private javax.swing.JMenuItem editGenotype;
    private javax.swing.JMenuItem editGia2DConfig;
    private javax.swing.JMenuItem uploadImages;
    private javax.swing.JMenuItem uploadSeedsCSV;
    private javax.swing.JMenuItem addUser;
    private javax.swing.JMenuItem editUser;
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
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem about;
    private javax.swing.JMenuItem documentation;
    // End of variables declaration//GEN-END:variables

}