package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.db.enums.UserAccessLevel;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.DirRename;
import org.danforthcenter.genome.rootarch.rsagia2.UserAccess;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class SelectOrganismFrame extends JDialog implements
        ActionListener, PropertyChangeListener {
    private JComboBox comboBox1;
    private JButton editButton;
    private JPanel panel1;
    private JButton viewButton;
    private MetadataDBFunctions mdf;
    private DirRename dirRenameApp;
    private File baseDir;

    public SelectOrganismFrame(DirRename dirRenameApp, File baseDir) {
        super(null, "Select Organism", ModalityType.APPLICATION_MODAL);

        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
        this.dirRenameApp = dirRenameApp;
        this.baseDir = baseDir;
        editButton.addActionListener(this);
        viewButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        loadOrganisms();
    }

    private void loadOrganisms() {
        DefaultComboBoxModel organisms = new DefaultComboBoxModel();
        Result<Record> organismRecord = this.mdf.selectAllOrganism();
        for (Record r : organismRecord) {
            organisms.addElement((String) r.getValue("organism_name"));
        }
        comboBox1.setModel(organisms);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.editButton) {
            if (UserAccess.getCurrentAccessLevel() == UserAccessLevel.Admin) {
                int index = comboBox1.getSelectedIndex();
                String selectedOrganism = (String) comboBox1.getItemAt(index);
                EditOrganismFrame editOrganism = new EditOrganismFrame(selectedOrganism, this.dirRenameApp, this.baseDir);
                editOrganism.addPropertyChangeListener("getall", this);
                editOrganism.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(null, "You don't have the permission to edit organism.", null, JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == this.viewButton) {
            String selectedOrganism = (String) comboBox1.getSelectedItem();
            ViewOrganismFrame viewOrganism = new ViewOrganismFrame(selectedOrganism);
            viewOrganism.setVisible(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("getall")) {
            loadOrganisms();
            firePropertyChange("getall", null, null);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
        panel1.setPreferredSize(new Dimension(300, 100));
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
        comboBox1 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(comboBox1, gbc);
        editButton = new JButton();
        editButton.setText("Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(editButton, gbc);
        viewButton = new JButton();
        viewButton.setText("View");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 50, 0, 0);
        panel1.add(viewButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}