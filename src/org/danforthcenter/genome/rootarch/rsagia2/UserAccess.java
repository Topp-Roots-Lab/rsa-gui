package org.danforthcenter.genome.rootarch.rsagia2;

import java.lang.reflect.Method;
import com.sun.javafx.PlatformUtil;
import org.jooq.Record;
import org.jooq.Result;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.UserDBFunctions;

public class UserAccess
{
    private static String username = null;
    private static UserDBFunctions udf = new UserDBFunctions();

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
            throw new UserAccessException("Could not get username from system");
        }

        return username;
    }

    public static String getCurrentAccessLevel()
    {
        Result<Record> userRecord = udf.findUserFromName(getCurrentUser());
        if (userRecord.size() == 0)
        {
            return null;
        }
        else
        {
            return (String) userRecord.getValue(0, "access_level");
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
