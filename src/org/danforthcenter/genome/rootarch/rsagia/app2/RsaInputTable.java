/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.IApplication;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author bm93
 */
public class RsaInputTable extends javax.swing.JTable implements
		javax.swing.Scrollable, java.awt.event.MouseListener,
		java.awt.event.ActionListener {
	public static final String SELECTED = "Selected";
	public static final String EXPERIMENT = "Experiment";
	public static final String SPECIES = "Species";
	public static final String PLANT = "Plant";
	public static final String IMAGING_DAY = "Imaging Day";
	public static final String IMAGE_TYPES = "Image Types";
	public static final String PREFERRED_TYPE = "Preferred Type";
	public static final String SCALE_SANDBOX = "# Scale (sandbox)";
	public static final String CROP_SANDBOX = "# Crop (sandbox)";
	public static final String GIA_SANDBOX = "# 2D (sandbox)";
	public static final String SCALE_TOTAL = "# Scale (total)";
	public static final String CROP_TOTAL = "# Crop (total)";
	public static final String GIA_TOTAL = "# 2D (total)";
	public static final String SCALE_SAVED = "# Scale (saved)";
	public static final String CROP_SAVED = "# Crop (saved)";
	public static final String GIA_SAVED = "# 2D (saved)";
	public static final String RW_TOTAL = "# Rootwork (total)";
	public static final String RW_SANDBOX = "# Rootwork (sandbox)";
	public static final String RW_SAVED = "# Rootwork (saved)";
	public static final String GIA3D_SAVED = "# Gia 3D (saved)";
	public static final String GIA3D_SANDBOX = "# Gia 3D (sandbox)";
	public static final String GIA3D_TOTAL = "# Gia 3D (total)";
	public static final String QC2_SANDBOX = "# Qc2 (sandbox)";
	public static final String QC2_SAVED = "# Qc2 (saved)";

	protected HashMap<String, TableCellEditor> editors;
	protected HashMap<String, TableCellRenderer> renderers;
	protected ArrayList<String> savedCols;
	protected ArrayList<RsaImageSet> inputData;
	protected ApplicationManager am;
	protected HashSet<String> allCols;
	protected JComboBox preferredTypeCombo;
	protected MultiComboBoxCellEditor mcb;

	protected JPopupMenu selectPopup;
	protected JMenuItem chkItem;
	protected JMenuItem uchkItem;

	protected JPopupMenu preferPopup;
	protected ArrayList<JMenuItem> preferItems;
	protected int preferPrefixLength;

	public RsaInputTable(ApplicationManager am) {
		super();
		this.am = am;
		this.setAutoCreateRowSorter(true);
		// vp23 commented
		// this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// vp23 added
		this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.setFillsViewportHeight(true);

		this.selectPopup = new JPopupMenu();
		chkItem = new JMenuItem("Check selected");
		chkItem.addActionListener(this);
		selectPopup.add(chkItem);
		uchkItem = new JMenuItem("Uncheck selected");
		uchkItem.addActionListener(this);
		selectPopup.add(uchkItem);

		preferPopup = new JPopupMenu();
		preferItems = new ArrayList<JMenuItem>();
		String pp = "Set prefered to ";
		preferPrefixLength = pp.length();
		for (String s : RsaImageSet.ALLOWED_TYPES) {
			JMenuItem jmi = new JMenuItem("Set prefered to " + s);
			jmi.addActionListener(this);
			preferPopup.add(jmi);
		}
		this.addMouseListener(this);

		editors = new HashMap<String, TableCellEditor>();
		renderers = new HashMap<String, TableCellRenderer>();

		allCols = new HashSet<String>();
		allCols.add(SELECTED);
		allCols.add(SPECIES);
		allCols.add(EXPERIMENT);
		allCols.add(PLANT);
		allCols.add(IMAGING_DAY);
		allCols.add(IMAGE_TYPES);
		allCols.add(PREFERRED_TYPE);
		allCols.add(SCALE_SANDBOX);
		allCols.add(SCALE_SAVED);
		allCols.add(SCALE_TOTAL);
		allCols.add(CROP_SANDBOX);
		allCols.add(CROP_SAVED);
		allCols.add(CROP_TOTAL);
		allCols.add(GIA_SANDBOX);
		allCols.add(GIA_SAVED);
		allCols.add(GIA_TOTAL);
		allCols.add(GIA3D_SANDBOX);
		allCols.add(GIA3D_SAVED);
		allCols.add(GIA3D_TOTAL);
		allCols.add(RW_SANDBOX);
		allCols.add(RW_SAVED);
		allCols.add(RW_TOTAL);
		allCols.add(QC2_SANDBOX);
		allCols.add(QC2_SAVED);

		mcb = new MultiComboBoxCellEditor();

		DefaultTableModel dtm = new DefaultTableModel();// new
														// DefaultTableModel(new
														// Object[][]
														// {{null,null,null,null}},
														// new String[] {"what",
														// "why", "where",
														// "when"});
		this.editors.put(SELECTED,
				this.getDefaultEditor(new Boolean(false).getClass()));
		this.renderers.put(SELECTED,
				this.getDefaultRenderer(new Boolean(false).getClass()));
		this.editors.put(EXPERIMENT,
				this.getDefaultEditor(new String().getClass()));
		this.renderers.put(EXPERIMENT,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(SPECIES,
				this.getDefaultEditor(new String().getClass()));
		this.renderers.put(SPECIES,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(PLANT, this.getDefaultEditor(new String().getClass()));
		this.renderers.put(PLANT,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(IMAGING_DAY,
				this.getDefaultEditor(new String().getClass()));
		this.renderers.put(IMAGING_DAY,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(IMAGE_TYPES,
				this.getDefaultEditor(new String().getClass()));
		this.renderers.put(IMAGE_TYPES,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(PREFERRED_TYPE, mcb);
		this.renderers.put(PREFERRED_TYPE,
				this.getDefaultRenderer(new String().getClass()));
		this.editors.put(SCALE_SANDBOX,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(SCALE_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(CROP_SANDBOX,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(CROP_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(GIA_SANDBOX,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(GIA_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(SCALE_SAVED,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(SCALE_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(CROP_SAVED,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(CROP_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(GIA_SAVED,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(GIA_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(SCALE_TOTAL,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(SCALE_TOTAL,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(CROP_TOTAL,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(CROP_TOTAL,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.editors.put(GIA_TOTAL,
				this.getDefaultEditor(new Integer(-1).getClass()));
		this.renderers.put(GIA_TOTAL,
				this.getDefaultRenderer(new Integer(-1).getClass()));

		this.renderers.put(GIA3D_TOTAL,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.renderers.put(GIA3D_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.renderers.put(GIA3D_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));

		this.renderers.put(RW_TOTAL,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.renderers.put(RW_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.renderers.put(RW_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));

		this.renderers.put(QC2_SANDBOX,
				this.getDefaultRenderer(new Integer(-1).getClass()));
		this.renderers.put(QC2_SAVED,
				this.getDefaultRenderer(new Integer(-1).getClass()));

		// eh, we're enforcing the order of the saved cols
		savedCols = new ArrayList<String>();
		savedCols.add(SELECTED);
		savedCols.add(IMAGE_TYPES);
		savedCols.add(PREFERRED_TYPE);

		for (String s : savedCols) {
			addColumn(dtm, s);
		}

		this.setModel(dtm);
		this.getTableHeader().setReorderingAllowed(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chkItem) {
			setSelectedOnSelected(true);
		} else if (e.getSource() == uchkItem) {
			setSelectedOnSelected(false);
		} else if (preferPopup.getComponentIndex((JMenuItem) e.getSource()) > -1)// preferItems.contains((JMenuItem)e.getSource()))
		{
			String s = ((JMenuItem) e.getSource()).getActionCommand()
					.substring(preferPrefixLength);
			setPreferedOnSelected(s);
		}
	}

	protected void setPreferedOnSelected(String s) {
		int[] rows = this.getSelectedRows();
		for (int x : rows) {
			int r = this.convertRowIndexToModel(x);
			RsaImageSet ris = inputData.get(r);
			for (String t : ris.getInputTypes()) {
				if (t.equals(s)) {
					this.getModel().setValueAt(s, r, 2);
				}
			}
		}
	}

	protected void setSelectedOnSelected(boolean b) {
		int[] rows = this.getSelectedRows();
		for (int x : rows) {
			int r = this.convertRowIndexToModel(x);
			this.getModel().setValueAt(b, r, 0);
		}
	}

	public void mouseReleased(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseExited(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseEntered(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			if (e.isPopupTrigger()) {
				Point p = e.getPoint();
				int c = this.columnAtPoint(p);
				if (c == 0) {
					selectPopup.show(this, p.x, p.y);
				} else if (c == 2) {
					preferPopup.show(this, p.x, p.y);
				}

			}

		}

	}

	public void mouseClicked(MouseEvent e) {

	}

	protected void addColumn(DefaultTableModel dtm, String s) {
		dtm.addColumn(s);
		// dtm.
		// setModel(dtm);
		// this.getColumn(s).setCellEditor(editors.get(s));
		// this.getColumn(s).setCellRenderer(renderers.get(s));
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);

		for (int i = 0; i < this.getColumnCount(); i++) {
			String s = this.getColumnName(i);
			// this.getColumn(s).sizeWidthToFit();

			// ungh, hack on the width
			// the set to header width method doesn't seem to work
			this.getColumn(s).setPreferredWidth(100);
			this.getColumn(s).setCellEditor(editors.get(s));
			this.getColumn(s).setCellRenderer(renderers.get(s));
		}

		doLayout();

	}

	public HashSet<String> getAllPossibleColumns() {
		return allCols;
	}

	public HashSet<String> getSavedColumns() {
		return new HashSet<String>(savedCols);
	}

	public int getSavedColumnCount() {
		return savedCols.size();
	}

	protected Object computeCellData(int row, String col) {
		RsaImageSet ris = inputData.get(row);
		Object ans = null;
		if (col.equals(SELECTED)) {
			ans = new Boolean(false);
		} else if (col.equals(IMAGE_TYPES)) {
			ans = Arrays.toString(ris.getInputTypes());
		} else if (col.equals(PREFERRED_TYPE)) {
			ans = ris.getInputTypes()[0];
		} else if (col.equals(SPECIES)) {
			ans = ris.getSpecies();
		} else if (col.equals(EXPERIMENT)) {
			ans = ris.getExperiment();
		} else if (col.equals(PLANT)) {
			ans = ris.getPlant();
		} else if (col.equals(IMAGING_DAY)) {
			ans = ris.getImagingDay();
		} else if (col.equals(SCALE_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getScale(), false, true));
		} else if (col.equals(SCALE_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getScale(), true, false));
		} else if (col.equals(SCALE_TOTAL)) {
			ans = new Integer(getAppCount(ris, am.getScale(), true, true));
		} else if (col.equals(CROP_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getCrop(), false, true));
		} else if (col.equals(CROP_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getCrop(), true, false));
		} else if (col.equals(CROP_TOTAL)) {
			ans = new Integer(getAppCount(ris, am.getCrop(), true, true));
		} else if (col.equals(GIA_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getGiaRoot2D(), false, true));
		} else if (col.equals(GIA_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getGiaRoot2D(), true, false));
		} else if (col.equals(GIA_TOTAL)) {
			ans = new Integer(getAppCount(ris, am.getGiaRoot2D(), true, true));
		} else if (col.equals(GIA3D_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getGia3D_v2(), false, true));
		} else if (col.equals(GIA3D_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getGia3D_v2(), true, false));
		} else if (col.equals(GIA3D_TOTAL)) {
			ans = new Integer(getAppCount(ris, am.getGia3D_v2(), true, true));
		} else if (col.equals(RW_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getRootwork3D(), false, true));
		} else if (col.equals(RW_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getRootwork3D(), true, false));
		} else if (col.equals(RW_TOTAL)) {
			ans = new Integer(getAppCount(ris, am.getRootwork3D(), true, true));
		} else if (col.equals(QC2_SANDBOX)) {
			ans = new Integer(getAppCount(ris, am.getQc2(), false, true));
		} else if (col.equals(QC2_SAVED)) {
			ans = new Integer(getAppCount(ris, am.getQc2(), true, false));
		}

		return ans;
	}

	protected int getAppCount(RsaImageSet ris, IApplication app,
			boolean doSaved, boolean doSandbox) {
		ArrayList<OutputInfo> ois = OutputInfo.getInstances(am, ris, doSaved,
				doSandbox, null, false);
		int v = 0;
		for (OutputInfo oi : ois) {
			if (oi.isValid() && oi.getAppName().equals(app.getName())) {
				v++;
			}
		}

		return v;
	}

	public ArrayList<RsaImageSet> getInputData() {
		return this.inputData;
	}

	public void updateRows(ArrayList<Integer> rowIndexes) {
		int colCount = this.getColumnCount();
		for (int i : rowIndexes) {
			for (int j = savedCols.size(); j < colCount; j++) {
				Object ans = computeCellData(i, this.getColumnName(j));
				this.getModel().setValueAt(ans, i, j);
			}
		}
	}

	public ArrayList<Integer> getCheckedRowIndexes() {
		TableModel dtm = this.getModel();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < dtm.getRowCount(); i++) {
			if ((Boolean) dtm.getValueAt(i, 0)) {
				indexes.add(i);
			}
		}
		return indexes;
	}

	public void setColumns(ArrayList<String> cols) {
		ArrayList<Object[]> newData = new ArrayList<Object[]>();
		DefaultTableModel dtm = (DefaultTableModel) this.getModel();
		for (int i = 0; i < dtm.getRowCount(); i++) {
			Object[] nr = new Object[savedCols.size() + cols.size()];
			for (int j = 0; j < savedCols.size(); j++) {
				nr[j] = dtm.getValueAt(i, j);
			}

			for (int j = savedCols.size(); j - savedCols.size() < cols.size(); j++) {
				nr[j] = computeCellData(i, cols.get(j - savedCols.size()));
			}

			newData.add(nr);
		}

		DefaultTableModel dtm2 = new DefaultTableModel();
		for (String s : savedCols) {
			addColumn(dtm2, s);
		}
		for (String s : cols) {
			addColumn(dtm2, s);
		}
		for (Object[] row : newData) {
			dtm2.addRow(row);
		}

		this.setModel(dtm2);
	}

	/**
	 * Returns an array of selected data, in order! Also sets preferred type.
	 * 
	 * @return
	 */
	public ArrayList<RsaImageSet> getSelectedData() {
		ArrayList<RsaImageSet> ans = new ArrayList<RsaImageSet>();
		ArrayList<Integer> order = new ArrayList<Integer>();
		DefaultTableModel dtm = (DefaultTableModel) getModel();
		for (int i = 0; i < dtm.getRowCount(); i++) {
			if ((Boolean) dtm.getValueAt(i, 0)) {
				RsaImageSet ris = inputData.get(i);
				ris.setPreferredType((String) dtm.getValueAt(i, 2));
				order.add(i);
			}
		}

		ViewOrderComparator voc = new ViewOrderComparator(this);
		Collections.sort(order, voc);

		for (Integer i : order) {
			ans.add(inputData.get(i));
		}

		return ans;
	}

	public void setData(ArrayList<RsaImageSet> inputData) {
		DefaultTableModel dtm = (DefaultTableModel) this.getModel();
		dtm.setRowCount(0);
		this.inputData = inputData;

        System.out.println("setData inputData.size " + inputData.size());

		ArrayList<String[]> types = new ArrayList<String[]>();
		for (int i = 0; i < inputData.size(); i++) {
			// RsaImageSet ris = inputData.get(i);
			types.add(inputData.get(i).getInputTypes());
            System.out.println("setData types.size " + types.size() + " " + types.get(0).length);
			Object[] nr = new Object[dtm.getColumnCount()];
			for (int j = 0; j < nr.length; j++) {
				nr[j] = computeCellData(i, dtm.getColumnName(j));

			}

			dtm.addRow(nr);
		}

		mcb.setData(types);
		// MultiComboBoxCellEditor mcb = new MultiComboBoxCellEditor(types);
		// this.getColumn(PREFERRED_TYPE).setCellEditor(mcb);
		// this.setModel(dtm);
		// doLayout();
	}

	protected static class ViewOrderComparator implements
			java.util.Comparator<Integer> {
		protected JTable table;

		public ViewOrderComparator(JTable table) {
			this.table = table;
		}

		public int compare(Integer o1, Integer o2) {
			int i1 = table.convertRowIndexToView(o1);
			int i2 = table.convertRowIndexToView(o2);

			int ans = 0;
			ans = (i1 < i2) ? -1 : ans;
			ans = (i1 > i2) ? 1 : ans;

			return ans;
		}

	}
}
