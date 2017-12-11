package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;

import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism.ORGANISM;

public class ConnectDb {
    private MysqlDataSource mds;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbServer;
    private DSLContext dslContext;
    private Connection conn;

    public ConnectDb()
    {
        this.mds = new MysqlDataSource();
        this.dbName= "rsa_gia";
        this.dbUser= "rsa-gia";
        this.dbPassword = "rsagia";
        this.dbServer = "localhost";
        mds.setDatabaseName(this.dbName);
        mds.setUser(this.dbUser);
        mds.setPassword(this.dbPassword);
        mds.setServerName(this.dbServer);

        try {
            this.conn = mds.getConnection();
            this.dslContext = DSL.using(conn, SQLDialect.MYSQL);
            Result<Record> result = dslContext.select().from(ORGANISM).fetch();
            Object K=result.getValue(0,"organism_name");
            int O=9;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DSLContext getDslContext() {
        return dslContext;
    }
}
