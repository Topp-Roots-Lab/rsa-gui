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
// tw 2014nov12 update security to be platform independent
// combine file and directory permissions to work with posix and acl systems
// Rather than pass separate permissions through many methods,
// Check if path is file or directory and apply appropriate permissions
public interface ISecurityManager {

	public void setPermissions(File f, boolean r);

	// public void setDirectoryPermissions (File dir);

	// public void setFilePermissions(File f);

    public void setDirLinkPermissions(File link, boolean r);

//	public void setLinkPermissions(File link);

	public void setRecursivePermissions(File dir);
}
