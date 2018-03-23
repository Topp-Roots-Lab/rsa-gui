/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.danforthcenter.genome.rootarch.rsagia.app2.App;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

/**
 *
 * @author bm93
 */
public class OutputInfo implements IProgramRun{
	protected Date date;
	protected String user;
	protected int outputs;
	protected File dir;
	protected String appName;
	protected RsaImageSet ris;
	protected int runID;
	protected Integer savedConfigID;
	protected String unsavedConfigContents;
	protected String inputRuns;
	protected String descriptors;
	protected String results;

	protected static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

	protected static String[] spcname = App.getSpecieName();
	protected static String[] spccode = App.getSpecieCode();

	public OutputInfo(OutputInfo oi) {
		date = oi.date;
		user = oi.user;
		outputs = oi.outputs;
		dir = oi.dir;
		appName = oi.appName;
		ris = oi.ris;
	}

	public String getPrefix() {
		String s1 = null;
		for (int i = 0; i < spcname.length; i++) {
			if (ris.getSpecies().equals(spcname[i])) {
				s1 = spccode[i];
				break;
			}
		}

		String s2 = s1 + ris.getExperiment() + ris.getPlant()
				+ ris.getImagingDay();
		String s3 = s2 + "_" + dir.getName();

		return s3;
	}

	public static String getSpecieCode(String speceiname) {
		String code = null;
		for (int i = 0; i < spcname.length; i++) {
			if (speceiname.equals(spcname[i])) {
				code = spccode[i];
				break;
			}
		}

		return code;
	}
	public static ArrayList<OutputInfo> getInstances(ApplicationManager am, RsaImageSet ris,
													 boolean doSaved, boolean doSandbox, ArrayList<String> filters,boolean red) {
		ArrayList<OutputInfo> ans = new ArrayList<OutputInfo>();

		OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
		Result<Record> datasetRecord = null;

		datasetRecord = oidbf.getOutputsFromProgramRunTable(ris, filters, doSaved, doSandbox, red);

		if(datasetRecord.size()!=0)
		{
			for (Record r : datasetRecord) {
				Date date = (Date) r.getValue("run_date");
				String date_ = new SimpleDateFormat(DATE_FORMAT).format(date);
				//int size = date.length();
				//String date1 = date.substring(0,size-2);
				String[] d = date_.split("_");
				File filePath = null;
				byte x = (byte) r.getValue("saved");
				if(x == 0)
				{
					filePath = new File(ris.getProcessedDir()+File.separator+"sandbox"+File.separator
							+r.getValue("name")+File.separator
							+r.getValue("user_name") + "_" + d[0] + "_" + d[1]);
				}
				else
				{
					filePath = new File(ris.getProcessedDir()+File.separator+"saved"+File.separator
							+r.getValue("name")+File.separator
							+r.getValue("user_name") + "_" + d[0] + "_" + d[1]);
				}
				int runID = (int) r.getValue("run_id");
				Integer savedConfigID = null;
				if(r.getValue("saved_config_id")!=null) {
					savedConfigID = (int) r.getValue("saved_config_id");
				}
				String unsavedConfigContents = null;
				if(r.getValue("unsaved_config_contents")!=null) {
					unsavedConfigContents = (String) r.getValue("unsaved_config_contents");
				}
				String descs = null;
				if(r.getValue("descriptors")!=null) {
					descs = (String) r.getValue("descriptors");
				}
				String inputRuns = null;
				if(r.getValue("input_runs")!=null) {
					inputRuns = String.valueOf(r.getValue("input_runs"));
				}
                String results = null;
                if(r.getValue("results")!=null) {
                    results = String.valueOf(r.getValue("results"));
                }
				OutputInfo oi = getInstance(filePath, am, ris);
				oi.setRunID(runID);
				oi.setSavedConfigID(savedConfigID);
				oi.setUnsavedConfigContents(unsavedConfigContents);
				oi.setInputRuns(inputRuns);
				oi.setDescriptors(descs);
				oi.setResults(results);

				if (red && !oi.isValid()) {
					ans.add(oi);
				} else if (red == false) {
					ans.add(oi);
				}
			}
		}
		return ans;
	}
	public static ArrayList<OutputInfo> getInstances_old(ApplicationManager am,
													 RsaImageSet ris, boolean doSaved, boolean doSandbox,
													 ArrayList<String> filters, boolean red) {
		DirectoryFileFilter dff = new DirectoryFileFilter();
		ReviewDirFilter rff = new ReviewDirFilter(filters);
		ArrayList<File> af = new ArrayList<File>();
		if (doSaved) {
			af.add(new File(ris.getProcessedDir() + File.separator + "saved"));
		}
		if (doSandbox) {
			af.add(new File(ris.getProcessedDir() + File.separator + "sandbox"));
		}

		ArrayList<OutputInfo> ans = new ArrayList<OutputInfo>();
		for (File f3 : af) {
			if (f3.exists()) {
				File[] fs1 = null;
				if (filters == null) {
					fs1 = f3.listFiles(dff);
				} else {
					fs1 = f3.listFiles(rff);
				}
				if (fs1 != null) // surprised this could be null
				{
					for (File f : fs1) {
						if (f != null) {
							File[] fs2 = f.listFiles(dff);
							if (fs2 != null) {
								for (File f2 : fs2) {
									OutputInfo oi = getInstance(f2, am, ris);
									boolean isvalid = oi.isValid();
									if (red && !oi.isValid()) {
										ans.add(oi);
									} else if (red == false) {
										ans.add(oi);
									} else {
										//
									}
								}
							}
						}
					}
				}
			}
		}

		return ans;
	}

	public boolean isValid() {
		System.out.println(this.getClass() + " isValid true");
		return false;
	}

	/**
	 * Given a working directory path and an image set, will construct the
	 * corresponding OutputInfo.
	 *
	 * @param dir
	 * @param am
	 * @param ris
	 * @return
	 */
	public static OutputInfo getInstance(File dir, ApplicationManager am,
										 RsaImageSet ris) {
		String s = dir.getAbsoluteFile().getName();
		String[] ss = s.split("_");

		if (ss.length != 3) {
			throw new OutputInfoException("Badly named working directory: "
					+ dir.getAbsolutePath());
		}

		String user = ss[0];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Date date = null;

		try {
			date = sdf.parse(ss[1] + "_" + ss[2]);
		} catch (java.text.ParseException e) {
			throw new OutputInfoException(e);
		}

		String appName = dir.getParentFile().getName();
		OutputInfo ans = null;

		IApplication app = am.getApplicationByName(appName);

		if (app != null) {
			ans = app.getOutputInfo(dir, ris);
			// ans = new OutputInfo(date, user, outputs, dir, appName, ris);
		}

		return ans;
	}

	public boolean isSaved() {
		return dir.getParentFile().getParentFile().getName().equals("saved");
	}

	// tw 2015jan8
//    public static OutputInfo moveToSaved(OutputInfo oi, ApplicationManager am){
	public static OutputInfo moveToSaved(OutputInfo oi, ApplicationManager am) throws IOException {
		File newDir = new File(oi.getDir().getAbsolutePath()
				.replace("sandbox", "saved"));

		System.out.println("moveToSaved " + oi.getDir().getAbsolutePath());
		System.out.println(newDir.getAbsolutePath());


		OutputInfo oi2 = new OutputInfo(oi.getDate(), oi.getUser(),
				oi.getOutputs(), newDir, oi.getAppName(), oi.getRis());
		OutputInfo.createDirectory(oi2, am);

		// tw 2015jan8
		for ( File sandFile : oi.getDir().listFiles() ) {
			File savedFile = new File(newDir + File.separator + sandFile.getName());
			if (!savedFile.exists() ) {
//                Files.copy(sandFile.toPath(), savedFile.toPath());
				Files.move(sandFile.toPath(), savedFile.toPath());

			}

			if ( sandFile.isDirectory() ) {
				for ( File sandSubFile : sandFile.listFiles() ) {
					File savedSubFile = new File(newDir
							+ File.separator + sandFile.getName()
							+ File.separator + sandSubFile.getName()
					);
					if (!savedSubFile.exists() ) {
//                        Files.copy(sandSubFile.toPath(), savedSubFile.toPath());
						Files.move(sandSubFile.toPath(), savedSubFile.toPath());
					}
				}

			}

		}

		// tw 2015may6 error in setting permissions on viper, possibly due to NFS file system with network users and local group
		//am.getIsm().setPermissions(newDir, true);
		// end

		delete(oi, am);
		oi.getDir().renameTo(newDir);
		return oi2;
	}

	public static void delete(OutputInfo oi, ApplicationManager am) {
		FileUtil.deleteRecursively(oi.getDir());
	}

	public static void createDirectory(OutputInfo oi, ApplicationManager am) {
		File app = oi.getDir().getParentFile();
		File ss = app.getParentFile();

		OutputInfo.createDirectoryWithPermissions(ss, am.getIsm());
		OutputInfo.createDirectoryWithPermissions(app, am.getIsm());
		OutputInfo.createDirectoryWithPermissions(oi.getDir(), am.getIsm());
	}

	protected static void createDirectoryWithPermissions(File f, ISecurityManager ism) {
		if (!f.exists()) {
			if (!f.mkdir()) {
				throw new OutputInfoException("Could not create directory: "
						+ f.getAbsolutePath());
			}

			// // tw 2014nov12
			// ism.setDirectoryPermissions(f);
			ism.setPermissions(f, false);
		}
	}

	public OutputInfo(File dir, RsaImageSet ris) {
		this.dir = dir;
		this.ris = ris;
		String s = dir.getAbsoluteFile().getName();
		String[] ss = s.split("_");

		if (ss.length != 3) {
			throw new OutputInfoException("Badly named working directory: "
					+ dir.getAbsolutePath());
		}

		user = ss[0];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		date = null;
		try {
			date = sdf.parse(ss[1] + "_" + ss[2]);
		} catch (java.text.ParseException e) {
			throw new OutputInfoException(e);
		}

		appName = dir.getParentFile().getName();
	}

	public OutputInfo(Date date, String user, int outputs, File dir,
					  String appName, RsaImageSet ris) {
		this.date = date;
		this.user = user;
		this.outputs = outputs;
		this.dir = dir;
		this.appName = appName;
		this.ris = ris;
	}

	/**
	 * Given the appName, ris, and whether to be saved... will create an
	 * outputinfo. Without creating the directory!
	 *
	 * @param appName
	 * @param ris
	 * @param toSaved
	 */
	public OutputInfo(String appName, RsaImageSet ris, boolean toSaved) {
		date = new Date();
		user = UserAccess.getCurrentUser();
		outputs = InputOutputTypes.NONE;
		String wn = user + "_"
				+ (new SimpleDateFormat(DATE_FORMAT)).format(date);
		dir = new File(ris.getProcessedDir() + File.separator
				+ ((toSaved) ? "saved" : "sandbox") + File.separator + appName
				+ File.separator + wn);
		this.appName = appName;
		this.ris = ris;
	}

/*	protected String getUserFromSystem() {
		String ans = System.getProperty("user.name");
		if (ans == null) {
			throw new OutputInfoException("Could not get user name from system");
		}
		return ans;
	}*/

	public RsaImageSet getRis() {
		return ris;
	}

	public String getAppName() {
		return appName;
	}

	public Date getDate() {
		return date;
	}

	public File getDir() {
		return dir;
	}

	public int getOutputs() {
		return outputs;
	}

	public String getUser() {
		return user;
	}

	public int getRunID() {
		return runID;
	}

	public void setRunID(int runID) {
		this.runID = runID;
	}

	public Integer getSavedConfigID() {
		return savedConfigID;
	}
	public void setSavedConfigID(Integer savedConfigID) {
		this.savedConfigID = savedConfigID;
	}
	public String getUnsavedConfigContents() {
		return unsavedConfigContents;
	}
	public void setUnsavedConfigContents(String unsavedConfigContents) {
		this.unsavedConfigContents = unsavedConfigContents;
	}
	public String getInputRuns() {
		return inputRuns;
	}
	public void setInputRuns(String inputRuns) {
		this.inputRuns = inputRuns;
	}

	public String getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(String descriptors) {
		this.descriptors = descriptors;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	protected static class OutputInfoException extends RuntimeException {
		public OutputInfoException(Throwable th) {
			super(th);
		}

		public OutputInfoException(String msg) {
			super(msg);
		}
	}

	@Override
	public String toString() {
		return this.getAppName() + "." + this.dir.getName();
	}

	// ============================<editor-fold desc="ReviewDirFilter">{{{
	public static class ReviewDirFilter implements FileFilter {
		private final ArrayList<String> dirs;

		public ReviewDirFilter(ArrayList<String> dirs) {
			this.dirs = dirs;
		}

		@Override
		public boolean accept(File file) {
			boolean exists_dir = file.exists() && file.isDirectory();

			// if (dirs==null || dirs.isEmpty())return true;

			for (String dir : dirs) {
				String fn = file.getName();
				if (exists_dir && file.getName().equals(dir)) {
					return true;
				}
			}
			return false;
		}
	}
	// End of ImageFileFilter...........................}}}</editor-fold>

}
