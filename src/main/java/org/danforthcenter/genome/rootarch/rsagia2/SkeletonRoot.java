/*
 * Copyright 2012 Duke University, Benfey Lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author vp23
 * 
 *         not used for now
 * 
 */
public class SkeletonRoot {
	protected final static String TEMPLATE_EXT = "xml";

	protected String skel3DExecScript;
	protected File skel3DConfig;

	public SkeletonRoot(String skel3DExecScript, File skel3DConfig) {
		this.skel3DExecScript = skel3DExecScript;
		this.skel3DConfig = skel3DConfig;
	}

	/**
	 * Assumes a previously written job xml file exists at JOB_XML_NAME.
	 */
	public Process start() {
		String[] cmd = { "nice", skel3DExecScript };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);

		Process ans = null;
		try {
			ans = pb.start();
		} catch (IOException e) {
			throw new GiaRootException("Error starting process", e);
		}

		return ans;
	}

	/**
	 * Sets permissions on generate files
	 */
	public void postprocess(File dir, ApplicationManager am) {
		for (File f : dir.listFiles()) {
			if (f.isFile()) {

				// // tw 2014nov13
				// am.getIsm().setFilePermissions(f);
				am.getIsm().setPermissions(f, false);
			}
		}
	}

	protected static class GiaRootException extends RuntimeException {
		public GiaRootException(String msg, Throwable th) {
			super(msg, th);
		}
	}
}
