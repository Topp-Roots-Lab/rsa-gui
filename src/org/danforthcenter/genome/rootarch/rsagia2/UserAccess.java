package org.danforthcenter.genome.rootarch.rsagia2;

import java.lang.reflect.Method;
import com.sun.javafx.PlatformUtil;

public class UserAccess
{
    private static String username = null;

    public static String getCurrentUser()
    {
        if (username != null)
        {
            return username;
        }

        String className = null;
        String methodName = null;

        if (PlatformUtil.isWindows())
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

    private static class UserAccessException extends RuntimeException
    {
        public UserAccessException(String msg)
        {
            super(msg);
        }
    }
}
