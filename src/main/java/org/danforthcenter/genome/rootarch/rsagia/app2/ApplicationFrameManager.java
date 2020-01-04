/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.beans.PropertyChangeEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import org.danforthcenter.genome.rootarch.rsagia2.*;

/**
 * Given a list of inputs and apps to run, this class handles executing the
 * corresponding configuration and run frames.
 * 
 * @author bm93, vp23
 */
public class ApplicationFrameManager extends JComponent implements
		java.beans.PropertyChangeListener {
	protected MainFrame mfrm;
	protected ApplicationManager am;
	protected ArrayList<String> appNames;
	protected int curApp;
	protected ArrayList<RsaImageSet> inputs;
	protected ScaleAllPanel sp;
	protected ImageManipulationFrame spImf;
	protected int spCount;
	protected CompositeImageFrame cif;
	protected CropPanelManager cpm;
	protected GiaRoot2DConfigurationFrame grcf;
	protected ChooseOutputFrame cof;
	protected String giaTemplate;
	protected ArrayList<OutputInfo> giaManualCrops;
	protected String giaDescriptors;
	protected GiaRoot2DFrame giaFrame;
	protected Rootwork3DFrame_new rootf;
    protected Rootwork3DPersFrame_new rootfpers;
	protected ReviewFrame2 rf;
	protected ExportFrameManager efm;
	protected ChooseOutputFrame gia3DCof;
	protected GiaRoot3DFrame gia3DFrame;
	protected ArrayList<OutputInfo> gia3DVols;
	protected GiaRoot3DLogFrame gia3DLogFrame;
	protected QualityControlFrameManager qcfm;
	protected QualityControlFrameManager qcfm2;
	protected QualityControlFrameManager qcfm3;

	protected ArrayList<OutputInfo> gia3D_v2Vols;
	protected ChooseOutputFrame gia3D_v2Cof;
	protected Gia3D_v2Frame gia3D_v2Frame;
	protected Gia3D_v2LogFrame gia3D_v2LogFrame;

	public ApplicationFrameManager(MainFrame mfrm, ApplicationManager am,
			ArrayList<String> appNames, ArrayList<RsaImageSet> inputs) {
		this.mfrm = mfrm;
		this.am = am;
		this.appNames = appNames;
		this.curApp = 0;
		this.inputs = inputs;
	}

	public void run() {

		RisPreprocessFrame rpf = new RisPreprocessFrame();
		rpf.setCount(1, inputs.size());
		rpf.setVisible(true);
		for (int i = 0; i < inputs.size(); i++) {
			inputs.get(i).preprocess();
			rpf.setCount(i, inputs.size());
		}
		rpf.dispose();

		curApp = -1;
		doNext();
	}

	private void doNext() {
		curApp++;
		if (curApp < appNames.size()) {
			String s = appNames.get(curApp);
			Scale scale = am.getScale();
			Crop crop = am.getCrop();
			Crop recrop = am.getRecrop();
			GiaRoot2D gia = am.getGiaRoot2D();
			Rootwork3D rootwork3D = am.getRootwork3D();
            Rootwork3DPers rootwork3DPers = am.getRootwork3DPers();
			GiaRoot3D gia3D = am.getGiaRoot3D();
			Gia3D_v2 gia3D_v2 = am.getGia3D_v2();
			if (s.equals(scale.getName())) {
                System.out.println(this.getClass() + " " + inputs.size());
                runScale(inputs, scale.getName());
			} else if (s.equals(crop.getName())) {
				cif = new CompositeImageFrame(inputs, null, crop, am);
				cif.addPropertyChangeListener("done", this);
				cif.setLocationRelativeTo(null);
				cif.setVisible(true);
			} else if (s.equals(recrop.getName())) {
				chooseCrop(am.getRecrop().getRecrop());
			} else if (s.equals(gia.getName())) {
				grcf = new GiaRoot2DConfigurationFrame(gia,
						gia.getAllDescriptors());
				grcf.addPropertyChangeListener("done", this);
				grcf.setVisible(true);
			} else if (s.equals(rootwork3D.getName())) {
				rootf = new Rootwork3DFrame_new(am, inputs);
				rootf.addPropertyChangeListener("done", this);
				rootf.setLocationRelativeTo(null);
				rootf.setVisible(true);
            } else if (s.equals(rootwork3DPers.getName())) {
                rootfpers = new Rootwork3DPersFrame_new(am, inputs);
                rootfpers.setLocationRelativeTo(null);
                rootfpers.addPropertyChangeListener("done", this);
                rootfpers.setVisible(true);
			} else if (s.equals("review")) {
				rf = new ReviewFrame2(inputs, am);
				rf.addPropertyChangeListener("done", this);
				rf.setLocationRelativeTo(null);
				rf.setVisible(true);
			} else if (s.equals("export")) {
				efm = new ExportFrameManager(am.getExport(), am.getScale(),
						am.getGiaRoot2D(), inputs, am);
				efm.run();
			} else if (s.equals("qc")) {
				qcfm = new QualityControlFrameManager(am.getQc(), null, null,
						am.getGiaRoot2D(), am.getRootwork3D(), inputs, am);
				qcfm.run();
			} else if (s.equals("qc2")) {
                System.out.println(this.getClass() + " qc2 selected " + inputs.get(0).getProcessedDir() );
				qcfm2 = new QualityControlFrameManager(null, am.getQc2(), null,
						am.getGiaRoot2D(), am.getRootwork3D(), inputs, am);
				qcfm2.run();
			} else if (s.equals("qc3")) {
				// /allow qc3 only on bio-ross///////////////////
				String Msg = "";
				// String hostname = null;
				//
				String hostname = "TEST_VIRTUAL_MACHINE";
				// try{
				// hostname = getHostName();
				// }
				// catch(UnknownHostException e){
				// Msg = e.getMessage();
				// JOptionPane.showMessageDialog(mfrm, Msg);
				// return;
				// }
				if (hostname != null
						&& hostname
								.equalsIgnoreCase("bio-busch.biology.duke.edu")) {
					Msg = "QC3 feature not supported "
							+ "on bio-busch.biology.duke.edu \n "
							+ "Use bio-ross.biology.duke.edu instead.";
					JOptionPane.showMessageDialog(mfrm, Msg);
					return;
				}
				// /////////////////

				qcfm3 = new QualityControlFrameManager(null, null, am.getQc3(),
						am.getGiaRoot2D(), am.getRootwork3D(), inputs, am);
				qcfm3.run();
			} else if (s.equals(gia3D.getName())) {
				HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
				for (RsaImageSet ris : inputs) {
					ArrayList<OutputInfo> ois = OutputInfo.getInstances(am,
							ris, true, false, null, false);
					for (int i = ois.size() - 1; i >= 0; i--) {
						if ((ois.get(i).getOutputs() & gia3D
								.getRequiredInputs()) == 0) {
							ois.remove(i);
						}
					}
					map.put(ris, ois);
				}

				gia3DCof = new ChooseOutputFrame(map, true, am, true, false);
				gia3DCof.setInfoText("Choose the 3D volume to use (one per image set): ");
				gia3DCof.setTitle("GiaRoot3D: Choose Volume");
				gia3DCof.addPropertyChangeListener("done", this);
				gia3DCof.setVisible(true);
			} else if (s.equals(gia3D_v2.getName())) {
				HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
				for (RsaImageSet ris : inputs) {
					ArrayList<OutputInfo> ois = OutputInfo.getInstances(am,
							ris, true, false, null, false);
					for (int i = ois.size() - 1; i >= 0; i--) {
						if ((ois.get(i).getOutputs() & gia3D_v2
								.getRequiredInputs()) == 0) {
							ois.remove(i);
						}
					}
					map.put(ris, ois);
				}

				gia3D_v2Cof = new ChooseOutputFrame(map, true, am, true, false);
				gia3D_v2Cof
						.setInfoText("Choose the 3D volume to use (one per image set): ");
				gia3D_v2Cof.setTitle("gia3D_v2: Choose Volume");
				gia3D_v2Cof.addPropertyChangeListener("done", this);
				gia3D_v2Cof.setVisible(true);
			}
		}

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == sp) {
			if ((Integer) evt.getNewValue() == spCount) {
				spImf.dispose();
				spImf = null;
				sp = null;
				firePropertyChange("update", false, true);
				doNext();

			}
		} else if (evt.getSource() == cif) {
			ArrayList<OutputInfo> outputs = cif.getOutputs();
			ArrayList<OutputInfo> recropinputs = cif.getRecropInputs();

			Crop crop = null;
			if (cif.isRecrop()) {
				crop = am.getRecrop();
			} else {
				crop = am.getCrop();
			}

			cif.dispose();
			cif = null;

			cpm = new CropPanelManager(inputs, recropinputs, outputs, crop, am);
			cpm.addPropertyChangeListener("done", this);
			cpm.run();
		} else if (evt.getSource() == cpm) {
			cpm = null;
			firePropertyChange("update", false, true);
			doNext();
		} else if (evt.getSource() == grcf) {
			giaTemplate = grcf.getGiaTemplate();
			giaDescriptors = grcf.getDescriptors();
			grcf.dispose();
			grcf = null;
			chooseCrop(am.getCrop().getRecrop());
		} else if (evt.getSource() == cof) {
			giaManualCrops = cof.getOutputs();
			//
			// Assumption:
			//
			// cof.getOneOutputOnly()=true
			// if and only if
			// the Gia is processed
			//
			// So,
			// cof.getOneOutputOnly()=true
			// indicates that we proceed with Gia,
			// cof.getOneOutputOnly()=true
			// indicates that we proceed with Recrop
			if (cof.getOneOutputOnly()) {
				// Gia
				cof.dispose();
				cof = null;
				doGia();
			} else {
				// Recrop
				cof.dispose();
				cof = null;
				Crop recrop = am.getRecrop();
				cif = new CompositeImageFrame(inputs, giaManualCrops, recrop,
						am);
				cif.addPropertyChangeListener("done", this);
				cif.setVisible(true);
			}
		} else if (evt.getSource() == rf) {
			rf.dispose();
			rf = null;
			firePropertyChange("update", false, true);
		} else if (evt.getSource() == giaFrame) {
			giaFrame.dispose();
			giaFrame = null;
			firePropertyChange("update", false, true);
		} else if (evt.getSource() == efm) {
			efm = null;
			doNext();
		} else if (evt.getSource() == this.rootf) {
			rootf.dispose();
			rootf = null;
			firePropertyChange("update", false, true);
			doNext();
        } else if (evt.getSource() == this.rootfpers) {
            rootfpers.dispose();
            rootfpers = null;
			firePropertyChange("update", false, true);
            doNext();
		} else if (evt.getSource() == gia3DCof) {
			gia3DVols = gia3DCof.getOutputs();
			gia3DCof.dispose();
			gia3DCof = null;

			gia3DFrame = new GiaRoot3DFrame(am.getGiaRoot3D().getDescriptors(),
					am.getGiaRoot3D().getConfigs());
			gia3DFrame.addPropertyChangeListener("done", this);
			gia3DFrame.setVisible(true);
		} else if (evt.getSource() == gia3DFrame) {
			String descriptors = gia3DFrame.getDescriptors();
			String config = gia3DFrame.getConfig();
			gia3DFrame.dispose();
			gia3DFrame = null;

			ArrayList<IOutputVolume3D> vols = new ArrayList<IOutputVolume3D>();
			ArrayList<RsaImageSet> riss = new ArrayList<RsaImageSet>();
			for (int i = 0; i < gia3DVols.size(); i++) {
				vols.add((IOutputVolume3D) gia3DVols.get(i));
				riss.add(gia3DVols.get(i).getRis());
			}
			gia3DVols = null;

			int maxProcesses = AdminFrameNew.AdminSettings.getMaxProcesses();
			gia3DLogFrame = new GiaRoot3DLogFrame(maxProcesses,
					am.getGiaRoot3D(), am, riss, vols, descriptors, config);
			gia3DLogFrame.addPropertyChangeListener("done", this);
			gia3DLogFrame.setLocationRelativeTo(null);
			gia3DLogFrame.setVisible(true);
		} else if (evt.getSource() == gia3DLogFrame) {
			gia3DLogFrame.dispose();
			gia3DLogFrame = null;
			firePropertyChange("update", false, true);
			doNext();
		} else if (evt.getSource() == gia3D_v2Cof) {
			gia3D_v2Vols = gia3D_v2Cof.getOutputs();
			gia3D_v2Cof.dispose();
			gia3D_v2Cof = null;

			// gia3D_v2Frame = new
			// Gia3D_v2Frame(am.getGia3D_v2().getDescriptors(),
			// am.getGia3D_v2().getConfigs());
			//
			// Pass descriptors_view instead of descriptors.
			//
			// Currently, descriptors configuration on Gia3D_v2_Frame
			// does not work for Skeleton Root3D - one can move around
			// descriptors, but it is only cosmetics.
			// Moreover, Gia2D and Gia3D similar configurations do no work too,
			// though it looks like gia-job.xml files are properly changed
			// and passed to Gia. But the restult giaroot_3d.csv still
			// contains calculatios for ALL descriptors
			gia3D_v2Frame = new Gia3D_v2Frame(am.getGia3D_v2()
					.getDescriptorsView(), am.getGia3D_v2().getConfigs());

			gia3D_v2Frame.addPropertyChangeListener("done", this);
			gia3D_v2Frame.setLocationRelativeTo(null);
			gia3D_v2Frame.setVisible(true);
		} else if (evt.getSource() == gia3D_v2Frame) {
			String descriptors = gia3D_v2Frame.getDescriptors();
			String config = gia3D_v2Frame.getConfig();
			gia3D_v2Frame.dispose();
			gia3D_v2Frame = null;

			ArrayList<IOutputVolume3D> vols = new ArrayList<IOutputVolume3D>();
			ArrayList<RsaImageSet> riss = new ArrayList<RsaImageSet>();
			for (int i = 0; i < gia3D_v2Vols.size(); i++) {
				vols.add((IOutputVolume3D) gia3D_v2Vols.get(i));
				riss.add(gia3D_v2Vols.get(i).getRis());
			}
			gia3D_v2Vols = null;

			int maxProcesses = AdminFrameNew.AdminSettings.getMaxProcesses();
			gia3D_v2LogFrame = new Gia3D_v2LogFrame(maxProcesses,
					am.getGia3D_v2(), am, riss, vols, descriptors, config);
			gia3D_v2LogFrame.addPropertyChangeListener("done", this);
			gia3D_v2LogFrame.setLocationRelativeTo(null);
			gia3D_v2LogFrame.setVisible(true);
		} else if (evt.getSource() == gia3D_v2LogFrame) {
			gia3D_v2LogFrame.dispose();
			gia3D_v2LogFrame = null;
			firePropertyChange("update", false, true);
			doNext();
		}

	}

	protected void doGia() {
		HashMap<RsaImageSet, OutputInfo> hm = new HashMap<RsaImageSet, OutputInfo>();
		for (RsaImageSet ris : inputs) {
			hm.put(ris, null);
		}
		for (OutputInfo oi : giaManualCrops) {
			hm.put(oi.getRis(), oi);
		}

		ArrayList<GiaRoot2DInput> giaInputs = new ArrayList<GiaRoot2DInput>();
		for (Map.Entry<RsaImageSet, OutputInfo> ent : hm.entrySet()) {
			GiaRoot2DInput g = new GiaRoot2DInput(ent.getKey(), giaTemplate,
					ent.getValue(), am, giaDescriptors);
			giaInputs.add(g);
		}

		int maxProcesses = AdminFrameNew.AdminSettings.getMaxProcesses();
		giaFrame = new GiaRoot2DFrame(maxProcesses, am.getGiaRoot2D(),
				giaInputs, am);
		giaFrame.addPropertyChangeListener("done", this);
		giaFrame.setLocationRelativeTo(null);
		giaFrame.setVisible(true);
	}

	public void chooseCrop(boolean recrop) {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> hm = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		for (RsaImageSet r : inputs) {
			ArrayList<OutputInfo> withCrop = new ArrayList<OutputInfo>();
			ArrayList<OutputInfo> outputs = OutputInfo.getInstances(am, r,
					true, recrop, null, false);

			for (OutputInfo oi : outputs) {
				if (oi.isValid()
						&& ((oi.getOutputs() & InputOutputTypes.CROP) != 0)) {
					withCrop.add(oi);
				}
			}
			hm.put(r, withCrop);
		}

		cof = new ChooseOutputFrame(hm, !recrop, am, true, false);
		cof.setInfoText("Per image set, choose a cropped image to use: ");
		cof.addPropertyChangeListener("done", this);
		cof.setLocationRelativeTo(null);
		cof.setVisible(true);
	}

	public void runScale(ArrayList<RsaImageSet> riss, String s) {
		spCount = riss.size();
		spImf = new ImageManipulationFrame();
		spImf.setLocationRelativeTo(null); // Center the produced frame to scale image
		sp = new ScaleAllPanel(spImf, riss, (Scale) am.getApplicationByName(s), am);
		sp.addPropertyChangeListener("curIndex", this);
		spImf.setVisible(true);
	}

	public static String getHostName() throws UnknownHostException {
		String hostname = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipAddr = addr.getAddress();
			hostname = addr.getHostName();
			// System.out.println("hostname="+hostname);
		} catch (UnknownHostException e) {
			throw new UnknownHostException("Unknown host");
		}
		return hostname;
	}
}
