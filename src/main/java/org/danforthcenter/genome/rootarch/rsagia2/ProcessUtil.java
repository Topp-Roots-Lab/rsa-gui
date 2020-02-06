/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.IOException;

/**
 * Apparently, Processes from ProcessBuilder don't clear up their resources
 * properly. The documentation sucks. You need to explicitly close all the
 * associate streams and call the destroy method.
 * 
 * @author bm93
 */
public class ProcessUtil {
	public static Process dispose(Process p) {
		try {
			if (p.getErrorStream() != null) {
				p.getErrorStream().close();
			}
		} catch (IOException e) {

		}

		try {
			if (p.getInputStream() != null) {
				p.getInputStream().close();
			}
		} catch (IOException e) {

		}

		try {
			if (p.getOutputStream() != null) {
				p.getOutputStream().close();
			}
		} catch (IOException e) {

		}

		p.destroy();

		return null;
	}
}
