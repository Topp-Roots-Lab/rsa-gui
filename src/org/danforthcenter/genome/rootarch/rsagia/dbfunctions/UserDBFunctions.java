package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

public class UserDBFunctions {
    public Result<Record> findUserFromName(String userName)
    {
        String query = "select * from user where user_name = '" + userName +"'";
        Result<Record> userRecord = ConnectDb.getDslContext().fetch(query);
        return userRecord;
    }
    public Result<Record> findUserFromID(int userID)
    {
        Result<Record> userRecord = ConnectDb.getDslContext().fetch("select * from user where user_id=" + userID);
        return userRecord;
    }
}
