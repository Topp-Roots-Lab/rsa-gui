package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddOrganismFrame extends JDialog implements ActionListener {
    private JTextField organismNameField;
    private JTextField organismCodeField;
    private JPanel panel1;
    private JButton addButton;
    private JTextField speciesField;
    private JTextField subspeciesField;
    private JTextField descriptionField;
    private MetadataDBFunctions mdf;

    public AddOrganismFrame() {
        super(null, "Add Organism", ModalityType.APPLICATION_MODAL);
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        addButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String organismName = organismNameField.getText();
            String organismCode = organismCodeField.getText();
            String species = speciesField.getText();
            String subspecies = subspeciesField.getText();
            String description = descriptionField.getText();
            boolean check = true;
            if (organismName.length() == 0 || mdf.isAlpha(organismName) == false
                    || !organismName.toLowerCase().equals(organismName)) {
                check = false;
                JOptionPane.showMessageDialog(null, "Organism name is not in valid format.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && (organismCode.length() != 2 || mdf.isAlpha(organismCode) == false
                    || !organismCode.substring(0, 1).toUpperCase().equals(organismCode.substring(0, 1))
                    || !organismCode.substring(1, 2).toLowerCase().equals(organismCode.substring(1, 2)))) {
                check = false;
                JOptionPane.showMessageDialog(null, "Organism code is not in valid format.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && mdf.checkOrganismExists(organismName)) {
                check = false;
                JOptionPane.showMessageDialog(null, "This organism is already added.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && mdf.checkOrganismCodeExists(organismCode)) {
                check = false;
                JOptionPane.showMessageDialog(null, "This species code is already added.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && mdf.checkSpeciesExists(species, subspecies)) {
                check = false;
                JOptionPane.showMessageDialog(null, "This combination of species and subspecies is already added.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true) {
                this.mdf.insertNewOrganism(organismName, organismCode, species, subspecies, description);
                JOptionPane.showMessageDialog(null, "Organism " + organismName + " is added successfully.", null, JOptionPane.INFORMATION_MESSAGE);
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
        panel1.setPreferredSize(new Dimension(340, 256));
        final JLabel label1 = new JLabel();
        label1.setText("Organism Name:*");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Organism Code:*");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label2, gbc);
        organismNameField = new JTextField();
        organismNameField.setPreferredSize(new Dimension(120, 25));
        organismNameField.setToolTipText("<html>\nCommon name of the organism, in lowercase:<br />\ne.g. \"rice\" or \"rice_glab\"<br />\n(This value is unique for each organism.)\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismNameField, gbc);
        organismCodeField = new JTextField();
        organismCodeField.setPreferredSize(new Dimension(120, 25));
        organismCodeField.setToolTipText("<html>\nTwo letter code, first letter uppercase, second letter lowercase, usually abbreviation of scientific name:<br />e.g. \"Os\" for rice<br />\n(This value is unique for each organism.)\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismCodeField, gbc);
        addButton = new JButton();
        addButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(addButton, gbc);
        speciesField = new JTextField();
        speciesField.setPreferredSize(new Dimension(120, 25));
        speciesField.setToolTipText("<html>\nScientific name of the organism:<br />\ne.g. \"Oryza sativa\" for rice<br />\n(The combination of species and subspecies is unique for each organism.)\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(speciesField, gbc);
        subspeciesField = new JTextField();
        subspeciesField.setPreferredSize(new Dimension(120, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(subspeciesField, gbc);
        descriptionField = new JTextField();
        descriptionField.setPreferredSize(new Dimension(120, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(descriptionField, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Species:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Subspecies:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Description:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label5, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}