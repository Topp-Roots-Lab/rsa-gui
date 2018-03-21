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

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.*;
import org.jooq.tools.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 *
 * @author bm93
 */
public class Rootwork3DPersLogFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener, javax.swing.event.ListSelectionListener,
		java.beans.PropertyChangeListener {

	private int maxProcesses;
	private Rootwork3DPers rootwork3DPers;
	private ApplicationManager am;
	private ArrayList<RsaImageSet> thresholdRiss;
    private ArrayList<RsaImageSet> scaleRiss;
	private ArrayList<IOutputThreshold> thresholds;
    private ArrayList<OutputInfo> scales;
	private ArrayList<Rootwork3DPersOutput> outputs;
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

	private ArrayList<JTextArea> outputTextAreas;
	private ArrayList<JScrollPane> outputPanels;
	private int cur;
	private int doneCnt;

    // tw 2015july15
    private int camDist;
    private int rotDir;
	private boolean doFindRotAxis;
    private boolean doCalib;
    private Double pitch;
    private Double roll;
    private int translation;
    private int focusOffset;
	private boolean doAdd;

	/** Creates new form MultiLogWindow */
	// tw 2015july11
	public Rootwork3DPersLogFrame(int maxProcesses, Rootwork3DPers rootwork3DPers,
								  ApplicationManager am, ArrayList<RsaImageSet> thresholdRiss,
//			ArrayList<IOutputThreshold> thresholds, int reconLowerThesh,
								  ArrayList<IOutputThreshold> thresholds,
								  // tw 2015july11 add to meet perspective scale requirement
//                                  ArrayList<RsaImageSet> ScaleRiss,
								  ArrayList<OutputInfo> scales,
								  int nodesOctree, int imagesUsed, int reconOption,
//			int reconUpperThreshold, int distortionRadius,
								  int distortionRadius,
								  int numberOfComponents, int resolution, int refImage,
								  double refRatio,
								  int camDist, int rotDir, String doFindRotAxis, String doCalib,
								  Double pitch, Double roll, int translation, int focusOffset,
								  String doAdd) {
		initComponents();


        // tw 2015july15
        this.camDist = camDist;
        this.rotDir = rotDir;
		if (doFindRotAxis.equalsIgnoreCase("T") || doFindRotAxis.equalsIgnoreCase("true") ) {
			this.doFindRotAxis = true;
		}
		else{
			this.doFindRotAxis = false;
		}
		if (doCalib.equalsIgnoreCase("T") || doCalib.equalsIgnoreCase("true") ) {
			this.doCalib = true;
		}
		else{
			this.doCalib = false;
		}
		if (doAdd.equalsIgnoreCase("T") || doAdd.equalsIgnoreCase("true") ) {
			this.doAdd = true;
		}
		else{
			this.doAdd = false;
		}
        this.pitch = pitch;
        this.roll = roll;
        this.translation = translation;
        this.focusOffset = focusOffset;


		this.maxProcesses = maxProcesses;
		this.rootwork3DPers = rootwork3DPers;
		this.am = am;
		this.thresholdRiss = thresholdRiss;
		this.thresholds = thresholds;
        this.scaleRiss = scaleRiss;
        this.scales = scales;

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
		this.outputs = new ArrayList<Rootwork3DPersOutput>();
		outputTextAreas = new ArrayList<JTextArea>();
		outputPanels = new ArrayList<JScrollPane>();

		String[] cols = { "Image Set", "Status" };
		DefaultTableModel dtm = new DefaultTableModel(cols, 0);
		statusTable.setModel(dtm);
		statusTable.getSelectionModel().addListSelectionListener(this);
		addWindowListener(this);

		cur = 0;
		doneCnt = 0;
		for (int i = 0; i < thresholdRiss.size(); i++) {
			Rootwork3DPersOutput oi = new Rootwork3DPersOutput(rootwork3DPers.getName(), thresholdRiss.get(i),
					false);
			OutputInfo.createDirectory(oi, am);

			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			oidbf.insertProgramRunTable(oi);
			JSONObject jo = new JSONObject();
			OutputInfo usedOI = (OutputInfo) thresholds.get(i);
			String used = "Used " + usedOI.getAppName() + " Run ID";
			jo.put(used,usedOI.getRunID());
			used = "Used " + scales.get(i).getAppName() + " Run ID";
			jo.put(used,scales.get(i).getRunID());
			oi.setInputRuns(jo.toString());
			oi.setSavedConfigID(null);

			outputs.add(oi);
			add(thresholdRiss.get(i));
		}

		for (int i = 0; i < maxProcesses && i < thresholdRiss.size(); i++) {
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
		// GiaRoot2DWorker grw = new GiaRoot2DWorker(gia, inputs.get(cur),
		// outputs.get(cur), outputTextAreas.get(cur), cur, am);

        System.out.println(this.getClass().getSimpleName() + " cur " + cur);
        System.out.println("\t\toutputs(cur) " + outputs.get(cur));
        System.out.println("\t\tthresholds(cur) " + thresholds.get(cur));
        System.out.println("\t\tscales(cur) " + scales.get(cur));

        Rootwork3DPersWorker rw = new Rootwork3DPersWorker(rootwork3DPers,
				outputs.get(cur), thresholds.get(cur), scales.get(cur), reconLowerThresh,
				nodesOctree, imagesUsed, reconOption, outputTextAreas.get(cur),
				am, cur, reconUpperThreshold, distortionRadius,
				numberOfComponents, resolution, refImage, refRatio,
                this.camDist, this.rotDir, this.doFindRotAxis, this.doCalib, this.pitch,
                this.roll, this.translation, this.focusOffset, this.doAdd
        );

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
		if (evt.getSource().getClass().equals(Rootwork3DPersWorker.class)
				&& evt.getPropertyName().equals("state")
				&& evt.getNewValue() == SwingWorker.StateValue.DONE) {
			Rootwork3DPersWorker rw = (Rootwork3DPersWorker) evt.getSource();
			int i = rw.getId();
			int v = rw.getReturnValue();

			String s = (v == 0) ? "DONE" : "ERROR(" + v + ")";
			statusTable.setValueAt(s, i, 1);
			doneCnt++;

			if (cur < thresholdRiss.size()) {
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
		if (doneCnt != thresholdRiss.size()) {
			JOptionPane.showMessageDialog(this,
					"Cannot close until all tasks are finished.");
		} else {
			OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();
			for(Rootwork3DPersOutput oi:outputs)
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

		jScrollPane1 = new JScrollPane();
		statusTable = new javax.swing.JTable();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		detailsPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("RootArch Compute - Multi Log");

		statusTable.setModel(new DefaultTableModel(
				new Object[][] { { null, null }, { null, null },
						{ null, null }, { null, null } }, new String[] {
						"Image Set", "Status" }) {
			Class[] types = new Class[] { String.class,
					String.class };
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

		GroupLayout detailsPanelLayout = new GroupLayout(
				detailsPanel);
		detailsPanel.setLayout(detailsPanelLayout);
		detailsPanelLayout.setHorizontalGroup(detailsPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 667, Short.MAX_VALUE));
		detailsPanelLayout.setVerticalGroup(detailsPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGap(0, 260, Short.MAX_VALUE));

		GroupLayout layout = new GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.TRAILING)
												.addComponent(
														detailsPanel,
														GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jScrollPane1,
														GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE,
														677, Short.MAX_VALUE)
												.addComponent(
														jLabel1,
														GroupLayout.Alignment.LEADING)
												.addComponent(
														jLabel2,
														GroupLayout.Alignment.LEADING))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(12, 12, 12)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1)
												.addComponent(jLabel2))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										GroupLayout.PREFERRED_SIZE,
										156,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(detailsPanel,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE,
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
	private JScrollPane jScrollPane1;
	private javax.swing.JTable statusTable;
	// End of variables declaration//GEN-END:variables

}
