/*
 *  giatransformation.h
 *  Project
 *
 *  Created by taras on 07.05.10.
 *  Copyright 2010 Georgia Tech. All rights reserved.
 *
 */

#ifndef GIATRANSFORMATION_H
#define GIATRANSFORMATION_H

#include "bnpalgorithm.h"

namespace BioNic {
    
    namespace Kernel {
        class BnLogger;
        class BnConfigurationManager;
    }
    
    class GiaTransform;
    
    typedef GiaTransform *(*PNewTransformFn)();

    class GiaTransform : public GiaOperator {
    private:
        shared_ptr<BnDataWrapper<void> > result;
    protected:
        virtual void * doTransform (BnDataWrapper<void> * data, set<string> & newTags) = 0;
        void setResult (shared_ptr<BnDataWrapper<void> > result);
    public:
        GiaTransform(const string & name);
        virtual ~GiaTransform();
        shared_ptr<BnDataWrapper<void> > transform (shared_ptr<BnDataWrapper<void> > data);
        virtual void feed (shared_ptr<BnDataWrapper<void> > data);
        
        virtual shared_ptr<BnDataWrapper<void> > getSampleResult ();
        virtual bool needsInput ();
    };
}

#endif // GIATRANSFORMATION_H