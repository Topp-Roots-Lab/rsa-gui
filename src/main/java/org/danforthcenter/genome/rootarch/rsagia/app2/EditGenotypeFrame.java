package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditGenotypeFrame extends JDialog implements ActionListener {
    private JPanel panel1;
    private JTextField organismField;
    private JTextField genotypeField;
    private JButton saveButton;
    private JButton cancelButton;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;
    private String selectedGenotype;

    public EditGenotypeFrame(String selectedOrganism, String selectedGenotype) {
        super(null, "Edit Genotype", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        this.saveButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        this.selectedOrganism = selectedOrganism;
        this.selectedGenotype = selectedGenotype;
        this.organismField.setText(this.selectedOrganism);
        this.genotypeField.setText(this.selectedGenotype);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.saveButton) {
            String newGenotype = this.genotypeField.getText();
            boolean check = true;
            if (newGenotype.isEmpty()) {
                check = false;
                JOptionPane.showMessageDialog(null, "Genotype value should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && !newGenotype.equals(this.selectedGenotype) && this.mdf.checkGenotypeExists(this.selectedOrganism, newGenotype)) {
                check = false;
                JOptionPane.showMessageDialog(null, "Genotype value already exists.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true) {
                try {
                    this.mdf.updateGenotype(this.selectedOrganism, this.selectedGenotype, newGenotype);
                    firePropertyChange("getall", null, null);
                    JOptionPane.showMessageDialog(null, "The genotype is edited successfully.", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "The genotype is NOT edited successfully.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == this.cancelButton) {
            this.dispose();
        }
    }

    private void createUIComponents() {
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
        label1.setText("Selected Organism:");
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
        organismField = new JTextField();
        organismField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismField, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Enter Genotype:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        genotypeField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(genotypeField, gbc);
        saveButton = new JButton();
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel1.add(saveButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(cancelButton, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
