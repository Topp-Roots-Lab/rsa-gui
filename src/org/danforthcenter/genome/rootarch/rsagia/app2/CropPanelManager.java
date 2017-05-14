/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.Crop;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 * Cropping is unique in that it is done in two steps. This class is used to
 * manage the order and execution of the two different CropPanels on the
 * ImageManipulationFrame.
 *
 * @author bm93
 */
public class CropPanelManager implements java.beans.PropertyChangeListener {
    protected PropertyChangeSupport pcs;

    protected ArrayList<RsaImageSet> inputs;
    protected ArrayList<OutputInfo> recropinputs;
    protected ArrayList<OutputInfo> outputs;
    protected ArrayList<Integer> rots;
    protected ArrayList<Rectangle> rects;

    protected int curIndex;
    protected Crop crop;
    protected ApplicationManager am;
    protected ImageManipulationFrame imf;
    protected CropTopPanel ctp;
    protected CropRestPanel crp;
    protected CropAllPanel cap;

    protected CropWorker cw;
    protected int endCropIndex;
    protected int curCropIndex;
    protected CropWaitFrame cwf;

    // number of files (imgs) that are used for cropping
    private int num_crop_imgs;

    public CropPanelManager(ArrayList<RsaImageSet> inputs,
                            ArrayList<OutputInfo> recropinputs, ArrayList<OutputInfo> outputs,
                            Crop crop, ApplicationManager am) {
        this.inputs = inputs;
        this.recropinputs = recropinputs;
        this.outputs = outputs;
        this.crop = crop;
        this.am = am;
        this.cwf = null;

        curIndex = 0;
        endCropIndex = -1;
        curCropIndex = 0;
        rects = new ArrayList<Rectangle>();
        rots = new ArrayList<Integer>();

        pcs = new PropertyChangeSupport(this);

        if (crop.getRecrop()) {
            // number of sets for recropping
            // (calculated based on the 'outputs')
            num_crop_imgs = recropinputs.size();
        } else {
            // number of sets for cropping
            // (calculated based on the 'inputs')
            num_crop_imgs = inputs.size();
        }
    }

    public void run() {
        imf = new ImageManipulationFrame();
        imf.setVisible(true);

        doNext();
    }

    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getSource() == cap) // crop rest panel
        {
            if (evt.getPropertyName().equals("done")
                    && (Boolean) evt.getNewValue() == true) {
                rects.add(curIndex, cap.getRectangle());
                rots.add(curIndex,cap.getRot());
                cap = null;
                endCropIndex++;
                cropNext();

                curIndex++;
                if (curIndex < num_crop_imgs) {
                    doNext();
                } else {
                    imf.dispose();
                    if (curCropIndex < num_crop_imgs) {
                        cwf = new CropWaitFrame();
                        cwf.setLabel(curCropIndex + 1, num_crop_imgs);
                        cwf.setVisible(true);
                    }
                }
            }
        } else if (evt.getSource() == cw) // crop worker
        {
            if (evt.getPropertyName().equals("state")) {
                if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    // rename files for recropping only
                    if (crop.getRecrop()) {
                        OutputInfo oi = cw.getOi();
                        crop.postprocessrecrop(oi, am);
                    }

                    cw = null;

                    curCropIndex++;
                    if (curCropIndex < num_crop_imgs) {
                        cropNext();
                    } else {
                        if (cwf != null) {
                            cwf.dispose();
                            cwf = null;
                        }
                        pcs.firePropertyChange("done", false, true);
                    }
                }
            }
        }
    }

    /**
     * Creates a single SwingWorker to crop the curCropIndex image
     */
    protected void cropNext() {

        if (cw == null && curCropIndex <= endCropIndex) {
            if (cwf != null) {
                cwf.setLabel(curCropIndex + 1, num_crop_imgs);
            }

            if (crop.getRecrop()) {
                cw = new CropWorker(null, recropinputs.get(curCropIndex),
                        outputs.get(curCropIndex), crop, am,
                        rots.get(curCropIndex), rects.get(curCropIndex));
            } else {
                cw = new CropWorker(inputs.get(curCropIndex), null,
                        outputs.get(curCropIndex), crop, am,
                        rots.get(curCropIndex), rects.get(curCropIndex));
            }

            cw.addPropertyChangeListener(this);
            cw.execute();
        }
    }

    protected void doNext() {
        // RsaImageSet ris = inputs.get(curIndex);
		Rectangle rect = (rects.size() > 0) ? rects.get(rects.size() - 1)
				: null;
        int rot = (rots.size() > 0) ? rots.get(rects.size() - 1)
                : -1;
        if (crop.getRecrop()) {
            cap = new CropAllPanel(imf, crop, null, recropinputs.get(curIndex),
                    outputs.get(curIndex), rect, rot);
        } else {
            RsaImageSet ris = inputs.get(curIndex);
            cap = new CropAllPanel(imf, crop, ris, null, outputs.get(curIndex),
                    rect, rot);
        }
        if (curIndex == 0) {
            imf.pack();
        }
        cap.addPropertyChangeListener("done", this);
    }
}
