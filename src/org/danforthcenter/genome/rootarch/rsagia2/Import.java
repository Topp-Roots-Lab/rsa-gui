package org.danforthcenter.genome.rootarch.rsagia2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Import implements IApplication {
    private String importScriptPath;

    private File baseDir;

    private ISecurityManager ism;

    public Import(String importScriptPath, File baseDir, ISecurityManager ism) {
        this.importScriptPath = importScriptPath;
        this.baseDir = baseDir;
        this.ism = ism;
    }

    public Process start(File importDirectory, boolean deleteOriginals, File organismsFile, File movedImagesetsFile) {
        Process ans = null;

        List<String> commandsList = new ArrayList<>(Arrays.asList(
            this.importScriptPath,
            organismsFile.getAbsolutePath(),
            importDirectory.getAbsolutePath(),
            this.baseDir.getAbsolutePath() + File.separator + "original_images",
            "--non-interactive",
            "--moved-imagesets-file",
            movedImagesetsFile.getAbsolutePath()
        ));
        if (deleteOriginals) {
            commandsList.add("--delete-originals");
        }
        if (this.importScriptPath.endsWith(".py")) {
            commandsList.add(0, "python");
        }

        String[] cmd = commandsList.toArray(new String[0]);

        System.out.println("/t/t" + this.getClass().getSimpleName() + "\n");
        for (String subcmd : cmd) {
            System.out.print(subcmd + " ");
        }
        System.out.println();

        ProcessBuilder pb = new ProcessBuilder(cmd);

        pb.redirectErrorStream(true);

        try {
            UserAccess.elevatePrivileges();
            ans = pb.start();
        } catch (IOException e) {
            throw new ImportException("Error running cmd: "
                    + Arrays.toString(cmd), e);
        } finally {
            UserAccess.reducePrivileges();
        }
        System.out.println("Import process returns " + ans);

        return ans;
    }

    public void preprocess(File organismsFile) throws IOException {
        MetadataDBFunctions dbFunctions = new MetadataDBFunctions();
        Result<Record> organismRecord = dbFunctions.selectAllOrganism();

        FileWriter writer = new FileWriter(organismsFile);
        for (Record r : organismRecord) {
            String speciesCode = (String) r.getValue("species_code");
            String organismName = (String) r.getValue("organism_name");
            writer.write(speciesCode + "\t" + organismName + "\n");
        }
        writer.close();
    }

    public List<String[]> postprocess(File movedImagesetsFile) throws IOException {
        List<String[]> importedImagesets = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(movedImagesetsFile));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            importedImagesets.add(currentLine.split("\\t"));
        }
        return importedImagesets;
    }

    private static class ImportException extends RuntimeException {
        private ImportException(String msg, Throwable th) {
            super(msg, th);
        }
    }

    @Override
    public String getName() {
        return "import";
    }

    @Override
    public int getOutputs() {
        return InputOutputTypes.RAW;
    }

    @Override
    public OutputInfo getOutputInfo(File f, RsaImageSet ris) {
        return null;
    }

    @Override
    public int getPossibleOutputs() {
        return InputOutputTypes.RAW;
    }

    @Override
    public int getOptionalInputs() {
        return InputOutputTypes.NONE;
    }

    @Override
    public int getRequiredInputs() {
        return InputOutputTypes.RAW;
    }

    @Override
    public String getReviewString(OutputInfo oi) {
        return null;
    }

    @Override
    public boolean hasRequiredInput(RsaImageSet ris, ApplicationManager am) {
        return true;
    }
}
