/*
 * bionicrunner.h
 *
 *  Created on: Oct 2, 2009
 *      Author: taras
 */

#ifndef BIONICRUNNER_H_
#define BIONICRUNNER_H_

#include "bnpdatawrapper.h"
#include "bnpalgorithm.h"
#include "bnpconfiguration.h"

#include <list>
#include <string>
#include <map>
#include <set>

using namespace std;

#define BIONIC_RUNNER_VERSION "20100107"

/*
namespace BioNic {
    
    namespace Kernel {
        class BnProcessingCore;
        class IDataStorage;
        class BnConfigurationManager;
        class BnLogger;
        class BnProject;
    }
    
    class BionicRunner {        
    protected:
        bool validateDataWrapper(BnDataWrapper<void> *dataWrapper, string description);
        void setupTempConfig(BnConfiguration & config);
        
    public:        
        BionicRunner(PNewAlgorithmFn algorithmFactoryFunc);
        virtual ~BionicRunner();
        
        virtual BnDataWrapper<void> *loadDataWrapper(string dataset, string dataid);
        virtual void saveDataWrappersList(BnDataWrapperList wrappers, const string &destDataset, const string &imageFormat = "jpg");
        virtual BnDataWrapperList test(BnDataWrapperList inputs);
        void saveUsedConfigurationAsExample(const string &filename);
        template<typename T> void setParameter(const string &parameterName, const T& value);
        void setVerboseLevel(LogLevel level);
        
    private:
        BnAlgorithm *algorithm;
        list<string> requiredInputKeys;
        list<string> producedOutputKeys;
        BnConfiguration config;
        string tempConfigName;
        Kernel::BnLogger *logger;
        Kernel::BnProcessingCore *core;
        Kernel::IDataStorage *formatter;
        Kernel::BnConfigurationManager *confManager;
        Kernel::BnProject *project;
    };
    
    template<typename T>
    void BionicRunner::setParameter(const string &parameterName, const T& value) {
        config.setParameter<T>(algorithm->getName(), parameterName, value);
    }
    
}
 */

#endif /* BIONICRUNNER_H_ */
