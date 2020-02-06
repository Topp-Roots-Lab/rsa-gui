/*
 *  Copyright 2013 vp23.
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

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JTextArea;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.Gia3D_v2;
import org.danforthcenter.genome.rootarch.rsagia2.Gia3D_v2Output;
import org.danforthcenter.genome.rootarch.rsagia2.ProcessUtil;

/**
 *
 * @author vp23
 */
public class Gia3D_v2Worker extends javax.swing.SwingWorker<Integer, String> {
	/*
	 * To change this template, choose Tools | Templates and open the template
	 * in the editor.
	 */
	protected Gia3D_v2 gia3D_v2;
	protected Gia3D_v2Output output;
	protected JTextArea log;
	protected ApplicationManager am;
	protected int id;
	protected boolean done;
	protected int returnValue;

	public int getReturnValue() {
		return returnValue;
	}

	public Gia3D_v2Worker(Gia3D_v2 gia3D_v2, Gia3D_v2Output output,
			JTextArea log, int id, ApplicationManager am) {
		this.gia3D_v2 = gia3D_v2;
		this.output = output;
		this.log = log;
		this.id = id;
		this.am = am;
		done = false;
	}

	public int getId() {
		return id;
	}

	// vp23 hack-SRL
	public Gia3D_v2Output getOutput() {
		return output;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// System.out.println("doInBackground() id: " + id + "; " +
		// input.getRis().toString());

		final Process p;
		try {
			p = gia3D_v2.start(output);
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
			@Override
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
			throw new Gia3D_v2WorkerException("Error running gia3D_v2 process",
					e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {

			}
		}

		gia3D_v2.postprocess(output, am);
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

	public static class Gia3D_v2WorkerException extends RuntimeException {
		public Gia3D_v2WorkerException(String msg, Throwable th) {
			super(msg, th);
		}
	}

	protected static class gia3D_v2Thread implements Runnable {
		@Override
		public void run() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
}