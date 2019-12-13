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
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.OrganismRecord;
import org.jooq.Field;
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
public class Organism extends TableImpl<OrganismRecord> {

    private static final long serialVersionUID = 830468898;

    /**
     * The reference instance of <code>rsa_gia.organism</code>
     */
    public static final Organism ORGANISM = new Organism();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrganismRecord> getRecordType() {
        return OrganismRecord.class;
    }

    /**
     * The column <code>rsa_gia.organism.organism_name</code>.
     */
    public final TableField<OrganismRecord, String> ORGANISM_NAME = createField("organism_name", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>rsa_gia.organism.species_code</code>.
     */
    public final TableField<OrganismRecord, String> SPECIES_CODE = createField("species_code", org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>rsa_gia.organism.species</code>.
     */
    public final TableField<OrganismRecord, String> SPECIES = createField("species", org.jooq.impl.SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>rsa_gia.organism.subspecies</code>.
     */
    public final TableField<OrganismRecord, String> SUBSPECIES = createField("subspecies", org.jooq.impl.SQLDataType.VARCHAR(50), this, "");

    /**
     * The column <code>rsa_gia.organism.description</code>.
     */
    public final TableField<OrganismRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * Create a <code>rsa_gia.organism</code> table reference
     */
    public Organism() {
        this(DSL.name("organism"), null);
    }

    /**
     * Create an aliased <code>rsa_gia.organism</code> table reference
     */
    public Organism(String alias) {
        this(DSL.name(alias), ORGANISM);
    }

    /**
     * Create an aliased <code>rsa_gia.organism</code> table reference
     */
    public Organism(Name alias) {
        this(alias, ORGANISM);
    }

    private Organism(Name alias, Table<OrganismRecord> aliased) {
        this(alias, aliased, null);
    }

    private Organism(Name alias, Table<OrganismRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.ORGANISM_ORGANISM_SPECIES_CODE_UINDEX, Indexes.ORGANISM_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrganismRecord> getPrimaryKey() {
        return Keys.KEY_ORGANISM_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrganismRecord>> getKeys() {
        return Arrays.<UniqueKey<OrganismRecord>>asList(Keys.KEY_ORGANISM_PRIMARY, Keys.KEY_ORGANISM_ORGANISM_SPECIES_CODE_UINDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organism as(String alias) {
        return new Organism(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organism as(Name alias) {
        return new Organism(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Organism rename(String name) {
        return new Organism(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Organism rename(Name name) {
        return new Organism(name, null);
    }
}