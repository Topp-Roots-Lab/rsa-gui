package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.jooq.Record;
import org.jooq.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OutputInfoDBFunctions {
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
        Result<Record> datasetRecord = ConnectDb.getDslContext().fetch(query);

        return datasetRecord;
    }

    /**
     * This method is used to find the ID of the last program run.
     * If the program has not been run before, then return 0.
     * This method assumes that any caller will increment the value
     * by 1 before taking action.
     * @return int This is the ID value of the last program run
     */
    public int findMaxRunID() {
        String query = "select max(run_id) max from program_run";
        Result<Record> resultRecord = ConnectDb.getDslContext().fetch(query);
        if (resultRecord.getValue(0, "max") == null) {
            return 0;
        } else {
            return (int) resultRecord.getValue(0, "max");
        }
    }

    public int findAppID(String appName) {
        String query = "select program_id from program where name = '" + appName + "'";
        Result<Record> resultRecord = ConnectDb.getDslContext().fetch(query);
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
        ConnectDb.getDslContext().execute(query);
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
        ConnectDb.getDslContext().execute(query);
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
        ConnectDb.getDslContext().execute(query);
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
        ConnectDb.getDslContext().execute(query);
    }
    public void updateRedFlag(OutputInfo oi)
    {
        boolean redFlag = !oi.isValid();
        String query = "update program_run set red_flag="+redFlag+ " where run_id=" +oi.getRunID();
        ConnectDb.getDslContext().execute(query);
    }

    public void insertSavedConfig(String newContents, String configName, String appName)
    {
        int programID = this.findAppID(appName);
        String query = "insert into saved_config (program_id,name,contents) values ("+programID+",'"+configName+"','"+newContents+"')";
        ConnectDb.getDslContext().execute(query);
    }

    public void updateSavedConfig(String newContents, String newConfigName, String oldConfigName, String appName)
    {
        int programID = this.findAppID(appName);
        String query = "update saved_config set contents='" + newContents + "', name='" + newConfigName + "' where program_id=" + programID + " and name='" + oldConfigName + "'";
        ConnectDb.getDslContext().execute(query);
    }

    public void moveSavedConfigToUnsavedConfig(String configName, String appName)
    {
        Result<Record> configRecord = this.findSavedConfigFromName(configName, appName);
        int configID = (int) configRecord.getValue(0, "config_id");
        String contents = (String) configRecord.getValue(0, "contents");
        String query = "update program_run set saved_config_id=null, unsaved_config_contents='" + contents + "' where saved_config_id=" + configID;
        ConnectDb.getDslContext().execute(query);
    }

    public boolean checkSavedConfigNameExists(String configName, String appName)
    {
        Result<Record> configRecord = this.findSavedConfigFromName(configName, appName);
        if (configRecord == null || configRecord.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public String findSavedConfigName(int configID)
    {
        String query = "select name from saved_config where config_id=" + configID;
        Result<Record> resultRecord = ConnectDb.getDslContext().fetch(query);
        String name = (String) resultRecord.getValue(0, "name");
        return name;
    }

    public int findSavedConfigID(String configName, String appName)
    {
        Result<Record> configRecord = this.findSavedConfigFromName(configName, appName);
        return (int) configRecord.getValue(0, "config_id");
    }

    public String findSavedConfigContents(String configName, String appName)
    {
        Result<Record> configRecord = this.findSavedConfigFromName(configName, appName);
        return (String) configRecord.getValue(0, "contents");
    }

    public Result<Record> findSavedConfigFromName(String configName, String appName)
    {
        int programID = this.findAppID(appName);
        Result<Record> configRecord = ConnectDb.getDslContext().fetch("select * from saved_config where program_id=" + programID + " and name='" + configName + "'");
        return configRecord;
    }

    public ArrayList<String> getSavedConfigs(String appName)
    {
        ArrayList<String> savedConfigs = new ArrayList();
        int appID = this.findAppID(appName);
        String query = "select name from saved_config where program_id=" + appID;
        Result<Record> resultRecord = ConnectDb.getDslContext().fetch(query);
        for(Record r:resultRecord)
        {
            savedConfigs.add((String) r.getValue("name"));
        }
        return savedConfigs;
    }
}
