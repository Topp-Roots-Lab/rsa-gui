package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.danforthcenter.genome.rootarch.rsagia.db.enums.DatasetCountConditionType;
import org.danforthcenter.genome.rootarch.rsagia.db.enums.SeedExperimentTimepointValue;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.SavedConfig;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.User;
import org.danforthcenter.genome.rootarch.rsagia2.*;
import org.jooq.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Dataset.DATASET;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Experiment.EXPERIMENT;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism.ORGANISM;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Seed.SEED;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetImagePaths.DATASET_IMAGE_PATHS;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetCount.DATASET_COUNT;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramRun.PROGRAM_RUN;



public class FillDb {
    private File baseDir;
    private File originalImagesPath;
    private File processedImagesPath;
    private File rsaGiaTemplatesPath;
    private Properties sysProps;
    private String[] speciesArray;
    private String[] experimentArray;
    private String[] seedArray;
    private DSLContext dslContext;

    public FillDb() throws IOException {
        FileInputStream fis1 = null;
        this.sysProps = new Properties();

        fis1 = new FileInputStream(new File("default.properties"));
        this.sysProps.load(fis1);
        this.baseDir = new File(this.sysProps.getProperty("base_dir"));
        this.originalImagesPath = new File(this.baseDir.getAbsolutePath() + File.separator
                + "original_images"
//                + File.separator + this.preferredType
        );
        this.processedImagesPath = new File(this.baseDir.getAbsolutePath() + File.separator + "processed_images");
        this.rsaGiaTemplatesPath = new File(this.baseDir.getAbsolutePath() + File.separator + "rsa-gia-templates");
        ConnectDb dbConnection = new ConnectDb();
        dslContext = dbConnection.getDslContext();
    }

    public void fillUserTable(ArrayList<RsaImageSet> riss, ApplicationManager am)
    {
        int i = 1;
        i = this.fillPrepUserTable(i,riss,am,true,false);
        i = this.fillPrepUserTable(i,riss,am,false,true);
    }
    public int fillPrepUserTable(int i,ArrayList<RsaImageSet> riss, ApplicationManager am, boolean doSaved,boolean doSandbox)
    {
        Boolean red = false;
        for(RsaImageSet ris:riss) {
            ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris, doSaved, doSandbox, null, red);
            for (OutputInfo oi : ois) {
                String username = oi.getUser();
                Result<Record> userRecord = dslContext.fetch("select * from user where user_name='" + username + "'");
                if (userRecord.size() == 0) {
                    String query = "insert into user values(" + i + ",'" + username + "','" + username+"','"+username+"','topplab')";
                    dslContext.execute(query);
                    i = i + 1;
                }
            }
        }
        return i;
    }

    //organism,experiment,seed tables
    public void fillTables1() {
        File[] ss = this.originalImagesPath.listFiles();
        int i = 1;
        int j = 1;
        if (ss != null && ss.length > 0) {
            for (File s : ss) {
                String species_name = s.getName();
                dslContext.insertInto(ORGANISM, ORGANISM.ORGANISM_NAME, ORGANISM.SPECIES, ORGANISM.SUBSPECIES, ORGANISM.VARIETY)
                        .values(species_name, "species", null, null).execute();
                //dslContext.execute("insert into organism(organism_name,species,subspecies,variety) values('" + species + "','species',NULL,NULL)");
                File[] exps = s.listFiles();
                if (exps != null && exps.length > 0) {
                    for (File exp : exps) {
                        String experiment_name = exp.getName();
                        dslContext.insertInto(EXPERIMENT, EXPERIMENT.EXPERIMENT_CODE, EXPERIMENT.USER_ID, EXPERIMENT.DESCRIPTION)
                                .values(experiment_name, 1, "desc").execute();
                        File[] seeds = exp.listFiles();
                        if (seeds != null && seeds.length > 0) {
                            for (File seed : seeds) {
                                String seed_name = seed.getName();
                                dslContext.insertInto(SEED, SEED.SEED_ID, SEED.SEED_NAME, SEED.DESCRIPTION, SEED.EXPERIMENT_START_DATE, SEED.EXPERIMENT_TIMEPOINT_VALUE, SEED.GENOTYPE, SEED.DRY_SHOOT, SEED.DRY_ROOT, SEED.WET_SHOOT, SEED.WET_ROOT, SEED.STERILIZATION_CHAMBER, ORGANISM.ORGANISM_NAME, EXPERIMENT.EXPERIMENT_CODE)
                                        .values(i, seed_name, "desc", null, SeedExperimentTimepointValue.day, "genotype", null, null, null, null, null, species_name, experiment_name).execute();
                                File[] timeValues = seed.listFiles();
                                if (timeValues != null && timeValues.length > 0) {
                                    for (File timeValue : timeValues) {
                                        String timeValue_name = timeValue.getName();
                                        dslContext.insertInto(DATASET,DATASET.DATASET_ID,DATASET.SEED_ID,DATASET.TIMEPOINT_D_T_VALUE)
                                                .values(j,i,timeValue_name).execute();
                                        if(timeValue_name.substring(0,1).equals("t"))
                                        {
                                            String query = "update seed set experiment_timepoint_value='"+SeedExperimentTimepointValue.hour+"' where seed_id="+i;
                                            dslContext.execute(query);
                                        }
                                        File[] imageTypes = timeValue.listFiles();
                                        if (imageTypes != null && imageTypes.length > 0) {
                                            for (File imageType : imageTypes) {
                                                String imageTypeName = imageType.getName();
                                                dslContext.insertInto(DATASET_IMAGE_PATHS,DATASET_IMAGE_PATHS.DATASET_ID,DATASET_IMAGE_PATHS.IMAGE_TYPE,DATASET_IMAGE_PATHS.IMAGE_PATH)
                                                        .values(j, imageTypeName,imageType.getAbsolutePath()).execute();
                                            }
                                        }
                                        j = j+1;
                                    }
                                }
                                i = i + 1;
                            }
                        }
                    }
                }
            }
        }
    }


    public void fillTables2() {

    }

    public void fillSavedConfigTableGia2d() {
        File gia2dXmlFilesPath = new File(this.rsaGiaTemplatesPath + File.separator + "giaroot_2d");
        File[] gia2dXmlFiles = gia2dXmlFilesPath.listFiles();
        int i = 1;
        for (File gia2dXmlFile : gia2dXmlFiles) {
            String configName = gia2dXmlFile.getName();
            String path = gia2dXmlFile.getAbsolutePath();
            if(File.separator.equals( "\\")) {
                path = path.replaceAll("\\\\", "\\\\\\\\");
            }
            String contents = null;
            try {
                contents = new String(Files.readAllBytes((Paths.get(path))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Query query2 = dslContext.query("insert into saved_config(config_id,program_id,name,contents) values("
                    + i + ",3,'" + configName.substring(0, configName.length() - 4) + "','"+contents+"')");
            dslContext.execute(query2);
            i = i + 1;
        }
    }

    public void fillSavedConfigTableGia3dv2() {
        File gia3dv2XmlFilesPath = new File(this.rsaGiaTemplatesPath + File.separator + "gia3d_v2");
        File[] gia3dv2XmlFiles = gia3dv2XmlFilesPath.listFiles();
        int result = dslContext.fetchCount(SavedConfig.SAVED_CONFIG) + 1;
        for (File gia3dv2XmlFile : gia3dv2XmlFiles) {
            String configName = gia3dv2XmlFile.getName();
            String path = gia3dv2XmlFile.getAbsolutePath();
            if(File.separator.equals( "\\")) {
                path = path.replaceAll("\\\\", "\\\\\\\\");
            }
            String contents = null;
            try {
                contents = new String(Files.readAllBytes((Paths.get(path))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            dslContext.execute("insert into saved_config(config_id,program_id,name,contents) values("
                    + result + ",6,'" + configName.substring(0, configName.length() - 4) + "','" + contents +"')");
            result = result + 1;
        }
    }

    public void fillSavedConfigTable() {
        //dslContext.insertInto(SAVED_CONFIG,SAVED_CONFIG.CONFIG_ID,SAVED_CONFIG.PROGRAM_ID,SAVED_CONFIG.NAME,SAVED_CONFIG.CONTENTS)
        //      .values(0,0,null,null).execute();
        //dslContext.execute("insert into saved_config values(-1,-1,NULL,NULL)");
        this.fillSavedConfigTableGia2d();
        this.fillSavedConfigTableGia3dv2();
    }

    /*
    public void convertTemplatestoJson() {
        File gia2dXmlFilesPath = new File(this.rsaGiaTemplatesPath + File.separator + "giaroot_2d");
        File[] gia2dXmlFiles = gia2dXmlFilesPath.listFiles();
        int PRETTY_PRINT_INDENT_FACTOR = 4;
        String jsonPrettyPrintString;
        JSONObject xmlJSONObj;

        for (File gia2dXmlFile : gia2dXmlFiles) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(gia2dXmlFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line;
            StringBuilder sb = new StringBuilder();

            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            xmlJSONObj = XML.toJSONObject(String.valueOf(sb));
            jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            String x = XML.toString(xmlJSONObj);
            //File fjson = new File("C:\\rsa\\processed_images\\rice\\RIL\\p00001\\d12\\sandbox\\giaroot_2d\\feray_2017-11-14_17-10-02");

            //String str = "Hello";
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("zirt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.write(String.valueOf(x));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    */

    public void getUser() {
        Result<Record> result = dslContext.select().from(User.USER).fetch();
    }

    public void fillProgramRunTable(ArrayList<RsaImageSet> riss, ApplicationManager am) {
        int i = 1;
        //String [] programNames = {"scale","crop","giaroot_2d,rootwork_3d,rootwork_3d_perspective, giaroot_3d_v2"};
        HashMap<Object, Object> programMap = new HashMap<>();
        Result<Record> record = dslContext.fetch("select * from program");
        for(Record r:record)
        {
            programMap.put(r.getValue("program_id"),r.getValue("name"));
        }
        int run_id = 1;
        for (RsaImageSet ris : riss) {
          run_id = getSandboxSavedOutputs(run_id,i,programMap,am,ris,true,false,false);
          run_id=getSandboxSavedOutputs(run_id,i,programMap,am,ris,false,true,false);
          i = i+1;
        }
    }

    public int getSandboxSavedOutputs(int run_id,int i,HashMap<Object, Object> programMap,ApplicationManager am, RsaImageSet ris, boolean doSandbox, boolean doSaved,boolean red)
    {
        ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris, doSaved, doSandbox, null, red);
        int s=0; int c=0;int g2d=0; int r3d=0;int r3dpers=0;int g3dv2=0;
        int rs=0; int rc=0;int rg2d=0; int rr3d=0;int rr3dpers=0;int rg3dv2=0;
        String username = null;Date date = null;int datasetID = 0;String processedPath = null;
        String organism = ris.getSpecies();
        String experiment = ris.getExperiment();
        String plant = ris.getPlant();
        String day = ris.getImagingDay();
        Result<Record> datasetRecord = dslContext.fetch("select  dataset_id from dataset d inner join seed s on d.seed_id=s.seed_id where " +
                "s.organism_name='" +organism+ "' and s.experiment_code='"+experiment+ "' and s.seed_name='"+plant+"' " +
                "and d.timepoint_d_t_value='"+day+"'");
        datasetID = (int) datasetRecord.getValue(0,"dataset_id");
        for (OutputInfo oi : ois) {
            username = oi.getUser();
            Result<Record> userRecord = dslContext.fetch("select user_id from user where user_name='"+username+"'");
            int userId = (int) userRecord.getValue(0,"user_id");
            date = oi.getDate();
            String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
            String date_ = new SimpleDateFormat(DATE_FORMAT).format(date);
            processedPath = oi.getDir().getAbsolutePath();

            if(File.separator.equals( "\\"))
            {
                processedPath = processedPath.replaceAll("\\\\","\\\\\\\\");
            }

            for(Map.Entry<Object, Object> hs:programMap.entrySet()) {
                String appName = (String) hs.getValue();
                int appId = (int) hs.getKey();
                if(appName.equals("scale")&&oi.getAppName().equals("scale")) {
                    if(oi.isValid()) {
                        s = s + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id +1;
   //                     dslContext.insertInto(PROGRAM_RUN,PROGRAM_RUN.RUN_ID,PROGRAM_RUN.USER_ID,PROGRAM_RUN.PROGRAM_ID,PROGRAM_RUN.RUN_DATE,
    //                            PROGRAM_RUN.DATASET_ID,PROGRAM_RUN.PROCESSED_PATH,PROGRAM_RUN.CONFIG_ID,PROGRAM_RUN.CONFIG_CONTENTS,
     //                           PROGRAM_RUN.OTHER_CONTENTS,PROGRAM_RUN.SAVED,PROGRAM_RUN.RED_FLAG,PROGRAM_RUN.RESULTS)
         //                       .values(run_id,userId,appId,null,datasetID,processedPath,null,null,null,doSaved,0,null);
                    }
                    else
                    {
                        rs = rs + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id +1;
                    }
                }
                else if(appName.equals("crop")&&oi.getAppName().equals("crop"))
                {
                    if(oi.isValid()) {
                        c = c + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id +1;
                    }
                    else
                    {
                        rc = rc +1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                }
                else if(appName.equals("giaroot_2d")&&oi.getAppName().equals("giaroot_2d"))
                {
                    if(oi.isValid()) {
                        g2d = g2d + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id +1;
                    }
                    else
                    {
                        rg2d = rg2d + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                }
                else if(appName.equals("rootwork_3d")&&oi.getAppName().equals("rootwork_3d"))
                {
                    if(oi.isValid()) {
                        r3d = r3d + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id +1;
                    }
                    else
                    {
                        rr3d = rr3d + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                }
                else if(appName.equals("rootwork_3d_perspective")&&oi.getAppName().equals("rootwork_3d_perspective"))
                {
                    if(oi.isValid()) {
                        r3dpers = r3dpers + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                    else
                    {
                        rr3dpers = rr3dpers + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                }
                else if(appName.equals("gia3d_v2")&&oi.getAppName().equals("gia3d_v2"))
                {
                    if(oi.isValid()) {
                        g3dv2 = g3dv2 + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+0+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                    else
                    {
                        rg3dv2 = rg3dv2 + 1;
                        String q="insert into program_run values("+run_id+","+userId+","+appId+","+datasetID+","+doSaved+","+1+",'"+ processedPath
                                +"','"+date_+"',NULL,NULL,NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id+1;
                    }
                }
            }
        }
        for(Map.Entry<Object,Object> hs:programMap.entrySet()) {
            String appName = (String) hs.getValue();
            int appId = (int) hs.getKey();
            int x= 0;int y=0;
            if(appName.equals("scale"))
            {
                x = s;
                y = rs;
            }
            else if(appName.equals("crop"))
            {
                x = c;
                y = rc;
            }
            else if(appName.equals("giaroot_2d"))
            {
                x = g2d;
                y = rg2d;
            }
            else if(appName.equals("rootwork_3d"))
            {
                x = r3d;
                y = rr3d;
            }
            else if(appName.equals("rootwork_3d_perspective"))
            {
                x= r3dpers;
                y = rr3dpers;
            }
            else if(appName.equals("gia3d_v2"))
            {
                x = g3dv2;
                y = rg3dv2;
            }
            if(doSandbox == true) {
                dslContext.insertInto(DATASET_COUNT, DATASET_COUNT.DATASET_ID, DATASET_COUNT.PROGRAM_ID, DATASET_COUNT.CONDITION_TYPE, DATASET_COUNT.DATA_COUNT, DATASET_COUNT.RED_FLAG_COUNT)
                        .values(i, appId, DatasetCountConditionType.sandbox, x, y).execute();
            }
            else if(doSaved == true)
            {
                dslContext.insertInto(DATASET_COUNT, DATASET_COUNT.DATASET_ID, DATASET_COUNT.PROGRAM_ID, DATASET_COUNT.CONDITION_TYPE, DATASET_COUNT.DATA_COUNT, DATASET_COUNT.RED_FLAG_COUNT)
                        .values(i, appId, DatasetCountConditionType.saved, x, y).execute();
            }
        }
        return run_id;
    }
    public int getCount(OutputInfo oi, String appName)
    {
        int v=0;
        if (oi.isValid() && oi.getAppName().equals(appName)) {
            v++;
        }
        return v;
    }
}
