#!/usr/bin/python
"""
###################################################################
# Paul Zurek, May 15, 2013
###################################################################
# 
#  Creates images for Quality Control for 3D models (class QC3D)
# (This funcionality is currently integrated with the Gia GUI)
#
#  NOTE: this code currently works only on bio-ross (does not work on bio-busch)
#        The reason - the lack of some Python libraries on bio-busch 
#        (it means that the Quality Control for 3D models is not available on bio-busch)
#
#
#  How to run: [path_to_QC3D.py]/QC3D.py [src] [dest]
#
# where
#          src - input voxel file full path 
#          dest - output mosaic (three angles and top images) file full path 
#
##################################################################
#
# Modified by Vladimir Popov, May 20, 2013
# 1. Added the Local Methods and Main modules to support mosaic output and labeling
#
##################################################################
"""


import os, sys, re, csv, Image, traceback, time
import ImageFont,ImageDraw
import numpy as np

################################
## class QC3D
###############################
class QC3D:
    """class for making flat pictures of 3D model using the .out files from ying's rootwork"""
    def __init__(self, file, dest, angles):
        #
        # file - input voxel file full path 
        # dest - output mosaic (three angles and top images) file full path 
        #
        self.SCALE = 1   #how much to scale the images by        
        self.INCREMENT = 25  #bigger = brighter picture in the end, but you will loose the "shading" effect
        self.ANGLES = angles #which angles to show
        self.data = open(os.path.abspath(file), 'rb')
        self.array_size, self.xpad, self.ypad, self.zpad = self.find_dimensions()
        self.DATA_ARRAY = self.load_vox_to_array()          #data in a numpy array format
        self.TOP_VIEW = self.top_view()                     #PIL image, view from the top
        self.SIDE_VIEWS = self.generate_mosaic()            #a list of PIL images, each one from a different angle

    def find_dimensions(self):
        """find the extend of the model, biggest x, y, z coordinate"""
        self.data.seek(0)  #go back to beginning of file, just in case
        tmp = csv.reader(self.data, delimiter = ' ')
        tmp.next()
        tmp.next()
        xlist = []
        ylist = []
        zlist = []
        for i in tmp:
            xlist.append(int(i[0]))
            ylist.append(int(i[1]))
            zlist.append(int(i[2]))
        return (max(xlist)+1, max(ylist)+1, max(zlist)+1), 0, 0, 0

    def load_vox_to_array(self):
        """Loads the data into an numpy array"""
        tmp_array = np.zeros(self.array_size, np.uint8)
        self.data.seek(0) #go back to beginning, just in case
        tmp = csv.reader(self.data, delimiter = ' ')
        tmp.next()
        tmp.next()
        for i in tmp:
            tmp_array[int(i[0]) - self.xpad, int(i[1]) - self.ypad, int(i[2]) - self.zpad] = self.INCREMENT
        tmp_array = np.swapaxes(tmp_array, 1, 2)   #this and next line flip it so top is top
        tmp_array = np.fliplr(tmp_array)
        return tmp_array

    def rotate_array(self, rot_angle):
        """ somehow... it works
        -1 = return array to starting position
        0 = don't do any rotation
        all other numbers, rotate that many degrees"""
        if rot_angle == -1:
            self.DATA_ARRAY = self.load_vox_to_array()
        elif rot_angle == 0:
            pass
        else:
            split_array = np.hsplit(self.DATA_ARRAY, self.DATA_ARRAY.shape[1])
            arrays = []
            for i in split_array:
                i = np.squeeze(i)
                img = Image.fromarray(i).rotate(rot_angle)
                #img = Image.fromarray(i).rotate(rot_angle, expand = True)
                img_ar = np.asarray(img)
                arrays.append(img_ar)
            self.DATA_ARRAY = np.dstack(arrays)
            self.DATA_ARRAY = np.swapaxes(self.DATA_ARRAY, 1, 2)
            del(arrays)

    def top_view(self):
        """ gives you a top stacked view of the model """
        self.data.seek(0)
        data = csv.reader(self.data, delimiter = ' ')
        data.next()
        data.next()
        img_tmp = Image.new('1', (self.array_size[0], self.array_size[1]), 0)
        img_pix = img_tmp.load()
        for i in data:
            img_pix[int(i[0]), int(i[1])] = img_pix[int(i[0]), int(i[1])] + self.INCREMENT

        #find the top-left corner that has a non-zero value for a pixel and use that to crop the image
        xmax = 99999
        ymax = 99999
        for x in range(0, img_tmp.size[0]-1):
            for y in range(0, img_tmp.size[1]-1):
                val = img_pix[x,y]
                if val != 0:
                    if x <= xmax:
                        xmax = x
                    if y <= ymax:
                        ymax = y  
        return img_tmp.crop((xmax, ymax, img_tmp.size[0]-1, img_tmp.size[1]-1))
    
    def flatten_array(self):
        """ turns the 3d array into a 2d array which can be viewed using .show_bw() """
        tmp_array = np.zeros((self.DATA_ARRAY.shape[1], self.DATA_ARRAY.shape[2]), np.uint8)
        for i in range(0, self.DATA_ARRAY.shape[0]):
            tmp_array = self.DATA_ARRAY[i] + tmp_array
        self.FLAT_ARRAY = tmp_array

    def show_bw(self):
        """ shows the flattened image"""
        try:
            img = Image.fromarray(self.FLAT_ARRAY)
            img = img.convert("RGB")
            W, H = img.size
            img = img.resize((int(W * self.SCALE), int(H * self.SCALE)), Image.BICUBIC)
            img.show()
        except AttributeError:
            traceback.print_exc(file=sys.stdout)
            print "Most likley you forgot to do .flatten_array() before doing .show_bw() on this class instance"

    def rotate_and_show(self, degrees):
        self.rotate_array(degrees)
        self.flatten_array()
        self.show_bw()

    def generate_mosaic(self):
        self.DATA_ARRAY = self.load_vox_to_array()
        sides = []
        for i in self.ANGLES:
            self.rotate_array(i)
            self.flatten_array()
            img = Image.fromarray(self.FLAT_ARRAY)
            img = img.convert("RGB")
            W, H = img.size
            img = img.resize((int(W * self.SCALE), int(H * self.SCALE)), Image.BICUBIC)
            sides.append(img)
        return sides


################################
## Local methods
###############################
FONT = "/usr/local/bin/gia-programs/quality-control/resources/courbd.ttf"

def findMaxSizes(qc3d):
    w_img_max, h_img_max = (0,0)
    # get max size among angle images
    for i in qc3d.SIDE_VIEWS:
        w_imga, h_imga = qc3d.SIDE_VIEWS[0].size
	if w_imga>w_img_max:
	   w_img_max=w_imga  
	if h_imga>h_img_max:
           h_img_max=h_imga  

    # get the size of the top image
    w_imgt, h_imgt = qc3d.TOP_VIEW.size
    #print "w_imgt =",w_imgt
    #print "h_imgt =",h_imgt

    # get max size among angle and top images
    w_img_max=max(w_img_max,w_imgt)
    h_img_max=max(h_img_max,h_imgt)
    #print "w_img_max =",w_img_max
    #print "h_img_max =",h_img_max 

    w_img_max= w_img_max/int(scale)
    h_img_max= h_img_max/int(scale)

    return (w_img_max,h_img_max)

def add_imgs_to_mosaic(msc):
    # add the angle images to the mosaic
    msc.paste(qc3d.SIDE_VIEWS[0],(0, h_msc/2))
    msc.paste(qc3d.SIDE_VIEWS[1],(0, 0))
    msc.paste(qc3d.SIDE_VIEWS[2],(w_msc/2, 0))
    # add the top image to the mosaic 
    img=qc3d.TOP_VIEW.convert("RGB")
    msc.paste(img,(w_msc/2, h_msc/2))
    return msc

def add_label(img, pos, text, font_size): 

    font = ImageFont.truetype(FONT,font_size)
    
    draw = ImageDraw.Draw(img)
    text = str(text)
    color = (255,0,0)
    text_pos = pos
    draw.text(text_pos,text,color,font=font)

    return img

def add_lines_and_labels(msc):
    # convert to RGB, otherwise add_label will not work
    img=msc.convert("RGB")

    # add lines (cross)
    draw = ImageDraw.Draw(msc)
    draw.line((0, h_msc/2, w_msc, h_msc/2), fill=128,width=1)
    draw.line((w_msc/2, 0 , w_msc/2, h_msc), fill=128,width=1)

    # add labels
    FONT_SIZE=10
    LBL_PAD_W=23
    LBL_PAD_H=15
    label="top"
    pos=(w_msc-LBL_PAD_W,h_msc-LBL_PAD_H)
    img = add_label(msc, pos, label,FONT_SIZE)
    label="1"
    pos=(LBL_PAD_W,h_msc-LBL_PAD_H)
    img = add_label(msc, pos, label,FONT_SIZE)
    label="2"
    pos=(LBL_PAD_W,LBL_PAD_H)
    img = add_label(msc, pos, label,FONT_SIZE)
    label="3"
    pos=(w_msc-LBL_PAD_W,LBL_PAD_H)
    img = add_label(msc, pos, label,FONT_SIZE)
    
    return msc 

################################
## main
###############################
src=sys.argv[1]
dest=sys.argv[2]
print "src=",src
print "dest=",dest
qc3d = QC3D(src,dest,[0,60,60])
# get the scale
scale=qc3d.SCALE
#print "qc3d.SCALE =",qc3d.SCALE

# find max size of angle and top images
w_img_max,h_img_max = findMaxSizes(qc3d)

####################################
# commented by Vladimir Popov - no libraries to support this now
#
#qc3d.TOP_VIEW.show()
#for i in qc3d.SIDE_VIEWS:
#    i.show()
####################################

# initialize mosaic
msc = Image.new("RGB",(w_img_max*2, h_img_max*2))
w_msc=msc.size[0]
h_msc=msc.size[1]
#print "msc.size =",msc.size

# adding images to the mosaic
msc = add_imgs_to_mosaic(msc)
# adding lines and labels to the mosaic
msc = add_lines_and_labels(msc)
# save the mosaic
msc.save(os.path.abspath(os.path.join(dest)))
