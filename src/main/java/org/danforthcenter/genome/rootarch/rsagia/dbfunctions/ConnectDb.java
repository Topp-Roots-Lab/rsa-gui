package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.swing.*;
import java.sql.Connection;

public class ConnectDb
{
    private static ConnectDb singleConnection = null;

    private MysqlDataSource mds;
    private Connection conn;
    private DSLContext dslContext;

    private ConnectDb()
    {
        this.mds = new MysqlDataSource();
    }

    public void setDbServer(String dbServer)
    {
        this.mds.setServerName(dbServer);
    }

    public void setDbName(String dbName)
    {
        this.mds.setDatabaseName(dbName);
    }

    public void setDbCredentials(String dbUser, String dbPassword)
    {
        this.mds.setUser(dbUser);
        this.mds.setPassword(dbPassword);
    }

    public void connect()
    {
        boolean connectionSucceeded = false;
        String exception_message = "";
        try
        {
            this.conn = this.mds.getConnection();
            this.dslContext = DSL.using(this.conn, SQLDialect.MYSQL);
            connectionSucceeded = true;
        }
        catch (Exception e)
        {
            exception_message = e.toString();
        }

        if (!connectionSucceeded)
        {
            System.out.println(exception_message);
            final String errorMessage = "Could not connect to DB " + this.mds.getDatabaseName() + " on " + this.mds.getServerName() + ".\n" + "Details: " + exception_message;
            JOptionPane.showMessageDialog(null, errorMessage, "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public boolean isConnectionValid()
    {
        boolean isValid = false;
        try
        {
            isValid = this.conn.isValid(0);
        }
        catch (Exception e)
        {
        }
        return isValid;
    }

    public static ConnectDb getInstance()
    {
        if (singleConnection == null)
        {
            singleConnection = new ConnectDb();
        }

        return singleConnection;
    }

    public static DSLContext getDslContext()
    {
        ConnectDb instance = ConnectDb.getInstance();
        if (instance.dslContext == null || !instance.isConnectionValid())
        {
            instance.connect();
        }
        return instance.dslContext;
    }
}
