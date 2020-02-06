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
public interface IOutputDescriptors3D {
	public File getCsvFile();

	/**
	 * Scale (in voxels).
	 * 
	 * @return
	 */
	public double getScale();
}
