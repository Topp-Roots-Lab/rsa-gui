/*
 *  giaruntime.h
 *  bionic
 *
 *  Created by taras on 16.03.10.
 *  Copyright 2010 Georgia Tech. All rights reserved.
 *
 */

#ifndef GIARUNTIME_H_
#define GIARUNTIME_H_

#include <string>
#include <map>

using namespace std;

#include "bionic.h"

namespace BioNic {

	namespace Kernel {
		class BnProcessingCore;
		struct BnProcessingCoreJob;
		class BnHelper;
		class BnLogger;
		class BnConfigurationManager;
		class BnProject;
		class IDataStorage;
        class BnExecutionRoadMap;
	}

    class GiaRuntime {
    public:
        GiaRuntime (string license, string email);
        virtual ~GiaRuntime();
		Kernel::BnProject * getProject();
        
        bool saveChanges ();
        bool loadProject(const string &url);
        bool createProject(const string &url);
        bool uploadImages(const list<string> &files);
        Kernel::BnExecutionRoadMap * createRoadmap();
        string registerRoadmap (Kernel::BnExecutionRoadMap * roadmap);
        bool computePreviewImage (const string & datasetId, const string & resultType, Kernel::BnExecutionRoadMap roadmap, BnConfiguration config, string & resultObjectId);
		bool computeOutput (Kernel::BnExecutionRoadMap roadmap);
        int getCoreJobsRunning ();
        
        Kernel::BnProcessingCore * getCore();
        Kernel::BnLogger * getLogger();
        string getLastError();
        BnConfiguration & getConfiguration();
        BnConfiguration * getPtrConfiguration();
        
        void getCompletedFamilyIds (list<pair<string,string> > & roadmapFamilyIds);
        void getResults (const string & roadmapId, const string & datasetId, list<string> & ids);
        
        string getDataImageFilename (const string & dataId);
        
        bool exportResults (const string & filename, list<string> _types, const string & scope, map<string,string> naming);
        bool exportResultsImg (const string & path, list<string> _types, const string & scope);
        
        void rememberConfiguration ();
        void restoreConfiguration ();
        
    protected:
        set<string> getDataRoles (list<string> objectIds);
        
		static void jobFinalization(Kernel::BnProcessingCoreJob * job);
        
    private:
        static GiaRuntime * instance;
        Kernel::BnProject *project;
        Kernel::BnLogger *logger;
        Kernel::BnProcessingCore *core;
        Kernel::BnConfigurationManager *confManager;
        BnConfiguration configuration;
        BnConfiguration storedConfig;
		list<Kernel::IDataStorage*> exporters;
        Kernel::IDataStorage *storage;
        string lastError;
    };
}

#endif //GIARUNTIME_H_