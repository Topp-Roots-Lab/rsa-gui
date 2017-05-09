/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Rootwork3DFrame.java
 *
 * Created on Aug 13, 2010, 2:15:38 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia2.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bm93
 */
public class Rootwork3DPersFrame extends javax.swing.JFrame implements
	java.awt.event.ActionListener, java.beans.PropertyChangeListener {

	protected ApplicationManager am;
	protected ArrayList<RsaImageSet> riss;
	protected ChooseOutputFrame cofScale;
    protected ChooseOutputFrame cofGia;
	protected Rootwork3DPersLogFrame rlf;

    protected ArrayList<OutputInfo> scales = new ArrayList<OutputInfo>();
    protected ArrayList<RsaImageSet> scaleInputs = new ArrayList<RsaImageSet>();


	/** Creates new form Rootwork3DFrame */
	public Rootwork3DPersFrame(ApplicationManager am, ArrayList<RsaImageSet> riss) {
		initComponents();
		this.am = am;
		this.riss = riss;
		DecimalInputVerifier div = new DecimalInputVerifier();
//		reconLowerThreshold.setInputVerifier(div);
		nodesOctreeField.setInputVerifier(div);
		imagesUsedField.setInputVerifier(div);
		reconOptionField.setInputVerifier(div);
//		reconUpperField.setInputVerifier(div);

		nextButton.addActionListener(this);
		cancelButton.addActionListener(this);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

    // tw 2015july1

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
    public void getThresholdInput(){
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
    public void getScaleInput(){
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
        cofScale.setVisible(true);

    }


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
        }
		else if (evt.getSource() == cofGia && evt.getPropertyName().equals("done")
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
                    Integer.parseInt(focusOffsetField.getText())
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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

//		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
//		reconLowerThreshold = new javax.swing.JTextField();
		nodesOctreeField = new javax.swing.JTextField();
		imagesUsedField = new javax.swing.JTextField();
		reconOptionField = new javax.swing.JTextField();
		nextButton = new javax.swing.JButton();
//		jLabel5 = new javax.swing.JLabel();
//		reconUpperField = new javax.swing.JTextField();
		jLabel6 = new javax.swing.JLabel();
		distortionRadiusField = new javax.swing.JTextField();
		numComponentsField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		resolutionField = new javax.swing.JTextField();
		jLabel8 = new javax.swing.JLabel();
		refImageField = new javax.swing.JTextField();
		refRatioField = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		cancelButton = new javax.swing.JButton();

		jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
		camDistField = new javax.swing.JTextField();
        rotDirField = new javax.swing.JTextField();
        pitchField = new javax.swing.JTextField();
        rollField = new javax.swing.JTextField();

		jLabel11.setText("Distance to Camera (mm):");
        jLabel12.setText("Direction of Rotation (-1 or 1):");
        jLabel13.setText("Pitch (deg):");
        jLabel14.setText("Roll (deg):");

        camDistField.setText("1000");
        rotDirField.setText("1");
        pitchField.setText("0.0");
        rollField.setText("0.0");

        jLabel15 = new javax.swing.JLabel();
        jLabel15.setText("Use calibration (T/F):");
        doCalibField = new javax.swing.JTextField();
        doCalibField.setText("T");

        jLabel16 = new javax.swing.JLabel();
        jLabel16.setText("Rotation axis correction (pixels):");
        translationField = new javax.swing.JTextField();
        translationField.setText("0");

        jLabel17 = new javax.swing.JLabel();
        jLabel17.setText("Focus offset (mm):");
        focusOffsetField = new javax.swing.JTextField();
        focusOffsetField.setText("0");

        jLabel18 = new javax.swing.JLabel();
        jLabel18.setText("Find Rotation Axis (T/F):");
        doFindRotAxisField = new javax.swing.JTextField();
        doFindRotAxisField.setText("T");


//		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

//		jLabel1.setText("Recon Lower Threshold:");
		jLabel2.setText("Number Nodes Octree:");
		jLabel3.setText("Number Images Used:");
		jLabel4.setText("Recon Option:");

//		reconLowerThreshold.setText("19");

		nodesOctreeField.setText("10000000");
		imagesUsedField.setText("20");
		reconOptionField.setText("1");
		nextButton.setText("Next");

//		jLabel5.setText("Recon Upper Threshold:");

//		reconUpperField.setText("20");

		jLabel6.setText("Distortion Radius:");

		distortionRadiusField.setText("-1");

		numComponentsField.setText("1");

		jLabel7.setText("Number of Components:");

		resolutionField.setText("4");

		jLabel8.setText("Resolution:");

		refImageField.setBackground(new java.awt.Color(222, 223, 227));
		refImageField.setEditable(false);
		refImageField.setText("1");

		refRatioField.setBackground(new java.awt.Color(222, 223, 227));
		refRatioField.setEditable(false);
		refRatioField.setText("1");

		jLabel9.setText("Ref Image:");

		jLabel10.setText("Ref Ratio:");

		cancelButton.setText("Cancel");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        jLabel2)
//																				.addComponent(
//																						jLabel1)
//																				.addComponent(
//																						jLabel5)
                                                                                .addComponent(
                                                                                        jLabel3)
                                                                                .addComponent(
                                                                                        jLabel4)
                                                                                .addComponent(
                                                                                        jLabel6)
                                                                                .addComponent(
                                                                                        jLabel11)
                                                                                .addComponent(
                                                                                        jLabel12)
                                                                                .addComponent(
                                                                                        jLabel18)
                                                                                .addComponent(
                                                                                        jLabel15)
                                                                                .addComponent(
                                                                                        jLabel13)
                                                                                .addComponent(
                                                                                        jLabel14)
                                                                                .addComponent(
                                                                                        jLabel16)
                                                                                .addComponent(
                                                                                        jLabel17)
                                                                                .addComponent(
                                                                                        jLabel7)
                                                                                .addComponent(
                                                                                        jLabel8))
                                                                .addGap(18, 18,
                                                                        18)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        resolutionField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        distortionRadiusField,
                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        reconOptionField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        imagesUsedField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
//																				.addComponent(
//																						reconLowerThreshold,
//																						javax.swing.GroupLayout.DEFAULT_SIZE,
//																						229,
//																						Short.MAX_VALUE)
//																				.addComponent(
//																						reconUpperField,
//																						javax.swing.GroupLayout.DEFAULT_SIZE,
//																						229,
//																						Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        nodesOctreeField,
                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        numComponentsField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        camDistField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        rotDirField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        doFindRotAxisField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        doCalibField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        pitchField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        rollField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        translationField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        focusOffsetField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        229,
                                                                                        Short.MAX_VALUE)
                                                                ))
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addComponent(
                                                                                        jLabel9,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        jLabel10,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE))
                                                                .addGap(81, 81,
                                                                        81)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        refRatioField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        254,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        refImageField,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        254,
                                                                                        Short.MAX_VALUE)))
                                                .addGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        nextButton,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        85,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(
                                                                        cancelButton,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        85,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
//								.addGroup(
//										layout.createParallelGroup(
//												javax.swing.GroupLayout.Alignment.BASELINE)
//												.addComponent(jLabel1)
//												.addComponent(
//														reconLowerThreshold,
//														javax.swing.GroupLayout.PREFERRED_SIZE,
//														javax.swing.GroupLayout.DEFAULT_SIZE,
//														javax.swing.GroupLayout.PREFERRED_SIZE))
//								.addPreferredGap(
//										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//								.addGroup(
//										layout.createParallelGroup(
//												javax.swing.GroupLayout.Alignment.BASELINE)
//												.addComponent(
//														reconUpperField,
//														javax.swing.GroupLayout.PREFERRED_SIZE,
//														javax.swing.GroupLayout.DEFAULT_SIZE,
//														javax.swing.GroupLayout.PREFERRED_SIZE)
//												.addComponent(jLabel5))
//								.addPreferredGap(
//										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        nodesOctreeField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel2))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        imagesUsedField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel3))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        reconOptionField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel4))
                                .addGap(7, 7, 7)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        distortionRadiusField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        numComponentsField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel7))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        resolutionField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel8))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        camDistField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel11))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        rotDirField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel12))
                                .addGap(20, 20, 20)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        doFindRotAxisField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel18))

                                .addGap(20, 20, 20)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        doCalibField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel15))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        pitchField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel13))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        rollField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel14))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        translationField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel16))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        focusOffsetField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel17))


                                .addGap(20, 20, 20)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        refImageField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel9))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        refRatioField,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel10))
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        40, Short.MAX_VALUE)
                                .addGroup(
                                        layout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(cancelButton)
                                                .addComponent(
                                                        nextButton,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        29,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// new Rootwork3DFrame().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField distortionRadiusField;
	private javax.swing.JTextField imagesUsedField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JButton nextButton;
	private javax.swing.JTextField nodesOctreeField;
	private javax.swing.JTextField numComponentsField;
//	private javax.swing.JTextField reconLowerThreshold;
	private javax.swing.JTextField reconOptionField;
//	private javax.swing.JTextField reconUpperField;
	private javax.swing.JTextField refImageField;
	private javax.swing.JTextField refRatioField;
	private javax.swing.JTextField resolutionField;
	// End of variables declaration//GEN-END:variables

    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;

    private javax.swing.JTextField camDistField;
    private javax.swing.JTextField rotDirField;
    private javax.swing.JTextField pitchField;
    private javax.swing.JTextField rollField;
    private javax.swing.JTextField doCalibField;
    private javax.swing.JTextField translationField;
    private javax.swing.JTextField focusOffsetField;
    private javax.swing.JTextField doFindRotAxisField;

}
