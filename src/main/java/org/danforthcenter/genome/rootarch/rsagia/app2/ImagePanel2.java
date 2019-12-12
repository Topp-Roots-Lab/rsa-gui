/*
 *  Copyright 2013 vp23.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author vp23
 *
 *         currently not used
 *
 */
public class ImagePanel2 extends JPanel implements IResizable {
	protected double sx;
	protected double sy;
	// protected int rot;
	protected BufferedImage bi;
	protected AffineTransform at;

	public ImagePanel2() {
		super();
		this.setBackground(Color.red);
		this.setOpaque(true);
		sx = 1.0;
		sy = 1.0;
		bi = null;
		at = new AffineTransform();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (bi != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawRenderedImage(bi, at);
		}
	}

	protected void updateTransform() {
		int sx2 = (int) Math.round(bi.getWidth() * sx);
		int sy2 = (int) Math.round(bi.getHeight() * sx);
		// double theta = rot * Math.PI / 2.0;

		Graphics2D g2d = (Graphics2D) this.getGraphics();
		at = g2d.getTransform();
		// at.rotate(theta);
		int w = sx2;
		int h = sy2;

		// switch (rot)
		// {
		// case 1:
		// w = sy2;
		// h = sx2;
		// at.translate(0, -w);
		// break;
		// case 2:
		// at.translate(-w, -h);
		// break;
		// case 3:
		// w = sy2;
		// h = sx2;
		// at.translate(-h, 0);
		// break;
		// }
		// at.scale(sx, sy);
		setPreferredSize(new java.awt.Dimension(w, h));
		revalidate();
		firePropertyChange("size", null, null);

		at.scale(sx, sy);
	}

	public void setScale(double sx, double sy) {
		this.sx = sx;
		this.sy = sy;

		updateTransform();
		repaint();
	}

	public void setRotation(int rot) {
		// this.rot = rot;

		updateTransform();
		repaint();
	}

	public void setImage(File f) {
		bi = null;
		try {
			bi = ImageIO.read(f);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Image:" + f.getAbsolutePath()
					+ " could not be loaded.");
		}

		setImage(bi);
	}

	public void setImage(BufferedImage bi) {
		this.bi = bi;

		// this.getParent().setPreferredSize(new
		// java.awt.Dimension(bi.getWidth(), bi.getHeight()));
		updateTransform();
		repaint();
	}

	public double[] getScale() {
		double[] ans = { sx, sy };
		return ans;
	}
}
