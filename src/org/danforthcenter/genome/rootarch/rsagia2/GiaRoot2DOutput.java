/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author bm93
 */
public class GiaRoot2DOutput extends OutputInfo implements IOutputCrop,
		IOutputDescriptors2D, IOutputThreshold {
	public static final String THRESHOLD_SUBSTRING = "_thresholdedimage_";
	public static final String CROP_SUBSTRING = "_croppedimage_";
	public static final String DESCRIPTOR_FILENAME = "giaroot_2d.csv";

	protected File descriptorFile;

	public GiaRoot2DOutput(File f, RsaImageSet ris) {
		super(f, ris);

		descriptorFile = new File(dir.getAbsolutePath() + File.separator
				+ DESCRIPTOR_FILENAME);
		outputs = getCropFlag() | getThresholdFlag() | getDescriptorFlag();
	}

	public File[] getCroppedImages() {
		NameSubstringFileFilter nsf = new NameSubstringFileFilter(
				CROP_SUBSTRING);
		return dir.listFiles(nsf);
	}

	public File[] getThresholdedImages() {
		NameSubstringFileFilter nsf = new NameSubstringFileFilter(
				THRESHOLD_SUBSTRING);
		return dir.listFiles(nsf);
	}

	public File getDescriptorFile() {
		return descriptorFile;
	}

	protected int getCropFlag() {
		return 0;
	}


    // tw 2015july15
    public File getCropPropFile() {
        File cropPropFile = new File(dir + File.separator
            + "crop.properties");
        return cropPropFile;
    }


	protected int getThresholdFlag() {
		File[] f = getThresholdedImages();
		return (f != null && f.length > 0) ? InputOutputTypes.THRESHOLD : 0;
	}

	protected int getDescriptorFlag() {
		return (!descriptorFile.exists()) ? 0 : InputOutputTypes.DESCRIPTORS_2D;
	}

	@Override
	public boolean isValid() {
        boolean ans = true;

		if ((getThresholdFlag() | getDescriptorFlag()) != (InputOutputTypes.THRESHOLD | InputOutputTypes.DESCRIPTORS_2D)) {
			ans = false;
		} else {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(descriptorFile));
				String str = br.readLine();
				if (str == null || str.trim().length() == 0) {
					ans = false;
				}
			} catch (IOException e) {
				ans = false;
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {

					}
				}
			}
		}

//        System.out.println( this.getClass() + " ans " + ans);

		return ans;
	}
}
