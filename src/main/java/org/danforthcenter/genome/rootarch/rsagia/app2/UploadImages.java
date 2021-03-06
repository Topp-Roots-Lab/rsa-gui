package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.Import;
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
import java.util.List;

public class UploadImages extends JFrame implements ActionListener, PropertyChangeListener {
    private JButton importButton;
    private JPanel panel1;
    private JButton browseButton;
    private JTextArea printstextArea;
    private JScrollPane jsp;
    private JTextField selectedPathField;
    private JCheckBox checkBoxDelete;
    private JLabel statusLabel;
    private JLabel processingField;
    private JLabel directoryInfo;
    private JLabel deletionInfo;
    private JFileChooser fileChooser;

    private Import importApp;

    public UploadImages(Import importApp, File baseDir) {
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.setTitle("Upload Imageset");
        this.fileChooser = new JFileChooser();
        this.fileChooser.setDialogTitle("Select Imageset Directory");
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ImageIcon questionIcon = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");
        Image scaledImage = questionIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        this.directoryInfo.setIcon(scaledIcon);
        this.deletionInfo.setIcon(scaledIcon);
        File defaultImageSetDir = new File(baseDir, "to_sort" + File.separator + UserAccess.getCurrentUser() + File.separator);
        this.selectedPathField.setText(defaultImageSetDir.getAbsolutePath());

        this.browseButton.addActionListener(this);
        this.importButton.addActionListener(this);

        pack();

        this.importApp = importApp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            this.fileChooser.setCurrentDirectory(new File(this.selectedPathField.getText()));
            int result = this.fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedPathField.setText(this.fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == importButton) {
            MetadataDBFunctions dbFunctions = new MetadataDBFunctions();
            Result<Record> organismRecord = dbFunctions.selectAllOrganism();
            if (organismRecord.size() == 0) {
                JOptionPane.showMessageDialog(null, "Organism table is empty. Please add an organism first.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.processingField.setText("Processing...");
            this.printstextArea.setText(null);
            try {
                ImportWorker iw = new ImportWorker(this.importApp, new File(this.selectedPathField.getText()), this.checkBoxDelete.isSelected(), this.printstextArea);
                iw.addPropertyChangeListener(this);
                iw.execute();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "The images or directory are not in valid format for loading", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().getClass().equals(ImportWorker.class)
                && evt.getPropertyName().equals("state")
                && evt.getNewValue() == SwingWorker.StateValue.DONE) {

            boolean checkAdd = false;

            ImportWorker rw = (ImportWorker) evt.getSource();
            List<String[]> v = rw.getReturnValue();

            if (v != null) {
                MetadataDBFunctions mdf = new MetadataDBFunctions();

                for (int i = 0; i < v.size(); i++) {
                    String[] datasetFeatures = v.get(i);

                    String organism = datasetFeatures[0];
                    String experiment = datasetFeatures[1];
                    String seed = datasetFeatures[2];
                    String timepoint = datasetFeatures[3];
                    String imageType = datasetFeatures[4];
                    String userName = UserAccess.getCurrentUser();
                    if (mdf.addNewImageSet(organism, experiment, seed, timepoint, imageType, userName)) {
                        checkAdd = true;
                    }
                }
            }

            String s = "ERROR";
            if (checkAdd == true) {
                s = "DONE";
                firePropertyChange("getall", null, null);
            }
            this.processingField.setText(s);
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
        panel1.setPreferredSize(new Dimension(600, 300));
        jsp = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(jsp, gbc);
        printstextArea = new JTextArea();
        printstextArea.setEditable(false);
        jsp.setViewportView(printstextArea);
        final JLabel label1 = new JLabel();
        label1.setText("Imageset directory path:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 0);
        panel1.add(label1, gbc);
        selectedPathField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(selectedPathField, gbc);
        browseButton = new JButton();
        browseButton.setText("Browse...");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(browseButton, gbc);
        importButton = new JButton();
        Font importButtonFont = this.$$$getFont$$$(null, Font.BOLD, -1, importButton.getFont());
        if (importButtonFont != null) importButton.setFont(importButtonFont);
        importButton.setText("Import");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(importButton, gbc);
        processingField = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(processingField, gbc);
        statusLabel = new JLabel();
        statusLabel.setText("Import status:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 0);
        panel1.add(statusLabel, gbc);
        directoryInfo = new JLabel();
        directoryInfo.setToolTipText("<html>\nThe rsa-data user (which is a member of the rootarch group) needs <strong>read</strong> and <strong>execute</strong> permissions on the directory, and <strong>read</strong> permissions on every file in the directory.<br />\nA couple of alternative ways to do this:\n<ol>\n<li>For the directory and the files in it, change the owner to rsa-data and give these permissions to the owner.</li>\n<li>For the directory and the files in it, change the group to rootarch and give these permissions to the group.</li>\n</ol>\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(directoryInfo, gbc);
        deletionInfo = new JLabel();
        deletionInfo.setToolTipText("<html>\nThe rsa-data user (which is a member of the rootarch group) needs <strong>write</strong> permissions on the directory, in addition to the permissions above.<br />\nA couple of alternative ways to do this:\n<ol>\n<li>For the directory, change the owner to rsa-data and give these permissions to the owner.</li>\n<li>For the directory, change the group to rootarch and give these permissions to the group.</li>\n</ol>\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(deletionInfo, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Delete source files after moving:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(label2, gbc);
        checkBoxDelete = new JCheckBox();
        checkBoxDelete.setSelected(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(checkBoxDelete, gbc);
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
