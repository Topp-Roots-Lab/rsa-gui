/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import ij.ImagePlus;
import ij.process.Blitter;
import ij.process.ImageProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author bm93, vp23
 */
public class Rootwork3D implements IApplication {
	protected String rootworkScriptPath;
	protected String reconstruction3d_exe;
	protected String reconstruction3d_stl_exe;
	protected String useMatlab;
	protected ISecurityManager ism;
	protected Rootwork3DXml rxml;

	public Rootwork3D(String rootworkScriptPath, String reconstruction3d_exe,
			String reconstruction3d_stl_exe, String useMatlab,
			ISecurityManager ism) {
		this.ism = ism;
		this.rootworkScriptPath = rootworkScriptPath;
		this.reconstruction3d_exe = reconstruction3d_exe;
		this.reconstruction3d_stl_exe = reconstruction3d_stl_exe;
		this.useMatlab = useMatlab;
	}

	@Override
	public String getName() {
		return "rootwork_3d";
	}

	@Override
	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		boolean ans = false;

		for (OutputInfo oi : OutputInfo.getInstances(am, ris, true, false,
				null, false)) {
			if (oi.isValid()

                    // oi.getOutputs = int
					&& ((oi.getOutputs() & InputOutputTypes.THRESHOLD) != 0)) {
				ans = true;
				break;
			}
		}

        System.out.println(this.getClass() + " has Required Input ans " + ans);
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
		return InputOutputTypes.CROP | InputOutputTypes.THRESHOLD;
	}

	@Override
	public String getReviewString(OutputInfo oi) {
		return oi.toString();
	}

	@Override
	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new Rootwork3DOutput(f, ris);
	}

	public Process start(OutputInfo out, IOutputThreshold iot, int reconOpt,
						 int reconLowerThresh, int numNodesOctree, int numImagesUsed,
						 int reconUpperThreshold, int distortionRadius,
						 int numberOfComponents, int resolution, int refImage,
						 double refRatio, boolean doAdd) {
		Process ans = null;
		Rootwork3DOutput rout = new Rootwork3DOutput(out);
		//
		// vp
		//
		// Note: reconLowerThresh, reconUpperThreshold
		// are not used, when calling the C++ reconstruction3D code
		//
		// Potentially, they can be used in the Matlab code for
		// special thresholding (never used in the rsa pipeline)
		//
		preprocess(rout, iot, reconOpt, reconLowerThresh, numNodesOctree,
				numImagesUsed, reconUpperThreshold, distortionRadius,
				numberOfComponents, resolution, refImage, refRatio, doAdd);

		boolean isSTL = false;
		String[] cmd = getReconstruction3dCmd(useMatlab, rout, isSTL);
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

	private String[] getReconstruction3dCmd(String useMatlab,
			Rootwork3DOutput rout, boolean isSTL) {
		String[] ret = null;

		//
		// Check whether Matlab is used.
		// Based on that, either matlab script or the
		// C++ reconstruction3D code is launced
		//
		if (useMatlab.equalsIgnoreCase("yes")) {
			// make the parameters to lauch the matlab code
			String[] cmd = { rootworkScriptPath,
					rout.getDir().getAbsolutePath() };
			ret = cmd;
		} else {
			//
			// find the best rotation
			//
			int best_axis = -1;
			HashMap<String, String> size = null;
			if (!isSTL) {
				//
				// compared with the matlab: best_axis=matlab_best_axis-1,
				// because best_axis index goes from 0 to width-1 and
				// matlab_best_axis index goes from 1 to width
				//
				// rotation axis goes along the vertical direction
				//
				best_axis = findRotationAxis(rout);
				//
				// crop using the best rotation axis
				size = cropUsingBestRotationAxis(rout, best_axis);
			} else {
				// skip
				// do nothing
			}

			// get the number of Rotation digits
			int rot_digits = getNumberRotationDigits(rout);
			// get the C++ reconstruction component parameters
			String[] prm = getReconstruction3dParams(rout, size, rot_digits,
					isSTL);
			ret = prm;
		}

		return ret;
	}

	private HashMap<String, String> cropUsingBestRotationAxis(
			Rootwork3DOutput rout, int best_axis) {
		HashMap<String, String> ret = new HashMap();

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
		File[] fs = silhouette.listFiles();
		if (fs != null) {
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
					int col = Math.min(best_axis, (width - 1) - best_axis);
					// x - horizontal corresponds to the width axis
					// y - vertical corresponds to the height axis
					int x = best_axis - col;
					int y = 0;

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
					h = height;
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

                    // tw 2015jun29 Have it working with png
					String renfname = f.getName();//.replaceFirst(".png", ".bmp");
					String renout = silhouette3.getAbsolutePath()
							+ File.separator + renfname;
					FileUtil.renameFile(new File(out), new File(renout));

					// // don't save to bmp: there are bigger than png
					// ij.IJ.saveAs(img_crop,"bmp", out);

				}
			}
		}

		ret.put("nCols", String.valueOf(w));
		ret.put("nRows", String.valueOf(h));

		return ret;

	}

	private String[] getReconstruction3dParams(Rootwork3DOutput rout,
			HashMap<String, String> size, int rot_digits, boolean isSTL) {
		String[] ret = null;

		//
		// para.txt file is located in the reconstruction3d folder
		// (currently reconstruction3d=/usr/local/bin/reconstruction3d)
		//
		String paraFileName = (new File(reconstruction3d_exe)).getParentFile()
				.getAbsolutePath() + File.separator + "para.txt";

		String rsaReconstruction3d = null;
		if (isSTL) {
			rsaReconstruction3d = reconstruction3d_stl_exe;
		} else {
			rsaReconstruction3d = reconstruction3d_exe;
		}

		if (isSTL) {
			// ReconOption=5 corresponds to STL
			String[] cmd = {
            // tw 2015jan15 remove "nice" for windows
//					"nice",
					rsaReconstruction3d,
					String.valueOf(5),
					String.valueOf(rout.getVolumeFile().getAbsolutePath()),
					String.valueOf(rout.getVolumeFile().getAbsolutePath()
							+ ".stl"), String.valueOf(rxml.getExtraInfo()) };
			ret = cmd;
		} else {
			String[] cmd = {
                    // tw 2015jan15 remove "nice" for windows
//					"nice",
					rsaReconstruction3d,
					String.valueOf(rxml.getReconOption()),
					size.get("nCols"),
					size.get("nRows"),
					String.valueOf(rxml.getNumNodesOctree()),
					rout.getDir() + File.separator + "silhouette3"
//                    rout.getDir() + File.separator + "silhouette"
							+ File.separator + getPrefix(rout),
					String.valueOf(rxml.getNumImagesUsed()),
//                    paraFileName,
					String.valueOf(rxml.getResolution()),
					String.valueOf(rxml.getDistortionRadius()),
					String.valueOf(rxml.getNumberOfComponents()),
					String.valueOf(rout.getVolumeFile().getAbsolutePath()),
					String.valueOf(rxml.getExtraInfo()),
					String.valueOf(rot_digits),
					String.valueOf(rxml.getRefImage()),
					String.valueOf(rxml.getRefRatio()),
					String.valueOf(rxml.getDoAdd()) };
			ret = cmd;
		}

		// /INFO////////INFO/////////INFO///////////INFO///////////////////
		//
		// Taken from the matlab code
		//
		// Here is the signature for the C++ reconstruction 3d
		//
		// unix([recon_file ' 1 ' num2str(nCols) ' ' ...
		// num2str(nRows) ' ' ...
		// num2str(numNodesOnOctree) ' '...
		// silDir3_plus_filePrefix ' ' ...
		// num2str(numImgUsed) ' ' ...
		// paraFileName ' ' num2str(resolution) ' ' ...
		// num2str(distortion_radius) ' ' ...
		// num2str(num_components) ' ' ...
		// recon_filename ' ' ...
		// num2str(extraInfo) ' ' ...
		// num2str(ROTATION_DIGITS) ' ' ...
		// num2str(ref_image) ' ' ...
		// num2str(ref_ratio)]);
		//
		// /INFO////////INFO/////////INFO///////////INFO///////////////////
		//
		// Here is the signature for the C++ reconstruction 3d for STL
		//
		// unix([recon_file_v4_STL ' 5 ' recon_filename ' ' ...
		// output_file ' ' num2str(extraInfo)]);
		//
		// /INFO////////INFO/////////INFO///////////INFO///////////////////

		// just more convenient for debugging
		String cmd_str = "";
		for (int i = 0; i < ret.length; i++) {
			cmd_str += ret[i] + " ";
		}

		return ret;
	}

	private String getPrefix(Rootwork3DOutput rout) {

		// rout.getVolumeFile().getName()=OsRILp00001d12_vp23_2013-07-31_18-16-56_rootwork.out

		String ret = "";

		String name = rout.getVolumeFile().getName();
		String[] ar = name.split("_");

		ret = ar[0] + "_";

		return ret;
	}

	private int getNumberRotationDigits(Rootwork3DOutput rout) {
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

	private int findRotationAxis(Rootwork3DOutput rout) {
		int ret = -1;

		int width = -1;
		int height = -1;

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

	// below the convertToMonochrome used, because ImagePlus can only
	// manage not less than the 8-bit gray image (we need 1-bit images here)
	//
	// Note that using 8-bit gray would result in ten times bigger images
	//
	private void createThrFileWith0And1(File in, File ft) {
		// ////////////////////////////debug/////////////////////////////////////////////
		// debug -data generated
		// File in = new
		// File("/data/rsa/processed_images/rice/RIL/p00001/d12/sandbox/rootwork_3d/ct103_2013-06-20_15-10-53/silhouette/OsRILp00001d12_01.bmp");
		// File in2 = new
		// File("/data/rsa/processed_images/rice/RIL/p00001/d12/sandbox/rootwork_3d/vp23_2013-07-31_12-53-57/silhouette/OsRILp00001d12_01.bmp");
		// /////////////////////////////////////////////////////////////////////////
		try {
			// System.out.println("first line in.getAbsolutePath()="+in.getAbsolutePath());
			ImagePlus imgporig = ij.IJ.openImage(in.getAbsolutePath());
			// System.out.println("in.getAbsolutePath()="+in.getAbsolutePath());
			ImageProcessor ip = imgporig.getProcessor().convertToByte(false)
					.duplicate();
			// ImageProcessor ip =
			// imgporig.getProcessor().convertToByte(true).duplicate();
			// ImageProcessor ip =
			// imgporig.getProcessor().convertToFloat().duplicate();

			// ////debug/////////////debug///////////////////////////////////////////////////
			//
			// ImagePlus imgporig2 = ij.IJ.openImage(in2.getAbsolutePath());
			// ImageProcessor ip2 =
			// imgporig2.getProcessor().convertToByte(false).duplicate();
			// int len2 = ip.getPixelCount();
			// int count2 = 0;
			// for(int i=0;i<len2;i++){
			// int val = ip.get(i);
			// int val2 = ip2.get(i);
			// // be carefull: we converted from byte to int
			// // (don't use val>0, for instance)
			// if(val != val2){
			// count2++;
			// }else{
			//
			// }
			// }
			// /debug///////////////debug///////////////////////////////////////////////////////////

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

			// png files are very small:
			// only ~4kb, instead of 400KB (tiff) and 45Kb mono (bmp) as used
			// to be when using matlab code
			//
			// save to png (and keep png extension here - no restrictions)
			// (compared when images are sent to the C++ component - the .bmp
			// needed)
			//
			// System.out.println("out="+out);
			ij.IJ.saveAs(imgp, "png", out);

		} catch (Exception e) {
			System.out.println("Error in createThrFileWith0And1 ="
					+ e.toString());
		}

		// ij.IJ.saveAs(imgp,"bmp", out);
	}

	public Process startSTL_orig(OutputInfo out, IOutputThreshold iot,
			int reconLowerThresh, int numNodesOctree, int numImagesUsed) {
		Process ans = null;
		Rootwork3DOutput rout = new Rootwork3DOutput(out);
		preprocessSTL(rout.getConfigFile());

        // tw 2015jan15 remove "nice" for windows
//		String[] cmd = { "nice", rootworkScriptPath,
        String[] cmd = { rootworkScriptPath,
				rout.getDir().getAbsolutePath() };
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new Rootwork3DException("Error running cmd: "
					+ Arrays.toString(cmd), e);
		}

		return ans;
	}

	public Process startSTL(String useMatlab, Rootwork3DOutput rout,
			boolean isSTL) {
		Process ans = null;
		//
		isSTL = true;
		String[] cmd = getReconstruction3dCmd(useMatlab, rout, isSTL);
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new Rootwork3DException("Error running cmd: "
					+ Arrays.toString(cmd), e);
		}

		return ans;
	}

	protected void preprocessSTL(File configFile) {
		BufferedReader br = null;
		String s = "";
		String eol = System.getProperty("line.separator");
		try {
			br = new BufferedReader(new FileReader(configFile));
			String s2 = null;
			while ((s2 = br.readLine()) != null) {
				s += s2 + eol;
			}
			s = s.replaceFirst("<recon-option>.*</recon-option>",
					"<recon-option>5</recon-option>");
		} catch (IOException e) {
			throw new Rootwork3DException("Could not read config file: "
					+ configFile.getAbsolutePath(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {

				}
			}
		}

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(configFile));
			bw.write(s);
		} catch (IOException e) {
			throw new Rootwork3DException("Could not write to config file: "
					+ configFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * Sets up file structure, returns the location of the config file for
	 * Rootwork.
	 * 
	 * @param out
	 * @param gia
	 * @param reconOpt
	 * @param reconLowerThresh
	 * @param numNodesOctree
	 * @param numImages
	 * @return
	 */
	protected void preprocess(Rootwork3DOutput rout, IOutputThreshold iot,
							  int reconOpt, int reconLowerThresh, int numNodesOctree,
							  int numImagesUsed, int reconUpperThreshold, int distortionRadius,
							  int numberOfComponents, int resolution, int refImage,
							  double refRatio, boolean doAdd) {
		rout.makeThresholdDir(ism);
		File ft = rout.getThresholdDir();
		File[] fs = iot.getThresholdedImages();
		// System.out.println("fs.length = : "+fs.length);

		String pref = null;
		String ext = null;
		for (File f : fs) {
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
//			System.out.println("after: "+f.getAbsolutePath()+" -- "+lnk.getAbsolutePath());

			// convert the lnk to 0 and 1 file and save in the silhouette
			// folder,
			// which is the parent of the ft = .../silhouette/thresholding
			// folder
			if (!useMatlab.equalsIgnoreCase("yes")) {
				createThrFileWith0And1(lnk, ft);
			}
		}

		rxml = new Rootwork3DXml(rout.getVolumeFile(), reconOpt,
				reconLowerThresh, numNodesOctree, numImagesUsed, fs.length,
				ext, pref, reconUpperThreshold, distortionRadius,
				numberOfComponents, resolution, refImage, refRatio, doAdd);
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

		boolean isSTL = true;
		Rootwork3DOutput rout = new Rootwork3DOutput(out);
		startSTL(useMatlab, rout, isSTL);

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

	protected static class Rootwork3DException extends RuntimeException {
		public Rootwork3DException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
