#pragma once

#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <algorithm>
#include "mesh.h"
#include "TriSurfMesh.h"

using namespace std;

struct Point
{
	Point() {x = y = z = 0;}
	Point(int x, int y, int z) {this->x = x; this->y = y; this->z = z;}
	void set(Point p) {this->x = p.x; this->y = p.y; this->z = p.z;}
	void set(int x, int y, int z) {this->x = x; this->y = y; this->z = z;}
	bool operator==(const Point&p) {return x==p.x && y==p.y && z==p.z;}
	bool operator<=(const Point &p) {return (x<=p.x && y<=p.y && z<=p.z);}
	bool operator<(const Point &p) {return (x<p.x && y<p.y && z<p.z);}
	Point operator-(const Point &p) {return Point(x-p.x, y-p.y, z-p.z);}
	Point operator+(const Point &p) {return Point(x+p.x, y+p.y, z+p.z);}
	Point operator/(const unsigned int d) {return Point(x/d, y/d, z/d);}
	int x, y, z;
};

class Octree
{
public:

	/*
	* construction function
	* input:
	*	maxNode:	max #. nodes
	*	cubeSize:	size of the cube
	* note: coordinates from (0,0,0) to (sizex, sizey, sizez)
	*/
	Octree(int maxNode, Point fullSize, Point endSize);

	/*
	* construct the octree
	* note: nothing on the octree should be modified after that
	*/
	int construct();

	/*
	* check whether a voxel is on the object or not
	*/
	bool isOnObject(Point* p);

	/*
	* output the voxels on the object to a file
	* return: #. voxels on the object
	*/
	void outputVoxels(int* consistency, int* reliability, Point* points);

	/*
	* return the voxels on the object
	*/
	void outputVoxels(vector< Point* > &objVoxels);

	/*
	* output surface mesh given a set of points on the object
	*/
	void outputMesh(vector< Point* > &myobj, Mesh* &mesh, bool isSorted = false);

	/*
	* output surface mesh given a set of points on the object
	*/
	void outputMesh(vector< Point* > &myobj, string filename, float extrainfo, bool isSorted = false);

	/*
	* set cubesize
	*/
	void setCubesize(Point cubeSize) {this->cubeSize.set(cubeSize);}

	/**
	* drop octree
	*/
	void dropOctree();

	/**
	* expand the object using lower_threshold
	*/
	void expandObject(vector< Point* > &myobj);

	/**
	* output voxels on the surface of the object
	*/
	void outputVoxelsOnSurface(vector< Point* > &myobj, vector< Point* > &mysurfacevoxels);

	void adjustMesh(Mesh* mesh);

	int getNumLeaf() {return nleaf;}

	int getNumVoxel() {return nvoxel;}

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
	virtual unsigned int check(Point lp, Point hp, int &radius, int &nImagesConsistent) = 0; 
	
	/*
	* split a node equally into 8 parts
	* input:
	*	lp:		lower point coordinate
	*	hp:		hight point coordinate
	*	pts:	compute 4 choices on each axis (return)
	*/
	inline void split(Point *lp, Point *hp, Point *pts);

	/*
	* output subtree indexed on 'cur' position in the tree to a file
	* input:
	*	myfile:		output file
	*	cur:		index of the subtree
	*	lp:			low point
	*	hp:			high point
	*/
	void outputVoxels(int *consistency, int *reliability, Point *points, int cur, Point* lp, Point* hp, int& index);

	/*
	* output subtree indexed on 'cur' position in the tree
	* input:
	*	objVoxels:	store all the voxels in the subtree
	*	cur:		index of the subtree
	*	lp:			low point
	*	hp:			high point
	*/
	void outputVoxels(vector< Point* > &objVoxels, int cur, Point* lp, Point* hp);
	
	/*
	* output surface cubes
	*/
	void outputSurfaceCubes(vector< Point* >& myobj, vector< MCCube* >& surfaceCubes, bool isSorted=false);

protected:
	bool *_content;				// whether there is anything on the node
	unsigned int *_next;		// first child's subscript
	int *_reliability;			// perturb radius
	int *_consistency;			// #. images consistent with
	int nleaf;					// #. leaves
	int nvoxel;				// #. voxels
	int nnode;					// current #. nodes
	int maxNode;				// max #. node
	Point cubeSize;				// initial size of cube
	Point endSize;				// max size of the cube to finish expanding 
	Point fullSize;				// full resolution of the original object

	Point *lps;					// lowest point of a subcube
	Point *hps;					// highest point of a subcube
};
