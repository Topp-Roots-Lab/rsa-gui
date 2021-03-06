package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.UserDBFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("unchecked")
public class AddUserFrame extends JDialog implements ActionListener {
    private JComboBox<String> accessLevelComboBox;
    private JTextField firstNameField;
    private JButton addUserButton;
    private JTextField lastNameField;
    private JTextField labNameField;
    private JTextField userNameField;
    private JPanel panel1;
    private UserDBFunctions udf;

    public AddUserFrame() {
        super(null, "Add User", ModalityType.APPLICATION_MODAL);
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        addUserButton.addActionListener(this);
        this.udf = new UserDBFunctions();
        loadAccessLevels();
    }

    public void loadAccessLevels() {
        DefaultComboBoxModel<String> accessLevels = new DefaultComboBoxModel<>();
        String[] accessLevelList = this.udf.getAccessLevels();
        for (String item : accessLevelList) {
            accessLevels.addElement(item);
        }
        accessLevelComboBox.setModel(accessLevels);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addUserButton) {
            String userName = this.userNameField.getText();
            String accessLevel = (String) this.accessLevelComboBox.getSelectedItem();
            String firstName = this.firstNameField.getText();
            String lastName = this.lastNameField.getText();
            String labName = this.labNameField.getText();
            boolean check = true;
            if (userName.isEmpty()) {
                check = false;
                JOptionPane.showMessageDialog(null, "Username should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true && this.udf.checkUserExists(userName)) {
                check = false;
                JOptionPane.showMessageDialog(null, "Username already exists.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            if (check == true) {
                try {
                    this.udf.insertUser(userName, accessLevel, firstName, lastName, labName);
                    JOptionPane.showMessageDialog(null, "The user is added successfully.", null, JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "The user is NOT added successfully.", "ERROR", JOptionPane.ERROR_MESSAGE);
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
        panel1.setPreferredSize(new Dimension(300, 300));
        final JLabel label1 = new JLabel();
        label1.setText("First Name:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label1, gbc);
        firstNameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(firstNameField, gbc);
        addUserButton = new JButton();
        addUserButton.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(addUserButton, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Last Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Lab Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label3, gbc);
        lastNameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(lastNameField, gbc);
        labNameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(labNameField, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Username:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(label4, gbc);
        userNameField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(userNameField, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Access Level:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 10);
        panel1.add(label5, gbc);
        accessLevelComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(accessLevelComboBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
