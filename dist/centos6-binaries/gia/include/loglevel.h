#ifndef LOGLEVEL_H
#define LOGLEVEL_H

#include <string>

using namespace std;

namespace BioNic {
    
    struct LogLevel {
    private:
        int level;
        char label[100];
    protected:
        LogLevel(int level, const char *label);
    public:
        ~LogLevel();
        
        static LogLevel LLFATAL;
        static LogLevel LLERROR;
        static LogLevel LLREPORT;
        static LogLevel LLWARNING;
        static LogLevel LLINFO;
        static LogLevel LLDEBUG;
        static LogLevel LLCONFIG;
        
        string getLabel();
        int getLevel();
        bool encompasses(LogLevel x);
    };
}

#endif //LOGLEVEL_H