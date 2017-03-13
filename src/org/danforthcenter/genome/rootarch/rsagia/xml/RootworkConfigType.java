//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.27 at 04:16:31 PM EST 
//

package org.danforthcenter.genome.rootarch.rsagia.xml;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RootworkConfigType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="RootworkConfigType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="do-crop" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="use-giaroots-crop" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="do-likelihood-img" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="do-silhouette-hysteresis" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="use-giaroots-thresholding" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="do-silhouette-harmonic" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="do-axis" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="do-reconstruct-hysteresis" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="extension-root-images" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extension-giaroots-crop-images" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extension-giaroots-thresh-images" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="data-dir" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="file-prefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="silhouette-for-do-axis" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="num-imgs" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="num-img-used" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="resolution" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="num-nodes-on-octree" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="sil-upper-threshold" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="sil-lower-threshold" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="harmonic-threshold" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="recon-upper-threshold" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="recon-lower-threshold" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="recon-option" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="output-file-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="output-file-stl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extra-info" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="distortion-radius" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="number-of-components" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ref-image" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ref-ratio" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootworkConfigType", propOrder = { "doCrop",
		"useGiarootsCrop", "doLikelihoodImg", "doSilhouetteHysteresis",
		"useGiarootsThresholding", "doSilhouetteHarmonic", "doAxis",
		"doReconstructHysteresis", "extensionRootImages",
		"extensionGiarootsCropImages", "extensionGiarootsThreshImages",
		"dataDir", "filePrefix", "silhouetteForDoAxis", "numImgs",
		"numImgUsed", "resolution", "numNodesOnOctree", "silUpperThreshold",
		"silLowerThreshold", "harmonicThreshold", "reconUpperThreshold",
		"reconLowerThreshold", "reconOption", "outputFileName",
		"outputFileStl", "extraInfo", "distortionRadius", "numberOfComponents",
		"refImage", "refRatio" })
public class RootworkConfigType {

	@XmlElement(name = "do-crop", required = true)
	protected BigInteger doCrop;
	@XmlElement(name = "use-giaroots-crop", required = true)
	protected BigInteger useGiarootsCrop;
	@XmlElement(name = "do-likelihood-img", required = true)
	protected BigInteger doLikelihoodImg;
	@XmlElement(name = "do-silhouette-hysteresis", required = true)
	protected BigInteger doSilhouetteHysteresis;
	@XmlElement(name = "use-giaroots-thresholding", required = true)
	protected BigInteger useGiarootsThresholding;
	@XmlElement(name = "do-silhouette-harmonic", required = true)
	protected BigInteger doSilhouetteHarmonic;
	@XmlElement(name = "do-axis", required = true)
	protected BigInteger doAxis;
	@XmlElement(name = "do-reconstruct-hysteresis", required = true)
	protected BigInteger doReconstructHysteresis;
	@XmlElement(name = "extension-root-images", required = true)
	protected String extensionRootImages;
	@XmlElement(name = "extension-giaroots-crop-images", required = true)
	protected String extensionGiarootsCropImages;
	@XmlElement(name = "extension-giaroots-thresh-images", required = true)
	protected String extensionGiarootsThreshImages;
	@XmlElement(name = "data-dir", required = true)
	protected String dataDir;
	@XmlElement(name = "file-prefix", required = true)
	protected String filePrefix;
	@XmlElement(name = "silhouette-for-do-axis", required = true)
	protected BigInteger silhouetteForDoAxis;
	@XmlElement(name = "num-imgs", required = true)
	protected BigInteger numImgs;
	@XmlElement(name = "num-img-used", required = true)
	protected BigInteger numImgUsed;
	@XmlElement(required = true)
	protected BigInteger resolution;
	@XmlElement(name = "num-nodes-on-octree", required = true)
	protected BigInteger numNodesOnOctree;
	@XmlElement(name = "sil-upper-threshold", required = true)
	protected BigInteger silUpperThreshold;
	@XmlElement(name = "sil-lower-threshold", required = true)
	protected BigInteger silLowerThreshold;
	@XmlElement(name = "harmonic-threshold", required = true)
	protected BigInteger harmonicThreshold;
	@XmlElement(name = "recon-upper-threshold", required = true)
	protected BigInteger reconUpperThreshold;
	@XmlElement(name = "recon-lower-threshold", required = true)
	protected BigInteger reconLowerThreshold;
	@XmlElement(name = "recon-option", required = true)
	protected BigInteger reconOption;
	@XmlElement(name = "output-file-name", required = true)
	protected String outputFileName;
	@XmlElement(name = "output-file-stl", required = true)
	protected String outputFileStl;
	@XmlElement(name = "extra-info")
	protected double extraInfo;
	@XmlElement(name = "distortion-radius", required = true)
	protected BigInteger distortionRadius;
	@XmlElement(name = "number-of-components", required = true)
	protected BigInteger numberOfComponents;
	@XmlElement(name = "ref-image", required = true)
	protected BigInteger refImage;
	@XmlElement(name = "ref-ratio")
	protected double refRatio;

	/**
	 * Gets the value of the doCrop property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoCrop() {
		return doCrop;
	}

	/**
	 * Sets the value of the doCrop property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoCrop(BigInteger value) {
		this.doCrop = value;
	}

	/**
	 * Gets the value of the useGiarootsCrop property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getUseGiarootsCrop() {
		return useGiarootsCrop;
	}

	/**
	 * Sets the value of the useGiarootsCrop property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setUseGiarootsCrop(BigInteger value) {
		this.useGiarootsCrop = value;
	}

	/**
	 * Gets the value of the doLikelihoodImg property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoLikelihoodImg() {
		return doLikelihoodImg;
	}

	/**
	 * Sets the value of the doLikelihoodImg property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoLikelihoodImg(BigInteger value) {
		this.doLikelihoodImg = value;
	}

	/**
	 * Gets the value of the doSilhouetteHysteresis property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoSilhouetteHysteresis() {
		return doSilhouetteHysteresis;
	}

	/**
	 * Sets the value of the doSilhouetteHysteresis property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoSilhouetteHysteresis(BigInteger value) {
		this.doSilhouetteHysteresis = value;
	}

	/**
	 * Gets the value of the useGiarootsThresholding property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getUseGiarootsThresholding() {
		return useGiarootsThresholding;
	}

	/**
	 * Sets the value of the useGiarootsThresholding property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setUseGiarootsThresholding(BigInteger value) {
		this.useGiarootsThresholding = value;
	}

	/**
	 * Gets the value of the doSilhouetteHarmonic property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoSilhouetteHarmonic() {
		return doSilhouetteHarmonic;
	}

	/**
	 * Sets the value of the doSilhouetteHarmonic property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoSilhouetteHarmonic(BigInteger value) {
		this.doSilhouetteHarmonic = value;
	}

	/**
	 * Gets the value of the doAxis property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoAxis() {
		return doAxis;
	}

	/**
	 * Sets the value of the doAxis property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoAxis(BigInteger value) {
		this.doAxis = value;
	}

	/**
	 * Gets the value of the doReconstructHysteresis property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDoReconstructHysteresis() {
		return doReconstructHysteresis;
	}

	/**
	 * Sets the value of the doReconstructHysteresis property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDoReconstructHysteresis(BigInteger value) {
		this.doReconstructHysteresis = value;
	}

	/**
	 * Gets the value of the extensionRootImages property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExtensionRootImages() {
		return extensionRootImages;
	}

	/**
	 * Sets the value of the extensionRootImages property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExtensionRootImages(String value) {
		this.extensionRootImages = value;
	}

	/**
	 * Gets the value of the extensionGiarootsCropImages property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExtensionGiarootsCropImages() {
		return extensionGiarootsCropImages;
	}

	/**
	 * Sets the value of the extensionGiarootsCropImages property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExtensionGiarootsCropImages(String value) {
		this.extensionGiarootsCropImages = value;
	}

	/**
	 * Gets the value of the extensionGiarootsThreshImages property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExtensionGiarootsThreshImages() {
		return extensionGiarootsThreshImages;
	}

	/**
	 * Sets the value of the extensionGiarootsThreshImages property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExtensionGiarootsThreshImages(String value) {
		this.extensionGiarootsThreshImages = value;
	}

	/**
	 * Gets the value of the dataDir property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDataDir() {
		return dataDir;
	}

	/**
	 * Sets the value of the dataDir property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDataDir(String value) {
		this.dataDir = value;
	}

	/**
	 * Gets the value of the filePrefix property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFilePrefix() {
		return filePrefix;
	}

	/**
	 * Sets the value of the filePrefix property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFilePrefix(String value) {
		this.filePrefix = value;
	}

	/**
	 * Gets the value of the silhouetteForDoAxis property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getSilhouetteForDoAxis() {
		return silhouetteForDoAxis;
	}

	/**
	 * Sets the value of the silhouetteForDoAxis property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setSilhouetteForDoAxis(BigInteger value) {
		this.silhouetteForDoAxis = value;
	}

	/**
	 * Gets the value of the numImgs property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumImgs() {
		return numImgs;
	}

	/**
	 * Sets the value of the numImgs property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumImgs(BigInteger value) {
		this.numImgs = value;
	}

	/**
	 * Gets the value of the numImgUsed property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumImgUsed() {
		return numImgUsed;
	}

	/**
	 * Sets the value of the numImgUsed property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumImgUsed(BigInteger value) {
		this.numImgUsed = value;
	}

	/**
	 * Gets the value of the resolution property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getResolution() {
		return resolution;
	}

	/**
	 * Sets the value of the resolution property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setResolution(BigInteger value) {
		this.resolution = value;
	}

	/**
	 * Gets the value of the numNodesOnOctree property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumNodesOnOctree() {
		return numNodesOnOctree;
	}

	/**
	 * Sets the value of the numNodesOnOctree property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumNodesOnOctree(BigInteger value) {
		this.numNodesOnOctree = value;
	}

	/**
	 * Gets the value of the silUpperThreshold property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getSilUpperThreshold() {
		return silUpperThreshold;
	}

	/**
	 * Sets the value of the silUpperThreshold property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setSilUpperThreshold(BigInteger value) {
		this.silUpperThreshold = value;
	}

	/**
	 * Gets the value of the silLowerThreshold property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getSilLowerThreshold() {
		return silLowerThreshold;
	}

	/**
	 * Sets the value of the silLowerThreshold property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setSilLowerThreshold(BigInteger value) {
		this.silLowerThreshold = value;
	}

	/**
	 * Gets the value of the harmonicThreshold property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getHarmonicThreshold() {
		return harmonicThreshold;
	}

	/**
	 * Sets the value of the harmonicThreshold property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setHarmonicThreshold(BigInteger value) {
		this.harmonicThreshold = value;
	}

	/**
	 * Gets the value of the reconUpperThreshold property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getReconUpperThreshold() {
		return reconUpperThreshold;
	}

	/**
	 * Sets the value of the reconUpperThreshold property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setReconUpperThreshold(BigInteger value) {
		this.reconUpperThreshold = value;
	}

	/**
	 * Gets the value of the reconLowerThreshold property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getReconLowerThreshold() {
		return reconLowerThreshold;
	}

	/**
	 * Sets the value of the reconLowerThreshold property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setReconLowerThreshold(BigInteger value) {
		this.reconLowerThreshold = value;
	}

	/**
	 * Gets the value of the reconOption property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getReconOption() {
		return reconOption;
	}

	/**
	 * Sets the value of the reconOption property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setReconOption(BigInteger value) {
		this.reconOption = value;
	}

	/**
	 * Gets the value of the outputFileName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * Sets the value of the outputFileName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOutputFileName(String value) {
		this.outputFileName = value;
	}

	/**
	 * Gets the value of the outputFileStl property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getOutputFileStl() {
		return outputFileStl;
	}

	/**
	 * Sets the value of the outputFileStl property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setOutputFileStl(String value) {
		this.outputFileStl = value;
	}

	/**
	 * Gets the value of the extraInfo property.
	 * 
	 */
	public double getExtraInfo() {
		return extraInfo;
	}

	/**
	 * Sets the value of the extraInfo property.
	 * 
	 */
	public void setExtraInfo(double value) {
		this.extraInfo = value;
	}

	/**
	 * Gets the value of the distortionRadius property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getDistortionRadius() {
		return distortionRadius;
	}

	/**
	 * Sets the value of the distortionRadius property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setDistortionRadius(BigInteger value) {
		this.distortionRadius = value;
	}

	/**
	 * Gets the value of the numberOfComponents property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumberOfComponents() {
		return numberOfComponents;
	}

	/**
	 * Sets the value of the numberOfComponents property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumberOfComponents(BigInteger value) {
		this.numberOfComponents = value;
	}

	/**
	 * Gets the value of the refImage property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getRefImage() {
		return refImage;
	}

	/**
	 * Sets the value of the refImage property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setRefImage(BigInteger value) {
		this.refImage = value;
	}

	/**
	 * Gets the value of the refRatio property.
	 * 
	 */
	public double getRefRatio() {
		return refRatio;
	}

	/**
	 * Sets the value of the refRatio property.
	 * 
	 */
	public void setRefRatio(double value) {
		this.refRatio = value;
	}

}
