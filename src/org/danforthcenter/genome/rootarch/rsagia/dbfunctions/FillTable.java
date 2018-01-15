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

    DSLContext dslContext = null;

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

        String query = "select e.organism_name,dip.image_type,e.experiment_code,d.dataset_id,s.seed_id,s.seed_name,d.timepoint_d_t_value, " +
                "p.name,dc.condition_type,dc.data_count,dc.red_flag_count,dip.image_path from experiment e inner join seed s " +
                "inner join dataset d inner join dataset_count dc inner join dataset_image_paths dip inner join " +
                "program p on s.experiment_id=e.experiment_id and d.dataset_id=dc.dataset_id and s.seed_id=d.seed_id and " +
                "d.dataset_id=dip.dataset_id and dc.program_id=p.program_id ";
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
                query = query + "s.organism_name='" + spp.getR3() + "'";
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
                query = query + "s.experiment_code='" + exp.getR3() + "'";

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
                        query = query + "d.timepoint_d_t_value>'" + im.getR1() + "' and d.timepoint_d_t_value<'" + im.getR2() + "'";
                    } else if (im.getR2() != null) {
                        query = query + "d.timepoint_d_t_value<'" + im.getR2() + "'";
                    } else if (im.getR1() != null) {
                        query = query + "d.timepoint_d_t_value>'" + im.getR1() + "'";
                    } else if (im.getR3() != null) {
                        query = query + "d.timepoint_d_t_value='" + im.getR3() + "'";
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
                query = query + "(s.seed_name='" + plfilter.getR3() + "' and d.timepoint_d_t_value='" + dayfilter.getR3() + "')";
                pi1 = pi1 + 1;
            }
            query = query + ")";
        }
        query = query + " order by d.dataset_id;";
        Result<Record> datasetRecord = dslContext.fetch(query);
        return datasetRecord;
    }

}

