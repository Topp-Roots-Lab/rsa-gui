/*
 * BnpImageData.h
 *
 *  Created on: Jun 15, 2009
 *      Author: taras
 */

#ifndef CVIMAGEWRAPPER_H_
#define CVIMAGEWRAPPER_H_

#include <bionic.h>

#include <string>

#include <opencv/cv.h>
#include <opencv/highgui.h>

using namespace std;
using namespace BioNic;

/**
 * Example class, represents image data.
 */
class CvImageWrapper : public BnDataWrapper<IplImage> {
public:
	CvImageWrapper () {
	}
    
	CvImageWrapper (IplImage *image) : BnDataWrapper<IplImage>(image) {
	}
    
	virtual ~CvImageWrapper () {
		if (getData()) {
//        printf("unloaded ptr:(%x)\n", this);
			IplImage * image = data();
			cvReleaseImage(&image);
			setData(0);
		}
	}
    
    string supportedFileExt () { return "jpg;tif;tiff;jpeg;jp2;png;cr2;bmp;dib;jpe;dib;pbm;pgm;ppm;"; }
    
	/**
	 * Serialization method. Stores object's properties into SerializationInfo structure.
	 */
	virtual void serialize(string & value, const map<string,string> & config) {
        if (!data())
            return;
        string format = config.find("serialize_image_format")->second;
        value = generateDataFileName() + "." + format;
        cvSaveImage(value.c_str(), data());
	}
    
	/**
	 * Deserialization method. Sets current object's properties to those provided by SerializationInfo structure.
	 */
	virtual void deserialize(const string & value, const map<string,string> & config) {
        IplImage *image = cvLoadImage(value.c_str(), CV_LOAD_IMAGE_ANYCOLOR);
        if (!image) {
            logMsg( LogLevel::LLERROR, "(%s) image failed", value.c_str());
            throw formatMsg("(%s) image failed to load correctly! Check if the BioNic framework was compiled with libjpeg-dev or libtiff-dev or applicable.", value.c_str());
        } else {
            logMsg( LogLevel::LLDEBUG, "(%s) loaded;dim=[%dx%d];ch=[%d];depth=[%d]", value.c_str(), image->height, image->width, image->nChannels, image->depth);
        }
        setData( image );
	}

    long long getMemorySize () {
        if (data())
            return data()->imageSize;
        else
            return 0;
    }
    
	BN_DECLARE_DATA_WRAPPER(CvImageWrapper);
};

#endif /* CVIMAGEWRAPPER_H_ */
