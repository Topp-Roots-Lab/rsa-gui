#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>
#include <math.h>
#include <vector>

////////////////////////////////////////////////////////////
// TYPE DEFINITIONS
////////////////////////////////////////////////////////////
typedef unsigned int uint32;

typedef struct Vertex {
	float x, y, z;				// (x,y,z) are Euclidean coordinates
	void Set(Vertex* v){this->x = v->x; this->y = v->y; this->z = v->z;}
	void Set(float x, float y, float z){this->x = x; this->y = y; this->z = z;}
	Vertex(float x, float y, float z){this->x = x; this->y = y; this->z = z;}
	Vertex(){}
} Vertex;

typedef struct Triangle {
	int index[3];				//indices to vertices
} Triangle;

typedef struct Mesh {
	Vertex		*vertices;		//set of vertices
	Vertex		*normals;		//set of normals
	Vertex		*material;		//set of materials
	std::vector< Vertex* > materials;

	Triangle	*faces;			//set of faces
	int			numVertices;	//# vertices
	int			numFaces;		//# faces
	float		bbox[2][3];		//bounding box
	Mesh(){
		numVertices = 0; 
		numFaces = 0; 
		vertices = NULL;
		normals = NULL; 
		material = NULL; 
		faces = NULL;
		// Initialize bounding box
		bbox[0][0] = 1e30f; 
		bbox[1][0] = -1e30f;
		bbox[0][1] = 1e30f; 
		bbox[1][1] = -1e30f;
		bbox[0][2] = 1e30f; 
		bbox[1][2] = -1e30f;
	}
} Mesh;
