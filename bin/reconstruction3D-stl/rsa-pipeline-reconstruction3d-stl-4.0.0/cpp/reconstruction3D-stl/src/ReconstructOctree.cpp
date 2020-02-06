#include "ReconstructOctree.h"
#include <ctime>
#include <math.h>

using namespace std;

ReconstructOctree::ReconstructOctree(int maxNode, Point fullSize, Point endSize, 
									 string sil_prefix, int nfile, 
									 string file_ext, string file_conf, 
									 int upper_th, int lower_th)
	:Octree(maxNode, fullSize, endSize)
{
	readSilhouettes(sil_prefix, nfile, file_ext);
	readConfiguration(file_conf);
	this->upper_threshold = nfile - upper_th;
	this->lower_threshold = nfile - lower_th;
	cout << "Loaded all the images." << endl;
}

void ReconstructOctree::readSilhouettes(string file_prefix, int nfile, string file_ext)
{
	silhouette_stat = new int** [nfile];

	char buffer[10];
	int i, j, count;
	int *iter;
	int **counts;
	for (int file = 0; file < nfile; file++)
	{
		//	read bmp image
		string filename = file_prefix;
		sprintf(buffer, "%d", file+1);
		if (file < 9) filename.append("0");       
		filename.append(buffer);
		filename.append(file_ext);
		bool** myimg = readBlackWhiteImage(filename, image_height, image_width);
		
		
		//	count #. 1's 
		counts = silhouette_stat[file] = new int* [image_height];

		i = 0;
		iter = counts[i] = new int [image_width];
		count = 0;
		for (j = 0; j < image_width; j++)
		{
			if (myimg[i][j] == 1) count++;
			*iter = count;
			iter++;
		}
		
		for (i = 1; i < image_height; i++)
		{
			iter = counts[i] = new int [image_width];
			count = 0;
			for (j = 0; j < image_width; j++)
			{
				if (myimg[i][j] == 1) count++;
				*iter = counts[i-1][j] + count;
				iter++;
			}
		}
	}

	nimages = nfile;
	image_center_x = image_height / 2.0f;
	image_center_y = image_width / 2.0f;
}

bool** ReconstructOctree::readBlackWhiteImage(string filename, int& n, int& m)
{
	CImg<bool> image(filename.c_str());
	n = image.height();
	m = image.width();
	bool **myimg = new bool*[n];
	CImg<bool>::iterator iter = image.begin();
	bool *myiter;
	for (int i = 0; i < n; i++) 
	{
		myiter = myimg[i] = new bool[m];
		for (int j = 0; j < m; j++)
		{
			*myiter = *iter;
			myiter++;
			iter++;
		}
	}
	return myimg;
}

int ReconstructOctree::getSilStat(int image, int lr, int lc, int hr, int hc)
{
	// check image index
	if (image<0 || image>=nimages) return 0;

	// check boundary
	if (lr>hr || lc>hc) return 0;
	if (lr < 0) lr = 0;
	if (lc < 0) lc = 0;
	if (hr < 0) hr = 0;
	if (hc < 0) hc = 0; 
	if (lr>=image_height) lr = image_height-1;
	if (hr>=image_height) hr = image_height-1;
	if (hc>=image_width) hc = image_width-1;
	if (lc>=image_width) lc = image_width-1;

	int** iter = silhouette_stat[image];
	int ret = iter[hr][hc];
	if (lr>0 && lc>0)
		ret += iter[lr-1][lc-1];
	if (lr>0)
		ret -= iter[lr-1][hc];
	if (lc>0)
		ret -= iter[hr][lc-1];
	return ret;
}

void ReconstructOctree::readConfiguration(string file_conf)
{
	ifstream myfile(file_conf.c_str());

	//	get translation and rotation matrix
	Rs = new float* [nimages];
	int i;
	float *iter_rs;
	float tmp;
	for (int image = 0; image < nimages; image++)
	{
		iter_rs = Rs[image] = new float [2];
		for (i = 0; i < 2; i++)
		{
			myfile >> tmp;
			*iter_rs = tmp;
			iter_rs++;
		}
	}
	myfile.close();
}

unsigned int ReconstructOctree::lower_check(Point lp, Point hp, int &nImagesConsistent) 
{
	return check(lp, hp, lower_threshold, nImagesConsistent);
}

unsigned int ReconstructOctree::upper_check(Point lp, Point hp, int &nImagesConsistent)
{
	return check(lp, hp, upper_threshold, nImagesConsistent);
}

unsigned int ReconstructOctree::check(Point lp, Point hp, int threshold, int &nImagesConsistent)
{
	// check whether lp is a lower point than hp
	if (lp.x >= hp.x || lp.y >= hp.y || lp.z >= hp.z) {
		nImagesConsistent = 0; 
		return 0;
	}
	
	int x[2] = {lp.x, hp.x};
	int y[2] = {lp.y, hp.y};
	int z[2] = {hp.z, lp.z};
	
	int lr=0, hr=0, lc=0, hc=0, count;
	int nfail = 0;
	bool isFull = true;
	for (int image = 0; image < nimages; image++)
	{
		perspectiveProject(image, Point(x[chooselx[image]], y[choosely[image]], z[0]), lr, lc);
		perspectiveProject(image, Point(x[choosehx[image]], y[choosehy[image]], z[1]), hr, hc);
		count = getSilStat(image, lr, lc, hr, hc);
		if (isFull && (count == 0 || count < (hr-lr+1)*(hc-lc+1)))
			isFull = false;
		if (count == 0)
		{
			nfail++;
			if (nfail > threshold) break;
		}
	}

	nImagesConsistent = nimages - nfail;
	if (isFull) return 1;
	if (nfail <= threshold) return 2;
	return 0;
}

void ReconstructOctree::perspectiveProject(int image, Point p, int& row, int& col)
{
	float *iter_rs;
	iter_rs = Rs[image];
	row = (int)floor(scales[0]*(-(p.z - halfSize.z)) + image_center_x);
	col = (int)floor(scales[1]*(iter_rs[0] * (p.x - halfSize.x)
						+ iter_rs[1] * (p.y - halfSize.y)) + image_center_y);
}

void ReconstructOctree::setResolution(Point cubeSize)
{
	this->setCubesize(cubeSize);
	halfSize = cubeSize/2;
	scales[0] = 1.0f * fullSize.z / cubeSize.z;
	scales[1] = 1.0f * fullSize.x / cubeSize.x;
	cout << cubeSize.x << " " << cubeSize.y << " " << cubeSize.z << " " 
		<<scales[0] << " " << scales[1] << endl;

	//	compute which to choose to form a rectangle backprojected on the image
	chooselx = new bool[nimages];
	choosely = new bool[nimages];
	choosehx = new bool[nimages];
	choosehy = new bool[nimages];

	int x[2] = {0, fullSize.x};
	int y[2] = {0, fullSize.y};
	int minc = 0, maxc = 0;
	int row=0, col=0;
	for (int image = 0; image < nimages; image++)
	{
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
			{
				perspectiveProject(image, Point(x[i], y[j], 0), row, col);
				if (i == 0 && j == 0 || col < minc)
				{
					minc = col;
					chooselx[image] = (bool)i;
					choosely[image] = (bool)j;
				}
				if (i == 0 && j == 0 || col > maxc)
				{
					maxc = col;
					choosehx[image] = (bool)i;
					choosehy[image] = (bool)j;
				}
			}
	}
}

void ReconstructOctree::dropImage()
{
	for (int i = 0; i < nimages; ++i) {
		for (int j = 0; j < image_height; ++j)
			delete[] silhouette_stat[i][j];
		delete[] silhouette_stat[i];
	}
	delete[] silhouette_stat;
}

int*** ReconstructOctree::directReconstruct()
{
	int ***consistentImgs = new int ** [cubeSize.x];
	int **iter_i, *iter_j;
	int i, j, k;
	unsigned int result;
	int nImageConsistent;
	for (i = 0; i < cubeSize.x; i++)
	{
		iter_i = consistentImgs[i] = new int * [cubeSize.y];
		for (j = 0; j < cubeSize.y; j++)
		{
			iter_j = *iter_i = new int [cubeSize.z];
			for (k = 0; k < cubeSize.z; k++)
			{
				result = upper_check(Point(i,j,k), Point(i+1,j+1,k+1), nImageConsistent);
				*iter_j = nImageConsistent;
				iter_j++;
			}
			iter_i++;
		}
	}
	return consistentImgs;
}

void testDirectConstruct(ReconstructOctree *mytree, Point volsize, string filename, Point cubeSize) 
{
	mytree->setResolution(cubeSize);

	clock_t starttime = clock();

	int ***consistentImgs = mytree->directReconstruct();
	
	clock_t endtime = clock();
	cout << "Total time: " << (endtime-starttime)/CLOCKS_PER_SEC << " seconds." << endl;

	ofstream myfile(filename.c_str());
	for (int i = 0; i < volsize.x; i++)
	{
		for (int j = 0; j < volsize.y; j++)
			for (int k = 0; k < volsize.z; k++)
				myfile << consistentImgs[i][j][k] << " ";
		myfile << endl;
	}
	myfile.close();
}

void testOctree(ReconstructOctree *mytree, Point cubeSize, vector< Point* > &myobj) 
{
	mytree->setResolution(cubeSize);

	clock_t starttime = clock();

	int feedback = mytree->construct();
	if (feedback == -1)
	{
		cout << "please increase #. available octree nodes." << endl;
		exit(-1);
	}
	clock_t endtime = clock();
	cout << "Total time: " << (endtime-starttime)*1.0/CLOCKS_PER_SEC << " seconds." << endl;

	//	find all the voxels on the object
	mytree->outputVoxels(myobj);

	//	BFS expand using lower_threshold
	mytree->expandObject(myobj);
	cout << myobj.size() << " voxels after expansion." << endl;

	mytree->dropOctree();
	mytree->dropImage();
}
