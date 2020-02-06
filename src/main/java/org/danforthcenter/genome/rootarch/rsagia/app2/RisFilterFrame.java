/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RisFilterFrame.java
 *
 * Created on Aug 2, 2010, 1:17:49 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.danforthcenter.genome.rootarch.rsagia2.StringPairFilter;

/**
 *
 * @author bm93
 */
public class RisFilterFrame extends javax.swing.JFrame implements
		java.awt.event.WindowListener {
	protected ArrayList<StringPairFilter> experimentFilter;
	protected ArrayList<StringPairFilter> speciesFilter;
	protected ArrayList<StringPairFilter> plantFilter;
	protected ArrayList<StringPairFilter> imagingDayFilter;
	protected ArrayList<StringPairFilter> imagingDay_PlantFilter;

	public RisFilterFrame(String species, String experiment, String plant,
			String imagingDay, String imagingDayPlant) {
		this();

		speciesField.setText(species);
		experimentField.setText(experiment);
		plantField.setText(plant);
		imagingDayField.setText(imagingDay);
		imagingDay_PlantField.setText(imagingDayPlant);

	}

	/** Creates new form RisFilterFrame */
	public RisFilterFrame() {
		initComponents();
		this.setTitle("Filter Data Table");

		speciesField.setInputVerifier(new StringPairFilterInputVerifier());
		experimentField.setInputVerifier(new StringPairFilterInputVerifier());
		plantField.setInputVerifier(new StringPairFilterInputVerifier());
		imagingDayField.setInputVerifier(new StringPairFilterInputVerifier());
		imagingDay_PlantField
				.setInputVerifier(new StringPairFilterPlantDayInputVerifier());

		this.closeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				windowClosing(null);
			}
		});

		this.addWindowListener(this);
	}

	public void windowOpened(WindowEvent e) {

	}

	public void windowIconified(WindowEvent e) {

	}

	public void windowDeiconified(WindowEvent e) {

	}

	public void windowDeactivated(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {
		if (speciesField.getInputVerifier().shouldYieldFocus(speciesField)
				&& experimentField.getInputVerifier().shouldYieldFocus(
						experimentField)
				&& plantField.getInputVerifier().shouldYieldFocus(plantField)
				&& imagingDayField.getInputVerifier().shouldYieldFocus(
						imagingDayField)
				&& imagingDay_PlantField.getInputVerifier().shouldYieldFocus(
						imagingDay_PlantField)) {

			speciesFilter = getFilters(speciesField);
			experimentFilter = getFilters(experimentField);
			plantFilter = getFilters(plantField);
			imagingDayFilter = getFilters(imagingDayField);
			imagingDay_PlantFilter = getFilters(imagingDay_PlantField);

			firePropertyChange("done", false, true);
		}
	}

	protected ArrayList<StringPairFilter> getFilters(JTextField tf) {
		String s = tf.getText();
		ArrayList<StringPairFilter> ans = new ArrayList<StringPairFilter>();
		if (s.length() > 0) {
			ans = StringPairFilter.getInstances(s);
		}

		return ans;
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowActivated(WindowEvent e) {

	}

	public ArrayList<StringPairFilter> getExperimentFilter() {
		return experimentFilter;
	}

	public ArrayList<StringPairFilter> getImagingDayFilter() {
		return imagingDayFilter;
	}

	public ArrayList<StringPairFilter> getImagingDay_PlantFilter() {
		return imagingDay_PlantFilter;
	}

	public ArrayList<StringPairFilter> getPlantFilter() {
		return plantFilter;
	}

	public ArrayList<StringPairFilter> getSpeciesFilter() {
		return speciesFilter;
	}

	protected static class StringPairFilterInputVerifier extends
			javax.swing.InputVerifier {
		public StringPairFilterInputVerifier() {
			super();
		}

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			boolean ans = super.shouldYieldFocus(input);
			if (!ans) {
				JOptionPane
						.showMessageDialog(
								input.getParent(),
								"The filter text is incorrect. It must be a comma-delimited list or blank.  Example: a-,a,-b,a-b");
			}

			return ans;
		}

		@Override
		public boolean verify(JComponent input) {
			String s = ((JTextField) input).getText();
			return s.length() == 0 || StringPairFilter.isValid(s);
		}
	}

	protected static class StringPairFilterPlantDayInputVerifier extends
			javax.swing.InputVerifier {
		public StringPairFilterPlantDayInputVerifier() {
			super();
		}

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			boolean ans = super.shouldYieldFocus(input);
			if (!ans) {
				JOptionPane
						.showMessageDialog(
								input.getParent(),
								"The filter text is incorrect. It must be a comma-delimited list of PlantDay pairs or blank.  Example: p00001d16,p00002d12");
			}

			return ans;
		}

		@Override
		public boolean verify(JComponent input) {
			String s = ((JTextField) input).getText();
			return s.length() == 0 || StringPairFilter.isValidPlantDay(s);
		}
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

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		speciesField = new javax.swing.JTextField();
		experimentField = new javax.swing.JTextField();
		plantField = new javax.swing.JTextField();
		imagingDayField = new javax.swing.JTextField();
		closeButton = new javax.swing.JButton();
		imagingDay_PlantField = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jLabel1.setText("Enter a list of filters per field:");

		jLabel2.setText("Example: p00001, p00002-p00008, p00010-, -p00200");

		jLabel3.setText("Species:");

		jLabel4.setText("Experiment:");

		jLabel5.setText("Plant:");

		jLabel6.setText("Imaging Day:");

		closeButton.setText("Close");

		jLabel7.setText("PlantDay:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel1)
												.addComponent(jLabel2)
												.addComponent(
														closeButton,
														javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														88,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						jLabel5)
																				.addComponent(
																						jLabel6)
																				.addComponent(
																						jLabel4)
																				.addComponent(
																						jLabel3)
																				.addComponent(
																						jLabel7))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						imagingDay_PlantField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						280,
																						Short.MAX_VALUE)
																				.addComponent(
																						experimentField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						280,
																						Short.MAX_VALUE)
																				.addComponent(
																						plantField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						280,
																						Short.MAX_VALUE)
																				.addComponent(
																						imagingDayField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						280,
																						Short.MAX_VALUE)
																				.addComponent(
																						speciesField,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						280,
																						Short.MAX_VALUE))))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel2)
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														speciesField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel3))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														experimentField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel4))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel5)
												.addComponent(
														plantField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel6)
												.addComponent(
														imagingDayField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		imagingDay_PlantField,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		34,
																		Short.MAX_VALUE)
																.addComponent(
																		closeButton))
												.addComponent(jLabel7))
								.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new RisFilterFrame().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton closeButton;
	private javax.swing.JTextField experimentField;
	private javax.swing.JTextField imagingDayField;
	private javax.swing.JTextField imagingDay_PlantField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JTextField plantField;
	private javax.swing.JTextField speciesField;
	// End of variables declaration//GEN-END:variables

}
