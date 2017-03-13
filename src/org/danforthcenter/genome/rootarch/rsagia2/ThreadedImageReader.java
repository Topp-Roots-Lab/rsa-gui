/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

/**
 * 
 * @author bm93
 */
public class ThreadedImageReader implements Runnable {
	protected IImageReadListener callback;
	protected File imgFile;
	protected int[] subsample;

	public ThreadedImageReader(File f, IImageReadListener callback,
			int[] subsample) {
		this.imgFile = f;
		this.callback = callback;
		this.subsample = subsample;
	}

	public void run() {
		javax.imageio.stream.FileImageInputStream fiis = null;
		BufferedImage bi = null;
		ImageReader ir = null;
		try {
			fiis = new FileImageInputStream(imgFile);
			ir = ImageIO.getImageReaders(fiis).next();
			ImageReadParam irp = ir.getDefaultReadParam();
			irp.setSourceSubsampling(subsample[0], subsample[1], 0, 0);
			ir.setInput(fiis);
			bi = ir.read(0, irp);
		} catch (FileNotFoundException e) {
			throw new ListThreadedImageReaderException(e);
		} catch (IOException e) {
			throw new ListThreadedImageReaderException(e);
		} finally {
			if (ir != null) {
				ir.dispose();
			}
			if (fiis != null) {
				try {
					fiis.close();
				} catch (IOException e) {

				}
			}
		}

		callback.imageIsRead(bi, this);
	}

	public static class ListThreadedImageReaderException extends
			RuntimeException {
		public ListThreadedImageReaderException(Throwable th) {
			super(th);
		}
	}
}
