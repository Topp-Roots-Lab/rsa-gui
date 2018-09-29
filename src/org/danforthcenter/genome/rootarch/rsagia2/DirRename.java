package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirRename implements IApplication {
    private String renameScriptPath;

    private ISecurityManager ism;

    public DirRename(String renameScriptPath, ISecurityManager ism) {
        this.renameScriptPath = renameScriptPath;
        this.ism = ism;
    }

    public void start(File src, String newName) {
        List<String> commandsList = new ArrayList<>(Arrays.asList(
                this.renameScriptPath,
                src.getAbsolutePath(),
                newName
        ));
        if (this.renameScriptPath.endsWith(".py")) {
            commandsList.add(0, "python");
        }

        String[] cmd = commandsList.toArray(new String[0]);

        ProcessBuilder pb = new ProcessBuilder(cmd);

        int returnValue = -1;
        Process p = null;
        try {
            p = pb.start();
            returnValue = p.waitFor();
        } catch (IOException e) {
            throw new DirRenameException("Error running cmd: "
                    + Arrays.toString(cmd), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (p != null) {
            ProcessUtil.dispose(p);
        }

        if (returnValue != 0) {
            throw new DirRenameException("Command: "
                    + Arrays.toString(cmd) + "; returned: " + returnValue, null);
        }
    }

    private static class DirRenameException extends RuntimeException {
        private DirRenameException(String msg, Throwable th) {
            super(msg, th);
        }
    }

    @Override
    public String getName() {
        return "dir_rename";
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
