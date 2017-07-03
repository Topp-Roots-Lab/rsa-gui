package org.danforthcenter.genome.rootarch.rsagia.app2;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import org.danforthcenter.genome.rootarch.rsagia2.IOutputThreshold;
import org.danforthcenter.genome.rootarch.rsagia2.InputOutputTypes;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 * Created by Feray Demirci on 6/25/2017.
 */
public class Rootwork3DFrame_new extends JFrame implements
        ActionListener, PropertyChangeListener {
    protected ApplicationManager am;
    protected ArrayList<RsaImageSet> riss;
    protected ChooseOutputFrame cof;
    protected Rootwork3DLogFrame rlf;

    private JTextField nodesOctreeField;
    private JTextField imagesUsedField;
    private JTextField reconOptionField;
    private JTextField distortionRadiusField;
    private JTextField numComponentsField;
    private JTextField resolutionField;
    private JTextField refImageField;
    private JTextField refRatioField;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JButton nextButton;
    private JButton cancelButton;
    private JLabel jLabel9;
    private JTextField doAddField;
    private JPanel panel1;

    /**
     * Creates new form Rootwork3DFrame
     */
    public Rootwork3DFrame_new(ApplicationManager am, ArrayList<RsaImageSet> riss) {
        $$$setupUI$$$();
        this.am = am;
        this.riss = riss;
        DecimalInputVerifier div = new DecimalInputVerifier();
        nodesOctreeField.setInputVerifier(div);
        imagesUsedField.setInputVerifier(div);
        reconOptionField.setInputVerifier(div);

        nextButton.addActionListener(this);
        cancelButton.addActionListener(this);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
            for (RsaImageSet ris : riss) {
                ArrayList<OutputInfo> descs = new ArrayList<OutputInfo>();
                map.put(ris, descs);
                ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris,
                        true, false, null, false);
                for (OutputInfo oi : ois) {
                    if (oi.isValid()
                            && (oi.getOutputs() & InputOutputTypes.THRESHOLD) > 0) {
                        descs.add(oi);
                    }
                }
            }
            this.setVisible(false);
            cof = new ChooseOutputFrame(map, true, am, true, false);
            cof.addPropertyChangeListener("done", this);
            cof.setVisible(true);
        } else if (e.getSource() == cancelButton) {
            // TODO: clean up first if needed
            cancel();

            // close this window
            closeWindow();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == cof && evt.getPropertyName().equals("done")
                && (Boolean) evt.getNewValue()) {
            ArrayList<OutputInfo> ois = cof.getOutputs();
            ArrayList<IOutputThreshold> thresholds = new ArrayList<IOutputThreshold>();
            ArrayList<RsaImageSet> inputs = new ArrayList<RsaImageSet>();

            for (int i = 0; i < ois.size(); i++) {
                thresholds.add((IOutputThreshold) ois.get(i));
                inputs.add(ois.get(i).getRis());
            }

            cof.dispose();
            cof = null;
            int maxProcesses = AdminFrameNew.AdminSettings.getMaxProcesses();
            rlf = new Rootwork3DLogFrame(maxProcesses, am.getRootwork3D(), am,
                    inputs, thresholds,
//                            Integer.parseInt(reconLowerThreshold.getText()),
                    Integer.parseInt(nodesOctreeField.getText()),
                    Integer.parseInt(imagesUsedField.getText()),
                    Integer.parseInt(reconOptionField.getText()),
//                            Integer.parseInt(reconUpperField.getText()),
                    Integer.parseInt(distortionRadiusField.getText()),
                    Integer.parseInt(numComponentsField.getText()),
                    Integer.parseInt(resolutionField.getText()),
                    Integer.parseInt(refImageField.getText()),
                    Double.parseDouble(refRatioField.getText()), doAddField.getText());
            rlf.addPropertyChangeListener("done", this);
            rlf.setVisible(true);
        } else if (evt.getSource() == rlf
                && evt.getPropertyName().equals("done")
                && (Boolean) evt.getNewValue()) {
            rlf.dispose();
            rlf = null;
            this.firePropertyChange("done", false, true);
        }
    }

    private void cancel() {
        // TODO: clean up
    }

    private void closeWindow() {
        dispose();
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
        panel1.setMinimumSize(new Dimension(380, 250));
        panel1.setPreferredSize(new Dimension(380, 250));
        jLabel1 = new JLabel();
        jLabel1.setFont(new Font(jLabel1.getFont().getName(), Font.BOLD, jLabel1.getFont().getSize()));
        jLabel1.setText("Number Nodes Octree:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel1, gbc);
        jLabel2 = new JLabel();
        jLabel2.setFont(new Font(jLabel2.getFont().getName(), Font.BOLD, jLabel2.getFont().getSize()));
        jLabel2.setText("Number Images Used:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel2, gbc);
        jLabel3 = new JLabel();
        jLabel3.setFont(new Font(jLabel3.getFont().getName(), Font.BOLD, jLabel3.getFont().getSize()));
        jLabel3.setText("Recon Option:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel3, gbc);
        jLabel4 = new JLabel();
        jLabel4.setFont(new Font(jLabel4.getFont().getName(), Font.BOLD, jLabel4.getFont().getSize()));
        jLabel4.setText("Distortion Radius:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel4, gbc);
        jLabel5 = new JLabel();
        jLabel5.setFont(new Font(jLabel5.getFont().getName(), Font.BOLD, jLabel5.getFont().getSize()));
        jLabel5.setText("Number of Components:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(jLabel5, gbc);
        jLabel6 = new JLabel();
        jLabel6.setFont(new Font(jLabel6.getFont().getName(), Font.BOLD, jLabel6.getFont().getSize()));
        jLabel6.setText("Resolution:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel6, gbc);
        jLabel7 = new JLabel();
        jLabel7.setFont(new Font(jLabel7.getFont().getName(), Font.BOLD, jLabel7.getFont().getSize()));
        jLabel7.setText("Ref Image:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel7, gbc);
        jLabel8 = new JLabel();
        jLabel8.setFont(new Font(jLabel8.getFont().getName(), Font.BOLD, jLabel8.getFont().getSize()));
        jLabel8.setText("Ref Ratio:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel8, gbc);
        nodesOctreeField = new JTextField();
        nodesOctreeField.setText("10000000");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(nodesOctreeField, gbc);
        imagesUsedField = new JTextField();
        imagesUsedField.setText("20");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(imagesUsedField, gbc);
        reconOptionField = new JTextField();
        reconOptionField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(reconOptionField, gbc);
        distortionRadiusField = new JTextField();
        distortionRadiusField.setText("-1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(distortionRadiusField, gbc);
        numComponentsField = new JTextField();
        numComponentsField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(numComponentsField, gbc);
        resolutionField = new JTextField();
        resolutionField.setText("4");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(resolutionField, gbc);
        refImageField = new JTextField();
        refImageField.setEditable(false);
        refImageField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(refImageField, gbc);
        refRatioField = new JTextField();
        refRatioField.setEditable(false);
        refRatioField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(refRatioField, gbc);
        nextButton = new JButton();
        nextButton.setPreferredSize(new Dimension(80, 26));
        nextButton.setText("Next");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel1.add(nextButton, gbc);
        cancelButton = new JButton();
        cancelButton.setPreferredSize(new Dimension(80, 26));
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 0);
        panel1.add(cancelButton, gbc);
        jLabel9 = new JLabel();
        jLabel9.setFont(new Font(jLabel9.getFont().getName(), Font.BOLD, jLabel9.getFont().getSize()));
        jLabel9.setText("Add Top Line:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel9, gbc);
        doAddField = new JTextField();
        doAddField.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(doAddField, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
