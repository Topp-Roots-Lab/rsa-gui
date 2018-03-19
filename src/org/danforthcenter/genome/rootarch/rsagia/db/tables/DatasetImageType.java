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
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetImageTypeRecord;
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
public class DatasetImageType extends TableImpl<DatasetImageTypeRecord> {

    private static final long serialVersionUID = -1818299967;

    /**
     * The reference instance of <code>rsa_gia.dataset_image_type</code>
     */
    public static final DatasetImageType DATASET_IMAGE_TYPE = new DatasetImageType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetImageTypeRecord> getRecordType() {
        return DatasetImageTypeRecord.class;
    }

    /**
     * The column <code>rsa_gia.dataset_image_type.dataset_id</code>.
     */
    public final TableField<DatasetImageTypeRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>rsa_gia.dataset_image_type.image_type</code>.
     */
    public final TableField<DatasetImageTypeRecord, String> IMAGE_TYPE = createField("image_type", org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * Create a <code>rsa_gia.dataset_image_type</code> table reference
     */
    public DatasetImageType() {
        this(DSL.name("dataset_image_type"), null);
    }

    /**
     * Create an aliased <code>rsa_gia.dataset_image_type</code> table reference
     */
    public DatasetImageType(String alias) {
        this(DSL.name(alias), DATASET_IMAGE_TYPE);
    }

    /**
     * Create an aliased <code>rsa_gia.dataset_image_type</code> table reference
     */
    public DatasetImageType(Name alias) {
        this(alias, DATASET_IMAGE_TYPE);
    }

    private DatasetImageType(Name alias, Table<DatasetImageTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private DatasetImageType(Name alias, Table<DatasetImageTypeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.DATASET_IMAGE_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DatasetImageTypeRecord> getPrimaryKey() {
        return Keys.KEY_DATASET_IMAGE_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DatasetImageTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetImageTypeRecord>>asList(Keys.KEY_DATASET_IMAGE_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<DatasetImageTypeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DatasetImageTypeRecord, ?>>asList(Keys.DATASET_IMAGE_PATHS_DATASET_DATASET_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImageType as(String alias) {
        return new DatasetImageType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetImageType as(Name alias) {
        return new DatasetImageType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetImageType rename(String name) {
        return new DatasetImageType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetImageType rename(Name name) {
        return new DatasetImageType(name, null);
    }
}
