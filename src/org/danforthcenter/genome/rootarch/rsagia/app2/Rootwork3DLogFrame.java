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
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.*;
import org.jooq.tools.json.JSONObject;

/**
 *
 * @author bm93
 */
public class Rootwork3DLogFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener, javax.swing.event.ListSelectionListener,
		java.beans.PropertyChangeListener {

	private int maxProcesses;
	private Rootwork3D rootwork3D;
	private ApplicationManager am;
	private ArrayList<RsaImageSet> riss;
	private ArrayList<IOutputThreshold> thresholds;
	private ArrayList<Rootwork3DOutput> outputs;
	private int reconLowerThresh;
	private int nodesOctree;
	private int imagesUsed;
	private int reconOption;
	private int reconUpperThreshold;
	private int distortionRadius;
	private int numberOfComponents;
	private int resolution;
	private int refImage;
	private double refRatio;
	private boolean doAdd;
	private ArrayList<JTextArea> outputTextAreas;
	private ArrayList<JScrollPane> outputPanels;
	private int cur;
	private int doneCnt;

	/** Creates new form MultiLogWindow */
	public Rootwork3DLogFrame(int maxProcesses, Rootwork3D rootwork3D,
                              ApplicationManager am, ArrayList<RsaImageSet> riss,
//			ArrayList<IOutputThreshold> thresholds, int reconLowerThesh,
                              ArrayList<IOutputThreshold> thresholds,
                              int nodesOctree, int imagesUsed, int reconOption,
//			int reconUpperThreshold, int distortionRadius,
                              int distortionRadius,
                              int numberOfComponents, int resolution, int refImage,
                              double refRatio, String doAdd) {
		initComponents();

		this.maxProcesses = maxProcesses;
		this.rootwork3D = rootwork3D;
		this.am = am;
		this.riss = riss;
		this.thresholds = thresholds;
//		this.reconLowerThresh = reconLowerThesh;
		this.nodesOctree = nodesOctree;
		this.imagesUsed = imagesUsed;
		this.reconOption = reconOption;
		this.reconUpperThreshold = reconUpperThreshold;
		this.distortionRadius = distortionRadius;
		this.numberOfComponents = numberOfComponents;
		this.resolution = resolution;
		this.refImage = refImage;
		this.refRatio = refRatio;
		if (doAdd.equalsIgnoreCase("T") || doAdd.equalsIgnoreCase("true") ) {
			this.doAdd = true;
		}
		else {
			this.doAdd = false;
		}
		this.outputs = new ArrayList<Rootwork3DOutput>();
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
			Rootwork3DOutput oi = new Rootwork3DOutput(rootwork3D.getName(), riss.get(i),
					false);
			OutputInfo.createDirectory(oi, am);

			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			oidbf.insertProgramRunTable(oi);
			JSONObject jo = new JSONObject();
			OutputInfo usedOI = (OutputInfo) thresholds.get(i);
			String used = "Used " + usedOI.getAppName() + " Run ID";
			jo.put(used,usedOI.getRunID());
			oi.setInputRuns(jo.toString());
			oi.setSavedConfigID(null);

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

	private void doNext() {
		// System.out.println("ABOUT TO RUn");
		// GiaRoot2DWorker grw = new GiaRoot2DWorker(gia, inputs.get(cur),
		// outputs.get(cur), outputTextAreas.get(cur), cur, am);
		Rootwork3DWorker rw = new Rootwork3DWorker(rootwork3D,
				outputs.get(cur), thresholds.get(cur), reconLowerThresh,
				nodesOctree, imagesUsed, reconOption, outputTextAreas.get(cur),
				am, cur, reconUpperThreshold, distortionRadius,
				numberOfComponents, resolution, refImage, refRatio, doAdd);

		rw.addPropertyChangeListener(this);
		rw.execute();
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
		if (evt.getSource().getClass().equals(Rootwork3DWorker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			Rootwork3DWorker rw = (Rootwork3DWorker) evt.getSource();
			int i = rw.getId();
			int v = rw.getReturnValue();

			String s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
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
			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			for(Rootwork3DOutput oi:outputs)
			{
				oidbf.updateRedFlag(oi);
				oidbf.updateContents(oi);
				oi.getRis().updateCountsOfApp(oi.getAppName());
			}
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

}
