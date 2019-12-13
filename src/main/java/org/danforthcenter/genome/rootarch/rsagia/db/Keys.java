/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db;


import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.tables.Dataset;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetImageType;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Experiment;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Genotype;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Program;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramDependency;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramRun;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.SavedConfig;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Seed;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.User;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetImageTypeRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ExperimentRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.GenotypeRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.OrganismRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ProgramDependencyRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ProgramRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ProgramRunRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.SavedConfigRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.SeedRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.UserRecord;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>rsa_gia</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<DatasetRecord, Integer> IDENTITY_DATASET = Identities0.IDENTITY_DATASET;
    public static final Identity<ExperimentRecord, Integer> IDENTITY_EXPERIMENT = Identities0.IDENTITY_EXPERIMENT;
    public static final Identity<GenotypeRecord, Integer> IDENTITY_GENOTYPE = Identities0.IDENTITY_GENOTYPE;
    public static final Identity<ProgramRecord, Integer> IDENTITY_PROGRAM = Identities0.IDENTITY_PROGRAM;
    public static final Identity<ProgramRunRecord, Integer> IDENTITY_PROGRAM_RUN = Identities0.IDENTITY_PROGRAM_RUN;
    public static final Identity<SavedConfigRecord, Integer> IDENTITY_SAVED_CONFIG = Identities0.IDENTITY_SAVED_CONFIG;
    public static final Identity<SeedRecord, Integer> IDENTITY_SEED = Identities0.IDENTITY_SEED;
    public static final Identity<UserRecord, Integer> IDENTITY_USER = Identities0.IDENTITY_USER;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DatasetRecord> KEY_DATASET_PRIMARY = UniqueKeys0.KEY_DATASET_PRIMARY;
    public static final UniqueKey<DatasetImageTypeRecord> KEY_DATASET_IMAGE_TYPE_PRIMARY = UniqueKeys0.KEY_DATASET_IMAGE_TYPE_PRIMARY;
    public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_PRIMARY = UniqueKeys0.KEY_EXPERIMENT_PRIMARY;
    public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX = UniqueKeys0.KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX;
    public static final UniqueKey<GenotypeRecord> KEY_GENOTYPE_PRIMARY = UniqueKeys0.KEY_GENOTYPE_PRIMARY;
    public static final UniqueKey<GenotypeRecord> KEY_GENOTYPE_GENOTYPE_GENOTYPE_NAME_ORGANISM_NAME_UINDEX = UniqueKeys0.KEY_GENOTYPE_GENOTYPE_GENOTYPE_NAME_ORGANISM_NAME_UINDEX;
    public static final UniqueKey<OrganismRecord> KEY_ORGANISM_PRIMARY = UniqueKeys0.KEY_ORGANISM_PRIMARY;
    public static final UniqueKey<OrganismRecord> KEY_ORGANISM_ORGANISM_SPECIES_CODE_UINDEX = UniqueKeys0.KEY_ORGANISM_ORGANISM_SPECIES_CODE_UINDEX;
    public static final UniqueKey<ProgramRecord> KEY_PROGRAM_PRIMARY = UniqueKeys0.KEY_PROGRAM_PRIMARY;
    public static final UniqueKey<ProgramDependencyRecord> KEY_PROGRAM_DEPENDENCY_PRIMARY = UniqueKeys0.KEY_PROGRAM_DEPENDENCY_PRIMARY;
    public static final UniqueKey<ProgramRunRecord> KEY_PROGRAM_RUN_PRIMARY = UniqueKeys0.KEY_PROGRAM_RUN_PRIMARY;
    public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_PRIMARY = UniqueKeys0.KEY_SAVED_CONFIG_PRIMARY;
    public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_SAVED_CONFIG_PROGRAM_ID_NAME_UINDEX = UniqueKeys0.KEY_SAVED_CONFIG_SAVED_CONFIG_PROGRAM_ID_NAME_UINDEX;
    public static final UniqueKey<SeedRecord> KEY_SEED_PRIMARY = UniqueKeys0.KEY_SEED_PRIMARY;
    public static final UniqueKey<SeedRecord> KEY_SEED_SEED_EXPERIMENT_ID_SEED_NAME_UINDEX = UniqueKeys0.KEY_SEED_SEED_EXPERIMENT_ID_SEED_NAME_UINDEX;
    public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = UniqueKeys0.KEY_USER_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_USER_USER_NAME_UINDEX = UniqueKeys0.KEY_USER_USER_USER_NAME_UINDEX;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<DatasetRecord, SeedRecord> DATASET_SEED_SEED_ID_FK = ForeignKeys0.DATASET_SEED_SEED_ID_FK;
    public static final ForeignKey<DatasetImageTypeRecord, DatasetRecord> DATASET_IMAGE_TYPE_DATASET_DATASET_ID_FK = ForeignKeys0.DATASET_IMAGE_TYPE_DATASET_DATASET_ID_FK;
    public static final ForeignKey<ExperimentRecord, OrganismRecord> EXPERIMENT_ORGANISM_ORGANISM_NAME_FK = ForeignKeys0.EXPERIMENT_ORGANISM_ORGANISM_NAME_FK;
    public static final ForeignKey<ExperimentRecord, UserRecord> EXPERIMENT_USER_USER_ID_FK = ForeignKeys0.EXPERIMENT_USER_USER_ID_FK;
    public static final ForeignKey<GenotypeRecord, OrganismRecord> GENOTYPE_ORGANISM_ORGANISM_NAME_FK = ForeignKeys0.GENOTYPE_ORGANISM_ORGANISM_NAME_FK;
    public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK_2 = ForeignKeys0.PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK_2;
    public static final ForeignKey<ProgramRunRecord, UserRecord> PROGRAM_RUN_USER_USER_ID_FK = ForeignKeys0.PROGRAM_RUN_USER_USER_ID_FK;
    public static final ForeignKey<ProgramRunRecord, ProgramRecord> PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<ProgramRunRecord, DatasetRecord> PROGRAM_RUN_DATASET_DATASET_ID_FK = ForeignKeys0.PROGRAM_RUN_DATASET_DATASET_ID_FK;
    public static final ForeignKey<ProgramRunRecord, SavedConfigRecord> PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK = ForeignKeys0.PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK;
    public static final ForeignKey<SavedConfigRecord, ProgramRecord> SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<SeedRecord, ExperimentRecord> SEED_EXPERIMENT_EXPERIMENT_ID_FK = ForeignKeys0.SEED_EXPERIMENT_EXPERIMENT_ID_FK;
    public static final ForeignKey<SeedRecord, GenotypeRecord> SEED_GENOTYPE_GENOTYPE_ID_FK = ForeignKeys0.SEED_GENOTYPE_GENOTYPE_ID_FK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<DatasetRecord, Integer> IDENTITY_DATASET = createIdentity(Dataset.DATASET, Dataset.DATASET.DATASET_ID);
        public static Identity<ExperimentRecord, Integer> IDENTITY_EXPERIMENT = createIdentity(Experiment.EXPERIMENT, Experiment.EXPERIMENT.EXPERIMENT_ID);
        public static Identity<GenotypeRecord, Integer> IDENTITY_GENOTYPE = createIdentity(Genotype.GENOTYPE, Genotype.GENOTYPE.GENOTYPE_ID);
        public static Identity<ProgramRecord, Integer> IDENTITY_PROGRAM = createIdentity(Program.PROGRAM, Program.PROGRAM.PROGRAM_ID);
        public static Identity<ProgramRunRecord, Integer> IDENTITY_PROGRAM_RUN = createIdentity(ProgramRun.PROGRAM_RUN, ProgramRun.PROGRAM_RUN.RUN_ID);
        public static Identity<SavedConfigRecord, Integer> IDENTITY_SAVED_CONFIG = createIdentity(SavedConfig.SAVED_CONFIG, SavedConfig.SAVED_CONFIG.CONFIG_ID);
        public static Identity<SeedRecord, Integer> IDENTITY_SEED = createIdentity(Seed.SEED, Seed.SEED.SEED_ID);
        public static Identity<UserRecord, Integer> IDENTITY_USER = createIdentity(User.USER, User.USER.USER_ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<DatasetRecord> KEY_DATASET_PRIMARY = createUniqueKey(Dataset.DATASET, "KEY_dataset_PRIMARY", Dataset.DATASET.DATASET_ID);
        public static final UniqueKey<DatasetImageTypeRecord> KEY_DATASET_IMAGE_TYPE_PRIMARY = createUniqueKey(DatasetImageType.DATASET_IMAGE_TYPE, "KEY_dataset_image_type_PRIMARY", DatasetImageType.DATASET_IMAGE_TYPE.DATASET_ID, DatasetImageType.DATASET_IMAGE_TYPE.IMAGE_TYPE);
        public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_PRIMARY = createUniqueKey(Experiment.EXPERIMENT, "KEY_experiment_PRIMARY", Experiment.EXPERIMENT.EXPERIMENT_ID);
        public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX = createUniqueKey(Experiment.EXPERIMENT, "KEY_experiment_experiment_experiment_code_organism_name_uindex", Experiment.EXPERIMENT.EXPERIMENT_CODE, Experiment.EXPERIMENT.ORGANISM_NAME);
        public static final UniqueKey<GenotypeRecord> KEY_GENOTYPE_PRIMARY = createUniqueKey(Genotype.GENOTYPE, "KEY_genotype_PRIMARY", Genotype.GENOTYPE.GENOTYPE_ID);
        public static final UniqueKey<GenotypeRecord> KEY_GENOTYPE_GENOTYPE_GENOTYPE_NAME_ORGANISM_NAME_UINDEX = createUniqueKey(Genotype.GENOTYPE, "KEY_genotype_genotype_genotype_name_organism_name_uindex", Genotype.GENOTYPE.GENOTYPE_NAME, Genotype.GENOTYPE.ORGANISM_NAME);
        public static final UniqueKey<OrganismRecord> KEY_ORGANISM_PRIMARY = createUniqueKey(Organism.ORGANISM, "KEY_organism_PRIMARY", Organism.ORGANISM.ORGANISM_NAME);
        public static final UniqueKey<OrganismRecord> KEY_ORGANISM_ORGANISM_SPECIES_CODE_UINDEX = createUniqueKey(Organism.ORGANISM, "KEY_organism_organism_species_code_uindex", Organism.ORGANISM.SPECIES_CODE);
        public static final UniqueKey<ProgramRecord> KEY_PROGRAM_PRIMARY = createUniqueKey(Program.PROGRAM, "KEY_program_PRIMARY", Program.PROGRAM.PROGRAM_ID);
        public static final UniqueKey<ProgramDependencyRecord> KEY_PROGRAM_DEPENDENCY_PRIMARY = createUniqueKey(ProgramDependency.PROGRAM_DEPENDENCY, "KEY_program_dependency_PRIMARY", ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_ID, ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_DEPENDENCY_ID);
        public static final UniqueKey<ProgramRunRecord> KEY_PROGRAM_RUN_PRIMARY = createUniqueKey(ProgramRun.PROGRAM_RUN, "KEY_program_run_PRIMARY", ProgramRun.PROGRAM_RUN.RUN_ID);
        public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_PRIMARY = createUniqueKey(SavedConfig.SAVED_CONFIG, "KEY_saved_config_PRIMARY", SavedConfig.SAVED_CONFIG.CONFIG_ID);
        public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_SAVED_CONFIG_PROGRAM_ID_NAME_UINDEX = createUniqueKey(SavedConfig.SAVED_CONFIG, "KEY_saved_config_saved_config_program_id_name_uindex", SavedConfig.SAVED_CONFIG.PROGRAM_ID, SavedConfig.SAVED_CONFIG.NAME);
        public static final UniqueKey<SeedRecord> KEY_SEED_PRIMARY = createUniqueKey(Seed.SEED, "KEY_seed_PRIMARY", Seed.SEED.SEED_ID);
        public static final UniqueKey<SeedRecord> KEY_SEED_SEED_EXPERIMENT_ID_SEED_NAME_UINDEX = createUniqueKey(Seed.SEED, "KEY_seed_seed_experiment_id_seed_name_uindex", Seed.SEED.EXPERIMENT_ID, Seed.SEED.SEED_NAME);
        public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = createUniqueKey(User.USER, "KEY_user_PRIMARY", User.USER.USER_ID);
        public static final UniqueKey<UserRecord> KEY_USER_USER_USER_NAME_UINDEX = createUniqueKey(User.USER, "KEY_user_user_user_name_uindex", User.USER.USER_NAME);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<DatasetRecord, SeedRecord> DATASET_SEED_SEED_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_SEED_PRIMARY, Dataset.DATASET, "dataset_seed_seed_id_fk", Dataset.DATASET.SEED_ID);
        public static final ForeignKey<DatasetImageTypeRecord, DatasetRecord> DATASET_IMAGE_TYPE_DATASET_DATASET_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_DATASET_PRIMARY, DatasetImageType.DATASET_IMAGE_TYPE, "dataset_image_type_dataset_dataset_id_fk", DatasetImageType.DATASET_IMAGE_TYPE.DATASET_ID);
        public static final ForeignKey<ExperimentRecord, OrganismRecord> EXPERIMENT_ORGANISM_ORGANISM_NAME_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_ORGANISM_PRIMARY, Experiment.EXPERIMENT, "experiment_organism_organism_name_fk", Experiment.EXPERIMENT.ORGANISM_NAME);
        public static final ForeignKey<ExperimentRecord, UserRecord> EXPERIMENT_USER_USER_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_USER_PRIMARY, Experiment.EXPERIMENT, "experiment_user_user_id_fk", Experiment.EXPERIMENT.USER_ID);
        public static final ForeignKey<GenotypeRecord, OrganismRecord> GENOTYPE_ORGANISM_ORGANISM_NAME_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_ORGANISM_PRIMARY, Genotype.GENOTYPE, "genotype_organism_organism_name_fk", Genotype.GENOTYPE.ORGANISM_NAME);
        public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, ProgramDependency.PROGRAM_DEPENDENCY, "program_dependency_program_program_id_fk", ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_ID);
        public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK_2 = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, ProgramDependency.PROGRAM_DEPENDENCY, "program_dependency_program_program_id_fk_2", ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_DEPENDENCY_ID);
        public static final ForeignKey<ProgramRunRecord, UserRecord> PROGRAM_RUN_USER_USER_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_USER_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_user_user_id_fk", ProgramRun.PROGRAM_RUN.USER_ID);
        public static final ForeignKey<ProgramRunRecord, ProgramRecord> PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_program_program_id_fk", ProgramRun.PROGRAM_RUN.PROGRAM_ID);
        public static final ForeignKey<ProgramRunRecord, DatasetRecord> PROGRAM_RUN_DATASET_DATASET_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_DATASET_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_dataset_dataset_id_fk", ProgramRun.PROGRAM_RUN.DATASET_ID);
        public static final ForeignKey<ProgramRunRecord, SavedConfigRecord> PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_SAVED_CONFIG_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_saved_config_config_id_fk", ProgramRun.PROGRAM_RUN.SAVED_CONFIG_ID);
        public static final ForeignKey<SavedConfigRecord, ProgramRecord> SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, SavedConfig.SAVED_CONFIG, "saved_config_program_program_id_fk", SavedConfig.SAVED_CONFIG.PROGRAM_ID);
        public static final ForeignKey<SeedRecord, ExperimentRecord> SEED_EXPERIMENT_EXPERIMENT_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_EXPERIMENT_PRIMARY, Seed.SEED, "seed_experiment_experiment_id_fk", Seed.SEED.EXPERIMENT_ID);
        public static final ForeignKey<SeedRecord, GenotypeRecord> SEED_GENOTYPE_GENOTYPE_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_GENOTYPE_PRIMARY, Seed.SEED, "seed_genotype_genotype_id_fk", Seed.SEED.GENOTYPE_ID);
    }
}