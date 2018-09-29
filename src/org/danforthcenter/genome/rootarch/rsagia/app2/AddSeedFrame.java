package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddSeedFrame extends JDialog implements ActionListener {
    private JTextField seedField;
    private JTextField dryshootField;
    private JTextField dryrootField;
    private JTextField wetshootField;
    private JTextField wetrootField;
    private JTextField schamberField;
    private JTextField descriptionField;
    private JTextField imagingStartField;
    private JComboBox organismComboBox;
    private JComboBox experimentComboBox;
    private JComboBox imagingIntervalUnitComboBox;
    private JButton addButton;
    private JPanel panel1;
    private JComboBox genotypeComboBox;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;

    public AddSeedFrame() {
        super(null, "Add Seed", ModalityType.APPLICATION_MODAL);
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        addButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();

        loadOrganisms();

        DefaultComboBoxModel units = new DefaultComboBoxModel(new String[]{"hour", "day"});
        units.setSelectedItem("day");
        imagingIntervalUnitComboBox.setModel(units);

        organismComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedOrganism = (String) e.getItem();
                    loadExperiments();
                    loadGenotypes();
                }
            }
        });
    }

    private void loadGenotypes() {
        DefaultComboBoxModel genotypes = new DefaultComboBoxModel();
        genotypes.addElement("None");
        Result<Record> genotypeRecord = this.mdf.findGenotypesFromOrganism(this.selectedOrganism);
        for (Record r : genotypeRecord) {
            genotypes.addElement((String) r.getValue("genotype_name"));
        }
        genotypeComboBox.setModel(genotypes);
    }

    private void loadOrganisms() {
        DefaultComboBoxModel organisms = new DefaultComboBoxModel();
        Result<Record> organismRecord = this.mdf.selectAllOrganism();
        for (Record r : organismRecord) {
            organisms.addElement((String) r.getValue("organism_name"));
        }
        this.selectedOrganism = (String) organisms.getElementAt(0);
        organismComboBox.setModel(organisms);
        loadExperiments();
        loadGenotypes();
    }

    private void loadExperiments() {
        DefaultComboBoxModel experiments = new DefaultComboBoxModel();
        Result<Record> expRecord = this.mdf.findExperimentFromOrganism(this.selectedOrganism);
        for (Record r : expRecord) {
            experiments.addElement((String) r.getValue("experiment_code"));
        }
        experimentComboBox.setModel(experiments);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String organism = (String) organismComboBox.getSelectedItem();
            String experiment = (String) experimentComboBox.getSelectedItem();
            String seed = seedField.getText();
            String genotypeName = (String) genotypeComboBox.getSelectedItem();
            int genotypeID = -1;
            if (!genotypeName.equals("None")) {
                Result<Record> genotypeRecord = this.mdf.findGenotypeID(genotypeName, organism);
                Record r = genotypeRecord.get(0);
                genotypeID = (int) r.getValue("genotype_id");
            }
            boolean check = true;
            Double dryshoot = null;
            if (!dryshootField.getText().isEmpty()) {
                try {
                    dryshoot = Double.parseDouble(dryshootField.getText());
                } catch (NumberFormatException nfe) {
                    check = false;
                    JOptionPane.showMessageDialog(null, "Dryshoot value should be numeric value.", null, JOptionPane.ERROR_MESSAGE);
                }
            }
            Double dryroot = null;
            if (!dryrootField.getText().isEmpty() && check == true) {
                try {
                    dryroot = Double.parseDouble(dryrootField.getText());
                } catch (NumberFormatException nfe) {
                    check = false;
                    JOptionPane.showMessageDialog(null, "Dryroot value should be numeric value.", null, JOptionPane.ERROR_MESSAGE);
                }
            }
            Double wetshoot = null;
            if (!wetshootField.getText().isEmpty() && check == true) {
                try {
                    wetshoot = Double.parseDouble(wetshootField.getText());
                } catch (NumberFormatException nfe) {
                    check = false;
                    JOptionPane.showMessageDialog(null, "Wetshoot value should be numeric value.", null, JOptionPane.ERROR_MESSAGE);
                }
            }
            Double wetroot = null;
            if (!wetrootField.getText().isEmpty() && check == true) {
                try {
                    wetroot = Double.parseDouble(wetrootField.getText());
                } catch (NumberFormatException nfe) {
                    check = false;
                    JOptionPane.showMessageDialog(null, "Wetroot value should be numeric value.", null, JOptionPane.ERROR_MESSAGE);
                }
            }
            String schamber = "";
            if (!schamberField.getText().isEmpty()) {
                schamber = schamberField.getText();
            }
            String imagingIntervalUnit = (String) imagingIntervalUnitComboBox.getSelectedItem();
            String description = descriptionField.getText();
            String date = imagingStartField.getText();
            Date imagingStartDate = null;
            if (!date.isEmpty() && check == true) {
                try {
                    imagingStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                    check = false;
                    JOptionPane.showMessageDialog(null, "You must have entered a wrong date value.", null, JOptionPane.ERROR_MESSAGE);
                }
            }

            if (check == true && (organism.isEmpty() || experiment.isEmpty() || seed.isEmpty())) {
                check = false;
                JOptionPane.showMessageDialog(null, "You must have entered a wrong organism or experiment or seed value.", null, JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && !seed.substring(0, 1).equals("p")) {
                check = false;
                JOptionPane.showMessageDialog(null, "Seed should start with letter 'p'.", null, JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && this.mdf.checkSeedExists(organism, experiment, seed) == true) {
                check = false;
                JOptionPane.showMessageDialog(null, "This seed is already added.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true) {
                this.mdf.insertSeed(organism, experiment, seed, genotypeID, dryshoot, dryroot, wetshoot, wetroot, schamber, imagingIntervalUnit, description,
                        imagingStartDate);
                JOptionPane.showMessageDialog(null, "The seed is added to database successfully.", null, JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
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
        panel1.setOpaque(true);
        panel1.setPreferredSize(new Dimension(500, 500));
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
        label3.setText("Seed Name:");
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
        label6.setText("Dry Root");
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
        addButton = new JButton();
        addButton.setPreferredSize(new Dimension(120, 31));
        addButton.setRolloverEnabled(false);
        addButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 24;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(addButton, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 23;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer13, gbc);
        seedField = new JTextField();
        seedField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(seedField, gbc);
        dryshootField = new JTextField();
        dryshootField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dryshootField, gbc);
        dryrootField = new JTextField();
        dryrootField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dryrootField, gbc);
        wetshootField = new JTextField();
        wetshootField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(wetshootField, gbc);
        wetrootField = new JTextField();
        wetrootField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(wetrootField, gbc);
        schamberField = new JTextField();
        schamberField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(schamberField, gbc);
        descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 20;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(descriptionField, gbc);
        imagingStartField = new JTextField();
        imagingStartField.setPreferredSize(new Dimension(12, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 22;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(imagingStartField, gbc);
        organismComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismComboBox, gbc);
        experimentComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(experimentComboBox, gbc);
        imagingIntervalUnitComboBox = new JComboBox();
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
