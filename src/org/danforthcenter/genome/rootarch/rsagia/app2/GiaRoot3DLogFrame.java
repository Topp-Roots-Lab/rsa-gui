/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MultiLogWindow.java
 *
 * Created on Apr 1, 2010, 3:04:05 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot3D;
import org.danforthcenter.genome.rootarch.rsagia2.GiaRoot3DOutput;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputVolume3D;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author bm93
 */
public class GiaRoot3DLogFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener, javax.swing.event.ListSelectionListener,
		java.beans.PropertyChangeListener {

	protected int maxProcesses;
	protected GiaRoot3D giaRoot3D;
	protected ApplicationManager am;
	protected ArrayList<RsaImageSet> riss;
	protected ArrayList<IOutputVolume3D> vols;
	protected ArrayList<GiaRoot3DOutput> outputs;

	protected String descriptors;
	protected String config;

	protected ArrayList<JTextArea> outputTextAreas;
	protected ArrayList<JScrollPane> outputPanels;
	protected int cur;
	protected int doneCnt;

	/** Creates new form MultiLogWindow */
	public GiaRoot3DLogFrame(int maxProcesses, GiaRoot3D giaRoot3D,
			ApplicationManager am, ArrayList<RsaImageSet> riss,
			ArrayList<IOutputVolume3D> vols, String descriptors, String config) {
		initComponents();

		this.maxProcesses = maxProcesses;
		this.giaRoot3D = giaRoot3D;
		this.am = am;
		this.riss = riss;
		this.vols = vols;

		this.outputs = new ArrayList<GiaRoot3DOutput>();
		outputTextAreas = new ArrayList<JTextArea>();
		outputPanels = new ArrayList<JScrollPane>();

		String[] cols = { "Image Set", "Status" };
		DefaultTableModel dtm = new DefaultTableModel(cols, 0);
		statusTable.setModel(dtm);
		statusTable.getSelectionModel().addListSelectionListener(this);
		addWindowListener(this);

		cur = 0;
		doneCnt = 0;
		for (int i = 0; i < riss.size(); i++) {
			GiaRoot3DOutput oi = giaRoot3D.preprocess(riss.get(i), vols.get(i),
					descriptors, config);
			outputs.add(oi);
			add(riss.get(i));
		}

		for (int i = 0; i < maxProcesses && i < riss.size(); i++) {
			doNext();
		}
	}

	protected void add(RsaImageSet ris) {
		DefaultTableModel dtm = (DefaultTableModel) statusTable.getModel();

		JTextArea jt = new JTextArea();
		JScrollPane jsp = new JScrollPane();
		jt.setColumns(5);
		jt.setRows(20);
		jsp.setViewportView(jt);
		outputTextAreas.add(jt);
		outputPanels.add(jsp);

		// System.out.println("ADDDED");
		String[] row = { ris.toString(), "Pending" };
		dtm.addRow(row);
	}

	protected void doNext() {
		// System.out.println("ABOUT TO RUn");
		// GiaRoot2DWorker grw = new GiaRoot2DWorker(gia, inputs.get(cur),
		// outputs.get(cur), outputTextAreas.get(cur), cur, am);
		GiaRoot3DWorker gw = new GiaRoot3DWorker(giaRoot3D, outputs.get(cur),
				outputTextAreas.get(cur), cur, am);

		gw.addPropertyChangeListener(this);
		gw.execute();
		cur++;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == statusTable.getSelectionModel()) {
			int i = e.getFirstIndex();
			int r = statusTable.getSelectedRow();

			detailsPanel.removeAll();
			if (r > -1) {
				GroupLayout gl = (GroupLayout) detailsPanel.getLayout();
				gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(
						outputPanels.get(r)));
				gl.setVerticalGroup(gl.createSequentialGroup().addComponent(
						outputPanels.get(r)));

				// mlw.getDetailsPanel().add(outputs.get(r));
				outputTextAreas.get(r).setVisible(true);
			}

			detailsPanel.repaint();
			detailsPanel.validate();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getNewValue());
		if (evt.getSource().getClass().equals(GiaRoot3DWorker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			GiaRoot3DWorker rw = (GiaRoot3DWorker) evt.getSource();
			int i = rw.getId();
			int v = rw.getReturnValue();

			String s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
			// ////////////////////////////////////////////////////////////
			// vp23 - A hack for correcting SRL in giaroot_3d.csv
			// Take it away at the time when gia-roots program fixes
			// the SRL calculation.
			//
			// change SRL in giaroot_3d.csv : SRL --> 1/SRL
			// (according to Paul and Chris)
			//
			fixSRL(rw);
			//
			// end vp23 - A hack for correcting SRL in giaroot_3d.csv
			// ////////////////////////////////////////////////////////////
			statusTable.setValueAt(s, i, 1);
			doneCnt++;

			if (cur < riss.size()) {
				doNext();
			}
		}
	}

	public void windowActivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosed(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosing(WindowEvent e) {
		if (doneCnt != riss.size()) {
			JOptionPane.showMessageDialog(this,
					"Cannot close until all tasks are finished.");
		} else {
			firePropertyChange("done", false, true);
			// mlw.dispose();
		}
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeactivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeiconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowIconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowOpened(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		statusTable = new javax.swing.JTable();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		detailsPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("RootArch Compute - Multi Log");

		statusTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null }, { null, null },
						{ null, null }, { null, null } }, new String[] {
						"Image Set", "Status" }) {
			Class[] types = new Class[] { java.lang.String.class,
					java.lang.String.class };
			boolean[] canEdit = new boolean[] { false, false };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jScrollPane1.setViewportView(statusTable);

		jLabel2.setText("[select a row for more info]");

		detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				javax.swing.BorderFactory.createLineBorder(new java.awt.Color(
						0, 0, 0)), "Image Set Details"));

		javax.swing.GroupLayout detailsPanelLayout = new javax.swing.GroupLayout(
				detailsPanel);
		detailsPanel.setLayout(detailsPanelLayout);
		detailsPanelLayout.setHorizontalGroup(detailsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 667, Short.MAX_VALUE));
		detailsPanelLayout.setVerticalGroup(detailsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 260, Short.MAX_VALUE));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														detailsPanel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														677, Short.MAX_VALUE)
												.addComponent(
														jLabel1,
														javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jLabel2,
														javax.swing.GroupLayout.Alignment.LEADING))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(12, 12, 12)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1)
												.addComponent(jLabel2))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										156,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(detailsPanel,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// new GiaRootFrame(4).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel detailsPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable statusTable;

	// End of variables declaration//GEN-END:variables

	// ============================<editor-fold desc="vp23 hack-SRL">{{{
	private void fixSRL(GiaRoot3DWorker rw) {
		String O3RF_SRL = "o3rf_srl";
		ArrayList<String[]> sheet = new ArrayList<String[]>();

		BufferedReader br = null;
		GiaRoot3DOutput output = rw.getOutput();
		File f = output.getCsvFile();
		try {
			br = new BufferedReader(new FileReader(f));
			String s1 = br.readLine();
			String s2 = br.readLine();
			String[] hdrs = s1.split(",");
			String[] data = s2.split(",");
			// find and fix o3rf_srl - inverse the old value
			for (int j = 0; j < hdrs.length; j++) {
				try {
					if (hdrs[j].equals(O3RF_SRL)) {
						double d = Double.parseDouble(data[j]);
						d = 1 / d;
						data[j] = Double.toString(d);
					}
				} catch (NumberFormatException e) {
					// will not happen
				}

			}
			// add headers and new data to the new sheet
			sheet.add(hdrs);
			sheet.add(data);

		} catch (Exception e) {
			throw new GiaRoot3DLogFrameException("Error parsing file:"
					+ f.getAbsolutePath(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {

				}
			}
		}

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < sheet.size(); i++) {
				String[] row = sheet.get(i);
				bw.write(row[0]);
				for (int j = 1; j < row.length; j++) {
					bw.write("," + row[j]);
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			throw new GiaRoot3DLogFrameException("Error writing to file: "
					+ f.getAbsolutePath(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {

				}
			}
		}

	}

	// End of vp23 hack-SRL...........................}}}</editor-fold>

	// ============================<editor-fold
	// desc="GiaRoot3DLogFrameException">{{{
	static class GiaRoot3DLogFrameException extends RuntimeException {

		public GiaRoot3DLogFrameException(String msg) {
			super(msg);
		}

		public GiaRoot3DLogFrameException(Throwable th) {
			super(th);
		}

		public GiaRoot3DLogFrameException(String msg, Throwable th) {
			super(msg, th);
		}

	}
	// End of
	// GiaRoot3DLogFrameException...........................}}}</editor-fold>
}
