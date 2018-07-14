package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BatchSeedImport extends JFrame implements ActionListener {

    private JTextField selectedPathField;
    private JButton browseButton;
    private JButton uploadCSVFileButton;
    private JPanel panel1;
    private JFileChooser fileChooser;
    private MetadataDBFunctions mdf;

    public BatchSeedImport() {
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.setTitle("Upload CSV File");
        this.fileChooser = new JFileChooser();
        this.fileChooser.setDialogTitle("Select CSV File");
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        this.browseButton.addActionListener(this);
        this.uploadCSVFileButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.browseButton) {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedPathField.setText(this.fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == this.uploadCSVFileButton) {
            File csvFile = new File(this.selectedPathField.getText());
            if (csvFile.getName().endsWith(".csv")) {
                String path = csvFile.getAbsolutePath();
                String[] contentsArray = null;
                String contents = null;
                try {
                    contents = new String(Files.readAllBytes(Paths.get(path)));
                    String[] contentsLines = contents.split(System.lineSeparator());
                    String[] headingArray = contentsLines[0].split(",");
                    boolean check = true;
                    for (int i = 1; i < contentsLines.length && check == true; i++) {
                        if (contentsLines.length > 1) {
                            if (headingArray[0].equals("Order")) {
                                contentsArray = contentsLines[i].split(",", -1);
                                String organism = contentsArray[1];

                                if (mdf.checkOrganismExists(organism)) {
                                    String experiment = contentsArray[2];
                                    if (experiment.isEmpty()) {
                                        check = false;
                                        JOptionPane.showMessageDialog(null, "The experiment value should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                        this.dispose();
                                    } else if (mdf.checkOrgandExpPairExists(organism, experiment)) {
                                        String seed = contentsArray[3];
                                        int genotypeID = -1;
                                        String genotypeName = contentsArray[4];
                                        if (!seed.substring(0, 1).equals("p")) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The seed value should start with 'p'.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        } else if (seed.isEmpty()) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The seed value should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        } else if (genotypeName.isEmpty()) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The genotype value should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        } else if (!genotypeName.equals("None")) {
                                            Result<Record> genotypeRecord = this.mdf.findGenotypeID(genotypeName, organism);
                                            try {
                                                Record r = genotypeRecord.get(0);
                                                genotypeID = (int) r.getValue("genotype_id");
                                            } catch (Exception e1) {
                                                check = false;
                                                JOptionPane.showMessageDialog(null, "The genotype value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                                this.dispose();
                                            }
                                        }
                                        double dry_shoot = 0;
                                        double dry_root = 0;
                                        double wet_shoot = 0;
                                        double wet_root = 0;
                                        String str_chamber = null;

                                        try {
                                            dry_shoot = Double.parseDouble(contentsArray[5]);
                                        } catch (Exception e1) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The dry shoot value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        try {
                                            dry_root = Double.parseDouble(contentsArray[6]);
                                        } catch (Exception e1) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The dry root value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        try {
                                            wet_shoot = Double.parseDouble(contentsArray[7]);
                                        } catch (Exception e1) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The wet shoot value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        try {
                                            wet_root = Double.parseDouble(contentsArray[8]);
                                        } catch (Exception e1) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The wet root value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        try {
                                            str_chamber = contentsArray[9];
                                        } catch (Exception e1) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The sterilization chamber value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        String imagingUnit = contentsArray[10];
                                        if (!imagingUnit.equals("day") && !imagingUnit.equals("hour")) {
                                            check = false;
                                            JOptionPane.showMessageDialog(null, "The imaging time point should be either day or hour.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                            this.dispose();
                                        }
                                        String description = contentsArray[11];
                                        Date imagingStartDate = null;
                                        String date = contentsArray[12];
                                        if (!date.equals("")) {
                                            date = date.replace("_", " ");
                                            try {
                                                imagingStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                                                if (imagingStartDate.equals(null)) {
                                                    JOptionPane.showMessageDialog(null, "The date you entered should be in yyyy-MM-dd_HH:mm:ss format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                                    this.dispose();
                                                    check = false;
                                                }
                                            } catch (ParseException e1) {
                                                JOptionPane.showMessageDialog(null, "The date you entered should be in yyyy-MM-dd_HH:mm:ss format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                                this.dispose();
                                                check = false;
                                                e1.printStackTrace();
                                            }
                                        }
                                        if (!mdf.checkSeedExists(organism, experiment, seed)) {
                                            try {
                                                if (check == true) {
                                                    mdf.insertSeed(organism, experiment, seed, genotypeID, dry_shoot, dry_root, wet_shoot, wet_root,
                                                            str_chamber, imagingUnit, description, imagingStartDate);
                                                    //firePropertyChange("getall", false, true);
                                                }
                                            } catch (Exception e2) {
                                                JOptionPane.showMessageDialog(null, "The values you entered are in wrong format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                                e2.printStackTrace();
                                                this.dispose();
                                            }

                                        } else {
                                            try {
                                                if (check == true) {
                                                    mdf.updateSeed(seed, organism, experiment, seed, genotypeID, dry_shoot, dry_root, wet_shoot, wet_root,
                                                            str_chamber, imagingUnit, description, imagingStartDate);
                                                    //firePropertyChange("getall", false, true);
                                                }
                                            } catch (Exception e2) {
                                                JOptionPane.showMessageDialog(null, "The values you entered are in wrong format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                                e2.printStackTrace();
                                                this.dispose();
                                            }
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "The organism and experiment pair you entered is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                        this.dispose();
                                        check = false;
                                    }
                                } else {
                                    if (organism.isEmpty()) {
                                        check = false;
                                        JOptionPane.showMessageDialog(null, "The organism value should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                        this.dispose();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "The organism you entered is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                        this.dispose();
                                        check = false;
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "There should be headings in first row.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                                check = false;
                            }
                        }
                    }
                    if (check == true) {
                        JOptionPane.showMessageDialog(null, "The seeds are saved", "INFO", JOptionPane.INFORMATION_MESSAGE);
                        this.dispose();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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
        panel1.setPreferredSize(new Dimension(500, 150));
        final JLabel label1 = new JLabel();
        label1.setText("File Path:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(label1, gbc);
        selectedPathField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(selectedPathField, gbc);
        browseButton = new JButton();
        browseButton.setText("Browse...");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(browseButton, gbc);
        uploadCSVFileButton = new JButton();
        uploadCSVFileButton.setText("Upload CSV File");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(uploadCSVFileButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
