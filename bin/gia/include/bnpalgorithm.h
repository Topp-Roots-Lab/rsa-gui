/*
 * BnAlgorithm.h
 *
 *  Created on: Jun 26, 2009
 *      Author: taras
 */
#ifndef BNPALGORITHM_H_
#define BNPALGORITHM_H_

#include "bnpdatawrapper.h"
#include "bnpconfiguration.h"

#include <list>
#include <map>
#include <string>
#include <ctime>
#include <sstream>

#include <boost/shared_ptr.hpp>
#include <boost/make_shared.hpp>

using namespace boost;
using namespace std;

namespace BioNic {
    
    namespace Kernel {
        class BnLogger;
        class BnConfigurationManager;
    }
    
#ifdef WIN32
#define BN_DECLARE extern "C" __declspec(dllexport)
#else
#define BN_DECLARE extern "C"
#endif
    
    class GiaOperator {
    public:
        GiaOperator (string name);
        virtual ~GiaOperator();
        string getName ();
        void setConfig(BnConfiguration config);
        void setLogger(Kernel::BnLogger * logger);
        
    protected:
        void logMsg(LogLevel level, string fmt, ...);
        //Implementations of this abstract class will use this function to ask for the values of required parameters registered in their child constructors.
        //User input is always passed as string and it's up to child class to convert it to required representation.
        template<typename P> P getParameter(string parameterName);
		template<typename P> P getParameter(string parameterName, P defaultValue);        
        template<typename P> void rememberGuessedParameter(string parameterName, P parameterValue);
        Kernel::BnLogger * getLogger();
        map<string,string> & getRequiredParameters();
        map<string,string> & getGuessedParameters();
        char * formatMsg(char *fmt, ... );
        
    private:
        map<string, string> requiredParameters;
        map<string, string> guessedParameters;
        string name;
        Kernel::BnLogger *logger;
        BnConfiguration configuration;        
    };
    
    //forward declaration
    class BnAlgorithm;
    
    typedef list< GiaOperator* > BnAlgorithmsList;
    
    typedef BnAlgorithm *(*PNewAlgorithmFn)();
    
    /**
     * Class that represents an Algorithm concept. It is the base class for every specific algorithm, user wants to implement.
     * Plugin in BNP Framework is a dynamic linking library containing (a) specific Algorithm implementation,
     * (b) factory method that creates instance of Algorithm.
     * NB Because of compiler problems, template class implementations are included into header file.
     * However they still will be compiled into separate dynamic library.
     */
    class BnAlgorithm : public GiaOperator {
    public:
        BnAlgorithm(const string & name);
        virtual ~BnAlgorithm();
        /**
         * All claims of required input nodes and parameters should be included in this function.
         * Deriving class is expected to call function of derived class.
         * Processing core will execute this function after construction and before the plugin is going to be used.
         */
        virtual void initialize() = 0;
        list<string> getRequiredInputKeys();
        list<string> getProducedOutputKeys();
        void setRequiredInput(shared_ptr<BnDataWrapper<void> > data);
        shared_ptr<BnDataWrapper<void> > getProducedOutput(const string & typeName);
        BnDataWrapperList getMockedWrappers();
        BnpExecutionHistoryRecord getExecutionHistoryRecord ();
        /**
         * This method should be implemented by children
         * Executes the algorithm routine given that all input data and parameters were supplied to object.
         * Execution is performed in synchronous manner.
         */
        virtual void execute () = 0;
        
    protected:
        /**
         * Deriving classes should use this function to claim dependence of certain nodes (by name of type). Which will be supplied by processing core before execution.
         * This function should be called by deriving classes from initialize function.
         */
        template<class P> void requiresInput();
        /**
         * Implementations of this abstract class will use this function to ask for the values of required inputs registered
         * in their child constructors.
         * It's up to child class to perform correct dynamic cast after obtaining data object.
         */
        template<class P> P* getRequiredInput();
        template<class P> void producesOutput();
        void setProducedOutput (shared_ptr<BnDataWrapper<void> > output, void * data);
        
        template<class PDataWrapper, typename P>
        void setProducedOutput (P data);
        
        template<class PDataWrapper, typename P>
        void setProducedOutput (P* data);
        
    private:
        void populateKeys(const map<string, shared_ptr<BnDataWrapper<void> > > & source, list<string> & keys);
        
        /**
         * Internal list of input data required by plugin to compute result.
         * This list will be populated by this base class after child class will ask for required inputs in constructor.
         * Child classes can then access the value of required inputs using getRequiredInput function.
         */
        map<string, shared_ptr<BnDataWrapper<void> > > requiredInputs;
        map<string, shared_ptr<BnDataWrapper<void> > > producedOutputs;
        map<string, shared_ptr<BnDataWrapper<void> > > mockedInputs;
        map<string, shared_ptr<BnDataWrapper<void> > > mockedOutputs;
        list<string> requiredInputKeys;
        list<string> producedOutputKeys;
    };
    
    template<class P>
    void BnAlgorithm::requiresInput() {
        if (!requiredInputKeys.empty()) {
            cout << "Required inputs should be requested during initialization phase. See documentation" << endl;
			throw formatMsg("Required inputs should be requested during initialization phase. See documentation");
        }
        string temp = string(P::getTypeNameStatic());
        if (requiredInputs.find( temp ) == requiredInputs.end()) {
            requiredInputs.insert(make_pair( temp, shared_ptr<BnDataWrapper<void> >() ));
            mockedInputs.insert(make_pair( temp, shared_ptr<BnDataWrapper<void> > (P::createNewInstanceStatic()) ));
        }
    }
    
    template<class P>
    P* BnAlgorithm::getRequiredInput() {
        if (requiredInputs.find( string(P::getTypeNameStatic()) ) == requiredInputs.end()) {
            cout << formatMsg("Requested required {%s} input not registered during initialization. See documentation", P::getTypeNameStatic().c_str()) << endl;
			throw formatMsg("Requested required {%s} input not registered during initialization. See documentation", P::getTypeNameStatic().c_str());
        }
        P *wrapper = (P*)(requiredInputs[string(P::getTypeNameStatic())].get());
        if (!wrapper) {
            cout << formatMsg("Looks like algorithm will receive null instead of input. {%s} type failed", P::getTypeNameStatic().c_str()) << endl;
			throw formatMsg("Looks like algorithm will receive null instead of input. {%s} type failed", P::getTypeNameStatic().c_str());
        }
        else if (!wrapper->getData()) {
            cout << formatMsg("Looks like algorithm will receive null DATA instead. {%s} type failed (wrapper itself is valid)", P::getTypeNameStatic().c_str()) << endl;
            throw formatMsg("Looks like algorithm will receive null DATA instead. {%s} type failed (wrapper itself is valid)", P::getTypeNameStatic().c_str());
        }
        return wrapper;
    }
    
    template<class P>
    void BnAlgorithm::producesOutput() {
        if (!producedOutputKeys.empty()) {
            cout << formatMsg("Produced outputs should be specified during initialization phase. See documentation") << endl;
            throw formatMsg("Produced outputs should be specified during initialization phase. See documentation");
        }
        string temp = string(P::getTypeNameStatic());
        if (mockedOutputs.find( temp ) == mockedOutputs.end())
            mockedOutputs.insert(make_pair( temp, shared_ptr<BnDataWrapper<void> > (P::createNewInstanceStatic()) ));
    }
    
    template<class PDataWrapper, typename P>
    void BnAlgorithm::setProducedOutput (P data) {
        setProducedOutput(shared_ptr<BnDataWrapper<void> >( PDataWrapper::descriptor() ), new P(data));
    }

    template<class PDataWrapper, typename P>
    void BnAlgorithm::setProducedOutput (P* data) {
        setProducedOutput(shared_ptr<BnDataWrapper<void> >( PDataWrapper::descriptor() ), data);
    }
    
    template<typename P>
    P GiaOperator::getParameter(string parameterName) {
        string thisName = getName();
        P value = configuration.getParameter<P>(getName(), parameterName);
		stringstream ss;
		ss << value;
		requiredParameters[parameterName] = ss.str();
		return value;
    }

	template<typename P>
    P GiaOperator::getParameter(string parameterName, P defaultValue) {
        string thisName = getName();
        P value = configuration.getParameter<P>(getName(), parameterName, defaultValue);
		stringstream ss;
		ss << value;
		requiredParameters[parameterName] = ss.str();
		return value;
    }
    
    template<typename P>
    void GiaOperator::rememberGuessedParameter(string parameterName, P parameterValue) {
        ostringstream ss;
        ss << parameterValue;
        guessedParameters[parameterName] = ss.str();
    }
    
}

#endif /* BNPALGORITHM_H_ */
