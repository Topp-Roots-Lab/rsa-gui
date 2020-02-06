/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

/**
 * 
 * @author bm93
 */
public class GiaRoot2DInput {
	protected RsaImageSet ris;
	protected String templateString;
	protected OutputInfo crop;
	protected ApplicationManager am;
	protected String descriptors;

	public GiaRoot2DInput(RsaImageSet ris, String templateString,
			OutputInfo crop, ApplicationManager am, String descriptors) {
		this.ris = ris;
		this.templateString = templateString;
		this.crop = crop;
		this.am = am;
		this.descriptors = descriptors;
	}

	public String getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(String descriptors) {
		this.descriptors = descriptors;
	}

	public ApplicationManager getAm() {
		return am;
	}

	public void setAm(ApplicationManager am) {
		this.am = am;
	}

	public OutputInfo getCrop() {
		return crop;
	}

	public void setCrop(OutputInfo crop) {
		this.crop = crop;
	}

	public RsaImageSet getRis() {
		return ris;
	}

	public void setRis(RsaImageSet ris) {
		this.ris = ris;
	}

	public String getTemplateString() {
		return templateString;
	}

	public void setTemplateString(String templateString) {
		this.templateString = templateString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GiaRoot2DInput other = (GiaRoot2DInput) obj;
		if (other == this) {
			return true;
		}
		if (!other.ris.equals(ris)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (this.ris != null ? this.ris.hashCode() : 0);
		return hash;
	}
}
