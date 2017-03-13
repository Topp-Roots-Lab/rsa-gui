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
 * Responsible for producing the gia job xml file.
 * 
 * @author bm93
 */
public class Gia2DJobXml {
	/*
	 * 
	 * <?xml version="1.0"?> <job config="CONFIG_PATH" driver="xml"
	 * connection_string="OUTPUT_DIR"> <upload connection_string="INPUT_DIR" />
	 * <compute types="croppedimage" /> <export driver="csv"
	 * connection_string="OUTPUT_DIR/JOB_NAME"/> <reinterpret
	 * type="croppedimage" nick="canonicalrootimage" /> <compute types=
	 * "grayimage;thresholdedimage;mediannumberofrootsfeaturevalue;specificrootlengthfeaturevalue"
	 * /> </job> <?xml version="1.0"?> <job config="CONFIG_PATH" driver="xml"
	 * connection_string="OUTPUT_DIR"> <upload connection_string="INPUT_DIR" />
	 * <compute types="croppedimage" /> <export driver="csv"
	 * connection_string="OUTPUT_DIR/JOB_NAME"/> <reinterpret
	 * type="croppedimage" nick="canonicalrootimage" /> <compute types=
	 * "averagerootwidthfeaturevalue;bushinessfeaturevalue;croppedimage;ellipseaxesaspectratiofeaturevalue;grayimage;majorellipseaxesfeaturevalue;maximumnumberofrootsfeaturevalue;mediannumberofrootsfeaturevalue;minorellipseaxesfeaturevalue;networkareafeaturevalue;networkconvexareafeaturevalue;perimeterfeaturevalue;solidityfeaturevalue;specificrootlengthfeaturevalue;thinnedimage;thresholdedimage"
	 * /> </job>
	 * 
	 * <?xml version="1.0"?> <job config="CONFIG_PATH" driver="xml"
	 * connection_string="OUTPUT_DIR"> <export driver="csv"
	 * connection_string="OUTPUT_DIR/JOB_NAME"/> <upload
	 * connection_string="INPUT_DIR" /> <reinterpret type="rawimage"
	 * nick="canonicalrootimage" /> <compute types=
	 * "averagerootwidthfeaturevalue;bushinessfeaturevalue;ellipseaxesaspectratiofeaturevalue;majorellipseaxesfeaturevalue;maximumnumberofrootsfeaturevalue;mediannumberofrootsfeaturevalue;minorellipseaxesfeaturevalue;networkareafeaturevalue;networkconvexareafeaturevalue;perimeterfeaturevalue;solidityfeaturevalue;specificrootlengthfeaturevalue"
	 * /> </job>
	 */

	public static final String JOB_NAME = "giaroot_2d";

	protected File inputDir;
	protected File outputDir;
	protected File configFile;
	protected boolean doCrop;
	protected String descriptors;

	public Gia2DJobXml(File inputDir, File outputDir, File configFile,
			boolean doCrop, String descriptors) {
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.configFile = configFile;
		this.doCrop = doCrop;
		this.descriptors = descriptors;
	}

	public void write(File f) {
		String eol = System.getProperty("line.separator");
		String s1 = "<?xml version=\"1.0\"?>"
				+ eol
				+ "<job config=\""
				+ configFile.getAbsolutePath()
				+ "\" driver=\"xml\" connection_string=\""
				+ outputDir.getAbsolutePath()
				+ "\">"
				+ eol
				+ "    <export driver=\"csv\" connection_string=\""
				+ outputDir.getAbsolutePath()
				+ File.separator
				+ JOB_NAME
				+ ".csv\"/>"
				+ eol
				+ ((!doCrop) ? "" : "    <compute types=\"croppedimage\" />"
						+ eol)
				+ "    <upload connection_string=\""
				+ inputDir.getAbsolutePath()
				+ "\" />"
				+ eol
				+ ((!doCrop) ? "    <reinterpret type=\"rawimage\" nick=\"canonicalrootimage\" />"
						: "    <reinterpret type=\"croppedimage\" nick=\"canonicalrootimage\" />")
				+ eol + "    <compute types=\"" + descriptors + "\"/>" + eol
				+ "</job>" + eol;

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(s1);
		} catch (IOException e) {
			throw new GiaJobXmlException(e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {

			}
		}
	}

	public static class GiaJobXmlException extends RuntimeException {
		public GiaJobXmlException(Throwable th) {
			super(th);
		}
	}
}
