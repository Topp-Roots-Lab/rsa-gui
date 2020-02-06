/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;

/**
 * 
 * @author bm93
 */
public class NameSubstringFileFilter implements java.io.FileFilter {
	protected String substring;

	public NameSubstringFileFilter(String substring) {
		this.substring = substring;
	}

	public boolean accept(File pathname) {
		return pathname.getName().contains(substring);
	}

}
