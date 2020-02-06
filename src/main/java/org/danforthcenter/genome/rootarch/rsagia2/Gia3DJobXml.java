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
 * Creates the job xml that GiaRoots uses to computer a set of 3D descriptors
 * off of a rootwork model.
 * 
 * 
 * @author bm93
 */
public class Gia3DJobXml {

	/*
	 * <?xml version="1.0"?> <job
	 * config="/Users/tarasgalkovskyi/dev/gia/dist/jobs/config-corn-azu.xml"
	 * driver="xml"
	 * connection_string="/Users/tarasgalkovskyi/dev/gia/dist/test-proj/">
	 * <export driver="csv" connection_string="./test1-job.csv"/> <upload
	 * connection_string="./test-data/" /> <compute types=
	 * "bushinessfeature3d;convexvolumefeaturevolumetric3d;maximumnumberofrootsfeature3d;mediannumberofrootsfeature3d;surfacefeatureplanar3d;volumefeaturevolumetric3d"
	 * /> </job>
	 */

	protected File configFile;
	protected File inputDir;
	protected File outputDir;
	protected String descriptors;

	public Gia3DJobXml(File configFile, File inputDir, File outputDir,
			String descriptors) {
		this.configFile = configFile;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.descriptors = descriptors;
	}

	public void write(File jobFile) {
		String eol = System.getProperty("line.separator");
		String s = "<?xml version=\"1.0\"?>";
		s += eol + "<job config=\"" + configFile.getAbsolutePath()
				+ "\" driver=\"xml\" connection_string=\""
				+ outputDir.getAbsolutePath() + "\">";
		s += eol + "\t" + "<export driver=\"csv\" connection_string=\""
				+ outputDir.getAbsolutePath() + File.separator
				+ GiaRoot3DOutput.CSV_FILE + "\"/>";
		s += eol + "\t" + "<upload connection_string=\""
				+ inputDir.getAbsolutePath() + "\"/>";
		s += eol + "\t" + "<compute types=\"" + descriptors + "\" />";
		s += eol + "</job>" + eol;

		BufferedWriter bw = null;
		try {
			try {
				bw = new BufferedWriter(new FileWriter(jobFile));
				bw.write(s);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		} catch (IOException e) {
			throw new Gia3DJobXmlException("Unable to write to file: "
					+ jobFile.getAbsolutePath(), e);
		}
	}

	protected static class Gia3DJobXmlException extends RuntimeException {
		public Gia3DJobXmlException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
