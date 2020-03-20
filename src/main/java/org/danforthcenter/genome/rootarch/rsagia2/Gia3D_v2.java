/*
 *  Copyright 2013 vp23.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.jooq.tools.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author vp23
 */

public class Gia3D_v2 implements IApplication {

	private ApplicationManager am;
	private GiaRoot giaRoot;
	private String descriptors;
	private String descriptors_view;
	private String gia3D_v2ScriptPath;
	private String gia3D_v2MatlabScriptPath;
	private File templateDir;
	private Skel3DConfigXml scx;
	private String useMatlab;

	private final static String SURFACE_AREA_SKEL3D_NAME = "SurfArea";
	private final static String SURFACE_AREA_MATLAB_NAME = "area";

	public Gia3D_v2(String gia3D_v2ScriptPath, String descriptors,
					String descriptors_view, String gia3D_v2MatlabScriptPath,
					File templateDir, String useMatlab) {
		this.am = null;
		this.gia3D_v2ScriptPath = gia3D_v2ScriptPath;
		this.gia3D_v2MatlabScriptPath = gia3D_v2MatlabScriptPath;
		this.descriptors = descriptors;
		this.descriptors_view = descriptors_view;
		this.templateDir = templateDir;
		this.useMatlab = useMatlab;
	}

	public void setAm(ApplicationManager am) {
		this.am = am;
	}

	public String getUseMatlab() {
		return useMatlab;
	}

	public ArrayList<String> getConfigs() {
        OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		ArrayList<String> ans = oidbf.getSavedConfigs("gia3d_v2");
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

	public ArrayList<String> getDescriptorsView() {
		ArrayList<String> ans = new ArrayList<String>();
		String strs[] = descriptors_view.split(",");
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
	public Gia3D_v2Output preprocess(RsaImageSet ris, IOutputVolume3D vol,
									 String descriptors, String config) {
		Gia3D_v2Output oi = getOutputInfo(ris);
		OutputInfo.createDirectory(oi, am);

		OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		oidbf.insertProgramRunTable(oi);
		int configID = oidbf.findSavedConfigID(config, oi.getAppName());
		oi.setSavedConfigID(configID);
		JSONObject jo = new JSONObject();
		OutputInfo usedOI = (OutputInfo) vol;
		String used = "Used "+usedOI.getAppName()+ " Run ID";
		jo.put(used,vol.getRunID());
		oi.setInputRuns(jo.toString());
		oi.setUnsavedConfigContents(null);
		oi.setDescriptors(descriptors);

		File templateFile = new File(templateDir + File.separator + config
				+ ".xml");
		scx = new Skel3DConfigXml(templateFile, "out",
				Gia3D_v2Output.OUTPUT_TYPE_2);
		scx.write(oi.getConfigFile(templateFile));

		// preprocess is done before the 'start', so linkname would be updated
		File linkname = new File(oi.getDir().getAbsolutePath()
				+ File.separator + "rootwork.out");
		oi.setVolumeFile(linkname);

		// tw 2015may6 change order to match arguments in Java Files.createSymbolicLink method
		//FileUtil.createSymLink(vol.getVolumeFile(), linkname);
		FileUtil.createSymLink(linkname, vol.getVolumeFile());

		writeScaleFile(oi, vol.getScale());

		writeFeaturesHeaderFile(oi);

		return oi;
	}

	public void writeScaleFile(Gia3D_v2Output out, double scale) {
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
			throw new GiaRoot3D_v2Exception("Error saving scale information", e);
		}
	}

	public void writeFeaturesHeaderFile(Gia3D_v2Output out) {
		BufferedWriter bw = null;
		File features = out.getTsvFile();

		String[] desc = descriptors.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("Name");
		sb.append("\t");
		for (int i = 0; i < desc.length; i++) {
			sb.append(desc[i]);
			sb.append("\t");
		}
		// get the last tab out
		String header = sb.toString().substring(0, sb.toString().length() - 1);

		try {
			bw = new BufferedWriter(new FileWriter(features));
			bw.write(header);
			// don't add the line separator here,
			// because the Root3D SkeletonTraits
			// will append a new line with the traits
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception(
					"Error writing features header file", e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {

			}
		}
	}

	/**
	 *
	 * @param output
	 * @return
	 */
	public Process start(Gia3D_v2Output output) {
		Process ans = null;
		List<Skel3DConfigXml.ConfigProperty> configProps = null;
		double scale = 0.0;
		try {
			configProps = scx.getConfigPropertyies();
			// TODO: get all params
			// for the time being only scale - now it depends on the order in
			// XML
			scale = Double.parseDouble(configProps.get(2).getValue());
		} catch (Exception e) {
			throw new GiaRoot3D_v2Exception("start()", e);
		}

//		String[] cmd = { "nice", gia3D_v2ScriptPath,
		// tw 2015july16 remove nice for windows
		String[] cmd = { gia3D_v2ScriptPath,
				output.getVolumeFile().getAbsolutePath(),
				output.getTsvFile().getAbsolutePath(), String.valueOf(scale) };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception("Error starting process", e);
		}

		return ans;
	}

	/**
	 *
	 * @param output
	 * @return
	 */
	public Process startMatlab(Gia3D_v2Output output) {
		Process ans = null;
		List<Skel3DConfigXml.ConfigProperty> configProps = null;
		double scale = 0.0;
		try {
			configProps = scx.getConfigPropertyies();
			// TODO: get all params
			// for the time being only scale - now it depends on the order in
			// XML
			scale = Double.parseDouble(configProps.get(2).getValue());
		} catch (Exception e) {
			throw new GiaRoot3D_v2Exception("startMatlab()", e);
		}

		String inFile = output.getVolumeFile().getAbsolutePath();
		String outFile = output.getMatlabFile().getAbsolutePath();
//		String[] cmd = { "nice", gia3D_v2MatlabScriptPath, inFile, outFile, };
		// tw 2015july16 remove nice for windows
		String[] cmd = { gia3D_v2MatlabScriptPath, inFile, outFile, };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception("Error starting process", e);
		}

		return ans;
	}

	/**
	 * Sets permissions on generate filesFHFH
	 */
	public void postprocess(Gia3D_v2Output output, ApplicationManager am) {
		File f1 = null;
		File f2 = null;
		File f3 = null;
		File f4 = null;
		// rename iv files according to the pipeline convention
		for (File f : output.getDir().listFiles()) {
			File[] files = output.getDir().listFiles();
			if (f.isFile()) {
				// according to the ouput "iv" files name
				if (f.getName().endsWith(
						"or." + Gia3D_v2Output.OUTPUT_TYPE)) {
					f1 = f;
				}
				if (f.getName().endsWith(
						"sk." + Gia3D_v2Output.OUTPUT_TYPE)) {
					f2 = f;
				}
				// according to the ouput "wrl" files name
				if (f.getName().endsWith(
						"or." + Gia3D_v2Output.OUTPUT_TYPE_2)) {
					f3 = f;
				}
				if (f.getName().endsWith(
						"sk." + Gia3D_v2Output.OUTPUT_TYPE_2)) {
					f4 = f;
				}
			}
		}
		FileUtil.renameFile(f1, output.getIvOrFile());
		FileUtil.renameFile(f2, output.getIvSkFile());
		FileUtil.renameFile(f3, output.getWrlOrFile());
		FileUtil.renameFile(f4, output.getWrlSkFile());

		// permissions
		for (File f : output.getDir().listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				am.getIsm().setPermissions(f, false);
			}
		}
	}

	/**
	 * updates the SurfArea Root3D Skeleton Trait with the Matlab surface area
	 */
	public void postprocessMatlab(Gia3D_v2Output output, ApplicationManager am) {

		final Properties matlabProps = new Properties();
		FileInputStream fis1 = null;
		try {
			File file = output.getMatlabFile();
			fis1 = new FileInputStream(file);
			matlabProps.load(fis1);
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception(e);
		} finally {
			if (fis1 != null) {
				try {
					fis1.close();
				} catch (IOException e) {

				}
			}
		}

		String headers = "";
		String traits = "";
		String thirdline = "";
		String Msg = "";
		String[] hdrs = null;
		String[] trts = null;

		BufferedReader br = null;
		try {
			File file = output.getTsvFile();
			br = new BufferedReader(new FileReader(file));
			// get - the first line - the headers of the Root Skeleton Traits
			headers = br.readLine();
			// get - the secong line - the correspoinding Root Skeleton Traits
			traits = br.readLine();
			// get - the third line - there should be nothing in there
			thirdline = br.readLine();

			Msg = "File should have only two not empty lines "
					+ "with the Root3D Skeleton Traits headers and   : "
					+ "with the correspondibg Root3D Skeleton Traits values   : "
					+ output.getTsvFile().getAbsolutePath();

			if (headers == null || headers.isEmpty() || traits == null
					|| headers.isEmpty() || thirdline != null) {
				throw new GiaRoot3D_v2Exception(Msg);
			}
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				int test = 0;
			}
		}

		BufferedWriter bw = null;
		try {
			File file = output.getTsvFile();
			bw = new BufferedWriter(new FileWriter(file));

			hdrs = headers.split("\t");
			trts = traits.split("\t");

			List<String> hdrs_l = Arrays.asList(hdrs);
			int ind = hdrs_l.indexOf(SURFACE_AREA_SKEL3D_NAME);

			if (ind < 0) {
				throw new GiaRoot3D_v2Exception(Msg);
			}
			// change the trait value at the location=ind to Matlab value
			String area = matlabProps.getProperty(SURFACE_AREA_MATLAB_NAME);
			trts[ind] = area;

			// update the second line of the tsv file
			String traits_new = "";
			for (int i = 0; i < trts.length; i++) {
				traits_new = traits_new + trts[i] + "\t";
			}
			// strip the last "\t"
			traits_new = traits_new.substring(0, traits_new.length() - 1);
			traits_new = traits_new + System.getProperty("line.separator");

			// write two lines to the file
			bw.write(headers + System.getProperty("line.separator"));
			bw.write(traits_new + System.getProperty("line.separator"));
		} catch (IOException e) {
			throw new GiaRoot3D_v2Exception(e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {

			}
		}

		// permissions
		for (File f : output.getDir().listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				// am.getIsm().setFilePermissions(f);
				am.getIsm().setPermissions(f, false);
			}
		}
	}

	private String getTemplateString(OutputInfo oi) {
		ExtensionFileFilter eff = new ExtensionFileFilter("xml");
		File[] fs = oi.getDir().listFiles(eff);
		String ans = null;
		for (File f : fs) {
			String s = f.getName();
			int i = s.lastIndexOf(Gia3D_v2Output.CONFIG_XML_SUFFIX);
			if (i > 0) {
				ans = s.substring(0, i);
				break;
			}
		}

		return ans;
	}

	@Override
	public String getName() {
		return "gia3d_v2";
	}

	@Override
	public int getOptionalInputs() {
		return InputOutputTypes.NONE;
	}

	public Gia3D_v2Output getOutputInfo(RsaImageSet ris) {
		Gia3D_v2Output oi = new Gia3D_v2Output(getName(), ris, false);
		return oi;
	}

	@Override
	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new Gia3D_v2Output(f, ris);
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
		return oi.toString() + "(" + getTemplateString(oi) + ")";
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

	private static class GiaRoot3D_v2Exception extends RuntimeException {
		public GiaRoot3D_v2Exception(Throwable th) {
			super(th);
		}

		public GiaRoot3D_v2Exception(String msg) {
			super(msg);
		}

		public GiaRoot3D_v2Exception(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
