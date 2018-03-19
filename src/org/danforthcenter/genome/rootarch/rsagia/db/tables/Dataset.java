/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.Indexes;
import org.danforthcenter.genome.rootarch.rsagia.db.Keys;
import org.danforthcenter.genome.rootarch.rsagia.db.RsaGia;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class Dataset extends TableImpl<DatasetRecord> {

    private static final long serialVersionUID = 410865930;

    /**
     * The reference instance of <code>rsa_gia.dataset</code>
     */
    public static final Dataset DATASET = new Dataset();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetRecord> getRecordType() {
        return DatasetRecord.class;
    }

    /**
     * The column <code>rsa_gia.dataset.dataset_id</code>.
     */
    public final TableField<DatasetRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>rsa_gia.dataset.seed_id</code>.
     */
    public final TableField<DatasetRecord, Integer> SEED_ID = createField("seed_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.dataset.timepoint</code>.
     */
    public final TableField<DatasetRecord, String> TIMEPOINT = createField("timepoint", org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>rsa_gia.dataset</code> table reference
     */
    public Dataset() {
        this(DSL.name("dataset"), null);
    }

    /**
     * Create an aliased <code>rsa_gia.dataset</code> table reference
     */
    public Dataset(String alias) {
        this(DSL.name(alias), DATASET);
    }

    /**
     * Create an aliased <code>rsa_gia.dataset</code> table reference
     */
    public Dataset(Name alias) {
        this(alias, DATASET);
    }

    private Dataset(Name alias, Table<DatasetRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dataset(Name alias, Table<DatasetRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.DATASET_DATASET_SEED_SEED_ID_FK, Indexes.DATASET_DATASET_TIMEPOINT_D_T_VALUE_INDEX, Indexes.DATASET_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<DatasetRecord, Integer> getIdentity() {
        return Keys.IDENTITY_DATASET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DatasetRecord> getPrimaryKey() {
        return Keys.KEY_DATASET_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DatasetRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetRecord>>asList(Keys.KEY_DATASET_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<DatasetRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DatasetRecord, ?>>asList(Keys.DATASET_SEED_SEED_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dataset as(String alias) {
        return new Dataset(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dataset as(Name alias) {
        return new Dataset(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dataset rename(String name) {
        return new Dataset(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dataset rename(Name name) {
        return new Dataset(name, null);
    }
}
