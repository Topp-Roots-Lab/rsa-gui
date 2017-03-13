/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Given parameters, constructs the XML config file expected by rootwork. For
 * more information, see
 * http://mk42ws.biology.duke.edu:8000/wiki/010-BenfeyLab/120
 * -BioBusch/030-RootArch/120-AppScr/110-Recon/091-RunRwGen
 * 
 * @author bm93, vp23
 */
public class Rootwork3DXml {
	protected File outputFile;
	protected int reconOption;
	protected int reconLowerThreshold;
	protected int numNodesOctree;
	protected int numImagesUsed;
	protected int numImages;
	protected String extGiaImages;
	protected String filePrefix;
	protected int reconUpperThreshold;
	protected int distortionRadius;
	protected int numberOfComponents;
	protected int resolution;
	protected int refImage;
	protected double refRatio;
	protected String extraInfo;

	public Rootwork3DXml(File outputFile, int reconOption,
			int reconLowerThreshold, int numNodesOctree, int numImagesUsed,
			int numImages, String extGiaImages, String filePrefix,
			int reconUpperThreshold, int distortionRadius,
			int numberOfComponents, int resolution, int refImage,
			double refRatio) {
		this.outputFile = outputFile;
		this.reconOption = reconOption;
		this.reconLowerThreshold = reconLowerThreshold;
		this.numNodesOctree = numNodesOctree;
		this.numImagesUsed = numImagesUsed;
		this.numImages = numImages;
		this.extGiaImages = extGiaImages;
		this.filePrefix = filePrefix;
		this.reconUpperThreshold = reconUpperThreshold;
		this.distortionRadius = distortionRadius;
		this.numberOfComponents = numberOfComponents;
		this.resolution = resolution;
		this.refImage = refImage;
		this.refRatio = refRatio;

		// note used - everwhere equals to 0.15
		this.extraInfo = "0.15";
	}

	public void save(File f) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(toString());
		} catch (IOException e) {
			throw new Rootwork3DXmlException("Error writing to file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public String getExtGiaImages() {
		return extGiaImages;
	}

	public void setExtGiaImages(String extGiaImages) {
		this.extGiaImages = extGiaImages;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public int getNumImagesUsed() {
		return numImagesUsed;
	}

	public void setNumImagesUsed(int numImagesUsed) {
		this.numImagesUsed = numImagesUsed;
	}

	public int getNumNodesOctree() {
		return numNodesOctree;
	}

	public void setNumNodesOctree(int numNodesOctree) {
		this.numNodesOctree = numNodesOctree;
	}

	public int getReconLowerThreshold() {
		return reconLowerThreshold;
	}

	public void setReconLowerThreshold(int reconLowerThreshold) {
		this.reconLowerThreshold = reconLowerThreshold;
	}

	public int getReconOption() {
		return reconOption;
	}

	public void setReconOption(int reconOption) {
		this.reconOption = reconOption;
	}

	public int getRefImage() {
		return refImage;
	}

	public void setRefImage(int refImage) {
		this.refImage = refImage;
	}

	public double getRefRatio() {
		return refRatio;
	}

	public void setRefRatio(int refRatio) {
		this.refRatio = refRatio;
	}

	public int getResolution() {
		return resolution;
	}

	public int getDistortionRadius() {
		return distortionRadius;
	}

	public int getNumberOfComponents() {
		return numberOfComponents;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	@Override
	public String toString() {
		String eol = System.getProperty("line.separator");
		String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + eol
				+ "<params>" + eol;
		ans += "\t<do-crop>0</do-crop>" + eol;
		ans += "\t<use-giaroots-crop>0</use-giaroots-crop>" + eol;
		ans += "\t<do-likelihood-img>0</do-likelihood-img>" + eol;
		ans += "\t<do-silhouette-hysteresis>0</do-silhouette-hysteresis>" + eol;
		ans += "\t<use-giaroots-thresholding>1</use-giaroots-thresholding>"
				+ eol;
		ans += "\t<do-silhouette-harmonic>0</do-silhouette-harmonic>" + eol;
		ans += "\t<do-axis>1</do-axis>" + eol;
		ans += "\t<do-reconstruct-hysteresis>1</do-reconstruct-hysteresis>"
				+ eol;
		ans += "\t<extension-root-images>jpg</extension-root-images>" + eol;
		ans += "\t<extension-giaroots-crop-images>tiff</extension-giaroots-crop-images>"
				+ eol;
		ans += "\t<extension-giaroots-thresh-images>tiff</extension-giaroots-thresh-images>"
				+ eol;
		ans += "\t<data-dir></data-dir>" + eol;
		ans += "\t<file-prefix>" + filePrefix + "</file-prefix>" + eol;
		ans += "\t<silhouette-for-do-axis>1</silhouette-for-do-axis>" + eol;
		ans += "\t<num-imgs>" + numImages + "</num-imgs>" + eol;
		ans += "\t<num-img-used>" + numImagesUsed + "</num-img-used>" + eol;
		ans += "\t<resolution>" + resolution + "</resolution>" + eol;
		ans += "\t<num-nodes-on-octree>" + numNodesOctree
				+ "</num-nodes-on-octree>" + eol;
		ans += "\t<sil-upper-threshold>0.99</sil-upper-threshold>" + eol;
		ans += "\t<sil-lower-threshold>0.9</sil-lower-threshold>" + eol;
		ans += "\t<harmonic-threshold>0.3</harmonic-threshold>" + eol;
		ans += "\t<recon-lower-threshold>" + reconLowerThreshold
				+ "</recon-lower-threshold>" + eol;
		ans += "\t<recon-upper-threshold>" + reconUpperThreshold
				+ "</recon-upper-threshold>" + eol;
		ans += "\t<recon-option>" + reconOption + "</recon-option>" + eol;
		ans += "\t<output-file-name>" + outputFile.getAbsolutePath()
				+ "</output-file-name>" + eol;
		ans += "\t<output-file-stl>" + outputFile.getAbsolutePath()
				+ ".stl</output-file-stl>" + eol;
		ans += "\t<extra-info>0.15</extra-info>" + eol;
		ans += "\t<distortion-radius>" + distortionRadius
				+ "</distortion-radius>" + eol;
		ans += "\t<number-of-components>" + numberOfComponents
				+ "</number-of-components>" + eol;
		ans += "\t<ref-image>" + refImage + "</ref-image>" + eol;
		ans += "\t<ref-ratio>" + refRatio + "</ref-ratio>" + eol;
		ans += "</params>" + eol;

		return ans;
	}

	protected static class Rootwork3DXmlException extends RuntimeException {
		public Rootwork3DXmlException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
