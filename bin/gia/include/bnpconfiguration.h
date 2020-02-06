/*
 * bnpconfiguration.h
 *
 *  Created on: Aug 16, 2009
 *      Author: taras
 */

#ifndef BNPCONFIGURATION_H_
#define BNPCONFIGURATION_H_

#include "loglevel.h"

#include <cstdio>
#include <string>
#include <map>
#include <list>
#include <sstream>
#include <iostream>

using namespace std;

namespace BioNic {
    
    namespace Kernel {
        class BnLogger;
    }
    
    class BnConfiguration : private map<string, map<string, string> > {
    protected:
        virtual void logMsg(Kernel::BnLogger * logger, LogLevel level, string fmt, ... );
		string getParameterString(const string &algorithmId, const string &parameterName, bool optional);
    public:
        BnConfiguration ();
        virtual ~BnConfiguration();
        
        virtual void clear();
        virtual void read(const string &filename, Kernel::BnLogger * logger);
        virtual void write(const string &filename, Kernel::BnLogger * logger);
        bool empty ();
        int size ();

		bool hasParameter(const string &algorithmId, const string &parameterName);

        template<typename T> T getParameter(const string &algorithmId, const string &parameterName);
		template<typename T> T getParameter(const string &algorithmId, const string &parameterName, const T& defaultValue);
        void removeParameter (const string &algorithmId, const string &parameterName);

        template<typename T> void setParameter(const string &algorithmId, const string &parameterName, const T& value);
        
        list<string> getPropertiesPrefixed(const string & dataId, const string & prefix);
        
        void addSection(const string &sectionName, const map<string, string> &section);
        const map<string, string> &getSection(const string &sectionName);
        void removeSection(const string &section);
        
        void logAll(Kernel::BnLogger * logger);
    };

    template<>
    bool BnConfiguration::getParameter(const string &algorithmId, const string &parameterName);

	template<>
    bool BnConfiguration::getParameter(const string &algorithmId, const string &parameterName, const bool& defaultValue);

    template<>
	string BnConfiguration::getParameter(const string &algorithmId, const string &parameterName);
    
    template<>
	string BnConfiguration::getParameter(const string &algorithmId, const string &parameterName, const string& defaultValue);

	template<typename T>
	T BnConfiguration::getParameter(const string &algorithmId, const string &parameterName) {
		stringstream ss(getParameterString(algorithmId, parameterName, false));
        T val;
        ss >> val;
        return val;
	}

	template<typename T>
	T BnConfiguration::getParameter(const string &algorithmId, const string &parameterName, const T& defaultValue) {
		string str = getParameterString(algorithmId, parameterName, true);
		if (str.size() == 0)
			return defaultValue;
		stringstream ss(str);
        T val;
        ss >> val;
        return val;
	}
    
    template<>
    void BnConfiguration::setParameter(const string &algorithmId, const string &parameterName, const bool& value);

    template<typename T>
    void BnConfiguration::setParameter(const string &algorithmId, const string &parameterName, const T& value) {
        stringstream ss;
        ss << value;
        if (this->find(algorithmId) == this->end())
            (*this)[algorithmId] = map<string, string>();
        (*this)[algorithmId][parameterName] = ss.str();
    }
    
}

#endif /* BNPCONFIGURATION_H_ */
