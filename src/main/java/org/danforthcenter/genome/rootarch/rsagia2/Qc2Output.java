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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * 
 * @author vp23
 */
public class Qc2Output extends OutputInfo implements IOutputDescriptorsQc2 {
	public static final String QC2_FILENAME = "thresholding_qc2.csv";
	protected File qc2File;

	public Qc2Output(File dir, RsaImageSet ris) {
		super(dir, ris);

		qc2File = new File(dir.getAbsolutePath() + File.separator
				+ QC2_FILENAME);
		outputs = InputOutputTypes.QC2;
	}

	@Override
	public File getCsvFile() {
		return qc2File;
	}

	@Override
	public int getOutputs() {
		return super.getOutputs();
	}

	private HashMap getQc2() {
		HashMap qc2 = new HashMap();
		FileInputStream fis = null;
		int count = 0;

		try {
			FileInputStream fstream = new FileInputStream(qc2File);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] headers = null;
			String[] qc2_data = null;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				count++;
				if (count == 1)
					headers = strLine.split(",");
				if (count == 2)
					qc2_data = strLine.split(",");

				// Print the content on the console
				// System.out.println (strLine);
			}
			// TODO:check if data is taken from the file
			// arrays should not be null and be of equal size
			// populate hashtable
			for (int i = 0; i < headers.length; i++) {
				qc2.put(headers[i], qc2_data[i]);
			}

		} catch (Exception e) {
			throw new Qc2OutputException("Could not read Qc2 infor from: "
					+ qc2File.getAbsolutePath(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}

		return qc2;
	}

	@Override
	public boolean isValid() {
		// eh, a bit of a hack
		boolean ans = true;
		try {
			getQc2();
		} catch (Exception e) {
			ans = false;
		}

		return ans;
	}

	// ====================<editor-fold desc="Qc2OutputException()">{{{
	protected static class Qc2OutputException extends RuntimeException {
		public Qc2OutputException(Throwable th) {
			super(th);
		}

		public Qc2OutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of Qc2OutputException()....................}}}</editor-fold>
}
