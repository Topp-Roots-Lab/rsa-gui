package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.UserDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ViewUserFrame extends JDialog implements ActionListener {
    private JTextField userNameField;
    private JButton okButton;
    private JTextField accessLevelField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField labNameField;
    private JPanel panel1;
    private JCheckBox activeCheckBox;
    private UserDBFunctions udf;

    public ViewUserFrame(String selectedUserName) {
        super(null, "View User", ModalityType.APPLICATION_MODAL);
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        this.okButton.addActionListener(this);
        this.udf = new UserDBFunctions();

        Result<Record> userRecord = this.udf.findUserFromName(selectedUserName);
        Record r = userRecord.get(0);

        userNameField.setText(selectedUserName);
        accessLevelField.setText((String) r.getValue("access_level"));
        firstNameField.setText((String) r.getValue("first_name"));
        lastNameField.setText((String) r.getValue("last_name"));
        labNameField.setText((String) r.getValue("lab_name"));
        activeCheckBox.setSelected(((Byte) r.getValue("active")) == 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.okButton) {
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
        panel1.setPreferredSize(new Dimension(300, 300));
        final JLabel label1 = new JLabel();
        label1.setText("Username:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Access Level:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("First Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Last Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Lab Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label5, gbc);
        userNameField = new JTextField();
        userNameField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(userNameField, gbc);
        okButton = new JButton();
        okButton.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(okButton, gbc);
        accessLevelField = new JTextField();
        accessLevelField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(accessLevelField, gbc);
        firstNameField = new JTextField();
        firstNameField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(firstNameField, gbc);
        lastNameField = new JTextField();
        lastNameField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(lastNameField, gbc);
        labNameField = new JTextField();
        labNameField.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(labNameField, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Active:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label6, gbc);
        activeCheckBox = new JCheckBox();
        activeCheckBox.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(activeCheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}