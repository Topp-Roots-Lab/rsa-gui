package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;

import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism.ORGANISM;

public class ConnectDb
{
    private static ConnectDb singleConnection = null;

    private MysqlDataSource mds;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbServer;
    private DSLContext dslContext;
    private Connection conn;

    private ConnectDb()
    {
        this.mds = new MysqlDataSource();
        this.dbName= "rsa_gia";
        this.dbUser= "rsa-gia";
        this.dbPassword = "rsagia";
        //this.dbServer = "mercury.bioinformatics.danforthcenter.org";
        this.dbServer = "localhost";
        mds.setDatabaseName(this.dbName);
        mds.setUser(this.dbUser);
        mds.setPassword(this.dbPassword);
        mds.setServerName(this.dbServer);

        try
        {
            this.conn = mds.getConnection();
            this.dslContext = DSL.using(conn, SQLDialect.MYSQL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        return ConnectDb.getInstance().dslContext;
    }
}
