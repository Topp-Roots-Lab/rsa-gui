/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

//import edu.duke.genome.rootarch.rsagia.*;
import java.io.File;
import java.io.IOException;

// 2014oct8 tw imports added for modified methods
import java.awt.Desktop;
import java.util.Properties;
import java.util.*;
import java.nio.file.*;
//import java.nio.file.attribute.*;
//import java.util.Set;
//import java.nio.file.FileSystem;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.attribute.PosixFileAttributeView;
//import java.nio.file.attribute.AclFileAttributeView;
//import java.nio.file.Path;

/**
 * Utility class for Linux-based file operations. The Java file IO library
 * doesn't have much support for Unix-specific security concepts. Unfortunately,
 * this makes this code platform-dependent.
 * 
 * TODO: convert this to an interface/factory pattern.
 * 
 * @author bm93
 */

/**
 * 2014oct28 tw Updated to newer Java Nio functionality to make RSA-Gia run on
 * multiple platforms
 */

public class FileUtil {

	public static void setSecurity(File f, String group, boolean recursive)
			throws IOException {

//        System.out.println("setSecurity " + f + " " + recursive);

		String fileSys = null;
		String action = "secure";

		Path filePath = FileSystems.getDefault().getPath(f.toString());
		FileSystem fileSystem = FileSystems.getDefault();
		Set<String> fileSystemViews = fileSystem.supportedFileAttributeViews();

		if (fileSystemViews.contains("posix")) {
			fileSys = "posix";
		} else if (fileSystemViews.contains("acl")) {
			fileSys = "acl";
		}

		FileVisitorUtil fileVisitorUtil = new FileVisitorUtil(filePath,
				fileSys, group, recursive, action);
		fileVisitorUtil.doFileVisits();

		/**
         * Changes group and permissions of file or directory via chmod and
         * chgrp commands.
         *
         * @param f
         *            file or directory set security on
         * @param chmod
		 *            the permission parameter to chmod, (e.g., go+rwx,u-X)
		 * @param group
		 *            the group identifier to pass to chgrp
		 * @param recursive
		 *            whether to pass -R parameter to both commands
		 */
		/*
		 * String[] cmd = null; if (recursive) { String[] t = { "chgrp", "-R",
		 * group, f.getAbsolutePath() }; cmd = t; } else { String[] t = {
		 * "chgrp", group, f.getAbsolutePath() }; cmd = t; }
		 * 
		 * ProcessBuilder pb = new ProcessBuilder(cmd); int ret = -1; Process p
		 * = null; try { p = pb.start(); ret = p.waitFor(); } catch (IOException
		 * e) { throw new FileUtilException(e); } catch (InterruptedException e)
		 * { Thread.currentThread().interrupt(); }
		 * 
		 * if (p != null) { ProcessUtil.dispose(p); }
		 * 
		 * if (ret != 0) { throw new FileUtilException("Command: " +
		 * Arrays.toString(cmd) + "; returned: " + ret); }
		 * 
		 * if (recursive) { String[] t = { "chmod", "-R", chmod,
		 * f.getAbsolutePath() }; cmd = t; } else { String[] t = { "chmod",
		 * chmod, f.getAbsolutePath() }; cmd = t; }
		 * 
		 * pb = new ProcessBuilder(cmd); ret = -1; p = null; try { p =
		 * pb.start(); ret = p.waitFor(); } catch (IOException e) { throw new
		 * FileUtilException(e); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); }
		 * 
		 * if (p != null) { ProcessUtil.dispose(p); } if (ret != 0) { throw new
		 * FileUtilException("Command: " + cmd + "; returned: " + ret); }
		 */

	}

	/**
	 * Uses the native rm -r method in order to avoid following symlinks.
	 * 
	 * @param f
	 */
	public static void deleteRecursively(File f) {

		String fileSys = null;
		String group = null;
		String action = "delete";
		Boolean recursive = true;

		Path filePath = FileSystems.getDefault().getPath(f.toString());

		FileVisitorUtil fileUSecurity = new FileVisitorUtil(filePath, fileSys,
				group, recursive, action);
		fileUSecurity.doFileVisits();

		/*
		 * String[] cmd = { "rm", "-r", f.getAbsolutePath() }; ProcessBuilder pb
		 * = new ProcessBuilder(cmd); int ret = -1; Process p = null; try { p =
		 * pb.start(); ret = p.waitFor(); } catch (IOException e) { throw new
		 * FileUtilException(e); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); }
		 * 
		 * if (p != null) { ProcessUtil.dispose(p); }
		 * 
		 * if (ret != 0) { throw new FileUtilException("Command: " +
		 * Arrays.toString(cmd) + "; returned: " + ret); }
		 */

	}

	// 2014oct13 tw change src to target and target to link to conform to unix
	// terms
    // 2015jan1 tw Windows doesn't allow creating directory symlinks without elevated
    // privileges. So, use 2 different methods, one for directories and one for files.

	/**
	 * Creates a symbolic link target, pointing to file src
	 * 
	 * @param target
	 *            file the link will point
	 * @param link
	 *            location of the link/shortcut
	 */
	// public static void createSymLink(File src, File target) {
	public static void createDirLink(File target, File link) {
//    public static void createSymLink(File link, File target) {

		// 2014oct13 tw

		Path targetPath = target.toPath();
		Path symPath = link.toPath();

        if (!link.exists()) {
            link.mkdir();
        }


/*
		try {
//			Files.createSymbolicLink(symPath, targetPath);
//            Files.createLink(symPath, targetPath);
		} catch (IOException ioe) {
			throw new FileUtilException(ioe);
		} catch (UnsupportedOperationException uoe) {
			// Some file systems do not support symbolic links.
			throw new FileUtilException(uoe);
		}
*/

	}


    /**
     * Creates a symbolic link target, pointing to file src
     *
     * @param target
     *            file the link will point
     * @param link
     *            location of the link/shortcut
     */
    // public static void createSymLink(File src, File target) {
//    public static void createSymLink(File target, File link) {
    public static void createSymLink(File link, File target) {

        // 2014oct13 tw

        Path targetPath = target.toPath();
        Path symPath = link.toPath();
//        File targetFile = new File("C:\\Users\\tom\\testDir\\testSubDir\\testFile.txt");
//        Path targetPath = targetFile.toPath();
//        File symFile = new File("C:\\Users\\tom\\testDir\\testSubDir\\testFileSym2.txt");
//        Path symPath = symFile.toPath();


        FileSystem fileSystem = FileSystems.getDefault();
        Set<String> fileSystemViews = fileSystem.supportedFileAttributeViews();

        try {

            if (fileSystemViews.contains("posix")) {
                Files.createSymbolicLink(symPath, targetPath);
            } else if (fileSystemViews.contains("acl")) {
                Files.createLink(symPath, targetPath);
            }

//            Files.createSymbolicLink(symPath, targetPath);
//            Files.createLink(symPath, targetPath);
//            Files.copy(targetPath, symPath);
        } catch (IOException ioe) {
            throw new FileUtilException(ioe);
        } catch (UnsupportedOperationException uoe) {
            // Some file systems do not support symbolic links.
            throw new FileUtilException(uoe);
        }
    }





	/**
	 * renames file src to target
	 * 
	 * @param src
	 * @param target
	 */
	public static void renameFile(File src, File target) {
		Path srcPath = src.toPath();
		Path targetPath = target.toPath();

		// tw 2014nov11
		// If target is a directory, then src will be moved while retaining name
		// If target is a file, then src will be renamed
		// with the absolute path determined by target first, then source
		// If source can be a directory, this method is not sufficient
		// A copy method, and possibly a file tree visitor, is needed
		// in order to handle cases where directory is not empty

		if (target.isDirectory()) {
			try {
				Files.move(srcPath, targetPath.resolve(srcPath.getFileName()),
						StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException ioe) {
				throw new FileUtilException(ioe);
			} catch (UnsupportedOperationException uoe) {
				throw new FileUtilException(uoe);
			} catch (SecurityException se) {
				throw new FileUtilException(se);
			}
		} else {
			try {
				Files.move(srcPath, srcPath.resolveSibling(targetPath),
						StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException ioe) {
				throw new FileUtilException(ioe);
			} catch (UnsupportedOperationException uoe) {
				throw new FileUtilException(uoe);
			} catch (SecurityException se) {
				throw new FileUtilException(se);
			}
		}

		/*
		 * String[] cmd = { "mv", src.getAbsolutePath(),
		 * target.getAbsolutePath() }; ProcessBuilder pb = new
		 * ProcessBuilder(cmd);
		 * 
		 * int ret = -1; Process p = null; try { p = pb.start(); ret =
		 * p.waitFor(); } catch (IOException e) { throw new
		 * FileUtilException(e); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); }
		 * 
		 * if (p != null) { ProcessUtil.dispose(p); }
		 * 
		 * if (ret != 0) { throw new FileUtilException("Command: " +
		 * Arrays.toString(cmd) + "; returned: " + ret); }
		 */

	}

	/**
	 * renames directory src to newName by using external application to obtain privileges
	 *
	 * @param src
	 * @param newName
	 * @param dirRenameApp
	 */
	public static void renameDirWithPrivileges(File src, String newName, DirRename dirRenameApp) {
		dirRenameApp.start(src, newName);
	}

	// tw 2014nov11 This method does not appear to be used.
	// permissions are platform dependent
	// To obtain permissions, add variable or method to FileVisitorUtil
	// with recursion = false for a single file and true for a tree
	/**
	 * Returns a string uuugggooo, r-xrwx--r
	 * 
	 * @param f
	 * @return
	 */
	/*
	 * public static String getPermissions(File f) { String p = callLs(f);
	 * String ans = p.substring(1, 10); return ans; }
	 */

	// tw 2014nov11 This method does not appear to be used.
	// groups are platform dependent
	// Identifying the principal group is straight forward in posix systems
	// In acl system, iterate through acl entry list, get principal names,
	// then check if name is a group principal, if group principals are
	// supported by the OS
	//
	/**
	 * Returns the group identifier of the file or directory
	 * 
	 * @param f
	 * @return
	 */
	/*
	 * public static String getGroup(File f) { String p = callLs(f); String ps[]
	 * = p.split(" ", 0);
	 * 
	 * return ps[3]; }
	 */

/*	public static String getUser(File f) {

		// 2014oct13 tw
		// This method is not used, so goal is not clear
		// The ls command used previously will return file owner
		// To select user logged onto OS, use system properties in 2 lines below
		Properties sysProps = new Properties();
		return sysProps.getProperty("user.name");

		// 2014oct13 tw
		// If goal is to select file/directory owner, use method below and not
		// one above
		// ACL systems, such as Windows may prepend domain to user, which might
		// affect calling methods
		/*
		 * FileSystem fileSystem = FileSystems.getDefault(); Set<String>
		 * fileSystemViews = fileSystem.supportedFileAttributeViews(); String
		 * user = null; Path fPath = f.toPath();
		 * 
		 * if ( fileSystemViews.contains("posix") ) { PosixFileAttributeView
		 * posixFView = Files.getFileAttributeView(fPath,
		 * PosixFileAttributeView.class); try { user =
		 * posixFView.getOwner().toString(); } catch (IOException e){ } } else
		 * if ( fileSystemViews.contains("acl") ) {
		 * 
		 * AclFileAttributeView aclView = Files.getFileAttributeView(dirPath,
		 * AclFileAttributeView.class); try { user =
		 * aclView.getOwner().toString(); } catch (IOException e){ } }
		 * 
		 * return user;
		 */

		/*
		 * String s = callLs(f); String[] ss = s.split(" "); int cnt = 0; int i
		 * = 0; while (cnt < 2) { cnt += (ss[i].length() > 0) ? 1 : 0; i++; }
		 * 
		 * return ss[cnt];
	}*/

	// tw 2014nov11 This method does not appear to be used.
	// platform independent recursion uses the the file visitor methods as in
	// FileVisitorUtil
	// To obtain a list of files/directories, add variable or method to
	// FileVisitorUtil
	/*
	 * public static ArrayList<File> getRecursive(File src) { ArrayList<File>
	 * ans = new ArrayList<File>(); String s = callLs(src); if (s.charAt(0) !=
	 * 'l') { ans.add(src); if (src.isDirectory()) { for (File f :
	 * src.listFiles()) { ans.addAll(getRecursive(f)); } } }
	 * 
	 * return ans; }
	 */

	public static void openFileBrowser(File dir) {
		// 2014oct8 tw use system default file explorer
		// which does not need a process builder

		// String[] cmd = { "nautilus", dir.getAbsolutePath() };
		// ProcessBuilder pb = new ProcessBuilder(cmd);
		// Process p = null;

		try {
			Desktop.getDesktop().open(dir);
			// p = pb.start();
		} catch (IOException e) {
			throw new FileUtilException(e);
		}
	}

	// tw 2014nov11 This method is platform specific
	// and no longer needed
	/*
	 * protected static String callLs(File f) { String ans = null; String[] cmd
	 * = { "ls", "-l", "-d", f.getAbsolutePath() }; ProcessBuilder pb = new
	 * ProcessBuilder(cmd); BufferedReader br = null;
	 * 
	 * int ret = -1; Process p = null; try { p = pb.start(); br = new
	 * BufferedReader(new InputStreamReader(p.getInputStream())); ret =
	 * p.waitFor();
	 * 
	 * ans = br.readLine(); br.close(); } catch (IOException e) { throw new
	 * FileUtilException(e); } catch (InterruptedException e) {
	 * Thread.currentThread().interrupt(); } finally { if (br != null) { try {
	 * br.close(); } catch (IOException e) {
	 * 
	 * } } }
	 * 
	 * if (p != null) { ProcessUtil.dispose(p); }
	 * 
	 * if (ret != 0) { throw new FileUtilException("Command: " +
	 * Arrays.toString(cmd) + "; returned: " + ret); }
	 * 
	 * return ans; }
	 */

	public static class FileUtilException extends RuntimeException {
		public FileUtilException(String msg) {
			super(msg);
		}

		public FileUtilException(Throwable th) {
			super(th);
		}
	}

}
