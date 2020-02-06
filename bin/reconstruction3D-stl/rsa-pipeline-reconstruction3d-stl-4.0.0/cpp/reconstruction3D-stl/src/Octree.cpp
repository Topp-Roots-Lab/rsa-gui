#include "Octree.h"
#include "util.h"

/*
* Note: maxNode should be positive
*/
Octree::Octree(int maxNode, Point fullSize, Point endSize)
{
	this->_content = new bool[maxNode];
	this->_next = new unsigned int[maxNode];
	this->_reliability = new int[maxNode];
	this->nnode = 0;
	this->maxNode = maxNode;
	this->fullSize.set(fullSize);
	this->endSize.set(endSize);
	this->cubeSize.set(fullSize);
}

inline void Octree::split(Point *lp, Point *hp, Point *pts)
{
	pts[0].set(*lp);
	pts[1].set(lp->x + (hp->x - lp->x)/2,
				lp->y + (hp->y - lp->y)/2,
				lp->z + (hp->z - lp->z)/2);
	pts[2].set(*hp);
}

int Octree::construct()
{
	this->lps = new Point[maxNode];
	this->hps = new Point[maxNode];

	nnode = 1;

	int cur = 0;

	lps[0].set(0,0,0);
	hps[0].set(cubeSize);

	int result;
	Point pts[3];
	int i,j,k;
	int count = 0;
	int nImagesConsistent;
	while (cur<nnode)
	{
		result = upper_check(lps[cur], hps[cur], nImagesConsistent);
		_reliability[cur] = nImagesConsistent;
		switch (result)
		{
		case 0:	
			_content[cur] = false;
			_next[cur] = 0;
			break;
		case 1: 
			_content[cur] = true;
			_next[cur] = 0;
			count++;
			break;
		default:
			_content[cur] = true;
			if (hps[cur]-lps[cur]<=endSize) 
			{
				_next[cur] = 0;
				count++;
			}
			else 
			{
				_next[cur] = nnode;
				if (nnode+8>=maxNode) 
				{
					return -1;
				}
				split(lps+cur, hps+cur, pts);
				for (i=0; i<2; i++)
					for (j=0; j<2; j++)
						for (k=0; k<2; k++)
						{
							lps[nnode].set(pts[i].x, pts[j].y, pts[k].z);
							hps[nnode].set(pts[i+1].x, pts[j+1].y, pts[k+1].z);
							nnode++;
						}
			}			
		}
		cur++;
	}
	cout << nnode << " nodes created." << endl;
	cout << count << " leaves on the octree." << endl;

	delete[] lps;
	delete[] hps;
	return 0;
}

void Octree::outputVoxels(vector< Point* > &objVoxels) 
{
	Point zerop = Point(0,0,0);
	outputVoxels(objVoxels, 0, &zerop, &cubeSize);
	cout << objVoxels.size() << " voxels on the object." << endl;
}

int Octree::outputVoxels(string filename)
{
	ofstream myfile(filename.c_str());
	Point zerop = Point(0,0,0);
	int nVoxels = 0;
	outputVoxels(myfile, 0, &zerop, &cubeSize, nVoxels);
	myfile.close();
	cout << nVoxels << " voxels on the object." << endl;
	return nVoxels;
}

void Octree::outputVoxels(vector< Point* > &objVoxels, int cur, Point* lp, Point* hp)
{
	if (_content[cur] == 0) return;

	if (_next[cur] == 0)
	{
		int x, y, z;
		for (x = lp->x; x < hp->x; x++)
			for (y = lp->y; y < hp->y; y++)
				for (z = lp->z; z < hp->z; z++) {
					objVoxels.push_back(new Point(x, y, z));
				}
	}
	else 
	{
		Point pts[3];
		split(lp, hp, pts);		
		int i, j, k;
		int next;
		Point nextlp, nexthp;
		for (i=0; i<2; i++)
			for (j=0; j<2; j++)
				for (k=0; k<2; k++)
				{
					next = _next[cur] + i*4 + j*2 + k;
					nextlp.set(pts[i].x, pts[j].y, pts[k].z);
					nexthp.set(pts[i+1].x, pts[j+1].y, pts[k+1].z);
					outputVoxels(objVoxels, next, &nextlp, &nexthp);
				}
	}
}

void Octree::outputVoxels(ofstream &myfile, int cur, Point* lp, Point* hp, int &nVoxels)
{
	if (_content[cur] == 0) return;

	if (_next[cur] == 0)
	{
		int x, y, z;
		for (x = lp->x; x < hp->x; x++)
			for (y = lp->y; y < hp->y; y++)
				for (z = lp->z; z < hp->z; z++) 
					myfile << x << " " << y << " " << z << " " << _reliability[cur] << endl;
		nVoxels += (hp->x - lp->x) * (hp->y - lp->y) * (hp->z - lp->z);
	}
	else 
	{
		Point pts[3];
		split(lp, hp, pts);		
		int i, j, k;
		int next;
		Point nextlp, nexthp;
		for (i=0; i<2; i++)
			for (j=0; j<2; j++)
				for (k=0; k<2; k++)
				{
					next = _next[cur] + i*4 + j*2 + k;
					nextlp.set(pts[i].x, pts[j].y, pts[k].z);
					nexthp.set(pts[i+1].x, pts[j+1].y, pts[k+1].z);
					outputVoxels(myfile, next, &nextlp, &nexthp, nVoxels);
				}
	}
}


bool ComparePoint(Point* p1, Point* p2)
{
	return (p1->z < p2->z) || (p1->z == p2->z && p1->x < p2->x) || 
		(p1->z == p2->z && p1->x == p2->x && p1->y < p2->y);
}
void Octree::outputSurfaceCubes(vector< Point* >& myobj, vector< MCCube* >& surfaceCubes, bool isSorted)
{
	//	sort the voxels in increasing z, x, y order
	if (!isSorted)
		sort(myobj.begin(), myobj.end(), ComparePoint);

	static int offsets[4][2] = {{-1, -1}, {0, -1}, {0, 0}, {-1, 0}};

	//	allocate memory
	bool*** onObject = new bool**[2];
	bool **itr1, **itr2;
	for (int i = 0; i < 2; ++i)
	{
		itr1 = onObject[i] = new bool*[cubeSize.x];
		for (int j = 0; j < cubeSize.x; ++j)
			itr1[j] = new bool[cubeSize.y];
	}
	int nVoxelsPerHeight = cubeSize.x * cubeSize.y;

	int x1, x2, y1, y2, z1;
	int k;
	int count;

	//	compute cubes on the surface
	int index = 0;
	vector< Point* >::const_iterator itr_p = myobj.begin();
	for (z1 = 0; z1 < cubeSize.z; ++z1) 
	{
		itr1 = onObject[index];
		itr2 = onObject[1-index];

		for (x1 = 0; x1 < cubeSize.x; ++x1){
			memset(itr1[x1], 0, sizeof(bool)*cubeSize.y);
		}
		while (itr_p != myobj.end() && (*itr_p)->z == z1) {
			itr1[(*itr_p)->x][(*itr_p)->y] = true;
			++itr_p;
		}
		
		for (x1 = 0; x1 < cubeSize.x; ++x1)
			for (y1 = 0; y1 < cubeSize.y; ++y1)
			{
				if (x1 == 0 || y1 == 0 || z1 == 0) continue;

				count = itr1[x1][y1] + itr1[x1-1][y1] + itr1[x1][y1-1] + itr1[x1-1][y1-1];
				count += itr2[x1][y1] + itr2[x1-1][y1] + itr2[x1][y1-1] + itr2[x1-1][y1-1];
				if (count > 0 && count < 8)	// if on the boundary
				{
					MCCube* cube = new MCCube();
					cube->p1 = new MCVertex((float)x1-1, (float)y1-1, (float)z1-1);
					cube->p2 = new MCVertex((float)x1, (float)y1, (float)z1);
					cube->val = new bool[8];
					for (k = 0; k < 4; k++)
					{
						x2 = x1 + offsets[k][0];
						y2 = y1 + offsets[k][1];
						cube->val[k] = itr2[x2][y2];
						cube->val[k+4] = itr1[x2][y2];
					}
					surfaceCubes.push_back(cube);
				}
			}
		index = 1-index;
	}

	cout << surfaceCubes.size() << " cubes on the surface." << endl;

	//	release memory
	for (int i = 0; i < 2; ++i)
	{
		itr1 = onObject[i];
		for (int j = 0; j < cubeSize.x; ++j)
			delete itr1[j];
		delete onObject[i];
	}
	delete onObject;
}

bool Octree::isOnObject(Point* p) 
{
	Point lp(0, 0, 0);
	Point hp(cubeSize);
	int cur = 0;

	Point pts[3];
	int i, j, k;
	int next;
	Point nextlp, nexthp;
	bool isFoundNext;
	while (_next[cur] > 0)
	{
		split(&lp, &hp, pts);		
		isFoundNext = false;
		for (i=0; i<2 && !isFoundNext; i++)
			for (j=0; j<2 && !isFoundNext; j++)
				for (k=0; k<2 && !isFoundNext; k++)
				{
					next = _next[cur] + i*4 + j*2 + k;
					nextlp.set(pts[i].x, pts[j].y, pts[k].z);
					nexthp.set(pts[i+1].x, pts[j+1].y, pts[k+1].z);
					
					if (nextlp <= *p && *p < nexthp)
					{
						isFoundNext = true;
						cur = next;
						lp.set(nextlp);
						hp.set(nexthp);
					}
				}
	}

	return _content[cur];
}

void Octree::dropOctree()
{
	delete[] _content;
	delete[] _reliability;
	delete[] _next;
}

void Octree::expandObject(vector< Point* > &myobj)
{
	static const int ndirs = 26;
	static int dirs[ndirs][3] = {{-1,-1,-1},{-1,-1,0},{-1,-1,1},
								{-1,0,-1},{-1,0,0},{-1,0,1},
								{-1,1,-1},{-1,1,0},{-1,1,1},
								{0,-1,-1},{0,-1,0},{0,-1,1},
								{0,0,-1},{0,0,1},
								{0,1,-1},{0,1,0},{0,1,1},
								{1,-1,-1},{1,-1,0},{1,-1,1},
								{1,0,-1},{1,0,0},{1,0,1},
								{1,1,-1},{1,1,0},{1,1,1}};
	static Point zerop(0,0,0);

	map<int, bool> mymap;
	vector< Point* >::const_iterator p_voxel;
	Point* p;
	int tmp;
	mymap.clear();
	for (p_voxel = myobj.begin(); p_voxel != myobj.end(); ++p_voxel) {
		p = *p_voxel;
		mymap[p->z*cubeSize.x*cubeSize.y + p->y*cubeSize.x + p->x] = true;
	}

	unsigned int i = 0;
	int k;
	Point p2;
	int key;
	while (i < myobj.size()) {
		p = myobj[i];
		for (k = 0; k < ndirs; ++k) {
			p2.x = p->x + dirs[k][0];
			p2.y = p->y + dirs[k][1];
			p2.z = p->z + dirs[k][2];
			if (zerop<=p2 && p2<cubeSize){
				key = p2.z*cubeSize.x*cubeSize.y+p2.y*cubeSize.x+p2.x;
				if (mymap.find(key) == mymap.end()) {
					if (lower_check(p2, Point(p2.x+1, p2.y+1, p2.z+1), tmp)) {
						myobj.push_back(new Point(p2.x, p2.y, p2.z));
					}
					mymap[key] = true;
				}
			}
		}
		++i;
	}
	mymap.clear();
}

void Octree::outputMesh(vector< Point* > &myobj, string filename, float extrainfo, bool isSorted)
{
	//	find surface cubes
	vector< MCCube* > surfaceCubes;
	outputSurfaceCubes(myobj, surfaceCubes, isSorted);

	//	generate triangulated mesh using MarchingCube Alg.
	vector< MCTri* > triangles;
	TriSurfMesh(surfaceCubes, triangles);
	cout << "Triangulated the surface. Total triangles: " << triangles.size() << endl;

	//	output mesh
	ofstream ofs(filename.c_str());
	//ofs << extrainfo << endl;
	ofs << "solid root" << endl;
	vector< MCTri* >::const_iterator i_triangle;
	MCTri* triangle;
	for (i_triangle = triangles.begin(); i_triangle != triangles.end(); ++i_triangle)
	{
		triangle = *i_triangle;
		ofs << "facet normal 0 0 0" << endl;
		ofs << "	outer loop" << endl;
		ofs << "		vertex " << triangle->p1.x << " "
			<< triangle->p1.y << " " << triangle->p1.z << endl;
		ofs << "		vertex " << triangle->p2.x << " "
			<< triangle->p2.y << " " << triangle->p2.z << endl;
		ofs << "		vertex " << triangle->p3.x << " "
			<< triangle->p3.y << " " << triangle->p3.z << endl;
		ofs << "	endloop" << endl;
		ofs << "endfacet" << endl;
	}
	ofs << "endsolid root" << endl;
	ofs.close();
}
