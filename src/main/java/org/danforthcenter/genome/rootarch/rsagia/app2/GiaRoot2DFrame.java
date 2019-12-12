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
public class GiaRoot2DFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener, javax.swing.event.ListSelectionListener,
		java.beans.PropertyChangeListener {

	protected int maxProcesses;
	protected GiaRoot2D gia;
	protected ArrayList<GiaRoot2DInput> inputs;
	protected ApplicationManager am;
	protected ArrayList<GiaRoot2DOutput> outputs;

	private ArrayList<JTextArea> outputTextAreas;
	private ArrayList<JScrollPane> outputPanels;
	private int cur;
	private int doneCnt;

	/** Creates new form MultiLogWindow */
	public GiaRoot2DFrame(int maxProcesses, GiaRoot2D gia,
						  ArrayList<GiaRoot2DInput> inputs, ApplicationManager am) {
		initComponents();

		this.maxProcesses = maxProcesses;
		this.gia = gia;
		this.inputs = inputs;
		this.am = am;
		this.outputs = new ArrayList<GiaRoot2DOutput>();

		outputTextAreas = new ArrayList<JTextArea>();
		outputPanels = new ArrayList<JScrollPane>();

		String[] cols = { "Image Set", "Status" };
		DefaultTableModel dtm = new DefaultTableModel(cols, 0);
		statusTable.setModel(dtm);
		statusTable.getSelectionModel().addListSelectionListener(this);
		addWindowListener(this);

		cur = 0;
		doneCnt = 0;
		for (int i = 0; i < inputs.size(); i++) {
			GiaRoot2DOutput oi = new GiaRoot2DOutput(gia.getName(), inputs.get(i)
					.getRis(), false);
			OutputInfo.createDirectory(oi, am);

			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			oidbf.insertProgramRunTable(oi);

			String descriptorsString = this.inputs.get(i).getDescriptors();
			String templateString = this.inputs.get(i).getTemplateString();
			OutputInfo cropOutput = this.inputs.get(i).getCrop();
			int cropRunID = cropOutput.getRunID();
			JSONObject jo = new JSONObject();
			String used = "Used " + cropOutput.getAppName() + " Run ID";
			jo.put(used,cropRunID);

			oi.setInputRuns(jo.toString());
            oi.setSavedConfigID(oidbf.findConfigID(templateString, oi.getAppName()));
            oi.setDescriptors(descriptorsString);
            oi.setUnsavedConfigContents(null);

			outputs.add(oi);
			add(inputs.get(i).getRis());
		}

		for (int i = 0; i < maxProcesses && i < inputs.size(); i++) {
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
		GiaRoot2DWorker grw = new GiaRoot2DWorker(gia, inputs.get(cur),
				outputs.get(cur), outputTextAreas.get(cur), cur, am);
		grw.addPropertyChangeListener(this);
		grw.execute();
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
		if (evt.getSource().getClass().equals(GiaRoot2DWorker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			GiaRoot2DWorker grw = (GiaRoot2DWorker) evt.getSource();
			int i = grw.getId();
			int v = grw.getReturnValue();

			String s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
			statusTable.setValueAt(s, i, 1);

			if (v == 0)
			{
				OutputInfo oi = grw.getOutput();
				OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
				oidbf.updateRedFlag(oi);
				oidbf.updateContents(oi);
				oidbf.updateDescriptors(oi);
				String csvJson = GiaRoot2DOutput.readFormatCSVFile(this.gia.getCsvFile(oi));
				oi.setResults(csvJson);
				oidbf.updateResults(oi);
				oi.getRis().updateCountsOfApp(oi.getAppName());
			}

			doneCnt++;

			if (cur < inputs.size()) {
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
		if (doneCnt != inputs.size()) {
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
		this.setLocationRelativeTo(null);

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
