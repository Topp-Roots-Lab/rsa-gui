#!/usr/bin/python -W ignore::DeprecationWarning

# Paul Zurek, December 9, 2011
#
# Creates images for quality control
# (This funcionality is currently integrated with the Gia GUI)
#
# Note that this script depends on the resource:
# FONT = "/usr/local/bin/gia-programs/quality-control/courbd.ttf"
#
# the following options do not work on bio-busch
# for the time being as of December 2011 (will be fixed)
# The reason - the lack of some Python libraries
#
# 000010000 - make a thresholded on cropped overlay image
# 000001000 - make a thresholded on gray overlay image
# 000000100 - make a skeleton on cropped overlay image
# 000000010 - make a skeleton on gray overlay image
# 000000001 - make a skeleton on thresholded overlay image
##################################################################
# Changed by vp23, Feb 29, 2012
# 1. Added the fifth parameter for the thresholded template name.
# 2. Changed the file name for the result adding the scale and the template name.
##################################################################

#import os, sys, csv, glob, Image, ImageFont, ImageDraw, re

import os
import sys
import csv
import glob
from PIL import Image
from PIL import ImageFont
from PIL import ImageDraw
import re

#----------------------------------------------
folder_in = sys.argv[1]   #folder with images
scale = sys.argv[2]       #scale, default should be 4
folder_out = sys.argv[3]  #folder where to save the generated images
proc_code = sys.argv[4]   #which images to generate in the form of a binary number
template_name = sys.argv[5] #which template was used to generate the giaroot_2d

# 000000000
# 100000000 - make a cropped image composite
# 010000000 - make a gray image composite
# 001000000 - make a thresholded image composite
# 000100000 - make a skeleton image composite
# 000010000 - make a thresholded on cropped overlay image
# 000001000 - make a thresholded on gray overlay image
# 000000100 - make a skeleton on cropped overlay image
# 000000010 - make a skeleton on gray overlay image
# 000000001 - make a skeleton on thresholded overlay image
# 111111111 - do everything
# 001100000 - make a thresholded and a skeleton image composites

#example ./all_qc_folder.py /data/rsa/processed_images/corn/NAM/p00039/d06/saved/giaroot_2d/prz_2011-05-10_16-56-21 4 ./ 101011000 [template_name]

# path to the resource
FONT = "/home/twalk/courbd.ttf"

status_code = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

packet_c = glob.glob(sys.argv[1] + "/*croppedimage*tiff")
packet_g = glob.glob(sys.argv[1] + "/*grayimage*tiff")
packet_t = glob.glob(sys.argv[1] + "/*thresholded*tiff")
packet_s = glob.glob(sys.argv[1] + "/*thinnedimage*tiff")

print "threshold image: " + packet_t[0]

image = open(packet_t[0], "r", 0)
img = Image.open(image)
#img.show()
#img = Image.open(packet_t[0])
plant_id = re.split("_", os.path.basename(packet_t[0]))[0]
run_id = os.path.split(os.path.normpath(sys.argv[1]))[-1]

w_sng, h_sng = img.size
print "image width: " + str(w_sng) + " height: " + str(h_sng)

w_sng = w_sng/int(scale)
h_sng = h_sng/int(scale)

imgNum = len(packet_t)
imgWide = 8
imgHigh = imgNum/imgWide
imgHigh = int(imgHigh + 1)

accum_c = Image.new("RGB",(w_sng*imgWide, h_sng*imgHigh))
accum_g = Image.new("RGB",(w_sng*imgWide, h_sng*imgHigh))
accum_t = Image.new("RGB",(w_sng*imgWide, h_sng*imgHigh))
accum_s = Image.new("RGB",(w_sng*imgWide, h_sng*imgHigh))
#accum_c = Image.new("RGB",(w_sng*8, h_sng*5))
#accum_g = Image.new("RGB",(w_sng*8, h_sng*5))
#accum_t = Image.new("RGB",(w_sng*8, h_sng*5))
#accum_s = Image.new("RGB",(w_sng*8, h_sng*5))
#----------------------------------------------

def make_coords(w, h, xl ,yl):
    list = []
    ct = 0
    for y in range(0,yl):
        for x in range(0,xl):
            ct += 1
            list.append((int(x*w), int((x*w)+w), int(y*h), int((y*h)+h)))
    return list

def add_watermark(img, ctid):
    #font = ImageFont.truetype("./courbd.ttf",25)
    # path
    path = FONT
    font = ImageFont.truetype(path,25)

    draw = ImageDraw.Draw(img)
    ctid = str(ctid)
    draw.text((5, 5),ctid,(0,191,255),font=font)
    return img

def add_to_canvas(w, h, item, indx, accum, ctid):
    img = Image.open(item[indx]).convert("RGB")
    img = img.resize((w,h))
    coor = list(item[0])
    img_num = int(re.split("_", os.path.basename(item[1]))[1])
    img = add_watermark(img, img_num)
    accum.paste(img,(coor[0], coor[2]))
    return accum

def sort_packet(packet):
    tmp = {}
    for i in packet:
        tmp[int(re.split("_", os.path.basename(i))[1])] = i
    packet = []
    for x in sorted(tmp.keys()):
        packet.append(tmp[x])
    return packet

def make_overlay(pix_1, pix_2, width, height):
    #print "pix_1"
    #print pix_1
    #print "pix_2"
    #print pix_2
    blank = Image.new("RGB",(width, height))
    total = blank.load()
    ct = 0
    for x in range(0,width):
        for y in range(0,height):
            if pix_1[x, y] == (0, 0, 0):
                R_bw, G_bw, B_bw = (0, 0, 0)
            else:
                R_bw, G_bw, B_bw = (0, 0, 70)
            R_gr, G_gr, B_gr = pix_2[x,y]
            total[x,y] = (((R_gr+R_bw)), ((G_gr+G_bw)), ((B_gr+B_bw)))
            ct += 1
    return blank

def make_overlay2(pix_1, pix_2, width, height):
    blank = Image.new("RGB",(width, height))
    total = blank.load()
    ct = 0
    for x in range(0,width):
        for y in range(0,height):
            if pix_1[x, y] == (255, 255, 255):
                total[x,y] = (255, 0, 0)
            else:
                total[x,y] = pix_2[x,y]
            ct += 1
    return blank

def make_mosaic(w_sng, h_sng, accum, copack, id):
    ctid = 0
    for item in sorted(copack):
        ctid += 1
        accum = add_to_canvas(w_sng, h_sng, item, id, accum, ctid)
    return accum

def save_me(accum, folder_out, plant_id, run_id, image_type, scale, template_name):
    file_name = plant_id + "_" + run_id + "_template_" + template_name + "_scale_"+scale+ "_"+image_type + ".png"
    print "Saving image: " + file_name
    accum.save(os.path.abspath(os.path.join(folder_out, file_name)))

packet_c = sort_packet(packet_c)
packet_g = sort_packet(packet_g)
packet_t = sort_packet(packet_t)
packet_s = sort_packet(packet_s)

#coords = make_coords(w_sng, h_sng, 8, 5)
coords = make_coords(w_sng, h_sng, imgWide, imgHigh)
copack = zip(coords, packet_c, packet_g, packet_t, packet_s)

# vp commented
#do_which = dict(enumerate(list(sys.argv[4]), 1))
#
# vp changed
do_which = dict(enumerate(list(sys.argv[4])))

print do_which

#cropped only
if do_which[0] == '1':
    accum_c = make_mosaic(w_sng, h_sng, accum_c, copack, 1)
    status_code[0] = 1
    save_me(accum_c, folder_out, plant_id, run_id, "crop_composite", scale, template_name)
    #accum_c.show()

#greyscale only
if do_which[1] == '1':
    accum_g = make_mosaic(w_sng, h_sng, accum_g, copack, 2)
    status_code[1] = 1
    save_me(accum_g, folder_out, plant_id, run_id, "gray_composite", scale, template_name)
    #accum_g.show()

#thresholded only
if do_which[2] == '1':
    accum_t = make_mosaic(w_sng, h_sng, accum_t, copack, 3)
    status_code[2] = 1
    save_me(accum_t, folder_out, plant_id, run_id, "thresholded_composite", scale, template_name)
    #accum_t.show()

#skeleton only
if do_which[3] == '1':
    accum_s = make_mosaic(w_sng, h_sng, accum_s, copack, 4)
    status_code[3] = 1
    save_me(accum_s, folder_out, plant_id, run_id, "skeleton_composite", scale, template_name)
    #accum_s.show()

#thresholded on cropped
if do_which[4] == '1':
    if status_code[2] == 0:
        accum_t = make_mosaic(w_sng, h_sng, accum_t, copack, 3)
        status_code[2] = 1
    if status_code[0] == 0:
        accum_c = make_mosaic(w_sng, h_sng, accum_c, copack, 1)
        status_code[0] = 1
    w, h = accum_t.size

    #print "accum_t"
    #print accum_t
    #print "accum_c"
    #print accum_c
    #print "accum_t.load()"
    #print accum_t.load()
    #print "accum_c.load()"
    #print accum_c.load()


    overlay_composite = make_overlay(accum_t.load(), accum_c.load(), w, h)
    save_me(overlay_composite, folder_out, plant_id, run_id, "thresholded_on_cropped", scale, template_name)
    #overlay_composite.show()

#thresholded on gray
if do_which[5] == '1':
    if status_code[2] == 0:
        accum_t = make_mosaic(w_sng, h_sng, accum_t, copack, 3)
        status_code[2] = 1
    if status_code[1] == 0:
        accum_g = make_mosaic(w_sng, h_sng, accum_g, copack, 2)
        status_code[1] = 1
    w, h = accum_t.size
    overlay_composite = make_overlay(accum_t.load(), accum_g.load(), w, h)
    save_me(overlay_composite, folder_out, plant_id, run_id, "thresholded_on_gray", scale, template_name)
    #overlay_composite.show()

#skeleton on cropped
if do_which[6] == '1':
    if status_code[3] == 0:
        accum_s = make_mosaic(w_sng, h_sng, accum_s, copack, 4)
        status_code[3] = 1
    if status_code[0] == 0:
        accum_c = make_mosaic(w_sng, h_sng, accum_c, copack, 1)
        status_code[0] = 1
    w, h = accum_t.size
    overlay_composite = make_overlay(accum_s.load(), accum_c.load(), w, h)
    save_me(overlay_composite, folder_out, plant_id, run_id, "skeleton_on_cropped", scale, template_name)
    #overlay_composite.show()

#skeleton on gray
if do_which[7] == '1':
    if status_code[3] == 0:
        accum_s = make_mosaic(w_sng, h_sng, accum_s, copack, 4)
        status_code[3] = 1
    if status_code[1] == 0:
        accum_g = make_mosaic(w_sng, h_sng, accum_g, copack, 2)
        status_code[1] = 1
    w, h = accum_t.size
    overlay_composite = make_overlay(accum_s.load(), accum_g.load(), w, h)
    save_me(overlay_composite, folder_out, plant_id, run_id, "skeleton_on_gray", scale, template_name)
    #overlay_composite.show()

#skeleton on thresholded
if do_which[8] == '1':
    if status_code[3] == 0:
        accum_s = make_mosaic(w_sng, h_sng, accum_s, copack, 4)
        status_code[3] = 1
    if status_code[2] == 0:
        accum_t = make_mosaic(w_sng, h_sng, accum_t, copack, 3)
        status_code[2] = 1
    w, h = accum_t.size
    overlay_composite = make_overlay2(accum_s.load(), accum_t.load(), w, h)
    save_me(overlay_composite, folder_out, plant_id, run_id, "skeleton_on_thresholded", scale, template_name)
    #overlay_composite.show()


