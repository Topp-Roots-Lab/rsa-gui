package org.danforthcenter.genome.rootarch.rsagia.dbfunctions;

import org.danforthcenter.genome.rootarch.rsagia.app2.App;
import org.danforthcenter.genome.rootarch.rsagia2.ISecurityManager;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.danforthcenter.genome.rootarch.rsagia2.StringPairFilter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet.getDayFilter;
import static org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet.getPlantFilter;

public class FillTable {

    private DSLContext dslContext;

    public FillTable() {
        ConnectDb dbConnection = new ConnectDb();
        this.dslContext = dbConnection.getDslContext();
    }
    public ArrayList<String> getProgramNames()
    {
        String query = "select * from program";
        Result<Record> records = dslContext.fetch(query);
        ArrayList<String> arr = new ArrayList<>();
        for(Record  r:records)
        {
            arr.add((String) r.getValue("name"));
        }
        return arr;
    }
    public Result<Record> getDatasets(ArrayList<StringPairFilter> sps, ArrayList<StringPairFilter> exps,
                                      ArrayList<StringPairFilter> pls, ArrayList<StringPairFilter> ims,
                                      ArrayList<StringPairFilter> pls_ims)
    {
        int s1 = 0;
        int e1 = 0;
        int p1 = 0;
        int i1 = 0;
        int pi1 = 0;
        String plsString = null;

        String query = "SELECT" +
                "  e.experiment_code," +
                "  e.organism_name," +
                "  s.seed_id," +
                "  s.seed_name," +
                "  d.dataset_id," +
                "  d.timepoint," +
                "  dit.image_type," +
                "  pr.program_id," +
                "  p.name," +
                "  SUM(IFNULL(pr.saved, 0)) AS saved_count," +
                "  IF(pr.saved IS NULL, 0, COUNT(*) - SUM(pr.saved)) AS sandbox_count " +
                "FROM experiment e" +
                "  INNER JOIN seed s ON e.experiment_id = s.experiment_id" +
                "  INNER JOIN dataset d ON s.seed_id = d.seed_id" +
                "  INNER JOIN dataset_image_type dit ON d.dataset_id = dit.dataset_id" +
                "  LEFT OUTER JOIN program_run pr ON d.dataset_id = pr.dataset_id AND pr.red_flag = 0" +
                "  LEFT OUTER JOIN program p ON pr.program_id = p.program_id ";
        if(sps != null && exps != null && pls != null && ims != null && pls_ims != null) {
            if (sps.size() != 0 || exps.size() != 0 || pls.size() != 0 || ims.size() != 0 || pls_ims.size() != 0) {
                query = query + "where ";
            }
            if (sps.size() > 0) {
                for (StringPairFilter spp : sps) {
                    if (s1 > 0) {
                        query = query + " or ";
                    } else {
                        query = query + "(";
                    }
                    query = query + "e.organism_name='" + spp.getR3() + "'";
                    s1 = s1 + 1;
                }
                query = query + ")";
            }
            if (exps.size() > 0) {
                if (s1 > 0) {
                    query = query + " and ";
                }
                for (StringPairFilter exp : exps) {
                    if (e1 > 0) {
                        query = query + " or ";
                    } else {
                        query = query + "(";
                    }
                    query = query + "e.experiment_code='" + exp.getR3() + "'";

                    e1 = e1 + 1;
                }
                query = query + ")";
            }
            if (pls_ims.size() == 0) {
                if (pls.size() > 0) {
                    if (s1 > 0 || e1 > 0) {
                        query = query + " and ";
                    }
                    for (StringPairFilter pl1 : pls) {
                        if (p1 > 0) {
                            query = query + " or ";
                        } else {
                            query = query + "(";
                        }
                        if (pl1.getR1() != null && pl1.getR2() != null) {
                            query = query + "s.seed_name>='" + pl1.getR1() + "' and s.seed_name<='" + pl1.getR2() + "'";
                        } else if (pl1.getR2() != null) {
                            query = query + "s.seed_name<='" + pl1.getR2() + "'";
                        } else if (pl1.getR1() != null) {
                            query = query + "s.seed_name>='" + pl1.getR1() + "'";
                        } else if (pl1.getR3() != null) {
                            query = query + "s.seed_name='" + pl1.getR3() + "'";
                        }
                        p1 = p1 + 1;
                    }
                    query = query + ")";
                }
                if (ims.size() > 0) {
                    if (s1 > 0 || e1 > 0 || p1 > 0) {
                        query = query + " and ";
                    }
                    for (StringPairFilter im : ims) {
                        if (i1 > 0) {
                            query = query + " or ";
                        } else {
                            query = query + "(";
                        }
                        if (im.getR1() != null && im.getR2() != null) {
                            query = query + "d.timepoint>'" + im.getR1() + "' and d.timepoint<'" + im.getR2() + "'";
                        } else if (im.getR2() != null) {
                            query = query + "d.timepoint<'" + im.getR2() + "'";
                        } else if (im.getR1() != null) {
                            query = query + "d.timepoint>'" + im.getR1() + "'";
                        } else if (im.getR3() != null) {
                            query = query + "d.timepoint='" + im.getR3() + "'";
                        }
                        i1 = i1 + 1;
                    }
                    query = query + ")";
                }
            } else {
                if (s1 > 0 || e1 > 0) {
                    query = query + " and ";
                }
                for (StringPairFilter pls_im : pls_ims) {
                    if (pi1 > 0) {
                        query = query + " or ";
                    } else {
                        query = query + "(";
                    }
                    StringPairFilter plfilter = getPlantFilter(pls_im);
                    StringPairFilter dayfilter = getDayFilter(pls_im);
                    query = query + "(s.seed_name='" + plfilter.getR3() + "' and d.timepoint='" + dayfilter.getR3() + "')";
                    pi1 = pi1 + 1;
                }
                query = query + ")";
            }
        }
        query = query + " GROUP BY dit.dataset_id,dit.image_type,pr.program_id order by d.dataset_id;";
        Result<Record> datasetRecord = dslContext.fetch(query);
        return datasetRecord;
    }

}

