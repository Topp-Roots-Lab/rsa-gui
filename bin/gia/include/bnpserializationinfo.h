/*
 * SerializationInfo.h
 *
 *  Created on: Jun 22, 2009
 *      Author: taras
 */

#ifndef BNPSERIALIZATIONINFO_H_
#define BNPSERIALIZATIONINFO_H_

#include <list>
#include <map>
#include <string>
#include <sstream>
#include <iomanip>
#include <ctime>

using namespace std;

namespace BioNic {
    
    enum DataRole {
        drProd,
        drPreview,
        drUnknown
    };    
    
    struct BnpExecutionHistoryRecord {
        string typeName;
        string algorithmName;
        string algorithmVersion;
        clock_t executionTime;
        map<string,string> parameters;
        map<string,string> messages;
        map<string,string> inputIds;
    };    
    
    struct GiaObjectMetadata;
    
    /**
     * Structure that holds serialization info that can be used for storage/retrieval of objects in/from external memory.
     */
    class GiaDataHeader : public std::map<string,string> {
    public:
        /**
         * Initializes empty serialization structure.
         */
        GiaDataHeader ();
        virtual ~GiaDataHeader ();
        
        bool isModified();
        void setModified(bool modified = true);
        
        bool hasProperty (const string & property) const;
        
        /**
         * Set the value of property with specified name.
         */
        template <typename T>
        void setProperty(const string & propertyName, const T & propertyValue);
        
        /**
         * Get the value of property with specified name.
         */
        template <typename T>
        T getProperty(const string & propertyName) const;
        
        /**
         * Get the list of properties having some values.
         */
//        map<string, string> getProperties();
        
        list<string> getPropertiesPrefixed(const string & prefix);
        
        const string& getObjectId();
        void setObjectId(const string& value);
        const string& getSourceId();
        void setSourceId(const string& value);
        const string& getTypeName();
        void setTypeName(const string& value);
        DataRole getDataRole ();
        void setDataRole (DataRole dataType);
        
        GiaObjectMetadata getMetadata ();
        
        bool isArchived();
        void setArchived(bool value);
        
        bool needsResaving ();
        void setNeedsResaving (bool value);
        
        BnpExecutionHistoryRecord getExecutionHistoryRecord();
        
        void printDebug();
    
    private:
        bool modified;
        bool archived;
        bool m_needsResaving;
    };
    
    template <typename T>
    void GiaDataHeader::setProperty(const string & propertyName, const T & propertyValue) {
        ostringstream oss;
        oss << setprecision(10) << propertyValue;
        (*this)[propertyName] = oss.str();
    }
    
    template <typename T>
    T GiaDataHeader::getProperty(const string & propertyName) const {
        if (find(propertyName) == end())
            return T();
        istringstream iss(find(propertyName)->second);
        T value;
        iss >> value;
        return value;
    }

    template <>
    string GiaDataHeader::getProperty(const string & propertyName) const;
    
    
}

#endif /* BNPSERIALIZATIONINFO_H_ */
