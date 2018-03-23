package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

public class RsaImageSetDBFunctions {
    private DSLContext dslContext;

    public RsaImageSetDBFunctions()
    {
        dslContext = ConnectDb.getDslContext();
    }
    public Result<Record> selectCountsOfAppForDatasetProgram(int datasetID,String programName)
    {
        String query = "SELECT" +
                "  pr.dataset_id,"+
                "  pr.program_id," +
                "  p.name," +
                "  SUM(IFNULL(pr.saved, 0)) AS saved_count," +
                //"  IF(pr.saved IS NULL,0, SUM(pr.saved)) AS saved_count," +
                "  IF(pr.saved IS NULL, 0, COUNT(*) - SUM(pr.saved)) AS sandbox_count " +
                "  FROM program_run pr"+
                "  INNER JOIN program p ON pr.program_id = p.program_id" +
                "  WHERE pr.red_flag=0" +
                "  AND p.name='" + programName +"'"+
                "  AND pr.dataset_id=" + datasetID +" group by program_id";
        Result<Record> datasetRecord = dslContext.fetch(query);
        return datasetRecord;
    }
}
