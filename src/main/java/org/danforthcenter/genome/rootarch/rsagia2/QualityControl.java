/*
 *  Copyright 2011 vp23.
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.danforthcenter.genome.rootarch.rsagia.app2.SelectQc2OutputFrame;
import org.danforthcenter.genome.rootarch.rsagia.app2.SelectQc3OutputFrame;
import org.danforthcenter.genome.rootarch.rsagia.app2.SelectQcOutputFrame;

/**
 * 
 * @author vp23
 */
public class QualityControl implements IApplication {
	protected GiaRoot2D gia;
	protected String type;
	protected String qcPath;
	protected String qc3Path;

	private SelectQcOutputFrame selectQcFrame;
	private SelectQc2OutputFrame selectQc2Frame;
	private SelectQc3OutputFrame selectQc3Frame;
	private File[] input;
	private File output;
	private int scaleQc;
	private ArrayList<Integer> options;
	private ApplicationManager am;
	private RsaPipelineDirUtil dirUtil = new RsaPipelineDirUtil();

	// ============================<editor-fold desc="Constructor">{{{
	public QualityControl(String type, GiaRoot2D gia, String qcPath, String qc3Path) {
		this.gia = gia;
		this.type = type;
		this.qcPath = qcPath;
		this.qc3Path = qc3Path;
	}

	// End of Constructor...........................}}}</editor-fold>

	// ============================<editor-fold desc="hasRequiredInput">{{{
	/**
	 * 
	 * checks if there is at least one set with gia-roots 2d threshold files
	 * 
	 * (quality control files silently would NOT be generated for invalid sets)
	 * 
	 * @param ris
	 * @param am
	 */

	@Override
	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		boolean has2D = false;

		for (OutputInfo oi : OutputInfo.getInstances(am, ris, true, true, null,
				false)) {
			if (oi.isValid()) {
				if ((oi.getOutputs() & InputOutputTypes.DESCRIPTORS_2D) > 0) {
					has2D = true;
				}
				// skip this code - what this code does:
				// //vp - checks if at least one oi is not valid
				// else
				// {
				// has2D = false;
				// }
			}

			if (has2D) {
				break;
			}
		}

		return has2D;
	}

	// End of hasRequiredInput...........................}}}</editor-fold>

	@Override
	public int getRequiredInputs() {
		return InputOutputTypes.DESCRIPTORS_2D;
	}

	@Override
	public int getPossibleOutputs() {
		if (type.equalsIgnoreCase("qc2")) {
			return InputOutputTypes.QC2;
		} else if (type.equalsIgnoreCase("qc3")) {
			return InputOutputTypes.QC3;
		} else {
			// never happens
			return -1;
		}
	}

	public int getOutputs(File f) {
		if (type.equalsIgnoreCase("qc2")) {
			return InputOutputTypes.QC2;
		} else if (type.equalsIgnoreCase("qc3")) {
			return InputOutputTypes.QC3;
		} else {
			// never happens
			return -1;
		}
	}

	@Override
	public int getOutputs() {
		if (type.equalsIgnoreCase("qc2")) {
			return InputOutputTypes.QC2;
		} else if (type.equalsIgnoreCase("qc3")) {
			return InputOutputTypes.QC3;
		} else {
			// never happens
			return -1;
		}
	}

	@Override
	public int getOptionalInputs() {
		return InputOutputTypes.NONE;
	}

	@Override
	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		if (type.equalsIgnoreCase("qc2")) {
			return new Qc2Output(f, ris);
		} else if (type.equalsIgnoreCase("qc3")) {
			return new Qc3Output(f, ris);
		} else {
			// never happens
			return null;
		}
	}

	// ============================<editor-fold desc="getName">{{
	@Override
	public String getName() {
		return type;
	}

	// End of getName...........................}}}</editor-fold>

	// ============================<editor-fold desc="getName">{{
	// the qc2 is not suppossed to be shown on the Review screen,so,
	// probably no need to implement this method ...
	// when giaroot_2d are deleted, then the related qc2 would be deleted
	// silently (or with a prompt)
	@Override
	public String getReviewString(OutputInfo oi) {
		return oi.toString();
		// info can be added about the corresponding giaroot_2d item
		// return oi.toString() + "(" + this.getScale(oi) + ")";
	}

	// End of getName...........................}}}</editor-fold>

	// ============================<editor-fold desc="setGiaroot2dQcParams">{{{
	/**
	 * Copy gia-roots 2d threshold files
	 * 
	 * @param output
	 */
	public void setGiaroot2dQcParams(SelectQc2OutputFrame selectQc2Frame,
			File[] input, File output, int scaleQc, ArrayList<Integer> options,
			ApplicationManager am) {
		// this.selectQcFrame = selectQcFrame;
		this.selectQc2Frame = selectQc2Frame;
		// this.selectQc3Frame = selectQc3Frame;
		this.input = input;

		// For QC2, set output=null
		// For QC2, there is a need to write output qc file
		// per "001000000 - make a thresholded image composite"
		// to the giaroot_2d folders for every input dir, where
		// they can be found later for usage in SelectQc2OutputFrame.
		this.output = output;

		this.scaleQc = scaleQc;
		this.options = options;
		this.am = am;
	}

	// End of setGiaroot2dQcParams...........................}}}</editor-fold>

	// ============================<editor-fold desc="setRootwork3dQcParams">{{{
	/**
	 * Copy Rootwork3d voxel .out files
	 * 
	 * @param output
	 */
	public void setRootwork3dQcParams(SelectQc3OutputFrame selectQc3Frame,
			File[] input, File output, int scaleQc, ArrayList<Integer> options,
			ApplicationManager am) {
		// this.selectQcFrame = selectQcFrame;
		this.selectQc3Frame = selectQc3Frame;
		this.input = input;

		// For QC2, set output=null
		// For QC2, there is a need to write output qc file
		// per "001000000 - make a thresholded image composite"
		// to the giaroot_2d folders for every input dir, where
		// they can be found later for usage in SelectQc2OutputFrame.
		this.output = output;

		this.scaleQc = scaleQc;
		this.options = options;
		this.am = am;
	}

	// End of setRootwork3dQcParams...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc">{{{
	/**
     * 
     *
     * 
     */
	public void doQc(String src, int scaleQc, String dest, String code,
			String template_name) {

        System.out.println("QualityControl.java doQc " + src + " " + dest);
        File QCScript = new File(this.qcPath);
        if ( QCScript.exists() ) {

            // example
            // ./all_qc_folder.py
            // /data/rsa/processed_images/corn/NAM/p00039/d06/saved/giaroot_2d/prz_2011-05-10_16-56-21
            // 4 ./ 101011000 [template_name]

            String[] cmd = {this.qcPath, src, Integer.toString(scaleQc), dest, code,
                    template_name};

            ProcessBuilder pb = new ProcessBuilder(cmd);

            // just more convenient for debugging
            String cmd_str = "";
            for (int i = 0; i < cmd.length; i++) {
                cmd_str += cmd[i] + " ";
            }

            int ret = -1;
            Process p = null;
            try {
                System.out.println("QualityControl.java doQc running " + cmd_str);
                p = pb.start();
                ret = p.waitFor();
            } catch (IOException e) {
                throw new QualityControlException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (p != null) {
                ProcessUtil.dispose(p);
            }

            if (ret != 0) {
                throw new QualityControlException("Command: "
                        + Arrays.toString(cmd) + "; returned: " + ret);
            }
        }
        else {
            System.out.println(QCScript + " not found");
        }
	}

	// End of doQc...........................}}}</editor-fold>

	// ============================<editor-fold desc="doQc3">{{{
	/**
	 * TODO - change call signature based on the Python program
	 * 
	 * 
	 * 
	 */
	public void doQc3(String src, String dest) {
		// example
		// ./QC3D.py
		// /data/rsa/processed_images/rice/RIL/p00001/d12/sandbox/rootwork_3d/vp23_2013-04-29_13-06-50/OsRILp00001d12_vp23_2013-04-29_13-06-50_rootwork.out
		// ~/tmp/
		//
		String[] cmd = { this.qc3Path, src, dest };
		//
		// for debug comment the rest
		//
		ProcessBuilder pb = new ProcessBuilder(cmd);

		// just more convenient for debugging
		String cmd_str = "";
		for (int i = 0; i < cmd.length; i++) {
			cmd_str += cmd[i] + " ";
		}
		// System.out.println(cmd_str);

		int ret = -1;
		Process p = null;
		try {
			p = pb.start();
			ret = p.waitFor();
		} catch (IOException e) {
			throw new QualityControlException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		if (p != null) {
			ProcessUtil.dispose(p);
		}

		if (ret != 0) {
			throw new QualityControlException("Command: "
					+ Arrays.toString(cmd) + "; returned: " + ret);
		}
	}

	// End of doQc3...........................}}}</editor-fold>

	// ============================<editor-fold desc="getCode">{{{
	private String getCode(ArrayList<Integer> options) {

		// # 000000000
		// # 100000000 - make a cropped image composite
		// # 010000000 - make a gray image composite
		// # 001000000 - make a thresholded image composite
		// # 000100000 - make a skeleton image composite
		// # 000010000 - make a thresholded on cropped overlay image
		// # 000001000 - make a thresholded on gray overlay image
		// # 000000100 - make a skeleton on cropped overlay image
		// # 000000010 - make a skeleton on gray overlay image
		// # 000000001 - make a skeleton on thresholded overlay image
		// # 111111111 - do everything
		// # 001100000 - make a thresholded and a skeleton

		String code = "";

		for (int i = 0; i < options.size(); i++) {
			code = code + options.get(i).toString();
		}

		return code;
	}

	// End of getCode...........................}}}</editor-fold>

	// ============================<editor-fold desc="getNumOfOptions">{{{
	private int getNumOfOptions(String code) {
		int Count = 0;
		for (int i = 0; i < code.length(); i++) {
			if (String.valueOf(code.charAt(i)).equals("1")) {
				Count++;
			}
		}
		return Count;
	}

	// End of getNumOfOptions...........................}}}</editor-fold>

	// ============================<editor-fold desc="getTemplateName">{{{
	private String getTemplateName(String path) {
		String template_name = "";
		String suffix = GiaRoot.CONFIG_XML_SUFFIX;
		File[] files = new File(path).listFiles();
		for (File file : files) {
			String fn = file.getName();
			if (fn.endsWith(suffix)) {
				int ind = fn.indexOf(suffix);
				template_name = fn.substring(0, ind);
			}
		}

        System.out.println(this.getClass() + " getTemplateName " + template_name);
		return template_name;
	}

	// End of getTemplateName...........................}}}</editor-fold>

	// ============================<editor-fold
	// desc="ProgressBarGiaroot2dQc">{{{
	public class ProgressBarGiaroot2dQc extends JPanel implements
			PropertyChangeListener {

		private JProgressBar progressBar;
		private Task task;
		private JDialog dialog;

		// ============================<editor-fold desc="Task">{{{
		class Task extends SwingWorker<Void, Void> {
			/*
			 * Main task. Executed in background thread.
			 * 
			 * Cancel (iinterruption) at user's will is not implemented. ( For
			 * the case of QC, clicking on the x button looks OK: it would
			 * result aboritng the QC processing at the system will, in reality
			 * it happens quickly; the files that has been already generated
			 * will be left untoucned.
			 */
			@Override
			public Void doInBackground() {

                System.out.println(this.getClass() + " doInBackground ");

				String ErrMsg = "";
				Random random = new Random();
				int progress = 0;
				double process_d = 0.0;

				// Initialize progress property.
				setProgress(0);

				String code = getCode(options);
				int NumOfOptions = getNumOfOptions(code);
				// get the number of files to be genrated by the QC
				int NumQcFiles = input.length * NumOfOptions;
				double delta = (double) (1) / (double) NumQcFiles;

				// In our case, progress bar
				// is updated properly (in fact, almost properly)
				// only per image set
				// As far as, tracking the progress of the processing
				// images inside the image set is purely cosmetic
				// with random time interval [0,1000] ms

				while (progress < 100) {

                    System.out.println(this.getClass() + " progress <100");

					for (int i = 0; i < input.length; i++) {
						String src = input[i].getAbsolutePath();
						// this is to to check whether the giaroot_2d output
						// is already in place. It only will the existence of
						// the giaroot_2d.csv file, which is most likely makes
						// sure
						// that giaroot_2d output is valid
						File src_giaroot_2d_csv = new File(src + File.separator
								+ "giaroot_2d.csv");
						// this is to to check whether the rootwork_dd output is
						// Ok
						// or not - check whether the voxel file in in place

						File src_rootwork_3d_out = getVoxelFileName(new File(
								src));
						// copy all files to one directory: dest
						//
						// For QC2 there is a need to write output qc file
						// for the code
						// "001000000 - make a thresholded image composite"
						// to the giaroot_2d folders for every input dir,
						// which is src here,
						// where they can be found later
						// for usage in SelectQc2OutputFrame
						String dest = null;
						if (output != null) {
                            System.out.println("output != null");
							dest = output.getAbsolutePath();
						} else {
                            System.out.println("output == null");
							dest = createDestForQc2Qc3TmpFiles(src);
						}
						// call Python for both Qc and Qc2
						// (first, check if there are giaroot2d tiff files)
						if (selectQc3Frame == null) {

                            System.out.println(this.getClass() + " selecvtQC3Frame == null" + src_giaroot_2d_csv);

                            if (src_giaroot_2d_csv.exists()) {
								String tn = getTemplateName(src);
                                System.out.println(this.getClass() + " src_giaroot_2d_csv exists " + tn );
								doQc(src, scaleQc, dest, code, tn);
							} else {
								// there are no giaroot2d files
								ErrMsg = ErrMsg
										+ "There are no giaroot2d files in the folder: "
										+ src + "\n";
							}
						}

						// call Python for Qc3
						// (first, check if there is the voxel .out file)
						if (selectQc3Frame != null) {
							if (src_rootwork_3d_out.exists()) {
								dest = dest
										+ File.separator
										+ SelectQc3OutputFrame.ANGLESTOP_MOSAIC_FILE;

								// debug only
								// JOptionPane.showMessageDialog(null,
								// src_rootwork_3d_out.getAbsolutePath()+" --- "+
								// dest);

								doQc3(src_rootwork_3d_out.getAbsolutePath(),
										dest);
							} else {
								// there are no giaroot2d files
								ErrMsg = ErrMsg
										+ "There are no rootwork3d files in the folder: "
										+ src + "\n";
							}
						}
						// cosmetic things
						for (int j = 0; j < NumOfOptions; j++) {
							// //////////////////////////////////////////////
							//
							// Problem : any Qc option is frozen (hung on) and
							// cannot continue.
							// This happens only on bio-busch,there is no such a
							// problem on bio-busch server.
							//
							// / A history note: it might be a JVM (+plus Linux
							// congiguration, etc)
							// / related issue.
							//
							// bio-busch info:
							// java version "1.6.0_22"
							// OpenJDK Runtime Environment (IcedTea6 1.10.8)
							// (rhel-1.27.1.10.8.el5_8-x86_64)
							// OpenJDK 64-Bit Server VM (build 20.0-b11, mixed
							// mode)
							//
							// / bio-ross info:
							// java version "1.6.0_24"
							// OpenJDK Runtime Environment (IcedTea6 1.11.9)
							// (rhel-1.57.1.11.9.el6_4-x86_64)
							// OpenJDK 64-Bit Server VM (build 20.0-b12, mixed
							// mode)
							//
							// //////////////////////////////////////////////
							// //////////////////////////////////////////////
							// // NOTE: in fact, this delay, probably, not
							// needed at all
							// / So, the following pieces of code, implementing
							// / delay behaviour, can be be commented.
							// //////////////////////////////////////////////
							// ///////////////////////////////////////////////////////////
							// / for some reason it does not work on bio-ross,
							// / though works on bio-busch.
							// / Conflict with EDT does not seem to be a reason,
							// / because this method doInBackground() runs in
							// / a separate thread (?)
							// ///////////////////////////////////////////////////////////
							// Sleep for up to one second.
							// try {
							// Thread.sleep(random.nextInt(1000));
							// } catch (InterruptedException ignore) {
							// }
							// /////////////////////////////////////////////////////////

							// ///////////////////////////////////////////////////////////
							// / it works (on both bio-ross and bio-busch)
							// / Suprisingly enough,
							// / because wait() looks similar to Thread.sleep()
							// on low level
							// ///////////////////////////////////////////////////////////
							// final Object LOCK = new Object();
							// final long SLEEP = 1000;
							//
							// try {
							// synchronized (LOCK)
							// {
							// LOCK.wait(SLEEP);
							// }
							// } catch (InterruptedException e) {
							// // usually interrupted by other threads e.g.
							// during program shutdown
							// break;
							// }
							// ////////////////////////////////////////////////////////////
							process_d += delta;
							progress = (int) (process_d * (double) 100);
							// update progress.
							setProgress(Math.min(progress, 100));
						}
					}
					progress = 100;
					// update final progress.
					setProgress(Math.min(progress, 100));

					if (!ErrMsg.isEmpty()) {
						ErrMsg = "Quality Control ignored the following giaroot2d input(s):\n"
								+ ErrMsg;
						JOptionPane.showMessageDialog(null, ErrMsg);
					}
				}
                System.out.println("progress 100");
				return null;
			}

			/*
			 * Executed in event dispatching thread
			 */
			@Override
			public void done() {

                System.out.println(this.getClass() + " done ");

				Toolkit.getDefaultToolkit().beep();
				setCursor(null);

				if (selectQc2Frame != null) {
					selectQc2Frame.doStartQc2();
					selectQc2Frame.setVisible(true);
				}
				if (selectQc3Frame != null) {
					selectQc3Frame.doStartQc3();
					selectQc3Frame.setVisible(true);
				}
			}
		}

		// End of Task...........................}}}</editor-fold>

		// ============================<editor-fold desc="Constructor">{{{
		public ProgressBarGiaroot2dQc(JDialog dialog) {
			super(new BorderLayout());

			this.dialog = dialog;

			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);

			JPanel panel = new JPanel(new FlowLayout());
			panel.add(progressBar);

			add(panel, BorderLayout.PAGE_START);
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			// Instances of javax.swing.SwingWorker are not reusuable, so
			// we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}

		// End of Constructor...........................}}}</editor-fold>

		// ============================<editor-fold desc="propertyChange">{{{
		/**
		 * Implementation of PropertyChangeListener interface
		 * 
		 * Invoked when task's progress or state properties change.
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress".equals(evt.getPropertyName())) {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			} else if ("state".equals(evt.getPropertyName()) && task.isDone()) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		}

		// End of propertyChange...........................}}}</editor-fold>

		// ==========================<editor-fold
		// desc="createDestForQc2Qc3TmpFiles">{{{
		/**
		 * This function creates the tmp folder where to save the generated
		 * images (mosaics) for Qc2 and Qc3
		 * 
		 * The tmp folder would be deleted later *
		 * 
		 * 
		 * @param src
		 */
		private String createDestForQc2Qc3TmpFiles(String src) {

            System.out.println(this.getClass() + " createDestForQc2Qc3TmpFiles " + src);

			String dest = src + File.separator + "tmp";
			// initialize tmp folder
			File tmp = new File(dest);
			// create the tmp directory
			if (!tmp.mkdir()) {
				// throw new
				// SelectQc2OutputFrameException("Could not create directory: "
				// + tmp.getAbsolutePath(), null);
			}

			// // tw 2014nov13
			// am.getIsm().setDirectoryPermissions(tmp);
			am.getIsm().setPermissions(tmp, false);

			return dest;
		}

		// End of
		// createDestForQc2Qc3TmpFiles........................}}}</editor-fold>

		// ==========================<editor-fold desc="getVoxelFileName">{{{
		/**
		 * 
		 * 
		 * @param dir
		 */
		private File getVoxelFileName(File dir) {

			String species = dirUtil.getSpecies(dir);
			String speciecode = OutputInfo.getSpecieCode(species);
			String exp = dirUtil.getExp(dir);
			String plant = dirUtil.getPlant(dir);
			String day = dirUtil.getDay(dir);
			String userstamp = dir.getName();

			String name = speciecode + exp + plant + day + "_" + userstamp
					+ "_rootwork.out";

			return new File(dir.getAbsolutePath() + File.separator + name);
		}
		// End of getVoxelFileName........................}}}</editor-fold>

	}

	// End of ProgressBarGiaroot2dQc...........................}}}</editor-fold>

	// ============================<editor-fold
	// desc="QualityControlException">{{{
	static class QualityControlException extends RuntimeException {

		public QualityControlException(String msg) {
			super(msg);
		}

		public QualityControlException(Throwable th) {
			super(th);
		}

		public QualityControlException(String msg, Throwable th) {
			super(msg, th);
		}

	}

	// End of
	// QualityControlException...........................}}}</editor-fold>

	// ============================<editor-fold desc="archive">{{{

	// ============================<editor-fold
	// desc="obsolete_getGiaroot2dQc">{{{
	/**
	 * This function was called from outside (was public) in the past. Now it is
	 * moved to doInBackground() method in the implementation of the SwingWorker
	 * class. Left here for reference only.
	 * 
	 * Copy gia-roots 2d threshold files
	 * 
	 * @param output
	 */
	private void obsolete_getGiaroot2dQc(File[] input, File output,
			int scaleQc, ArrayList<Integer> options) {
		String ErrMsg = "";

		File[] outputs = new File[input.length];
		String code = getCode(options);

		for (int i = 0; i < input.length; i++) {
			String src = input[i].getAbsolutePath();
			File src_giaroot_2d_csv = new File(src + File.separator
					+ "giaroot_2d.csv");
			// copy all files to one directory: dest
			String dest = output.getAbsolutePath();

			// //
			// // copy files into directories per input name
			// //
			// String dest = output.getAbsolutePath()+ File.separator +
			// input[i].getName();
			// File dest_dir = new File(dest);
			//
			// if (!dest_dir.exists() && ! dest_dir.mkdir())
			// {
			// throw new QualityControlException("Could not create directory: "
			// + dest, null);
			// }

			// call Python
			// (first, check if there are giaroot2d tiff files)
			if (src_giaroot_2d_csv.exists()) {
				String tn = getTemplateName(src);
				doQc(src, scaleQc, dest, code, tn);
			} else {
				// there are no giaroot2d files
				ErrMsg = ErrMsg
						+ "There are no giaroot2d files in the folder: " + src
						+ "\n";
			}
		}

		if (!ErrMsg.isEmpty()) {
			ErrMsg = "Quality Control ignored the following giaroot2d input(s):\n"
					+ ErrMsg;
			JOptionPane.showMessageDialog(null, ErrMsg);
		}

	}

	// End of old_getGiaroot2dQc...........................}}}</editor-fold>

	// ============================<editor-fold desc="ProgressBarDemo">{{{
	public class ProgressBarDemo extends JPanel implements ActionListener,
			PropertyChangeListener {

		private JProgressBar progressBar;
		private JButton startButton;
		private JTextArea taskOutput;
		private Task task;

		class Task extends SwingWorker<Void, Void> {
			/*
			 * Main task. Executed in background thread.
			 */
			@Override
			public Void doInBackground() {
				Random random = new Random();
				int progress = 0;
				// Initialize progress property.
				setProgress(0);
				while (progress < 100) {
					// Sleep for up to one second.
					try {
						Thread.sleep(random.nextInt(1000));
					} catch (InterruptedException ignore) {
					}
					// Make random progress.
					progress += random.nextInt(10);
					setProgress(Math.min(progress, 100));
				}
				return null;
			}

			/*
			 * Executed in event dispatching thread
			 */
			@Override
			public void done() {
				Toolkit.getDefaultToolkit().beep();
				startButton.setEnabled(true);
				setCursor(null); // turn off the wait cursor
				taskOutput.append("Done!\n");
			}
		}

		public ProgressBarDemo() {
			super(new BorderLayout());

			// Create the demo's UI.
			startButton = new JButton("Start");
			startButton.setActionCommand("start");
			startButton.addActionListener(this);

			progressBar = new JProgressBar(0, 100);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);

			taskOutput = new JTextArea(5, 20);
			taskOutput.setMargin(new Insets(5, 5, 5, 5));
			taskOutput.setEditable(false);

			JPanel panel = new JPanel();
			panel.add(startButton);
			panel.add(progressBar);

			add(panel, BorderLayout.PAGE_START);
			add(new JScrollPane(taskOutput), BorderLayout.CENTER);
			setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		}

		/**
		 * Invoked when the user presses the start button.
		 */
		public void actionPerformed(ActionEvent evt) {
			startButton.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// Instances of javax.swing.SwingWorker are not reusuable, so
			// we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}

		/**
		 * Invoked when task's progress property changes.
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress" == evt.getPropertyName()) {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
				taskOutput.append(String.format("Completed %d%% of task.\n",
						task.getProgress()));
			}
		}

		/**
		 * Create the GUI and show it. As with all GUI code, this must run on
		 * the event-dispatching thread.
		 */
		// private static void createAndShowGUI() {
		// // Create and set up the window.
		// JFrame frame = new JFrame("ProgressBarDemo");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		// // Create and set up the content pane.
		// JComponent newContentPane = new ProgressBarDemo();
		// newContentPane.setOpaque(true); // content panes must be opaque
		// frame.setContentPane(newContentPane);
		//
		// // Display the window.
		// frame.pack();
		// frame.setVisible(true);
		// }

		// public static void main(String[] args) {
		// // Schedule a job for the event-dispatching thread:
		// // creating and showing this application's GUI.
		// javax.swing.SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		// createAndShowGUI();
		// }
		// });
		// }
	}
	// End of ProgressBarDemo...........................}}}</editor-fold>

	// End of archive...........................}}}</editor-fold>

}
