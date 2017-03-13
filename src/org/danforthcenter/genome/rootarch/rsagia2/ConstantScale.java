/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

/**
 * This is a bit of hack class. Basically, it allows me to create a virtual
 * scale output at run-time. My plan is to use this for the Export
 * functionality.
 * 
 * @author bm93
 */
public class ConstantScale implements IOutputScale {
	protected double scale;

	public ConstantScale(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return scale;
	}
}
