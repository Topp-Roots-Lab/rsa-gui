/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Given parameters, constructs the XML config file expected by rootwork. For
 * more information, see
 * http://mk42ws.biology.duke.edu:8000/wiki/010-BenfeyLab/120
 * -BioBusch/030-RootArch/120-AppScr/110-Recon/091-RunRwGen
 *
 * @author bm93, vp23
 */
public class Rootwork3DPersXml {
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

    // tw 2015july15 add parameters for perspective reconstruction

    protected boolean doFindRotAxis;
    protected int rotDigits;
    protected ArrayList<Rectangle> rectsUsed;
    protected double scale;
    protected int camDist;
    protected int rotDir;
    protected boolean doCalib;
    protected Double pitch;
    protected Double roll;
    protected int translation;
    protected int focusOffset;
    protected int heightOrig;
    protected int widthOrig;
    protected int xCrop;
    protected int yCrop;
    protected int heightCrop;
    protected int widthCrop;

    public Rootwork3DPersXml(File outputFile, int reconOption,
                         int reconLowerThreshold, int numNodesOctree, int numImagesUsed,
                         int numImages, String extGiaImages, String filePrefix,
                         int reconUpperThreshold, int distortionRadius,
                         int numberOfComponents, int resolution, int refImage,
                         double refRatio,
                             int rotDigits, ArrayList<Rectangle> rectsUsed, double scale,
                             int camDist,int rotDir, boolean doFindRotAxis, boolean doCalib,
                             Double pitch, Double roll, int translation, int focusOffset) {

        this.doFindRotAxis = doFindRotAxis;
        this.rotDigits = rotDigits;
        this.rectsUsed = rectsUsed;
        this.scale = scale;
        this.camDist = camDist;
        this.rotDir = rotDir;
        this.doCalib = doCalib;
        this.pitch = pitch;
        this.roll = roll;
        this.translation = translation;
        this.focusOffset = focusOffset;
        this.heightOrig = rectsUsed.get(0).height;
        this.widthOrig = rectsUsed.get(0).width;
        this.xCrop = rectsUsed.get(1).x;
        this.yCrop = rectsUsed.get(1).y;
        this.heightCrop = rectsUsed.get(1).height;
        this.widthCrop = rectsUsed.get(1).width;

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

    public boolean getDoFindRotAxis() {
        return doFindRotAxis;
    }

    public int getRotDigits() {
        return rotDigits;
    }

    public ArrayList<Rectangle> getRectsUsed() {
        return rectsUsed;
    }

    public int getHeightOrig() {
        return heightOrig;
    }

    public int getWidthOrig() {
        return widthOrig;
    }

    public int getHeightCrop() {
        return heightCrop;
    }

    public int getWidthCrop() {
        return widthCrop;
    }

    public int getXCrop() {
        return xCrop;
    }

    public int getYCrop() {
        return yCrop;
    }

    public double getScale() {
        return scale;
    }

    public int getCamDist() {
        return camDist;
    }

    public int getRotDir() {
        return rotDir;
    }

    public boolean getDoCalib() {
        return doCalib;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public int getTranslation() {
        return translation;
    }

    public double getFocusOffset() {
        return focusOffset;
    }



    @Override
    public String toString() {
        String eol = System.getProperty("line.separator");
        String ans = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + eol
                + "<params>" + eol;
        ans += "\t<do-crop>1</do-crop>" + eol;
        ans += "\t<use-giaroots-crop>0</use-giaroots-crop>" + eol;
        ans += "\t<use-giaroots-thresholding>1</use-giaroots-thresholding>"
                + eol;
        ans += "\t<extension-root-images>jpg</extension-root-images>" + eol;
        ans += "\t<extension-giaroots-crop-images>tiff</extension-giaroots-crop-images>"
                + eol;
        ans += "\t<extension-giaroots-thresh-images>tiff</extension-giaroots-thresh-images>"
                + eol;
        ans += "\t<data-dir></data-dir>" + eol;
        ans += "\t<file-prefix>" + filePrefix + "</file-prefix>" + eol;
        ans += "\t<num-imgs>" + numImages + "</num-imgs>" + eol;
        ans += "\t<num-img-used>" + numImagesUsed + "</num-img-used>" + eol;
        ans += "\t<resolution>" + resolution + "</resolution>" + eol;
        ans += "\t<num-nodes-on-octree>" + numNodesOctree
                + "</num-nodes-on-octree>" + eol;
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

        ans += "\t<do-axis>" + doFindRotAxis + "</do-axis>" + eol;
        ans += "\t<rot-digits>" + rotDigits + "</rot-digits>" + eol;
        ans += "\t<height-original>" + heightOrig + "</height-original>" + eol;
        ans += "\t<width-original>" + widthOrig + "</width-original>" + eol;
        ans += "\t<height-crop>" + heightCrop + "</height-crop>" + eol;
        ans += "\t<width-crop>" + widthCrop + "</width-crop>" + eol;
        ans += "\t<x-crop>" + xCrop + "</x-crop>" + eol;
        ans += "\t<y-crop>" + yCrop + "</y-crop>" + eol;
        ans += "\t<scale>" + scale + "</scale>" + eol;
        ans += "\t<camera-distance>" + camDist + "</camera-distance>" + eol;
        ans += "\t<rotation-direction>" + rotDir + "</rotation-direction>" + eol;
        ans += "\t<do-calibration>" + doCalib + "</do-calibration>" + eol;
        ans += "\t<pitch>" + pitch + "</pitch>" + eol;
        ans += "\t<roll>" + roll + "</roll>" + eol;
        ans += "\t<translation>" + translation + "</translation>" + eol;
        ans += "\t<focus-offset>" + focusOffset + "</focus-offset>" + eol;

        ans += "</params>" + eol;

        return ans;
    }

    protected static class Rootwork3DXmlException extends RuntimeException {
        public Rootwork3DXmlException(String msg, Throwable th) {
            super(msg, th);
        }
    }
}
