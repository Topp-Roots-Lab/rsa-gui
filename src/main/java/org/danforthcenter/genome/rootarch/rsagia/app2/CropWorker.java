/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Rectangle;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.Crop;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 * Cropping can take a few seconds. This places the cropping on a separate
 * thread queue. SwingWorkers prevent heavy processes from slowing down the UI.
 * 
 * @author bm93
 */

// tw 2015jun15 add rectSum
public class CropWorker extends javax.swing.SwingWorker<Integer, Integer> {
	protected RsaImageSet ris;

	// for recropping input
	protected OutputInfo recropi;

	protected OutputInfo oi;
	protected Crop crop;
	protected ApplicationManager am;
	protected int rot;
	protected Rectangle rect;
    protected Rectangle rectSum;

	public CropWorker(RsaImageSet ris, OutputInfo recropi, OutputInfo oi,
//			Crop crop, ApplicationManager am, int rot, Rectangle rect) {
            Crop crop, ApplicationManager am, int rot, Rectangle rect) {
		this.ris = ris;
		this.oi = oi;
		this.recropi = recropi;
		this.crop = crop;
		this.am = am;
		this.rot = rot;
		this.rect = rect;
	}

	public OutputInfo getOi() {
		return oi;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		crop.cropImages(ris, recropi, oi, am, rot, rect);
		return 0;
	}

}
