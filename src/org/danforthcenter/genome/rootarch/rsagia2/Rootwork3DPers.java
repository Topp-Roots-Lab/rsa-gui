package org.danforthcenter.genome.rootarch.rsagia2;


import ij.ImagePlus;
import ij.process.Blitter;
import ij.process.ImageProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// tw 2015july11
import java.util.Properties;
import java.awt.Rectangle;


/**
 * Created by twalk on 5/22/15.
 */
public class Rootwork3DPers  implements IApplication {

    protected String rootworkScriptPath;
    protected String reconstruction3dPers_exe;
//    protected String reconstruction3d_stl_exe;
//    protected String useMatlab;
    protected ISecurityManager ism;
    protected Rootwork3DPersXml rxml;


    private int newWidth = -1;
    private int newX = -1;


    public Rootwork3DPers(String reconstruction3dPers_exe, ISecurityManager ism) {
        this.ism = ism;
        this.reconstruction3dPers_exe = reconstruction3dPers_exe;
        this.newWidth = -1;
        this.newX = -1;
    }


    // tw 2015july11
    public double getScaleValue(OutputInfo ios){
        double scale = 0.;
        FileInputStream fis1 = null;
        Properties scaleProps = new Properties();

        File scalePropertiesFile = new File(ios.getDir() + File.separator
                + "scale.properties");
        try {
            fis1 = new FileInputStream(scalePropertiesFile);
            scaleProps.load(fis1);
            scale = Double.parseDouble(scaleProps.getProperty("scale"));
            System.out.println(this.getClass().getSimpleName() + " scale " + scale);
        } catch (IOException e) {
            System.out.println("CROP PROPERTIES FILE NOT FOUND. CROP SUM MAY BE CALCULATED WRONG");
        } finally {
            if (fis1 != null) {
                try {
                    fis1.close();
                } catch (IOException e) {
                }
            }
        }

        return scale;

    }


    public ArrayList<Rectangle> getRectsUsed(IOutputThreshold iot, boolean doFindRotAxis){

        ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

        ArrayList<Rectangle> cropRects = getCropRectangles(iot);

        Rectangle original = cropRects.get(0); // (x, y, width, height)
        rects.add(original);

        Rectangle crop = cropRects.get(1);
        System.out.println("cropx " + crop.x + "   crop.width " + crop.width);

        if ( this.newX != -1 && doFindRotAxis ){
            crop.x = crop.x + this.newX;
        }

        if ( this.newWidth != -1 && doFindRotAxis ){
            crop.width = this.newWidth;
        }

        System.out.println("cropx " + crop.x + "   crop.width " + crop.width);


        rects.add(crop);

        return rects;
    }



    public ArrayList<Rectangle> getCropRectangles(IOutputThreshold iot){

        ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
        File cropPropertiesFile = new File(iot.getCropPropFile().toString());
        Properties cropProps = new Properties();
        FileInputStream fis1 = null;

        Rectangle original = new Rectangle();
        Rectangle crop = new Rectangle();

        try {
            fis1 = new FileInputStream(cropPropertiesFile);
            cropProps.load(fis1);
            if ( cropProps.getProperty("original_image") != null && cropProps.getProperty("crop_sum") != null ){
                String[] origParams = cropProps.getProperty("original_image").split(",");
                original.x = Integer.parseInt(origParams[0]);
                original.y = Integer.parseInt(origParams[1]);
                original.width = Integer.parseInt(origParams[2]);
                original.height = Integer.parseInt(origParams[3]);
                String[] cropParams = cropProps.getProperty("crop_sum").split(",");
                crop.x = Integer.parseInt(cropParams[0]);
                crop.y = Integer.parseInt(cropParams[1]);
                crop.width = Integer.parseInt(cropParams[2]);
                crop.height = Integer.parseInt(cropParams[3]);

                rects.add(original);
                rects.add(crop);
            }
            else{
                System.out.println("ORIGINAL IMAGE SIZE AND/OR CROP SUM NOT FOUND. IS IT AN OLD CROP? NEW ORIGINAL SIZE WILL BE WRONG");
            }

        } catch (IOException e) {
            System.out.println("CROP PROPERTIES FILE NOT FOUND. ORIGINAL SIZE MAY BE CALCULATED WRONG");
        } finally {
            if (fis1 != null) {
                try {
                    fis1.close();
                } catch (IOException e) {
                }
            }
        }

        return rects;
    }




    @Override
    public String getName() {
        return "rootwork_3d_perspective";
    }

    @Override
    public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
        boolean ans = false;
        boolean scale = false;
        boolean threshold = false;

        for (OutputInfo oi : OutputInfo.getInstances(am, ris, true, false,
                null, false)) {

            if (oi.isValid()) {
                if ((oi.getOutputs() & InputOutputTypes.THRESHOLD) != 0) {
                    threshold = true;
                }
                if ((oi.getOutputs() & InputOutputTypes.SCALE) != 0) {
                    scale = true;
                }

            }
        }

        if ( scale && threshold ) {
            ans = true;
        }

        System.out.println(this.getClass().getSimpleName() + " has Required Input ans " + ans);
        return ans;
    }
    @Override
    public int getOptionalInputs() {
        return InputOutputTypes.NONE;
    }

    @Override
    public int getOutputs() {
        return InputOutputTypes.VOLUME_3D;
    }

    @Override
    public int getPossibleOutputs() {
        return InputOutputTypes.VOLUME_3D;
    }

    @Override
    public int getRequiredInputs() {
        return InputOutputTypes.SCALE | InputOutputTypes.THRESHOLD;
    }

    @Override
    public String getReviewString(OutputInfo oi) {
        return oi.toString();
    }

    @Override
    public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
        return new Rootwork3DPersOutput(f, ris);
    }

    public Process start(OutputInfo out, IOutputThreshold iot, OutputInfo ios, int reconOpt,
                         int reconLowerThresh, int numNodesOctree, int numImagesUsed,
                         int reconUpperThreshold, int distortionRadius,
                         int numberOfComponents, int resolution, int refImage,
                         double refRatio,
                         int camDist, int rotDir, boolean doFindRotAxis, boolean doCalib, Double pitch,
                         Double roll, int translation, int focusOffset,
                         boolean doAdd) {
        Process ans = null;
        Rootwork3DPersOutput rout = new Rootwork3DPersOutput(out);

        makeSil(rout, iot);

        double scale = getScaleValue(ios);

        // crop using the best rotation axis
        int best_axis = -1;
        best_axis = findRotationAxis(rout, doFindRotAxis);
        cropUsingBestRotationAxis(rout, best_axis, doFindRotAxis);

        int rot_digits = getNumberRotationDigits(rout);

        ArrayList<Rectangle> rectsUsed = getRectsUsed(iot, doFindRotAxis);

//        HashMap<String, String> size =  new HashMap();
//        size.put("nCols", String.valueOf(rectsUsed.get(1).width));
//        size.put("nRows", String.valueOf(rectsUsed.get(1).height));

        //
        // vp
        //
        // Note: reconLowerThresh, reconUpperThreshold
        // are not used, when calling the C++ reconstruction3D code
        preprocess(rout, iot, reconOpt, reconLowerThresh, numNodesOctree,
                numImagesUsed, reconUpperThreshold, distortionRadius,
                numberOfComponents, resolution, refImage, refRatio, rot_digits,
                rectsUsed, scale, camDist, rotDir, doFindRotAxis, doCalib, pitch,
                roll, translation, focusOffset, doAdd
                );

        out.setUnsavedConfigContents(rout.getUnsavedConfigContents());

        String[] cmd = getReconstruction3dCmd(rout);

        System.out.println("\t\t" + this.getClass().getSimpleName() + "\n");
        for (String subcmd : cmd) {
            System.out.print(subcmd + " ");
        }
        System.out.println();



        ProcessBuilder pb = new ProcessBuilder(cmd);

        pb.redirectErrorStream(true);

        try {
            ans = pb.start();
        } catch (IOException e) {
            throw new Rootwork3DException("Error running cmd: "
                    + Arrays.toString(cmd), e);
        }
        System.out.println("RootWork process returns" + ans);
        return ans;
    }

    private String[] getReconstruction3dCmd(Rootwork3DPersOutput rout) {
        String[] ret = null;

            // get the number of Rotation digits
            // get the C++ reconstruction component parameters
            String[] prm = getReconstruction3dParams(rout);
            ret = prm;

        return ret;
    }


    private String[] getReconstruction3dParams(Rootwork3DPersOutput rout) {
        String[] ret = null;

        String rsaReconstruction3dPers = reconstruction3dPers_exe;

        String[] cmd = {
            rsaReconstruction3dPers,
            String.valueOf(rxml.getReconOption()),
            String.valueOf(rxml.getWidthCrop()),
            String.valueOf(rxml.getHeightCrop()),
            String.valueOf(rxml.getNumNodesOctree()),
			rout.getDir() + File.separator + "silhouette3"
//            rout.getDir() + File.separator + "silhouette"
                    + File.separator + getPrefix(rout),
            String.valueOf(rxml.getNumImagesUsed()),
//            paraFileName,
            String.valueOf(rxml.getResolution()),
            String.valueOf(rxml.getDistortionRadius()),
            String.valueOf(rxml.getNumberOfComponents()),
            String.valueOf(rout.getVolumeFile().getAbsolutePath()),
            String.valueOf(rxml.getExtraInfo()),
            String.valueOf(rxml.getRotDigits()),
            String.valueOf(rxml.getRefImage()),
            String.valueOf(rxml.getRefRatio()),
            String.valueOf(rxml.getWidthOrig()),
            String.valueOf(rxml.getHeightOrig()),
            String.valueOf(rxml.getXCrop()),
            String.valueOf(rxml.getYCrop()),
            String.valueOf(rxml.getScale()),
            String.valueOf(rxml.getCamDist()),
            String.valueOf(rxml.getRotDir()),
            String.valueOf(rxml.getDoCalib()),
            String.valueOf(rxml.getTranslation()),
            String.valueOf(rxml.getRoll()),
            String.valueOf(rxml.getPitch()),
            String.valueOf(rxml.getFocusOffset()),
            String.valueOf(rxml.getDoAdd())
        };
        ret = cmd;

        // just more convenient for debugging
//        String cmd_str = "";
//        for (int i = 0; i < ret.length; i++) {
//            cmd_str += ret[i] + " ";
//        }

        return ret;
    }

    private String getPrefix(Rootwork3DPersOutput rout) {

        // rout.getVolumeFile().getName()=OsRILp00001d12_vp23_2013-07-31_18-16-56_rootwork.out

        String ret = "";

        String name = rout.getVolumeFile().getName();
        String[] ar = name.split("_");

        ret = ar[0] + "_";

        return ret;
    }

    private int getNumberRotationDigits(Rootwork3DPersOutput rout) {
        int ret = -1;

        File dir = rout.getThresholdDir();
        File[] fs = dir.listFiles();
        if (fs != null) {
            // get any file, for instance, the first one
            String n = fs[0].getName();
            String[] ss = n.split("_");
            String sret = ss[1].substring(0, ss[1].lastIndexOf("."));
            ret = sret.length();
        }

        return ret;
    }


    private void cropUsingBestRotationAxis(
            Rootwork3DPersOutput rout, int best_axis, boolean doFindRotAxis) {

        int w = -1;
        int h = -1;

        // create .../silhouette3 folder
        File silhouette3 = new File(rout.getDir() + File.separator
                + "silhouette3");
        if (!silhouette3.mkdir()) {
            // error
        }
        // // tw 2014nov12
        // ism.setDirectoryPermissions(silhouette3);
        ism.setPermissions(silhouette3, false);

        // Thresholding Dir
        File ft = rout.getThresholdDir();
        // silhouette folder, which is the parent
        // of the dir = .../thresholding folder
        File silhouette = ft.getParentFile();
//        System.out.println(this.getClass().getSimpleName() + " silhouette " + silhouette.toString());
        File[] fs = silhouette.listFiles();
//        System.out.println(this.getClass().getSimpleName() + " fs[0] " + fs[0]);
        if (fs != null) {
            System.out.println(this.getClass().getSimpleName() + " fs[0] " + fs[0]);
            ImagePlus img = null;
            ImagePlus img_crop = null;
            ImageProcessor ip = null;
            ImageProcessor ip_dpl = null;
            // find max "projection" of all fs images
            for (File f : fs) {
                if (f.isFile()) {
                    img = ij.IJ.openImage(f.getAbsolutePath());
                    int width = img.getWidth();
                    int height = img.getHeight();
                    ip = img.getProcessor().convertToByte(false);
                    // do not destroy the original image
                    ip_dpl = ip.duplicate();

                    // x - horizontal corresponds to the width axis
                    // y - vertical corresponds to the height axis
                    int x = 0;
                    int y = 0;
                    w = width;
                    h = height;

                    if ( doFindRotAxis && best_axis > 0) {
                        int col = Math.min(best_axis, (width - 1) - best_axis);
                        x = best_axis - col;

                        // ///////////////////////////////////////////////////
                        // // here is a sample (width=6).
                        // // Suppose, best_axis=2
                        // 0 1 2 3 4 5
                        // | | | | | |
                        // | | | | | |
                        // // then col = min(2,(6-1)-2)=2
                        // // The crop image will be
                        // 0 1 2 3 4
                        // | | | | |
                        // | | | | |
                        // // width = 2*col+1 = 2*2+1 = 5, that is five columns
                        // ///////////////////////////////////////////////////
                        w = 2 * col + 1;

                    }


                    ip_dpl.setRoi(x, y, w, h);
                    String fname = f.getName();
                    img_crop = new ImagePlus(fname, ip_dpl.crop());
                    String out = silhouette3.getAbsolutePath() + File.separator
                            + fname;

                    // png files are very small:
                    // only ~4kb, instead of 400KB (tiff) and 45Kb mono (bmp)
                    // as used to be when using matlab code
                    ij.IJ.saveAs(img_crop, "png", out);
                    //
                    // rename to the bmp - otherwise they will be rejected
                    // to be processed by the C++ component: it uses
                    // special object that needs to see the .bmp extension.
                    // (in fact, this object reads bytes and convert them to
                    // bool)
                    //
//                    String renfname = f.getName().replaceFirst(".png", ".bmp");
//                    String renout = silhouette3.getAbsolutePath()
//                            + File.separator + renfname;
//                    FileUtil.renameFile(new File(out), new File(renout));

                    // // don't save to bmp: there are bigger than png
                    // ij.IJ.saveAs(img_crop,"bmp", out);


                    // tw 2015july15
                    this.newWidth = w;
                    this.newX = x;

                }
            }
        }


    }


    private int findRotationAxis(Rootwork3DPersOutput rout, boolean doFindRotAxis) {
        int ret = -1;

        int width = -1;
        int height = -1;

        if ( doFindRotAxis ) {
            File ft = rout.getThresholdDir();
            // silhouette folder, which is the parent
            // of the dir = .../silhouette/thresholding folder
            File silhouette = ft.getParentFile();
            File[] fs = silhouette.listFiles();
            if (fs != null) {
                ImagePlus img = null;
                ImageProcessor ip = null;
                ImageProcessor add_ip = null;
                // find max "projection" of all fs images
                for (File f : fs) {
                    if (f.isFile()) {
                        img = ij.IJ.openImage(f.getAbsolutePath());
                        ip = img.getProcessor().convertToByte(false);
                        if (add_ip == null) {
                            width = img.getWidth();
                            height = img.getHeight();
                            // do not destroy the original image
                            add_ip = ip.duplicate();
                        }
                        // find max of add_ip and ip and copy result to the add_ip
                        // ip remains untouched
                        add_ip.copyBits(ip, 0, 0, Blitter.MAX);
                    }
                }
                // // debug
                // ImagePlus ip2 = img.createImagePlus();
                // ip2.setProcessor(ip);
                // ij.IJ.save(ip2, "/localhome/vp23/tmp/ip2.tif");
                // ImagePlus add_ip2 = img.createImagePlus();
                // add_ip2.setProcessor(add_ip);
                // ij.IJ.save(add_ip2, "/localhome/vp23/tmp/add_ip2.tif");

                //
                // find the best rotation axis
                //

                // sum all elements in the image
                double s = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int val = (int) add_ip.getPixel(j, i);
                        if (val > 0) {
                            s = s + 1;
                        }
                    }
                }

                // move column step by step and find the best column (axis) position
                double[] matches = new double[width];
                for (int i = 0; i < width; i++) {
                    int ncol = Math.min(i, width - 1 - i);
                    double s1 = 0;
                    for (int k = 0; k < height; k++) {
                        s1 = s1 + add_ip.getPixel(i, k);
                    }
                    double s2 = 0;
                    int j_symmetrical = -1;
                    for (int j = i - ncol; j < i; j++) {
                        // look at the points in the column j
                        // symmetrical relative to column i
                        j_symmetrical = 2 * i - j;
                        int test = 0;
                        for (int k = 0; k < height; k++) {
                            if (add_ip.getPixel(j, k) == 1
                                    && add_ip.getPixel(j_symmetrical, k) == 1) {
                                s2 = s2 + 1;
                            }
                        }
                    }
                    matches[i] = (s1 + 2 * s2) / s;
                }

                ret = getMax(matches);
            }

        }

        return ret;
    }

    private int getMax(double[] ar) {
        int ret = -1;
        double max = -1;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] > max) {
                ret = i;
                max = ar[i];
            }
            ;
        }

        return ret;
    }


    private void createThrFileWith0And1(File in, File ft) {

        try {
            // System.out.println("first line in.getAbsolutePath()="+in.getAbsolutePath());
            ImagePlus imgporig = ij.IJ.openImage(in.getAbsolutePath());
            // System.out.println("in.getAbsolutePath()="+in.getAbsolutePath());
            ImageProcessor ip = imgporig.getProcessor().convertToByte(false)
                    .duplicate();

            int len = ip.getPixelCount();
            int count = 0;
            for (int i = 0; i < len; i++) {
                int val = ip.get(i);
                // be carefull: we converted from byte to int
                // (don't use val>0, for instance)
                if (val != 0) {
                    count++;
                    val = 1;
                } else {
                    val = 0;
                }
                ip.set(i, val);
            }

            ImagePlus imgp = new ImagePlus(in.getName(), ip);
            String fname = in.getName();
            String out = ft.getParentFile().getAbsolutePath() + File.separator
                    + fname;

            ij.IJ.saveAs(imgp, "png", out);

        } catch (Exception e) {
            System.out.println("Error in createThrFileWith0And1 ="
                    + e.toString());
        }

    }


    protected void makeSil(Rootwork3DPersOutput rout, IOutputThreshold iot){

        rout.makeThresholdDir(ism);
        File ft = rout.getThresholdDir();
        File[] thresholdFiles = iot.getThresholdedImages();

        String pref = null;
        String ext = null;
        for (File f : thresholdFiles) {
            String n = f.getName();
            // System.out.println("first in loop name = : "+n);
            String[] ss = n.split("_");
            // System.out.println("ss[0] = : "+ss[0]);
            // System.out.println("ss[1] = : "+ss[1]);
            String s1 = n.substring(n.lastIndexOf("."), n.length());
            // System.out.println("s1 = : "+s1);
            ext = s1.substring(1, s1.length());
            // System.out.println("ext = : "+ext);
            String s2 = ss[0] + "_" + ss[1] + s1;
            pref = ss[0] + "_";
            File lnk = new File(ft.getAbsolutePath() + File.separator + s2);

//			System.out.println("before: "+f.getAbsolutePath()+" -- "+lnk.getAbsolutePath());

            // tw 2015jan15
//			FileUtil.createSymLink(f, lnk);
            FileUtil.createSymLink(lnk, f);

            createThrFileWith0And1(lnk, ft);

        }

    }



    /**
     * Sets up file structure, returns the location of the config file for
     * Rootwork.
     *
     * @param reconOpt
     * @param reconLowerThresh
     * @param numNodesOctree
     * @return
     */
    protected void preprocess(Rootwork3DPersOutput rout, IOutputThreshold iot,
                              int reconOpt, int reconLowerThresh, int numNodesOctree,
                              int numImagesUsed, int reconUpperThreshold, int distortionRadius,
                              int numberOfComponents, int resolution, int refImage,
                              double refRatio, int rot_digits,
                              ArrayList<Rectangle> rectsUsed, double scale, int camDist,
                              int rotDir, boolean doFindRotAxis, boolean doCalib, Double pitch,
                              Double roll, int translation, int focusOffset, boolean doAdd) {

        File ft = rout.getThresholdDir();
        File[] thresholdFiles = iot.getThresholdedImages();

        String pref = null;
        String ext = null;
        File f = thresholdFiles[0];

        String n = f.getName();
        String[] ss = n.split("_");
        String s1 = n.substring(n.lastIndexOf("."), n.length());
        ext = s1.substring(1, s1.length());
        pref = ss[0] + "_";

        rxml = new Rootwork3DPersXml(rout.getVolumeFile(), reconOpt,
                reconLowerThresh, numNodesOctree, numImagesUsed, thresholdFiles.length,
                ext, pref, reconUpperThreshold, distortionRadius,
                numberOfComponents, resolution, refImage, refRatio,
                rot_digits, rectsUsed, scale,
                camDist, rotDir, doFindRotAxis, doCalib, pitch,
                roll, translation, focusOffset, doAdd);
        rxml.save(rout.getConfigFile());
        rout.setUnsavedConfigContents(rxml.toString());

        // // tw 2014nov12
        // ism.setFilePermissions(rout.getConfigFile());
        ism.setPermissions(rout.getConfigFile(), false);

    }

    public void postprocess(OutputInfo out) {
        // For all these postprocess actions, nothing is printed out somewhere
        // to see the progress
        //
        // Ideally, it should be implemented like I did for the Skeleton 3d,
        // where all operations results are reflected in the log window
        //
        // convert to npz (generate .npz files from voxel .out files)
        System.out.println("Converting to NPZ for dir:"
                + out.getDir().getAbsolutePath());
        VoxNpzUtil.Convert(out.getDir().getAbsolutePath(), null);
        System.out.println("Converting to NPZ --- DONE");

        // create VRML for the components (in case when there more than one)
        System.out.println("Creating VRML for dir:"
                + out.getDir().getAbsolutePath());
        VrmlUtil.createShapeVrmlForVoxelsFiles(out.getDir());
        System.out.println("Creating VRML --- DONE");

        Rootwork3DPersOutput rout = new Rootwork3DPersOutput(out);
//        boolean isSTL = true;
//        startSTL(useMatlab, rout, isSTL);

        // create .../silhouette3 folder
        File silhouette3 = new File(out.getDir() + File.separator
                + "silhouette3");
        if (silhouette3.exists()) {
            // already exists

            // // tw 2014nov12
            // ism.setDirectoryPermissions(silhouette3);
            System.out.println("Setting permissions for silhouette3");
            ism.setPermissions(silhouette3, false);
        } else {
            // error - never happens
        }

        // set permissions
        // tw 2014nov12 update security to be platform independent
        // combine file and directory permissions to work with posix and acl
        // systems
        // Rather than pass separate permissions through many methods,
        // Check if path is file or directory and apply appropriate permissions
        System.out.println("Setting permissions for out directory " + out.getDir());
        File f = out.getDir();
        ism.setPermissions(f, true);
//        ism.setPermissions(f, false);
		/*
		 * ArrayList<File> fs = FileUtil.getRecursive(out.getDir()); for (File f
		 * : fs) { if (f.isDirectory()) { ism.setDirectoryPermissions(f); } else
		 * { ism.setFilePermissions(f); } }
		 */
    }

    private static class Rootwork3DException extends RuntimeException {
        private Rootwork3DException(String msg, Throwable th) {
            super(msg, th);
        }
    }




}
