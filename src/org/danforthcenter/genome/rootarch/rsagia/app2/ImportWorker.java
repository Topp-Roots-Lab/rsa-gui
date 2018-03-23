/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia2.*;

import javax.swing.*;
import java.io.*;
import java.util.List;

/**
 *
 * @author bm93
 */
public class ImportWorker extends SwingWorker<Integer, String> {
	/*
	 * To change this template, choose Tools | Templates and open the template
	 * in the editor.
	 */
	private Import importApp;
	private File importDirectory;
	private boolean deleteOriginals;

	private JTextArea log;
	private boolean done;
	private List<String[]> returnValue;

	public List<String[]> getReturnValue() {
		return returnValue;
	}

	public ImportWorker(Import importApp, File importDirectory, boolean deleteOriginals, JTextArea log) {
		this.importApp = importApp;
		this.importDirectory = importDirectory;
		this.deleteOriginals = deleteOriginals;

		this.log = log;
		this.done = false;
		this.returnValue = null;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		Process p = null;
		int exitValue;
		try {
			File organismsFile = File.createTempFile("allowed_organisms", ".txt");
			organismsFile.deleteOnExit();

			File movedImagesetsFile = File.createTempFile("moved_imagesets", ".txt");
			movedImagesetsFile.deleteOnExit();

			importApp.preprocess(organismsFile);

			p = importApp.start(this.importDirectory, this.deleteOriginals, organismsFile, movedImagesetsFile);

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				char[] cs = new char[1024];
				int len;
				while ((len = br.read(cs, 0, cs.length)) != -1) {
					String s = new String(cs, 0, len);
					publish(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new ImportException("Error running Import process", e);
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {

				}
			}

			p.waitFor();

			exitValue = p.exitValue();

			if (exitValue == 0) {
				returnValue = importApp.postprocess(movedImagesetsFile);
			}

			organismsFile.delete();
			movedImagesetsFile.delete();

		} catch (Exception e) {
			publish(e.getMessage());
			throw e;
		} finally {
			if (p != null) {
				ProcessUtil.dispose(p);
			}
		}
		return exitValue;
	}

	synchronized public void setDone(boolean done) {
		this.done = done;
	}

	synchronized public boolean getDone() {
		return done;
	}

	@Override
	protected void process(List<String> chunks) {
		if (log != null) {
			for (String s : chunks) {
				log.append(s);
			}
		}
	}

	public static class ImportException extends RuntimeException {
		public ImportException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
