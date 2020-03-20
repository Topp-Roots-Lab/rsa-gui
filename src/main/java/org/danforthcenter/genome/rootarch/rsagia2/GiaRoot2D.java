/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author bm93
 */
public class GiaRoot2D implements IApplication {
	private File templateDir;
	private final static String TEMPLATE_EXT = "xml";
	private final static String THRESHOLD_SUBSTR = "threshold";

	private String giaExecPath;
	private File giaPath;
	private String allDescriptors;

	public static final String OUTPUT_TYPE = "tiff";
	public static final String CONFIG_XML_SUFFIX = "-gia-config.xml";
	public static final String JOB_XML_NAME = "job-config.xml";

    private ISecurityManager ism;


    public GiaRoot2D(File templateDir, String giaExecPath, File giaPath,
			String allDescriptors, ISecurityManager ism) {
		this.templateDir = templateDir;
		// tw 2014july3 testing
		// this.templateDir = new
		// File("/data/rsa/rsa-gia-templates/giaroot_2d");
		this.giaExecPath = giaExecPath;
		this.giaPath = giaPath;
		this.allDescriptors = allDescriptors;

        // 2015jan1 tw
        this.ism = ism;

	}

	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		boolean ans = false;

		for (OutputInfo oi : OutputInfo.getInstances(am, ris, true, false,
				null, false)) {
			if (oi.isValid()
					&& ((oi.getOutputs() & InputOutputTypes.CROP) != 0)) {
				ans = true;
				break;
			}
		}

		return ans;
	}

	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new GiaRoot2DOutput(f, ris);
	}

	public String getAllDescriptors() {
		return allDescriptors;
	}

	public ArrayList<String> getSavedConfigs() {
		OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		ArrayList<String> ans = oidbf.getSavedConfigs("giaroot_2d");
		return ans;
	}

	public int getRequiredInputs() {
		return InputOutputTypes.RAW;
	}

	public int getPossibleOutputs() {
		return InputOutputTypes.DESCRIPTORS_2D | InputOutputTypes.GRAYSCALE
				| InputOutputTypes.SKELETON | InputOutputTypes.THRESHOLD;
	}

	public int getOutputs(File f) {
		boolean hasThreshold = false;
		boolean hasGrayScale = false;
		boolean hasDescriptors = false;
		boolean hasSkeleton = false;

		File[] fs = f.listFiles();
		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				String s = fs[i].getName();
				if (!hasThreshold && s.contains("thresholded")) {
					hasThreshold = true;
				}
				if (!hasGrayScale && s.contains("gray")) {
					hasGrayScale = true;
				}
				if (!hasDescriptors && s.equals("giaroot_2d.csv")) {
					hasDescriptors = true;
				}
				if (!hasSkeleton && s.contains("thinned")) {
					hasSkeleton = true;
				}
			}
		}

		int ans = 0;
		ans = (hasThreshold) ? ans | InputOutputTypes.THRESHOLD : ans;
		ans = (hasGrayScale) ? ans | InputOutputTypes.GRAYSCALE : ans;
		ans = (hasDescriptors) ? ans | InputOutputTypes.DESCRIPTORS_2D : ans;
		ans = (hasSkeleton) ? ans | InputOutputTypes.SKELETON : ans;

		return ans;
	}

	public int getOptionalInputs() {
		return InputOutputTypes.CROP;
	}

	public String getName() {
		return "giaroot_2d";
	}

	public int getOutputs() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Process start(GiaRoot2DInput input, OutputInfo output) {
		String[] cmd = {
                // tw 2015jan9 remove "nice" for Windows
//				"nice",
				giaExecPath,
				new File(output.getDir() + File.separator + JOB_XML_NAME)
						.getAbsolutePath() };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		Map<String, String> m = pb.environment();
		String lp = m.remove("LD_LIBRARY_PATH");
		createAlgorithmsLink(output);
		// FileUtil.createSymLink(new File(giaPath.getAbsolutePath() +
		// File.separator + "algorithms"), algSym);
		m.put("LD_LIBRARY_PATH", lp + ":" + giaPath.getAbsolutePath()
				+ File.separator + "lib");
		// pb.directory(output.getDir());
		pb.directory(giaPath.getAbsoluteFile());

		Process ans = null;
		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new GiaRoot2DException("Error starting process", e);
		}

		return ans;
	}

	private void createAlgorithmsLink(OutputInfo oi) {

        // 2014jan1 tw
        File algDir = new File(giaPath.getAbsolutePath()+ File.separator + "algorithms");
        File linkDir = new File(oi.getDir().getAbsolutePath() + File.separator + "algorithms");

        FileUtil.createDirLink(algDir, linkDir);
        ism.setDirLinkPermissions(linkDir, false);

        for ( File targetFile : algDir.listFiles() ) {
            File symbolicFile = new File(linkDir.toString() + File.separator + targetFile.getName().toString());
            FileUtil.createSymLink(symbolicFile, targetFile);
            ism.setPermissions(symbolicFile, false);
        }
	}

	private void deleteAlgorithmsLink(OutputInfo oi) {
		new File(oi.getDir().getAbsolutePath() + File.separator + "algorithms")
				.delete();
	}

	private File getTemplateFromString(String str) {
		return new File(templateDir + File.separator + str + "." + TEMPLATE_EXT);
	}

	public File getCsvFile(OutputInfo oi) {
		return new File(oi.getDir().getAbsolutePath() + File.separator
				+ Gia2DJobXml.JOB_NAME + ".csv");
	}

	public void preprocess(GiaRoot2DInput input, OutputInfo output,
			ApplicationManager am) {
		// write the gia xml files
		OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		String savedTemplate = oidbf.findSavedConfigContents(input.getTemplateString(),"giaroot_2d");
		GiaConfigXml gcx = new GiaConfigXml(savedTemplate, input
				.getRis().getPreferredType(), OUTPUT_TYPE);
		File configXml = new File(output.getDir() + File.separator
				+ input.getTemplateString() + CONFIG_XML_SUFFIX);
		gcx.write(configXml);

		boolean doCrop = input.getCrop() == null;
		File inputDir = (doCrop) ? input.getRis().getPreferredInputDir() : am
				.getCrop().getCropImageSubdir(input.getCrop());
		Gia2DJobXml gjx = new Gia2DJobXml(inputDir, output.getDir(), configXml,
				doCrop, input.getDescriptors());
		File jobXml2 = new File(output.getDir() + File.separator + JOB_XML_NAME);
		gjx.write(jobXml2);

        // tw 2015jun29
        copyCropProps(input, output);

        am.getIsm().setPermissions(output.getDir(), true);
	}

    // tw 2015jun29
    public void copyCropProps(GiaRoot2DInput input, OutputInfo output){

        String cropProps = input.getCrop().getDir().toString() + File.separator + "crop.properties";
        File cropPropsFile = new File(cropProps);
        Path cropPropsPath = cropPropsFile.toPath();
        String copyProps = output.getDir().toString() + File.separator + "crop.properties";
        File copyPropsFile = new File(copyProps);
        Path copyPropsPath = copyPropsFile.toPath();
        try {
            Files.copy(cropPropsPath, copyPropsPath);
        }catch (IOException e) {
            System.out.println("Error copying " + cropProps + " to " + copyProps);
        }

    }

	public String getTemplateString(OutputInfo oi) {
		String ans = null;
		if (oi.savedConfigID == null) {
			ans = "...";
		} else {
			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			ans = oidbf.findSavedConfigName(oi.savedConfigID);
		}
		return ans;
	}

	/**
	 * Sets permissions on generate files
	 */
	public void postprocess(OutputInfo oi, ApplicationManager am) {
		deleteAlgorithmsLink(oi);
		for (File f : oi.getDir().listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				// am.getIsm().setFilePermissions(f);
				am.getIsm().setPermissions(f, false);
			}
		}
	}

	public File[] getThresholdImages(OutputInfo oi) {
		NameSubstringFileFilter nsf = new NameSubstringFileFilter(
				THRESHOLD_SUBSTR);
		return oi.getDir().listFiles(nsf);
	}

	private static class GiaRoot2DException extends RuntimeException {
		public GiaRoot2DException(String msg, Throwable th) {
			super(msg, th);
		}
	}

	public String getReviewString(OutputInfo oi) {
		return oi.toString() + "(" + getTemplateString(oi) + ")";
	}

}
