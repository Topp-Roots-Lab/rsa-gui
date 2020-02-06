/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * 
 * @author bm93
 */
public class GiaRoot3DOutput extends OutputInfo implements IOutputDescriptors3D {

	public static final String CSV_FILE = "giaroot_3d.csv";
	public static final String SCALE_FILE = "scale.properties";

	public File getConfigFile(File templateFile) {
		return new File(dir.getAbsoluteFile() + File.separator
				+ templateFile.getName().replace("config.xml", "-config.xml"));
	}

	public File getJobFile() {
		return new File(dir.getAbsoluteFile() + File.separator + "gia-job.xml");
	}

	public File getCsvFile() {
		return new File(dir.getAbsoluteFile() + File.separator + CSV_FILE);
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
			throw new GiaRoot3DOutputException("Could not load scale", e);
		}

		return ans;
	}

	public File getScaleFile() {
		return new File(dir.getAbsolutePath() + File.separator + SCALE_FILE);
	}

	public GiaRoot3DOutput(File f, RsaImageSet ris) {
		super(f, ris);
		outputs = InputOutputTypes.DESCRIPTORS_3D;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	protected class GiaRoot3DOutputException extends RuntimeException {
		public GiaRoot3DOutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
