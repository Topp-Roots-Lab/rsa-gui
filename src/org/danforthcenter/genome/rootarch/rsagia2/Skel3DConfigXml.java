/*
 *  Copyright 2012 vp23.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author vp23
 */
public class Skel3DConfigXml {

	/*
	 * <property name="configname" value="2.25" /> <property
	 * name="serialize_image_format" value="iv" /> <property name="scale"
	 * value="2.25" />
	 */

	/*
	 * <property name="configname" value="2.25" /> <property
	 * name="serialize_image_format" value="wrl" /> <property name="scale"
	 * value="2.25" />
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
	public Skel3DConfigXml(File template, String inputFormat,
			String outputFormat) {
		this.template = template;
		this.inputFormat = inputFormat;
		this.outputFormat = outputFormat;
	}

	public List<ConfigProperty> getConfigPropertyies() throws Exception {
		try {
			List<ConfigProperty> configProperties;
			Skel3DConfigProperties skelProps = new Skel3DConfigProperties()
					.getInstance();
			skelProps.populateConfigProperties();
			configProperties = skelProps.getConfigProperties();
			return configProperties;
		} catch (Exception exc) {
			throw new Skel3DConfigXmlException("getConfigPropertyies()", exc);
		}
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
			throw new Skel3DConfigXmlException(e);
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

	public class Skel3DConfigProperties {

		List<ConfigProperty> ConfigProperties;
		Document dom;
		File skel3Dconfigxml;
		Skel3DConfigProperties skelProps;

		public Skel3DConfigProperties getInstance() {
			if (skelProps == null) {
				skelProps = new Skel3DConfigProperties();
			}
			;
			return skelProps;
		}

		private Skel3DConfigProperties() {
			this.skel3Dconfigxml = template;
			// create a list to hold the ConfigProperty objects
			ConfigProperties = new ArrayList<ConfigProperty>();
		}

		public void populateConfigProperties() throws Exception {
			try {
				// parse the xml file and get the dom object
				parseXmlFile(skel3Dconfigxml);
				// get each ConfigProperty element and create a ConfigProperty
				// object
				parseDocument();
				// Iterate through the list and print the data
				// printData();
			} catch (Exception exp) {
				throw exp;
			}
		}

		public List<ConfigProperty> getConfigProperties() {
			return ConfigProperties;
		}

		private void parseXmlFile(File xml) throws Exception {
			// get the factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				// Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();
				// parse using builder to get DOM representation of the XML file
				dom = db.parse(xml);
			} catch (ParserConfigurationException pce) {
				// pce.printStackTrace();
				throw pce;
			} catch (SAXException se) {
				// se.printStackTrace();
				throw se;
			} catch (IOException ioe) {
				// ioe.printStackTrace();
				throw ioe;
			}
		}

		private void parseDocument() {
			// get the root elememt
			Element docEle = dom.getDocumentElement();
			// get a nodelist of <property> elements
			NodeList nl = docEle.getElementsByTagName("property");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					// get the ConfigProperty element
					Element el = (Element) nl.item(i);
					// get the ConfigProperty object
					ConfigProperty e = getProperty(el);
					// add it to list
					ConfigProperties.add(e);
				}
			}
		}

		private ConfigProperty getProperty(Element prop) {

			String name = getAttrValue(prop, "name");
			String value = getAttrValue(prop, "value");
			ConfigProperty e = new ConfigProperty(name, value);

			return e;
		}

		private String getAttrValue(Element ele, String attrName) {
			String textVal = null;
			String attrvalue = ele.getAttribute(attrName);
			return attrvalue;
		}

		private void printData() {

			System.out.println("Number of properties '"
					+ ConfigProperties.size() + "'.");

			Iterator it = ConfigProperties.iterator();
			while (it.hasNext()) {
				System.out.println(it.next().toString());
			}
		}

		// public static void main(String[] args){
		// //create an instance
		// DomParserExample dpe = new DomParserExample();
		//
		// //call run example
		// dpe.runExample();
		// }

	}

	public class ConfigProperty {

		private String name;
		private String value;

		public ConfigProperty() {

		}

		public ConfigProperty(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ConfigProperty Details - ");
			sb.append("name:" + getName());
			sb.append(", ");
			sb.append("value:" + getValue());
			sb.append(".");

			return sb.toString();
		}
	}

	public static class Skel3DConfigXmlException extends RuntimeException {
		public Skel3DConfigXmlException(Throwable th) {
			super(th);
		}

		public Skel3DConfigXmlException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
