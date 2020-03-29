/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author bm93
 */
public class ApplicationManager {
	protected ISecurityManager ism;
	protected HashMap<String, IApplication> nameToApp;
	protected Scale scale;
	protected Crop crop;
	protected Crop recrop;
	protected GiaRoot2D giaRoot2D;
	protected Export export;
	protected Rootwork3D rootwork3D;
    protected Rootwork3DPers rootwork3DPers;
	protected GiaRoot3D giaRoot3D;
	protected Gia3D_v2 gia3D_v2;
	protected QualityControl qc;
	protected QualityControl qc2;
	protected QualityControl qc3;
	protected Import importApp;
	protected DirRename dirRenameApp;

	public ApplicationManager(ISecurityManager ism, Scale scale, Crop crop,
			Crop recrop, GiaRoot2D giaRoot2D, Export export,
            // tw 2015jun29
//			Rootwork3D rootwork3D, GiaRoot3D giaRoot3D,
            Rootwork3D rootwork3D, Rootwork3DPers rootwork3DPers, GiaRoot3D giaRoot3D,
			Gia3D_v2 gia3D_v2, QualityControl qc, QualityControl qc2, QualityControl qc3, Import importApp, DirRename dirRenameApp) {
		this.ism = ism;
		this.scale = scale;
		this.crop = crop;

		// for now there is no special recrop folder -
		// the result of the recropping is kept in the crop folder
		this.recrop = recrop;

		this.giaRoot2D = giaRoot2D;
		this.export = export;
		this.rootwork3D = rootwork3D;
        this.rootwork3DPers = rootwork3DPers;
		this.giaRoot3D = giaRoot3D;
		this.gia3D_v2 = gia3D_v2;
		this.qc = qc;
		this.qc2 = qc2;
		this.qc3 = qc3;
		this.importApp = importApp;
		this.dirRenameApp = dirRenameApp;

		nameToApp = new HashMap<String, IApplication>();
		nameToApp.put(scale.getName(), scale);
		nameToApp.put(crop.getName(), crop);
		nameToApp.put(recrop.getName(), recrop);
		nameToApp.put(giaRoot2D.getName(), giaRoot2D);
		nameToApp.put(rootwork3D.getName(), rootwork3D);
        nameToApp.put(rootwork3DPers.getName(), rootwork3DPers);
		nameToApp.put(giaRoot3D.getName(), giaRoot3D);
		nameToApp.put(gia3D_v2.getName(), gia3D_v2);
		nameToApp.put(qc.getName(), qc);
		nameToApp.put(qc2.getName(), qc2);
		nameToApp.put(qc3.getName(), qc3);
		nameToApp.put(importApp.getName(), importApp);
		nameToApp.put(dirRenameApp.getName(), dirRenameApp);

	}

	public Export getExport() {
		return export;
	}

	public ISecurityManager getIsm() {
		return ism;
	}

	public Crop getCrop() {
		return crop;
	}

	public Crop getRecrop() {
		return recrop;
	}

	public GiaRoot2D getGiaRoot2D() {
		return giaRoot2D;
	}

	public Scale getScale() {
		return scale;
	}

	public Rootwork3D getRootwork3D() {
		return rootwork3D;
	}

    public Rootwork3DPers getRootwork3DPers() {
        return rootwork3DPers;
    }

	public GiaRoot3D getGiaRoot3D() {
		return giaRoot3D;
	}

	public Gia3D_v2 getGia3D_v2() {
		return gia3D_v2;
	}

	public QualityControl getQc() {
		return qc;
	}

	public QualityControl getQc2() {
		return qc2;
	}

	public QualityControl getQc3() {
		return qc3;
	}

	public Import getImport() {
		return importApp;
	}

	public DirRename getDirRename() {
		return dirRenameApp;
	}

	public IApplication getApplicationByName(String n) {
		return nameToApp.get(n);
	}

	public File[] getSandboxDirectories(RsaImageSet ris) {
		File f = getSandboxDirectory(ris);
		File[] fs = f.listFiles(new DirectoryFileFilter());

		ArrayList<File> ans = new ArrayList<File>();
		if (fs != null) {
			for (File f2 : fs) {
				File[] fs2 = f2.listFiles();
				if (fs2 != null) {
					for (File f3 : fs2) {
						ans.add(f3);
					}
				}
			}
		}

		return (File[]) (ans.toArray(new File[ans.size()]));
	}

	protected File getSandboxDirectory(RsaImageSet ris) {
		return new File(ris.getProcessedDir() + File.separator + "sandbox");
	}

	protected static class ApplicationManagerException extends RuntimeException {
		public ApplicationManagerException(Throwable th) {
			super(th);
		}

		public ApplicationManagerException(String msg) {
			super(msg);
		}
	}
}
