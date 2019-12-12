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
public class Qc3Output extends OutputInfo implements IOutputDescriptorsQc3 {
	public static final String QC3_FILENAME = "anglestop_qc3.csv";
	protected File qc3File;

	public Qc3Output(File dir, RsaImageSet ris) {
		super(dir, ris);

		qc3File = new File(dir.getAbsolutePath() + File.separator
				+ QC3_FILENAME);
		outputs = InputOutputTypes.QC3;
	}

	@Override
	public File getCsvFile() {
		return qc3File;
	}

	private HashMap getQc3() {
		HashMap qc3 = new HashMap();
		FileInputStream fis = null;
		int count = 0;

		try {
			FileInputStream fstream = new FileInputStream(qc3File);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] headers = null;
			String[] qc3_data = null;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				count++;
				if (count == 1)
					headers = strLine.split(",");
				if (count == 2)
					qc3_data = strLine.split(",");

				// Print the content on the console
				// System.out.println (strLine);
			}
			// TODO:check if data is taken from the file
			// arrays should not be null and be of equal size
			// populate hashtable
			for (int i = 0; i < headers.length; i++) {
				qc3.put(headers[i], qc3_data[i]);
			}

		} catch (Exception e) {
			throw new Qc3OutputException("Could not read Qc3 infor from: "
					+ qc3File.getAbsolutePath(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {

				}
			}
		}

		return qc3;
	}

	@Override
	public boolean isValid() {
		// eh, a bit of a hack
		boolean ans = true;
		try {
			getQc3();
		} catch (Exception e) {
			ans = false;
		}

		return ans;
	}

	// ====================<editor-fold desc="Qc3OutputException()">{{{
	protected static class Qc3OutputException extends RuntimeException {
		public Qc3OutputException(Throwable th) {
			super(th);
		}

		public Qc3OutputException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of Qc3OutputException()....................}}}</editor-fold>
}
