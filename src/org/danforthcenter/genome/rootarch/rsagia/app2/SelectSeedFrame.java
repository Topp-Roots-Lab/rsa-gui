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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

public class SelectSeedFrame extends JDialog implements ActionListener, PropertyChangeListener {
    private JComboBox orgComboBox;
    private JComboBox expComboBox;
    private JComboBox seedComboBox;
    private JButton viewButton;
    private JButton editButton;
    private JPanel panel1;
    private File baseDir;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;
    private String selectedExperiment;
    private String selectedSeed;

    public SelectSeedFrame(File baseDir) {
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.setTitle("Select Seed");
        pack();
        this.setModal(true);
        this.baseDir = baseDir;
        this.viewButton.addActionListener(this);
        this.editButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        ArrayList<String> orgList = this.mdf.findOrgsHavingSeed();
        for (String org : orgList) {
            orgComboBox.addItem(org);
        }
        this.selectedOrganism = (String) orgComboBox.getItemAt(0);
        orgComboBox.setSelectedItem(this.selectedOrganism);
        orgComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    expComboBox.removeAllItems();
                    seedComboBox.removeAllItems();
                    selectedOrganism = (String) e.getItem();
                    Result<Record> expRecord3 = mdf.findExperimentFromOrganism((String) orgComboBox.getSelectedItem());
                    for (Record r3 : expRecord3) {
                        expComboBox.addItem((String) r3.getValue("experiment_code"));
                    }
                    expComboBox.setSelectedItem(expComboBox.getItemAt(0));
                }
            }
        });

        Result<Record> expRecord2 = this.mdf.findExperimentFromOrganism((String) orgComboBox.getSelectedItem());
        for (Record r2 : expRecord2) {
            expComboBox.addItem((String) r2.getValue("experiment_code"));
        }
        this.selectedExperiment = (String) expComboBox.getItemAt(0);
        expComboBox.setSelectedItem(this.selectedExperiment);
        expComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    seedComboBox.removeAllItems();
                    selectedExperiment = (String) e.getItem();

                    Result<Record> expRecord6 = mdf.findExperiment(selectedExperiment, selectedOrganism);
                    int selectedExpID = (int) expRecord6.get(0).getValue("experiment_id");
                    Result<Record> seedRecord = mdf.findSeedFromExperimentID(selectedExpID);
                    for (Record r7 : seedRecord) {
                        seedComboBox.addItem(r7.getValue("seed_name"));
                    }
                    selectedSeed = (String) seedComboBox.getItemAt(0);
                    seedComboBox.setSelectedItem(selectedSeed);

                }
            }
        });

        Result<Record> expRecord4 = this.mdf.findExperiment(selectedExperiment, selectedOrganism);
        int selectedExpID = (int) expRecord4.get(0).getValue("experiment_id");
        Result<Record> seedRecord = this.mdf.findSeedFromExperimentID(selectedExpID);
        for (Record r5 : seedRecord) {
            seedComboBox.addItem(r5.getValue("seed_name"));
        }
        this.selectedSeed = (String) seedComboBox.getItemAt(0);
        seedComboBox.setSelectedItem(this.selectedSeed);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewButton) {
            ViewSeedFrame vsf = new ViewSeedFrame(this.selectedOrganism, this.selectedExperiment, this.selectedSeed);
            vsf.setVisible(true);
        } else if (e.getSource() == editButton) {
            EditSeedFrame esf = new EditSeedFrame(selectedOrganism, selectedExperiment, selectedSeed, baseDir);
            esf.addPropertyChangeListener("getall", this);
            esf.setVisible(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("getall")) {
            firePropertyChange("getall", null, null);
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
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        seedComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(seedComboBox, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        viewButton = new JButton();
        viewButton.setText("View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(viewButton, gbc);
        editButton = new JButton();
        editButton.setText("Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(editButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
