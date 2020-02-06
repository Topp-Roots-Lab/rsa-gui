#pragma once

#include <vector>
#include "mesh.h"
#include "TriSurfMesh.h"


//Apply jet color using the value (0 - 1)
void SetJetMaterial( float value, float& elem_a, float& elem_b, float& elem_c){
	elem_c = 1.5f - 4.0f*fabs(value - 0.25f);
	if (elem_c <0 ) elem_c = 0.0f;
	else if (elem_c > 1 ) elem_c = 1.0f;
	elem_b = 1.5f - 4.0f*fabs(value - 0.5f);
	if (elem_b <0 ) elem_b = 0.0f;
	else if (elem_b > 1) elem_b = 1.0f;
	elem_a = 1.5f - 4.0f*fabs(value - 0.75f);
	if ( elem_a <0 ) elem_a = 0.0f;
	else if ( elem_a > 1 ) elem_a = 1.0f;
}

void SetGrayMaterial( float value, float& elem_a, float& elem_b, float& elem_c){
	elem_a = value;
	elem_b = value;
	elem_c = value;
}

void SetCoolMaterial( float value, float& elem_a, float& elem_b, float& elem_c){
	elem_a = value;
	elem_b = 1 - value;
	elem_c = 1.0f;
}

void LoadVoxels(vector< Point* >& myobj, Mesh* &mesh)
{
	// Allocate mesh structure
	mesh = new Mesh();
	if (!mesh) {
		fprintf(stderr, "Unable to allocate memory \n");
		exit(-1);
	}

	mesh->numVertices = myobj.size();
	mesh->vertices = new Vertex[ mesh->numVertices ];
	Vertex* p_vertex = mesh->vertices;
	vector< Point* >::const_iterator p_voxel;
	Point* p;
	for (p_voxel = myobj.begin(); p_voxel != myobj.end(); ++p_voxel, ++p_vertex) {
		p = *p_voxel;
		p_vertex->Set((float)p->x, (float)p->y, (float)p->z);
	}

	mesh->bbox[0][0] = 1e30f;
	mesh->bbox[0][1] = 1e30f;
	mesh->bbox[0][2] = 1e30f;
	mesh->bbox[1][0] = -1e30f;
	mesh->bbox[1][1] = -1e30f;
	mesh->bbox[1][2] = -1e30f;

	for ( int i = 0; i < mesh->numVertices; ++i ){
		Vertex& vert = mesh->vertices[i];

		if ( vert.x < mesh->bbox[0][0] ) mesh->bbox[0][0] = vert.x;
		if ( vert.x > mesh->bbox[1][0] ) mesh->bbox[1][0] = vert.x;

		if ( vert.y < mesh->bbox[0][1] ) mesh->bbox[0][1] = vert.y;
		if ( vert.y > mesh->bbox[1][1] ) mesh->bbox[1][1] = vert.y;

		if ( vert.z < mesh->bbox[0][2] ) mesh->bbox[0][2] = vert.z;
		if ( vert.z > mesh->bbox[1][2] ) mesh->bbox[1][2] = vert.z;
	}

	//load material
	mesh->material = new Vertex[mesh->numVertices];
	float range = mesh->bbox[1][2] - mesh->bbox[0][2];
	float min_z = mesh->bbox[0][2];

	//apply jet material
	mesh->material = new Vertex[ mesh->numVertices ];
	Vertex* pV = mesh->vertices; Vertex* pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetJetMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//apply cool material
	mesh->material = new Vertex[ mesh->numVertices ];
	pV = mesh->vertices; pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetCoolMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//apply gray material
	mesh->material = new Vertex[ mesh->numVertices ];
	pV = mesh->vertices; pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetGrayMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//set default material to be jet color
	mesh->material = mesh->materials[0];
}

void LoadMesh( vector<MCTri*>& triangles, Mesh* &mesh )
{

	// Allocate mesh structure
	mesh = new Mesh();
	if (!mesh) {
		fprintf(stderr, "Unable to allocate memory \n");
		exit(-1);
	}

	vector<MCTri*>::const_iterator triItr = triangles.begin();
	mesh->numFaces = triangles.size();
	mesh->numVertices = mesh->numFaces * 3;
	mesh->vertices = new Vertex[ mesh->numVertices ];
	mesh->faces = new Triangle[ mesh->numFaces ];
	mesh->normals = new Vertex[ mesh->numVertices ];
	memset(mesh->normals,0,sizeof(Vertex)*mesh->numVertices);

	Vertex* pVertex = mesh->vertices;
	Triangle* pFace = mesh->faces;
	MCTri* pMCTri;

	float a1,b1,c1,a2,b2,c2;
	Vertex normal;
	float squared_normal_length, normal_length;
	int t_index = 0, index = 0;

	for ( ; triItr != triangles.end(); ++triItr ){
		pMCTri = *triItr;
		pVertex->Set( pMCTri->p1.x, pMCTri->p1.y, pMCTri->p1.z);
		Vertex& v1 = *pVertex;
		++pVertex;

		pVertex->Set( pMCTri->p2.x, pMCTri->p2.y, pMCTri->p2.z);
		Vertex& v2 = *pVertex;
		++pVertex;

		pVertex->Set( pMCTri->p3.x, pMCTri->p3.y, pMCTri->p3.z);
		Vertex& v3 = *pVertex;
		++pVertex;

		pFace->index[0] = t_index;
		pFace->index[1] = t_index+1;
		pFace->index[2] = t_index+2;


		// Compute normal for the face
		a1 = v2.x - v1.x; b1 = v2.y - v1.y; c1 = v2.z - v1.z;
		a2 = v3.x-  v2.x; b2 = v3.y - v2.y; c2 = v3.z - v2.z;
		normal.x = b1 * c2 - b2 * c1;
		normal.y = a2 * c1 - a1 * c2;
		normal.z = a1 * b2 - a2 * b1;
		squared_normal_length = normal.x*normal.x + normal.y*normal.y + normal.z*normal.z;
		normal_length = sqrt(squared_normal_length);
		if ( normal_length > 1.0e-6){
			normal.x /= normal_length;
			normal.y /= normal_length;
			normal.z /= normal_length;
		}

		//distribute normal vectors to each vertex of the face
		for ( int i = 0; i < 3; ++i){
			index = pFace->index[i];
			mesh->normals[index].x += normal.x;
			mesh->normals[index].y += normal.y;
			mesh->normals[index].z += normal.z;
		}

		++pFace;

		t_index += 3;
	}


	//normalize normals for each vertex
	Vertex* pNormal = mesh->normals;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pNormal ){
		squared_normal_length = 0;
		squared_normal_length += pNormal->x * pNormal->x;
		squared_normal_length += pNormal->y * pNormal->y;
		squared_normal_length += pNormal->z * pNormal->z;
		normal_length = sqrt(squared_normal_length);
		if ( normal_length > 1.0e-6){
			pNormal->x /= normal_length;
			pNormal->y /= normal_length;
			pNormal->z /= normal_length;
		}
	}


	mesh->bbox[0][0] = 1e30f;
	mesh->bbox[0][1] = 1e30f;
	mesh->bbox[0][2] = 1e30f;
	mesh->bbox[1][0] = -1e30f;
	mesh->bbox[1][1] = -1e30f;
	mesh->bbox[1][2] = -1e30f;


	for ( int i = 0; i < mesh->numVertices; ++i ){

		Vertex& vert = mesh->vertices[i];

		// Update bounding box
		if ( vert.x < mesh->bbox[0][0] ) mesh->bbox[0][0] = vert.x;
		if ( vert.x > mesh->bbox[1][0] ) mesh->bbox[1][0] = vert.x;

		if ( vert.y < mesh->bbox[0][1] ) mesh->bbox[0][1] = vert.y;
		if ( vert.y > mesh->bbox[1][1] ) mesh->bbox[1][1] = vert.y;

		if ( vert.z < mesh->bbox[0][2] ) mesh->bbox[0][2] = vert.z;
		if ( vert.z > mesh->bbox[1][2] ) mesh->bbox[1][2] = vert.z;
	}


	//load material
	mesh->material = new Vertex[mesh->numVertices];
	float range = mesh->bbox[1][2] - mesh->bbox[0][2];
	float min_z = mesh->bbox[0][2];



	//apply jet material
	mesh->material = new Vertex[ mesh->numVertices ];
	Vertex* pV = mesh->vertices; Vertex* pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetJetMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//apply cool material
	mesh->material = new Vertex[ mesh->numVertices ];
	pV = mesh->vertices; pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetCoolMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//apply gray material
	mesh->material = new Vertex[ mesh->numVertices ];
	pV = mesh->vertices; pC = mesh->material;
	for ( int i = 0; i < mesh->numVertices; ++i, ++pV, ++pC){
		SetGrayMaterial( (pV->z - min_z)/range, pC->x,pC->y, pC->z);
	}
	mesh->materials.push_back( mesh->material );

	//set default material to be jet color
	mesh->material = mesh->materials[0];

}
