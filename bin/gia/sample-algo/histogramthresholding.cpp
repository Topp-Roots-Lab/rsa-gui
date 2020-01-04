#include <bionic.h>
#include <gatech_roots_dataobjects.h>

#include <vector>
#include <iostream>
#include <numeric>

using namespace BioNic;

class HistogramThresholding : public BnAlgorithm {
public:
	HistogramThresholding() : BnAlgorithm("Histogram Thresholding") {
    }

	void initialize() {
		requiresInput<GrayImage >();
		producesOutput<ThresholdedImage>();
	}

	void execute() {
		IplImage *grayImage = getRequiredInput<GrayImage>()->getData();

		double dropBound = getParameter<double>("drop_bound");

		IplImage *result = cvCreateImage(cvGetSize(grayImage), IPL_DEPTH_8U, 1);

        std::vector<int> histogram(256, 0);

        int i, j;
		for (i = 0; i < grayImage->height; ++i)
			for (j = 0; j < grayImage->width; ++j)
				histogram [ cvGetReal2D(grayImage, i, j) ] ++;
        
        // calculating sum of all elements of histogram vectory using STL algorithm
        int area = std::accumulate(histogram.begin(), histogram.end(), 0);
        
        int threshold_low = area * 0.0001;
        
		int bound = 0;
        for (i = 255; i >= 0 && histogram[i] < threshold_low; --i);
        
        bound = i;
        if (!bound)
            bound ++;
        
        logMsg(LogLevel::LLREPORT, "Cutoff point is (%d)", bound);
        
        // looking for a steep edge
        bool foundRequiredDrop = false;
		int prevDifference = histogram[bound-1] - histogram[bound];
		for (i = bound; i > 0; --i) {
			int difference = histogram[i-1] - histogram[i];
			if (difference > dropBound * prevDifference) {
                foundRequiredDrop = true;
				bound = i;
				break;
			}
            prevDifference = difference;
		}
        
        if (!foundRequiredDrop)
            logMsg(LogLevel::LLREPORT, "No drop of difference of size (%f). Try choosing smaller value", dropBound);
        logMsg(LogLevel::LLREPORT, "Chosen boundary intensity value is {%d}", bound);
        rememberGuessedParameter("threshold_boundary", bound);

		cvThreshold(grayImage, result, bound, 255, CV_THRESH_BINARY);

		setProducedOutput<ThresholdedImage>( result );
	}
};

BN_DECLARE BnAlgorithm* newAlgorithm() {
	return (BnAlgorithm*) new HistogramThresholding();
}

int main() {
    BionicRunner runner(newAlgorithm);
    
    BnDataWrapper<void> *image = runner.loadDataWrapper("./jobs/grayimages/test.dataset", "grayimage_kr7hpxrtv0l");
    BnDataWrapperList inputs;
    inputs.push_back(image);
    
    runner.setParameter("drop_bound", 6.0);
    BnDataWrapperList outputs = runner.test(inputs);
    runner.saveDataWrappersList(outputs, "./jobs/histogram-test.dataset");
    
    runner.saveUsedConfigurationAsExample("./jobs/histogram-daemon.job");
    return 0;
};
