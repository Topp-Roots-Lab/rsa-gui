#include "TriSurfMesh.h"

MCVertex* mdPt = new MCVertex[12];

void Polygonise(MCCube* cube, vector< MCTri* >& output)
{

	int cubeindex = 0;
	bool* bItr = cube->val; int bit = 1;
	for ( int i = 0; i < 8; ++i){
		if ( ! (*bItr) ){
			cubeindex |= bit;
		}
		bit += bit; ++bItr;
	}

	//extract edge index from the pre-defined table	
	int edgeIndex = edgeTable[cubeindex];

	//if nothing to generate
	if ( edgeIndex== 0 )
		return;	

	//pre-compute the middle value
	float mdX = (cube->p1->x + cube->p2->x)/2;
	float mdY = (cube->p1->y + cube->p2->y)/2;
	float mdZ = (cube->p1->z + cube->p2->z)/2;

	//compute the 12 cases of middle edge points
	MCVertex* mdPtItr = mdPt;
	if ( edgeIndex & 1 ){	// 0---1
		mdPtItr->x = mdX;
		mdPtItr->y = cube->p1->y;
		mdPtItr->z = cube->p1->z;
	}

	++mdPtItr;
	if ( edgeIndex & 2 ){	// 1---2
		mdPtItr->x = cube->p2->x;
		mdPtItr->y = mdY;
		mdPtItr->z = cube->p1->z;
	}

	++mdPtItr;
	if ( edgeIndex & 4 ){	// 2---3
		mdPtItr->x = mdX;
		mdPtItr->y = cube->p2->y;
		mdPtItr->z = cube->p1->z;
	}

	++mdPtItr;
	if ( edgeIndex & 8 ){	// 3---0
		mdPtItr->x = cube->p1->x;
		mdPtItr->y = mdY;
		mdPtItr->z = cube->p1->z;
	}

	++mdPtItr;
	if ( edgeIndex & 16){	// 4---5
		mdPtItr->x = mdX;
		mdPtItr->y = cube->p1->y;
		mdPtItr->z = cube->p2->z;		
	}

	++mdPtItr;
	if ( edgeIndex & 32){	// 5---6
		mdPtItr->x = cube->p2->x;
		mdPtItr->y = mdY;
		mdPtItr->z = cube->p2->z;
	}

	++mdPtItr;
	if ( edgeIndex & 64){	//6---7
		mdPtItr->x = mdX;
		mdPtItr->y = cube->p2->y;
		mdPtItr->z = cube->p2->z;
	}

	++mdPtItr;
	if ( edgeIndex & 128){	//7---4
		mdPtItr->x = cube->p1->x;
		mdPtItr->y = mdY;
		mdPtItr->z = cube->p2->z;
	}

	++mdPtItr;
	if ( edgeIndex & 256){	//0---4
		mdPtItr->x = cube->p1->x;
		mdPtItr->y = cube->p1->y;
		mdPtItr->z = mdZ;
	}

	++mdPtItr;
	if ( edgeIndex & 512){	//1---5
		mdPtItr->x = cube->p2->x;
		mdPtItr->y = cube->p1->y;
		mdPtItr->z = mdZ;
	}

	++mdPtItr;
	if ( edgeIndex & 1024){	//2---6
		mdPtItr->x = cube->p2->x;
		mdPtItr->y = cube->p2->y;
		mdPtItr->z = mdZ;
	}

	++mdPtItr;
	if ( edgeIndex & 2048){//3---7
		mdPtItr->x = cube->p1->x;
		mdPtItr->y = cube->p2->y;
		mdPtItr->z = mdZ;
	}


	/* Create the triangle */
	int* tableItr = triTable[cubeindex];
	while( (*tableItr) >= 0){
		output.push_back( 
			new MCTri(
				mdPt[*tableItr], 
				mdPt[*(tableItr + 1)], 
				mdPt[*(tableItr + 2)]
				) 
			);
			tableItr += 3;
	}
}


// extract surface mesh
void TriSurfMesh( vector< MCCube*>& input, vector< MCTri*>& output){
	vector< MCCube* >::const_iterator itr_cube; MCCube*	pCube;
	for ( itr_cube = input.begin(); itr_cube != input.end(); ++itr_cube ){
		pCube = *itr_cube;
		Polygonise( pCube, output );
	}
}
