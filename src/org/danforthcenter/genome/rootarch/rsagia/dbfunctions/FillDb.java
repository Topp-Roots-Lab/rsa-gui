package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia.app2.App;
import org.danforthcenter.genome.rootarch.rsagia.db.enums.SeedImagingIntervalUnit;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.SavedConfig;
import org.danforthcenter.genome.rootarch.rsagia.db.tables.User;
import org.danforthcenter.genome.rootarch.rsagia2.*;
import org.jooq.*;
import org.jooq.tools.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;

import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Dataset.DATASET;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Experiment.EXPERIMENT;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Organism.ORGANISM;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.Seed.SEED;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.DatasetImageType.DATASET_IMAGE_TYPE;
import static org.danforthcenter.genome.rootarch.rsagia.db.tables.ProgramRun.PROGRAM_RUN;
import static org.danforthcenter.genome.rootarch.rsagia2.Scale.SCALE_FILE;
import static org.danforthcenter.genome.rootarch.rsagia2.Scale.SCALE_PROP;


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
    protected static String[] spcname = App.getSpecieName();
    protected static String[] spccode = App.getSpecieCode();
    private MetadataDBFunctions mdf;

    public FillDb(File baseDir) throws IOException {
        FileInputStream fis1 = null;
        this.sysProps = new Properties();

        this.baseDir = baseDir;
        this.originalImagesPath = new File(this.baseDir.getAbsolutePath() + File.separator
                + "original_images"
//                + File.separator + this.preferredType
        );
        this.processedImagesPath = new File(this.baseDir.getAbsolutePath() + File.separator + "processed_images");
        this.rsaGiaTemplatesPath = new File(this.baseDir.getAbsolutePath() + File.separator + "rsa-gia-templates");
        dslContext = ConnectDb.getDslContext();
        this.mdf = new MetadataDBFunctions();
    }

    public void refillAllTables(ArrayList<RsaImageSet> riss, ApplicationManager am) {
        deleteAllTables();
        fillUserTable(riss, am);
        fillTables1();
        fillSavedConfigTable();
        fillProgramRunTable(riss, am);
    }

    public void fillUserTable(ArrayList<RsaImageSet> riss, ApplicationManager am) {
        int i = 1;
        i = this.fillPrepUserTable(i, riss, am, true, false);
        i = this.fillPrepUserTable(i, riss, am, false, true);
    }

    public int fillPrepUserTable(int i, ArrayList<RsaImageSet> riss, ApplicationManager am, boolean doSaved, boolean doSandbox) {
        Boolean red = false;
        for (RsaImageSet ris : riss) {
            ArrayList<OutputInfo> ois = OutputInfo.getInstances_old(am, ris, doSaved, doSandbox, null, red);
            for (OutputInfo oi : ois) {
                String username = oi.getUser();
                Result<Record> userRecord = dslContext.fetch("select * from user where user_name='" + username + "'");
                if (userRecord.size() == 0) {
                    String query = "insert into user values(" + i + ",'" + username + "','','','topplab','Submitter')";
                    dslContext.execute(query);
                    i = i + 1;
                }
            }
        }
        return i;
    }

    //organism,experiment,seed tables
    public void fillTables1() {
        HashMap<String, String> organismToOrgCodeMap = new HashMap<>();
        int sizeofSpeciesArray = spcname.length;
        for (int i = 0; i < sizeofSpeciesArray; i++) {
            organismToOrgCodeMap.put(spcname[i], spccode[i]);
        }

        File[] ss = this.originalImagesPath.listFiles();
        int i = 1;
        int j = 1;
        int e = 1;
        if (ss != null && ss.length > 0) {
            for (File s : ss) {
                String species_name = s.getName();
                dslContext.insertInto(ORGANISM, ORGANISM.ORGANISM_NAME, ORGANISM.SPECIES_CODE, ORGANISM.SPECIES, ORGANISM.SUBSPECIES, ORGANISM.VARIETY)
                        .values(species_name, organismToOrgCodeMap.get(species_name), null, null, null).execute();
                //dslContext.execute("insert into organism(organism_name,species,subspecies,variety) values('" + species + "','species',NULL,NULL)");
                File[] exps = s.listFiles();
                if (exps != null && exps.length > 0) {
                    for (File exp : exps) {
                        String experiment_name = exp.getName();
                        System.out.println(experiment_name);
                        dslContext.insertInto(EXPERIMENT, EXPERIMENT.EXPERIMENT_ID, EXPERIMENT.EXPERIMENT_CODE, EXPERIMENT.ORGANISM_NAME, EXPERIMENT.USER_ID, EXPERIMENT.DESCRIPTION)
                                .values(e, experiment_name, species_name, 3, null).execute();
                        File[] seeds = exp.listFiles();
                        if (seeds != null && seeds.length > 0) {
                            for (File seed : seeds) {
                                String seed_name = seed.getName();
                                dslContext.insertInto(SEED, SEED.SEED_ID, SEED.EXPERIMENT_ID, SEED.SEED_NAME, SEED.GENOTYPE, SEED.DRY_SHOOT, SEED.DRY_ROOT, SEED.WET_SHOOT, SEED.WET_ROOT, SEED.STERILIZATION_CHAMBER, SEED.IMAGING_INTERVAL_UNIT, SEED.DESCRIPTION, SEED.IMAGING_START_DATE)
                                        .values(i, e, seed_name, null, null, null, null, null, null, SeedImagingIntervalUnit.day, null, null).execute();
                                File[] timeValues = seed.listFiles();
                                if (timeValues != null && timeValues.length > 0) {
                                    for (File timeValue : timeValues) {
                                        String timeValue_name = timeValue.getName();
                                        dslContext.insertInto(DATASET, DATASET.DATASET_ID, DATASET.SEED_ID, DATASET.TIMEPOINT)
                                                .values(j, i, timeValue_name).execute();
                                        if (timeValue_name.substring(0, 1).equals("t")) {
                                            String query = "update seed set imaging_interval_unit='" + SeedImagingIntervalUnit.hour + "' where seed_id=" + i;
                                            dslContext.execute(query);
                                        }
                                        File[] imageTypes = timeValue.listFiles();
                                        if (imageTypes != null && imageTypes.length > 0) {
                                            for (File imageType : imageTypes) {
                                                String imageTypeName = imageType.getName();
                                                //System.out.println(species_name + " " + experiment_name + " " + seed_name + " " + imageTypeName);

                                                dslContext.insertInto(DATASET_IMAGE_TYPE, DATASET_IMAGE_TYPE.DATASET_ID, DATASET_IMAGE_TYPE.IMAGE_TYPE)
                                                        .values(j, imageTypeName).execute();
                                            }
                                        }
                                        j = j + 1;
                                    }
                                }
                                i = i + 1;
                            }
                        }
                        e = e + 1;
                    }
                }
            }
        }
    }

    public void fillSavedConfigTableGia2d() {
        File gia2dXmlFilesPath = new File(this.rsaGiaTemplatesPath + File.separator + "giaroot_2d");
        File[] gia2dXmlFiles = gia2dXmlFilesPath.listFiles();
        int i = 1;
        for (File gia2dXmlFile : gia2dXmlFiles) {
            String configName = gia2dXmlFile.getName();
            String path = gia2dXmlFile.getAbsolutePath();
            if (File.separator.equals("\\")) {
                path = path.replaceAll("\\\\", "\\\\\\\\");
            }
            String contents = null;
            try {
                contents = new String(Files.readAllBytes((Paths.get(path))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Query query2 = dslContext.query("insert into saved_config(config_id,program_id,name,contents) values("
                    + i + ",3,'" + configName.substring(0, configName.length() - 4) + "','" + contents + "')");
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
            if (File.separator.equals("\\")) {
                path = path.replaceAll("\\\\", "\\\\\\\\");
            }
            String contents = null;
            try {
                contents = new String(Files.readAllBytes((Paths.get(path))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            dslContext.execute("insert into saved_config(config_id,program_id,name,contents) values("
                    + result + ",6,'" + configName.substring(0, configName.length() - 4) + "','" + contents + "')");
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
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("ftest"));
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
        HashMap<Object, Object> programMap = new HashMap<>();
        Result<Record> record = dslContext.fetch("select * from program");
        for (Record r : record) {
            programMap.put(r.getValue("program_id"), r.getValue("name"));
        }
        int run_id = 1;
        for (RsaImageSet ris : riss) {
            run_id = getSandboxSavedOutputs(run_id, i, programMap, am, ris, true, false, false);
            run_id = getSandboxSavedOutputs(run_id, i, programMap, am, ris, false, true, false);
            i = i + 1;
        }
    }

    public int getSandboxSavedOutputs(int run_id, int i, HashMap<Object, Object> programMap, ApplicationManager am, RsaImageSet ris, boolean doSandbox, boolean doSaved, boolean red) {
        ArrayList<OutputInfo> ois = OutputInfo.getInstances_old(am, ris, doSaved, doSandbox, null, red);
        String username = null;
        Date date = null;
        int datasetID = 0;
        String processedPath = null;
        String organism = ris.getSpecies();
        String experiment = ris.getExperiment();
        String plant = ris.getPlant();
        String day = ris.getImagingDay();
        Result<Record> datasetRecord = dslContext.fetch("select  dataset_id from dataset d inner join seed s on d.seed_id=s.seed_id " +
                "inner join experiment e on s.experiment_id=e.experiment_id where " +
                "e.organism_name='" + organism + "' and e.experiment_code='" + experiment + "' and s.seed_name='" + plant + "' " +
                "and d.timepoint='" + day + "'");
        datasetID = (int) datasetRecord.getValue(0, "dataset_id");
        for (OutputInfo oi : ois) {
            username = oi.getUser();
            Result<Record> userRecord = dslContext.fetch("select user_id from user where user_name='" + username + "'");
            int userId = (int) userRecord.getValue(0, "user_id");
            date = oi.getDate();
            String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            String date_ = new SimpleDateFormat(DATE_FORMAT).format(date);
            processedPath = oi.getDir().getAbsolutePath();

            if (File.separator.equals("\\")) {
                processedPath = processedPath.replaceAll("\\\\", "\\\\\\\\");
            }

            for (Map.Entry<Object, Object> hs : programMap.entrySet()) {
                String appName = (String) hs.getValue();
                int appId = (int) hs.getKey();

                if (appName.equals("scale") && oi.getAppName().equals("scale")) {
                    if (oi.isValid()) {
                        String scaleResult = this.scalePropertyFileToJSONString(oi);
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved +
                                "," + 0 + ",'" + date_ + "',NULL,NULL,NULL, NULL,"+scaleResult+")";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved
                                + "," + 1 + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("crop") && oi.getAppName().equals("crop")) {
                    if (oi.isValid()) {
                        String cropResult = this.cropPropertyFileToJSONString(oi);
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved +
                                "," + 0 + ",'" + date_ + "',NULL,NULL,NULL, NULL," + cropResult + ")";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved +
                                "," + 1 + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("giaroot_2d") && oi.getAppName().equals("giaroot_2d")) {
                    if (oi.isValid() && oi.getDir().listFiles().length != 2) {
                        ArrayList<String> features = null;
                        try {
                            features = this.getgia2dJobConfigFeatures(oi);
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        }
                        String templateName = features.get(0);
                        String savedConfigIDString = "NULL";
                        String configContents = "NULL";
                        Result<Record> savedTemplate = mdf.findSavedTemplateFromName(templateName,appName);
                        if (savedTemplate.size() > 0) {
                            Integer savedConfigID = (int) savedTemplate.get(0).getValue("config_id");
                            savedConfigIDString = savedConfigID.toString();
                        } else {
                            String path = features.get(2);
                            try {
                                configContents = "'" + new String(Files.readAllBytes((Paths.get(path)))) + "'";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        String descriptors = features.get(1);

                        String cropProps = "NULL";
                        File cropResultFile = new File(oi.getDir() + File.separator
                                + "crop.properties");
                        if (cropResultFile.exists()) {
                            cropProps = this.cropPropertyFileToJSONString(oi);
                        }

                        String gia2DResult = GiaRoot2DOutput.readFormatCSVFile(new File(oi.getDir() + File.separator + "giaroot_2d.csv"));
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved +
                                "," + 0 + ",'" + date_ + "'," + savedConfigIDString + "," + configContents + "," + cropProps + ",'" + descriptors + "','" + gia2DResult + "')";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved +
                                "," + 1 + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("rootwork_3d") && oi.getAppName().equals("rootwork_3d")) {
                    if (oi.isValid()) {

                        String path = oi.getDir().getAbsolutePath() + File.separator + "config.xml";
                        if (File.separator.equals("\\")) {
                            path = path.replaceAll("\\\\", "\\\\\\\\");
                        }
                        String rootworkUnsavedConfigContents  = "NULL";
                        try {
                            rootworkUnsavedConfigContents = "'" + new String(Files.readAllBytes((Paths.get(path)))) + "'";
                            rootworkUnsavedConfigContents = rootworkUnsavedConfigContents.replace("\\", "\\\\");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "',NULL,"+rootworkUnsavedConfigContents+",NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("rootwork_3d_perspective") && oi.getAppName().equals("rootwork_3d_perspective")) {
                    if (oi.isValid()) {

                        String path = oi.getDir().getAbsolutePath()+ File.separator + "config.xml";
                        if (File.separator.equals("\\")) {
                            path = path.replaceAll("\\\\", "\\\\\\\\");
                        }
                        String rootworkPersUnsavedConfigContents  = "NULL";
                        try {
                            rootworkPersUnsavedConfigContents = "'" + new String(Files.readAllBytes((Paths.get(path)))) + "'";
                            rootworkPersUnsavedConfigContents = rootworkPersUnsavedConfigContents.replace("\\", "\\\\");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "',NULL,"+rootworkPersUnsavedConfigContents+",NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("gia3d_v2") && oi.getAppName().equals("gia3d_v2")) {
                    if (oi.isValid()) {
                        String inputConfig = this.gia3Dv2Config(oi);
                        int configID = (int) mdf.findSavedTemplateFromName(inputConfig,oi.getAppName()).get(0).getValue("config_id");
                        String scaleProp = this.scalePropertyFileToJSONString(oi);
                        String gia3dv2Result = Gia3D_v2Output.readFormatTSVFile(new File(oi.getDir() + File.separator + "gia_3d_v2.tsv"));
                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "'," + configID + ",NULL,"+scaleProp+", NULL,'"+gia3dv2Result+"')";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("qc") && oi.getAppName().equals("qc")) {
                    if (oi.isValid()) {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("qc2") && oi.getAppName().equals("qc2")) {
                    if (oi.isValid()) {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                } else if (appName.equals("qc3") && oi.getAppName().equals("qc3")) {
                    if (oi.isValid()) {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 0
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL, NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    } else {

                        String q = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                                "values(" + run_id + "," + userId + "," + appId + "," + datasetID + "," + doSaved + "," + 1
                                + ",'" + date_ + "',NULL,NULL,NULL, NULL,NULL)";
                        dslContext.execute(q);
                        run_id = run_id + 1;
                    }
                }
            }
        }
        return run_id;
    }

    public String cropPropertyFileToJSONString(OutputInfo oi)
    {
        File cropResultFile = new File(oi.getDir() + File.separator
                + "crop.properties");
        BufferedReader br = null;
        JSONObject jo = new JSONObject();
        jo.put("legacy",true);
        String cropResult = "NULL";
        try {
            br = new BufferedReader(new FileReader(cropResultFile));
            String line = null;
            try {
                while (br.ready()) {
                    line = br.readLine();
                    if (line.contains("original_image=")) {
                        String original_image = line.split("=")[1];
                        jo.put("original_image",original_image);
                    }
                    else if(line.contentEquals("input_type="))
                    {
                        String input_type = line.split("=")[1];
                        jo.put("input_type",input_type);
                    }
                    else if(line.contains("rotation="))
                    {
                        String rotation = line.split("=")[1];
                        jo.put("rotation",rotation);
                    }
                    else if(line.contains("crop="))
                    {
                        String cropline = line.split("=")[1];
                        jo.put("crop",cropline);
                    }
                    else if(line.contains("crop_sum"))
                    {
                        String crop_sum  = line.split("=")[1];
                        jo.put("crop_sum",crop_sum);
                    }
                }
                cropResult = "'" + jo.toString() + "'";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return cropResult;
    }
    public String gia3Dv2Config(OutputInfo oi)
    {
        String gia3DScaleConfig = "";
        File[] files = new File(oi.getDir().getAbsolutePath()).listFiles(new ExtensionFileFilter("xml"));
        for(File f: files)
        {
            String fileName = f.getName();
            if(fileName.contains("config"))
            {
                String[] fileNameSplitArray = fileName.split("-");
                gia3DScaleConfig = fileNameSplitArray[0];
            }
        }


        return gia3DScaleConfig;
    }
    public String scalePropertyFileToJSONString(OutputInfo oi)
    {
        File scaleResultFile = new File(oi.getDir().getAbsolutePath() + File.separator
                + SCALE_FILE);
        BufferedReader br = null;
        JSONObject jo = new JSONObject();
        jo.put("legacy",true);
        String scaleResult = "NULL";
        try {
            br = new BufferedReader(new FileReader(scaleResultFile));
            String line = null;
            try {
                while (br.ready()) {
                    line = br.readLine();
                    if (line.contains("scale=")) {
                        double scale = Double.parseDouble(line.split("=")[1]);
                        jo.put(SCALE_PROP,scale);
                    }
                }
                scaleResult = "'" + jo.toString() + "'";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return scaleResult;
    }
    public  ArrayList<String> getgia2dJobConfigFeatures(OutputInfo gia2DOutput) throws ParserConfigurationException, IOException, SAXException {
        File gia2DOutputDir =  new File(gia2DOutput.getDir().getAbsolutePath());
        File[] fileList = gia2DOutputDir.listFiles(new ExtensionFileFilter("xml"));
        String template= null;
        String descriptors =null;
        ArrayList<String> features=new ArrayList<>();
        for(int i=0;i<fileList.length;i++)
        {
            File f = fileList[i];
            if(f.getName().contains("job-config"))
            {
                try {

                    File fXmlFile = new File(f.getPath());
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getElementsByTagName("job");
                    for (int j = 0; j < nList.getLength(); j++) {

                        Node nNode = nList.item(j);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element eElement = (Element) nNode;
                            String configPath = eElement.getAttribute("config");
                            File configFile = new File(configPath);
                            String configFileName = configFile.getName();
                            template = configFileName.split("-gia-config")[0];
                            features.add(template);
                            NodeList nList2 = doc.getElementsByTagName("compute");
                            for(int k=0; k<nList2.getLength(); k++)
                            {
                                Node nNode2 = nList2.item(k);
                                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                    Element eElement2 = (Element) nNode2;
                                    descriptors = eElement2.getAttribute("types");
                                    features.add(descriptors);
                                }
                            }
                            features.add(configPath);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return features;
    }
    public void deleteAllTables()
    {
        dslContext.execute("delete from dataset_image_type");
        dslContext.execute("delete from program_run");
        dslContext.execute("delete from dataset");
        dslContext.execute("delete from seed");
        dslContext.execute("delete from saved_config");
        dslContext.execute("delete from experiment");
        dslContext.execute("delete from organism");
        dslContext.execute("delete from user");

    }
    public int getCount(OutputInfo oi, String appName) {
        int v = 0;
        if (oi.isValid() && oi.getAppName().equals(appName)) {
            v++;
        }
        return v;
    }
}
