package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia.db.enums.UserAccessLevel;
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
        Result<Record> userRecord = ConnectDb.getDslContext().fetch("select * from user where user_id = " + userID);
        return userRecord;
    }

    public Result<Record> getAllUsers()
    {
        Result<Record> userRecord = ConnectDb.getDslContext().fetch("select * from user");
        return userRecord;
    }

    public Result<Record> getActiveUsers()
    {
        Result<Record> userRecord = ConnectDb.getDslContext().fetch("select * from user where active=1");
        return userRecord;
    }

    public boolean checkUserExists(String userName)
    {
        Result<Record> userRecord = this.findUserFromName(userName);
        if (userRecord == null || userRecord.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int insertUser(String userName, String accessLevel, String firstName, String lastName, String labName)
    {
        String query = "insert into user (user_name, first_name, last_name, lab_name, access_level, active) " +
                "values ('" + userName + "','" + firstName + "','" + lastName + "','" + labName + "','" + accessLevel + "', true)";
        DSLContext dslContext = ConnectDb.getDslContext();
        dslContext.execute(query);
        return dslContext.lastID().intValue();
    }

    public String[] getAccessLevels()
    {
        return new String[] {UserAccessLevel.Researcher.toString(), UserAccessLevel.Admin.toString()};
    }

    public void updateUser(String newUserName, String oldUserName, String newAccessLevel, String newFirstName, String newLastName,
                           String newLabName, Boolean newActive)
    {
        String query = "update user set user_name='" + newUserName + "',first_name='" +newFirstName + "',last_name='"+newLastName+ "',lab_name='"+
                newLabName + "',access_level='"+newAccessLevel+"', active=" + newActive + " where user_name='" + oldUserName + "'";
        ConnectDb.getDslContext().execute(query);
    }
}
