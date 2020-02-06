#include "ReconstructOctree.h"

using namespace std;

class NullOctree : public Octree
{
public:
	NullOctree(int maxNode, Point fullSize, Point endSize)
		: Octree(maxNode, fullSize, endSize) {}
private:
	unsigned int lower_check(Point lp, Point hp, int &nImagesConsistent) {
		nImagesConsistent = 0; return 0;
	} 
	unsigned int upper_check(Point lp, Point hp, int &nImagesConsistent) {
		nImagesConsistent = 0; return 0;
	}
};

void outputVoxels(vector< Point* >& myobj, string filename, float extrainfo)
{
	ofstream outfile(filename.c_str());
	outfile << extrainfo << endl;
	outfile << myobj.size() << endl;
	vector< Point* >::const_iterator p_point;
	Point* point;
	for (p_point = myobj.begin(); p_point != myobj.end(); ++p_point) {
		point = *p_point;
		outfile << point->x << " " << point->y << " " << point->z << endl;
	}
	outfile.close();
}


void loadVoxels(string filename, vector< Point* >& myobj, int& imgWidth, int &imgHeight) 
{
	ifstream infile(filename.c_str());
	int n, i;
	int x, y, z;
	char buffer[1024];
	imgWidth = imgHeight = 0;
	float extraInfo;
	infile >> extraInfo;
	infile >> n;
	for (i = 0; i < n; i++) {
		infile >> x >> y >> z;
		if (x > imgWidth) imgWidth = x;
		if (y > imgWidth) imgWidth = y;
		if (z > imgHeight) imgHeight = z;
		infile.getline(buffer, 1023);
		myobj.push_back(new Point(x, y, z));
	}
	infile.close();
	imgHeight++; imgWidth++;
}

int main(int argc, char** argv) 
{
	if (argc < 2) {
		cout << "Invalid arguments" << endl;
		exit(-1);
	}
	int opt = atoi(argv[1]);
	cout << opt << " " << argc << endl;
	if (opt != 2 && opt != 5) {
		cout << "Invalid arguments" << endl;
		exit(-1);
	}
	if (opt == 2 && argc != 13 || opt == 5 && argc != 5) {
		cout << "Invalid arguments" << endl;
		exit(-1);
	}

	if (opt == 5)
	{
		int sampling = 1;
		vector< Point* > myobj;
		int imgWidth, imgHeight;
		cout << argv[2] << endl;
		loadVoxels(argv[2], myobj, imgWidth, imgHeight);
		cout << myobj.size() << " voxels loaded, imgwidth = " << imgWidth <<
			", imgheight = " << imgHeight << "." << endl;

		Point fullsize( imgWidth, imgWidth, imgHeight );
		NullOctree* mytree = new NullOctree(0, fullsize/sampling, Point(1,1,1));
		mytree->outputMesh(myobj, argv[3], atof(argv[4]));
		return 0;
	}
	int imgWidth = atoi(argv[2]);
	int imgHeight = atoi(argv[3]);
	Point fullsize( imgWidth, imgWidth, imgHeight );

	int numNodesOnOctree = atoi(argv[4]);
	Point Unit(1,1,1);

	char* silPrefix = argv[5];
	int numImgUsed = atoi(argv[6]);
	char* paraFilePathName = argv[7];
	int sampling = atoi(argv[8]);
	int upper_th = atoi(argv[9]);
	int lower_th = atoi(argv[10]);
	
	ReconstructOctree octree(numNodesOnOctree, fullsize, Unit, silPrefix, numImgUsed, ".bmp", paraFilePathName, upper_th, lower_th);
	vector< Point* > myobj;
	testOctree(&octree, fullsize/sampling, myobj);
	cout << "output voxels..." << endl;
	outputVoxels(myobj, argv[11], atof(argv[12]));
	return 0;
}
