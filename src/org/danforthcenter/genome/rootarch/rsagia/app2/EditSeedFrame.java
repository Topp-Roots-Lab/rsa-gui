package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.DirRename;
import org.danforthcenter.genome.rootarch.rsagia2.FileUtil;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class EditSeedFrame extends JDialog implements ActionListener {
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField organismField;
    private JTextField experimentField;
    private JTextField seedField;
    private JTextField dryshootField;
    private JTextField dryrootField;
    private JTextField wetshootField;
    private JTextField wetrootField;
    private JTextField schamberField;
    private JTextField descriptionField;
    private JTextField imagingStartDateField;
    private JPanel panel1;
    private JComboBox imagingIntervalUnitComboBox;
    private JComboBox genotypeComboBox;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;
    private String selectedExperiment;
    private String seedNew;
    private String seedOld;
    private DirRename dirRenameApp;
    private File baseDir;

    public EditSeedFrame(String organism, String experiment, String seed, DirRename dirRenameApp, File baseDir) {
        super(null, "Edit Seed", ModalityType.APPLICATION_MODAL);
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        this.saveButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        this.selectedOrganism = organism;
        this.selectedExperiment = experiment;
        this.seedOld = seed;
        this.dirRenameApp = dirRenameApp;
        this.baseDir = baseDir;
        Result<Record> seedRecord = this.mdf.findSeedMetadataFromOrgExpSeed(organism, experiment, seed);
        Record r = seedRecord.get(0);
        organismField.setText(organism);
        experimentField.setText(experiment);
        seedField.setText(seed);

        int selectedGenotypeID = -1;
        String selectedGenotype = "None";
        Object seedGenotypeID = r.getValue("genotype_id");
        if (seedGenotypeID != null) {
            selectedGenotypeID = (int) seedGenotypeID;
        }
        DefaultComboBoxModel genotypes = new DefaultComboBoxModel();
        genotypes.addElement("None");
        Result<Record> genotypeRecord1 = this.mdf.findGenotypesFromOrganism(this.selectedOrganism);
        for (Record r1 : genotypeRecord1) {
            int genotypeId = (int) r1.getValue("genotype_id");
            String genotypeName = (String) r1.getValue("genotype_name");
            genotypes.addElement(genotypeName);
            if (genotypeId == selectedGenotypeID) {
                selectedGenotype = genotypeName;
            }
        }
        genotypeComboBox.setModel(genotypes);
        genotypeComboBox.setSelectedItem(selectedGenotype);

        if (r.getValue("dry_shoot") == null) {
            dryshootField.setText("");
        } else {
            dryshootField.setText(Double.toString((Double) r.getValue("dry_shoot")));
        }
        if (r.getValue("dry_root") == null) {
            dryrootField.setText("");
        } else {
            dryrootField.setText(Double.toString((Double) r.getValue("dry_root")));
        }
        if (r.getValue("wet_shoot") == null) {
            wetshootField.setText("");
        } else {
            wetshootField.setText(Double.toString((Double) r.getValue("wet_shoot")));
        }
        if (r.getValue("wet_root") == null) {
            wetrootField.setText("");
        } else {
            wetrootField.setText(Double.toString((Double) r.getValue("wet_root")));
        }
        if (r.getValue("str_chamber_row_column") == null) {
            schamberField.setText("");
        } else {
            schamberField.setText((String) r.getValue("str_chamber_row_column"));
        }
        DefaultComboBoxModel units = new DefaultComboBoxModel(new String[]{"hour", "day"});
        units.setSelectedItem(r.getValue("imaging_interval_unit"));
        imagingIntervalUnitComboBox.setModel(units);
        descriptionField.setText((String) r.getValue("description"));
        Date imagingStart = (Date) r.getValue("imaging_start_date");
        if (imagingStart == null) {
            imagingStartDateField.setText("");
        } else {
            imagingStartDateField.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(imagingStart));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            this.seedNew = seedField.getText();
            String genotypeNew = (String) genotypeComboBox.getSelectedItem();
            String dryshootNew = dryshootField.getText();
            String dryrootNew = dryrootField.getText();
            String wetshootNew = wetshootField.getText();
            String wetrootNew = wetrootField.getText();
            String imagingIntervalUnitNew = (String) imagingIntervalUnitComboBox.getSelectedItem();
            String descriptionNew = descriptionField.getText();
            String dateNew = imagingStartDateField.getText();
            Date imagingStartDateNew = null;
            try {
                imagingStartDateNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateNew);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            boolean check = true;
            if ((!seedNew.substring(0, 1).equals("p")) ||
                    !(this.mdf.isNumeric(dryshootNew) || dryshootNew.isEmpty() || dryshootNew == null) ||
                    !(this.mdf.isNumeric(dryrootNew) || dryrootNew.isEmpty() || dryrootNew == null) ||
                    !(this.mdf.isNumeric(wetshootNew) || wetshootNew.isEmpty() || wetshootNew == null) ||
                    !(this.mdf.isNumeric(wetrootNew) || wetrootNew.isEmpty() || wetrootNew == null)) {
                check = false;
                JOptionPane.showMessageDialog(null, "Some values are not in valid format.", "ERROR", JOptionPane.ERROR_MESSAGE);

            } else if (check == true) {
                File originalImagesOld = new File(this.baseDir + File.separator + "original_images" + File.separator +
                        this.selectedOrganism + File.separator + selectedExperiment + File.separator + this.seedOld);
                File processedImagesOld = new File(this.baseDir + File.separator + "processed_images" + File.separator +
                        this.selectedOrganism + File.separator + selectedExperiment + File.separator + this.seedOld);
                File processedImagesNew = new File(this.baseDir + File.separator + "processed_images" + File.separator +
                        this.selectedOrganism + File.separator + selectedExperiment + File.separator + this.seedNew);
                try {
                    if (originalImagesOld.exists() && !seedOld.equals(seedNew)) {
                        FileUtil.renameDirWithPrivileges(originalImagesOld, seedNew, this.dirRenameApp);
                    }
                    if (processedImagesOld.exists() && !seedOld.equals(seedNew)) {
                        FileUtil.renameFile(processedImagesOld, processedImagesNew);
                    }
                    Double dryshootNewD = null;
                    Double dryrootNewD = null;
                    Double wetshootNewD = null;
                    Double wetrootNewD = null;
                    String strchamberNewD = "";
                    if (!dryshootField.getText().isEmpty() && dryshootField.getText() != null) {
                        dryshootNewD = Double.valueOf(dryshootField.getText());
                    }
                    if (!dryrootField.getText().isEmpty() && dryrootField.getText() != null) {
                        dryrootNewD = Double.valueOf(dryrootField.getText());
                    }
                    if (!wetshootField.getText().isEmpty() && wetshootField.getText() != null) {
                        wetshootNewD = Double.valueOf(wetshootField.getText());
                    }
                    if (!wetrootField.getText().isEmpty() && wetrootField.getText() != null) {
                        wetrootNewD = Double.valueOf(wetrootField.getText());
                    }
                    if (!schamberField.getText().isEmpty() && schamberField.getText() != null) {
                        strchamberNewD = schamberField.getText();
                    }

                    int genotypeNewID = -1;
                    if (!genotypeNew.equals("None")) {
                        Result<Record> genotypeRecord3 = this.mdf.findGenotypeID(genotypeNew, this.selectedOrganism);
                        Record r3 = genotypeRecord3.get(0);
                        genotypeNewID = (int) r3.getValue("genotype_id");
                    }

                    this.mdf.updateSeed(this.seedOld, this.selectedOrganism, this.selectedExperiment, this.seedNew, genotypeNewID,
                            dryshootNewD, dryrootNewD, wetshootNewD, wetrootNewD, strchamberNewD, imagingIntervalUnitNew, descriptionNew,
                            imagingStartDateNew);
                    firePropertyChange("getall", null, null);
                    JOptionPane.showMessageDialog(null, "This seed is edited successfully.", null, JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Seed is NOT edited successfully.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                this.dispose();
            }
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setPreferredSize(new Dimension(500, 571));
        final JLabel label1 = new JLabel();
        label1.setText("Organism:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Experiment:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Seed:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Genotype:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Dry Shoot:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer6, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Dry Root:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer7, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Wet Shoot:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer8, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Wet Root:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer9, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Sterilization Chamber - RowColumn: (eg: 1-C2)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer10, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Imaging Interval Unit:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 19;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer11, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Description:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer12, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("Imaging Start Date (yyyy-MM-dd HH:mm:ss):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer13, gbc);
        saveButton = new JButton();
        saveButton.setHideActionText(false);
        saveButton.setPreferredSize(new Dimension(90, 35));
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 110, 0, 110);
        panel1.add(saveButton, gbc);
        cancelButton = new JButton();
        cancelButton.setPreferredSize(new Dimension(90, 35));
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(cancelButton, gbc);
        organismField = new JTextField();
        organismField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismField, gbc);
        experimentField = new JTextField();
        experimentField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(experimentField, gbc);
        seedField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(seedField, gbc);
        dryshootField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dryshootField, gbc);
        dryrootField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dryrootField, gbc);
        wetshootField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(wetshootField, gbc);
        wetrootField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(wetrootField, gbc);
        schamberField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(schamberField, gbc);
        descriptionField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(descriptionField, gbc);
        imagingStartDateField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(imagingStartDateField, gbc);
        imagingIntervalUnitComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        imagingIntervalUnitComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(imagingIntervalUnitComboBox, gbc);
        genotypeComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(genotypeComboBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
