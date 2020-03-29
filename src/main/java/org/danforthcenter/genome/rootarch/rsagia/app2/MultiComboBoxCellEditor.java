/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;

/**
 * Allows for custom combobox cell editor per row in a table.
 * 
 * @author bm93
 */
public class MultiComboBoxCellEditor implements
		javax.swing.table.TableCellEditor {
	protected HashMap<StringSet, DefaultCellEditor> stringToEditor;
	protected ArrayList<DefaultCellEditor> rowToEditor;
	protected int row;

	public MultiComboBoxCellEditor() {
		row = -1;
		stringToEditor = new HashMap<StringSet, DefaultCellEditor>();
		rowToEditor = new ArrayList<DefaultCellEditor>();
	}

	public void setData(ArrayList<String[]> rows) {
		row = -1;
		stringToEditor = new HashMap<StringSet, DefaultCellEditor>();
		rowToEditor = new ArrayList<DefaultCellEditor>();

		for (int i = 0; i < rows.size(); i++) {
			StringSet ss = new StringSet(rows.get(i));
			if (!stringToEditor.containsKey(ss)) {
				JComboBox<String> cb = new JComboBox<>();
				for (String s : ss.getVals()) {
					cb.addItem(s);
				}

				DefaultCellEditor dce = new DefaultCellEditor(cb);
				stringToEditor.put(ss, dce);
			}

			DefaultCellEditor def = stringToEditor.get(ss);
			rowToEditor.add(def);
		}
	}

	public boolean stopCellEditing() {
		return rowToEditor.get(row).stopCellEditing();
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return rowToEditor.get(row).shouldSelectCell(anEvent);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		rowToEditor.get(row).removeCellEditorListener(l);
	}

	public boolean isCellEditable(EventObject anEvent) {
		return true;
		// return rowToEditor.get(row).isCellEditable(anEvent);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		row = table.convertRowIndexToModel(row);
		this.row = row;
		return rowToEditor.get(row).getTableCellEditorComponent(table, value,
				isSelected, row, column);
	}

	public Object getCellEditorValue() {
		return rowToEditor.get(row).getCellEditorValue();
	}

	public void cancelCellEditing() {
		rowToEditor.get(row).cancelCellEditing();
	}

	public void addCellEditorListener(CellEditorListener l) {
		rowToEditor.get(row).addCellEditorListener(l);
	}

	protected static class StringSet {
		protected String[] vals;

		StringSet(String[] vals) {
			this.vals = Arrays.copyOf(vals, vals.length);
			Arrays.sort(this.vals);
		}

		@Override
		public int hashCode() {
			int ans = 0;
			for (String s : vals) {
				ans += 37 * s.hashCode();
			}

			return ans;
		}

		public String[] getVals() {
			return vals;
		}

		@Override
		public boolean equals(Object obj) {
			boolean ans = false;
			if (this == obj) {
				ans = true;
			} else if (obj.getClass().equals(this.getClass())) {
				StringSet ss = (StringSet) obj;
				ans = Arrays.equals(vals, ss.getVals());
			}

			return ans;
		}
	}
}
