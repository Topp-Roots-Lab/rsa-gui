/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.danforthcenter.genome.rootarch.rsagia.app2.App;
import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.FillTable;
import org.jooq.Record;
import org.jooq.Result;

/**
 * Responsible for storing all input data information. That is, what can be
 * taken from the file system.
 *
 * Also has static methods for querying, generating this data.
 *
 * @author bm93
 */
public class RsaImageSet {
    protected String species;
    protected String experiment;
    protected String plant;
    protected String imagingDay;
    protected Calendar imagingDate;

    protected  HashMap<String,int[]> countsApps;

    protected File inputDir;
    protected File processedDir;
    protected File protectedInputDir;
    protected String[] inputTypes;

    protected File baseDir;
    protected ISecurityManager ism;

    protected String preferredType;
    protected File preferredInputDir;

    public static String[] ALLOWED_TYPES = { "jpg", "tiff" };

    public ArrayList<RsaImageSet> getAll(File dir, ISecurityManager ism) {
        return getAll(dir, ism, new ArrayList<StringPairFilter>(),
                new ArrayList<StringPairFilter>(),
                new ArrayList<StringPairFilter>(),
                new ArrayList<StringPairFilter>(),
                new ArrayList<StringPairFilter>());
    }

    /**
     * Return a list of all image sets contained in the directory specified.
     * Typically, this would be /data/rsa
     *
     * @param dir
     * @return
     */
    public static ArrayList<RsaImageSet> getAll(File dir, ISecurityManager ism,
                                                ArrayList<StringPairFilter> sps, ArrayList<StringPairFilter> exps,
                                                ArrayList<StringPairFilter> pls, ArrayList<StringPairFilter> ims,
                                                ArrayList<StringPairFilter> pls_ims) {
        ArrayList<RsaImageSet> ans = new ArrayList<RsaImageSet>();

        File d2 = new File(dir.getAbsolutePath() + File.separator
                + "original_images"
//                + File.separator + this.preferredType
        );
        File[] ss = d2.listFiles();

        //////////////////////////////////////////////////////////////////////////////////////////
        String[] spcname = App.getSpecieName();
        List<String> listname = Arrays.asList(spcname);
        FillTable ft = new FillTable();
        Result<Record> datasetRecord = ft.getDatasets(sps,exps,pls,ims,pls_ims);
        int previousDatasetID=-1;
        RsaImageSet ris=null;

        for (Record r:datasetRecord)
        {
            if(!r.getValue("dataset_id").equals(previousDatasetID))
            {
                String species = (String) r.getValue("organism_name");
                boolean view = listname.contains(species);
                if (!view)
                {
                    continue;
                }
                String experiment = (String) r.getValue("experiment_code");
                String plant = (String) r.getValue("seed_name");
                String imagingDay = (String) r.getValue("timepoint_d_t_value");

                HashMap<String, int[]> countsApps = makeHashMapApps(ft);

                ris = new RsaImageSet(dir, species, experiment, plant, imagingDay, ism);
                ris.setCountsApps(countsApps);
                String  programName = (String) r.getValue("name");
                String conditionType = (String) r.getValue("condition_type");
                int total = (int) r.getValue("data_count");
                int[] countArray = countsApps.get(programName);
                if(conditionType.equals("sandbox"))
                {
                    countArray[0]=total;
                }
                else
                {
                    countArray[1]=total;
                }
                ans.add(ris);
                previousDatasetID= (int) r.getValue("dataset_id");
            }
            else
            {
                HashMap<String, int[]> countsApps = ris.getCounts();
                int total = (int) r.getValue("data_count");
                String conditionType = (String) r.getValue("condition_type");
                String  programName = (String) r.getValue("name");

                int[] countArray = countsApps.get(programName);
                if(conditionType.equals("sandbox"))
                {
                    countArray[0]=total;
                }
                else
                {
                    countArray[1]=total;
                }
            }
        }
        return ans;
    }

    public static HashMap<String, int[]> makeHashMapApps(FillTable ft)
    {
        HashMap<String, int[]> countsApps = new HashMap<String, int[]>();
        ArrayList<String> programNames = ft.getProgramNames();
        for(int i=0;i<programNames.size();i++)
        {
            countsApps.put(programNames.get(i),new int[2]);
        }
        return countsApps;
    }

    public void setCountsApps(HashMap<String, int[]> countsApps) {
        this.countsApps = countsApps;
    }

    public HashMap<String, int[]> getCounts()
    {
        return this.countsApps;
    }
    /////////////////////////////////////////////////////////////////////////////////////////

    public RsaImageSet(File dir, String species, String experiment,
                       String plant, String imagingDay, ISecurityManager ism) {
        this.species = species;
        this.experiment = experiment;
        this.plant = plant;
        this.imagingDay = imagingDay;

        String s1 = File.separator + species + File.separator + experiment
                + File.separator + plant + File.separator + imagingDay;
//                + File.separator + preferredType;
        this.inputDir = new File(dir.getAbsolutePath() + File.separator
                + "original_images" + s1);
        this.processedDir = new File(dir.getAbsolutePath() + File.separator
                + "processed_images" + s1);
        this.protectedInputDir = new File(this.processedDir + File.separator
                + "original");

        DirectoryFileFilter dff = new DirectoryFileFilter();
        File[] imgs = this.inputDir.listFiles(dff);
        // System.out.println(this.getClass() + " " + inputDir);

        this.imagingDate = null;

        // bug is caused if there are goofy directories without images
        if (imgs != null && imgs.length > 0) {
            this.imagingDate = Calendar.getInstance();
            this.imagingDate.setTimeInMillis(imgs[0].lastModified());
        } else {
            // System.out.println("No images in:" + inputDir.getAbsolutePath());
        }

        HashSet<String> hs = new HashSet<String>();
        if (imgs != null) {
            for (File img : imgs) {
                String[] ss = img.getName().split("\\.", 0);
                String t = ss[ss.length - 1];
                if (!hs.contains(t)) {
                    hs.add(t);
                }
            }
        }

        this.inputTypes = hs.toArray(new String[0]);
        this.baseDir = dir;
        this.ism = ism;
    }

    /**
     * Create output directory structure (if it doesn't exist), set the
     * appropriate permissions and groups
     *
     * //@param dir Root directory of the output structure //@param chmod Chmod
     * permissions for files and directories (e.g., u=rwX,go=rX) //@param group
     * Group to set for files and directories
     */
    public void preprocess() {
        File d1 = new File(baseDir.getAbsolutePath() + File.separator
                + "processed_images");
        makeAndSet(d1);
        File d2 = new File(d1.getAbsolutePath() + File.separator + species);
        makeAndSet(d2);
        File d3 = new File(d2.getAbsolutePath() + File.separator + experiment);
        makeAndSet(d3);
        File d4 = new File(d3.getAbsolutePath() + File.separator + plant);
        makeAndSet(d4);
        File d5 = new File(d4.getAbsolutePath() + File.separator + imagingDay);
        makeAndSet(d5);

//        ism.setPermissions(d1, true);


        // tw 2015jan7 In linux, directories can be symbolically linked,
        // which made this a 1 line loop previously
        // In windows, only linking files is permitted.
        // The directory and subdirectory have to be made before linking files.
        if (!protectedInputDir.exists()) {

            protectedInputDir.mkdir();
            ism.setPermissions(protectedInputDir, false);
            //ism.setPermissions(d1, true);

            for ( File subDir : inputDir.listFiles() ) {
                if ( subDir.isDirectory() ) {

                    File protectedSubDir = new File(protectedInputDir
                            + File.separator + subDir.getName() );
                    protectedSubDir.mkdir();
                    ism.setPermissions(protectedSubDir, false);

                    for ( File subFile : subDir.listFiles() ) {

                        File protectedSubFile = new File(protectedSubDir
                                + File.separator + subFile.getName());

                        if ( subFile.isFile() ) {
                            // System.out.println("symbolicFile: " + protectedSubFile);
                            FileUtil.createSymLink(protectedSubFile, subFile);
                            // ism.setPermissions(protectedSubFile, false);
                        }
                    }
                }
                else {
                    File protectedInputFile = new File(protectedInputDir
                            + File.separator + subDir.getName() );
                    FileUtil.createSymLink(protectedInputFile, subDir);
                    // ism.setPermissions(protectedInputFile, false);
                }

            }
//            ism.setDirLinkPermissions(d1, true);


        }
    }

    public static StringPairFilter getPlantFilter(StringPairFilter spf_in) {

        String plantday = spf_in.toString();
        // the last three symbols are the day/hour info
        String plant = plantday.substring(0, plantday.length() - 3);

        StringPairFilter spfp = StringPairFilter.getInstance(plant);

        return spfp;
    }

    public static StringPairFilter getDayFilter(StringPairFilter spf_in) {
        String plantday = spf_in.toString();
        // the last three symbols are the day/hour info
        String day = plantday.substring(plantday.length() - 3);

        StringPairFilter spfd = StringPairFilter.getInstance(day);

        return spfd;
    }


    protected void makeAndSet(File dir) {
        if (!dir.exists()) {
            dir.mkdir();

            // // tw 2014nov12
            // ism.setDirectoryPermissions(dir);
            ism.setPermissions(dir, false);
        }
    }

    public File getBaseDir() {
        return baseDir;
    }

    public String getExperiment() {
        return experiment;
    }

    public Calendar getImagingDate() {
        return imagingDate;
    }

    public String getImagingDay() {
        return imagingDay;
    }

    public File getInputDir() {
        return inputDir;
    }

    public String[] getInputTypes() {
        return inputTypes;
    }

    public String getPlant() {
        return plant;
    }

    public File getProcessedDir() {
        return processedDir;
    }

    public File getProtectedInputDir() {
        return protectedInputDir;
    }

    public String getSpecies() {
        return species;
    }

    public String getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(String preferredType) {
        // System.out.println(this.getClass() + " preferred " + preferredType);
        this.preferredType = preferredType;
        this.preferredInputDir = new File(this.inputDir.getAbsolutePath()
                + File.separator + preferredType);
    }

    public File getPreferredInputDir() {
        return preferredInputDir;
    }

    @Override
    public int hashCode() {
        return species.hashCode() + 33 * experiment.hashCode() + 7
                * plant.hashCode() + 2 * imagingDay.hashCode();
    }

    @Override
    public String toString() {
        return experiment + "." + species + "." + plant + "." + imagingDay;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj != this) {
            if (obj.getClass().equals(RsaImageSet.class)) {
                RsaImageSet ris = (RsaImageSet) obj;
                ans = ris.species.equals(this.species)
                        && ris.experiment.equals(this.experiment)
                        && ris.plant.equals(this.plant)
                        && ris.imagingDay.equals(this.imagingDay);
            }
        }

        return ans;
    }
}
