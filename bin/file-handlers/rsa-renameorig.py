#!/usr/bin/python2
# -*- coding: utf-8 -*-
# Python 2.7 compatible

"""
script name: rsa-renameorig

This script renames a directory in the original_images folder.
"""

import argparse
import os
import sys

existing_dir = ""

new_name = ""

parent_dir = ""

new_dir = ""

def testDirs():
    global existing_dir
    global new_name
    global parent_dir
    global new_dir

    if not os.path.exists(existing_dir):
        print "FATAL ERROR: directory ",existing_dir," does not exist."
        print
        sys.exit(1)
    if not os.path.isdir(existing_dir):
        print "FATAL ERROR: ",existing_dir," is not a directory."
        print
        sys.exit(1)
    if os.sep in new_name:
        print "FATAL ERROR: new directory name should not be a path, but it contains '",os.sep,"'."
        print
        sys.exit(1)
    if os.path.exists(new_dir):
        print "FATAL ERROR: ",new_dir," already exists."
        print
        sys.exit(1)
    if not os.access(parent_dir, os.W_OK):
        print "FATAL ERROR: insufficient permissions on ",parent_dir,"."
        print
        sys.exit(1)
    return

def parseCmdLine():
    global existing_dir
    global new_name

    parser = argparse.ArgumentParser()
    parser.add_argument("existing_dir", help="path to the directory to rename")
    parser.add_argument("new_name", help="new name for the directory")
    args = parser.parse_args()

    existing_dir = args.existing_dir
    new_name = args.new_name

    return

def main():
    global existing_dir
    global new_name
    global parent_dir
    global new_dir

    parseCmdLine()

    print
    print "=== Renaming directory under original_images directory ==="
    print

    print "Existing directory: ",existing_dir
    print "New name:           ",new_name
    print

    if os.name != "nt":
        os.setreuid(os.geteuid(), -1)

    parent_dir = os.path.dirname(os.path.realpath(existing_dir))

    new_dir = os.path.join(parent_dir, new_name)

    testDirs()

    os.rename(existing_dir, new_dir)

    print "Rename completed."
    print

if __name__=="__main__":
    main()
