package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia.db.enums.UserAccessLevel;
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

    public int findMax()
    {
        Result<Record> record = ConnectDb.getDslContext().fetch("select max(user_id) max from user");
        int max = (int) record.get(0).get("max");
        return max;
    }

    public void insertUser(String userName, String accessLevel, String firstName, String lastName, String labName)
    {
        int count = findMax() + 1;
        String query = "insert into user (user_id, user_name, first_name, last_name, lab_name, access_level) " +
                "values (" + count + ",'" + userName +"','" + firstName + "','" + lastName + "','" + labName + "','" + accessLevel + "')";
        ConnectDb.getDslContext().execute(query);
    }

    public String[] getAccessLevels()
    {
        return new String[] {UserAccessLevel.Researcher.toString(), UserAccessLevel.Admin.toString()};
    }

    public void updateUser(String newUserName, String oldUserName, String newAccessLevel, String newFirstName, String newLastName,
                           String newLabName)
    {
        String query = "update user set user_name='" + newUserName + "',first_name='" +newFirstName + "',last_name='"+newLastName+ "',lab_name='"+
                newLabName + "',access_level='"+newAccessLevel+"' where user_name='" + oldUserName + "'";
        ConnectDb.getDslContext().execute(query);
    }
}
