package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.DirRename;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class SelectSeedFrame extends JDialog implements ActionListener, PropertyChangeListener {
    private JComboBox<String> orgComboBox;
    private JComboBox<String> expComboBox;
    private JComboBox<String> seedComboBox;
    private JButton viewButton;
    private JButton editButton;
    private JPanel panel1;
    private JComboBox<String> genotypeComboBox;
    private DirRename dirRenameApp;
    private File baseDir;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;
    private String selectedExperiment;
    private String selectedGenotype;
    private String selectedSeed;

    public SelectSeedFrame(DirRename dirRenameApp, File baseDir) {
        super(null, "Select Seed", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.dirRenameApp = dirRenameApp;
        this.baseDir = baseDir;
        this.viewButton.addActionListener(this);
        this.editButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();

        loadOrganisms();

        orgComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedOrganism = (String) e.getItem();
                    loadExperiments();
                }
            }
        });

        expComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedExperiment = (String) e.getItem();
                    loadGenotypes();
                }
            }
        });

        genotypeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedGenotype = (String) e.getItem();
                    loadSeeds();
                }
            }
        });

        seedComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedSeed = (String) e.getItem();
                }
            }
        });
    }

    private void loadOrganisms() {
        ArrayList<String> orgList = this.mdf.findOrgsHavingSeed();
        DefaultComboBoxModel<String> organisms = new DefaultComboBoxModel<>(orgList.toArray(new String[0]));
        selectedOrganism = (String) organisms.getElementAt(0);
        orgComboBox.setModel(organisms);
        loadExperiments();
    }

    private void loadExperiments() {
        DefaultComboBoxModel<String> experiments = new DefaultComboBoxModel<>();
        Result<Record> expRecord = this.mdf.findExperimentFromOrganism(selectedOrganism);
        for (Record r : expRecord) {
            experiments.addElement((String) r.getValue("experiment_code"));
        }
        selectedExperiment = (String) experiments.getElementAt(0);
        expComboBox.setModel(experiments);
        loadGenotypes();
    }

    private void loadGenotypes() {
        DefaultComboBoxModel<String> genotypes = new DefaultComboBoxModel<>();
        Result<Record> genotypeRecord = this.mdf.findDistinctGenotypesOfExperiment(selectedExperiment, selectedOrganism);
        for (Record r : genotypeRecord) {
            Object genotypeName = r.getValue("genotype_name");
            if (genotypeName == null) {
                genotypeName = "None";
            }
            genotypes.addElement((String) genotypeName);
        }
        selectedGenotype = (String) genotypes.getElementAt(0);
        genotypeComboBox.setModel(genotypes);
        loadSeeds();
    }

    private void loadSeeds() {
        DefaultComboBoxModel<String> seeds = new DefaultComboBoxModel<>();
        Result<Record> seedRecord = this.mdf.findSeedsFromOrganismExperimentGenotype(selectedOrganism, selectedExperiment, selectedGenotype);
        for (Record r : seedRecord) {
            seeds.addElement((String) r.getValue("seed_name"));
        }
        selectedSeed = (String) seeds.getElementAt(0);
        seedComboBox.setModel(seeds);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewButton) {
            if (selectedOrganism != null && selectedExperiment != null && selectedGenotype != null && selectedSeed != null) {
                ViewSeedFrame vsf = new ViewSeedFrame(selectedOrganism, selectedExperiment, selectedSeed);
                vsf.setLocationRelativeTo(null);
                vsf.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please add seed first!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == editButton) {
            if (selectedOrganism != null && selectedExperiment != null && selectedGenotype != null && selectedSeed != null) {
                EditSeedFrame esf = new EditSeedFrame(selectedOrganism, selectedExperiment, selectedSeed, dirRenameApp, baseDir);
                esf.addPropertyChangeListener("getall", this);
                esf.setLocationRelativeTo(null);
                esf.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please add seed first!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("getall")) {
            firePropertyChange("getall", null, null);
            loadGenotypes();
            loadSeeds();
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
        panel1.setPreferredSize(new Dimension(300, 200));
        final JLabel label1 = new JLabel();
        label1.setText("Select Organism:");
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
        orgComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(orgComboBox, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Select Experiment:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        expComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(expComboBox, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Select Seed:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        seedComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(seedComboBox, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        viewButton = new JButton();
        viewButton.setText("View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 70, 0, 0);
        panel1.add(viewButton, gbc);
        editButton = new JButton();
        editButton.setText("Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(editButton, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Select Genotype:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        genotypeComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(genotypeComboBox, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
