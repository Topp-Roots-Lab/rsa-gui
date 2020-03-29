/*
 *  Copyright 2013 vp23.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

//import edu.duke.benfeylab.miscellanies.StrLib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author vp23
 */

/**
 * class VoxNpzUtil
 */
public class VoxNpzUtil {

	/** Private constructor to prevent instantiation */
	private VoxNpzUtil() {
	}

	// ======================================<editor-fold desc="main">{{{
	/**
	 * Convert '.out' Rootwork voxels files to '.npz' files (used in Qvox)
	 * 
	 * @param dir
	 *            directory
	 * @param file
	 *            voxels file name
	 * 
	 *            The second parameter is optional. If this parameter missed,
	 *            then then all '.out' voxels files in the given directory are
	 *            converted. All original '.out' voxels files remain the same
	 *            and are not touched by the conversion program. The converted
	 *            file(s) are located in the same directory 'dir' with the
	 *            '.npz' extension: for instance, 'voxels.out' ->
	 *            'voxels.out.npz'
	 */
	public static void Convert(String dir, String file) {
		try {
			if (dir == null)
				return;

			DIR = new File(dir);

			MsgErr = "Error occured during "
					+ "conversion voxel .out file to .npz file. "
					+ "Input parameters: dir=" + DIR.getAbsolutePath();

			if (file != null) {
				FILE_VOXELS = new File(file);
				MsgErr = MsgErr + " file=" + FILE_VOXELS.getAbsolutePath();
			}

			doConversion();

			// gzip test
			// doTest();
		} catch (Exception exc) {
			throw new VoxNpzUtilException(MsgErr, exc);
		}
	}

	// End of main..................................}}}</editor-fold>

	// ======================================<editor-fold desc="variables">{{{

	private static File DIR;
	private static File FILE_VOXELS;
	private static int LINES_SKIP = 2;
	private static int NUMBER_VOXELS_LINE = 2;
	private static int NUMBER_VOXELS = 2;
	// array list for points with coordinates x,y,z
	private static ArrayList<Point> points = new ArrayList<Point>();
	// max and min values
	private static int x_min = Integer.MAX_VALUE;
	private static int y_min = Integer.MAX_VALUE;
	private static int z_min = Integer.MAX_VALUE;
	private static int x_max = 0;
	private static int y_max = 0;
	private static int z_max = 0;

	private static String MsgErr = "";

	// End of variables..................................}}}</editor-fold>

	// ======================<editor-fold desc="doConversion">{{{
	@SuppressWarnings("static-access")
	private static void doConversion() {
		// if both DIR and FILE_VOXELS are passed
		if (FILE_VOXELS != null) {
			String name = FILE_VOXELS.getName();
			int index = name.lastIndexOf(".");
			if (index == -1)
				return;
			String end = name.substring(index);
			// this would include also .out and .out_1 endings and the like
			if (!end.toLowerCase().startsWith(".out"))
				return;

			// input voxels .out file
			String fn = DIR.getAbsolutePath() + File.separator + name;
			File input = new File(fn);
			// output vol file
			File output = new File(fn + ".npz");
			System.out.print("Processing " + fn + "\n");
			processContents(input, output);
			return;
		}
		// if only not null DIR is passed
		File[] files = DIR.listFiles();
		if (files == null) {
			String msg = "files=" + files
					+ ". Most likely permission denied ...";
			System.out.println(msg);
			return;
		}
		int count = 0;
		for (File file : files) {
			String name = file.getName();
			int index = name.lastIndexOf(".");
			if (index == -1)
				continue;
			String end = name.substring(index);
			// this would include also .out and .out_1 endings and the like
			if (end.toLowerCase().startsWith(".out")) {
				count++;
				// input voxels .out file
				String input_fn = DIR.getAbsolutePath() + File.separator + name;
				File input = new File(input_fn);
				// output vol file
				File output = new File(input_fn + ".npz");
				System.out.print("Processing " + input + "\n");
				processContents(input, output);
			}
		}
		if (count == 0) {
			String msg = "No voxels files with '.out' extension found";
			System.out.println(msg);
		}
	}

	// End of doConversion.....................}}}</editor-fold>

	// =================================<editor-fold desc="processContents">{{{
	static private void processContents(File input, File output) {
		try {
			BufferedReader inf = new BufferedReader(new FileReader(input));
			FileOutputStream fos = new FileOutputStream(output);
			DataOutputStream dos = new DataOutputStream(fos);
			try {
				String line = null;
				int count = 0;
				while ((line = inf.readLine()) != null) {
					count++;
					// get the number of voxels - the second line in the input
					// voxels file
					if (count == NUMBER_VOXELS_LINE) {
						NUMBER_VOXELS = Integer.parseInt(line);
						String msg = "read number of voxels: " + NUMBER_VOXELS
								+ "\n";
						System.out.print(msg);
						continue;
					}
					if (count > LINES_SKIP)
						processLine(line);
				}
				// get min and max for coordinates x,y,z
				findMaxMin(points);
				// calculate size
				int x_size = x_max - x_min + 1;
				int y_size = y_max - y_min + 1;
				int z_size = z_max - z_min + 1;
				// show size
				System.out.print("x_size=" + x_size + "\n");
				System.out.print("y_size=" + y_size + "\n");
				System.out.print("z_size=" + z_size + "\n");
				// give a warning
				if (NUMBER_VOXELS != points.size()) {
					String msg = "WARNING: actual number of voxels "
							+ points.size()
							+ " does not equal the number of voxels"
							+ " given at the top of the file " + NUMBER_VOXELS
							+ "\n";
					System.out.print(msg);
				}
				// create buffer for data
				int size = x_size * y_size * z_size;
				byte[] buffer = new byte[size];
				// translate every point in the set to (1,1,1) and write to
				// buffer
				for (Point p : points) {
					// translate
					int x = p.x - x_min + 1;
					int y = p.y - y_min + 1;
					int z = p.z - z_min + 1;
					// write to bufffer
					int id_off = (z - 1) * y_size * x_size + (y - 1) * x_size
							+ x - 1;
					buffer[id_off] = (byte) 200;
				}
				// gzip
				byte[] gzbuffer = gZIP(buffer);
				// write header
				writeHeaderNpz(dos, x_size, y_size, z_size);
				// write data
				dos.write(gzbuffer);
			} finally {
				resetVars();
				inf.close();
				fos.close();
				dos.close();
			}
		} catch (IOException exc) {
			throw new VoxNpzUtilException(MsgErr, exc);
		}
	}

	// End of processContents..................................}}}</editor-fold>

	// =================================<editor-fold desc="resetVars">{{{
	private static void resetVars() {
		points.clear();
		setMaxMin();
	}

	// End of resetVars..................................}}}</editor-fold>

	// =================================<editor-fold desc="setMaxMin">{{{
	private static void setMaxMin() {
		x_min = Integer.MAX_VALUE;
		y_min = Integer.MAX_VALUE;
		z_min = Integer.MAX_VALUE;
		x_max = 0;
		y_max = 0;
		z_max = 0;
	}

	// End of setMaxMin..................................}}}</editor-fold>

	// =================================<editor-fold desc="processLine">{{{
	private static void processLine(String line) {
		String tokens[] = line.split(" ");

		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		int z = Integer.parseInt(tokens[2]);

		points.add(new Point(x, y, z));
	}

	// End of processLine..................................}}}</editor-fold>

	// =================================<editor-fold desc="findMaxMin">{{{
	private static void findMaxMin(ArrayList<Point> points) {
		for (Point p : points) {
			if (p.x > x_max)
				x_max = p.x;
			if (p.y > y_max)
				y_max = p.y;
			if (p.z > z_max)
				z_max = p.z;
			if (p.x < x_min)
				x_min = p.x;
			if (p.y < y_min)
				y_min = p.y;
			if (p.z < z_min)
				z_min = p.z;
		}
	}

	// End of findMaxMin..................................}}}</editor-fold>

	// =================================<editor-fold desc="gZIP">{{{
	private static byte[] gZIP(byte[] data) throws IOException {
		// A grow-able, memory-resident byte array
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// A gzip processor to write into the memory array
		GZIPOutputStream gzipos = new GZIPOutputStream(byteArrayOutputStream);
		// Write the data to the gzip processor
		gzipos.write(data);
		// Close the file.This adds the gzip checksum, etc.
		gzipos.close();
		// Get the compressed data.This can be persisted
		byte[] gzdata = byteArrayOutputStream.toByteArray();

		return gzdata;
	}

	// End of gZIP..................................}}}</editor-fold>

	// =================================<editor-fold desc="writeHeaderNpz">{{{
	private static void writeHeaderNpz(DataOutputStream dos, int x_size,
			int y_size, int z_size) throws IOException {
		// npz file header
		dos.writeBytes("NPZ-11\n");
		dos.writeBytes("# Image file generated by vox2npz conversion program\n");
		dos.writeBytes("TYPE NPIC_IMAGE_3C\n");
		dos.writeBytes("COMP GZIP\n");
		dos.writeBytes("XMAX " + Integer.toString(x_size) + "\n");
		dos.writeBytes("YMAX " + Integer.toString(y_size) + "\n");
		dos.writeBytes("ZMAX " + Integer.toString(z_size) + "\n");
		// dos.writeBytes("PROP Alpha-Color: 0\n");
		// dos.writeBytes("PROP Voxel-Size: 1\n");
		// dos.writeBytes("PROP Int-Endian: 0123\n");
		// dos.writeBytes("PROP Voxel-Endian: 0\n");
		// dos.writeBytes("PROP Res-X: 1.000000\n");
		// dos.writeBytes("PROP Res-Y: 1.000000\n");
		// dos.writeBytes("PROP Res-Z: 1.000000\n");
		dos.writeBytes("DATA\n");
	}

	// End of writeHeaderNpz..................................}}}</editor-fold>

	// ===================================<editor-fold desc="Point">{{{
	private static final class Point {
		public final int x;
		public final int y;
		public final int z;

		Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	// End of Point..................................}}}</editor-fold>

	// ====================<editor-fold desc="VoxNpzUtilException()">{{{
	protected static class VoxNpzUtilException extends RuntimeException {
		public VoxNpzUtilException(Throwable th) {
			super(th);
		}

		public VoxNpzUtilException(String msg, Throwable th) {
			super(msg, th);
		}
	}

	// End of VoxNpzUtilException()....................}}}</editor-fold>

	// ////////////////////////////////////////////////////////////////
	// ///TEST
	// ////////////////////////////////////////////////////////////////
	// ===================================<editor-fold desc="doTest">{{{
	private static void doTest() throws IOException {
		// Original Data
		byte[] dataBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 1, 2, 3, 4, 5, 5, 6, 67, 7, 8, 9 };
		// A grow-able, memory-resident byte array
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// A gzip processor to write into the memory array
		GZIPOutputStream gzipos = new GZIPOutputStream(byteArrayOutputStream);
		// Write the data to the gzip processor
		gzipos.write(dataBytes);
		// Close the file.This adds the gzip checksum, etc.
		gzipos.close();
		// Debug.Show what we are compressing
		System.out.println("data bytes len " + dataBytes.length);
		int len = dataBytes.length;
		System.out.print(" data: ");
		for (byte b : dataBytes) {
			System.out.print(b + " ");
		}
		System.out.println("");
		// Get the compressed data.This can be persisted
		byte[] gzBytes = byteArrayOutputStream.toByteArray();
		// Debug,show that the compressed data is different (and presumably
		// shorter)
		System.out.println("compressed bytes len " + gzBytes.length);
		System.out.print(" data compressed: ");
		for (byte b : gzBytes) {
			System.out.print(b + " ");
		}
		System.out.println("");
		// A structure to read memory-resident byte arrays
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				gzBytes);
		// A gzip expander
		GZIPInputStream gzipis = new GZIPInputStream(byteArrayInputStream);
		// A place to hold the uncompressed data, the length is carried over
		// from the original data
		byte[] unzip = new byte[len];
		// Read the compressed data into the uncompressed byte array
		gzipis.read(unzip, 0, len);
		// Debug,show that the recovered data exactly matches the input data
		System.out.println("un-compressed bytes len " + unzip.length);
		System.out.print(" data un-compressed: ");
		for (byte b : unzip) {
			System.out.print(b + " ");
		}
		System.out.println("");
		System.out.println("Input data matches Output data: "
				+ Arrays.equals(dataBytes, unzip));
		System.out.println("Compression ratio: "
				+ String.format("%.2f", (double) gzBytes.length
						/ (double) dataBytes.length));
	}
	// End of doTest..................................}}}</editor-fold>
	// ////////////////////////////////////////////////////////////////
}
