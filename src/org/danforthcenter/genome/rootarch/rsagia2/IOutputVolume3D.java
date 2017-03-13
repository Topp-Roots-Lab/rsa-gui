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
public interface IOutputVolume3D {
	public File getVolumeFile();

	public File getVolumeSTLFile();

	public File getVolumeDir();

	/**
	 * Returns the scaling (in voxels from pixels) of this voluem
	 * 
	 * @return
	 */
	public double getScale();
}
