/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db.tables.records;


import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.tables.User;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserRecord extends UpdatableRecordImpl<UserRecord> implements Record5<Integer, String, String, String, String> {

    private static final long serialVersionUID = -1426687521;

    /**
     * Setter for <code>rsa_gia.user.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>rsa_gia.user.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>rsa_gia.user.user_name</code>.
     */
    public void setUserName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>rsa_gia.user.user_name</code>.
     */
    public String getUserName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>rsa_gia.user.first_name</code>.
     */
    public void setFirstName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>rsa_gia.user.first_name</code>.
     */
    public String getFirstName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>rsa_gia.user.last_name</code>.
     */
    public void setLastName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>rsa_gia.user.last_name</code>.
     */
    public String getLastName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>rsa_gia.user.lab_name</code>.
     */
    public void setLabName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>rsa_gia.user.lab_name</code>.
     */
    public String getLabName() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return User.USER.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return User.USER.USER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return User.USER.FIRST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return User.USER.LAST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return User.USER.LAB_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getFirstName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getLastName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getLabName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getFirstName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getLastName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getLabName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord value1(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord value2(String value) {
        setUserName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord value3(String value) {
        setFirstName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord value4(String value) {
        setLastName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord value5(String value) {
        setLabName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRecord values(Integer value1, String value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserRecord
     */
    public UserRecord() {
        super(User.USER);
    }

    /**
     * Create a detached, initialised UserRecord
     */
    public UserRecord(Integer userId, String userName, String firstName, String lastName, String labName) {
        super(User.USER);

        set(0, userId);
        set(1, userName);
        set(2, firstName);
        set(3, lastName);
        set(4, labName);
    }
}
