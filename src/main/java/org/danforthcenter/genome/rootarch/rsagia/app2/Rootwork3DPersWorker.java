/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia2.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 * @author bm93
 */
public class Rootwork3DPersWorker extends javax.swing.SwingWorker<Integer, String> {
	/*
	 * To change this template, choose Tools | Templates and open the template
	 * in the editor.
	 */
	protected Rootwork3DPers rootwork3DPers;
	protected OutputInfo out;
	protected IOutputThreshold threshold;
	protected OutputInfo scale;
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

	// tw 2015july15
	protected int camDist;
	protected int rotDir;
	protected boolean doFindRotAxis;
	protected boolean doCalib;
	protected Double pitch;
	protected Double roll;
	protected int translation;
	protected int focusOffset;
	protected boolean doAdd;


	public int getReturnValue() {
		return returnValue;
	}

	public Rootwork3DPersWorker(Rootwork3DPers rootwork3DPers, OutputInfo out,
								IOutputThreshold threshold, OutputInfo scale,
								int reconLowerThresh, int nodesOctree,
								int imagesUsed, int reconOption, JTextArea log,
								ApplicationManager am, int id, int reconUpperThreshold,
								int distortionRadius, int numberOfComponents, int resolution,
								int refImage, double refRatio,
								int camDist, int rotDir, boolean doFindRotAxis, boolean doCalib, Double pitch,
								Double roll, int translation, int focusOffset,
								boolean doAdd) {

		this.camDist = camDist;
		this.rotDir = rotDir;
		this.doFindRotAxis = doFindRotAxis;
		this.doCalib = doCalib;
		this.pitch = pitch;
		this.roll = roll;
		this.translation = translation;
		this.focusOffset = focusOffset;
		this.doAdd = doAdd;


		this.rootwork3DPers = rootwork3DPers;
		this.out = out;
		this.threshold = threshold;
		this.scale = scale;
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

	public OutputInfo getOutput() {
		return out;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		Process p = null;
		try {
			// System.out.println("ABOUT TO START)");
			p = rootwork3DPers.start(out, threshold, scale, reconOption, reconLowerThresh,
					nodesOctree, imagesUsed, reconUpperThreshold,
					distortionRadius, numberOfComponents, resolution, refImage,
					refRatio,
					this.camDist, this.rotDir, this.doFindRotAxis, this.doCalib, this.pitch,
					this.roll, this.translation, this.focusOffset, this.doAdd
			);
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

			rootwork3DPers.postprocess(out);
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
