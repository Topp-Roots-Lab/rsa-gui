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
public class CropOutput extends OutputInfo implements IOutputCrop {
	public CropOutput(File f, RsaImageSet ris) {
		super(f, ris);
		outputs = InputOutputTypes.CROP;
	}

	public File[] getCroppedImages() {
		return getCroppedImageDir().listFiles();
	}

	protected File getCroppedImageDir() {
		return new File(dir.getAbsolutePath() + File.separator + "images");
	}

	@Override
	public boolean isValid() {
		boolean ans = true;
		File[] cis = getCroppedImages();
		ExtensionFileFilter eff = new ExtensionFileFilter(
				ris.getPreferredType());
		if (cis == null) {
			ans = false;
		} else if (cis.length == 0) // should do a stronger check here, but it's
									// hard
		{
			ans = false;
		} else if (!new File(dir.getAbsolutePath() + File.separator
				+ "crop.properties").exists()) {
			ans = false;
		}

		return ans;
	}
}
