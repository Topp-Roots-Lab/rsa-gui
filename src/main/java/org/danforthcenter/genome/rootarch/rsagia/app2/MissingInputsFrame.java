package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.IApplication;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by Feray Demirci on 5/9/2017.
 */
public class MissingInputsFrame extends JDialog implements ActionListener, WindowListener {
    private boolean cancel;
    private RsaInputTable rsaTable;
    private ArrayList<Integer> badIndexes;
    private ArrayList<RsaImageSet> badImageSets;
    private JLabel missingText;
    private JButton removeAllButton;
    private JTable missingTable;
    private JPanel panel1;
    private JButton closeButton;

    public MissingInputsFrame(RsaInputTable rsaTable, String s, ApplicationManager am) {
        super(null, "Bad Datasets", ModalityType.APPLICATION_MODAL);
        this.cancel = false;
        this.rsaTable = rsaTable;
        this.badIndexes = new ArrayList<Integer>();
        this.badImageSets = new ArrayList<RsaImageSet>();
        this.getBadImageSets(s, am);
        if (this.badIndexes.size() > 0) {
            $$$setupUI$$$();
            this.getContentPane().add(panel1);
            missingText.setText("<html>Cannot run " + s + " on these. They are missing one or more required -valid- inputs (e.g., cropping for gia root, thresholding for rootwork)</html>");
            DefaultTableModel model = new DefaultTableModel();
            missingTable.setModel(model);
            model.addColumn("#");
            model.addColumn("Image Set");
            missingTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            missingTable.getColumnModel().getColumn(1).setPreferredWidth(260);

            int i = 0;
            for (RsaImageSet ris : this.badImageSets) {
                model.addRow(new Object[]{i + 1, ris});
                i = i + 1;
            }

            removeAllButton.addActionListener(this);
            closeButton.addActionListener(this);
            addWindowListener(this);

            pack();
            this.setResizable(false);
            this.setVisible(true);
        }
    }

    public void getBadImageSets(String s, ApplicationManager am) {
        IApplication app = am.getApplicationByName(s);
        ArrayList<Integer> rowIndexes = this.rsaTable.getCheckedRowIndexes();
        ArrayList<RsaImageSet> inputData = this.rsaTable.getInputData();
        for (int i : rowIndexes) {
            RsaImageSet ris = inputData.get(i);
            if (s.equals("export")) {
                if (!am.getExport().hasRequiredInput(ris, am)) {
                    this.badIndexes.add(i);
                    this.badImageSets.add(ris);
                }
            } else if (s.equals("qc")) {
                if (!am.getQc().hasRequiredInput(ris, am)) {
                    this.badIndexes.add(i);
                    this.badImageSets.add(ris);
                }
            } else if (s.equals("qc2")) {
                if (!am.getQc2().hasRequiredInput(ris, am)) {
                    this.badIndexes.add(i);
                    this.badImageSets.add(ris);
                }
            } else if (s.equals("qc3")) {
                if (!am.getQc3().hasRequiredInput(ris, am)) {
                    this.badIndexes.add(i);
                    this.badImageSets.add(ris);
                }
            } else if (!s.equals("review")) {
                if (!app.hasRequiredInput(ris, am)) {
                    this.badIndexes.add(i);
                    this.badImageSets.add(ris);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removeAllButton) {
            for (int i : badIndexes) {
                this.rsaTable.getModel().setValueAt(false, i, 0);
            }
            dispose();
        }
        if (e.getSource() == closeButton) {
            this.cancel = true;
            dispose();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.cancel = true;
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public boolean getCancel() {
        return this.cancel;
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
        panel1.setMaximumSize(new Dimension(340, 252));
        panel1.setMinimumSize(new Dimension(340, 252));
        panel1.setPreferredSize(new Dimension(340, 252));
        removeAllButton = new JButton();
        removeAllButton.setText("Skip these and Continue");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(removeAllButton, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(280, 100));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(scrollPane1, gbc);
        missingTable = new JTable();
        missingTable.setDragEnabled(false);
        missingTable.setIntercellSpacing(new Dimension(5, 1));
        scrollPane1.setViewportView(missingTable);
        closeButton = new JButton();
        closeButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(closeButton, gbc);
        missingText = new JLabel();
        missingText.setMaximumSize(new Dimension(280, 60));
        missingText.setMinimumSize(new Dimension(280, 60));
        missingText.setPreferredSize(new Dimension(280, 60));
        missingText.setText("label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(missingText, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
