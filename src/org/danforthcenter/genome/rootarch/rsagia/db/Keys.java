/*
 * This file is generated by jOOQ.
*/
package org.danforthcenter.genome.rootarch.rsagia.db;


import javax.annotation.Generated;

import org.danforthcenter.genome.rootarch.rsagia.db.tables.Dataset;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetCount;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetImagePaths;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Experiment;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Program;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramDependency;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramRun;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.SavedConfig;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.Seed;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.User;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetCountRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetImagePathsRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.DatasetRecord;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.records.ExperimentRecord;
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
    public static final Identity<ProgramRecord, Integer> IDENTITY_PROGRAM = Identities0.IDENTITY_PROGRAM;
    public static final Identity<SavedConfigRecord, Integer> IDENTITY_SAVED_CONFIG = Identities0.IDENTITY_SAVED_CONFIG;
    public static final Identity<SeedRecord, Integer> IDENTITY_SEED = Identities0.IDENTITY_SEED;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DatasetRecord> KEY_DATASET_PRIMARY = UniqueKeys0.KEY_DATASET_PRIMARY;
    public static final UniqueKey<DatasetCountRecord> KEY_DATASET_COUNT_PRIMARY = UniqueKeys0.KEY_DATASET_COUNT_PRIMARY;
    public static final UniqueKey<DatasetImagePathsRecord> KEY_DATASET_IMAGE_PATHS_PRIMARY = UniqueKeys0.KEY_DATASET_IMAGE_PATHS_PRIMARY;
    public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_PRIMARY = UniqueKeys0.KEY_EXPERIMENT_PRIMARY;
    public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX = UniqueKeys0.KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX;
    public static final UniqueKey<OrganismRecord> KEY_ORGANISM_PRIMARY = UniqueKeys0.KEY_ORGANISM_PRIMARY;
    public static final UniqueKey<ProgramRecord> KEY_PROGRAM_PRIMARY = UniqueKeys0.KEY_PROGRAM_PRIMARY;
    public static final UniqueKey<ProgramDependencyRecord> KEY_PROGRAM_DEPENDENCY_PRIMARY = UniqueKeys0.KEY_PROGRAM_DEPENDENCY_PRIMARY;
    public static final UniqueKey<ProgramRunRecord> KEY_PROGRAM_RUN_PRIMARY = UniqueKeys0.KEY_PROGRAM_RUN_PRIMARY;
    public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_PRIMARY = UniqueKeys0.KEY_SAVED_CONFIG_PRIMARY;
    public static final UniqueKey<SeedRecord> KEY_SEED_PRIMARY = UniqueKeys0.KEY_SEED_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = UniqueKeys0.KEY_USER_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<DatasetRecord, SeedRecord> DATASET_SEED_SEED_ID_FK = ForeignKeys0.DATASET_SEED_SEED_ID_FK;
    public static final ForeignKey<DatasetCountRecord, DatasetRecord> DATASET_COUNT_DATASET_DATASET_ID_FK = ForeignKeys0.DATASET_COUNT_DATASET_DATASET_ID_FK;
    public static final ForeignKey<DatasetCountRecord, ProgramRecord> DATASET_COUNT_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.DATASET_COUNT_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<DatasetImagePathsRecord, DatasetRecord> DATASET_IMAGE_PATHS_DATASET_DATASET_ID_FK = ForeignKeys0.DATASET_IMAGE_PATHS_DATASET_DATASET_ID_FK;
    public static final ForeignKey<ExperimentRecord, OrganismRecord> EXPERIMENT_ORGANISM_ORGANISM_NAME_FK = ForeignKeys0.EXPERIMENT_ORGANISM_ORGANISM_NAME_FK;
    public static final ForeignKey<ExperimentRecord, UserRecord> EXPERIMENT_USER_USER_ID_FK = ForeignKeys0.EXPERIMENT_USER_USER_ID_FK;
    public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<ProgramRunRecord, UserRecord> PROGRAM_RUN_USER_USER_ID_FK = ForeignKeys0.PROGRAM_RUN_USER_USER_ID_FK;
    public static final ForeignKey<ProgramRunRecord, ProgramRecord> PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<ProgramRunRecord, DatasetRecord> PROGRAM_RUN_DATASET_DATASET_ID_FK = ForeignKeys0.PROGRAM_RUN_DATASET_DATASET_ID_FK;
    public static final ForeignKey<ProgramRunRecord, SavedConfigRecord> PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK = ForeignKeys0.PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK;
    public static final ForeignKey<SavedConfigRecord, ProgramRecord> SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK = ForeignKeys0.SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK;
    public static final ForeignKey<SeedRecord, ExperimentRecord> SEED_EXPERIMENT_EXPERIMENT_ID_FK = ForeignKeys0.SEED_EXPERIMENT_EXPERIMENT_ID_FK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<DatasetRecord, Integer> IDENTITY_DATASET = createIdentity(Dataset.DATASET, Dataset.DATASET.DATASET_ID);
        public static Identity<ProgramRecord, Integer> IDENTITY_PROGRAM = createIdentity(Program.PROGRAM, Program.PROGRAM.PROGRAM_ID);
        public static Identity<SavedConfigRecord, Integer> IDENTITY_SAVED_CONFIG = createIdentity(SavedConfig.SAVED_CONFIG, SavedConfig.SAVED_CONFIG.CONFIG_ID);
        public static Identity<SeedRecord, Integer> IDENTITY_SEED = createIdentity(Seed.SEED, Seed.SEED.SEED_ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<DatasetRecord> KEY_DATASET_PRIMARY = createUniqueKey(Dataset.DATASET, "KEY_dataset_PRIMARY", Dataset.DATASET.DATASET_ID);
        public static final UniqueKey<DatasetCountRecord> KEY_DATASET_COUNT_PRIMARY = createUniqueKey(DatasetCount.DATASET_COUNT, "KEY_dataset_count_PRIMARY", DatasetCount.DATASET_COUNT.DATASET_ID, DatasetCount.DATASET_COUNT.PROGRAM_ID, DatasetCount.DATASET_COUNT.CONDITION_TYPE);
        public static final UniqueKey<DatasetImagePathsRecord> KEY_DATASET_IMAGE_PATHS_PRIMARY = createUniqueKey(DatasetImagePaths.DATASET_IMAGE_PATHS, "KEY_dataset_image_paths_PRIMARY", DatasetImagePaths.DATASET_IMAGE_PATHS.DATASET_ID, DatasetImagePaths.DATASET_IMAGE_PATHS.IMAGE_TYPE);
        public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_PRIMARY = createUniqueKey(Experiment.EXPERIMENT, "KEY_experiment_PRIMARY", Experiment.EXPERIMENT.EXPERIMENT_ID);
        public static final UniqueKey<ExperimentRecord> KEY_EXPERIMENT_EXPERIMENT_EXPERIMENT_CODE_ORGANISM_NAME_UINDEX = createUniqueKey(Experiment.EXPERIMENT, "KEY_experiment_experiment_experiment_code_organism_name_uindex", Experiment.EXPERIMENT.EXPERIMENT_CODE, Experiment.EXPERIMENT.ORGANISM_NAME);
        public static final UniqueKey<OrganismRecord> KEY_ORGANISM_PRIMARY = createUniqueKey(Organism.ORGANISM, "KEY_organism_PRIMARY", Organism.ORGANISM.ORGANISM_NAME);
        public static final UniqueKey<ProgramRecord> KEY_PROGRAM_PRIMARY = createUniqueKey(Program.PROGRAM, "KEY_program_PRIMARY", Program.PROGRAM.PROGRAM_ID);
        public static final UniqueKey<ProgramDependencyRecord> KEY_PROGRAM_DEPENDENCY_PRIMARY = createUniqueKey(ProgramDependency.PROGRAM_DEPENDENCY, "KEY_program_dependency_PRIMARY", ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_DEPENDENCY_ID, ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_ID);
        public static final UniqueKey<ProgramRunRecord> KEY_PROGRAM_RUN_PRIMARY = createUniqueKey(ProgramRun.PROGRAM_RUN, "KEY_program_run_PRIMARY", ProgramRun.PROGRAM_RUN.RUN_ID);
        public static final UniqueKey<SavedConfigRecord> KEY_SAVED_CONFIG_PRIMARY = createUniqueKey(SavedConfig.SAVED_CONFIG, "KEY_saved_config_PRIMARY", SavedConfig.SAVED_CONFIG.CONFIG_ID);
        public static final UniqueKey<SeedRecord> KEY_SEED_PRIMARY = createUniqueKey(Seed.SEED, "KEY_seed_PRIMARY", Seed.SEED.SEED_ID);
        public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = createUniqueKey(User.USER, "KEY_user_PRIMARY", User.USER.USER_ID);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<DatasetRecord, SeedRecord> DATASET_SEED_SEED_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_SEED_PRIMARY, Dataset.DATASET, "dataset_seed_seed_id_fk", Dataset.DATASET.SEED_ID);
        public static final ForeignKey<DatasetCountRecord, DatasetRecord> DATASET_COUNT_DATASET_DATASET_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_DATASET_PRIMARY, DatasetCount.DATASET_COUNT, "dataset_count_dataset_dataset_id_fk", DatasetCount.DATASET_COUNT.DATASET_ID);
        public static final ForeignKey<DatasetCountRecord, ProgramRecord> DATASET_COUNT_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, DatasetCount.DATASET_COUNT, "dataset_count_program_program_id_fk", DatasetCount.DATASET_COUNT.PROGRAM_ID);
        public static final ForeignKey<DatasetImagePathsRecord, DatasetRecord> DATASET_IMAGE_PATHS_DATASET_DATASET_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_DATASET_PRIMARY, DatasetImagePaths.DATASET_IMAGE_PATHS, "dataset_image_paths_dataset_dataset_id_fk", DatasetImagePaths.DATASET_IMAGE_PATHS.DATASET_ID);
        public static final ForeignKey<ExperimentRecord, OrganismRecord> EXPERIMENT_ORGANISM_ORGANISM_NAME_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_ORGANISM_PRIMARY, Experiment.EXPERIMENT, "experiment_organism_organism_name_fk", Experiment.EXPERIMENT.ORGANISM_NAME);
        public static final ForeignKey<ExperimentRecord, UserRecord> EXPERIMENT_USER_USER_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_USER_PRIMARY, Experiment.EXPERIMENT, "experiment_user_user_id_fk", Experiment.EXPERIMENT.USER_ID);
        public static final ForeignKey<ProgramDependencyRecord, ProgramRecord> PROGRAM_DEPENDENCY_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, ProgramDependency.PROGRAM_DEPENDENCY, "program_dependency_program_program_id_fk", ProgramDependency.PROGRAM_DEPENDENCY.PROGRAM_ID);
        public static final ForeignKey<ProgramRunRecord, UserRecord> PROGRAM_RUN_USER_USER_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_USER_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_user_user_id_fk", ProgramRun.PROGRAM_RUN.USER_ID);
        public static final ForeignKey<ProgramRunRecord, ProgramRecord> PROGRAM_RUN_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_program_program_id_fk", ProgramRun.PROGRAM_RUN.PROGRAM_ID);
        public static final ForeignKey<ProgramRunRecord, DatasetRecord> PROGRAM_RUN_DATASET_DATASET_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_DATASET_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_dataset_dataset_id_fk", ProgramRun.PROGRAM_RUN.DATASET_ID);
        public static final ForeignKey<ProgramRunRecord, SavedConfigRecord> PROGRAM_RUN_SAVED_CONFIG_CONFIG_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_SAVED_CONFIG_PRIMARY, ProgramRun.PROGRAM_RUN, "program_run_saved_config_config_id_fk", ProgramRun.PROGRAM_RUN.CONFIG_ID);
        public static final ForeignKey<SavedConfigRecord, ProgramRecord> SAVED_CONFIG_PROGRAM_PROGRAM_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_PROGRAM_PRIMARY, SavedConfig.SAVED_CONFIG, "saved_config_program_program_id_fk", SavedConfig.SAVED_CONFIG.PROGRAM_ID);
        public static final ForeignKey<SeedRecord, ExperimentRecord> SEED_EXPERIMENT_EXPERIMENT_ID_FK = createForeignKey(org.danforthcenter.genome.rootarch.rsagia.db.Keys.KEY_EXPERIMENT_PRIMARY, Seed.SEED, "seed_experiment_experiment_id_fk", Seed.SEED.EXPERIMENT_ID);
    }
}
