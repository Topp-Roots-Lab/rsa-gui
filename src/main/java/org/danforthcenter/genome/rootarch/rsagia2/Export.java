/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is responsible for creating spreadsheets from multiple runs of
 * GiaRoots. It creates two sheets: rawFile and aggrFile. It also contains the
 * information on how to apply scale to the gia roots descriptors.
 * 
 * It also will export 3d descriptors into a single file.
 * 
 * It's a horrific class. It will be obsolete once we start loading data into
 * the database.
 * 
 * @author bm93, vp23
 */
public class Export {
	protected File idFile;
	protected Scale scale;
	protected GiaRoot2D gia;
	protected HashMap<String, IScale> scaleMap;
	protected HashMap<String, String> synonymMap;
	protected HashMap<String, IScale> scale3dMap;
	protected HashMap<String, String> synonym3dMap;
	protected ArrayList<String> desc3dList;

	protected HashMap<String, IScale> scaleGia3d_v2Map;
	protected HashMap<String, String> synonymGia3d_v2Map;
	protected ArrayList<String> descGia3d_v2List;
	protected MetadataDBFunctions mdf;

	public Export(File idFile, Scale scale, GiaRoot2D gia, String configString,
			String config3dString,
			String configGia3d_v2Config) {
		this.idFile = idFile;
		this.scale = scale;
		this.gia = gia;
		mdf = new MetadataDBFunctions();
		scaleMap = new HashMap<String, IScale>();
		synonymMap = new HashMap<String, String>();
		scale3dMap = new HashMap<String, IScale>();
		synonym3dMap = new HashMap<String, String>();
		desc3dList = new ArrayList<String>();
		scaleGia3d_v2Map = new HashMap<String, IScale>();
		synonymGia3d_v2Map = new HashMap<String, String>();
		descGia3d_v2List = new ArrayList<String>();
		loadFromConfigString(configString, scaleMap, synonymMap,
				new ArrayList<String>());
		loadFromConfigString(config3dString, scale3dMap, synonym3dMap,
				desc3dList);
		loadFromConfigString(configGia3d_v2Config, scaleGia3d_v2Map,
				synonymGia3d_v2Map, descGia3d_v2List);
	}

	private void loadFromConfigString(String configString,
			HashMap<String, IScale> scaleMap,
			HashMap<String, String> synonymMap, ArrayList<String> descList) {
		String[] ss1 = configString.split(";");
		for (String s1 : ss1) {
			String[] ss2 = s1.split(",");
			IScale isc = null;
			if (ss2[1].equals("linear")) {
				isc = new LinearScale();
			} else if (ss2[1].equals("none")) {
				isc = new NoScale();
			} else if (ss2[1].equals("squared")) {
				isc = new SquaredScale();
			}
			// vp23
			else if (ss2[1].equals("inversesquared")) {
				isc = new InverseSquaredScale();
			} else if (ss2[1].equals("cubed")) {
				isc = new CubedScale();
			}

			descList.add(ss2[0]);
			scaleMap.put(ss2[0], isc);
			for (int i = 2; i < ss2.length; i++) {
				synonymMap.put(ss2[i], ss2[0]);
			}
		}
	}

	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		boolean hasScale = false;
		boolean has2D = false;

		for (OutputInfo oi : OutputInfo.getInstances(am, ris, true, false,
				null, false)) {
			if (oi.isValid()) {
				if ((oi.getOutputs() & InputOutputTypes.SCALE) > 0) {
					hasScale = true;
				}
				if ((oi.getOutputs() & InputOutputTypes.DESCRIPTORS_2D) > 0) {
					has2D = true;
				}
			}

			if (hasScale && has2D) {
				break;
			}
		}

		return hasScale && has2D;
	}

	/**
	 * Write an export file for 3D descriptors. Aggregates descriptors from a
	 * set of RsaImageSets.
	 * 
	 * Format is plant descriptors, source_id, and descriptor list. The order of
	 * the descriptor list is set by the config3dString.
	 * 
	 * Scales if necessary.
	 * 
	 * @param riss
	 * @param scaleMap
	 * @param desc3dMap
	 * @param outFile
	 */
	public void export3D(ArrayList<RsaImageSet> riss,
			HashMap<RsaImageSet, IOutputScale> scaleMap,
			HashMap<RsaImageSet, IOutputDescriptors3D> desc3dMap, File outFile) {
		ArrayList<String[]> sheet = new ArrayList<String[]>();
		int jOffset = 6;
		String[] ss1 = new String[desc3dList.size() + jOffset];
		ss1[0] = "Species";
		ss1[1] = "Experiment";
		ss1[2] = "Plant";
		ss1[3] = "ImagingDay";
		ss1[4] = "source_id";
		ss1[5] = "Scale";
		for (int i = 0; i < desc3dList.size(); i++) {
			ss1[jOffset + i] = desc3dList.get(i);
		}

		sheet.add(ss1);

		for (int i = 0; i < riss.size(); i++) {
			RsaImageSet ris = riss.get(i);
			double scale2 = scaleMap.get(ris).getScale();
			String[] row = null;
			if (desc3dMap.containsKey(ris)) {
				BufferedReader br = null;
				File f = desc3dMap.get(ris).getCsvFile();
				try {
					br = new BufferedReader(new FileReader(f));
					String s1 = br.readLine();
					String s2 = br.readLine();
					String[] hdrs = s1.split(",");
					HashMap<String, Integer> hdrMap = new HashMap<String, Integer>();
					for (int j = 0; j < hdrs.length; j++) {
						String h = synonym3dMap.containsKey(hdrs[j]) ? synonym3dMap
								.get(hdrs[j]) : hdrs[j];
						hdrMap.put(h, j);
					}

					String[] vals = s2.split(",");

					row = new String[desc3dList.size() + jOffset];
					row[0] = ris.getSpecies();
					row[1] = ris.getExperiment();
					row[2] = ris.getPlant();
					row[3] = ris.getImagingDay();
					row[4] = vals[0];
					row[5] = Double.toString(scale2);

					for (int j = jOffset; j < ss1.length; j++) {
						String v = "";
						if (hdrMap.containsKey(ss1[j])) {
							v = vals[hdrMap.get(ss1[j])];
							try {
								if (scale3dMap.containsKey(ss1[j])) {
									double d = Double.parseDouble(v);
									double d2 = scale3dMap.get(ss1[j]).scale(d,
											scale2);
									v = Double.toString(d2);
									//
									// check with Crhis ones again - delete if
									// not needed
									//
									// //////////////////////////////// no need
									// for this hack,because
									// ////////////////////////////////
									// t3rf_solidity is calculated by gia-roots
									// -
									// //////////////////////////////// add
									// t3rf_solidity in the default.properties
									// //////////////////////////////// to
									// gia3d_descriptors: ...., t3rf_solidity
									// //////////////////////////////// and
									// export_3d_config: ....;
									// Solidity3D,none,t3rf_solidity
									// ///////////////////////////////////////////
									// // vp23 hack-add-solidity
									// //////////////////////////////////////////
									// //
									// // for adding Solidity3D, which is not
									// // calculated by gia-roots
									// // (it should be taken from gia-roots
									// result, but this is a hack)
									// // Also, notice Solidity3DOlgaTaras is
									// not presented
									// // in the giaroot_3d.csv - it is only for
									// the export
									// //
									// // calculate the value for solidity here
									// // Solidity3DOlgaTaras =
									// // =
									// taras_RootSystemVolume3D/taras_ConvexHullVolume3D=
									// // according to config
									// // t3rf_solidity =
									// t3rf_volume/t3rf_convex_volume
									// //
									// if (ss1[j].equals("Solidity3D")) {
									// String v1 =
									// vals[hdrMap.get("RootSystemVolume3D")];
									// String v2 =
									// vals[hdrMap.get("ConvexHullVolume3D")];
									// double dv1 = Double.parseDouble(v1);
									// double dv2 = Double.parseDouble(v2);
									// // no scale needed, because both
									// // o3rf_volume, t3rf_convex_volume ~ mm^3
									// d = dv1/dv2;
									// v = Double.toString(d);
									// }
									// ///////////////////////////////////////////
									// // end of vp23 hack-add-solidity
									// //////////////////////////////////////////
								}
							} catch (NumberFormatException e) {
								v = "";
							}
						}

						row[j] = v;
					}

					sheet.add(row);
				} catch (Exception e) {
					throw new ExportException("Error parsing file:"
							+ f.getAbsolutePath(), e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {

						}
					}
				}
			}
		}

		// add scale identifiers to header
		for (int i = 0; i < desc3dList.size(); i++) {
			sheet.get(0)[jOffset + i] = desc3dList.get(i)
					+ (scale3dMap.containsKey(desc3dList.get(i)) ? scale3dMap
							.get(desc3dList.get(i)).toString() : "");
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new ExportException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}


	/**
	 * Write an export file for 3D descriptors. Aggregates descriptors from a
	 * set of RsaImageSets.
	 * 
	 * Format is plant descriptors, source_id, and descriptor list. The order of
	 * the descriptor list is set by the config3dString.
	 * 
	 * Scales if necessary.
	 * 
	 * @param riss
	 * @param scaleMap
	 * @param descGia3d_v2Map
	 * @param outFile
	 */
	public void exportGia3d_v2(ArrayList<RsaImageSet> riss,
			HashMap<RsaImageSet, IOutputScale> scaleMap,
			HashMap<RsaImageSet, IOutputDescriptorsSkeleton3D> descGia3d_v2Map,
			File outFile) {
		ArrayList<String[]> sheet = new ArrayList<String[]>();
		int jOffset = 16;
		String[] ss1 = new String[descGia3d_v2List.size() + jOffset];
		ss1[0] = "Species";
		ss1[1] = "Experiment";
		ss1[2] = "Plant";
		ss1[3] = "ImagingDay";
		ss1[4] = "source_id";
		ss1[5] = "Scale";
		ss1[6] = "Genotype";
		ss1[7] = "Dry Shoot";
		ss1[8] = "Dry Root";
		ss1[9] = "Wet Shoot";
		ss1[10] = "Wet Root";
		ss1[11] = "Sterilization Chamber";
		ss1[12] = "RowColumn";
		ss1[13] = "Imaging Interval Unit";
		ss1[14] = "Description";
		ss1[15] = "Imaging Start Date";

		for (int i = 0; i < descGia3d_v2List.size(); i++) {
			ss1[jOffset + i] = descGia3d_v2List.get(i);
		}

		sheet.add(ss1);

		for (int i = 0; i < riss.size(); i++) {
			RsaImageSet ris = riss.get(i);
			double scale2 = scaleMap.get(ris).getScale();
			String[] row = null;
			if (descGia3d_v2Map.containsKey(ris)) {
				BufferedReader br = null;
				File f = descGia3d_v2Map.get(ris).getTsvFile();
				try {
					br = new BufferedReader(new FileReader(f));
					String s1 = br.readLine();
					String s2 = br.readLine();
					String[] hdrs = s1.split("\t");
					HashMap<String, Integer> hdrMap = new HashMap<String, Integer>();
					for (int j = 0; j < hdrs.length; j++) {
						String h = synonymGia3d_v2Map.containsKey(hdrs[j]) ? synonymGia3d_v2Map
								.get(hdrs[j]) : hdrs[j];
						hdrMap.put(h, j);
					}

					String[] vals = s2.split("\t");

					row = new String[descGia3d_v2List.size() + jOffset];
					row[0] = ris.getSpecies();
					row[1] = ris.getExperiment();
					row[2] = ris.getPlant();
					row[3] = ris.getImagingDay();
					row[4] = vals[0];
					row[5] = Double.toString(scale2);

					int datasetID = ris.getDatasetID();
					Result<Record> seedRecord = this.mdf.findSeedFromDatasetID(datasetID);
					Record r = seedRecord.get(0);
					String genotype_name = "";
					String dry_shoot = "";
					String dry_root = "";
					String wet_shoot = "";
					String wet_root = "";
					String sterilization = "";
					String rowcolumn = "";
					String img_interval_unit = "";
					String description = "";
					String img_start_date = "";

					if(r.getValue("genotype_name")!=null)
					{
						genotype_name = (String) r.getValue("genotype_name");
					}
					if(r.getValue("dry_shoot")!=null)
					{
						dry_shoot = Double.toString((Double) r.getValue("dry_shoot"));
					}
					if(r.getValue("dry_root")!=null)
					{
						dry_root = Double.toString((Double) r.getValue("dry_root"));
					}
					if(r.getValue("wet_shoot")!=null)
					{
						wet_shoot = Double.toString((Double) r.getValue("wet_shoot"));
					}
					if(r.getValue("wet_root")!=null)
					{
						wet_root = Double.toString((Double) r.getValue("wet_root"));
					}

					String strChamberRowColumn = (String) r.getValue("sterilization_chamber");

					if(strChamberRowColumn!=null)
					{
						sterilization = strChamberRowColumn.split("-")[0];
						rowcolumn = strChamberRowColumn.split("-")[1];
					}
					if(r.getValue("imaging_interval_unit")!=null)
					{
						img_interval_unit =  (String) r.getValue("imaging_interval_unit");
					}
					if(r.getValue("description")!=null)
					{
						description = (String) r.getValue("description");
					}
					if(r.getValue("imaging_start_date")!=null)
					{
						Date imagingStart = (Date) r.getValue("imaging_start_date");
						img_start_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(imagingStart);
					}

					row[6] = genotype_name;
					row[7] = dry_shoot;
					row[8] = dry_root;
					row[9] = wet_shoot;
					row[10] = wet_root;
					row[11] = sterilization;
					row[12] = rowcolumn;
					row[13] = img_interval_unit;
					row[14] = description;
					row[15] = img_start_date;

					for (int j = jOffset; j < ss1.length; j++) {
						String v = "";
						if (hdrMap.containsKey(ss1[j])) {
							v = vals[hdrMap.get(ss1[j])];
							try {
								if (scaleGia3d_v2Map.containsKey(ss1[j])) {
									double d = Double.parseDouble(v);
									double d2 = scaleGia3d_v2Map.get(ss1[j])
											.scale(d, scale2);
									v = Double.toString(d2);
								}
							} catch (NumberFormatException e) {
								v = "";
							}
						}

						row[j] = v;
					}

					sheet.add(row);
				} catch (Exception e) {
					throw new ExportException("Error parsing file:"
							+ f.getAbsolutePath(), e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {

						}
					}
				}
			}
		}

		// add scale identifiers to header
		for (int i = 0; i < descGia3d_v2List.size(); i++) {
			sheet.get(0)[jOffset + i] = descGia3d_v2List.get(i)
					+ (scaleGia3d_v2Map.containsKey(descGia3d_v2List.get(i)) ? scaleGia3d_v2Map
							.get(descGia3d_v2List.get(i)).toString() : "");
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new ExportException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * Write an export file for Qc2 descriptors. Aggregates descriptors from a
	 * set of RsaImageSets.
	 * 
	 * Headers are hard coded for now TODO: need to add a config setting, etc
	 * 
	 * Scales if necessary.
	 * 
	 * @param riss
	 * @param scaleMap
	 * @param descQc2Map
	 * @param outFile
	 */
	public void exportQc2(ArrayList<RsaImageSet> riss,
			HashMap<RsaImageSet, IOutputScale> scaleMap,
			HashMap<RsaImageSet, IOutputDescriptorsQc2> descQc2Map, File outFile) {
		// the headers (the first line) in the "..._qc2.csv" file
		// Species Experiment Plant ImagingDay Passed Failed Other Recrop Top
		// Recrop Bottom Recrop Sides Noise Roots Noise Spots Noise Cone Noise
		// Other Bad Images % Folder Path Template

		ArrayList<String[]> sheet = new ArrayList<String[]>();
		int jOffset = 0;
		int Num = 17;
		String[] ss1 = new String[Num + jOffset];
		//
		// hard coded here - need to add a config setting
		//
		// these are the headers in every "..._qc2.csv" file (the first line)
		ss1[0] = "Species";
		ss1[1] = "Experiment";
		ss1[2] = "Plant";
		ss1[3] = "ImagingDay";
		ss1[4] = "Passed";
		ss1[5] = "Failed";
		ss1[6] = "Other";
		ss1[7] = "Recrop Top";
		ss1[8] = "Recrop Bottom";
		ss1[9] = "Recrop Sides";
		ss1[10] = "Noise Roots";
		ss1[11] = "Noise Spots";
		ss1[12] = "Noise Cone";
		ss1[13] = "Noise Other";
		ss1[14] = "Bad Images %";
		ss1[15] = "Folder Path";
		ss1[16] = "Template";

		sheet.add(ss1);

		for (int i = 0; i < riss.size(); i++) {
			RsaImageSet ris = riss.get(i);
			String[] vals = null;
			if (descQc2Map.containsKey(ris)) {
				BufferedReader br = null;
				File f = descQc2Map.get(ris).getCsvFile();
				try {
					br = new BufferedReader(new FileReader(f));
					String s1 = br.readLine();
					String s2 = br.readLine();
					vals = s2.split(",");
					sheet.add(vals);
				} catch (Exception e) {
					throw new ExportException("Error parsing file:"
							+ f.getAbsolutePath(), e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {

						}
					}
				}
			}
		}

		//
		// write to file
		//
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new ExportException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * Write an export file for Qc3 descriptors. Aggregates descriptors from a
	 * set of RsaImageSets.
	 * 
	 * Headers are hard coded for now TODO: need to add a config setting, etc
	 * 
	 * Scales if necessary.
	 * 
	 * @param riss
	 * @param scaleMap
	 * @param descQc3Map
	 * @param outFile
	 */
	public void exportQc3(ArrayList<RsaImageSet> riss,
			HashMap<RsaImageSet, IOutputScale> scaleMap,
			HashMap<RsaImageSet, IOutputDescriptorsQc3> descQc3Map, File outFile) {
		// the headers (the first line) in the "..._qc3.csv" file
		// Species Experiment Plant ImagingDay Passed Failed Other Folder Path

		ArrayList<String[]> sheet = new ArrayList<String[]>();
		int jOffset = 0;
		int Num = 8;
		String[] ss1 = new String[Num + jOffset];
		//
		// hard coded here - need to add a config setting
		//
		// these are the headers in every "..._qc2.csv" file (the first line)
		ss1[0] = "Species";
		ss1[1] = "Experiment";
		ss1[2] = "Plant";
		ss1[3] = "ImagingDay";
		ss1[4] = "Passed";
		ss1[5] = "Failed";
		ss1[6] = "Other";
		ss1[7] = "Folder Path";

		sheet.add(ss1);

		for (int i = 0; i < riss.size(); i++) {
			RsaImageSet ris = riss.get(i);
			String[] vals = null;
			if (descQc3Map.containsKey(ris)) {
				BufferedReader br = null;
				File f = descQc3Map.get(ris).getCsvFile();
				try {
					br = new BufferedReader(new FileReader(f));
					String s1 = br.readLine();
					String s2 = br.readLine();
					vals = s2.split(",");
					sheet.add(vals);
				} catch (Exception e) {
					throw new ExportException("Error parsing file:"
							+ f.getAbsolutePath(), e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {

						}
					}
				}
			}
		}

		//
		// write to file
		//
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new ExportException("Error writing to file: "
					+ outFile.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}

	}

	public void export(ArrayList<OutputInfo> scales,
			ArrayList<OutputInfo> gias, File rawFile, File aggrFile) {
		ArrayList<Double> scaleValues = new ArrayList<Double>();
		for (int i = 0; i < scales.size(); i++) {
			scaleValues.add(scale.getScale(scales.get(i)));
		}

		ArrayList<String[]> rawSheet = new ArrayList<String[]>();
		ArrayList<String[]> aggrSheet = new ArrayList<String[]>();

		Set<String> descriptors = scaleMap.keySet();
		HashMap<String, Integer> rawHeader = getRawHeader(descriptors);
		HashMap<String, Integer> aggrHeader = getAggrHeader(descriptors);

		for (int i = 0; i < gias.size(); i++) {
			// for the time being - fix should be in the appropriate place
			OutputInfo oi = gias.get(i);
			if (oi != null)
				addToSheets(gias.get(i), scaleValues.get(i), rawHeader,
						rawSheet, aggrHeader, aggrSheet);
		}

		writeSheet(rawHeader, rawSheet, rawFile);
		writeSheet(aggrHeader, aggrSheet, aggrFile);
	}

	protected void writeSheet(HashMap<String, Integer> header,
			ArrayList<String[]> sheet, File f) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			String[] ss = new String[header.size()];
			for (Map.Entry<String, Integer> ent : header.entrySet()) {
				// remove the word featurevalue from all the headers, by request
				// BM
				String s = ent.getKey();// .replace("featurevalue", "");
				if (scaleMap.containsKey(s)) {
					s = s + scaleMap.get(s).toString();
				}
				ss[ent.getValue()] = s;
			}

			writeLine(ss, bw);
			for (int i = 0; i < sheet.size(); i++) {
				writeLine(sheet.get(i), bw);
			}
		} catch (IOException e) {
			throw new ExportException("Error write to file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {

				}
			}
		}
	}

	protected void writeLine(String[] line, BufferedWriter bw)
			throws IOException {
		if (line.length > 0) {
			bw.write(line[0]);
		}
		for (int i = 1; i < line.length; i++) {
			bw.write("," + line[i]);
		}
		bw.write(System.getProperty("line.separator"));
	}

	protected void addToSheets(OutputInfo oi, double scale,
			HashMap<String, Integer> rawHeader, ArrayList<String[]> rawSheet,
			HashMap<String, Integer> aggrHeader, ArrayList<String[]> aggrSheet) {
		BufferedReader br = null;
		File f = gia.getCsvFile(oi);
		RsaImageSet ris = oi.getRis();

		try {
			br = new BufferedReader(new FileReader(f));

			// first, read the header out of the file
			String s = br.readLine();
			ArrayList<String> fileHeader = new ArrayList<String>();
			String delim = ",";
			if (s.contains(";")) {
				delim = ";";
			}
			String[] ss = s.split(delim);
			for (String s2 : ss) {
				if (synonymMap.containsKey(s2)) {
					s2 = synonymMap.get(s2);
				}
				fileHeader.add(s2);
			}

			int runID = oi.getRunID();
			Result<Record> seedRecord = this.mdf.findSeedInfoFromRunID(runID);
			Record r = seedRecord.get(0);

			int n = 0;
			double[] means = new double[fileHeader.size()];
			while ((s = br.readLine()) != null) {
				String[] row1 = new String[rawHeader.size()];
				row1[rawHeader.get("gia_dir")] = oi.getDir().getAbsolutePath();
				row1[rawHeader.get("species")] = ris.getSpecies();
				row1[rawHeader.get("experiment")] = ris.getExperiment();
				row1[rawHeader.get("plant")] = ris.getPlant();
				row1[rawHeader.get("imaging_day")] = ris.getImagingDay();
				row1[rawHeader.get("scale")] = Double.toString(scale);
				// row1[rawHeader.get("image_number")] = parseImageNumber()

				String genotype_name = "";
				String dry_shoot = "";
				String dry_root = "";
				String wet_shoot = "";
				String wet_root = "";
				String sterilization = "";
				String rowcolumn = "";
				String img_interval_unit = "";
				String description = "";
				String img_start_date = "";

				if(r.getValue("genotype_name")!=null)
				{
					genotype_name = (String) r.getValue("genotype_name");
				}
				if(r.getValue("dry_shoot")!=null)
				{
					dry_shoot = Double.toString((Double) r.getValue("dry_shoot"));
				}
				if(r.getValue("dry_root")!=null)
				{
					dry_root = Double.toString((Double) r.getValue("dry_root"));
				}
				if(r.getValue("wet_shoot")!=null)
				{
					wet_shoot = Double.toString((Double) r.getValue("wet_shoot"));
				}
				if(r.getValue("wet_root")!=null)
				{
					wet_root = Double.toString((Double) r.getValue("wet_root"));
				}

				String strChamberRowColumn = (String) r.getValue("sterilization_chamber");

				if(strChamberRowColumn!=null)
				{
					sterilization = strChamberRowColumn.split("-")[0];
					rowcolumn = strChamberRowColumn.split("-")[1];
				}
				if(r.getValue("imaging_interval_unit")!=null)
				{
					img_interval_unit =  (String) r.getValue("imaging_interval_unit");
				}
				if(r.getValue("description")!=null)
				{
					description = (String) r.getValue("description");
				}
				if(r.getValue("imaging_start_date")!=null)
				{
					Date imagingStart = (Date) r.getValue("imaging_start_date");
					img_start_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(imagingStart);
				}

				row1[rawHeader.get("genotype")] = genotype_name;
				row1[rawHeader.get("dry_shoot")] = dry_shoot;
				row1[rawHeader.get("dry_root")] = dry_root;
				row1[rawHeader.get("wet_shoot")] = wet_shoot;
				row1[rawHeader.get("wet_root")] = wet_root;
				row1[rawHeader.get("sterilization_chamber")] = sterilization;
				row1[rawHeader.get("rowcolumn")] = rowcolumn;
				row1[rawHeader.get("imaging_interval_unit")] = img_interval_unit;
				row1[rawHeader.get("description")] = description;
				row1[rawHeader.get("imaging_start_date")] = img_start_date;

				ss = s.split(delim);
				for (int i = 0; i < ss.length; i++) {
					String h = fileHeader.get(i);
					String s2 = ss[i];
					if (scaleMap.containsKey(h)) {
						try {
							Double d = Double.parseDouble(s2);
							d = scaleMap.get(h).scale(d, scale);
							means[i] += d;
							s2 = Double.toString(d);
						} catch (NumberFormatException e) {
							s2 = "";
						}
					}

					if (rawHeader.containsKey(h)) {
						row1[rawHeader.get(h)] = s2;
					}
					if (h.equals("source_id")) {
						row1[rawHeader.get("image_number")] = Integer
								.toString(parseImageNumber(s2));
					}
				}
				rawSheet.add(row1);
				n++;
			}

			for (int i = 0; i < means.length; i++) {
				means[i] /= (double) n;
			}

			String[] row2 = new String[aggrHeader.size()];
			row2[aggrHeader.get("gia_dir")] = oi.getDir().getAbsolutePath();
			row2[aggrHeader.get("species")] = ris.getSpecies();
			row2[aggrHeader.get("experiment")] = ris.getExperiment();
			row2[aggrHeader.get("plant")] = ris.getPlant();
			row2[aggrHeader.get("imaging_day")] = ris.getImagingDay();
			row2[aggrHeader.get("scale")] = Double.toString(scale);
			row2[aggrHeader.get("image_count")] = Integer.toString(n);

			String genotype_name = "";
			String dry_shoot = "";
			String dry_root = "";
			String wet_shoot = "";
			String wet_root = "";
			String sterilization = "";
			String rowcolumn = "";
			String img_interval_unit = "";
			String description = "";
			String img_start_date = "";

			if(r.getValue("genotype_name")!=null)
			{
				genotype_name = (String) r.getValue("genotype_name");
			}
			if(r.getValue("dry_shoot")!=null)
			{
				dry_shoot = Double.toString((Double) r.getValue("dry_shoot"));
			}
			if(r.getValue("dry_root")!=null)
			{
				dry_root = Double.toString((Double) r.getValue("dry_root"));
			}
			if(r.getValue("wet_shoot")!=null)
			{
				wet_shoot = Double.toString((Double) r.getValue("wet_shoot"));
			}
			if(r.getValue("wet_root")!=null)
			{
				wet_root = Double.toString((Double) r.getValue("wet_root"));
			}

			String strChamberRowColumn = (String) r.getValue("sterilization_chamber");

			if(strChamberRowColumn!=null)
			{
				sterilization = strChamberRowColumn.split("-")[0];
				rowcolumn = strChamberRowColumn.split("-")[1];
			}
			if(r.getValue("imaging_interval_unit")!=null)
			{
				img_interval_unit =  (String) r.getValue("imaging_interval_unit");
			}
			if(r.getValue("description")!=null)
			{
				description = (String) r.getValue("description");
			}
			if(r.getValue("imaging_start_date")!=null)
			{
				Date imagingStart = (Date) r.getValue("imaging_start_date");
				img_start_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(imagingStart);
			}

			row2[aggrHeader.get("genotype")] = genotype_name;
			row2[aggrHeader.get("dry_shoot")] = dry_shoot;
			row2[aggrHeader.get("dry_root")] = dry_root;
			row2[aggrHeader.get("wet_shoot")] = wet_shoot;
			row2[aggrHeader.get("wet_root")] = wet_root;
			row2[aggrHeader.get("sterilization_chamber")] = sterilization;
			row2[aggrHeader.get("rowcolumn")] = rowcolumn;
			row2[aggrHeader.get("imaging_interval_unit")] = img_interval_unit;
			row2[aggrHeader.get("description")] = description;
			row2[aggrHeader.get("imaging_start_date")] = img_start_date;

			for (int i = 0; i < fileHeader.size(); i++) {
				String h = fileHeader.get(i);
				if (scaleMap.containsKey(h)) {
					row2[aggrHeader.get(h)] = Double.toString(means[i]);
				}
			}

			aggrSheet.add(row2);
		} catch (IOException e) {
			throw new ExportException("Error loading file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * Parses image number from file path in the column source_id in the gia
	 * roots file. Current rule is the second field (assuming delimited) by
	 * underscores.
	 * 
	 * @param sourceId
	 * @return
	 */
	protected int parseImageNumber(String sourceId) {
		File f = new File(sourceId);
		String s = f.getName();
		String[] ss = s.split("_");

		return Integer.parseInt(ss[1]);
	}

	protected HashMap<String, Integer> getAggrHeader(Set<String> descriptors) {
		HashMap<String, Integer> ans = new HashMap<String, Integer>();

		int i = 0;
		ans.put("gia_dir", i++);
		ans.put("scale", i++);
		ans.put("species", i++);
		ans.put("experiment", i++);
		ans.put("plant", i++);
		ans.put("imaging_day", i++);
		ans.put("image_count", i++);

		ans.put("genotype",i++);
		ans.put("dry_shoot",i++);
		ans.put("dry_root",i++);
		ans.put("wet_shoot",i++);
		ans.put("wet_root",i++);
		ans.put("sterilization_chamber",i++);
		ans.put("rowcolumn",i++);
		ans.put("imaging_interval_unit",i++);
		ans.put("description",i++);
		ans.put("imaging_start_date",i++);

		for (String s : descriptors) {
			ans.put(s, i);
			i++;
		}

		return ans;
	}

	protected HashMap<String, Integer> getRawHeader(Set<String> descriptors) {
		HashMap<String, Integer> ans = new HashMap<String, Integer>();

		int i = 0;
		ans.put("gia_dir", i++);
		ans.put("source_id", i++);
		ans.put("scale", i++);
		ans.put("species", i++);
		ans.put("experiment", i++);
		ans.put("plant", i++);
		ans.put("imaging_day", i++);
		ans.put("image_number", i++);

		ans.put("genotype",i++);
		ans.put("dry_shoot",i++);
		ans.put("dry_root",i++);
		ans.put("wet_shoot",i++);
		ans.put("wet_root",i++);
		ans.put("sterilization_chamber",i++);
		ans.put("rowcolumn",i++);
		ans.put("imaging_interval_unit",i++);
		ans.put("description",i++);
		ans.put("imaging_start_date",i++);

		for (String s : descriptors) {
			ans.put(s, i);
			i++;
		}

		return ans;
	}

	protected class NoScale implements IScale {
		public double scale(double value, double scale) {
			return value;
		}

		@Override
		public String toString() {
			return "";
		}
	}

	protected class LinearScale implements IScale {
		public double scale(double value, double scale) {
			return value * scale;
		}

		@Override
		public String toString() {
			return "(mm)";
		}
	}

	protected class SquaredScale implements IScale {
		public double scale(double value, double scale) {
			return value * scale * scale;
		}

		@Override
		public String toString() {
			return "(mm^2)";
		}
	}

	protected class CubedScale implements IScale {
		public double scale(double value, double scale) {
			return value * scale * scale * scale;
		}

		@Override
		public String toString() {
			return "(mm^3)";
		}
	}

	// vp23
	protected class InverseSquaredScale implements IScale {
		public double scale(double value, double scale) {
			return value / (scale * scale);
		}

		@Override
		public String toString() {
			return "(1/mm^2)";
		}
	}

	protected static interface IScale {
		public double scale(double value, double scale);
	}

	protected class ExportException extends RuntimeException {
		public ExportException(String msg, Throwable th) {
			super(msg, th);
		}
	}

}
