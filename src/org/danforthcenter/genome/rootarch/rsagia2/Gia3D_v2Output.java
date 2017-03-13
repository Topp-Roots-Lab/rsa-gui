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

//
///**
// *
// * @author vp23
// */
//public class Gia3D_v2Output {
//
//}

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * 
 * @author vp23
 */
public class Gia3D_v2Output extends OutputInfo implements
		IOutputDescriptorsSkeleton3D {

	public static final String TSV_FILE = "gia_3d_v2.tsv";
	public static final String TSV_FILE_TMP = "gia_3d_v2-tmp.tsv";
	public static final String MATLAB_FILE = "gia_3d_v2.matlab";
	public static final String OUTPUT_TYPE = "iv";
	public static final String OUTPUT_TYPE_2 = "wrl";
	public static final String CONFIG_XML_SUFFIX = "-gia3d_v2-config.xml";

	// just in case:
	// compare this scale to the 'scale' param
	// passed over to the Root3D Skeleton Traits executable -
	// no hidden interactions according Olga and Chris
	public static final String SCALE_FILE = "scale.properties";

	protected File volumeFile;
	// iv file for object
	protected File ivOrFile;
	// iv file for skeleton
	protected File ivSkFile;
	// wrl file for object
	protected File wrlOrFile;
	// wrl file for skeleton
	protected File wrlSkFile;

	public File getConfigFile(File templateFile) {
		return new File(dir.getAbsoluteFile() + File.separator
				+ templateFile.getName().replace(".xml", "")
				+ CONFIG_XML_SUFFIX);
	}

	public File getJobFile() {
		return new File(dir.getAbsoluteFile() + File.separator + "gia-job.xml");
	}

	@Override
	public File getTsvFile() {
		return new File(dir.getAbsoluteFile() + File.separator + TSV_FILE);
	}

	@Override
	public int getOutputs() {
		return super.getOutputs();
	}

	@Override
	public double getScale() {
		double ans = 0.0;

		Properties p = new Properties();
		FileReader fr = null;
		try {
			try {
				fr = new FileReader(getScaleFile());
				p.load(fr);
				ans = Double.parseDouble((String) p.get("scale"));
			} finally {
				if (fr != null) {
					fr.close();
				}
			}
		} catch (Exception e) {
			throw new GiaRoot3D_v2OutputException("Could not load scale", e);
		}

		return ans;
	}

	public File getScaleFile() {
		return new File(dir.getAbsolutePath() + File.separator + SCALE_FILE);
	}

	public Gia3D_v2Output(File f, RsaImageSet ris) {
		super(f, ris);
		outputs = InputOutputTypes.DESCRIPTORS_GIA_3D_V2;

		// volumeFile = new File(dir.getAbsolutePath() + File.separator +
		// getPrefix() + "_rootwork.out");

		ivOrFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_or." + OUTPUT_TYPE);
		ivSkFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_sk." + OUTPUT_TYPE);

		wrlOrFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_or." + OUTPUT_TYPE_2);
		wrlSkFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_sk." + OUTPUT_TYPE_2);
	}

	public File getVolumeFile() {
		return volumeFile;
	}

	public void setVolumeFile(File file) {
		volumeFile = file;
	}

	public File getIvOrFile() {
		return ivOrFile;
	}

	public File getIvSkFile() {
		return ivSkFile;
	}

	public File getWrlOrFile() {
		return wrlOrFile;
	}

	public File getWrlSkFile() {
		return wrlSkFile;
	}

	public File getMatlabFile() {
		return new File(dir.getAbsoluteFile() + File.separator + MATLAB_FILE);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	protected class GiaRoot3D_v2OutputException extends RuntimeException {
		public GiaRoot3D_v2OutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
