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
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.GenotypeRecord;
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
public class Genotype extends TableImpl<GenotypeRecord> {

    private static final long serialVersionUID = 66856951;

    /**
     * The reference instance of <code>rsa_gia.genotype</code>
     */
    public static final Genotype GENOTYPE = new Genotype();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GenotypeRecord> getRecordType() {
        return GenotypeRecord.class;
    }

    /**
     * The column <code>rsa_gia.genotype.genotype_id</code>.
     */
    public final TableField<GenotypeRecord, Integer> GENOTYPE_ID = createField("genotype_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>rsa_gia.genotype.genotype_name</code>.
     */
    public final TableField<GenotypeRecord, String> GENOTYPE_NAME = createField("genotype_name", org.jooq.impl.SQLDataType.VARCHAR(20), this, "");

    /**
     * The column <code>rsa_gia.genotype.organism_name</code>.
     */
    public final TableField<GenotypeRecord, String> ORGANISM_NAME = createField("organism_name", org.jooq.impl.SQLDataType.VARCHAR(20), this, "");

    /**
     * Create a <code>rsa_gia.genotype</code> table reference
     */
    public Genotype() {
        this(DSL.name("genotype"), null);
    }

    /**
     * Create an aliased <code>rsa_gia.genotype</code> table reference
     */
    public Genotype(String alias) {
        this(DSL.name(alias), GENOTYPE);
    }

    /**
     * Create an aliased <code>rsa_gia.genotype</code> table reference
     */
    public Genotype(Name alias) {
        this(alias, GENOTYPE);
    }

    private Genotype(Name alias, Table<GenotypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Genotype(Name alias, Table<GenotypeRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.GENOTYPE_GENOTYPE_GENOTYPE_NAME_ORGANISM_NAME_UINDEX, Indexes.GENOTYPE_GENOTYPE_ORGANISM_ORGANISM_NAME_FK, Indexes.GENOTYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<GenotypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GENOTYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<GenotypeRecord> getPrimaryKey() {
        return Keys.KEY_GENOTYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<GenotypeRecord>> getKeys() {
        return Arrays.<UniqueKey<GenotypeRecord>>asList(Keys.KEY_GENOTYPE_PRIMARY, Keys.KEY_GENOTYPE_GENOTYPE_GENOTYPE_NAME_ORGANISM_NAME_UINDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<GenotypeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GenotypeRecord, ?>>asList(Keys.GENOTYPE_ORGANISM_ORGANISM_NAME_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Genotype as(String alias) {
        return new Genotype(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Genotype as(Name alias) {
        return new Genotype(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Genotype rename(String name) {
        return new Genotype(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Genotype rename(Name name) {
        return new Genotype(name, null);
    }
}