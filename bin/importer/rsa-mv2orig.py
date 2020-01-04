#!/usr/bin/python2
# -*- coding: utf-8 -*-
# Python 2.7 compatible

"""
script name: rsa-mv2orig

Originally created by Mikhail Kovtun <mikhail.kovtun@duke.edu> 

Fri May 31 2013  Vladimir Popov <vladimir.popov@duke.edu> 

This script works on bio-busch.biology.duke.edu server and is used 
for the Root Architecture project.

This script creates the directory subtree in the original_images folder, 
which corresponds the names of the files located in some directory 
(usually it is the /data/rsa/to_sort folder).

--------------------------------------------------------
- file name and directory structure
--------------------------------------------------------

The file structure for the names is going to be like this.

for corn:
ZmNAMp00001d03_40.cr2

for rice:
OsRILp00001d12_40.cr2

The first 2 letters indicate the species
Zm = corn
Os = rice
Fk = model
Ta = wheat
Gm = millet
SB = sorghum
Bd = brome (purple false brome)
Ms = alfalfa
Sv = Sviridis
Si = Sitalica


NOTE: Is=insilico data is not moved to original_images by this script

the next 3 letters will be the experiments, ie: NAM or RIL

the next characters will be the plant, ie: p00001;
this is the only field in filename that can have arbitrary length

the next 3 characters will be the date, ie: d03 or d00, etc (or hour like h00 or h48, etc)

the next 3 characters or 4 characters will be the number of images in a set, 
so it will run, for instance, from _01 to _99 or from _001 to _999.
(though it might change if we take fewer or more images)
Notice that every image set is suppossed to have only 
one format (2 or 3 digits, not both in the same image set). This technically
possible, that is data can be moved to original_images folder, but
there might be problems when using this data by application such as
Rootwork, Gia-Roots, etc.

the last 3 or 4 is the file extension, ie .cr2 for RAW images, or .jpg
for jpgs.

--------------------------------------------------------
- directory structure
--------------------------------------------------------

/data/rsa/
|-- [rsa-data]  original_images
|   |-- [rsa-data]  corn
|   |   `-- [rsa-data]  NAM
|   |       `-- [rsa-data]  p00001
|   |           |-- [rsa-data]  d03
|   |           |   `--[rsa-data] cr2
|   |           |-- [rsa-data]  d05
|   |           |-- [rsa-data]  d07
|   |           |-- [rsa-data]  d10
|   |           |-- [rsa-data]  d12
|   |           `-- [rsa-data]  d14
|   `-- [rsa-data]  rice
|       `-- [rsa-data]  RIL
|           `-- [rsa-data]  p00001
|               |-- [rsa-data]  d12
|               |-- [rsa-data]  d14
|               `-- [rsa-data]  d16
.   ...............................
.   ...............................
.   ...............................
|
|
`-- [rsa-data]  to_sort

--------------------------------------------------------
- run
--------------------------------------------------------

This program accept one input parameter: directory where the files to 
be processed are located.

To run type: rarchdirs <dir>
--------------------------------------------------------
- code update
--------------------------------------------------------
added a new specie Fk=model               -- Vladimir Popov --- Feb 14, 2011
added 3 digits support for rotation number -- Vladimir Popov --- May 2, 2011
added a new specie Ta=wheat  -- Vladimir Popov --- Aug 5, 2011
added a new specie Gm=millet  -- Vladimir Popov --- March 22, 2012
added a new specie Sb=sorghum  -- Vladimir Popov --- March 27, 2012
added the following checks:    -- Vladimir Popov --- May 8, 2013
1. check against possible sizes of the images sets;
2. check format consistency in the image set: 
   for rotation number - either ALL files have two digits, 
   or ALL files have three digits
3. check if rotations go one by one 1,2,...,..., up to the end of the image set.
   (for instance, if there are 5 images in the set, 
   then there should be images with 1,2,3,4,5 rotation numbers
   with zero padding taken into account)

"""

import sys
import os
import stat
import shutil
import argparse
if os.name != 'nt':
	import pwd
	import grp


# Constants     #------------------------------------------------{{{

DIR_DEST = '/data/rsa/original_images'
DIR_SRC_DFLT = '/data/rsa/to_sort'
#DIR_DEST = '/home/data/rsa/original_images'
#DIR_SRC_DFLT = '/home/data/rsa/to_sort'

GR_RSA_NAME = 'rootarch'
USR_RSA_NAME = 'rsa-data'

######## TEST TEST TEST #### if not commented ###########
#DIR_DEST = '/localhome/vp23/RootArchitecture/test/data/rsa/original_images'
#DIR_SRC_DFLT = '/localhome/vp23/RootArchitecture/test/data/rsa/to_sort'
#GR_RSA_NAME = 'rootarch'
#USR_RSA_NAME = 'vp23'
########################################################

# End of constants ..............................................}}}

# Global variables     #----------------------------------------{{{

dest_dir = ''
src_dir = ''

rsa_uid = 0     # uid of user 'rsa-data' (needed to set ownership)
rsa_gid = 0     # gid of group 'rootarch' (needed to set ownership)

nr_wrong_owner = 0  # The number of objects in src_dir with wrong ownership

nr_img_groups = 0   # Number of image groups to move
nr_img_files  = 0   # Number of image files to move

moved_img_groups = 0    # Number of image groups actually moved
moved_img_files = 0     # Number of image files actually moved

existing_groups = []    # List of keys of groups already in destination dir

to_move = dict()    # Map from keys (first 14 chars of filename) to tuples
                    # (species, EXP, plant, day, itype, [fn1,...,fnN])

nr_errors = 0       # Number of errors

allow_existing = False  # secret flag to allow append to existing directories

skip_permissions = False  # skip checking user permissions and setting file masks

non_interactive = False  # proceed without prompting for confirmation

delete_originals = False  # delete files in source directory as they are moved

organism_file = None  # tab-separated file that contains the list of valid organism codes and names

moved_imagesets_file = None  # file that will be filled with the metadata for each successfully-moved imageset

allowed_organisms = dict()

# the list of the valid sizes of the images sets
number_img_sets=[1,2,3,4,5,6,8,9,10,12,15,18,20,24,30,36,40,45,60,72,90,120,180,360]

# End of constants ..............................................}}}

def makeRSAdirs(path):  #----------------------------------------{{{
    """Behaves like os.makedirs(path,02770), but also sets group owner
    to 'rootarch'.
    Here, we are sure that some grandparent exists.
    """
    global rsa_uid
    global rsa_gid
    global skip_permissions
    
    (parent,simple) = os.path.split(path)
    if not os.path.exists(path):
        makeRSAdirs(parent)
        os.mkdir(path,02750)        # ??? os.mkdir applies umask to mode
        if not skip_permissions:
            # On XFS filesystem, chown/chgrp might reset 's' bit.
            # So, the chown/chgrp comes first
            os.chown(path,rsa_uid,rsa_gid)
            os.chmod(path,02750)        # This should work...
    return
#................................................................}}}


def moveFiles():    #--------------------------------------------{{{
    """Move files.
    """
    global src_dir
    global dest_dir
    global to_move
    global moved_img_groups
    global moved_img_files

    global nr_errors
    global skip_permissions
    global delete_originals
    global moved_imagesets_file

    if moved_imagesets_file:
        try:
            imagesets_file_handle = open(moved_imagesets_file, "w")
        except:
            print "ERROR: cannot open file " + moved_imagesets_file + " for writing"
            sys.exit(1)

    for key in to_move.keys():
        (species, EXP, plant, day, itype, filelist) = to_move[key]
        print "  Moving image group "+os.path.join(species,EXP,plant,day,itype)
        try:
            group_dir = os.path.join(dest_dir,species,EXP,plant,day,itype)
            makeRSAdirs(group_dir)
            moved_img_groups += 1
            for fn in filelist:
                orig_image_path = os.path.join(src_dir,fn)
                new_image_path = os.path.join(group_dir,fn)
                if delete_originals:
                    shutil.move(orig_image_path,new_image_path)
                else:
                    shutil.copy2(orig_image_path,new_image_path)
                if not skip_permissions:
                    # On XFS filesystem, chown/chgrp might reset 's' bit.
                    # So, the chown/chgrp comes first
                    os.chown(new_image_path,rsa_uid,rsa_gid)                
                    os.chmod(new_image_path,0640) 
                moved_img_files += 1
            if moved_imagesets_file:
                imagesets_file_handle.write(species + "\t" + EXP + "\t" + plant + "\t" + day + "\t" + itype + '\n')
        except:
            print "ERROR: cannot move group "+os.path.join(species,EXP,plant,day,itype)
            (exc_type,exc_value,exc_traceback) = sys.exc_info()
            print "      ",exc_value
            nr_errors += 1

    if moved_imagesets_file:
        imagesets_file_handle.close()
#................................................................}}} 

def checkSrcSets():    #--------------------------------------------{{{
    """check Src Sets.
    """
    global src_dir
    global dest_dir
    global to_move
    global moved_img_groups
    global moved_img_files
    global number_img_sets

    global nr_errors   

    print "INFO: the sizes of the valid images sets are as follows:"
    print "possible sizes : ",number_img_sets
    
    for key in to_move.keys():
        (species, EXP, plant, day, itype, filelist) = to_move[key]
        imageset=os.path.join(species,EXP,plant,day,itype) 
        #print "  checking images in the group "+os.path.join(species,EXP,plant,day,itype)
        
    	try:            
            rots=[]
            rots_nopad=[]
            cnt=-1
            isfrmt=True
            cntf=-1
            for fn in filelist:
                cnt=cnt+1              

            	fn_s=fn.split('_')
                ind=fn_s[1].find('.')
                rot=fn_s[1][0:ind]
                rots.append(rot)

                # check format consistency - 
                # all images should have the same number of digits for rotation
                if isfrmt and cnt > 0 and cnt < len(filelist) and len(rots[cnt-1]) != len(rots[cnt]):
                        cntf=cnt
	        	isfrmt=False
 
                #print "fn="+fn_s[1]
                #print "ind=",ind
                #print "rot="+rot
                rot_nopad=rot.lstrip("0")
                # rotation should be a number - it is already checked in parseDirs()
                rots_nopad.append(int(rot_nopad))
            #
            # end for fn in filelist:
            #
            #print rotations
            rots_nopad.sort()
            #print rots_nopad
            #
            # check whether the rotations presents a valid set
            #
            if not isfrmt:
                FRMTNOTVALID= "all files should have the same number of digits for rotation"   
                SEE="For instance,see files"             
            	print "WARNING:"+" "+"imageset"+"="+imageset+" : "+FRMTNOTVALID
                print SEE+" :",filelist[cntf-1],filelist[cntf]
            if not len(rots) in number_img_sets:
                NUMSNOTVALID= "the number of files is not valid"                
            	print "WARNING:"+" "+"imageset"+"="+imageset+" : "+NUMSNOTVALID+" :",len(rots) 
            #
            # check if all rotations, one by one 1,2,...,len(rots_nopad), 
            # are presented between 1 and len(rots_nopad)
            # (one may have an image set: ***_3.bmp,***_7.bmp. It is a correct set, except for 
            #  it should be ***_1.bmp,***_2.bmp)
            #
            cntr=0
            ispermutations=True
            for rt in rots_nopad: 
            	cntr=cntr+1
                if not rt == cntr:
                	ispermutations=False
				 
            if not ispermutations:
                PRMNOTVALID= "all rotations should go one by one: 1,2,..., ... up to the end of the image set"                 
            	print "WARNING:"+" "+"imageset"+"="+imageset+" : "+PRMNOTVALID
                print filelist
                      
        except:
            print "ERROR: checking images in the group "+imageset
            (exc_type,exc_value,exc_traceback) = sys.exc_info()
            print "      ",exc_value
            nr_errors += 1
#................................................................}}} 

def parseDirs():    #--------------------------------------------{{{
    """Parse source and destination directories.
    """
    global src_dir
    global dest_dir
    global nr_img_files
    global nr_img_groups
    global existing_groups
    global to_move
    global nr_errors
    global allow_existing
    global nr_wrong_owner
    global delete_originals
    global organism_file
    global allowed_organisms
    
    try:
        organism_file_handle = open(organism_file, "r")
    except:
        print "ERROR: cannot open file " + organism_file + " for reading"
        sys.exit(1)

    try:
        for line in organism_file_handle:
            (organism_code, organism_name) = line.strip().split()
            allowed_organisms[organism_code] = organism_name
    except:
        print "ERROR: organisms in " + organism_file + " are not formatted correctly"
        sys.exit(1)
    finally:
        organism_file_handle.close()

    # On this step, the only possible error is inability to read source dir
    # (os.path.exists(..) just returns False if permissions denied)
    try:
        src_list = os.listdir(src_dir)
    except:
        print "ERROR: cannot read directory "+src_dir
        print
        nr_errors += 1
        return
        
    for fn in src_list:
        # We process only files -- directories are ignored
        if os.path.isfile( os.path.join(src_dir,fn) ):
            (valid, key, fn, species, EXP, plant, day, itype) = parseFn(fn)
            if valid:
                if delete_originals:
                    file_mode = os.W_OK
                else:
                    file_mode = os.R_OK
                # Check access to file
                if not os.access(os.path.join(src_dir,fn), file_mode):
                    nr_wrong_owner += 1
                # First, check whether the group already exists in dest
                if key in existing_groups:
                    # we already reported this
                    pass
                else:
                    if key in to_move:
                        # add file to existing group to move
                        to_move[key][5].append(fn)
                        nr_img_files += 1
                    else:
                        # New group: check whether it exists in destination
                        dest_group_dir = os.path.join(dest_dir,species,EXP,plant,day,itype)
                        if os.path.exists(dest_group_dir) and not allow_existing:
                            print "ERROR: image set already exists:",dest_group_dir
                            existing_groups.append(key)
                        else:
                            to_move[key] = (species, EXP, plant, day, itype, [ fn ])
                            nr_img_groups += 1
                            nr_img_files += 1
            else:
                print "WARNING: filename syntax violated:",fn
#................................................................}}} 

def parseFn(fn):  #----------------------------------------------{{{
    """Parse the file name.
    Parts of the filename are detected as follows:
        species:    the first 2 characters of the filename; explicitly
                    tested for possible values in this function
        experiment: the next 3 characters of the filename (must be uppercase)
        plant:      the characters starting 6th character and ending at the
                    last letter 'd' in the filename ('d' is not included)
        day:        3 characters starting from the last letter ('d' or 'h')
        rotation#:  2 or 3 characters starting after the last '_'
        type:       filename extension
    This function returns tuple (bool, key, fn, species, EXP, plant, day)
    where bool is True if filename is valid,
          key is unique group id (composed of the first 14 chars of fn)
          fn is original filename,
          species, EXP, plant, day are elements of path to be created.
    If bool is False, other elements of tuple are junk.
    """
    
    err_result = (False,'',fn,'','','','','')   # this will be returned if error
    
    # Find delimiting points in the filename
    typ_idx = fn.rfind('.')
    if typ_idx < 0: return err_result
    rot_idx = fn.rfind('_',0,typ_idx)
    if rot_idx < 0: return err_result
    day_idx = -1
    for i in range(rot_idx,0,-1):
        if fn[i].isalpha():
            day_idx = i
            break
    if day_idx < 0: return err_result
    pla_idx = 5
    exp_idx = 2
    spe_idx = 0
    
    # Extract filename parts (they will be tested later)
    species_cd  = fn[spe_idx  :exp_idx]
    EXP         = fn[exp_idx  :pla_idx]
    plant       = fn[pla_idx  :day_idx]
    day         = fn[day_idx  :rot_idx]   # including starting letter ('d' or 'h')
    rotation    = fn[rot_idx+1:typ_idx]   # excluding starting '_'
    itype       = fn[typ_idx+1:       ]   # excluding starting '.'

    # Check species
    # (here is the only place where we have to translate)
    if species_cd in allowed_organisms:
        species = allowed_organisms[species_cd]
    else:
        return err_result
        
    # Check experiment type -- should consist of 3 uppercase letters
    if not ( EXP.isalpha() and EXP.isupper() ):  return err_result
        
    # Check plant -- 
    # 1) remove leading and trailing dashes
    # 2) after this operation, plant should not be empty string
    plant = plant.strip('-')
    if len(plant)==0:  return err_result
    
    # Check day(or hour)-- should be alpha symbol (already checked above) followed by 2 digits
#    if (not len(day[0:])==3 and not len(day[0:]==4) or not day[1:].isdigit():  return err_result
    if ((not len(day[0:])==3 and not len(day[0:])==4) or not day[1:].isdigit()):  return err_result
    # Check image number -- should be '_' followed by 2 or 3 digits
    #print "rotation=",rotation
    if ((not len(rotation)==2 and not len(rotation)==3) or not rotation.isdigit()):  return err_result
        
    # Check image type (filename extension): should be non-empty
    if not ( len(itype)>0 ):  return err_result
    
    # Create key
    key = species_cd + EXP + plant + day + itype
        
    return (True, key, fn, species, EXP, plant, day, itype)
#................................................................}}}



def parseCmdLine(): #--------------------------------------------{{{
    """Parse command line.
    Sets global variable 'src_dir' and 'dest_dir'.
    """
    global src_dir
    global dest_dir
    global organism_file
    global allow_existing
    global skip_permissions
    global non_interactive
    global delete_originals
    global moved_imagesets_file
    
    parser = argparse.ArgumentParser()
    parser.add_argument("organism_file", help="tab-separated file that contains the list of valid organism codes and names")
    parser.add_argument("source_dir", nargs="?", default=DIR_SRC_DFLT, help="default: " + DIR_SRC_DFLT)
    parser.add_argument("destination_dir", nargs="?", default=DIR_DEST, help="default: " + DIR_DEST)
    parser.add_argument("--allow-existing", help="allow append to existing directories", action="store_true")
    parser.add_argument("--skip-permissions", help="skip checking user permissions and setting file masks", action="store_true")
    parser.add_argument("--non-interactive", help="proceed without prompting for confirmation", action="store_true")
    parser.add_argument("--delete-originals", help="delete files in source directory as they are moved", action="store_true")
    parser.add_argument("--moved-imagesets-file", help="file that will be filled with the metadata for each successfully-moved imageset")
    args = parser.parse_args()

    src_dir = args.source_dir
    dest_dir = args.destination_dir
    organism_file = args.organism_file
    allow_existing = args.allow_existing
    skip_permissions = args.skip_permissions
    non_interactive = args.non_interactive
    delete_originals = args.delete_originals
    moved_imagesets_file = args.moved_imagesets_file

    return
#................................................................}}}


def testDirs():     #--------------------------------------------{{{
    """Test whether source and destination directories exist
    and are directories.
    Names of directories are taken from variables 'src_dir' and 'dest_dir'.
    """
    global dest_dir
    global src_dir
    
    if not os.path.exists(dest_dir):
        print 'FATAL ERROR: directory',dest_dir,'does not exist.'
        print
        sys.exit(1)
    if not os.path.isdir(dest_dir):
        print 'FATAL ERROR:',dest_dir,'is not a directory.'
        print
        sys.exit(1)
    if not os.path.exists(src_dir):
        print 'FATAL ERROR: directory',src_dir,'does not exist.'
        print
        sys.exit(1)
    if not os.path.isdir(src_dir):
        print 'FATAL ERROR:',src_dir,'is not a directory.'
        print
        sys.exit(1)
    return
#................................................................}}}
    
def testUser():    #--------------------------------------------{{{
    """Test whether the current user is 'rsa-data'.
    Additionaly, it test that user 'rsa-data' is a member of 'rootarch'
    (which always should be true).
    It sets global variables 'rsa_uid' and 'rsa_gid'.
    """
    global rsa_uid
    global rsa_gid
    global skip_permissions

    if skip_permissions:
        return

    curr_username = pwd.getpwuid(os.geteuid()).pw_name
    
    try:
        rootarch_info = grp.getgrnam(GR_RSA_NAME)
    except:
        print "FATAL ERROR: there is no '%s' group." % GR_RSA_NAME
        print
        sys.exit(1)
    rsa_gid = rootarch_info[2]
    
    try:
        rsa_data_info = pwd.getpwnam(USR_RSA_NAME)
    except:
        print "FATAL ERROR: there is no '%s' user." % USR_RSA_NAME
        print
        sys.exit(1)
    rsa_uid = rsa_data_info[2]
    
    if not USR_RSA_NAME in rootarch_info[3]:
        print "FATAL ERROR: user '%s' is not a member of '%s'." % (USR_RSA_NAME,GR_RSA_NAME)
        print
        sys.exit(1)
    
    if curr_username!=USR_RSA_NAME and curr_username!='root':
        print "ERROR: this script can be executed by user '%s' only." % USR_RSA_NAME
        print "       (current user is '%s')." % curr_username
        print
        sys.exit(1)
    return
#................................................................}}}
    
def main():     #------------------------------------------------{{{
    """Main procedure.
    """
    global src_dir
    global dest_dir
    global nr_img_files
    global nr_img_groups
    global existing_groups
    global to_move
    global moved_img_groups
    global moved_img_files
    global nr_errors
    global nr_wrong_owner
    global skip_permissions

    parseCmdLine()  # this sets 'src_dir' and 'dest_dir'

    print
    print "=== Moving original images to original_images directory ==="
    print

    print "Source directory:     ",src_dir
    print "Destination directory:",dest_dir
    print

    if os.name == 'nt':
        skip_permissions = True
    else:
        os.setreuid(os.geteuid(), -1)
    
    testUser()      # this sets 'rsa_uid' and 'rsa_gid'
    
    testDirs()      # this exits if error
     
    print "Parsing directories..."
    parseDirs()
    
    print "Checking imaging sets..."
    checkSrcSets()
    
    if nr_wrong_owner > 0 and os.getenv('USER')!='root':
        print
        print "ERROR: The source directory contains %d files that cannot be accessed." % nr_wrong_owner
        print "Either correct this problem, or run this script with root privileges."
        print
        sys.exit(0)
    
    print
    if nr_img_groups==0:
        print "There is nothing to move."
    else:
        print str(nr_img_groups)+" image groups ("+str(nr_img_files)+" files) will be moved."
        print
        
        if non_interactive:
            answer = "yes"
        else:
            answer = raw_input('Continue (yes/no)? ')

        if not (answer.lower()=='y' or answer.lower()=='yes'):
            print 'Operation aborted.'
        else:
            moveFiles()
            print 'Moving completed.'
            print str(moved_img_groups)+" image groups ("+str(moved_img_files)+" files) were moved."
    
    if nr_errors>0:
        print
        print 'HOWEVER:',nr_errors,'errors were detected.'
        print "Please run:"
        print "  rsa-chkrights-orig"
        print "  rsa-chkrights-orig "+src_dir
        print 'to find problems, fix them, and then re-run this script again.'

    print
    
    return
#................................................................}}}    
    
#================================================================{{{
# Start-up code
if __name__=="__main__":
    main()
#................................................................}}}

