/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.danforthcenter.genome.rootarch.rsagia.xml.RootworkConfigType;

/**
 * 
 * @author bm93
 */
public class Rootwork3DOutput extends OutputInfo implements IOutputVolume3D {
	protected File thresholdDir;
	protected File configFile;
	protected File volumeFile;

	public Rootwork3DOutput(OutputInfo oi) {
		super(oi);

		thresholdDir = new File(dir.getAbsolutePath() + File.separator
				+ "silhouette" + File.separator + "thresholding");
		configFile = new File(dir.getAbsolutePath() + File.separator
				+ "config.xml");
		volumeFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_rootwork.out");
	}

	public Rootwork3DOutput(File f, RsaImageSet ris) {
		super(f, ris);

		thresholdDir = new File(dir.getAbsolutePath() + File.separator
				+ "silhouette" + File.separator + "thresholding");
		configFile = new File(dir.getAbsolutePath() + File.separator
				+ "config.xml");
		volumeFile = new File(dir.getAbsolutePath() + File.separator
				+ getPrefix() + "_rootwork.out");

		outputs = InputOutputTypes.VOLUME_3D;
	}

	@Override
	public double getScale() {
		double ans = 0.0;

		javax.xml.bind.JAXBElement je = null;

		FileReader fr = null;
		XMLStreamReader sr = null;
		try {
			fr = new FileReader(configFile);
			JAXBContext jc = JAXBContext
					//.newInstance("edu.duke.genome.rootarch.rsagia.xml");
                    .newInstance("org.danforthcenter.genome.rootarch.rsagia.xml");
			Unmarshaller um = jc.createUnmarshaller();
			sr = javax.xml.stream.XMLInputFactory.newInstance()
					.createXMLStreamReader(fr);
			je = um.unmarshal(sr, new RootworkConfigType().getClass());
			RootworkConfigType rct = (RootworkConfigType) je.getValue();
			ans = rct.getResolution().doubleValue();
		} catch (Exception e) {
			throw new Rootwork3DOutputException(
					"Could not load scale from Rootwork config file", e);
		} finally {
			if (sr != null) {
				try {
					sr.close();
				} catch (Exception e) {

				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception e) {

				}
			}
		}

		return ans;
	}

	public File getThresholdDir() {
		return thresholdDir;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getVolumeFile() {
		return volumeFile;
	}

	public File getVolumeSTLFile() {
		return new File(volumeFile.getAbsolutePath() + ".stl");
	}

	public File getVolumeNPZFile() {
		return new File(volumeFile.getAbsolutePath() + ".npz");
	}

	public File getVolumeDir() {
		return dir;
	}

	public void makeThresholdDir(ISecurityManager ism) {
		if (!thresholdDir.getParentFile().mkdir()) {
			throw new Rootwork3DOutputException("Could not create directory: "
					+ thresholdDir.getParentFile().getAbsolutePath(), null);
		}

		// // tw 2014nov13
		// ism.setDirectoryPermissions(thresholdDir.getParentFile());
		ism.setPermissions(thresholdDir.getParentFile(), false);

		if (!thresholdDir.mkdir()) {
			throw new Rootwork3DOutputException("Could not create directory: "
					+ thresholdDir.getAbsolutePath(), null);
		}

		// // tw 2014nov13
		// ism.setDirectoryPermissions(dir);
		ism.setPermissions(dir, false);

	}

	@Override
	public boolean isValid() {
		return getVolumeFile().exists();
	}

	protected static class Rootwork3DOutputException extends RuntimeException {
		public Rootwork3DOutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
