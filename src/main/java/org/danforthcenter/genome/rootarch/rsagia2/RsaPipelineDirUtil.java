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

import java.io.File;

import org.danforthcenter.genome.rootarch.rsagia.app2.MainFrame;

/**
 * 
 * @author vp23
 */
public class RsaPipelineDirUtil {

	// ============================<editor-fold desc="getProcessedImages">{{{
	public File getProcessedImages() {

		return MainFrame.getProcessedImages();
	}

	// End of getProcessedImages...........................}}}</editor-fold>

	// ============================<editor-fold desc="getSpecies">{{{
	public String getSpecies(File f) {

		String specie = getElement(f, 0);
//        System.out.println(this.getClass() + " species " + specie);
		return specie;
	}

	// End of getSpecies..........................}}}</editor-fold>

	// ============================<editor-fold desc="getExp">{{{
	public String getExp(File f) {
		String exp = getElement(f, 1);
//        System.out.println(this.getClass() + " exp " + exp);
		return exp;
	}

	// End of getExp...........................}}}</editor-fold>

	// ============================<editor-fold desc="getPlant">{{{
	public String getPlant(File f) {
		String plant = getElement(f, 2);
		return plant;
	}

	// End of getPlant...........................}}}</editor-fold>

	// ============================<editor-fold desc="getDay">{{{
	public String getDay(File f) {
		String day = getElement(f, 3);
		return day;
	}

	// End of getDay...........................}}}</editor-fold>

	// ============================<editor-fold desc="getElement">{{{
	private String getElement(File f, int num) {
		String elem[] = null;
		try {
			String fn = f.getAbsolutePath();
			String processed_images = getProcessedImages().getAbsolutePath();
			int ind = fn.indexOf(processed_images);
			String fullpath = fn.substring(ind + processed_images.length() + 1);
//            System.out.println(this.getClass() + " fullpath " + fullpath);
			// fullpath=
			// /data/rsa/processed_images/insilico/cyl/a0.01-b0.01-h1-top0-topr0-x0y0z0-eps0.1-pi8/orthogonal/saved/giaroot_2d/vp23_2012-12-11_15-03-13
//			elem = fullpath.split("/");

            String separator = File.separator;
            if ( File.separator.equalsIgnoreCase("\\") ) {
                separator = separator + File.separator;
            }
//            System.out.println(this.getClass() + " separator " + separator);
            elem = fullpath.split(separator);
//            System.out.println(this.getClass() + " elem length " + elem.length);

        } catch (Exception e) {
			throw new RsaPipelineDirUtilException(
					"Invalid Rsa Pipelie file or directory: "
							+ f.getAbsolutePath(), e);
		}

		return elem[num];
	}

	// End of ImageFileFilter...........................}}}</editor-fold>

	// ==================<editor-fold desc="RsaPipelineDirUtilException()">{{{
	protected static class RsaPipelineDirUtilException extends RuntimeException {
		public RsaPipelineDirUtilException(Throwable th) {
			super(th);
		}

		public RsaPipelineDirUtilException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of RsaPipelineDirUtilException().................}}}</editor-fold>

}
