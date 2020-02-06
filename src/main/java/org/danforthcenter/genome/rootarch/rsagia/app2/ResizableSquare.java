/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 *
 * @author bm93
 */
public class ResizableSquare extends JComponent implements IResizable,
		MouseListener, IFocusable {

	protected Point p1;
	protected Point p2;
	protected Point p3;
	protected Point p4;
	protected Rectangle r1;
	protected Rectangle r2;
	protected Rectangle r3;
	protected Rectangle r4;
	protected Rectangle r12;
	protected Rectangle r23;
	protected Rectangle r34;
	protected Rectangle r14;
	protected ArrayList<Rectangle> rects;
	protected double sx;
	protected double sy;
	protected Color c;
	protected int rectThickness;
	protected boolean isDragging;
	protected boolean isMoving;
	protected Rectangle draggedRect;
	protected Point prevMousePoint;
	protected boolean topLocked;

	public ResizableSquare(Point p1, Point p2, Point p3, Point p4, double sx,
			double sy, Color c, int rectThickness) {
		super();
		topLocked = false;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		this.sx = sx;
		this.sy = sy;
		this.c = c;
		this.rectThickness = 5;// rectThickness;

		r1 = new Rectangle();
		r2 = new Rectangle();
		r3 = new Rectangle();
		r4 = new Rectangle();

		isDragging = false;
		isMoving = false;
		draggedRect = null;
		rects = new ArrayList<Rectangle>();
		enableEvents(MouseEvent.MOUSE_DRAGGED | MouseEvent.MOUSE_MOVED);

		calcRectangles();
	}

	public void setTopLocked(boolean b) {
		topLocked = b;
	}

	public Point getFocus() {
		return new Point(p1.x + (p3.x - p1.x) / 2, p1.y + (p3.y - p1.y) / 2);
	}

	protected void sortPoints() {
		int mx = (p1.x < p2.x) ? p1.x : p2.x;
		mx = (mx < p3.x) ? mx : p3.x; // only need to search 3 out of the 4

		int my = (p1.y < p2.y) ? p1.y : p2.y;
		my = (my < p3.y) ? my : p3.y;

		Point[] ps = { p1, p2, p3, p4 };
		Point[] ps2 = { p1, p2, p3, p4 };
		for (Point p : ps) {
			if (p.x == mx && p.y == my) {
				ps2[0] = p;
				break;
			}
		}

		for (Point p : ps) {
			if (p.x != mx && p.y == my) {
				ps2[1] = p;
				break;
			}
		}

		for (Point p : ps) {
			if (p.x != mx && p.y != my) {
				ps2[2] = p;
				break;
			}
		}

		for (Point p : ps) {
			if (p.x == mx && p.y != my) {
				ps2[3] = p;
				break;
			}
		}

		p1 = ps2[0];
		p2 = ps2[1];
		p3 = ps2[2];
		p4 = ps2[3];
	}

	protected void calcRectangles() {
		rects.clear();
		sortPoints();
		r1.setRect(new Rectangle(p1.x - rectThickness / 2, p1.y - rectThickness
				/ 2, rectThickness, rectThickness));
		r2.setRect(new Rectangle(p2.x - rectThickness / 2, p2.y - rectThickness
				/ 2, rectThickness, rectThickness));
		r3.setRect(new Rectangle(p3.x - rectThickness / 2, p3.y - rectThickness
				/ 2, rectThickness, rectThickness));
		r4.setRect(new Rectangle(p4.x - rectThickness / 2, p4.y - rectThickness
				/ 2, rectThickness, rectThickness));

		// Point p12 = new Point((p2.x - p1.x) / 2 + p1.x, p1.y);
		// Point p23 = new Point(p2.x, (p3.y - p2.y) / 2 + p2.y);
		// Point p34 = new Point((p3.x - p4.x) / 2 + p4.x, p4.y);
		// Point p14 = new Point(p1.x, (p4.y - p1.y) / 2 + p1.y);

		// r12 = new Rectangle(p12.x-rectThickness/2, p12.y-rectThickness/2,
		// rectThickness, rectThickness);
		// r23 = new Rectangle(p23.x-rectThickness/2, p23.y-rectThickness/2,
		// rectThickness, rectThickness);
		// r34 = new Rectangle(p34.x-rectThickness/2, p34.y-rectThickness/2,
		// rectThickness, rectThickness);
		// r14 = new Rectangle(p14.x-rectThickness/2, p14.y-rectThickness/2,
		// rectThickness, rectThickness);

		rects.add(r1);
		rects.add(r2);
		rects.add(r3);
		rects.add(r4);
		// rects.add(r12);
		// rects.add(r23);
		// rects.add(r34);
		// rects.add(r14);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(c);
		g.drawRect(p1.x, p1.y, p2.x - p1.x, p4.y - p1.y);
		g.setColor(Color.WHITE);
		// g.drawRect(p1.x, p1.y, r1.width, r1.width);
		g.drawRect(r1.x, r1.y, r1.width, r1.height);
		g.drawRect(r2.x, r2.y, r2.width, r2.height);
		g.drawRect(r3.x, r3.y, r3.width, r3.height);
		g.drawRect(r4.x, r4.y, r4.width, r4.height);

		// g.drawRect(r12.x, r12.y, r12.width, r12.height);
		// g.drawRect(r23.x, r23.y, r23.width, r23.height);
		// g.drawRect(r34.x, r34.y, r34.width, r34.height);
		// g.drawRect(r14.x, r14.y, r14.width, r14.height);
	}

	public void setScale(double sx, double sy) {
		p1 = new Point((int) Math.round(p1.x * sx / this.sx),
				(int) Math.round(p1.y * sy / this.sy));
		p2 = new Point((int) Math.round(p2.x * sx / this.sx),
				(int) Math.round(p2.y * sy / this.sy));
		p3 = new Point((int) Math.round(p3.x * sx / this.sx),
				(int) Math.round(p3.y * sy / this.sy));
		p4 = new Point((int) Math.round(p4.x * sx / this.sx),
				(int) Math.round(p4.y * sy / this.sy));

		this.sx = sx;
		this.sy = sy;
		calcRectangles();
		repaint();
	}

	public void setRotation(int rot) {
	}

	public void mouseClicked(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseEntered(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseExited(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		draggedRect = null;
		isMoving = false;

		for (Rectangle r : rects) {
			if (r.contains(p)) {
				draggedRect = r;
				isDragging = true;
				break;
			}
		}

		if (draggedRect == null) {
			if (Line2D.ptLineDist(p1.x, p1.y, p2.x, p2.y, p.x, p.y) < 3
					|| Line2D.ptLineDist(p2.x, p2.y, p3.x, p3.y, p.x, p.y) < 3
					|| Line2D.ptLineDist(p3.x, p3.y, p4.x, p4.y, p.x, p.y) < 3
					|| Line2D.ptLineDist(p1.x, p1.y, p4.x, p4.y, p.x, p.y) < 3) {
				isMoving = true;
				isDragging = true;
			}
		}

		if (isDragging) {
			prevMousePoint = e.getPoint();
			startDragging();
		}
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e);
		if (e.getSource() == this || e.getSource() == this.getParent()) {
			if (isDragging && e.getID() == MouseEvent.MOUSE_DRAGGED) {
				if (isMoving) {
					Point dp = new Point(e.getPoint().x - prevMousePoint.x,
							e.getPoint().y - prevMousePoint.y);
					prevMousePoint = e.getPoint();

					getParent().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					p1 = new Point(p1.x + dp.x, p1.y + ((topLocked) ? 0 : dp.y));
					p2 = new Point(p2.x + dp.x, p2.y + ((topLocked) ? 0 : dp.y));
					p3 = new Point(p3.x + dp.x, p3.y + dp.y);
					p4 = new Point(p4.x + dp.x, p4.y + dp.y);
					calcRectangles();
					repaint();
				} else {
					if (draggedRect == r1) {
						getParent()
								.setCursor(
										Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
						// System.out.println(p1 + ";" + p2 + ";" + p3 + ";" +
						// p4);
						p1 = new Point(e.getPoint().x, ((topLocked) ? p1.y
								: e.getPoint().y));
						p2 = new Point(p2.x, p1.y);
						p4 = new Point(p1.x, p4.y);
						// System.out.println(p1 + ";" + p2 + ";" + p3 + ";" +
						// p4);
					} else if (draggedRect == r2) {
						getParent()
								.setCursor(
										Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
						p2 = new Point(e.getPoint().x, ((topLocked) ? p2.y
								: e.getPoint().y));
						p1 = new Point(p1.x, p2.y);
						p3 = new Point(p2.x, p3.y);
					} else if (draggedRect == r3) {
						getParent()
								.setCursor(
										Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
						p3 = e.getPoint();
						p2 = new Point(p3.x, p2.y);
						p4 = new Point(p4.x, p3.y);
					} else if (draggedRect == r4) {
						getParent()
								.setCursor(
										Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
						p4 = e.getPoint();
						p1 = new Point(p4.x, p1.y);
						p3 = new Point(p3.x, p4.y);
					}

					calcRectangles();
					repaint();
				}
			} else if (e.getID() == MouseEvent.MOUSE_MOVED) {
				Point p = e.getPoint();

				if (r1.contains(p)) {
					getParent()
							.setCursor(
									Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				} else if (r2.contains(p)) {
					getParent()
							.setCursor(
									Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				} else if (r3.contains(p)) {
					getParent()
							.setCursor(
									Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else if (r4.contains(p)) {
					getParent()
							.setCursor(
									Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				} else if (Line2D.ptLineDist(p1.x, p1.y, p2.x, p2.y, p.x, p.y) < 3
						|| Line2D.ptLineDist(p2.x, p2.y, p3.x, p3.y, p.x, p.y) < 3
						|| Line2D.ptLineDist(p3.x, p3.y, p4.x, p4.y, p.x, p.y) < 3
						|| Line2D.ptLineDist(p1.x, p1.y, p4.x, p4.y, p.x, p.y) < 3) {
					getParent().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			} else {
				getParent().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	public Rectangle getRectangle() {
		return new Rectangle((int) (p1.x / sx), (int) (p1.y / sy),
				(int) ((p2.x - p1.x) / sx), (int) ((p4.y - p1.y) / sy));
	}

	protected void startDragging() {
	}

	@Override
	public String toString() {
		return "{(" + p1.x + "," + p1.y + ")," + (p2.x - p1.x) + ","
				+ (p4.y - p1.y) + "}";
	}

	public void mouseReleased(MouseEvent e) {
		isDragging = false;
		// throw new UnsupportedOperationException("Not supported yet.");

	}
}
