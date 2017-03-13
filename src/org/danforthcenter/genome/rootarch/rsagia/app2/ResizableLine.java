/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author bm93
 */
public class ResizableLine extends java.awt.Component implements
		java.awt.event.MouseListener, IResizable, IFocusable {
	protected Point p1;
	protected Point p2;
	protected Color c;
	protected double sx;
	protected double sy;
	protected java.awt.Rectangle r1;
	protected java.awt.Rectangle r2;
	protected int rectThickness;
	protected boolean orthOnly;
	protected boolean isDragging;
	protected int draggedRect;
	protected Point prevMouse;

	public ResizableLine() {
		super();
	}

	public ResizableLine(java.awt.Point p1, java.awt.Point p2,
			java.awt.Color c, int rectThickness) {
		this(p1, p2, c, rectThickness, 1, 1);
	}

	public ResizableLine(java.awt.Point p1, java.awt.Point p2,
			java.awt.Color c, int rectThickness, double sx, double sy) {
		this.p1 = p1;
		this.p2 = p2;
		this.c = c;
		this.rectThickness = 5;// rectThickness;
		orthOnly = false;
		isDragging = false;
		this.sx = sx;
		this.sy = sy;
		enableEvents(MouseEvent.MOUSE_DRAGGED | MouseEvent.MOUSE_MOVED);
		r1 = new java.awt.Rectangle(p1.x - this.rectThickness / 2, p1.y
				- this.rectThickness / 2, this.rectThickness,
				this.rectThickness);
		r2 = new java.awt.Rectangle(p2.x - this.rectThickness / 2, p2.y
				- this.rectThickness / 2, this.rectThickness,
				this.rectThickness);
	}

	@Override
	public Point getFocus() {
		return new Point(this.p1.x + (this.p2.x - this.p1.x) / 2, this.p1.y
				+ (this.p2.y - this.p1.y) / 2);
	}

	@Override
	public void setRotation(int rot) {
		// double theta = rot * Math.PI / 2.0;
		// AffineTransform at = new AffineTransform();
		// at.rotate(theta);
		// Point pp1 = new Point();
		// at.transform(p1, pp1);
		// Point pp2 = new Point();
		// at.transform(pp2, pp2);
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(c);
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		g.setColor(Color.WHITE);

		r1 = new java.awt.Rectangle(p1.x - rectThickness / 2, p1.y
				- rectThickness / 2, rectThickness, rectThickness);
		r2 = new java.awt.Rectangle(p2.x - rectThickness / 2, p2.y
				- rectThickness / 2, rectThickness, rectThickness);

		g.drawRect(r1.x, r1.y, r1.width, r1.height);
		g.drawRect(r2.x, r2.y, r2.width, r2.height);
	}

	@Override
	public void setScale(double sx, double sy) {

		Point pp1 = new Point((int) Math.round(p1.x * sx / this.sx),
				(int) Math.round(p1.y * sy / this.sy));
		Point pp2 = new Point((int) Math.round(p2.x * sx / this.sx),
				(int) Math.round(p2.y * sy / this.sy));
		this.sx = sx;
		this.sy = sy;
		setPoints(pp1, pp2);
	}

	public void setOrthOnly(boolean b) {
		orthOnly = b;
	}

	public double getLength() {
		return Math.pow(
				Math.pow((p1.x - p2.x) / sx, 2)
						+ Math.pow((p1.y - p2.y) / sy, 2), 0.5);
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e);
		if (e.getSource() == this || e.getSource() == this.getParent()) {
			// System.out.println(e);
			if (e.getID() == MouseEvent.MOUSE_DRAGGED && isDragging) {
				if (draggedRect == 1) {
					Point p = e.getPoint();
					if (orthOnly) {
						if (Math.abs(p.x - p2.x) < Math.abs(p.y - p2.y)) {
							p.x = p2.x;
						} else {
							p.y = p2.y;
						}
					}
					setPoints(p, p2);
					// p1 = p;
				} else if (draggedRect == 2) {
					Point p = e.getPoint();
					if (orthOnly) {
						if (Math.abs(p.x - p1.x) < Math.abs(p.y - p1.y)) {
							p.x = p1.x;
						} else {
							p.y = p1.y;
						}
					}
					setPoints(p1, p);
					// p2 = p;
				} else {
					Point p = e.getPoint();
					Point dp = new Point(p.x - prevMouse.x, p.y - prevMouse.y);
					prevMouse = p;
					setPoints(new Point(p1.x + dp.x, p1.y + dp.y), new Point(
							p2.x + dp.x, p2.y + dp.y));
				}
				this.repaint();
			} else if (e.getID() == MouseEvent.MOUSE_MOVED && !isDragging) {
				if (r1.contains(e.getPoint()) || r2.contains(e.getPoint())) {
					// this.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else if (java.awt.geom.Line2D.ptLineDist(p1.x, p1.y, p2.x,
						p2.y, e.getPoint().x, e.getPoint().y) < 3) {
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				} else {
					// this.getParent().setCursor(Cursor.getDefaultCursor());
					setCursor(Cursor.getDefaultCursor());
				}
			}
		}
	}

	protected void setPoints(Point p1, Point p2) {
		double l = getLength();
		this.p1 = p1;
		this.p2 = p2;
		this.firePropertyChange("length", l, getLength());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// this.enableEvents(java.awt.Event.MOUSE_MOVE);
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

		boolean b = false;

		if (r1.contains(e.getPoint())) {
			draggedRect = 1;
			b = true;
		} else if (r2.contains(e.getPoint())) {
			draggedRect = 2;
			b = true;
		} else {
			draggedRect = 3;
			prevMouse = e.getPoint();
			b = true;
		}

		if (b) {
			startDragging();
		}
	}

	protected void startDragging() {
		isDragging = true;
		// System.out.println("DRAGGING ON LINE!");
		this.getParent().setCursor(
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	protected void stopDragging() {
		isDragging = false;
		this.getParent().setCursor(Cursor.getDefaultCursor());
	}

	public void mouseReleased(MouseEvent e) {
		if (isDragging) {
			stopDragging();
		}
	}

	@Override
	public String toString() {
		return "{(" + p1.x + "," + p1.y + "),(" + p2.x + "," + p2.y + ")}";
	}
}
