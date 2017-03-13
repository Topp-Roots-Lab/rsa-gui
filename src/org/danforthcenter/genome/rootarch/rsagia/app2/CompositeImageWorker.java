/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.CompositeImage;
import org.danforthcenter.genome.rootarch.rsagia2.Crop;
import org.danforthcenter.genome.rootarch.rsagia2.ExtensionFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author bm93
 */
public class CompositeImageWorker extends
		javax.swing.SwingWorker<Integer, Integer> {

	protected ArrayList<RsaImageSet> gps;
	protected ArrayList<OutputInfo> recropinputs;
	protected Dimension thumbnailSize;
	protected ArrayList<OutputInfo> outputs;
	protected int cnt;
	protected Crop crop;
	protected ApplicationManager am;

	public CompositeImageWorker(ArrayList<RsaImageSet> gps,
			ArrayList<OutputInfo> recropinputs, Dimension thumbnailSize,
			Crop crop, ApplicationManager am) {
		this.gps = gps;
		this.recropinputs = recropinputs;
		this.thumbnailSize = thumbnailSize;
		this.crop = crop;
		this.am = am;
		cnt = 0;
		outputs = new ArrayList<OutputInfo>();
	}

	public ArrayList<OutputInfo> getOutputs() {
		return outputs;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		setProgress(cnt);
		if (recropinputs == null) {
			for (RsaImageSet gp : gps) {
				CompositeImage ci = new CompositeImage(3);
				BufferedImage bi = null;
				try {
					OutputInfo oi = new OutputInfo(crop.getName(), gp, false);
                    File oiDir = oi.getDir();
                    System.out.println(this.getClass() + oiDir.toString());
					OutputInfo.createDirectory(oi, am);
					ExtensionFileFilter eff = new ExtensionFileFilter(
							gp.getPreferredType());
   					bi = ImageIO
							.read(gp.getPreferredInputDir().listFiles(eff)[0]);
					BufferedImage bi2 = ci.read(gp.getPreferredInputDir()
							.listFiles(eff), CompositeImage.MIN, bi,
							thumbnailSize.width, thumbnailSize.height);
                    System.out.println(this.getClass() + " " + gp.getPreferredInputDir());
					crop.writeThumbnail(oi, bi2, am);
					outputs.add(oi);
					// ImageIO.write(bi2, gp.getPreferredType(),
					// gp.getRis().getMinThumbnail());
				} catch (Exception e) {
					e.printStackTrace();
					throw new CompositeImageWorkerException(e);
				}

				cnt++;
				setProgress(cnt);
			}
		} else {
			for (OutputInfo rcri : recropinputs) {
				CompositeImage ci = new CompositeImage(3);
				BufferedImage bi = null;
				try {
					// create output info for saving in the sandbox
					// In this case, call crop.getName() would retrun "recrop",
					// but "crop" is needed here, because all recropping
					// results are now kept in the 'crop' folder
					//
					String cropname = "crop";
					OutputInfo oi = new OutputInfo(cropname, rcri.getRis(),
							false);
					OutputInfo.createDirectory(oi, am);
					String PreferredType = "tiff";
					ExtensionFileFilter eff = new ExtensionFileFilter(
							PreferredType);
					// the crop images are in the "images" folder
					File cropimgdir = new File(rcri.getDir() + File.separator
							+ "images");
					File[] files = cropimgdir.listFiles(eff);
					if (files == null) {
						// skip, there is no "images" folder
						// with tiff files (crop) files.
						// It means that it is not a valid crop output.
						// which is used for the recropping
						//
						// TODO: check that the crop input that is used
						// for recropping is valid.
						// For now, no special object is used for the
						// recropping,
						// so, formally, any check, probably, would be a hack
						continue;
					}

					bi = ImageIO.read(cropimgdir.listFiles(eff)[0]);
					BufferedImage bi2 = ci.read(cropimgdir.listFiles(eff),
							CompositeImage.MIN, bi, thumbnailSize.width,
							thumbnailSize.height);

					// //////////////////////////////////////////////
					// test for the CompositeImage.MAX
					//
					// BufferedImage bi2 = ci.read(cropimgdir.listFiles(eff),
					// CompositeImage.MAX, bi, thumbnailSize.width,
					// thumbnailSize.height);
					// //////////////////////////////////////////////

                    System.out.println(this.getClass() + " recropinputs not null " + bi2);
					crop.writeThumbnail(oi, bi2, am);
					outputs.add(oi);
					// ImageIO.write(bi2, gp.getPreferredType(),
					// gp.getRis().getMinThumbnail());
				} catch (Exception e) {
					e.printStackTrace();
					throw new CompositeImageWorkerException(e);
				}

				cnt++;
				setProgress(cnt);
			}
		}

		return 0;
	}

	public static class CompositeImageWorkerException extends RuntimeException {
		public CompositeImageWorkerException(Throwable th) {
			super(th);
		}
	}
}
