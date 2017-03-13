/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A class for executing GiaRoot in general. It needs to be setup differently
 * for 2D vs 3D.
 * 
 * @author bm93
 */
public class GiaRoot {
	protected final static String TEMPLATE_EXT = "xml";

	protected String giaExecPath;
	protected File giaPath;

	public static final String OUTPUT_TYPE = "tiff";
	public static final String CONFIG_XML_SUFFIX = "-gia-config.xml";
	public static final String JOB_XML_NAME = "job-config.xml";

    protected ISecurityManager ism;


	public GiaRoot(String giaExecPath, File giaPath, ISecurityManager ism) {
		this.giaExecPath = giaExecPath;
		this.giaPath = giaPath;
        // 2015jan1 tw
        this.ism = ism;
	}

	/**
	 * Assumes a previously written job xml file exists at JOB_XML_NAME.
	 * 
	 * @param output
	 * @return
	 */
	public Process start(File jobFile, File dir) {
		String[] cmd = { "nice", giaExecPath, jobFile.getAbsolutePath() };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		Map<String, String> m = pb.environment();
		String lp = m.remove("LD_LIBRARY_PATH");
		createAlgorithmsLink(dir);
		m.put("LD_LIBRARY_PATH", lp + ":" + giaPath.getAbsolutePath()
				+ File.separator + "lib");
		// pb.directory(dir);
		pb.directory(giaPath);

		Process ans = null;
		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new GiaRootException("Error starting process", e);
		}

		return ans;
	}

	/**
	 * For whatever reason, there's a linking issue that currently remains
	 * unresolved in GiaRoots. If it can't find the algorithms directory in the
	 * current working directory, it will fail.
	 * 
	 * @param oi
	 */
	protected void createAlgorithmsLink(File dir) {

        // 2014jan1 tw
        File algDir = new File(giaPath.getAbsolutePath()+ File.separator + "algorithms");
        File linkDir = new File(dir.getAbsolutePath() + File.separator + "algorithms");

		FileUtil.createDirLink(algDir, linkDir);

        for ( File targetFile : algDir.listFiles() ) {
            File symbolicFile = new File(linkDir.toString() + File.separator + targetFile.getName().toString());
            FileUtil.createSymLink(symbolicFile, targetFile);
        }

	}

	protected void deleteAlgorithmsLink(File dir) {
		new File(dir.getAbsolutePath() + File.separator + "algorithms")
				.delete();
	}

	/**
	 * Sets permissions on generate files
	 */
	public void postprocess(File dir, ApplicationManager am) {
		deleteAlgorithmsLink(dir);
		for (File f : dir.listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				// am.getIsm().setFilePermissions(f);
				am.getIsm().setPermissions(f, false);
			}
		}
	}

	protected static class GiaRootException extends RuntimeException {
		public GiaRootException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
