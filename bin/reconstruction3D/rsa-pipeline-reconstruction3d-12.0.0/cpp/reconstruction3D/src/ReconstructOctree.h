#pragma once

#include "octree.h"
#include "CImg.h"
#include <iostream>
#include <fstream>
#include "math.h"

using namespace cimg_library;

class ReconstructOctree : public Octree
{
public:
	/*
	Note: cubeSize should be odd
	*/
	ReconstructOctree(int maxNode, Point fullSize, Point endSize, 
		string sil_prefix, int nfile, string file_ext, 
		string file_conf, int distortion_radius, int rotation_digits);

	/*
	* set 'cubeSize' and update 'scales' and 'halfSize';
	*/
	void setResolution(Point cubeSize);

	/**
	* release the memory of image information
	*/
	void dropImage(); 

	void setDistortionRadius(int distortion_radius) {this->distortion_radius = distortion_radius;}

	/**
	* return true if satisfy consistency check
	*/
	bool addConsistency(int refImageID, int* consistency, int* reliability, Point* points, int npts, double ratio);

protected:
	
	/*
	* check whether a node should be expanded or not
	* input:
	*	lp:	the left-bottom node
	*	hp:	the right-top node
	* output:
	*	0: if nothing
	*	1: if all
	*	2: otw.
	*/
	unsigned int check(Point lp, Point hp, int &radius, int &nImagesConsistent);

	/*
	* check whether a voxel is on the object or not
	* input:
	*	p:		the left-bottom coordinate of the voxel
	* output:
	*	true:	if on the object
	*	false:	otw.
	*/
	bool expand_check(Point* p);
	
	/*
	* read image information
	* input:
	*	file_prefix:	prefix of input image file names
	*	nfile:			#. files
	*	file_ext:		extension of input image files
	*	store image information in images_*;
	*/
	void readSilhouettes(string file_prefix, int nfile, 
                         string file_ext, int rotation_digits);

	/*
	* read black&white image
	* input:
	*	filename:	name of the input image
	*	n:			height of the image
	*	m:			width of the image
	* return:
	*	image content (bool type, 0-black, 1-white)
	*/
	bool** readBlackWhiteImage(string filename, int& n, int& m);

	/*
	* get #. 1's in the specific rectangle from (lr, lc) to (hr, hc)
	* input
	*	image:		image index
	*	lr:			upper row
	*	lc:			left column
	*	hr:			bottom row
	*	hc:			right column
	* Note: have to make sure that row & col are within the image boundary
	*/
	int getSilStat(int image, int lr, int lc, int hr, int hc);

	/*
	* read configuration file, which contains:
	*	1. threshold: percentage of images to be consistent with
	*	2. Rs, Ts: camera parameters
	*/
	void readConfiguration(string file_conf);

	/*
	* back project a point to the image by perspective projection
	* input:
	*	image:		image index
	*	p:			point coordinate
	*	row:		row after back projection (return value)
	*	col:		column after back projection (return value)
	*/
	void orthographicProject(int image, Point p, int& row, int& col);

protected:
	int*** silhouette_stat;							// #. 1's in the corresponding rectangle 
	int image_width, image_height;					// width and height of the images
	int nimages;									// #. images
	float image_center_x, image_center_y;			// center of image

	float** Rs;										// rotation matrix
	bool *chooselx, *choosely, *choosehx, *choosehy;
	float scales[2];								// zoom scale;
	Point halfSize;									// half of the cube size

	int distortion_radius;
};

void testOctree(ReconstructOctree *mytree, Point cubeSize);
