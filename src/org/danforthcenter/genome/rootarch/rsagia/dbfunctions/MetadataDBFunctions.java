package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MetadataDBFunctions {

    DSLContext dslContext;

    public MetadataDBFunctions()
    {
        dslContext = ConnectDb.getDslContext();
    }
    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    public boolean isNumeric(String s) {
        boolean ans =  s.matches("[-+]?\\d*\\.?\\d+");
        return ans;
    }
    public Result<Record> selectAllOrganism()
    {
        Result<Record> organismRecord = dslContext.fetch("select * from organism");
        return organismRecord;
    }
    public Result<Record> findExperiment(String experimentCode,String organism)
    {
        Result<Record> experimentRecord = dslContext.fetch("select * from experiment where experiment_code='" +
                experimentCode + "' and organism_name='" + organism + "'");
        return experimentRecord;
    }
    public Result<Record> findExperimentFromOrganism(String organism)
    {
        Result<Record> experimentRecord = dslContext.fetch("select * from experiment where organism_name='" + organism + "'");
        return  experimentRecord;
    }
    public ArrayList<String> findOrgsHavingSeed()
    {
        Result<Record> seedRecord = dslContext.fetch("select distinct(organism_name) from experiment e inner join seed s on " +
                "e.experiment_id=s.experiment_id");
        ArrayList<String> organismList = new ArrayList<>();
        for(Record r: seedRecord)
        {
            organismList.add((String) r.getValue("organism_name"));
        }
        return organismList;
    }
    public Result<Record> findOrganism(String organismName)
    {
        Result<Record> organismRecord = dslContext.fetch("select * from organism where organism_name='" +
                organismName + "'");
        return organismRecord;
    }
    public void updateOrganism(String organismNameNew, String code, String speciesNew,String subspeciesNew,String varietyNew,
                               String selectedOrganism)
    {
        if(speciesNew.isEmpty())
        {
            speciesNew = "NULL";
        }
        else
        {
            speciesNew = "'" + speciesNew + "'";
        }
        if(subspeciesNew.isEmpty())
        {
            subspeciesNew = "NULL";
        }
        else
        {
            subspeciesNew = "'" + subspeciesNew + "'";
        }
        if(varietyNew.isEmpty())
        {
            varietyNew = "NULL";
        }
        else
        {
            varietyNew = "'" + varietyNew + "'";
        }
        String query = "update organism set organism_name='" + organismNameNew + "',species_code='"+code+
                "',species=" + speciesNew + ",subspecies=" + subspeciesNew + ",variety=" + varietyNew
                + " where organism_name='" + selectedOrganism + "'";
        dslContext.execute(query);
    }
    public Result<Record> selectAllUsers()
    {
        Result<Record> userRecord = dslContext.fetch("select * from user");

        return userRecord;
    }
    public Result<Record>  selectAllExperiments()
    {
        Result<Record> experimentRecord = dslContext.fetch("select * from experiment");
        return experimentRecord;
    }
    public void insertNewOrganism(String organism, String speciesCode, String species, String subspecies, String variety)
    {
        if(species.isEmpty())
        {
            species = "NULL";
        }
        else
        {
            species = "'" + species +"'";
        }
        if(subspecies.isEmpty())
        {
            subspecies = "NULL";
        }
        else
        {
            subspecies = "'" + subspecies +"'";
        }
        if(variety.isEmpty())
        {
            variety = "NULL";
        }
        else
        {
            variety = "'" + variety +"'";
        }

        String query = "insert organism values ('" + organism + "','" + speciesCode + "'," + species + ","+ subspecies + "," + variety + ")" ;
        dslContext.execute(query);
    }
    public Result<Record> findOrganismByCode(String code)
    {
        Result<Record> organismRecord = dslContext.fetch("select * from organism where species_code='" +
                code + "'");
        return organismRecord;
    }
    public boolean checkOrganismExists(String organismName)
    {
        Result<Record> organismRecord = this.findOrganism(organismName);
        if(organismRecord == null || organismRecord.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkOrganismCodeExists(String code)
    {
        Result<Record> organismRecord = this.findOrganismByCode(code);
        if(organismRecord == null ||organismRecord.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void insertExperiment(String organism, String experimentCode,String description,String user)
    {
        UserDBFunctions udbf = new UserDBFunctions();
        int userID = (int) udbf.findUserFromName(user).get(0).getValue("user_id");

        Result<Record> record= dslContext.fetch("select max(experiment_id) max from experiment;");
        int max = (int) record.get(0).get("max");
        max= max +1;
        if(description.isEmpty())
        {
            description = "NULL";
        }
        else
        {
            description = "'" + description +"'";
        }
        String query = "insert experiment values(" + max +",'" + experimentCode +"','" + organism + "'," + userID + "," +description + ")";
        dslContext.execute(query);
    }
    public Result<Record> findDistinctTimepointValues()
    {
        Result<Record> timepoints = dslContext.fetch("select distinct(imaging_interval_unit) from seed");
        return timepoints;
    }

    public void insertSeed(String organism,String experiment,String seed,String genotype,Double dryshoot,Double  dryroot,
                           Double wetshoot, Double wetroot,Double sterilizationChamber, String imagingIntervalUnit,String description,
                           Date imagingStartDate)
    {
        String imagingStartDateString ="NULL";
        if(genotype.isEmpty()||genotype.equals(null))
        {
            genotype = "NULL";
        }
        else
        {
            genotype = "'" + genotype + "'";
        }
        if(description.isEmpty() || description.equals(null))
        {
            description = "NULL";
        }
        else
        {
            description = "'" + description +"'";
        }
        if(!imagingStartDate.equals(null))
        {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            imagingStartDateString = new SimpleDateFormat(DATE_FORMAT).format(imagingStartDate);
            imagingStartDateString = "'" + imagingStartDateString + "'";
        }

        Result<Record> seedRecord = dslContext.fetch("select max(seed_id) max from seed");
        int max = (int) seedRecord.get(0).get("max");
        max = max +1;
        Result<Record> expRecord = findExperiment(experiment,organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        String query = "insert into seed values(" + max + "," + expID + ",'" + seed + "'," +genotype + "," + dryshoot + "," + dryroot +
                "," + wetshoot + "," + wetroot + "," + sterilizationChamber + "," + description + ",'" + imagingIntervalUnit + "'," +imagingStartDateString +")";
        dslContext.execute(query);
    }
    public boolean checkSeedExists(String organism,String experimentCode,String seed)
    {
        Result<Record> expRecord = findExperiment(experimentCode,organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = dslContext.fetch("select * from seed where experiment_id=" + expID + " and seed_name='" + seed + "'");
        if(seedRecord == null || seedRecord.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkOrgCodeAndOrganismExists(String organism, String organismCode, String selectedOrganism)
    {
        Result<Record> orgRecord = dslContext.fetch("select * from organism " +
                "where (organism_name='" + organism + "' or species_code='" + organismCode + "') " +
                "and organism_name <> '" + selectedOrganism + "'");
        if(orgRecord == null || orgRecord.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkOrgandExpPairExists(String organism,String experiment)
    {
        Result<Record> expRecord = dslContext.fetch("select * from experiment where organism_name='" + organism + "' and " +
                "experiment_code='" + experiment + "'");
        if(expRecord == null || expRecord.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public Result<Record> findOrganismByExperiment(String experiment)
    {
        Result<Record> expRECORD = dslContext.fetch("select * from experiment where experiment_code='" + experiment + "'");
        return  expRECORD;
    }
    public void updateExperiment(String experimentOld,String experimentNew,String desc)
    {
        if(desc == null)
        {
            desc = "NULL";
        }
        else
        {
            desc = "'" + desc + "'";
        }
        String query = "update experiment set experiment_code='" + experimentNew + "',description=" +desc
                + " where experiment_code='" + experimentOld + "'";
        dslContext.execute(query);
    }
    public Result<Record> findSeedFromExperimentID(int expID)
    {
        Result<Record> seedRecord = dslContext.fetch("select * from seed where experiment_id=" + expID);
        return seedRecord;
    }
    public Result<Record> findSeedMetadataFromOrgExpSeed(String organism,String experiment,String seed)
    {
        Result<Record> expRecord = this.findExperiment(experiment,organism);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = dslContext.fetch("select * from seed where experiment_id=" + expID + " and seed_name='" + seed + "'");
        return seedRecord;
    }

    public void updateSeed(String oldSeed, String organism, String experiment,String seed, String genotype, Double dryshoot,
                           Double dryroot,
                           Double wetshoot, Double wetroot,Double sterilizationChamber, String imagingIntervalUnit,String description,
                           Date imagingStartDate)
    {
        int expID = (int) this.findExperiment(experiment,organism).get(0).getValue("experiment_id");
        String imagingStartDateString = "NULL";
        if(genotype.isEmpty()||genotype.equals(null))
        {
            genotype = "NULL";
        }
        else
        {
            genotype = "'" + genotype + "'";
        }
        if(description.isEmpty() || description.equals(null))
        {
            description = "NULL";
        }
        else
        {
            description = "'" + description +"'";
        }
        if(!imagingStartDate.equals(null))
        {
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            imagingStartDateString = new SimpleDateFormat(DATE_FORMAT).format(imagingStartDate);
            imagingStartDateString = "'" + imagingStartDateString + "'";
        }

        String query = "update seed set seed_name='" + seed + "',genotype=" + genotype +",dry_shoot=" + dryshoot + ",dry_root=" + dryroot+
                ",wet_shoot=" + wetshoot + ",wet_root=" + wetroot + ",sterilization_chamber=" +sterilizationChamber +
                ",imaging_interval_unit='" + imagingIntervalUnit + "',description=" + description + ",imaging_start_date=" + imagingStartDateString +
                " where experiment_id =" + expID + " and seed_name ='" +oldSeed + "'";
        dslContext.execute(query);
    }
    public boolean checkOrgExpSeedTripleExists(String org,String exp,String seed)
    {
        Result<Record> expRecord = this.findExperiment(exp,org);
        int expID = (int) expRecord.get(0).getValue("experiment_id");
        Result<Record> seedRecord = dslContext.fetch("select * from seed where experiment_id=" +expID + " and seed_name='" +seed + "'");
        if(seedRecord == null || seedRecord.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public Result<Record> findProgramID(String appName)
    {
        Result<Record> programRecord = dslContext.fetch("select * from program where name='" + appName + "'");
        return  programRecord;
    }
    public Result<Record> findSavedTemplateFromName(String tempName,String appName)
    {
        Result<Record> prgRec = this.findProgramID(appName);
        int programID = (int) prgRec.get(0).getValue("program_id");
        Result<Record> configRecord = dslContext.fetch("select * from saved_config where program_id=" + programID + " and name='"+
        tempName + "'");
        return configRecord;
    }
    public boolean checkSeedTimepointImageTypeExists(String organism,String experiment,String seed, String timepoint,String imageType){
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism,experiment,seed).get(0).getValue("seed_id");
        String query = "select * from dataset d inner join dataset_image_type dit on d.dataset_id = dit.dataset_id where d.seed_id=" +
                seedID + " and d.timepoint'" + timepoint + "' and dit.image_type='" + imageType + "'";
        Result<Record> resultRecord = dslContext.fetch(query);
        if(resultRecord != null || resultRecord.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkSeedTimepointExists(String organism, String experiment,String seed, String timepoint)
    {
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism,experiment,seed).get(0).getValue("seed_id");
        Result<Record> datasetRecord = dslContext.fetch("select * from dataset where seed_id="+seedID + " and timepoint='" + timepoint +
                "'");
        if(datasetRecord!=null || datasetRecord.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public Result<Record> findDataset(String organism,String experiment,String seed,String timepoint)
    {
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism,experiment,seed).get(0).getValue("seed_id");
        Result<Record> datasetRecord = dslContext.fetch("select * from dataset where seed_id=" + seedID + " and timepoint='"+
        timepoint + "'");
        return  datasetRecord;
    }
    public void insertDatasetImagePaths(String organism, String experiment,String seed, String timepoint,String imageType)
    {
        int datasetID = (int) this.findDataset(organism,experiment,seed,timepoint).get(0).getValue("dataset_id");
        dslContext.execute("insert into dataset_image_type values(" + datasetID + ",'"+imageType + "')");
    }
    public void insertDataset(String organism, String experiment,String seed, String timepoint,String imageType)
    {
        Result<Record> datasetRecord = dslContext.fetch("select max(dataset_id) max from dataset");
        int maxDatasetID = 0;
        if(datasetRecord.get(0).getValue("max") != null)
        {
            maxDatasetID = (int) datasetRecord.get(0).getValue("max");

        }
        int datasetID = maxDatasetID +1;
        int seedID = (int) this.findSeedMetadataFromOrgExpSeed(organism,experiment,seed).get(0).getValue("seed_id");
        dslContext.execute("insert into dataset values(" + datasetID + "," + seedID + ",'" + timepoint + "')");
        dslContext.execute("insert into dataset_image_type values(" + datasetID + ",'"+imageType + "')");
    }
    public void insertSeedFromUpload(String organism, String experiment,String seed, String timepoint,String imageType)
    {
        String imagingIntervalUnit = null;
        if(timepoint.substring(0,1).equals("d"))
        {
            imagingIntervalUnit = "day";
        }
        else if(timepoint.substring(0,1).equals("t"))
        {
            imagingIntervalUnit  = "hour";
        }
        Result<Record> seedRecord = dslContext.fetch("select max(seed_id) max from seed");
        int maxSeedID = 0;
        if(seedRecord.get(0).getValue("max")!= null)
        {
            maxSeedID = (int) seedRecord.get(0).getValue("max");

        }
        int expID = (int) this.findExperiment(experiment,organism).get(0).getValue("experiment_id");
        int seedID = maxSeedID + 1;

        dslContext.execute("insert into seed values(" + seedID + "," + expID + ",'" + seed + "',NULL,NULL,NULL,NULL,NULL," +
                "NULL,NULL,'"+imagingIntervalUnit+"',NULL)");
        this.insertDataset(organism,experiment,seed,timepoint,imageType);
    }
    public void insertExperimentFromUpload(String organism,String experiment,String seed,String timepoint, String imageType,String userName)
    {
        Result<Record> expRecord = dslContext.fetch("select max(experiment_id) max from experiment");
        int maxExpID = 0;
        if(expRecord.get(0).getValue("max") != null)
        {
            maxExpID = (int) expRecord.get(0).getValue("max");
        }
        int expID = maxExpID + 1;
        UserDBFunctions udf = new UserDBFunctions();
        Result<Record> userRecord = udf.findUserFromName(userName);
        int userID = (int) userRecord.get(0).getValue("user_id");
        dslContext.execute("insert into experiment values(" + expID + ",'" + experiment + "','" + organism + "',"+
                userID + ",NULL)");
        this.insertSeedFromUpload(organism,experiment,seed,timepoint,imageType);
    }

    public boolean addNewImageSet(String organism, String experiment,String seed,String timepoint,String imageType, String userName)
    {
        boolean check = true;

        boolean expTableCheck = this.checkOrgandExpPairExists(organism,experiment);
        if(expTableCheck == true)
        {
            boolean seedCheck = this.checkSeedExists(organism,experiment,seed);
            if(seedCheck == true)
            {
                boolean datasetTableCheck = this.checkSeedTimepointExists(organism,experiment,seed,timepoint);
                if(datasetTableCheck == true)
                {
                    boolean datasetImageTypeTableCheck = this.checkSeedTimepointImageTypeExists(organism,experiment,seed,timepoint,imageType);
                    if(datasetImageTypeTableCheck == true)
                    {
                        return false;
                    }
                    else
                    {
                        this.insertDatasetImagePaths(organism,experiment,seed,timepoint,imageType);
                    }
                }
                else
                {
                    this.insertDataset(organism,experiment,seed,timepoint,imageType);
                 }
            }
            else
            {
               this.insertSeedFromUpload(organism,experiment,seed,timepoint,imageType);

            }
        }
        else
        {
            this.insertExperimentFromUpload(organism,experiment,seed,timepoint,imageType,userName);

        }
        return check;
    }
    public Result<Record> selectAllDatasetImageTypeTable()
    {
        Result<Record> ditRecord = dslContext.fetch("select * from dataset_image_type");
        return ditRecord;
    }
}
