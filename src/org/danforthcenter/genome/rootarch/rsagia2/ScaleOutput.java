/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 
 * @author bm93
 */
public class ScaleOutput extends OutputInfo implements IOutputScale {
	public static final String SCALE_FILENAME = "scale.properties";
	protected File scaleFile;

	public ScaleOutput(File dir, RsaImageSet ris) {
		super(dir, ris);

		scaleFile = new File(dir.getAbsolutePath() + File.separator
				+ SCALE_FILENAME);
		outputs = InputOutputTypes.SCALE;
	}
	public ScaleOutput(String appName, RsaImageSet ris, boolean toSaved)
	{
		super(appName, ris, toSaved);
		this.scaleFile = new File(this.getDir()+ File.separator
				+ SCALE_FILENAME);
		this.outputs = InputOutputTypes.SCALE;
	}
	@Override
	public double getScale() {
		Properties props = new Properties();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(scaleFile);
			props.load(fis);
		} catch (Exception e) {
			throw new ScaleOutputException("Could not read scale from: "
					+ scaleFile.getAbsolutePath(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}

		return Double.parseDouble(props.getProperty(Scale.SCALE_PROP));
	}

	@Override
	public boolean isValid() {
		// eh, a bit of a hack
		boolean ans = true;
		try {
			getScale();
		} catch (Exception e) {
			ans = false;
		}

		return ans;
	}

	protected static class ScaleOutputException extends RuntimeException {
		public ScaleOutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
