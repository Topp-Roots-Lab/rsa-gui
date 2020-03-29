/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Lists only files that have either the specified extension, or whose extention
 * is found in the given list of extensions.
 * 
 * @author bm93
 */
public class ExtensionFileFilter implements java.io.FileFilter {

	protected ArrayList<String> extensions;

	/**
	 * Don't include the ".". That is, "xml" vs ".xml".
	 * 
	 * @param ext
	 */
	public ExtensionFileFilter(String ext) {
		extensions = new ArrayList<String>();
		extensions.add(ext);
	}

	public ExtensionFileFilter(ArrayList<String> extensions) {

		this.extensions = new ArrayList<>(extensions);
		Collections.sort(this.extensions);
	}

	public boolean accept(File pathname) {
		String s = pathname.getName();
		String[] ss = s.split("\\.");
		boolean ans = false;

		if (ss.length > 0) {
			String n = ss[ss.length - 1];
			if (extensions.contains(n)) {
				ans = true;
			}
		}

		return ans;
	}

}
