#include <iostream>
#include <fstream>
#include <map>
#include <stdlib.h>
#include "LinkedGraph.h"

const int MAX_COMP_NUMBER = 100;

int* _consistency;
int* _reliability;
Point* pts;
int n;
map<int, int> m_map_pts;
Point* maxp;
int* mst_gr;
int* mst_level;
int* gr_index_per_comp;
LinkedGraph* mytree;

inline int getHash(Point* p, Point* maxp)
{
	return (p->x*maxp->y*maxp->z + p->y*maxp->z + p->z);
}

int compareids(const void *a, const void *b)
{
	int id1 = *(int*)a;
	int id2 = *(int*)b;
	if (_reliability[id1] != _reliability[id2]) 
		return _reliability[id1] - _reliability[id2];
	return _consistency[id2] - _consistency[id1];
}

int mst_find(int id)
{
	int i = id, j = id;
	while (mst_gr[i] != i) i = mst_gr[i];
	int grid = i;
	while (mst_gr[j] != j) {
		i = mst_gr[j];
		mst_gr[j] = grid;
		j = i;
	}
	return grid;
}

//	return true if in the same group already
bool mst_union(int gr1, int gr2)
{
	if (gr1 == gr2) return true;
	if (mst_level[gr1] < mst_level[gr2]) {
		mst_gr[gr1] = gr2;
	}
	else if (mst_level[gr2] < mst_level[gr1]) {
		mst_gr[gr2] = gr1;
	}
	else {
		mst_gr[gr1] = gr2; ++mst_level[gr2];
	}
	return false;
}

int mymst(int minCompSize, vector<Point*> &myobj, vector<int> &comp_index)
{
	int* sorted_ids = new int[n];
	int* inverse_ids = new int[n];
	for (int i = 0; i < n; ++i) sorted_ids[i] = i;
	qsort(sorted_ids, n, sizeof(int), compareids);
	for (int i = 0; i < n; ++i) inverse_ids[sorted_ids[i]] = i;

	mytree = new LinkedGraph(n, n);
	mst_gr = new int[n];
	mst_level = new int[n];
	for (int i = 0; i < n; ++i) {
		mst_gr[i] = i;  mst_level[i] = 1;
	}

	int id, id_nxt;
	int gr1, gr2;
	int dx, dy, dz, x, y, z;
	int thash;
	Point* tp = new Point();

	for (int i = 0; i < n; ++i) {
		id = sorted_ids[i];
		for (dx = -1; dx <= 1; ++dx)
			for (dy = -1; dy <= 1; ++dy)
				for (dz = -1; dz <= 1; ++dz)
				{
					x = pts[id].x + dx;
					y = pts[id].y + dy;
					z = pts[id].z + dz;
					if (x > 0 && y > 0 && z > 0 &&
						x < maxp->x && y < maxp->y && z < maxp->z)
					{
						tp->x = x; tp->y = y; tp->z = z;
						thash = getHash(tp, maxp);
						if (m_map_pts.find(thash) != m_map_pts.end())
						{
							id_nxt = m_map_pts[thash];
							if (inverse_ids[id]>inverse_ids[id_nxt])
							{
								gr1 = mst_find(id);
								gr2 = mst_find(id_nxt);
								if (gr1 == gr2) continue;

								// link groups associated with id and id_nxt
								mst_union(gr1, gr2);
								mytree->addEdge(id, id_nxt);
							}
						}
					}
				}
	}
	//cout << "added " << mytree->edge_num() << " edges and " << 
	//	mytree->node_num() << " nodes." << endl;

	//	filter leaves
	int node1, node2;
	int tmp = 0;
	for (int i = 0; i < n; ++i) {
		node1 = i;
		while (_reliability[node1] > 0 && mytree->degree(node1) == 1) {
			node2 = mytree->firstAdjNode(node1);
			mytree->deleteEdge(node1, node2);
			node1 = node2;
		}
	}

	//	output voxels
	memset(mst_level, 0, sizeof(int)*n);
	for (int i = 0; i < n; ++i)
		if (mytree->degree(i)>0) {
			gr1 = mst_find(i);
			mst_level[gr1]++;
		}
	myobj.clear();
	comp_index.clear();
	int nComp = 0;
	int nElements;
	int comp_i;
	gr_index_per_comp = new int[MAX_COMP_NUMBER];
	for (int i = 0; i < n; ++i)
		if (mytree->degree(i)>0) {
			gr1 = mst_find(i);
			nElements = mst_level[gr1];
			if (nElements > minCompSize || nElements < -minCompSize) {
				if (nElements > 0) {
					gr_index_per_comp[nComp] = gr1;
					nComp++;
					cout << nElements << " ";
					mst_level[gr1] = -nElements;
				}
				myobj.push_back(pts+i);

				//	record component index
				comp_i = 0;
				while (gr_index_per_comp[comp_i] != gr1) ++comp_i;
				comp_index.push_back(comp_i);
			}
		}
	cout << endl;

	delete[] sorted_ids;
	delete[] inverse_ids;
	delete[] mst_gr;
	delete[] mst_level;
	delete[] gr_index_per_comp;
	delete tp;
	delete mytree;

	return nComp;
}

int guarantee_connectedness(int* consistency, int* reliability, Point* _pts, int npts, Point* volsize, int minCompSize, vector<Point*> &myobj, vector<int> &comp_index)
{
	_consistency = consistency;
	_reliability = reliability;
	pts = _pts; n = npts;
	maxp = volsize;

	Point* p_p = pts;
	for (int i = 0; i < npts; ++i, ++p_p)
		m_map_pts[getHash(p_p, maxp)] = i;

	int nComp = mymst(minCompSize, myobj, comp_index);
	m_map_pts.clear();
	return nComp;
}
