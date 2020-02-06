/*
 * bnpdatawrapper.h
 *
 *  Created on: Jul 14, 2009
 *      Author: taras
 */

#ifndef BNDATAWRAPPER_H_
#define BNDATAWRAPPER_H_

#include "bnpserializationinfo.h"
#include "loglevel.h"

#include <string>
#include <iomanip>
#include <sstream>
#include <list>
#include <set>
#include <typeinfo>
#include <ctime>
#include <memory>

#include <boost/shared_ptr.hpp>
#include <boost/make_shared.hpp>

using namespace boost;

using namespace std;

namespace BioNic {
    
    namespace Kernel {
        class BnLogger;
        class BnConfigurationManager;
    }
    
    /**
     * Type name declaration macros.
     */
#define BN_DECLARE_DATA_WRAPPER(x) \
public: \
virtual string getTypeName () \
{ return normalizeName(#x); };  \
static string getTypeNameStatic () \
{ return normalizeName(#x); };  \
static x *createNewInstanceStatic()	\
{ return new x(); }                         \
virtual BnDataWrapper<void> *createNewInstance() \
{ return x::createNewInstanceStatic(); } \
static BnDataWrapper<void> *descriptor () \
{ return (BnDataWrapper<void>*) new x(); } 
    
#define BN_DECLARE_DATA_SUBTYPE(x, y, tag)      \
class x : public y                      \
{                               \
public:                         \
x () : y() {}                   \
x (y::PDataType z) : y(z) {}    \
string birthTags () { return tag; } \
string supportedFileExt () { return ""; } \
BN_DECLARE_DATA_WRAPPER(x)     \
};
    
    /**
     * Type name retrieval routine.
     */
#define BN_TYPE_OF(x) x::getTypeNameStatic()
    
    struct GiaObjectMetadata : public map<string, string> {
    public:        
        void clearTags();
        set<string> tags();
        void addTag (const string & value);
        void removeTag (const string & value);
        bool containsTag (const string & value);
    };
    
    //forward declaration to provide void specialization
    template <typename T> class BnDataWrapper;
    typedef list< shared_ptr<BnDataWrapper<void> > > BnDataWrapperList;
    
    //void specialization
    template <>
    class BnDataWrapper<void> {
    private:
//        BnDataWrapper(const BnDataWrapper &copy);
        BnDataWrapper & operator = (const BnDataWrapper & copy);
    protected:
        BnDataWrapper();
        BnDataWrapper(void * copy);
        
        void logMsg(LogLevel level, string fmt, ...);
    public:
        virtual ~BnDataWrapper();
        
        BnpExecutionHistoryRecord* getHistory();
        void setHistory(BnpExecutionHistoryRecord history);
        GiaObjectMetadata * getMetadata ();
        void setMetadata (GiaObjectMetadata &metadata);
        
        const string & getSourceId();
        void setSourceId(const string & id);
        const string & getObjectId();
        void setObjectId(const string & id);
        DataRole getDataRole ();
        void setDataRole (DataRole dataType);
        void setExecutionTime(clock_t msecs);
        void setLogger(Kernel::BnLogger *logger);
        char* formatMsg(char *fmt, ... );
        
        void * getData() const;
        void setData(void * data);
        
        bool isCompliant(BnDataWrapper<void> * other);
        string generateDataFileName ();
        virtual string birthTags () { return ""; }
        virtual string supportedFileExt () { return ""; };
        virtual string getTypeName () = 0;
        virtual BnDataWrapper<void> * createNewInstance() = 0;
        
        virtual void serialize(string & value, const map<string,string> & config) = 0;
        virtual void deserialize(const string & value, const map<string,string> & config) = 0;
        void readSerializationInfo(shared_ptr<GiaDataHeader> info, const map<string,string> & config);
        void writeSerializationInfo(shared_ptr<GiaDataHeader> info, const map<string,string> & config, bool headerOnly = false);

        static string normalizeName(const string & name, bool data = true);
        
        virtual long long getMemorySize ();
        
    private:
        void * data;
        Kernel::BnLogger *logger;
        string sourceId;
        string objectId;
        BnpExecutionHistoryRecord history;    
        GiaObjectMetadata metadata;
        DataRole dataRole;
    };
    
    /**
     * Base class representing a data object concept in BNP Framework.
     * Implements common functionality among all data objects: serialization/deserialization, execution path that lead to this result.
     * Deriving classes should use BN_DECLARE_TYPE_NAME to register themselves as valid data objects of BNP framework.
     * All data objects need to implement this interface to allow serialization/deserialization.
     */
    template <typename T>
    class BnDataWrapper : public BnDataWrapper<void> {
    public:
        typedef T DataType;
        typedef DataType* PDataType;
    private:
        // Disabling copy constructor, we don't want to have a roller coaster with resources deallocation.
//        BnDataWrapper(const BnDataWrapper &copy);
        // Disabling assignment operator, we don't want to have a roller coaster with resources deallocation.
        BnDataWrapper & operator = (const BnDataWrapper & copy);
    protected:
        BnDataWrapper();
        BnDataWrapper(PDataType copy);
    public:
        virtual ~BnDataWrapper();

        PDataType data() const;
        void setData(PDataType data);

        virtual string getTypeName () = 0;
        
        bool isCompliant(BnDataWrapper<void> * other);
        virtual BnDataWrapper<void> * createNewInstance() = 0;
    };
    
    //=================================================================================================
    
    template <typename T>
    BnDataWrapper<T>::BnDataWrapper() : BnDataWrapper<void>() {
    }
    
    template <typename T>
    BnDataWrapper<T>::BnDataWrapper(PDataType copy) : BnDataWrapper<void>(copy) {
    }
    
    template <typename T>
    BnDataWrapper<T>::~BnDataWrapper() {
    }

    template <typename T>
    T* BnDataWrapper<T>::data() const {
        return (T*)BnDataWrapper<void>::getData();
    }
    
    template <typename T>
    void BnDataWrapper<T>::setData(PDataType data) {
        BnDataWrapper<void>::setData(data);
    }

    template <typename T>
    class BasicDataWrapper : public BnDataWrapper<T> {
    public:
        typedef T DataType;
        typedef DataType* PDataType;
        
    protected:
        BasicDataWrapper(PDataType copy) : BnDataWrapper<T>(copy) { }
    public:
        BasicDataWrapper() : BnDataWrapper<T>() { }
        virtual ~BasicDataWrapper() { }
        void serialize(string & value, const map<string,string> & config);
        void deserialize(const string & value, const map<string,string> & config);
        
        static string getTypeNameStatic () { return "error"; }
        
        bool isCompliant(BnDataWrapper<void> * other);
        virtual BnDataWrapper<void> * createNewInstance() {
            return 0;
        }
    };
    
    template <typename T>
    void BasicDataWrapper<T>::serialize(string & value, const map<string,string> & config) {
        stringstream ss;
        ss << setprecision(10) << *BnDataWrapper<T>::data();
        value = ss.str();
    }
    
    template <typename T>
    void BasicDataWrapper<T>::deserialize(const string & value, const map<string,string> & config) {
        stringstream ss(value);
        DataType temp;
        ss >> temp;
        setData(new DataType(temp));            
    }
}

#endif /* BNDATAWRAPPER_H_ */
