package org.danforthcenter.genome.rootarch.rsagia.app2;

import javax.swing.*;
import java.awt.*;

import org.danforthcenter.genome.rootarch.rsagia2.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Feray Demirci on 6/22/2017.
 */
public class Rootwork3DPersFrame_new extends JFrame implements
        ActionListener, PropertyChangeListener {
    private ApplicationManager am;
    private ArrayList<RsaImageSet> riss;
    private ChooseOutputFrame cofScale;
    private ChooseOutputFrame cofGia;
    private Rootwork3DPersLogFrame rlf;

    private ArrayList<OutputInfo> scales = new ArrayList<OutputInfo>();
    private ArrayList<RsaImageSet> scaleInputs = new ArrayList<RsaImageSet>();


    private JTextField nodesOctreeField;
    private JTextField imagesUsedField;
    private JTextField reconOptionField;
    private JTextField distortionRadiusField;
    private JTextField numComponentsField;
    private JTextField resolutionField;
    private JTextField camDistField;
    private JTextField rotDirField;
    private JTextField doFindRotAxisField;
    private JTextField doCalibField;
    private JTextField pitchField;
    private JTextField rollField;
    private JTextField translationField;
    private JTextField focusOffsetField;
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
    private JLabel jLabel9;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;

    private JTextField doAddField;
    private JLabel jLabel17;

    private JButton nextButton;
    private JButton cancelButton;
    private JPanel panel1;

    /**
     * Creates new form Rootwork3DFrame
     */
    public Rootwork3DPersFrame_new(ApplicationManager am, ArrayList<RsaImageSet> riss) {
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

    // tw 2015july1
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
//                getThresholdInput();
            getScaleInput();
        } else if (e.getSource() == cancelButton) {
            // TODO: clean up first if needed
            cancel();

            // close this window
            closeWindow();
        }
    }

    // tw 2015july1
    public void getThresholdInput() {
        HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
        for (RsaImageSet ris : riss) {
            ArrayList<OutputInfo> descs = new ArrayList<OutputInfo>();
            map.put(ris, descs);
            ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris, true, false, null, false);
            for (OutputInfo oi : ois) {
                if (oi.isValid()
                        && (oi.getOutputs() & InputOutputTypes.THRESHOLD) > 0) {
                    descs.add(oi);
                }
            }
        }
        this.setVisible(false);
        cofGia = new ChooseOutputFrame(map, true, am, true, false);
        cofGia.addPropertyChangeListener("done", this);
        cofGia.setVisible(true);

    }

    // tw 2015july1
    public void getScaleInput() {
        HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
        for (RsaImageSet ris : riss) {
            ArrayList<OutputInfo> descs = new ArrayList<OutputInfo>();
            map.put(ris, descs);
            ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris,
                    true, false, null, false);
            for (OutputInfo oi : ois) {
                if (oi.isValid()
                        && (oi.getOutputs() & InputOutputTypes.SCALE) > 0) {
                    System.out.println(this.getClass().getSimpleName() + " " + oi.getDir().toString());
                    descs.add(oi);
                }
            }
        }
        this.setVisible(false);
        cofScale = new ChooseOutputFrame(map, true, am, true, false);
        cofScale.addPropertyChangeListener("done", this);
        cofScale.setLocationRelativeTo(null);
        cofScale.setVisible(true);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {


        if (evt.getSource() == cofScale && evt.getPropertyName().equals("done")
                && (Boolean) evt.getNewValue()) {

            // tw 2015july14 since cof sets oneOutputOnly = true above, ois.size should = 1
            // all of the ArrayList manipulation here and later can be simplified to singe instance

            ArrayList<OutputInfo> oisScale = cofScale.getOutputs();

            for (int i = 0; i < oisScale.size(); i++) {
                System.out.println(this.getClass().getSimpleName() + " oisScale " + oisScale.get(i).getDir());
                scales.add((OutputInfo) oisScale.get(i));
//                scaleInputs.add(oisScale.get(i).getRis());
            }

            getThresholdInput();
            cofScale.dispose();
            cofScale = null;
        } else if (evt.getSource() == cofGia && evt.getPropertyName().equals("done")
                && (Boolean) evt.getNewValue()) {

            ArrayList<IOutputThreshold> thresholds = new ArrayList<IOutputThreshold>();
            ArrayList<RsaImageSet> giaInputs = new ArrayList<RsaImageSet>();

            ArrayList<OutputInfo> oisThreshold = cofGia.getOutputs();

            System.out.println(this.getClass().getSimpleName() + " ois.size " + oisThreshold.size());
            System.out.println(this.getClass().getSimpleName() + " scales(0) " + scales.get(0));
            // tw 2015july14 since cof sets oneOutputOnly = true above, ois.size should = 1
            // all of the ArrayList manipulation here and later can be simplified to singe instance


            for (int i = 0; i < oisThreshold.size(); i++) {
                thresholds.add((IOutputThreshold) oisThreshold.get(i));
                giaInputs.add(oisThreshold.get(i).getRis());
            }

            cofGia.dispose();
            cofGia = null;

            int maxProcesses = AdminFrameNew.AdminSettings.getMaxProcesses();
            rlf = new Rootwork3DPersLogFrame(maxProcesses, am.getRootwork3DPers(), am, giaInputs, thresholds,
                    scales,
//                        Integer.parseInt(reconLowerThreshold.getText()),
                    Integer.parseInt(nodesOctreeField.getText()),
                    Integer.parseInt(imagesUsedField.getText()),
                    Integer.parseInt(reconOptionField.getText()),
//                        Integer.parseInt(reconUpperField.getText()),
                    Integer.parseInt(distortionRadiusField.getText()),
                    Integer.parseInt(numComponentsField.getText()),
                    Integer.parseInt(resolutionField.getText()),
                    Integer.parseInt(refImageField.getText()),
                    Double.parseDouble(refRatioField.getText()),
                    Integer.parseInt(camDistField.getText()),
                    Integer.parseInt(rotDirField.getText()),
                    doFindRotAxisField.getText(),
                    doCalibField.getText(),
                    Double.parseDouble(pitchField.getText()),
                    Double.parseDouble(rollField.getText()),
                    Integer.parseInt(translationField.getText()),
                    Integer.parseInt(focusOffsetField.getText()),
                    doAddField.getText()
            );
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
        panel1.setMinimumSize(new Dimension(460, 420));
        panel1.setPreferredSize(new Dimension(460, 420));
        jLabel1 = new JLabel();
        Font jLabel1Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel1.getFont());
        if (jLabel1Font != null) jLabel1.setFont(jLabel1Font);
        jLabel1.setText("Number Nodes Octree:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel1, gbc);
        jLabel2 = new JLabel();
        Font jLabel2Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel2.getFont());
        if (jLabel2Font != null) jLabel2.setFont(jLabel2Font);
        jLabel2.setText("Number Images Used:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel2, gbc);
        jLabel3 = new JLabel();
        Font jLabel3Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel3.getFont());
        if (jLabel3Font != null) jLabel3.setFont(jLabel3Font);
        jLabel3.setText("Recon Option:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel3, gbc);
        jLabel4 = new JLabel();
        Font jLabel4Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel4.getFont());
        if (jLabel4Font != null) jLabel4.setFont(jLabel4Font);
        jLabel4.setText("Distortion Radius:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel4, gbc);
        jLabel5 = new JLabel();
        Font jLabel5Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel5.getFont());
        if (jLabel5Font != null) jLabel5.setFont(jLabel5Font);
        jLabel5.setText("Number of Components:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel5, gbc);
        jLabel6 = new JLabel();
        Font jLabel6Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel6.getFont());
        if (jLabel6Font != null) jLabel6.setFont(jLabel6Font);
        jLabel6.setText("Resolution:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel6, gbc);
        jLabel7 = new JLabel();
        Font jLabel7Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel7.getFont());
        if (jLabel7Font != null) jLabel7.setFont(jLabel7Font);
        jLabel7.setText("Distance to Camera (mm):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel7, gbc);
        jLabel8 = new JLabel();
        Font jLabel8Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel8.getFont());
        if (jLabel8Font != null) jLabel8.setFont(jLabel8Font);
        jLabel8.setText("Direction of Rotation (-1 or 1):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel8, gbc);
        jLabel9 = new JLabel();
        Font jLabel9Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel9.getFont());
        if (jLabel9Font != null) jLabel9.setFont(jLabel9Font);
        jLabel9.setText("Find Rotation Axis (T/F):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel9, gbc);
        jLabel10 = new JLabel();
        Font jLabel10Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel10.getFont());
        if (jLabel10Font != null) jLabel10.setFont(jLabel10Font);
        jLabel10.setText("Use calibration (T/F):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel10, gbc);
        jLabel11 = new JLabel();
        Font jLabel11Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel11.getFont());
        if (jLabel11Font != null) jLabel11.setFont(jLabel11Font);
        jLabel11.setText("Pitch (deg):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel11, gbc);
        jLabel12 = new JLabel();
        Font jLabel12Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel12.getFont());
        if (jLabel12Font != null) jLabel12.setFont(jLabel12Font);
        jLabel12.setText("Roll (deg):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel12, gbc);
        jLabel13 = new JLabel();
        Font jLabel13Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel13.getFont());
        if (jLabel13Font != null) jLabel13.setFont(jLabel13Font);
        jLabel13.setText("Rotation axis correction (pixels):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(jLabel13, gbc);
        jLabel14 = new JLabel();
        Font jLabel14Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel14.getFont());
        if (jLabel14Font != null) jLabel14.setFont(jLabel14Font);
        jLabel14.setText("Focus offset (mm):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel14, gbc);
        jLabel15 = new JLabel();
        Font jLabel15Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel15.getFont());
        if (jLabel15Font != null) jLabel15.setFont(jLabel15Font);
        jLabel15.setText("Ref Image:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel15, gbc);
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
        camDistField = new JTextField();
        camDistField.setText("1000");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(camDistField, gbc);
        rotDirField = new JTextField();
        rotDirField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(rotDirField, gbc);
        doFindRotAxisField = new JTextField();
        doFindRotAxisField.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(doFindRotAxisField, gbc);
        doCalibField = new JTextField();
        doCalibField.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(doCalibField, gbc);
        pitchField = new JTextField();
        pitchField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pitchField, gbc);
        rollField = new JTextField();
        rollField.setText("0.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(rollField, gbc);
        translationField = new JTextField();
        translationField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(translationField, gbc);
        refImageField = new JTextField();
        refImageField.setEditable(false);
        refImageField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(refImageField, gbc);
        jLabel16 = new JLabel();
        Font jLabel16Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel16.getFont());
        if (jLabel16Font != null) jLabel16.setFont(jLabel16Font);
        jLabel16.setText("Ref Ratio:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel16, gbc);
        refRatioField = new JTextField();
        refRatioField.setEditable(false);
        refRatioField.setText("1");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(refRatioField, gbc);
        jLabel17 = new JLabel();
        Font jLabel17Font = this.$$$getFont$$$(null, Font.BOLD, -1, jLabel17.getFont());
        if (jLabel17Font != null) jLabel17.setFont(jLabel17Font);
        jLabel17.setText("Add Top Line:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(jLabel17, gbc);
        doAddField = new JTextField();
        doAddField.setText("T");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(doAddField, gbc);
        nextButton = new JButton();
        nextButton.setPreferredSize(new Dimension(90, 26));
        nextButton.setText("Next");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel1.add(nextButton, gbc);
        cancelButton = new JButton();
        cancelButton.setPreferredSize(new Dimension(90, 26));
        cancelButton.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 17;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 0);
        panel1.add(cancelButton, gbc);
        focusOffsetField = new JTextField();
        focusOffsetField.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(focusOffsetField, gbc);
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
