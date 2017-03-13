/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JTextArea;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2D;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot2DInput;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.ProcessUtil;

/**
 *
 * @author bm93
 */
public class GiaRoot2DWorker extends javax.swing.SwingWorker<Integer, String> {
	/*
	 * To change this template, choose Tools | Templates and open the template
	 * in the editor.
	 */
	protected GiaRoot2D gia;
	protected GiaRoot2DInput input;
	protected OutputInfo output;
	protected JTextArea log;
	protected ApplicationManager am;
	protected int id;
	protected boolean done;
	protected int returnValue;

	public int getReturnValue() {
		return returnValue;
	}

	public GiaRoot2DWorker(GiaRoot2D gia, GiaRoot2DInput input,
			OutputInfo output, JTextArea log, int id, ApplicationManager am) {
		this.gia = gia;
		this.input = input;
        this.output = output;
        System.out.println(this.getClass().getSimpleName() + " " + input.getCrop().getDir());
        System.out.println(this.getClass().getSimpleName() + " " + output.getDir());
		this.log = log;
		this.id = id;
		this.am = am;
		done = false;
	}

	public int getId() {
		return id;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// System.out.println("doInBackground() id: " + id + "; " +
		// input.getRis().toString());
		gia.preprocess(input, output, am);
		final Process p;
		try {
			p = gia.start(input, output);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable th) {

			th.printStackTrace();
			throw new Exception("OMG");
		}
		// System.out.println("Starting id: " + id + "; " +
		// input.getRis().toString());
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					returnValue = -55;
				}
			}
		});
		th.start();

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
			returnValue = -55;
			e.printStackTrace();
			throw new GiaRoot2DWorkerException("Error running gia process", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {

			}
		}

		gia.postprocess(output, am);
		returnValue = p.exitValue();
		ProcessUtil.dispose(p);
		return returnValue;
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
				// System.out.print(s);
			}
		}
	}

	public static class GiaRoot2DWorkerException extends RuntimeException {
		public GiaRoot2DWorkerException(String msg, Throwable th) {
			super(msg, th);
		}
	}

	protected static class GiaThread implements Runnable {

		public void run() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
}
