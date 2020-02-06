/*
 This header file contains all data objects exported by gatech roots package
*/
#ifndef GATECH_ROOTS_DATAOBJECTS_H
#define GATECH_ROOTS_DATAOBJECTS_H

#include <iostream>

#include "cvimagewrapper.h"

class RawImage : public CvImageWrapper {                              
public:                        
    RawImage () : CvImageWrapper() {}                  
    RawImage (CvImageWrapper::PDataType z) : CvImageWrapper(z) {}   
    string birthTags () { return "image;input"; }
    BN_DECLARE_DATA_WRAPPER(RawImage)
};

BN_DECLARE_DATA_SUBTYPE(AggregateMinImage, CvImageWrapper, "image;aggregate");
BN_DECLARE_DATA_SUBTYPE(AggregateMaxImage, CvImageWrapper, "image;aggregate");
BN_DECLARE_DATA_SUBTYPE(CroppedImage, CvImageWrapper, "image");
BN_DECLARE_DATA_SUBTYPE(CleanedImage, CvImageWrapper, "image");
BN_DECLARE_DATA_SUBTYPE(GrayImage, CvImageWrapper, "image");
BN_DECLARE_DATA_SUBTYPE(CannyImage, CvImageWrapper, "image");
BN_DECLARE_DATA_SUBTYPE(ThresholdedImage, CvImageWrapper, "image");
BN_DECLARE_DATA_SUBTYPE(ThinnedImage, CvImageWrapper, "image");

BN_DECLARE_DATA_SUBTYPE(PerimeterFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(MinorEllipseAxesFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(MajorEllipseAxesFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(EllipseAxesAspectRatioFeature, BasicDataWrapper<double>, "");
BN_DECLARE_DATA_SUBTYPE(NetworkConvexAreaFeature, BasicDataWrapper<double>, "scalable;planar");
BN_DECLARE_DATA_SUBTYPE(NetworkAreaFeature, BasicDataWrapper<double>, "scalable;planar");
BN_DECLARE_DATA_SUBTYPE(SolidityFeature, BasicDataWrapper<double>, "");
BN_DECLARE_DATA_SUBTYPE(BushinessFeature, BasicDataWrapper<double>, "");
BN_DECLARE_DATA_SUBTYPE(VolumeFeature, BasicDataWrapper<double>, "scalable;volumetric");
BN_DECLARE_DATA_SUBTYPE(AverageRootWidthX2Feature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(SpecificRootLengthFeature, BasicDataWrapper<double>, "scalable;invplanar");
BN_DECLARE_DATA_SUBTYPE(MedianNumberOfRootsFeature, BasicDataWrapper<double>, "");
BN_DECLARE_DATA_SUBTYPE(MaximumNumberOfRootsFeature, BasicDataWrapper<double>, "");

//Olga's algorithm
BN_DECLARE_DATA_SUBTYPE(TotalLengthFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(SurfaceAreaFeature, BasicDataWrapper<double>, "scalable;planar");
BN_DECLARE_DATA_SUBTYPE(MaxWidthFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(DepthFeature, BasicDataWrapper<double>, "scalable;linear");
BN_DECLARE_DATA_SUBTYPE(WidthDepthRatioFeature, BasicDataWrapper<double>, "");
BN_DECLARE_DATA_SUBTYPE(LengthDistrFeature, BasicDataWrapper<double>, "");


BN_DECLARE_DATA_SUBTYPE(CComponentsCountFeature, BasicDataWrapper<double>, "");

//number of pixels in cm/inch
BN_DECLARE_DATA_SUBTYPE(MetricScaleParameter, BasicDataWrapper<double>, "");

class CroppingRect : public BnDataWrapper<CvRect> {
public:
	CroppingRect () { }
	CroppingRect (CvRect *rect) : BnDataWrapper<CvRect>(rect) { }
    
	virtual ~CroppingRect () { }
    
	virtual void serialize(string & value, const map<string,string> & config) {
        if (!data())
            return;
        stringstream ss;
        ss << data()->x << " " << data()->y << " " << data()->width << " " << data()->height;
        value = ss.str();
    }
    
	virtual void deserialize(const string & value, const map<string,string> & config) {
        CvRect * rect = new CvRect();
        stringstream ss(value);
        ss >> rect->x >> rect->y >> rect->width >> rect->height;
        setData(rect);
	}
    
    virtual string birthTags () {
        return "input;";
    }
    
	BN_DECLARE_DATA_WRAPPER(CroppingRect);
};

#endif //GATECH_ROOTS_DATAOBJECTS_H
