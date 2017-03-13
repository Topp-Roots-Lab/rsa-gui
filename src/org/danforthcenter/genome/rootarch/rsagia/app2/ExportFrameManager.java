/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.ConstantScale;
import org.danforthcenter.genome.rootarch.rsagia2.Export;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2D;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputDescriptors3D;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputDescriptorsQc2;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputDescriptorsQc3;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputDescriptorsSkeleton3D;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputScale;
import org.danforthcenter.genome.rootarch.rsagia2.InputOutputTypes;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.danforthcenter.genome.rootarch.rsagia2.Scale;

/**
 *
 * @author bm93
 */
public class ExportFrameManager implements java.beans.PropertyChangeListener {
	protected Export export;
	protected Scale scale;
	protected GiaRoot2D giaRoot;
	protected ArrayList<RsaImageSet> inputs;
	protected ApplicationManager am;

	ArrayList<OutputInfo> scales;
	ArrayList<OutputInfo> gias;
	protected HashMap<RsaImageSet, IOutputDescriptors3D> desc3ds;
	protected HashMap<RsaImageSet, IOutputDescriptorsSkeleton3D> descGia3d_v2s;
	protected HashMap<RsaImageSet, IOutputDescriptorsQc2> descQc2s;
	protected HashMap<RsaImageSet, IOutputDescriptorsQc3> descQc3s;
	protected ChooseOutputFrame cofScale;
	protected ChooseOutputFrame cofGia;
	protected ChooseOutputFrame cof3d;
	protected ChooseOutputFrame cofGia3d_v2;
	protected ChooseOutputFrame cofQc2;
	protected ChooseOutputFrame cofQc3;
	protected ExportDispatcherFrame edf;
	protected boolean cof3d_skipCheckFinallist = true;// not used
	// these params will be set later
	protected boolean cofGia_finallist_empty;// not used
	protected boolean cof3d_finallist_empty;// not used
	protected boolean cofSkel3d_finallist_empty;// not used
	protected boolean cofGia3d_v2_finallist_empty;// not used

	protected TreeMap<String, Boolean> expOptions;

	public ExportFrameManager(Export export, Scale scale, GiaRoot2D giaRoot,
			ArrayList<RsaImageSet> inputs, ApplicationManager am) {
		this.export = export;
		this.scale = scale;
		this.giaRoot = giaRoot;
		this.inputs = inputs;
		this.am = am;

		// //this.expOptions=expOptions;
		// // for test
		// expOptions = new TreeMap();
		// expOptions.put(am.getGiaRoot2D().getName(),true);
		// //expOptions.put(am.getGiaRoot3D().getName(),true);
		// expOptions.put(am.getSkeletonRoot3D().getName(),true);
		// expOptions.put(am.getGia3D_v2().getName(),true);

	}

	public void run() {
		// doScales();
		edf = new ExportDispatcherFrame();
		edf.addPropertyChangeListener("done", this);
		edf.setVisible(true);

	}

	protected void doScales() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiScales = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		scales = new ArrayList<OutputInfo>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.SCALE) > 0) {
					ss.add(oi);
				}
			}

			scales.add(null);
			multiScales.put(inputs.get(i), ss);
		}
		cofScale = new ChooseOutputFrame(multiScales, true, am, true, false);
		cofScale.setInfoText("Please select a single scale for the following:");
		cofScale.addPropertyChangeListener("done", this);
		cofScale.setVisible(true);
	}

	protected void doGias() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> multiGias = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		gias = new ArrayList<OutputInfo>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.DESCRIPTORS_2D) > 0) {
					ss.add(oi);
				}
			}

			gias.add(null);
			multiGias.put(inputs.get(i), ss);
		}
		cofGia = new ChooseOutputFrame(multiGias, true, am, true, true);
		cofGia.setInfoText("Please select a single 2D descriptor set for the following:");
		cofGia.addPropertyChangeListener("done", this);
		cofGia.setVisible(true);
	}

	protected void do3d() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.DESCRIPTORS_3D) > 0) {
					ss.add(oi);
				}
			}

			map.put(inputs.get(i), ss);
		}
		cof3d = new ChooseOutputFrame(map, true, am, true, true);
		cof3d.setInfoText("Please select a single 3D descriptor set for the following:");
		cof3d.addPropertyChangeListener("done", this);
		cof3d.setVisible(true);
	}

	protected void doGia3d_v2() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.DESCRIPTORS_GIA_3D_V2) > 0) {
					ss.add(oi);
				}
			}

			map.put(inputs.get(i), ss);
		}
		cofGia3d_v2 = new ChooseOutputFrame(map, true, am, true, true);
		cofGia3d_v2
				.setInfoText("Please select a single Gia3D_v2 descriptor set for the following:");
		cofGia3d_v2.addPropertyChangeListener("done", this);
		cofGia3d_v2.setVisible(true);
	}

	protected void doQc2() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.QC2) > 0) {
					ss.add(oi);
				}
			}

			map.put(inputs.get(i), ss);
		}
		cofQc2 = new ChooseOutputFrame(map, true, am, true, true);
		cofQc2.setInfoText("Please select a single Qc2 descriptor set for the following:");
		cofQc2.addPropertyChangeListener("done", this);
		cofQc2.setVisible(true);
	}

	protected void doQc3() {
		HashMap<RsaImageSet, ArrayList<OutputInfo>> map = new HashMap<RsaImageSet, ArrayList<OutputInfo>>();
		for (int i = 0; i < inputs.size(); i++) {
			ArrayList<OutputInfo> tmp = OutputInfo.getInstances(am,
					inputs.get(i), true, false, null, false);
			ArrayList<OutputInfo> ss = new ArrayList<OutputInfo>();
			for (OutputInfo oi : tmp) {
				if (oi.isValid()
						&& (oi.getOutputs() & InputOutputTypes.QC3) > 0) {
					ss.add(oi);
				}
			}

			map.put(inputs.get(i), ss);
		}
		cofQc3 = new ChooseOutputFrame(map, true, am, true, true);
		cofQc3.setInfoText("Please select a single Qc3 descriptor set for the following:");
		cofQc3.addPropertyChangeListener("done", this);
		cofQc3.setVisible(true);
	}

	protected void doExport() {
		javax.swing.JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Choose base file name for export");
		int v = jfc.showDialog(null, "Export");
		if (v == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();

			// remove a file extension
			if (f.getName().contains(".")) {
				String s = f.getAbsolutePath();
				f = new File(s.substring(0, s.lastIndexOf(".")));
			}

			File f2 = new File(jfc.getSelectedFile().getAbsolutePath()
					+ "_raw.csv");
			File f3 = new File(jfc.getSelectedFile().getAbsolutePath() + ".csv");
			File f4 = new File(jfc.getSelectedFile().getAbsolutePath()
					+ "_3d.csv");
			File f6 = new File(jfc.getSelectedFile().getAbsolutePath()
					+ "_gia3d_v2.csv");
			File f7 = new File(jfc.getSelectedFile().getAbsolutePath()
					+ "_qc2.csv");
			File f8 = new File(jfc.getSelectedFile().getAbsolutePath()
					+ "_qc3.csv");

			// for some reason,even in the GUI the finallist
			// is empty, the corresponding parameter ...._empty
			// does not reflect this -- needs to be fixed
			// For the time being,the root_2d (now fixed), root_3d and skel_3d
			// export
			// routines do not write data, write only headers, which is
			// a satisfactory behaviour for now.

			// 2D
			//
			// this is such a hack - this needs to be put into a SwingWorker
			// with a progress bar
			// if(!cofGia_finallist_empty) export.export(scales, gias, f2, f3);
			if (expOptions.get(am.getGiaRoot2D().getName()) != null)
				export.export(scales, gias, f2, f3);

			// 3D
			// if(!cof3d_finallist_empty){
			if (expOptions.get(am.getGiaRoot3D().getName()) != null) {
				HashMap<RsaImageSet, IOutputScale> scaleMap = new HashMap<RsaImageSet, IOutputScale>();
				for (int i = 0; i < scales.size(); i++) {
					// we need to correct the 2D scale with the 3D scale
					// the 2D pixels are scaled to mm, and the 3D scale is
					// scaled to voxels relative to pixels
					if (desc3ds.get(inputs.get(i)) != null) {
						ConstantScale s = new ConstantScale(
								((IOutputScale) scales.get(i)).getScale()
										* desc3ds.get(inputs.get(i)).getScale());
						scaleMap.put(inputs.get(i), s);
					}
				}

				export.export3D(new ArrayList<RsaImageSet>(desc3ds.keySet()),
						scaleMap, desc3ds, f4);
			}

			// Gia3d_v2
			// if(!cofGia3d_v2_finallist_empty){
			if (expOptions.get(am.getGia3D_v2().getName()) != null) {
				HashMap<RsaImageSet, IOutputScale> scaleMap = new HashMap<RsaImageSet, IOutputScale>();
				for (int i = 0; i < scales.size(); i++) {
					// we need to correct the 2D scale with the 3D scale
					// the 2D pixels are scaled to mm, and the 3D scale is
					// scaled to voxels relative to pixels
					if (descGia3d_v2s.get(inputs.get(i)) != null) {
						ConstantScale s = new ConstantScale(
								((IOutputScale) scales.get(i)).getScale()
										* descGia3d_v2s.get(inputs.get(i))
												.getScale());
						scaleMap.put(inputs.get(i), s);
					}
				}

				export.exportGia3d_v2(
						new ArrayList<RsaImageSet>(descGia3d_v2s.keySet()),
						scaleMap, descGia3d_v2s, f6);
			}

			// Qc2
			// if(!cofQc2_finallist_empty){
			if (expOptions.get(am.getQc2().getName()) != null) {

				//
				// no scale for Qc2
				//
				// HashMap<RsaImageSet, IOutputScale> scaleMap = new
				// HashMap<RsaImageSet, IOutputScale>();
				//
				// for (int i = 0; i < scales.size(); i++)
				// {
				// // we need to correct the 2D scale with the 3D scale
				// // the 2D pixels are scaled to mm, and the 3D scale is scaled
				// to voxels relative to pixels
				// if (descQc2s.get(inputs.get(i)) != null)
				// {
				// ConstantScale s = new
				// ConstantScale(((IOutputScale)scales.get(i)).getScale() *
				// descSkel3ds.get(inputs.get(i)).getScale());
				// scaleMap.put(inputs.get(i), s);
				// }
				// }

				export.exportQc2(new ArrayList<RsaImageSet>(descQc2s.keySet()),
						null, descQc2s, f7);
			}

			// Qc3
			// if(!cofQc3_finallist_empty){
			if (expOptions.get(am.getQc3().getName()) != null) {

				//
				// no scale for Qc2
				//
				// HashMap<RsaImageSet, IOutputScale> scaleMap = new
				// HashMap<RsaImageSet, IOutputScale>();
				// for (int i = 0; i < scales.size(); i++)
				// {
				// // // we need to correct the 2D scale with the 3D scale
				// // // the 2D pixels are scaled to mm, and the 3D scale is
				// scaled to voxels relative to pixels
				// // if (descQc2s.get(inputs.get(i)) != null)
				// // {
				// // ConstantScale s = new
				// ConstantScale(((IOutputScale)scales.get(i)).getScale() *
				// descSkel3ds.get(inputs.get(i)).getScale());
				// // scaleMap.put(inputs.get(i), s);
				// // }
				// }

				export.exportQc3(new ArrayList<RsaImageSet>(descQc3s.keySet()),
						null, descQc3s, f8);
			}

			JOptionPane.showMessageDialog(null, "DONE!");
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource() == edf && evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			//
			expOptions = edf.getOptions();
			edf.resetOptions();
			boolean selectScale = expOptions.get("select_scale");

			edf.dispose();
			edf = null;

			if (selectScale) {
				doScales();
			} else {
				// Silently:
				// get one (arbitary) scale for every image in the image set
				// TODO:

				// for now: if no scale, then go further
				dispatcher();
			}

		}
		if (evt.getSource() == cofScale && evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofScale.getOutputs();
			HashMap<RsaImageSet, OutputInfo> map = new HashMap<RsaImageSet, OutputInfo>();
			for (OutputInfo oi : tmp) {
				map.put(oi.getRis(), oi);
			}
			for (int i = 0; i < scales.size(); i++) {
				if (scales.get(i) == null) {
					scales.remove(i);
					scales.add(i, map.get(inputs.get(i)));
				}
			}
			cofScale.dispose();
			cofScale = null;

			// doGias();
			dispatcher();
		} else if (evt.getSource() == cofGia
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofGia.getOutputs();
			HashMap<RsaImageSet, OutputInfo> map = new HashMap<RsaImageSet, OutputInfo>();
			for (OutputInfo oi : tmp) {
				map.put(oi.getRis(), oi);
			}
			for (int i = 0; i < gias.size(); i++) {
				if (gias.get(i) == null) {
					gias.remove(i);
					gias.add(i, map.get(inputs.get(i)));
				}
			}
			cofGia.dispose();
			cofGia = null;

			// do3d();
			dispatcher();
		} else if (evt.getSource() == cof3d
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cof3d.getOutputs();
			desc3ds = new HashMap<RsaImageSet, IOutputDescriptors3D>();
			for (OutputInfo oi : tmp) {
				desc3ds.put(oi.getRis(), (IOutputDescriptors3D) oi);
			}
			cof3d.dispose();
			cof3d = null;

			dispatcher();
		} else if (evt.getSource() == cofGia3d_v2
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofGia3d_v2.getOutputs();
			descGia3d_v2s = new HashMap<RsaImageSet, IOutputDescriptorsSkeleton3D>();
			for (OutputInfo oi : tmp) {
				descGia3d_v2s.put(oi.getRis(),
						(IOutputDescriptorsSkeleton3D) oi);
			}
			cofGia3d_v2.dispose();
			cofGia3d_v2 = null;

			dispatcher();
		} else if (evt.getSource() == cofQc2
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofQc2.getOutputs();
			descQc2s = new HashMap<RsaImageSet, IOutputDescriptorsQc2>();
			for (OutputInfo oi : tmp) {
				descQc2s.put(oi.getRis(), (IOutputDescriptorsQc2) oi);
			}
			cofQc2.dispose();
			cofQc2 = null;

			dispatcher();
		} else if (evt.getSource() == cofQc3
				&& evt.getPropertyName().equals("done")
				&& (Boolean) evt.getNewValue()) {
			ArrayList<OutputInfo> tmp = cofQc3.getOutputs();
			descQc3s = new HashMap<RsaImageSet, IOutputDescriptorsQc3>();
			for (OutputInfo oi : tmp) {
				descQc3s.put(oi.getRis(), (IOutputDescriptorsQc3) oi);
			}
			cofQc3.dispose();
			cofQc3 = null;

			// dispatcher();
			doExport();
		}
	}

	private void dispatcher() {
		String giaRoot2D = am.getGiaRoot2D().getName();
		if (expOptions.containsKey(am.getGiaRoot2D().getName())
				&& expOptions.get(am.getGiaRoot2D().getName())) {
			doGias();
			expOptions.put(am.getGiaRoot2D().getName(), false);
		} else if (expOptions.containsKey(am.getGiaRoot3D().getName())
				&& expOptions.get(am.getGiaRoot3D().getName())) {
			do3d();
			expOptions.put(am.getGiaRoot3D().getName(), false);
		} else if (expOptions.containsKey(am.getGia3D_v2().getName())
				&& expOptions.get(am.getGia3D_v2().getName())) {
			doGia3d_v2();
			expOptions.put(am.getGia3D_v2().getName(), false);
		} else if (expOptions.containsKey(am.getQc2().getName())
				&& expOptions.get(am.getQc2().getName())) {
			doQc2();
			expOptions.put(am.getQc2().getName(), false);
		} else if (expOptions.containsKey(am.getQc3().getName())
				&& expOptions.get(am.getQc3().getName())) {
			doQc3();
			expOptions.put(am.getQc3().getName(), false);
		} else {
			//
			doExport();
		}

	}

}
