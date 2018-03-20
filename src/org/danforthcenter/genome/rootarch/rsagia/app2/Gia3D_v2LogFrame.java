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

///**
// *
// * @author vp23
// */
//public class Gia3D_v2LogFrame {
//
//}

import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.Gia3D_v2;
import org.danforthcenter.genome.rootarch.rsagia2.Gia3D_v2Output;
import org.danforthcenter.genome.rootarch.rsagia2.IOutputVolume3D;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author vp23
 */
public class Gia3D_v2LogFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener, javax.swing.event.ListSelectionListener,
		java.beans.PropertyChangeListener {

	private int maxProcesses;
	private Gia3D_v2 gia3D_v2;
	private ApplicationManager am;
	private ArrayList<RsaImageSet> riss;
	private ArrayList<IOutputVolume3D> vols;
	private ArrayList<Gia3D_v2Output> outputs;

	private String descriptors;
	private String config;

	private ArrayList<JTextArea> outputTextAreas;
	private ArrayList<JScrollPane> outputPanels;
	private int cur;
	private int doneCnt;
	private int cur2;
	private int doneCnt2;
	private boolean isMatlabStarted = false;

	/** Creates new form MultiLogWindow */
	public Gia3D_v2LogFrame(int maxProcesses, Gia3D_v2 gia3D_v2,
			ApplicationManager am, ArrayList<RsaImageSet> riss,
			ArrayList<IOutputVolume3D> vols, String descriptors, String config) {
		initComponents();

		this.maxProcesses = maxProcesses;
		this.gia3D_v2 = gia3D_v2;
		this.am = am;
		this.riss = riss;
		this.vols = vols;

		this.outputs = new ArrayList<Gia3D_v2Output>();
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
			Gia3D_v2Output oi = gia3D_v2.preprocess(riss.get(i), vols.get(i),
					descriptors, config);
			outputs.add(oi);
			add(riss.get(i));
		}

		for (int i = 0; i < maxProcesses && i < riss.size(); i++) {
			doNext();
		}
	}

	private void add(RsaImageSet ris) {
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

	private void doNext() {
		// System.out.println("ABOUT TO RUn");
		Gia3D_v2Worker gw = new Gia3D_v2Worker(gia3D_v2, outputs.get(cur),
				outputTextAreas.get(cur), cur, am);

		gw.addPropertyChangeListener(this);
		gw.execute();
		cur++;
	}

	private void doNext2() {
		// System.out.println("ABOUT TO RUn");
		// GiaRoot2DWorker grw = new GiaRoot2DWorker(gia, inputs.get(cur),
		// outputs.get(cur), outputTextAreas.get(cur), cur, am);
		Gia3D_v2MatlabWorker gmtlw = new Gia3D_v2MatlabWorker(gia3D_v2,
				outputs.get(cur2), outputTextAreas.get(cur2), cur2, am);

		gmtlw.addPropertyChangeListener(this);
		gmtlw.execute();
		cur2++;
	}

	@Override
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getNewValue());

		// the postprocessMatlab won't run if Matlab not used
		// (that is useMatlab != yes)
		// So, the SurfArea Root3D Skeleton Trait won't be substituted
		// with the Matlab surface area, and the corresponding Export
		// will give the SurfArea Root3D Skeleton Trait.
		//
		String useMatlab = gia3D_v2.getUseMatlab();

		if (evt.getSource().getClass().equals(Gia3D_v2Worker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			Gia3D_v2Worker gw = (Gia3D_v2Worker) evt.getSource();
			int i = gw.getId();
			int v = gw.getReturnValue();

			String s = "";
			if (useMatlab.equalsIgnoreCase("yes")) {
				// do not report here DONE when Matlab used,
				// because Matlab is on the way
				s = (v == 0) ? "Pending" : "ERROR(" + v + ")";
			} else {
				// report here DONE when Matlab NOT used,
				s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
			}
			statusTable.setValueAt(s, i, 1);
			doneCnt++;

			if (cur < riss.size()) {
				doNext();
			} else if (useMatlab.equalsIgnoreCase("yes") && !isMatlabStarted) {
				isMatlabStarted = true;
				cur2 = 0;
				doneCnt2 = 0;
				for (int k = 0; k < maxProcesses && k < riss.size(); k++) {
					doNext2();
				}
			}
		}
		if (useMatlab.equalsIgnoreCase("yes")
				&& evt.getSource().getClass()
						.equals(Gia3D_v2MatlabWorker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			Gia3D_v2MatlabWorker gmtlw = (Gia3D_v2MatlabWorker) evt.getSource();
			int i = gmtlw.getId();
			int v = gmtlw.getReturnValue();

			String s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
			statusTable.setValueAt(s, i, 1);
			doneCnt2++;

			if (cur2 < riss.size()) {
				doNext2();
			}
		}

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (doneCnt != riss.size()) {
			JOptionPane.showMessageDialog(this,
					"Cannot close until all tasks are finished.");
		} else {
		    for(Gia3D_v2Output oi:outputs)
            {
                OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
                oidbf.updateRedFlag(oi);
                oidbf.updateContents(oi);
                oidbf.updateDescriptors(oi);

                File tsvFile = oi.getTsvFile();
                String tsvJson = oi.readFormatTSVFile(tsvFile);
				oi.setResults(tsvJson);
				oidbf.updateResults(oi);

                oi.getRis().updateCountsOfApp(oi.getAppName());
            }
			firePropertyChange("done", false, true);
			// mlw.dispose();
		}
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
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
	}// </editor-fold>

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// new GiaRootFrame(4).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JPanel detailsPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable statusTable;

	// End of variables declaration

	// ============================<editor-fold
	// desc="Gia3D_v2LogFrameException">{{{
	static class Gia3D_v2LogFrameException extends RuntimeException {

		public Gia3D_v2LogFrameException(String msg) {
			super(msg);
		}

		public Gia3D_v2LogFrameException(Throwable th) {
			super(th);
		}

		public Gia3D_v2LogFrameException(String msg, Throwable th) {
			super(msg, th);
		}

	}
	// End of
	// Gia3D_v2LogFrameException...........................}}}</editor-fold>
}
