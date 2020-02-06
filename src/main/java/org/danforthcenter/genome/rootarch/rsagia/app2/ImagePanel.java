/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author bm93
 */
public class ImagePanel extends JPanel implements IResizable {
	protected double sx;
	protected double sy;
	protected int rot;
	protected BufferedImage bi;
	protected AffineTransform at;

	public ImagePanel() {
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
			// System.out.println("Repainting image!");
			// int sx2 = (int)Math.round(bi.getWidth() * sx);
			// int sy2 = (int)Math.round(bi.getHeight() * sx);
			// double theta = rot * Math.PI / 2.0;
			//
			// Graphics2D g2d = (Graphics2D)g;
			// AffineTransform at = g2d.getTransform();
			// at.rotate(theta);
			// int w = sx2;
			// int h = sy2;
			//
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
			// //at.scale(sx, sy);
			// setPreferredSize(new java.awt.Dimension(w, h));
			// revalidate();
			// firePropertyChange("size", null, null);
			//
			//
			// at.scale(sx, sy);
			// g2d.drawRenderedImage(bi, at);
			// //g2d.draw
			// /*
			// if (rot != 1 && rot != 3)
			// {
			// g2d.drawImage(bi, 0, 0, sx2, sy2, 0, 0, bi.getWidth(),
			// bi.getHeight(), null);
			// }
			// else
			// {
			// g2d.drawImage(bi, 0, 0, sx2, sy2, 0, 0, bi.getWidth(),
			// bi.getHeight(), null);
			// }
			// */
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawRenderedImage(bi, at);
		}
	}

	protected void updateTransform() {
		int sx2 = (int) Math.round(bi.getWidth() * sx);
		int sy2 = (int) Math.round(bi.getHeight() * sx);
		double theta = rot * Math.PI / 2.0;

		Graphics2D g2d = (Graphics2D) getGraphics();
		at = g2d.getTransform();
		at.rotate(theta);
		int w = sx2;
		int h = sy2;

		switch (rot) {
		case 1:
			w = sy2;
			h = sx2;
			at.translate(0, -w);
			break;
		case 2:
			at.translate(-w, -h);
			break;
		case 3:
			w = sy2;
			h = sx2;
			at.translate(-h, 0);
			break;
		}
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
		this.rot = rot;

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
