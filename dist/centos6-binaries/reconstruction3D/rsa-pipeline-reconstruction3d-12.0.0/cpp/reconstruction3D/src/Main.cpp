#include "ReconstructOctree.h"
#include "connect.h"

#include <sstream>

using namespace std;

const int MAX_DISTORTION_RADIUS = 40;
const double RATIO_STEP = 0.01;
const double MIN_REF_RATIO = 0.95;

class NullOctree : public Octree
{
public:
    NullOctree(int maxNode, Point fullSize, Point endSize)
        : Octree(maxNode, fullSize, endSize) {}
private:
    unsigned int check(Point lp, Point hp, int &radius, int &nImagesConsistent) {
        nImagesConsistent = 0; return 0;
    } 
};

void outputVoxels(vector< Point* >& myobj, const char* filename, float extrainfo)
{
    ofstream outfile(filename);
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

void outputVoxels(vector< Point* >& myobj, vector< int > &comp_index, 
                    const char* filename, int gr_index, float extrainfo)
{
    ofstream outfile(filename);
    vector< Point* >::const_iterator p_point;
    vector< int >::const_iterator p_comp_index;
    int np = 0;
    for (p_comp_index = comp_index.begin(); p_comp_index != comp_index.end(); 
        ++p_comp_index) {
        if (*p_comp_index == gr_index) np++;
    }
    outfile << extrainfo << endl;
    outfile << np << endl;

    Point* point;
    for (p_point = myobj.begin(), p_comp_index = comp_index.begin(); 
        p_point != myobj.end(); ++p_point, ++p_comp_index) {
        if (*p_comp_index != gr_index) continue;
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

    if (opt!=1 || argc!=16) {
        cout << "Invalid arguments" << endl;
        exit(-1);
    }

/*
//vp
    if (opt == 2) { // show result
        vector< Point* > myobj;
        int imgWidth, imgHeight;
        cout << "loading voxels: " << argv[2] << endl;
        loadVoxels(argv[2], myobj, imgWidth, imgHeight);
        cout << myobj.size() << " voxels loaded, imgwidth = " << imgWidth <<
            ", imgheight = " << imgHeight << "." << endl;

        Point fullsize( imgWidth, imgWidth, imgHeight );
        NullOctree* mytree = new NullOctree(0, fullsize, Point(1,1,1));
        mytree->outputMesh(myobj, mesh);
        GLUTInit(argc,argv, mesh);
        GLUTMainLoop();
        return 0;
    }
*/

    
    int imgWidth = atoi(argv[2]);
    int imgHeight = atoi(argv[3]);
    Point fullsize( imgWidth, imgWidth, imgHeight );
    int numNodesOnOctree = atoi(argv[4]);
    Point Unit(1,1,1);
    char* silPrefix = argv[5];
    int numImgUsed = atoi(argv[6]);
    char* paraFilePathName = argv[7];
    int sampling = atoi(argv[8]);

    //
    // vp
    //
    // if distortion_radius <0,
    // then it can be adjusted in the program based on ref_img and ratio- see comments below 
    //  
    // if distortion_radius >= 0,then the ref_img and ratio are used 
    // to check against the given rotation image (reference image)
    //
    int distortion_radius = atoi(argv[9]);

    int num_components = atoi(argv[10]);
    float extrainfo = atof(argv[12]);
    int rotation_digits = atoi(argv[13]); 

    //
    // vp 
    //
    // passed and used only if distortion_radius <0  - see comments below 
    //
    int ref_img = atoi(argv[14]);

    double ratio = atof(argv[15]);
    cout << "Executing"<< endl;
    cout << "exe=" << argv[0] << endl;
    cout << "Parameters:"<< endl;
    cout << "option=" << argv[1] << endl;
    cout << "imgHeight=" << imgHeight << endl;
    cout << "imgWidth=" << imgWidth << endl;
    cout << "numNodesOnOctree=" << numNodesOnOctree << endl;
    cout << "silPrefix=" << silPrefix << endl;
    cout << "numImgUsed=" << numImgUsed << endl;
    cout << "paraFilePathName=" << paraFilePathName << endl;
    cout << "resolution=" << sampling << endl;
    cout << "distortion_radius=" << distortion_radius << endl;
    cout << "num_components=" << num_components << endl;
    cout << "outputfile=" << argv[11] << endl;
    cout << "extrainfo=" << extrainfo << endl;
    cout << "rotation_digits=" << rotation_digits << endl;
    cout << "ref_img=" << ref_img << endl;
    cout << "ratio=" << ratio << endl;

    cout << "" << endl;
    cout << "NOTE: extrainfo, ref_img, ratio parameters are currently not used" << endl;
    cout << "" << endl;    

    vector< Point* > myobj;
    vector< int > comp_index;
    int nComp;    

    if (distortion_radius >= 0) {
          cout << "construct octree..." << endl;
          ReconstructOctree octree(numNodesOnOctree, fullsize, Unit, silPrefix, 
                                      numImgUsed, ".bmp", paraFilePathName, 
                                      distortion_radius,rotation_digits);
          Point volsize(fullsize/sampling);
          testOctree(&octree, volsize);
  
          cout << "generate candidate voxels..." << endl;
          int npts = octree.getNumVoxel();
          int *consistency = new int[npts];
          int *reliability = new int[npts];
          Point *pts = new Point[npts];
          octree.outputVoxels(consistency, reliability, pts);
  
          cout << "guarantee connectedness..." << endl;
          nComp = guarantee_connectedness(consistency, reliability, pts, npts, &volsize, 100, myobj, comp_index);
          cout << myobj.size() << " voxels, " << nComp << " components." << endl;
          
          cout << "output voxels..." << endl;
          outputVoxels(myobj, argv[11], extrainfo);

         //vp - output components

         for (int i = 0; i < nComp; ++i) {
	         // sprintf_s does not work
	         //
	         // Fix (probably, not the best)
	         //  
	         // makes concatenation filename + number, that argv[11] + "_" + i + 1
	         //
		 std::stringstream sstm;
		 std::string buffer;
		 sstm << argv[11] << "_" << i+1;
		 buffer  = sstm.str();
		 char *fn = new char[buffer.size()+1];
		 fn[buffer.size()]=0;
		 memcpy(fn,buffer.c_str(),buffer.size());
		 //

		 outputVoxels(myobj, comp_index, fn, i, extrainfo);
         }

    }
//
// vp 
//
// to disable pass distortion_radius >=0 
// Here is where the ref_img and ratio are used to check against the given rotation image (reference image)
//
// Checking against the given rotation image does not seem to improve reconstruction too much.
// Sometimes one can see that it will help to reconstruct some finer details 
// (thin branches that are located far from the center). But it is not consistent and 
// depends on the reference image. 
// "Average over all reference images (all rotations)", probably, might help, but there is no such a concept now.
//
//begin///////////////////////////////////////////////////////////////////////
    else {
    
      //while (ratio >= MIN_REF_RATIO) {   
    
      //     cout << "try: REF_RATIO = " << ratio << endl;
  
          if (num_components < 0) {
              cout << "Invalid arguments" << endl;
              exit(-1);
          }

          ReconstructOctree octree(numNodesOnOctree, fullsize, Unit, silPrefix, 
                                            numImgUsed, ".bmp", paraFilePathName, 
                                            distortion_radius, rotation_digits);
          Point volsize(fullsize/sampling);
          distortion_radius = 0;
          int *consistency, *reliability;
          int npts;
          Point *pts;
          bool isGenResult = true;
          while (true) {
             if (distortion_radius > MAX_DISTORTION_RADIUS) {
                  isGenResult = false; 
                  break;
             }
            
              cout << "try: distortion_radius = " << distortion_radius << endl;
  
              cout << "construct octree..." << endl;
              octree.setDistortionRadius(distortion_radius);
              testOctree(&octree, volsize);
  
              cout << "generate candidate voxels..." << endl;
              npts = octree.getNumVoxel();
              consistency = new int[npts];
              reliability = new int[npts];
              pts = new Point[npts];
              octree.outputVoxels(consistency, reliability, pts);
  
              //cout << "check consistency..." << endl;
              //if (octree.addConsistency(ref_img, consistency, reliability, pts, npts, ratio))
              //{
                    nComp = guarantee_connectedness(consistency, reliability, pts, npts, &volsize, 100, myobj, comp_index);
                    cout << "found: " << nComp << " components." << endl;
                    if (nComp <= num_components) {
                        break;
                    }
              //}
  
              delete[] consistency;
              delete[] reliability;
              delete[] pts;
              distortion_radius++;
          }
//end///////////////////////////////////////////////////////////////////////
  
          if (isGenResult) {
            cout << myobj.size() << " voxels." << endl;
            cout << "output voxels..." << endl;
            outputVoxels(myobj, argv[11], extrainfo);
            //break;
            //vp - output components
            // does not work - cannot find sprintf_s
            //char buffer[200];
            //for (int i = 0; i < nComp; ++i) {
            //  sprintf_s(buffer, 200, "%s_%d", argv[1], i+1);
            //  outputVoxels(myobj, comp_index, buffer, i);
            //}
            cout << "finally found: " << nComp << " components." << endl;
            if(nComp < 2){
            	return 0;
            }    
            for (int i = 0; i < nComp; ++i) {
                // sprintf_s does not work
                //
                // Fix (probably, not the best)
                //  
                // makes concatenation filename + number, that argv[11] + "_" + i + 1
                //
		std::stringstream sstm;
		std::string buffer;
		sstm << argv[11] << "_" << i+1;
		buffer  = sstm.str();
		char *fn = new char[buffer.size()+1];
		fn[buffer.size()]=0;
		memcpy(fn,buffer.c_str(),buffer.size());
		//

		outputVoxels(myobj, comp_index, fn, i, extrainfo);                 
             }
             //break;
          }          
      
      //    ratio -= RATIO_STEP;
      //}
    }
    
    return 0;
}
