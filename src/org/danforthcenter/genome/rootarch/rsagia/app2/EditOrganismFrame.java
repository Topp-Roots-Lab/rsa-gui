package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class EditOrganismFrame extends JDialog implements
        ActionListener, PropertyChangeListener {
    private JTextField nameField;
    private JButton saveButton;
    private JPanel panel1;
    private JTextField speciesField;
    private JTextField subspeciesField;
    private JTextField varietyField;
    private JButton cancelButton;
    private JTextField orgCodeField;
    private MetadataDBFunctions mdf;
    private String selectedOrganism;
    private File baseDir;

    public EditOrganismFrame(String selectedOrganism, File baseDir) {
        super(null, "Edit Organism", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        this.baseDir = baseDir;
        this.saveButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.selectedOrganism = selectedOrganism;
        nameField.setText(this.selectedOrganism);
        this.mdf = new MetadataDBFunctions();
        Result<Record> organismRecord = this.mdf.findOrganism(this.selectedOrganism);
        String organismCode = (String) organismRecord.getValue(0, "species_code");
        orgCodeField.setText(organismCode);
        String species = (String) organismRecord.getValue(0, "species");
        speciesField.setText(species);
        String subspecies = (String) organismRecord.getValue(0, "subspecies");
        subspeciesField.setText(subspecies);
        String variety = (String) organismRecord.getValue(0, "variety");
        varietyField.setText(variety);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String organismNameNew = nameField.getText();
            String organismCodeNew = orgCodeField.getText();
            String speciesNew = speciesField.getText();
            String subspeciesNew = subspeciesField.getText();
            String varietyNew = varietyField.getText();
            boolean check = true;
            if (organismCodeNew.length() != 2 || !organismCodeNew.substring(0, 1).toUpperCase().equals(organismCodeNew.substring(0, 1)) ||
                    !organismCodeNew.substring(1, 2).toLowerCase().equals(organismCodeNew.substring(1, 2)) || mdf.isAlpha(organismNameNew) == false ||
                    mdf.isAlpha(organismCodeNew) == false || !organismNameNew.toLowerCase().equals(organismNameNew) ||
                    organismNameNew.length() == 0) {
                check = false;
                JOptionPane.showMessageDialog(null, "Organism name or organism code is not in valid format.", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else if (check == true && this.mdf.checkOrgCodeAndOrganismExists(organismNameNew, organismCodeNew, this.selectedOrganism)) {
                check = false;
                JOptionPane.showMessageDialog(null, "Organism already exists.", "ERROR", JOptionPane.ERROR_MESSAGE);

            } else if (check == true) {
                File originalImagesOld = new File(this.baseDir + File.separator + "original_images" + File.separator + selectedOrganism);
                File originalImagesNew = new File(this.baseDir + File.separator + "original_images" + File.separator + organismNameNew);
                File processedImagesOld = new File(this.baseDir + File.separator + "processed_images" + File.separator + selectedOrganism);
                File processedImagesNew = new File(this.baseDir + File.separator + "processed_images" + File.separator + organismNameNew);

                try {
                    if (originalImagesOld.exists()) {
                        Files.move(originalImagesOld.toPath(), originalImagesNew.toPath(), REPLACE_EXISTING);
                    }
                    if (processedImagesOld.exists()) {
                        Files.move(processedImagesOld.toPath(), processedImagesNew.toPath(), REPLACE_EXISTING);
                    }
                    this.mdf.updateOrganism(organismNameNew, organismCodeNew, speciesNew, subspeciesNew, varietyNew, this.selectedOrganism);
                    firePropertyChange("getall", null, null);
                    JOptionPane.showMessageDialog(null, "Organism is edited successfully.", null, JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Organism is NOT edited successfully.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                this.dispose();
            }
        } else if (e.getSource() == cancelButton) {
            this.dispose();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    private void closeWindow() {
        dispose();
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
        Font panel1Font = this.$$$getFont$$$(null, -1, 14, panel1.getFont());
        if (panel1Font != null) panel1.setFont(panel1Font);
        panel1.setPreferredSize(new Dimension(300, 300));
        final JPanel spacer1 = new JPanel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer1, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, 14, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Species:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        speciesField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(speciesField, gbc);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, -1, 14, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Subspecies:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer5, gbc);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 14, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Variety:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        subspeciesField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(subspeciesField, gbc);
        varietyField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(varietyField, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer6, gbc);
        saveButton = new JButton();
        Font saveButtonFont = this.$$$getFont$$$(null, -1, 14, saveButton.getFont());
        if (saveButtonFont != null) saveButton.setFont(saveButtonFont);
        saveButton.setPreferredSize(new Dimension(90, 31));
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel1.add(saveButton, gbc);
        cancelButton = new JButton();
        Font cancelButtonFont = this.$$$getFont$$$(null, -1, 14, cancelButton.getFont());
        if (cancelButtonFont != null) cancelButton.setFont(cancelButtonFont);
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel1.add(cancelButton, gbc);
        nameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(nameField, gbc);
        orgCodeField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(orgCodeField, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer7, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Code:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
