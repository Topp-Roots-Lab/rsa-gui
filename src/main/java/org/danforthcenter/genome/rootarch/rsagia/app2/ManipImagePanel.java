/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;

import javax.swing.JLayeredPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 *
 * @author bm93
 */
public class ManipImagePanel extends /* JPanel */JLayeredPane implements
		Scrollable, java.beans.PropertyChangeListener {
	protected ImagePanel ip;
	protected Dimension preferredScrollableViewportSize;
	protected IFocusable focus;

	public ManipImagePanel() {
		super();
		focus = null;
		this.setBackground(Color.GREEN);
		this.setForeground(Color.YELLOW);
		this.setOpaque(true);
		this.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
		ip = new ImagePanel();
		// ip.setVisible(true);

		super.add(ip, new Integer(JLayeredPane.DEFAULT_LAYER));
		ip.setOpaque(true);
		ip.setVisible(true);
		ip.addPropertyChangeListener("size", this);
		this.moveToFront(ip);

		// this.revalidate();
		repaint();
	}

	public Dimension setPreferredScrollableViewportSize(Dimension d) {
		ip.setBounds(new Rectangle(d));
		return this.preferredScrollableViewportSize = d;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.preferredScrollableViewportSize;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return visibleRect.width / 5;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return visibleRect.width / 5;
	}

	public void setImage(BufferedImage bi) {
		ip.setImage(bi);
	}

	public void setImage(File f) {
		ip.setImage(f);
		// this.setPreferredSize(ip.getPreferredSize());
		// imageSizeChanged();
	}

	public void setScale(double sx, double sy) {
		for (Component c : this.getComponents()) {
			IResizable ir = (IResizable) c;
			ir.setScale(sx, sy);
		}

		imageSizeChanged();

		if (focus != null) {
			final JViewport view = (JViewport) this.getParent();
			final java.awt.Point p = focus.getFocus();
			p.x -= view.getExtentSize().width / 2;
			p.y -= view.getExtentSize().height / 2;
			// System.out.println("view mode= " + view.getScrollMode());
			// System.out.println("blit=" + JViewport.BLIT_SCROLL_MODE +
			// "; backing=" + JViewport.BACKINGSTORE_SCROLL_MODE + "; simple=" +
			// JViewport.SIMPLE_SCROLL_MODE);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					view.setViewPosition(p);
				}
			});
		}
	}

	/**
	 * 
	 * @param c
	 *            A component already in this container. The component to focus
	 *            on
	 */
	public void setFocus(IFocusable c) {
		focus = c;
	}

	public void setRotation(int rot) {
		ip.setRotation(rot);
	}

	protected void imageSizeChanged() {
		this.setPreferredSize(ip.getPreferredSize());
		for (Component c : this.getComponents()) {
			Dimension d = this.getPreferredSize();
			c.setBounds(0, 0, d.width, d.height);
		}

		this.repaint();
	}

	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
		Dimension d = this.getPreferredSize();
		comp.setBounds(0, 0, d.width, d.height);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == ip && evt.getPropertyName().equals("size")) {
			imageSizeChanged();
		}
	}
}
