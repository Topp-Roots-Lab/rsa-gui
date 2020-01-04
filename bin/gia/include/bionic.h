/*
 This header file contains all references required for creation of custom 
 algorithm. This also includes possibility of inclusion of custom data wrapper.
*/
#ifndef BIONIC_H
#define BIONIC_H

#include <map>
#include <set>
#include <list>
#include <string>
#include <cstring>

using namespace std;

//#define RES_FOLDER(x) ("giaroots.app/Contents/Resources/"+string(x))
#define RES_FOLDER(x) (string(x))

#define GIA_EMAIL "gia-roots@biology.gatech.edu"

namespace BioNic {
    typedef std::map<std::string, std::string> BnDefaultAlgorithms;
    typedef std::set<std::string> BnpGoals;
}

#include "bnpalgorithm.h"
#include "giatransformation.h"
#include "bnpdatawrapper.h"
#include "bnpserializationinfo.h"
#include "bionicrunner.h"
#include "giaruntime.h"

#endif //BIONIC_H
