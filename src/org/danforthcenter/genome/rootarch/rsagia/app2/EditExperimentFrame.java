package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.UserDBFunctions;
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

public class EditExperimentFrame extends JDialog implements ActionListener, PropertyChangeListener {
    private JTextField expNameField;
    private JComboBox orgComboBox;
    private JTextField descField;
    private JButton cancelButton;
    private JPanel panel1;
    private JButton saveButton;
    private JTextField organismField;
    private MetadataDBFunctions mdf;
    private String selectedExperiment;
    private String user;
    private File baseDir;
    private String selectedOrganism;


    public EditExperimentFrame(String selExperiment, String selOrganism, File baseDir) {
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.setTitle("Edit Experiment");
        pack();
        this.setModal(true);
        this.baseDir = baseDir;
        this.saveButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        this.selectedExperiment = selExperiment;
        this.selectedOrganism = selOrganism;
        expNameField.setText(this.selectedExperiment);

        Result<Record> experimentRecord = this.mdf.findExperiment(this.selectedExperiment, this.selectedOrganism);
        String description = (String) experimentRecord.getValue(0, "description");
        int userID = (int) experimentRecord.getValue(0, "user_id");
        UserDBFunctions udbf = new UserDBFunctions();
        Result<Record> userRecord = udbf.findUserFromID(userID);
        this.user = (String) userRecord.get(0).getValue("user_name");

        descField.setText(description);

        organismField.setText(selOrganism);

/*            int userID = (int) experimentRecord.getValue(0, "user_id");
            Result<Record> userRecord = this.mdf.selectAllUser();
            for (Record r : userRecord) {
                String userName = (String) r.getValue("user_name");
                comboBox2.addItem(userName);
                int user_id = (int) r.getValue("user_id");
                if (userID == user_id) {
                    comboBox2.setSelectedItem(user_id);
                }
            }*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String experimentNew = expNameField.getText();
        String desc = descField.getText();

        boolean check = true;
        if (e.getSource() == saveButton) {
            if (experimentNew.length() != 3 || !experimentNew.toUpperCase().equals(experimentNew)) {
                check = false;
                JOptionPane.showMessageDialog(null, "The experiment is not in valid format.", "ERROR", JOptionPane.ERROR_MESSAGE);

            }
            if (check == true && this.mdf.checkOrgandExpPairExists(experimentNew, selectedOrganism)) {
                check = false;
                JOptionPane.showMessageDialog(null, "This experiment and organism pair already exists.", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else if (check == true) {
                File originalImagesOld = new File(this.baseDir + File.separator + "original_images" + File.separator +
                        this.selectedOrganism + File.separator + selectedExperiment);
                File originalImagesNew = new File(this.baseDir + File.separator + "original_images" + File.separator +
                        this.selectedOrganism + File.separator + experimentNew);
                File processedImagesOld = new File(this.baseDir + File.separator + "processed_images" + File.separator +
                        this.selectedOrganism + File.separator + File.separator + selectedExperiment);
                File processedImagesNew = new File(this.baseDir + File.separator + "processed_images" + File.separator +
                        this.selectedOrganism + File.separator + File.separator + experimentNew);

                try {
                    Files.move(originalImagesOld.toPath(), originalImagesNew.toPath(), REPLACE_EXISTING);
                    Files.move(processedImagesOld.toPath(), processedImagesNew.toPath(), REPLACE_EXISTING);

                    this.mdf.updateExperiment(selectedExperiment, experimentNew, desc);
                    firePropertyChange("getall", null, null);
                    JOptionPane.showMessageDialog(null, "This experiment is edited successfully.", null, JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Experiment is NOT edited successfully.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                this.dispose();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

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
        final JPanel spacer1 = new JPanel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer2, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Enter Description:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        descField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(descField, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer3, gbc);
        saveButton = new JButton();
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 50, 0, 0);
        panel1.add(saveButton, gbc);
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(cancelButton, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Enter Experiment Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Selected  Organism:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        expNameField = new JTextField();
        expNameField.setPreferredSize(new Dimension(90, 31));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(expNameField, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel1.add(spacer4, gbc);
        organismField = new JTextField();
        organismField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(organismField, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
