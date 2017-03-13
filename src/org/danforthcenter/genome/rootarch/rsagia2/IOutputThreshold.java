/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;

/**
 * 
 * @author bm93
 */
public interface IOutputThreshold {
	public File[] getThresholdedImages();

    // tw 2015july15
    public File getCropPropFile();
}
