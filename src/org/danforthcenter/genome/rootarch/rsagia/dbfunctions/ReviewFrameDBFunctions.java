package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.jooq.DSLContext;

public class ReviewFrameDBFunctions {
    public void changeToSavedinDB(int runID)
    {
        String query = "update program_run set saved = 1 where run_id =" +runID;
        ConnectDb.getDslContext().execute(query);
    }
    public void deleteRun(int runID)
    {
        String query = "delete from program_run where run_id =" + runID;
        ConnectDb.getDslContext().execute(query);
    }
}
