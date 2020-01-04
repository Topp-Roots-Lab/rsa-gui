// This program will safely run rsa-renameorig.py as the privileged (setuid) user.
// Make sure that the owner of this program is the privileged user. Then, set the setuid bit on this program using "chmod".

#include <iostream>
#include <cstdlib>
#include <unistd.h>
#include <pwd.h>

using namespace std;

int main(int argc, char *argv[])
{
  clearenv();

  const uid_t euid = geteuid();
  const passwd *password = getpwuid(euid);

  // NOTE(tparker): During the upgrade of Viper from CentOS 6, I encountered
  // malloc(): memory corruption errors when trying to run this code. I think
  // it's because the size of the new arguments array should be one more because
  // the arguments appear to be shifted right by 1 after setting the first and
  // second arguments to the script whose permissions it's elevating
  char ** const new_argv = new char *[argc + sizeof(char*)];
  new_argv[0] = (char * const) "python2";
  new_argv[1] = (char * const) "/opt/rsa-gia/bin/file-handlers/rsa-renameorig.py";
  for (int i = 1; i <= argc; ++i)
  {
    new_argv[i + 1] = argv[i];
  }
  
  cout << "Elevated Launcher: Running rsa-renameorig.py with " << password->pw_name << " permissions." << endl;
  execv("/usr/bin/python2", new_argv);
}
