/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.Indexes;
import org.danforthcenter.genome.rootarch.rsagia.db.Keys;
import org.danforthcenter.genome.rootarch.rsagia.db.RsaGia;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ProgramRunRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class ProgramRun extends TableImpl<ProgramRunRecord> {

    private static final long serialVersionUID = -611810566;

    /**
     * The reference instance of <code>rsa_gia.program_run</code>
     */
    public static final ProgramRun PROGRAM_RUN = new ProgramRun();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProgramRunRecord> getRecordType() {
        return ProgramRunRecord.class;
    }

    /**
     * The column <code>rsa_gia.program_run.run_id</code>.
     */
    public final TableField<ProgramRunRecord, Integer> RUN_ID = createField("run_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.user_id</code>.
     */
    public final TableField<ProgramRunRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.program_id</code>.
     */
    public final TableField<ProgramRunRecord, Integer> PROGRAM_ID = createField("program_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.dataset_id</code>.
     */
    public final TableField<ProgramRunRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.saved</code>.
     */
    public final TableField<ProgramRunRecord, Byte> SAVED = createField("saved", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.red_flag</code>.
     */
    public final TableField<ProgramRunRecord, Byte> RED_FLAG = createField("red_flag", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.run_date</code>.
     */
    public final TableField<ProgramRunRecord, Timestamp> RUN_DATE = createField("run_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.program_run.input_runs</code>.
     */
    public final TableField<ProgramRunRecord, String> INPUT_RUNS = createField("input_runs", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * The column <code>rsa_gia.program_run.saved_config_id</code>.
     */
    public final TableField<ProgramRunRecord, Integer> SAVED_CONFIG_ID = createField("saved_config_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>rsa_gia.program_run.unsaved_config_contents</code>.
     */
    public final TableField<ProgramRunRecord, String> UNSAVED_CONFIG_CONTENTS = createField("unsaved_config_contents", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * The column <code>rsa_gia.program_run.descriptors</code>.
     */
    public final TableField<ProgramRunRecord, String> DESCRIPTORS = createField("descriptors", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * The column <code>rsa_gia.program_run.results</code>.
     */
    public final TableField<ProgramRunRecord, String> RESULTS = createField("results", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * Create a <code>rsa_gia.program_run</code> table reference
     */
    public ProgramRun() {
        this(DSL.name("program_run"), null);
    }

    /**
     * Create an aliased <code>rsa_gia.program_run</code> table reference
     */
    public ProgramRun(String alias) {
        this(DSL.name(alias), PROGRAM_RUN);
    }

    /**
     * Create an aliased <code>rsa_gia.program_run</code> table reference
     */
    public ProgramRun(Name alias) {
        this(alias, PROGRAM_RUN);
    }

    private ProgramRun(Name alias, Table<ProgramRunRecord> aliased) {
        this(alias, aliased, null);
    }

    private ProgramRun(Name alias, Table<ProgramRunRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return RsaGia.RSA_GIA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.PROGRAM_RUN_PRIMARY, Indexes.PROGRAM_RUN_PROGRAM_RUN_DATASET_DATASET_ID_FK, Indexes.PROGRAM_RUN_PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK, Indexes.PROGRAM_RUN_PROGRAM_RUN_RED_FLAG_INDEX, Indexes.PROGRAM_RUN_PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK, Indexes.PROGRAM_RUN_PROGRAM_RUN_USER_USER_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ProgramRunRecord> getPrimaryKey() {
        return Keys.KEY_PROGRAM_RUN_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ProgramRunRecord>> getKeys() {
        return Arrays.<UniqueKey<ProgramRunRecord>>asList(Keys.KEY_PROGRAM_RUN_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ProgramRunRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ProgramRunRecord, ?>>asList(Keys.PROGRAM_RUN_USER_USER_ID_FK, Keys.PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK, Keys.PROGRAM_RUN_DATASET_DATASET_ID_FK, Keys.PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProgramRun as(String alias) {
        return new ProgramRun(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProgramRun as(Name alias) {
        return new ProgramRun(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ProgramRun rename(String name) {
        return new ProgramRun(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ProgramRun rename(Name name) {
        return new ProgramRun(name, null);
    }
}
