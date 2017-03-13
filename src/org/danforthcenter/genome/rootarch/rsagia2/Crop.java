/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;
//import javax.imageio.ImageWriteParam;
//import com.sun.media.imageio.plugins.*;
//import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
//import com.sun.media.jai.codec.TIFFEncodeParam;
//import com.sun.media.jai.codecimpl.TIFFImage;
//import com.sun.media.jai.codecimpl.TIFFImageEncoder;
//import com.sun.media.jai.codecimpl.TIFFCodec;
//import com.sun.media.jai.codec.TIFFField;
//import com.sun.media.jai.codec.TIFFDirectory;
//import com.sun.media.jai.opimage.TIFFRIF;
//import com.sun.media.imageioimpl.plugins.tiff.*;


/**
 * 
 * @author bm93
 */
public class Crop implements IApplication {
	protected static final String THUMBNAIL_FILE = "min_thumbnail.tiff";
	protected static final String THUMBNAIL_TYPE = "tiff";
	protected static final String CROP_TYPE = "tiff";
	protected static final String CROP_SUFFIX = "_crop.tiff";
	protected static final String CROP_FILE = "crop.properties";
	protected static final String CROP_IMAGE_SUBDIR = "images";
	protected static final String ROTATION_PROP = "rotation";
	protected static final String CROP_PROP = "crop";
	protected static final String INPUT_TYPE_PROP = "input_type";

    // tw 2015jun15
    protected static final String CROP_SUM_PROP = "crop_sum";
    protected static final String ORIG_RECT_PROP = "original_image";

	// indicate that the Crop class would be used for the recropping
	private boolean recrop = false;

	public boolean getRecrop() {
		return recrop;
	}

	public void setRecrop(boolean value) {
		recrop = value;
	}

	@Override
	public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
		return true;
	}

	@Override
	public int getRequiredInputs() {
		if (!recrop) {
			return InputOutputTypes.RAW;
		} else {
			return InputOutputTypes.CROP;
		}
	}

	@Override
	public int getPossibleOutputs() {
		return InputOutputTypes.CROP;
	}

	public int getOutputs(File f) {
		return InputOutputTypes.CROP;
	}

	@Override
	public int getOptionalInputs() {
		return InputOutputTypes.NONE;
	}

	@Override
	public String getName() {
		if (!recrop) {
			return "crop";
		} else {
			return "recrop";
		}
	}

	@Override
	public int getOutputs() {
		return InputOutputTypes.CROP;
	}

	public File getThumbnail(OutputInfo oi) {
		return new File(oi.getDir().getAbsolutePath() + File.separator
				+ THUMBNAIL_FILE);
	}

	/**
	 * Crops all the images, writes a text file describing the transformation.
	 * 
	 * @param ris
	 * @param oi
	 * @param am
	 * @param rot
	 * @param rect
	 */
	public void cropImages(RsaImageSet ris, OutputInfo recropi, OutputInfo oi,
			ApplicationManager am, int rot, Rectangle rect) {
            // tw 2015jun15
//           ApplicationManager am, int rot, Rectangle rect) {
		// create the subdirectory for the images
		File f = getCropImageSubdir(oi);
		if (!f.mkdir()) {
			throw new CropException("Could not create directory: "
					+ f.getAbsolutePath(), null);
		}

		// // tw 2014nov13
		// am.getIsm().setDirectoryPermissions(f);
		am.getIsm().setPermissions(f, false);

		// write the text file containg transform parameters

		String preferredType = "";
		if (recropi == null) {
			preferredType = ris.getPreferredType();
		} else {
			preferredType = CROP_TYPE;
		}

        Rectangle rectSum = getRectSum(recropi);
        Rectangle origRect = getOrigRect(ris, recropi, rot, preferredType);

         // tw 2015jun15
//		 writeCropFile(oi, rot, rect, am, preferredType);
         writeCropFile(oi, rot, rect, rectSum, origRect, am, preferredType);

		// crop and rotate all the images
		ExtensionFileFilter eff = null;
		File[] srcs = null;
		if (recrop) {
			// based on the oi
			eff = new ExtensionFileFilter(preferredType);
			// the crop images are in the "images" folder
			File cropimgdir = new File(recropi.getDir() + File.separator
					+ "images");
			srcs = cropimgdir.listFiles(eff);
		} else {
			// based on the ris
			eff = new ExtensionFileFilter(preferredType);
			srcs = ris.getPreferredInputDir().listFiles(eff);
		}
		for (File src : srcs) {
			File dst = getCropImage(src, f, preferredType);
			cropAndRotate(src, dst, rot, rect);

			// // tw 2014nov13
			// am.getIsm().setFilePermissions(dst);
			am.getIsm().setPermissions(dst, false);
		}

//        am.getIsm().setPermissions(oi.getDir(), true);

	}


    // tw 2015june24
    public Rectangle getRectSum(OutputInfo recropOI){
        Rectangle rectangle = new Rectangle(0, 0, 0, 0);
        System.out.println("getRectSum " + rectangle.toString());
        File cropPropertiesFile;
        Properties cropProps = new Properties();
        FileInputStream fis1 = null;

        if ( recropOI == null){
            return rectangle;
        }
        else{
            cropPropertiesFile = new File(recropOI.getDir() + File.separator
                    + "crop.properties");
            try {
                fis1 = new FileInputStream(cropPropertiesFile);
                cropProps.load(fis1);
                if ( cropProps.getProperty(CROP_SUM_PROP) != null ){
                    String[] params = cropProps.getProperty(CROP_SUM_PROP).split(",");
                    rectangle.x = Integer.parseInt(params[0]);
                    rectangle.y = Integer.parseInt(params[1]);
                    rectangle.width = Integer.parseInt(params[2]);
                    rectangle.height = Integer.parseInt(params[3]);
                }
                else{
                    System.out.println("CROP SUM NOT FOUND. IS IT AN OLD CROP? NEW CROP SUM MAY BE CALCULATED WRONG");
                }

            } catch (IOException e) {
                System.out.println("CROP PROPERTIES FILE NOT FOUND. CROP SUM MAY BE CALCULATED WRONG");
            } finally {
                if (fis1 != null) {
                    try {
                        fis1.close();
                    } catch (IOException e) {
                    }
                }
            }

        }
        return rectangle;
    }


    public Rectangle getOrigRect(RsaImageSet ris, OutputInfo recropOI, int rot, String preferredType){
        Rectangle rectangle = new Rectangle(0, 0, 0, 0);

        if ( recropOI == null) {
            ExtensionFileFilter eff = new ExtensionFileFilter(preferredType);
            File[] srcs = ris.getPreferredInputDir().listFiles(eff);
            File src = srcs[0];

            BufferedImage bi1 = null;

            try {
                bi1 = ImageIO.read(src);

                if (rot == 1 || rot == 3) {
                    rectangle.width = bi1.getHeight();
                    rectangle.height = bi1.getWidth();
                } else {
                    rectangle.width = bi1.getWidth();
                    rectangle.height = bi1.getHeight();
                }

            } catch (IOException e) {
                System.out.println("ORIGINAL IMAGE FILE NOT FOUND. ORIGINAL SIZE WILL BE 0 IN CROP PROPERTIES");
            }
        }
        else{
            File cropPropertiesFile;
            Properties cropProps = new Properties();
            FileInputStream fis1 = null;

            cropPropertiesFile = new File(recropOI.getDir() + File.separator
                    + "crop.properties");
            try {
                fis1 = new FileInputStream(cropPropertiesFile);
                cropProps.load(fis1);
                if ( cropProps.getProperty(ORIG_RECT_PROP) != null ){
                    String[] params = cropProps.getProperty(ORIG_RECT_PROP).split(",");
                    rectangle.x = Integer.parseInt(params[0]);
                    rectangle.y = Integer.parseInt(params[1]);
                    rectangle.width = Integer.parseInt(params[2]);
                    rectangle.height = Integer.parseInt(params[3]);
                }
                else{
                    System.out.println("ORIGINAL SIZE NOT FOUND. IS IT AN OLD CROP? NEW ORIGINAL SIZE WILL BE WRONG");
                }

            } catch (IOException e) {
                System.out.println("CROP PROPERTIES FILE NOT FOUND. ORIGINAL SIZE MAY BE CALCULATED WRONG");
            } finally {
                if (fis1 != null) {
                    try {
                        fis1.close();
                    } catch (IOException e) {
                    }
                }
            }

        }

        return rectangle;
    }





	public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
		return new CropOutput(f, ris);
	}

    // tw 2015jun15
//	protected void writeCropFile(OutputInfo oi, int rot, Rectangle rect,
    protected void writeCropFile(OutputInfo oi, int rot, Rectangle rect, Rectangle rectSum,
                                 Rectangle origRect, ApplicationManager am, String inputType) {
        System.out.println("writeCropFile " + rectSum.x + " " + rectSum.y);
		Properties props = new Properties();
		props.setProperty(ROTATION_PROP, Integer.toString(rot));
		props.setProperty(CROP_PROP, rect.x + "," + rect.y + "," + rect.width
				+ "," + rect.height);

        rectSum.x = rectSum.x + rect.x;
        rectSum.y = rectSum.y + rect.y;
        System.out.println("writeCropFile " + rectSum.x + " " + rectSum.y);
        rectSum.width = rect.width;
        rectSum.height = rect.height;

        // tw 2015jun15
        props.setProperty(CROP_SUM_PROP, rectSum.x + "," + rectSum.y + "," + rectSum.width
                + "," + rectSum.height);
        props.setProperty(ORIG_RECT_PROP, origRect.x + "," + origRect.y + "," + origRect.width
                + "," + origRect.height);

		props.setProperty(INPUT_TYPE_PROP, inputType);

		File f = new File(oi.getDir().getAbsolutePath() + File.separator
				+ CROP_FILE);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			props.store(fos, CROP_TYPE);
		} catch (Exception e) {
			throw new CropException("Could not write to file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// // tw 2014nov13
		// am.getIsm().setFilePermissions(f);
		am.getIsm().setPermissions(f, false);
	}

	public String getInputType(OutputInfo oi) {
		Properties props = new Properties();
		File f = new File(oi.getDir().getAbsolutePath() + File.separator
				+ CROP_FILE);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			props.load(fis);
		} catch (IOException e) {
			throw new CropException("Could not load properties file: " + f, e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {

				}
			}
		}

		return props.getProperty(INPUT_TYPE_PROP);
	}

	public File getCropImageSubdir(OutputInfo oi) {
		return new File(oi.getDir().getAbsolutePath() + File.separator
				+ CROP_IMAGE_SUBDIR);
	}

	protected File getCropImage(File src, File imageSubdir, String srcExt) {
		File f = new File(imageSubdir.getAbsolutePath() + File.separator
				+ src.getName().replace("." + srcExt, CROP_SUFFIX));
		return f;
	}

	protected void cropAndRotate(File src, File dest, int rot, Rectangle rect) {
		BufferedImage bi1 = null;
		BufferedImage bi2 = null;
		BufferedImage bi3 = null;

		try {
			bi1 = ImageIO.read(src);
			//
			// handle impage type for recropping
			//
			// below, crop images are saved to tiff:
			// ImageIO.write(bi3, CROP_TYPE, dest)).
			// But, when reading cropped images for recropping,
			// the getType() returns zero (Custom Type),
			// which generates error when calling the BufferedImage constructor.
			//
			// So, hack the type setting the TYPE_3BYTE_BGR - it is the
			// same type that our original images have.
			// (works with jpg, but never tested with PNG, or other formats)
			//
			// By the way, the getType() returns not zero after reading
			// original images, so cropping works fine
			//
			// (One of the way to make it working for other original images
			// - not jpg as we have now - is calling getType() for some
			// original image that corresponds to the cropped/recropped one.
			// For instance, if
			// src="/data/rsa/processed_images/rice/RIL/p00001/d12/sandbox/crop/vp23_2013-03-27_09-46-44/images/OsRILp00001d12_01_crop.tiff"
			// then some original image
			// original image =
			// "/data/rsa/processed_images/rice/RIL/p00001/d12/original/jpg/OsRILp00001d12_01.jpg"
			//
			// But, still, there might be no an appropriate image
			// with not zero type found - in this case
			// different image type might be tried, which is not good too.
			//
			// So, leave for now as it is (it works) and review later
			// if absolutely necessary
			int type = bi1.getType();
			if (type == 0) {
				//
				// For now, our assumption is that all recropping would be done
				// using crop, recrop, recrop of recrop, etc, that is
				// no external sources outside for the recropping - so,
				// internal data would be the same as in the original data
				//
				// set default (the original jpg images are these type)
				type = BufferedImage.TYPE_3BYTE_BGR;
			}

			if (rot == 1 || rot == 3) {
				bi2 = new BufferedImage(bi1.getHeight(), bi1.getWidth(), type);
			} else {
				bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), type);

				// debug only TYPE_3BYTE_BGR = 5
				// bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), 5);
			}

			Graphics2D g1 = (Graphics2D) bi2.getGraphics();
			g1.rotate(rot * Math.PI / 2.0);

			if (rot == 1) {
				g1.translate(0, -bi2.getWidth());// bi2.getWidth(), 0);
			} else if (rot == 2) {
				g1.translate(-bi2.getWidth(), -bi2.getHeight());
			} else if (rot == 3) {
				g1.translate(-bi2.getHeight(), 0);
			}

			g1.drawImage(bi1, null, null);

			bi3 = new BufferedImage(rect.width, rect.height, type);
			Graphics2D g2 = (Graphics2D) bi3.getGraphics();
			g2.drawImage(bi2, 0, 0, rect.width, rect.height, rect.x, rect.y,
					rect.x + rect.width, rect.y + rect.height, null);

			/*
			 * javax.imageio.ImageWriter iw =
			 * ImageIO.getImageWritersByFormatName(CROP_TYPE).next();
			 * com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam iwp =
			 * (com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam)iw.
			 * getDefaultWriteParam();
			 * iwp.setCompressionMode(TIFFImageWriteParam.MODE_EXPLICIT);
			 * com.sun.media.imageioimpl.plugins.tiff.TIFFLZWCompressor comp =
			 * new com.sun.media.imageioimpl.plugins.tiff.TIFFLZWCompressor(1);
			 * iwp.setTIFFCompressor(comp);
			 * iwp.setCompressionType(comp.getCompressionType());
			 * javax.imageio.IIOImage iomg = new javax.imageio.IIOImage(bi3,
			 * null, iw.getDefaultStreamMetadata(iwp));
			 * javax.imageio.stream.FileImageOutputStream fios = new
			 * javax.imageio.stream.FileImageOutputStream(dest);
			 * iw.setOutput(fios); iw.write(iw.getDefaultStreamMetadata(iwp),
			 * iomg, iwp); fios.close();
			 */
			ImageIO.write(bi3, CROP_TYPE, dest);
		} catch (Exception e) {
			throw new CropException("Error cropping image: "
					+ src.getAbsolutePath() + " to " + dest.getAbsolutePath(),
					e);
		}

	}

	public void writeThumbnail(OutputInfo oi, BufferedImage bi,
			ApplicationManager am) {
        System.out.println(this.getClass() + " writeThumbnail");

//        TIFFEncodeParam TEP = new TIFFEncodeParam();
//        TIFFImageWriteParam tiffWritePar = new TIFFImageWriteParam(Locale.getDefault());
//        tiffWritePar.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);

		File f = getThumbnail(oi);
		try {
            System.out.println(this.getClass() + " " + bi + " " + f);
            Iterator writers = ImageIO.getImageWritersByFormatName("tiff");
			boolean b = ImageIO.write(bi, THUMBNAIL_TYPE, f);
			if (!b) {
				throw new CropException(
						"Could not find appropriate writer for type: "
								+ THUMBNAIL_TYPE
								+ "; in list "
								+ Arrays.toString(ImageIO
										.getWriterFormatNames()), null);
			}

		} catch (IOException e) {
			throw new CropException("Could not write thumbnail file: "
					+ f.getAbsolutePath(), e);
		}

		// // tw 2014nov13
		// am.getIsm().setFilePermissions(f);
		am.getIsm().setPermissions(f, false);

	}

	/**
	 * Sets permissions on generate filesFHFH
	 */
	public void postprocessrecrop(OutputInfo output, ApplicationManager am) {
		// the crop images are in the "images" folder
		File cropimgdir = new File(output.getDir() + File.separator + "images");

		for (File f : cropimgdir.listFiles()) {
			if (f.isFile()) {
				String recropsuffix = "_" + CROP_PROP + CROP_SUFFIX;
				String fn = f.getName();
				int ind = fn.indexOf(recropsuffix);
				if (ind > 0) {
					String fnnew = fn.substring(0, ind) + CROP_SUFFIX;
					File target = new File(cropimgdir.getAbsolutePath()
							+ File.separator + fnnew);
					FileUtil.renameFile(f, target);
				}
			}
		}

		// permissions - in fact, not needed here - do just in case
		for (File f : output.getDir().listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				// am.getIsm().setFilePermissions(f);
//				am.getIsm().setPermissions(f, false);
			}
		}
	}

	public String getReviewString(OutputInfo oi) {
		return oi.toString() + "(" + this.getInputType(oi) + ")";
	}

	protected static class CropException extends RuntimeException {
		public CropException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
