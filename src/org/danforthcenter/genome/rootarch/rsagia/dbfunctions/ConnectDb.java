package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.swing.*;
import java.sql.Connection;

import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism.ORGANISM;

public class ConnectDb
{
    private static ConnectDb singleConnection = null;

    private MysqlDataSource mds;
    private String dbServer;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private Connection conn;
    private DSLContext dslContext;

    private ConnectDb()
    {
    }

    public void setDbServer(String dbServer)
    {
        this.dbServer = dbServer;
    }

    public void setDbName(String dbName)
    {
        this.dbName = dbName;
    }

    public void setDbCredentials(String dbUser, String dbPassword)
    {
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void connect()
    {
        this.mds = new MysqlDataSource();
        this.mds.setServerName(this.dbServer);
        this.mds.setDatabaseName(this.dbName);
        this.mds.setUser(this.dbUser);
        this.mds.setPassword(this.dbPassword);

        boolean connectionSucceeded = false;

        try
        {
            this.conn = this.mds.getConnection();
            this.dslContext = DSL.using(this.conn, SQLDialect.MYSQL);
            connectionSucceeded = true;
        }
        catch (Exception e)
        {
        }

        if (!connectionSucceeded)
        {
            final String errorMessage = "Could not connect to DB " + this.dbName + " on " + this.dbServer + ".";
            JOptionPane.showMessageDialog(null, errorMessage, "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
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
        if (instance.dslContext == null)
        {
            instance.connect();
        }
        return instance.dslContext;
    }
}
