package org.danforthcenter.genome.rootarch.rsagia2;

import com.sun.jna.Platform;
import java.lang.reflect.Method;

public class UserAccess
{
    private static int originalUid = -1;

    private static int privilegedUid = -1;

    private static String username = null;

    public static String getCurrentUser()
    {
        if (username != null)
        {
            return username;
        }

        String className = null;
        String methodName = null;

        if (Platform.isWindows())
        {
            className = "com.sun.security.auth.module.NTSystem";
            methodName = "getName";
        }
        else
        {
            className = "com.sun.security.auth.module.UnixSystem";
            methodName = "getUsername";
        }

        try
        {
            Class<?> c = Class.forName(className);
            Method method = c.getDeclaredMethod(methodName);
            Object o = c.newInstance();
            username = (String) method.invoke(o);
        }
        catch (Exception e)
        {
            throw new UserAccessException("Could not get user name from system");
        }

        return username;
    }

    public static void elevatePrivileges()
    {
        if (Platform.isWindows())
        {
            return;
        }

        populateUids();
        CLibrary.INSTANCE.seteuid(privilegedUid);
    }

    public static void reducePrivileges()
    {
        if (Platform.isWindows())
        {
            return;
        }

        populateUids();
        CLibrary.INSTANCE.seteuid(originalUid);
    }

    private static void populateUids()
    {
        if (Platform.isWindows())
        {
            return;
        }

        if (originalUid < 0)
        {
            originalUid = CLibrary.INSTANCE.getuid();
        }
        if (privilegedUid < 0)
        {
            privilegedUid = CLibrary.INSTANCE.geteuid();
        }
    }

    private static class UserAccessException extends RuntimeException
    {
        public UserAccessException(String msg)
        {
            super(msg);
        }
    }
}
