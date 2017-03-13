/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 
 * @author bm93
 */
public class Scale implements IApplication {
	public static final String SCALE_FILE = "scale.properties";
	public static final String SCALE_PROP = "scale";

	@Override
	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		return true;
	}

	@Override
	public int getRequiredInputs() {
		return InputOutputTypes.RAW;
	}

	@Override
	public int getPossibleOutputs() {
		return InputOutputTypes.SCALE;
	}

	public int getOutputs(File f) {
		return InputOutputTypes.SCALE;
	}

	@Override
	public int getOutputs() {
		return InputOutputTypes.SCALE;
	}

	@Override
	public int getOptionalInputs() {
		return InputOutputTypes.NONE;
	}

	@Override
	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new ScaleOutput(f, ris);
	}

	public double getScale(OutputInfo oi) {
		Properties props = new Properties();
		File f = new File(oi.getDir().getAbsolutePath() + File.separator
				+ SCALE_FILE);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			props.load(fis);
		} catch (Exception e) {
			throw new ScaleException("Could not read scale from: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}

		return Double.parseDouble(props.getProperty(SCALE_PROP));
	}

	public void writeScale(boolean toSaved, RsaImageSet ris, double scale,
			ApplicationManager am) {
		OutputInfo oi = new OutputInfo("scale", ris, toSaved);
		OutputInfo.createDirectory(oi, am);
		File f = new File(oi.getDir().getAbsolutePath() + File.separator
				+ SCALE_FILE);
		Properties props = new Properties();
		props.setProperty(SCALE_PROP, Double.toString(scale));

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			props.store(fos, SCALE_FILE);
		} catch (Exception e) {
			throw new ScaleException("Could not write to file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {

				}
			}
		}

		// // tw 2014nov13
		// am.getIsm().setFilePermissions(f);
		am.getIsm().setPermissions(f, false);
	}

	public String getName() {
		return "scale";
	}

	public String getReviewString(OutputInfo oi) {
		return oi.toString() + "(" + this.getScale(oi) + ")";
	}

	protected static class ScaleException extends RuntimeException {
		public ScaleException(String msg) {
			super(msg);
		}

		public ScaleException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
