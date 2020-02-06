/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;

/**
 * Accepts only existing directories - used with the File.listFiles method.
 * 
 * @author bm93
 */
public class DirectoryFileFilter implements java.io.FileFilter {

	public boolean accept(File pathname) {

        boolean pathCheck;
//        System.out.println(pathname);
        pathCheck = pathname.exists() && pathname.isFile();
//        System.out.println(pathCheck);

        // tw 2014dec31
//		return pathname.exists() && pathname.isFile();
        return pathname.exists() && pathname.isDirectory();
	}

}
