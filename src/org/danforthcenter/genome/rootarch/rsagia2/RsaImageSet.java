/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.danforthcenter.genome.rootarch.rsagia.app2.App;

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

	protected File inputDir;
	protected File processedDir;
	protected File protectedInputDir;
	protected String[] inputTypes;

	protected File baseDir;
	protected ISecurityManager ism;

	protected String preferredType;
	protected File preferredInputDir;

	public static String[] ALLOWED_TYPES = { "jpg", "tiff" };

	public static ArrayList<RsaImageSet> getAll(File dir, ISecurityManager ism) {
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

		String[] spcname = App.getSpecieName();
		List<String> listname = Arrays.asList(spcname);

		if (ss != null && ss.length > 0) {
			for (File s : ss) {
				String species = s.getName();

				// only get the species determined by the default.config file
				boolean view = listname.contains(species);
				if (!view)
					continue;

				boolean b1 = sps.size() == 0;
				for (StringPairFilter spp : sps) {
					b1 = spp.accept(species);
					if (b1) {
						break;
					}
				}
				if (!b1) {
					continue;
				}
				File[] es = s.listFiles();
				if (es != null && es.length > 0) {
					for (File e : es) {
						String experiment = e.getName();
						boolean b2 = exps.size() == 0;
						for (StringPairFilter spp : exps) {
							b2 = spp.accept(experiment);
							if (b2) {
								break;
							}
						}
						if (!b2) {
							continue;
						}
						File[] ps = e.listFiles();

						// If "PlantDay" field is empty
						if (pls_ims.isEmpty()) {
							if (ps != null && ps.length > 0) {
								for (File p : ps) {
									String plant = p.getName();
									boolean b3 = pls.size() == 0;
									for (StringPairFilter spp : pls) {
										b3 = spp.accept(plant);
										if (b3) {
											break;
										}
									}
									if (!b3) {
										continue;
									}
									File[] ids = p.listFiles();
									if (ids != null && ids.length > 0) {
										for (File id : ids) {
											String imagingDay = id.getName();
											boolean b4 = ims.size() == 0;
											for (StringPairFilter spp : ims) {
												b4 = spp.accept(imagingDay);
												if (b4) {
													break;
												}
											}
											if (!b4) {
												continue;
											}
											if (id.listFiles() != null
													&& id.listFiles().length > 0) {
												RsaImageSet ris = new RsaImageSet(
														dir, species,
														experiment, plant,
														imagingDay, ism);
												ans.add(ris);
											}
										}
									}// imageday
								}
							}// plant
						}
						//
						// If "PlantDay" field is not empty, then
						// ignore (by current design) plant and imageday
						// settings
						// presented in the plant and imageday GUI fields
						else {
							if (ps != null && ps.length > 0) {
								for (File p : ps) {
									String plant = p.getName();
									boolean b3 = pls.size() == 0;
									for (StringPairFilter sppd : pls_ims) {
										// get pls and ims for the given pls_ims
										StringPairFilter spp_pls = getPlantFilter(sppd);
										b3 = spp_pls.accept(plant);
										if (!b3) {
											continue;
										}
										File[] ids = p.listFiles();
										if (ids != null && ids.length > 0) {
											for (File id : ids) {
												String imagingDay = id
														.getName();
												boolean b4 = ims.size() == 0;
												StringPairFilter spp_ims = getDayFilter(sppd);
												b4 = spp_ims.accept(imagingDay);
												if (!b4) {
													continue;
												}
												if (id.listFiles() != null
														&& id.listFiles().length > 0) {
													RsaImageSet ris = new RsaImageSet(
															dir, species,
															experiment, plant,
															imagingDay, ism);
													ans.add(ris);
												}
											}
										}// imageday
									}// end for (StringPairFilter sppd :
										// pls_ims)
								}// plant
							}// plants
						}// else If "PlantDay" used, ignore settings
					}
				}
			}
		}

		return ans;
	}

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

	private static StringPairFilter getPlantFilter(StringPairFilter spf_in) {

		String plantday = spf_in.toString();
		// the last three symbols are the day/hour info
		String plant = plantday.substring(0, plantday.length() - 3);

		StringPairFilter spfp = StringPairFilter.getInstance(plant);

		return spfp;
	}

	private static StringPairFilter getDayFilter(StringPairFilter spf_in) {
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
