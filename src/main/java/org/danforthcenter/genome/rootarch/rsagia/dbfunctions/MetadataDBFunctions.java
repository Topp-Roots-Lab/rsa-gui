package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.jooq.Record;
import org.jooq.Result;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MetadataDBFunctions {
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public boolean isNumeric(String s) {
        boolean ans = s.matches("[-+]?\\d*\\.?\\d+");
        return ans;
    }

    public Result<Record> selectAllOrganism() {
        Result<Record> organismRecord = ConnectDb.getDslContext().fetch("select * from organism");
        return organismRecord;
    }

    public Result<Record> findExperiment(String experimentCode, String organism) {
        Result<Record> experimentRecord = ConnectDb.getDslContext().fetch("select * from experiment where experiment_code='" +
                experimentCode + "' and organism_name='" + organism + "'");
        return experimentRecord;
    }

    public Result<Record> findGenotypeFromID(int genotypeID) {
        Result<Record> genotypeRecord = ConnectDb.getDslContext().fetch("select * from genotype where genotype_id=" + genotypeID);
        return genotypeRecord;
    }

    public Result<Record> findExperimentFromOrganism(String organism) {
        Result<Record> experimentRecord = ConnectDb.getDslContext().fetch("select * from experiment where organism_name='" + organism + "'");
        return experimentRecord;
    }

    public Result<Record> findGenotypesFromOrganism(String organism) {
        Result<Record> genotypeRecord = ConnectDb.getDslContext().fetch("select * from genotype where organism_name='" + organism + "' order by genotype_name");
        return genotypeRecord;
    }

    public ArrayList<String> findOrganismsWithGenotypes() {
        Result<Record> orgRecord = ConnectDb.getDslContext().fetch("select distinct(organism_name) from genotype");
        ArrayList<String> organismList = new ArrayList<>();
        for (Record r : orgRecord) {
            organismList.add((String) r.getValue("organism_name"));
        }
        return organismList;
    }

    public Result<Record> findGenotypeID(String genotypeName, String organism) {
        Result<Record> genotypeRecord = ConnectDb.getDslContext().fetch("select * from genotype where " +
                "genotype_name = '" + genotypeName + "' and organism_name = '" + organism + "'");
        return genotypeRecord;
    }

    public Result<Record> findDistinctGenotypesOfExperiment(String experimentCode, String organism) {
        String query = "select distinct(genotype_name) from seed s " +
                "left outer join genotype g on s.genotype_id = g.genotype_id " +
                "inner join experiment e on s.experiment_id = e.experiment_id " +
                "where e.experiment_code = '" + experimentCode + "' " +
                "and e.organism_name = '" + organism + "'";
        Result<Record> genotypeRecord = ConnectDb.getDslContext().fetch(query);
        return genotypeRecord;
    }

    public ArrayList<String> findOrgsHavingSeed() {
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select distinct(organism_name) from experiment e inner join seed s on " +
                "e.experiment_id=s.experiment_id");
        ArrayList<String> organismList = new ArrayList<>();
        for (Record r : seedRecord) {
            organismList.add((String) r.getValue("organism_name"));
        }
        return organismList;
    }

    public Result<Record> findOrganism(String organismName) {
        Result<Record> organismRecord = ConnectDb.getDslContext().fetch("select * from organism where organism_name = '" + organismName + "'");
        return organismRecord;
    }

    public boolean checkGenotypeExists(String organism, String genotype) {
        Result<Record> genotypeRecord = this.findGenotypeID(genotype, organism);
        if (genotypeRecord == null || genotypeRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void updateGenotype(String organism, String oldGenotype, String newGenotype)
    {
        String query = "update genotype set genotype_name = '" + newGenotype + "' where organism_name = '" + organism +
                "' and genotype_name = '" + oldGenotype + "'";
        ConnectDb.getDslContext().execute(query);
    }

    public void updateOrganism(String organismNameNew, String code, String speciesNew, String subspeciesNew, String descriptionNew,
                               String selectedOrganism) {
        if (speciesNew.isEmpty()) {
            speciesNew = "NULL";
        } else {
            speciesNew = "'" + speciesNew + "'";
        }
        if (subspeciesNew.isEmpty()) {
            subspeciesNew = "NULL";
        } else {
            subspeciesNew = "'" + subspeciesNew + "'";
        }
        if (descriptionNew.isEmpty()) {
            descriptionNew = "NULL";
        } else {
            descriptionNew = "'" + descriptionNew + "'";
        }
        String query = "update organism set organism_name='" + organismNameNew + "',species_code='" + code +
                "',species=" + speciesNew + ",subspecies=" + subspeciesNew + ",description=" + descriptionNew
                + " where organism_name='" + selectedOrganism + "'";
        ConnectDb.getDslContext().execute(query);
    }

    public Result<Record> selectAllExperiments() {
        Result<Record> experimentRecord = ConnectDb.getDslContext().fetch("select distinct experiment_code from experiment");
        return experimentRecord;
    }

    public void insertNewOrganism(String organism, String speciesCode, String species, String subspecies, String description) {
        if (species.isEmpty()) {
            species = "NULL";
        } else {
            species = "'" + species + "'";
        }
        if (subspecies.isEmpty()) {
            subspecies = "NULL";
        } else {
            subspecies = "'" + subspecies + "'";
        }
        if (description.isEmpty()) {
            description = "NULL";
        } else {
            description = "'" + description + "'";
        }

        String query = "insert into organism values ('" + organism + "','" + speciesCode + "'," + species + "," + subspecies + "," + description + ")";
        ConnectDb.getDslContext().execute(query);
    }

    public Result<Record> findOrganismByCode(String code) {
        Result<Record> organismRecord = ConnectDb.getDslContext().fetch("select * from organism where species_code='" + code + "'");
        return organismRecord;
    }

    public Result<Record> findOrganismBySpecies(String species, String subspecies) {
        String subspeciesString;
        if (subspecies.isEmpty()) {
            subspeciesString = " is null";
        } else {
            subspeciesString = "='" + subspecies + "'";
        }
        Result<Record> organismRecord = ConnectDb.getDslContext().fetch("select * from organism where species='" + species + "' and subspecies" + subspeciesString);
        return organismRecord;
    }

    public boolean checkOrganismExists(String organismName) {
        Result<Record> organismRecord = this.findOrganism(organismName);
        if (organismRecord == null || organismRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkOrganismCodeExists(String code) {
        Result<Record> organismRecord = this.findOrganismByCode(code);
        if (organismRecord == null || organismRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSpeciesExists(String species, String subspecies) {
        Result<Record> organismRecord = this.findOrganismBySpecies(species, subspecies);
        if (organismRecord == null || organismRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void insertExperiment(String organism, String experimentCode, String description, String user) {
        UserDBFunctions udbf = new UserDBFunctions();
        int userID = (int) udbf.findUserFromName(user).get(0).getValue("user_id");

        Result<Record> record = ConnectDb.getDslContext().fetch("select max(experiment_id) max from experiment;");
        int max = (int) record.get(0).get("max");
        max = max + 1;
        if (description.isEmpty()) {
            description = "NULL";
        } else {
            description = "'" + description + "'";
        }
        String query = "insert into experiment values(" + max + ",'" + experimentCode + "','" + organism + "'," + userID + "," + description + ")";
        ConnectDb.getDslContext().execute(query);
    }

    public void insertGenotype(String organism, String genotypeName) {
        String query = "insert into genotype (genotype_name, organism_name) values ('" + genotypeName + "', '" + organism + "')";
        ConnectDb.getDslContext().execute(query);
    }

    public void insertSeed(String organism, String experiment, String seed, int genotypeID, Double dryshoot, Double dryroot,
                           Double wetshoot, Double wetroot, String sterilizationChamber, String imagingIntervalUnit, String description,
                           Date imagingStartDate) {
        String genotypeString = "NULL";
        if (genotypeID > 0) {
            genotypeString = String.valueOf(genotypeID);
        }

        String dryShootString = "NULL";
        if (dryshoot >= 0) {
            dryShootString = String.valueOf(dryshoot);
        }
        String dryRootString = "NULL";
        if (dryroot >= 0) {
            dryRootString = String.valueOf(dryroot);
        }
        String wetShootString = "NULL";
        if (wetshoot >= 0) {
            wetShootString = String.valueOf(wetshoot);
        }
        String wetRootString = "NULL";
        if (wetroot >= 0) {
            wetRootString = String.valueOf(wetroot);
        }

        String imagingStartDateString = "NULL";
        if (imagingStartDate != null) {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            imagingStartDateString = new SimpleDateFormat(DATE_FORMAT).format(imagingStartDate);
            imagingStartDateString = "'" + imagingStartDateString + "'";
        }

        if (sterilizationChamber.isEmpty() || sterilizationChamber == null) {
            sterilizationChamber = "NULL";
        } else {
            sterilizationChamber = "'" + sterilizationChamber + "'";
        }

        if (description.isEmpty() || description == null) {
            description = "NULL";
        } else {
            description = "'" + description + "'";
        }

        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select max(seed_id) max from seed");
        int max = (int) seedRecord.get(0).get("max");
        max = max + 1;
        Result<Record> expRecord = findExperiment(experiment, organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        String query = "insert into seed values(" + max + "," + expID + ",'" + seed + "'," + genotypeString + "," + dryShootString + "," + dryRootString +
                "," + wetShootString + "," + wetRootString + "," + sterilizationChamber + "," + description + ",'" + imagingIntervalUnit + "'," + imagingStartDateString + ")";
        ConnectDb.getDslContext().execute(query);
    }

    public boolean checkSeedExists(String organism, String experimentCode, String seed) {
        Result<Record> expRecord = findExperiment(experimentCode, organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select * from seed where experiment_id=" + expID + " and seed_name='" + seed + "'");
        if (seedRecord == null || seedRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkOrganismIdentifiersExist(String organism, String organismCode, String species, String subspecies, String selectedOrganism) {
        String subspeciesString;
        if (subspecies.isEmpty()) {
            subspeciesString = " is null";
        } else {
            subspeciesString = "='" + subspecies + "'";
        }
        Result<Record> orgRecord = ConnectDb.getDslContext().fetch("select * from organism " +
                "where (organism_name='" + organism + "' " +
                "or species_code='" + organismCode + "' " +
                "or (species='" + species + "' and subspecies" + subspeciesString + ")) " +
                "and organism_name <> '" + selectedOrganism + "'");
        if (orgRecord == null || orgRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkOrgandExpPairExists(String organism, String experiment) {
        Result<Record> expRecord = ConnectDb.getDslContext().fetch("select * from experiment where organism_name='" + organism + "' and " +
                "experiment_code='" + experiment + "'");
        if (expRecord == null || expRecord.size() == 0) {
            return false;
        } else {
            return true;
        }

    }

    public Result<Record> findOrganismByExperiment(String experiment) {
        Result<Record> expRECORD = ConnectDb.getDslContext().fetch("select * from experiment where experiment_code='" + experiment + "'");
        return expRECORD;
    }

    public void updateExperiment(String experimentOld, String organism, String experimentNew, String desc) {
        if (desc == null) {
            desc = "NULL";
        } else {
            desc = "'" + desc + "'";
        }
        String query = "update experiment set experiment_code='" + experimentNew + "',description=" + desc
                + " where experiment_code='" + experimentOld + "' and organism_name='" + organism + "'";
        ConnectDb.getDslContext().execute(query);
    }

    public Result<Record> findSeedFromExperimentID(int expID) {
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select * from seed where experiment_id=" + expID);
        return seedRecord;
    }

    public Result<Record> findSeedsFromOrganismExperimentGenotype(String organism, String experiment, String genotypeName) {
        String query = "";
        if (genotypeName.equals("None")) {
            query = "select * from seed s inner join experiment e on s.experiment_id = e.experiment_id where e.experiment_code = '" + experiment
                    + "' and e.organism_name = '" + organism + "' and s.genotype_id is null";
        }
        else {
            query = "select * from seed s inner join genotype g on g.genotype_id = s.genotype_id inner join experiment e on " +
                    "e.experiment_id = s.experiment_id where e.experiment_code = '" + experiment + "' and g.organism_name = '" + organism +
                    "' and g.genotype_name = '" + genotypeName + "'";
        }
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch(query);
        return seedRecord;
    }

    public Result<Record> findSeedFromDatasetID(int datasetID)
    {
        String query = "select g.genotype_name,s.dry_shoot,s.dry_root,s.wet_shoot,s.wet_root,s.sterilization_chamber," +
                "s.description,s.imaging_interval_unit,s.imaging_start_date from seed s inner join dataset d on" +
                " s.seed_id=d.seed_id left outer join genotype g " +
                "on s.genotype_id=g.genotype_id where d.dataset_id=" + datasetID ;
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch(query);
        return seedRecord;
    }

    public Result<Record> findSeedInfoFromRunID(int runID)
    {
        String query = "select * from program_run where run_id=" + runID;
        Result<Record> datasetRecord = ConnectDb.getDslContext().fetch(query);
        Record r = datasetRecord.get(0);
        int datasetID = (int) r.getValue("dataset_id");
        Result<Record> seedRecord = this.findSeedFromDatasetID(datasetID);
        return seedRecord;
    }

    public Result<Record> findSeedMetadataFromOrgExpSeed(String organism, String experiment, String seed) {
        Result<Record> expRecord = this.findExperiment(experiment, organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select * from seed where experiment_id=" + expID + " and seed_name='" + seed + "'");
        return seedRecord;
    }

    public void updateSeed(String oldSeed, String organism, String experiment, String seed, int genotypeID, Double dryshoot,
                           Double dryroot,
                           Double wetshoot, Double wetroot, String sterilizationChamber, String imagingIntervalUnit, String description,
                           Date imagingStartDate) {
        String genotypeString = "NULL";
        if (genotypeID > 0) {
            genotypeString = String.valueOf(genotypeID);
        }

        String dryShootString = "NULL";
        if (dryshoot >= 0) {
            dryShootString = String.valueOf(dryshoot);
        }
        String dryRootString = "NULL";
        if (dryroot >= 0) {
            dryRootString = String.valueOf(dryroot);
        }
        String wetShootString = "NULL";
        if (wetshoot >= 0) {
            wetShootString = String.valueOf(wetshoot);
        }
        String wetRootString = "NULL";
        if (wetroot >= 0) {
            wetRootString = String.valueOf(wetroot);
        }

        String imagingStartDateString = "NULL";
        if (imagingStartDate != null) {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            imagingStartDateString = new SimpleDateFormat(DATE_FORMAT).format(imagingStartDate);
            imagingStartDateString = "'" + imagingStartDateString + "'";
        }

        if (sterilizationChamber.isEmpty() || sterilizationChamber == null) {
            sterilizationChamber = "NULL";
        } else {
            sterilizationChamber = "'" + sterilizationChamber + "'";
        }

        if (description.isEmpty() || description == null) {
            description = "NULL";
        } else {
            description = "'" + description + "'";
        }

        int expID = (int) this.findExperiment(experiment, organism).get(0).getValue("experiment_id");
        String query = "update seed set seed_name='" + seed + "',genotype_id=" + genotypeString + ",dry_shoot=" + dryShootString + ",dry_root=" + dryRootString +
                ",wet_shoot=" + wetShootString + ",wet_root=" + wetRootString + ",sterilization_chamber=" + sterilizationChamber +
                ",imaging_interval_unit='" + imagingIntervalUnit + "',description=" + description + ",imaging_start_date=" + imagingStartDateString +
                " where experiment_id =" + expID + " and seed_name ='" + oldSeed + "'";
        ConnectDb.getDslContext().execute(query);
    }

    public boolean checkOrgExpSeedTripleExists(String org, String exp, String seed) {
        Result<Record> expRecord = this.findExperiment(exp, org);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select * from seed where experiment_id=" + expID + " and seed_name='" + seed + "'");
        if (seedRecord == null || seedRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSeedTimepointImageTypeExists(String organism, String experiment, String seed, String timepoint, String imageType) {
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism, experiment, seed).get(0).getValue("seed_id");
        String query = "select * from dataset d inner join dataset_image_type dit on d.dataset_id = dit.dataset_id where d.seed_id=" +
                seedID + " and d.timepoint='" + timepoint + "' and dit.image_type='" + imageType + "'";
        Result<Record> resultRecord = ConnectDb.getDslContext().fetch(query);
        if (resultRecord == null || resultRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkSeedTimepointExists(String organism, String experiment, String seed, String timepoint) {
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism, experiment, seed).get(0).getValue("seed_id");
        Result<Record> datasetRecord = ConnectDb.getDslContext().fetch("select * from dataset where seed_id=" + seedID + " and timepoint='" + timepoint +
                "'");
        if (datasetRecord == null || datasetRecord.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Result<Record> findDataset(String organism, String experiment, String seed, String timepoint) {
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism, experiment, seed).get(0).getValue("seed_id");
        Result<Record> datasetRecord = ConnectDb.getDslContext().fetch("select * from dataset where seed_id=" + seedID + " and timepoint='" +
                timepoint + "'");
        return datasetRecord;
    }

    public void insertDatasetImagePaths(String organism, String experiment, String seed, String timepoint, String imageType) {
        int datasetID = (int) this.findDataset(organism, experiment, seed, timepoint).get(0).getValue("dataset_id");
        ConnectDb.getDslContext().execute("insert into dataset_image_type values(" + datasetID + ",'" + imageType + "')");
    }

    public void insertDataset(String organism, String experiment, String seed, String timepoint, String imageType) {
        Result<Record> datasetRecord = ConnectDb.getDslContext().fetch("select max(dataset_id) max from dataset");
        int maxDatasetID = 0;
        if (datasetRecord.get(0).getValue("max") != null) {
            maxDatasetID = (int) datasetRecord.get(0).getValue("max");

        }
        int datasetID = maxDatasetID + 1;
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism, experiment, seed).get(0).getValue("seed_id");
        ConnectDb.getDslContext().execute("insert into dataset values(" + datasetID + "," + seedID + ",'" + timepoint + "')");
        ConnectDb.getDslContext().execute("insert into dataset_image_type values(" + datasetID + ",'" + imageType + "')");
    }

    public void insertSeedFromUpload(String organism, String experiment, String seed, String timepoint, String imageType) {
        String imagingIntervalUnit = null;
        if (timepoint.substring(0, 1).equals("d")) {
            imagingIntervalUnit = "day";
        } else if (timepoint.substring(0, 1).equals("t")) {
            imagingIntervalUnit = "hour";
        }
        Result<Record> seedRecord = ConnectDb.getDslContext().fetch("select max(seed_id) max from seed");
        int maxSeedID = 0;
        if (seedRecord.get(0).getValue("max") != null) {
            maxSeedID = (int) seedRecord.get(0).getValue("max");

        }
        int expID = (int) this.findExperiment(experiment, organism).get(0).getValue("experiment_id");
        int seedID = maxSeedID + 1;

        ConnectDb.getDslContext().execute("insert into seed values(" + seedID + "," + expID + ",'" + seed + "',NULL,NULL,NULL,NULL,NULL," +
                "NULL,NULL,'" + imagingIntervalUnit + "',NULL)");
        this.insertDataset(organism, experiment, seed, timepoint, imageType);
    }

    public void insertExperimentFromUpload(String organism, String experiment, String seed, String timepoint, String imageType, String userName) {
        Result<Record> expRecord = ConnectDb.getDslContext().fetch("select max(experiment_id) max from experiment");
        int maxExpID = 0;
        if (expRecord.get(0).getValue("max") != null) {
            maxExpID = (int) expRecord.get(0).getValue("max");
        }
        int expID = maxExpID + 1;
        UserDBFunctions udf = new UserDBFunctions();
        Result<Record> userRecord = udf.findUserFromName(userName);
        int userID = (int) userRecord.get(0).getValue("user_id");
        ConnectDb.getDslContext().execute("insert into experiment values(" + expID + ",'" + experiment + "','" + organism + "'," +
                userID + ",NULL)");
        this.insertSeedFromUpload(organism, experiment, seed, timepoint, imageType);
    }

    public boolean addNewImageSet(String organism, String experiment, String seed, String timepoint, String imageType, String userName) {
        boolean expTableCheck = this.checkOrgandExpPairExists(organism, experiment);
        if (expTableCheck == true) {
            boolean seedCheck = this.checkSeedExists(organism, experiment, seed);
            if (seedCheck == true) {
                boolean datasetTableCheck = this.checkSeedTimepointExists(organism, experiment, seed, timepoint);
                if (datasetTableCheck == true) {
                    boolean datasetImageTypeTableCheck = this.checkSeedTimepointImageTypeExists(organism, experiment, seed, timepoint, imageType);
                    if (datasetImageTypeTableCheck == true) {
                        return false;
                    } else {
                        this.insertDatasetImagePaths(organism, experiment, seed, timepoint, imageType);
                    }
                } else {
                    this.insertDataset(organism, experiment, seed, timepoint, imageType);
                }
            } else {
                this.insertSeedFromUpload(organism, experiment, seed, timepoint, imageType);
            }
        } else {
            this.insertExperimentFromUpload(organism, experiment, seed, timepoint, imageType, userName);
        }
        return true;
    }
}
