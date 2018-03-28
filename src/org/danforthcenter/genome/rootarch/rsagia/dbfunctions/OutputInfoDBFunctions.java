package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia2.ExtensionFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OutputInfoDBFunctions {
    private DSLContext dslContext;

    public OutputInfoDBFunctions() {
        dslContext = ConnectDb.getDslContext();
    }

    public Result<Record> getOutputsFromProgramRunTable(RsaImageSet ris, ArrayList<String> filters, boolean saved, boolean sandbox, boolean red) {
        int datasetID = ris.getDatasetID();
        String query = "select * from program_run pr inner join program p on pr.program_id = p.program_id inner join " +
                "user u on u.user_id = pr.user_id where pr.dataset_id=" + datasetID;

        if (red == true) {
            query = query + " and red_flag = 1";
        }
        if (saved == false) {
            query = query + " and saved = 0";
        }
        else if(sandbox == false) {
            query = query + " and saved = 1";
        }
        if(filters != null) {
            query = query + " and (";
            for (int i = 0; i < filters.size(); i++) {
                if (i == filters.size() - 1) {
                    query = query + " p.name = '" + filters.get(i) + "')";
                } else {
                    query = query + " p.name ='" + filters.get(i) + "' or";
                }
            }
        }
        Result<Record> datasetRecord = dslContext.fetch(query);

        return datasetRecord;
    }

    public int findMaxRunID() {
        String query = "select max(run_id) max from program_run";
        Result<Record> resultRecord = dslContext.fetch(query);
        return (int) resultRecord.getValue(0, "max");
    }

    public int findAppID(String appName) {
        String query = "select program_id from program where name = '" + appName + "'";
        Result<Record> resultRecord = dslContext.fetch(query);
        return (int) resultRecord.getValue(0, "program_id");
    }

    public void insertProgramRunTable(OutputInfo oi) {
        int datasetID = oi.getRis().getDatasetID();
        int runID = this.findMaxRunID() + 1;
        int appID = this.findAppID(oi.getAppName());
        UserDBFunctions uf = new UserDBFunctions();
        int userID = (int) uf.findUserFromName(oi.getUser()).getValue(0, "user_id");
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        String date_ = new SimpleDateFormat(DATE_FORMAT).format(oi.getDate());

        String query = "insert into program_run (run_id, user_id, program_id, dataset_id, saved, red_flag, run_date, saved_config_id, unsaved_config_contents, input_runs, descriptors, results) " +
                "values(" + runID + "," + userID + "," + appID + "," + datasetID + "," + 0 + ","
                + 1 + ",'"+ date_ + "',NULL,NULL,NULL,NULL,NULL)";
        dslContext.execute(query);
        oi.setRunID(runID);
    }
    public void updateDescriptors(OutputInfo oi) {
        String descs = oi.getDescriptors();
        if(descs == null)
        {
            descs = "NULL";
        }
        else
        {
            descs = "'" + descs + "'";
        }
        String query = "update program_run set descriptors=" + descs + " where run_id=" + oi.getRunID();
        dslContext.execute(query);
    }
    public void updateResults(OutputInfo oi)
    {
        String results = oi.getResults();
        if(results == null)
        {
            results = "NULL";
        }
        else
        {
            results = "'" + results + "'";
        }
        String query = "update program_run set results=" + results +" where run_id=" + oi.getRunID();
        dslContext.execute(query);
    }
    public void updateContents(OutputInfo oi)
    {
        Integer configID = oi.getSavedConfigID();
        String configContents = oi.getUnsavedConfigContents();
        String inputRuns = oi.getInputRuns();

        String configIDString = "NULL";
        if (configID != null)
        {
            configIDString = configID.toString();
        }

        if (oi.getUnsavedConfigContents() != null)
        {
            configContents = "'" + configContents.replace("\\", "\\\\") + "'";
        }
        else
        {
            configContents = "NULL";
        }
        if (inputRuns != null)
        {
            inputRuns = "'" + inputRuns.toString() + "'";
        }
        else
        {
            inputRuns = "NULL";
        }
        String query = "update program_run set saved_config_id=" + configIDString + ",unsaved_config_contents=" + configContents +
                ",input_runs=" + inputRuns + " where run_id=" + oi.getRunID();
        dslContext.execute(query);
    }
    public void updateRedFlag(OutputInfo oi)
    {
        boolean redFlag = !oi.isValid();
        String query = "update program_run set red_flag="+redFlag+ " where run_id=" +oi.getRunID();
        dslContext.execute(query);
    }

    public int findConfigID(String templateName, String appName)
    {
        Result<Record> configRecord = this.findSavedTemplateFromName(templateName, appName);
        return (int) configRecord.getValue(0, "config_id");
    }
    public Result<Record> findSavedTemplateFromName(String templateName, String appName)
    {
        int programID = this.findAppID(appName);
        Result<Record> configRecord = dslContext.fetch("select * from saved_config where program_id=" + programID + " and name='"+
                templateName + "'");
        return configRecord;
    }
    public ArrayList<String> getTemplates(String appName)
    {
        ArrayList<String> templates = new ArrayList();
        int appID = this.findAppID(appName);
        String query = "select name from saved_config where program_id=" + appID;
        Result<Record> resultRecord = dslContext.fetch(query);
        for(Record r:resultRecord)
        {
            templates.add((String) r.getValue("name"));
        }
        return templates;
    }
}
