/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import org.danforthcenter.genome.rootarch.rsagia2.*;

/**
 *
 * @author bm93, vp23
 */
public class App {

	private static String[] spccode;
	private static String[] spcname;

	public static void main(String[] args) {
		try {
			UserAccess.reducePrivileges();

			if (args.length < 1) {
				System.out.println("Usage: rsa-gia PROPERTIES_FILE");
				System.exit(1);
			}

			System.getProperties().setProperty("org.jooq.no-logo", "true");

			///////////////
			//String user = System.getProperty("user.name");
			//String os = System.getProperty("os.name");

			//UserManagement um = new UserManagement();
			//ArrayList<String> userGroups = null;
			//userGroups = um.findUserGroups(user,os);

			///////////////

			final Properties sysProps = new Properties();
			FileInputStream fis1 = null;
			try {
				fis1 = new FileInputStream(new File(args[0]));
				sysProps.load(fis1);
			} catch (IOException e) {
				throw new IOException(e);
			} finally {
				if (fis1 != null) {
					try {
						fis1.close();
					} catch (IOException e) {

					}
				}
			}

			final Properties userProps = new Properties();
			final File uf = new File(System.getProperty("user.home")
					+ File.separator + ".rsa-gia" + File.separator
					+ "user.properties");
			if (!uf.exists()) {
				makeUserPropertiesFile(uf);
			}

			FileInputStream fis2 = null;
			try {
				fis2 = new FileInputStream(uf);
				userProps.load(fis2);
			} catch (IOException e) {
				throw new IOException(e);
			} finally {
				if (fis2 != null) {
					try {
						fis2.close();
					} catch (IOException e) {

					}
				}
			}


            String s3 = sysProps.getProperty("dir_group");
            String s4 = sysProps.getProperty("dir_permissions");
            String s5 = sysProps.getProperty("file_group");
            String s6 = sysProps.getProperty("file_permissions");
            String s8 = sysProps.getProperty("gia3d_descriptors");

            // // tw 2014nov13
            // // set permissions for posix and acl systems in FileVisitorUtil
            // final SimpleSecurityManager ssm = new SimpleSecurityManager(s4,
            // s3, s6, s5);
            final SimpleSecurityManager ssm = new SimpleSecurityManager(s3, s5);



			final File f1 = new File(sysProps.getProperty("base_dir"));
			Scale scale = new Scale();
			Crop crop = new Crop();

			// For now there is no special recrop object,
			// the Crop class is used for recropping.
			// Create Crop object
			Crop recrop = new Crop();
			// Indicate that it will be used for the recropping
			recrop.setRecrop(true);



			// the settings should be comma "," delimited, no spaces
			// The names and the codes should go in the
			// corresponding order like:
			// species_names=corn,model,rice,wheat,insilico,millet,sorghum
			// species_codes=Zm,Fk,Os,Ta,Is,Gm,Sb
			// where corn <--> Zm, model <--> Fk, rice <--> Os, ....
			//
			String sccodes = sysProps.getProperty("species_codes");
			String snames = sysProps.getProperty("species_names");
			spccode = sccodes.split(",");
			spcname = snames.split(",");

			File f2 = new File(sysProps.getProperty("gia_template_dir"));
			String s1 = sysProps.getProperty("gia_exec_path");
			File f3 = new File(sysProps.getProperty("gia_dir"));
			String s2 = sysProps.getProperty("gia_descriptors");


			String exportConfig = sysProps.getProperty("export_config");
			String export3dConfig = sysProps.getProperty("export_3d_config");
			String exportGia3d_v2Config = sysProps
					.getProperty("export_gia3d_v2_config");
			GiaRoot2D gia = new GiaRoot2D(f2, s1, f3, s2, ssm);
			Export export = new Export(null, scale, gia, exportConfig,
					export3dConfig, exportGia3d_v2Config);


			String s13 = userProps.getProperty("user_cols");
			final ArrayList<String> cols = new ArrayList<String>();
			if (s13 != null) {
				String[] cs = s13.split(",");
				for (String s : cs) {
					cols.add(s);
				}
			}

			File f5 = new File(sysProps.getProperty("gia3d_template_dir"));


			// these setting have been from the very begining - all useres have
			// them
			// in their user properties files.
			final ArrayList<StringPairFilter> spf1 = StringPairFilter
					.getInstances(userProps.getProperty("species_filter"));
			final ArrayList<StringPairFilter> spf2 = StringPairFilter
					.getInstances(userProps.getProperty("experiment_filter"));
			final ArrayList<StringPairFilter> spf3 = StringPairFilter
					.getInstances(userProps.getProperty("plant_filter"));
			final ArrayList<StringPairFilter> spf4 = StringPairFilter
					.getInstances(userProps.getProperty("imaging_day_filter"));
			// this was introuduced later, so not all users have it - initialize
			// properly and update all related to this setting code below
			String ipdf = userProps.getProperty("imaging_plant_day_filter");
			if (ipdf == null)
				ipdf = "";
			final ArrayList<StringPairFilter> spf5 = StringPairFilter
					.getInstances(ipdf);

			String s7 = sysProps.getProperty("rootwork3d_script_path");
			String s71 = sysProps.getProperty("reconstruction3d_exe");
			String s711 = sysProps.getProperty("reconstruction3d_stl_exe");
			String s72 = sysProps.getProperty("use_matlab");
			Rootwork3D rootwork3D = new Rootwork3D(s7, s71, s711, s72, ssm);

            GiaRoot3D giaRoot3D = new GiaRoot3D(s1, f3, s8, f5, ssm);


            // tw 2015jun29
            String s81 = sysProps.getProperty("reconstruction3dpers_exe");
            Rootwork3DPers rootwork3Dpers = new Rootwork3DPers(s81, ssm);


			String s11 = sysProps.getProperty("gia3d_v2_script_path");
			String s12 = sysProps.getProperty("gia3d_v2_descriptors");
			String s14 = sysProps.getProperty("gia3d_v2_descriptors_view");
			String s15 = sysProps.getProperty("gia3d_v2_matlab_script_path");
			File f7 = new File(sysProps.getProperty("gia3d_v2_template_dir"));
			Gia3D_v2 gia3D_v2 = new Gia3D_v2(s11, s12, s14, s15, f7, s72);

			String importScriptPath = sysProps.getProperty("import_script_path");
			Import importApp = new Import(importScriptPath, f1, ssm);

			final ApplicationManager am = new ApplicationManager(ssm, scale,
					crop, recrop, gia, export, rootwork3D, rootwork3Dpers, giaRoot3D,
					gia3D_v2, importApp);
			giaRoot3D.setAm(am);
			gia3D_v2.setAm(am);


			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					Thread.currentThread().setUncaughtExceptionHandler(
							new java.lang.Thread.UncaughtExceptionHandler() {
								public void uncaughtException(Thread t,
										Throwable e) {
									final Throwable th = e;
									java.awt.EventQueue
											.invokeLater(new Runnable() {
												public void run() {
													new ErrorFrame(th)
															.setVisible(true);
												}
											});
								}
							});
				}
			});


//            final File f11 = new File("/data/rsa/");


			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					new MainFrame(f1, ssm, am, spf1, spf2, spf3, spf4, spf5,
							uf, cols).setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			// return 3;
		}
		// return 0;
	}

	protected static void makeUserPropertiesFile(File uf) throws IOException {
		// tw 2014july9 added outer loop for presence of directory but missing
		// file
		if (!uf.getParentFile().isDirectory()) {
			if (!uf.getParentFile().mkdir()) {
				throw new IOException("Could not make directory: "
						+ uf.getParent());
			}
		}

		BufferedWriter bw = null;
		try {
			String ls = System.getProperty("line.separator");
			bw = new BufferedWriter(new FileWriter(uf));
			bw.write("version: 1" + ls);
			bw.write("species_filter:" + ls);
			bw.write("experiment_filter:" + ls);
			bw.write("plant_filter:" + ls);
			bw.write("imaging_day_filter:" + ls);
			bw.write("imaging_plant_day_filter:" + ls);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static String[] getSpecieCode() {

		return spccode;
	}

	public static String[] getSpecieName() {

		return spcname;
	}
}
