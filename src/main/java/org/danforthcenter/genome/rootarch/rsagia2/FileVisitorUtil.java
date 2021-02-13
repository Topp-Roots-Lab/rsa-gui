package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.IOException;
import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * Created by tom on 10/29/2014.
 *
 * Added to incorporate newer Java NIO functionality that will allow running
 * RSA-Gia on multiple platforms
 */
public class FileVisitorUtil {

	private Path location = null;
	private boolean recurse = false;
	private String fileSyst = null;
	private String group = null;
	private String action = null;

	public FileVisitorUtil(Path loc, String fileSys, String grp,
			boolean recursion, String act) {
		this.location = loc;
        this.fileSyst = fileSys;
        this.group = grp;
        this.recurse = recursion;
        this.action = act;
	}

	public void doFileVisits() {

		RunFileVisitor runFileVisit = new RunFileVisitor(fileSyst, group,
				action);
		try {
			if (recurse == true) {
				Files.walkFileTree(location,
						EnumSet.noneOf(FileVisitOption.class),
						Integer.MAX_VALUE, runFileVisit);
			} else {
				Files.walkFileTree(location,
						EnumSet.noneOf(FileVisitOption.class), 0, runFileVisit);
			}
		} catch (IOException ioe) {
 			throw new FileUtilSecurityException(ioe);
		}
	}

	private class RunFileVisitor extends SimpleFileVisitor<Path> {

		private String fileSys = null;
		private String gr = null;
		private String action = null;

		public RunFileVisitor(String fileSyst, String grp, String act) {
			fileSys = fileSyst;
			gr = grp;
			action = act;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) throws IOException {
/*			if (action.equalsIgnoreCase("secure")) {
				if (attrs.isDirectory()) {
					if (fileSys.equalsIgnoreCase("posix")) {
						setPosixPermissions(dir, attrs);
					} else if (fileSys.equalsIgnoreCase("acl")) {
						setAclPermissions(dir, attrs);
					} else {
						// do nothing, don't change existing permissions
					}
				}
			}
*/
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file,
				BasicFileAttributes attributes) throws IOException {

			// check file system and change permissions appropriately
			// need to add mac to make this work on mac computers
            if (action.equalsIgnoreCase("secure")) {
                if (fileSys.equalsIgnoreCase("posix")) {
                    //setPosixPermissions(file, attributes);
                    setPosixPermissions(file);
                } else if (fileSys.equalsIgnoreCase("acl")) {
                    //setAclPermissions(file, attributes);
                    // tw 2015jan13 try inheriting acl permissions from parent directory
//                    setAclPermissions(file);
                } else {
                    // do nothing, don't change existing permissions
                }
            } else if (action.equalsIgnoreCase("delete")) {
                if (attributes.isRegularFile() || attributes.isSymbolicLink()) {
                    Files.delete(file);
                }

            }

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path directory,
				IOException ioe) throws IOException {

            if (action.equalsIgnoreCase("secure")) {
                if (fileSys.equalsIgnoreCase("posix")) {
                    //setPosixPermissions(file, attributes);
                    setPosixPermissions(directory);
                } else if (fileSys.equalsIgnoreCase("acl")) {
                    //setAclPermissions(file, attributes);
					// tw 2015jan13 try inheriting acl permissions from parent directory
//                    setAclPermissions(file);
                } else {
                    // do nothing, don't change existing permissions
                }
            } else if (action.equalsIgnoreCase("delete")) {
				Files.delete(directory);
			}

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException ioe) throws IOException {
			System.out.println(this.getClass() + " Something went wrong while working on : " + file.getFileName());
			ioe.printStackTrace();
			return FileVisitResult.CONTINUE;
		}

		public void setPosixPermissions(Path f)
//        public void setPosixPermissions(Path f, BasicFileAttributes attribs)
		throws IOException {


            if (!Files.isSymbolicLink(f)) {

                File fp = f.toFile();

                PosixFileAttributeView posixFileView = Files.getFileAttributeView(
                        f, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw----");

                if (fp.isDirectory()) {
                    //if (attribs.isDirectory()) {
                    perms = PosixFilePermissions.fromString("rwxrwx---");
                }

                posixFileView.setPermissions(perms);

                GroupPrincipal group = f.getFileSystem()
                        .getUserPrincipalLookupService()
                        .lookupPrincipalByGroupName(gr);
                posixFileView.setGroup(group);
            }
        }

		//public void setAclPermissions(Path f, BasicFileAttributes attribs)
        public void setAclPermissions(Path f)
				throws IOException {

            File fp = f.toFile();

			ArrayList<String> aclEntryUsers = new ArrayList<String>();

			Properties sysProps = new Properties();
			sysProps = System.getProperties();

			String user = sysProps.getProperty("user.name");

			aclEntryUsers.add(user);
			aclEntryUsers.add(gr);

//			clearAclPerms(user, f);

			for (String aclEntryUser : aclEntryUsers) {
                if (!fp.isDirectory()) {
                //if (!attribs.isDirectory()) {
//                    System.out.println("setAclPermissions " + f + "    file");
                    setFileAclPerms(aclEntryUser, f);
                }
                else {
//                    System.out.println("setAclPermissions " + f + "    directory");

                    setDirAclPerms(aclEntryUser, f);
                }
			}
		}

		// clear all permissions except for system and administrators
		// add aclUser to system and administrator if setAclPerms fails
		public void clearAclPerms(String aclUser, Path p) throws IOException {
			List<Integer> aclEntryIndex = new ArrayList<Integer>();
			AclFileAttributeView aclView = Files.getFileAttributeView(p,
					AclFileAttributeView.class);
			List<AclEntry> aclEntryList = aclView.getAcl();
			for (AclEntry entry : aclEntryList) {
				// // tw 2014nov11 Windows 8 prepends domain to user
				// // switch to contains if equals does not work
				// // if (entry.principal().getName().equalsIgnoreCase(aclUser))
				// {
				// if ( entry.principal().getName().contains(aclUser) ) {
				// aclEntryIndex.add(aclEntryList.indexOf(entry));
				// }
				if (!entry.principal().getName().contains("SYSTEM")
						&& !entry.principal().getName().contains("system")
						&& !entry.principal().getName()
								.contains("Administrators")
						&& !entry.principal().getName()
								.contains("administrator")) {
					 if (entry.principal().getName().contains(aclUser)) {
                         aclEntryIndex.add(aclEntryList.indexOf(entry));
                     }
				}
			}
			Collections.sort(aclEntryIndex, Collections.reverseOrder());
			if (!aclEntryIndex.isEmpty()) {
				for (int index : aclEntryIndex) {
					aclEntryList.remove(index);
				}
			}

			aclView.setAcl(aclEntryList);

		}

		public void setFileAclPerms(String aclUser, Path p) throws IOException {

			// // Windows 8 returns group from users or group
			// // Some platforms may not support separate groups
			// // Try running with RSA-gia group as part of users
			UserPrincipal userPrinc = p.getFileSystem()
					.getUserPrincipalLookupService()
					.lookupPrincipalByName(aclUser);
			// // separate group if supported
			// GroupPrincipal groupPrinc =
			// f.getFileSystem().getUserPrincipalLookupService()
			// .lookupPrincipalByGroupName(gr);

			// // tw 2014nov12 file or directory specific permission are not
			// clear for acl systems
			// // to have separate permissions for files and directories, check
			// if path is directory
			// // and do appropriate acl permissions
			AclFileAttributeView aclView = Files.getFileAttributeView(p,
					AclFileAttributeView.class);
			List<AclEntry> aclEntryList = aclView.getAcl();
			AclEntry entry = AclEntry
					.newBuilder()
					.setType(AclEntryType.ALLOW)
					.setPrincipal(userPrinc)
					.setPermissions(AclEntryPermission.READ_DATA,
							AclEntryPermission.WRITE_DATA,
							AclEntryPermission.APPEND_DATA,
							AclEntryPermission.READ_ATTRIBUTES,
							AclEntryPermission.READ_NAMED_ATTRS,
							AclEntryPermission.WRITE_ATTRIBUTES,
							AclEntryPermission.WRITE_NAMED_ATTRS,
							AclEntryPermission.READ_ACL,
							AclEntryPermission.WRITE_ACL,
							AclEntryPermission.WRITE_OWNER,
							AclEntryPermission.DELETE,
							AclEntryPermission.DELETE_CHILD,
							AclEntryPermission.EXECUTE,
							AclEntryPermission.SYNCHRONIZE).build();
			aclEntryList.add(0, entry);
			aclView.setAcl(aclEntryList);

		}

	}


    public void setDirAclPerms(String aclUser, Path p) throws IOException {

        // // Windows 8 returns group from users or group
        // // Some platforms may not support separate groups
        // // Try running with RSA-gia group as part of users
        UserPrincipal userPrinc = p.getFileSystem()
                .getUserPrincipalLookupService()
                .lookupPrincipalByName(aclUser);
        // // separate group if supported
        // GroupPrincipal groupPrinc =
        // f.getFileSystem().getUserPrincipalLookupService()
        // .lookupPrincipalByGroupName(gr);

        // // tw 2014nov12 file or directory specific permission are not
        // clear for acl systems
        // // to have separate permissions for files and directories, check
        // if path is directory
        // // and do appropriate acl permissions
        AclFileAttributeView aclView = Files.getFileAttributeView(p,
                AclFileAttributeView.class);
        List<AclEntry> aclEntryList = aclView.getAcl();
        AclEntry entry = AclEntry
                .newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(userPrinc)
                .setPermissions(AclEntryPermission.READ_DATA,
                        AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.APPEND_DATA,
                        AclEntryPermission.ADD_FILE,
                        AclEntryPermission.ADD_SUBDIRECTORY,
                        AclEntryPermission.LIST_DIRECTORY,
                        AclEntryPermission.READ_ATTRIBUTES,
                        AclEntryPermission.READ_NAMED_ATTRS,
                        AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.WRITE_NAMED_ATTRS,
                        AclEntryPermission.READ_ACL,
                        AclEntryPermission.WRITE_ACL,
                        AclEntryPermission.WRITE_OWNER,
                        AclEntryPermission.DELETE,
                        AclEntryPermission.DELETE_CHILD,
                        AclEntryPermission.EXECUTE,
                        AclEntryPermission.SYNCHRONIZE).build();
        aclEntryList.add(0, entry);
        aclView.setAcl(aclEntryList);

    }

	public static class FileUtilSecurityException extends RuntimeException {
		public FileUtilSecurityException(String msg) {
			super(msg);
		}

		public FileUtilSecurityException(Throwable th) {
			super(th);
		}
	}

}
