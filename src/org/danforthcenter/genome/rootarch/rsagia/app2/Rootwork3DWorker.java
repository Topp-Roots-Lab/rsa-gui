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
import org.danforthcenter.genome.rootarch.rsagia2.IOutputThreshold;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.ProcessUtil;
import org.danforthcenter.genome.rootarch.rsagia2.Rootwork3D;

/**
 *
 * @author bm93
 */
public class Rootwork3DWorker extends javax.swing.SwingWorker<Integer, String> {
	/*
	 * To change this template, choose Tools | Templates and open the template
	 * in the editor.
	 */
	protected Rootwork3D rootwork3D;
	protected OutputInfo out;
	protected IOutputThreshold threshold;
	protected int reconLowerThresh;
	protected int nodesOctree;
	protected int imagesUsed;
	protected int reconOption;
	protected JTextArea log;
	protected ApplicationManager am;
	protected int id;
	protected boolean done;
	protected int returnValue;
	protected int reconUpperThreshold;
	protected int distortionRadius;
	protected int numberOfComponents;
	protected int resolution;
	protected int refImage;
	protected double refRatio;

	public int getReturnValue() {
		return returnValue;
	}

	public Rootwork3DWorker(Rootwork3D rootwork3D, OutputInfo out,
			IOutputThreshold threshold, int reconLowerThresh, int nodesOctree,
			int imagesUsed, int reconOption, JTextArea log,
			ApplicationManager am, int id, int reconUpperThreshold,
			int distortionRadius, int numberOfComponents, int resolution,
			int refImage, double refRatio) {
		this.rootwork3D = rootwork3D;
		this.out = out;
		this.threshold = threshold;
		this.reconLowerThresh = reconLowerThresh;
		this.nodesOctree = nodesOctree;
		this.imagesUsed = imagesUsed;
		this.reconOption = reconOption;
		this.log = log;
		this.am = am;
		this.id = id;
		this.done = false;
		this.returnValue = -1;
		this.reconUpperThreshold = reconUpperThreshold;
		this.distortionRadius = distortionRadius;
		this.numberOfComponents = numberOfComponents;
		this.resolution = resolution;
		this.refImage = refImage;
		this.refRatio = refRatio;
	}

	public int getId() {
		return id;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		Process p = null;
		try {
			// System.out.println("ABOUT TO START)");
			p = rootwork3D.start(out, threshold, reconOption, reconLowerThresh,
					nodesOctree, imagesUsed, reconUpperThreshold,
					distortionRadius, numberOfComponents, resolution, refImage,
					refRatio);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));

				char[] cs = new char[1024];
				int len;
				while ((len = br.read(cs, 0, cs.length)) != -1) {
					String s = new String(cs, 0, len);
					publish(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new Rootwork3DException(
						"Error running Rootwork3D process", e);
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {

				}
			}

			rootwork3D.postprocess(out);
			returnValue = p.exitValue();
		} catch (Exception e) {
			publish(e.getMessage());
			throw e;
		} finally {
			if (p != null) {
				ProcessUtil.dispose(p);
			}
		}
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
			}
		}
	}

	public static class Rootwork3DException extends RuntimeException {
		public Rootwork3DException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
