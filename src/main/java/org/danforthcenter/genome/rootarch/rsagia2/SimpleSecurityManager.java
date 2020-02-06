/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author bm93
 */
public class SimpleSecurityManager implements ISecurityManager {

	protected String dirPermissions;
	protected String dirGroup;
	protected String filePermissions;
	protected String fileGroup;
	protected String recursivePermissions;

	// // tw 2014nov12 directory and file permissions are set in FileVisitorUtil
	// // Differences between posix and acl systems complicate passing between
	// methods
	// public SimpleSecurityManager(String dirPermissions, String dirGroup,
	// String filePermissions, String fileGroup) {
	public SimpleSecurityManager(String dirGroup, String fileGroup) {
		// this.dirPermissions = dirPermissions;
		this.dirGroup = dirGroup;
		// this.filePermissions = filePermissions;
		this.fileGroup = fileGroup;
	}

	// tw 2014nov12 update security to be platform independent
	// combine file and directory permissions to work with posix and acl systems
	// Rather than pass separate permissions through many methods,
	// Check if path is file or directory and apply appropriate permissions
	public void setPermissions(File f, boolean recursive) {
		try {
            //System.out.println("setPermissions " + f + " " + recursive);

			FileUtil.setSecurity(f, fileGroup, recursive);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * public void setDirectoryPermissions(File dir) { try {
	 * FileUtil.setSecurity(dir, dirPermissions, dirGroup, false); } catch
	 * (IOException ioe) { ioe.printStackTrace(); } }
	 */
	public void setDirLinkPermissions(File link, boolean recursive) {
		//// do nothing, we'll rely on unix default

        // 2015jan1 tw For Windows and Unix, set permissions for directory but not linked files
        try {
//            System.out.println("setPermissions " + link + " " + recursive);
            FileUtil.setSecurity(link, dirGroup, recursive);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}


    /*
    * public void setFilePermissions(File f) { try { FileUtil.setSecurity(f,
    * filePermissions, fileGroup, false); } catch (IOException ioe) {
    * ioe.printStackTrace(); } }
    */
/*    public void setLinkPermissions(File link, boolean recursive) {
        //// do nothing, we'll rely on unix default
    }
*/



	/**
	 * Erroneous.
	 * 
	 * @param dir
	 */
	public void setRecursivePermissions(File dir) {

	}

}
