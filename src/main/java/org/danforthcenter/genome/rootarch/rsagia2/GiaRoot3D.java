/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 
 * @author bm93
 */
public class GiaRoot3D implements IApplication {
	protected ApplicationManager am;
	protected GiaRoot giaRoot;
	protected String descriptors;
	protected File templateDir;


	public GiaRoot3D(String giaExecPath, File giaPath, String descriptors,
			File templateDir, ISecurityManager ism) {
		this.am = null;
		this.giaRoot = new GiaRoot(giaExecPath, giaPath, ism);
		this.descriptors = descriptors;
		this.templateDir = templateDir;
	}

	public void setAm(ApplicationManager am) {
		this.am = am;
	}

	public ArrayList<String> getConfigs() {
		ArrayList<String> ans = new ArrayList<String>();
		ExtensionFileFilter eff = new ExtensionFileFilter("xml");
		File[] fs = templateDir.listFiles(eff);
		if (fs != null) {
			for (File f : fs) {
				ans.add(f.getName().replace(".xml", ""));
			}
		}

		return ans;
	}

	public ArrayList<String> getDescriptors() {
		ArrayList<String> ans = new ArrayList<String>();
		String strs[] = descriptors.split(",");
		for (String str : strs) {
			ans.add(str);
		}

		return ans;
	}

	/**
	 * Creates the output directory and allows the caller to have a handle to
	 * the output folder.
	 * 
	 * @param vol
	 * @param descriptors
	 * @return
	 */
	public GiaRoot3DOutput preprocess(RsaImageSet ris, IOutputVolume3D vol,
			String descriptors, String config) {
		GiaRoot3DOutput ans = getOutputInfo(ris);
		OutputInfo.createDirectory(ans, am);

		File templateFile = new File(templateDir + File.separator + config
				+ ".xml");
		OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		String savedTemplate = oidbf.findSavedConfigContents(config, "gia3d");
		GiaConfigXml gcx = new GiaConfigXml(savedTemplate, "tiff", "tiff");
		gcx.write(ans.getConfigFile(templateFile));

		FileUtil.createSymLink(vol.getVolumeFile(), new File(ans.getDir()
				.getAbsolutePath() + File.separator + "voxel_final.rootwork"));

		Gia3DJobXml gjx = new Gia3DJobXml(ans.getConfigFile(templateFile),
				ans.getDir(), ans.getDir(), descriptors);
		gjx.write(ans.getJobFile());

		writeScaleFile(ans, vol.getScale());

		return ans;
	}

	public void writeScaleFile(GiaRoot3DOutput out, double scale) {
		FileWriter fw = null;

		try {
			try {
				fw = new FileWriter(out.getScaleFile());

				Properties p = new Properties();
				p.setProperty("scale", Double.toString(scale));
				p.store(fw,
						"scale is the voxel scaling (i.e. resolution) from the 3D reconstruction");
			} finally {
				if (fw != null) {
					fw.close();
				}
			}
		} catch (Exception e) {
			throw new GiaRoot3DException("Error saving scale information", e);
		}
	}

	public Process start(GiaRoot3DOutput output) {
		Process ans = giaRoot.start(output.getJobFile(), output.getDir());
		return ans;
	}

	public void postprocess(GiaRoot3DOutput output) {
		giaRoot.postprocess(output.getDir(), am);
	}

	@Override
	public String getName() {
		return "giaroot_3d";
	}

	@Override
	public int getOptionalInputs() {
		return InputOutputTypes.NONE;
	}

	public GiaRoot3DOutput getOutputInfo(RsaImageSet ris) {
		OutputInfo oi = new OutputInfo(getName(), ris, false);
		return new GiaRoot3DOutput(oi.getDir(), ris);
	}

	@Override
	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new GiaRoot3DOutput(f, ris);
	}

	@Override
	public int getOutputs() {
		return InputOutputTypes.DESCRIPTORS_3D;
	}

	@Override
	public int getPossibleOutputs() {
		return InputOutputTypes.DESCRIPTORS_3D;
	}

	@Override
	public int getRequiredInputs() {
		return InputOutputTypes.VOLUME_3D;
	}

	@Override
	public String getReviewString(OutputInfo oi) {
		return oi.toString();
	}

	@Override
	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		boolean ans = false;
		ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris, true,
				false, null, false);
		for (OutputInfo oi : ois) {
			if ((oi.getOutputs() & getRequiredInputs()) != 0) {
				ans = true;
				break;
			}
		}

		return ans;
	}

	protected static class GiaRoot3DException extends RuntimeException {
		public GiaRoot3DException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
