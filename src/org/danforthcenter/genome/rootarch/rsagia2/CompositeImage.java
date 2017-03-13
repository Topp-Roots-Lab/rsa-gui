/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author bm93
 */
public class CompositeImage implements IImageReadListener,
		Thread.UncaughtExceptionHandler {
	public static final int MAX = 0;
	public static final int MIN = 1;

	protected ArrayList<BufferedImage> imgList;
	protected File[] imgFiles;
	protected int totalFiles;
	protected int curFile;

	protected int maxThreads;
	protected int threadCnt;

	int[] steps;
	int[] dim;

	protected Integer mySignal;

	public CompositeImage(int maxThreads) {
		imgList = new ArrayList<BufferedImage>();
		this.maxThreads = maxThreads;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();

		throw new CompositeImageException(e);
	}

	public BufferedImage read(File[] fs, int compType, BufferedImage sample,
			int maxWidth, int maxHeight) {
		imgFiles = fs;
		totalFiles = fs.length;
		curFile = 0;
		threadCnt = 0;

		mySignal = 0;

		steps = new int[2];
		dim = new int[2];

		calcSubSample(sample, maxWidth, maxHeight, steps, dim);
		for (int i = 0; i < maxThreads; i++) {
			startNextThread();
		}

		BufferedImage ans = getNext();
		for (int i = 1; i < totalFiles; i++) {
			BufferedImage bi = getNext();
			for (int y = 0; y < dim[1]; y++) {
				for (int x = 0; x < dim[0]; x++) {
					int rgb1 = ans.getRGB(x, y);
					int rgb2 = bi.getRGB(x, y);

					int r1 = (rgb1 & 0x00FF0000) >>> 4;
					int g1 = (rgb1 & 0x0000FF00) >>> 2;
					int b1 = (rgb1 & 0x000000FF);

					int r2 = (rgb2 & 0x00FF0000) >>> 4;
					int g2 = (rgb2 & 0x0000FF00) >>> 2;
					int b2 = (rgb2 & 0x000000FF);

					// /////////////////////////////////
					// // debug
					// /////////////////////////////////
					// if(r1!= 0 && r1 != 255 ||
					// g1!= 0 && g1 != 255 ||
					// b1!= 0 && b1 != 255){
					//
					// System.out.println("r1="+r1+
					// " "+
					// "g1="+g1+
					// " "+
					// "b1="+b1);
					// return null;
					// }
					// /////////////////////////////////

					float[] hsb1 = Color.RGBtoHSB(r1, g1, b1, null);
					float[] hsb2 = Color.RGBtoHSB(r2, g2, b2, null);

					if (compType == MAX) {
						if (hsb1[2] > hsb2[2]) {
							ans.setRGB(x, y, rgb1);
						} else {
							ans.setRGB(x, y, rgb2);
						}
					} else {
						if (hsb1[2] > hsb2[2]) {
							ans.setRGB(x, y, rgb2);
						} else {
							ans.setRGB(x, y, rgb1);
						}
					}
				}
			}

		}

		return ans;
	}

	synchronized protected BufferedImage getNext() {
		BufferedImage ans = null;
		if (imgList.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		ans = imgList.remove(0);
		return ans;
	}

	@Override
	synchronized public void imageIsRead(BufferedImage bi, Object source) {
		imgList.add(bi);
		startNextThread();
		notify();
	}

	protected void startNextThread() {
		if (curFile < totalFiles) {
			ThreadedImageReader tir = new ThreadedImageReader(
					imgFiles[curFile], this, steps);
			Thread th = new Thread(tir);
			th.start();
			curFile++;
		}
	}

	protected void calcSubSample(BufferedImage sample, int maxWidth,
			int maxHeight, int[] steps, int[] dim) {
		int w = sample.getWidth();
		int h = sample.getHeight();

		double sw = (double) w / (double) maxWidth;
		double sh = (double) h / (double) maxHeight;

		steps[0] = (int) Math.ceil(sw);
		steps[1] = (int) Math.ceil(sh);

		steps[0] = (steps[0] > steps[1]) ? steps[0] : steps[1];
		steps[1] = steps[0];

		dim[0] = w / steps[0];
		dim[1] = h / steps[1];
	}

	public static class CompositeImageException extends RuntimeException {
		public CompositeImageException(Throwable th) {
			super(th);
		}
	}
}
