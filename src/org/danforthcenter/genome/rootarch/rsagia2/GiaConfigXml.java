/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Responsible for producing the gia config xml file based off of some runtime
 * parameters and a template xml.
 * 
 * @author bm93
 */
public class GiaConfigXml {

	/*
	 * <property name="image_format" value="jpg" /> <property
	 * name="serialize_image_format" value="tiff" />
	 */

	public static final String INPUT_FORMAT_PLACEHOLDER = "${INPUT_IMAGE_TYPE}";
	public static final String OUTPUT_FORMAT_PLACEHOLDER = "${OUTPUT_IMAGE_TYPE}";

	protected File template;
	protected String inputFormat;
	protected String outputFormat;

	/**
	 * 
	 * @param template
	 * @param inputFormat
	 *            - no longer needed
	 * @param outputFormat
	 */
	public GiaConfigXml(File template, String inputFormat, String outputFormat) {
		this.template = template;
		this.inputFormat = inputFormat;
		this.outputFormat = outputFormat;
	}

	public void write(File f) {
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new FileReader(template));
			bw = new BufferedWriter(new FileWriter(f));

			String s = null;
			while ((s = br.readLine()) != null) {
				if (s.contains("serialize_image_format")) {
					bw.write("<property name=\"serialize_image_format\" value=\""
							+ outputFormat
							+ "\" />"
							+ System.getProperty("line.separator"));
				} else {
					bw.write(s + System.getProperty("line.separator"));
				}
			}
		} catch (IOException e) {
			throw new GiaConfigXmlException(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {

			}
		}
	}

	public static class GiaConfigXmlException extends RuntimeException {
		public GiaConfigXmlException(Throwable th) {
			super(th);
		}
	}
}
