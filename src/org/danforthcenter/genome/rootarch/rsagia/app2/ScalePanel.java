/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SetScalePanel.java
 *
 * Created on May 10, 2010, 1:24:36 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.ExtensionFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;
import org.danforthcenter.genome.rootarch.rsagia2.Scale;

/**
 * Creating application needs to link this as the working app panel in the
 * ImageManipulationFrame. Allows users to graphically determine the scale of an
 * image. Fires curIndex property change events.
 * 
 * @author bm93
 */
public class ScalePanel extends javax.swing.JPanel implements
		java.awt.event.ActionListener, java.awt.event.MouseListener,
		java.beans.PropertyChangeListener {
	protected ImageManipulationFrame imf;
	protected ArrayList<RsaImageSet> inputs;
	protected ApplicationManager am;
	protected double prevScale;
	protected int curIndex;
	protected Scale scale;
	protected ResizableLine line;

	/** Creates new form SetScalePanel */
	public ScalePanel(ImageManipulationFrame imf,
			ArrayList<RsaImageSet> inputs, Scale scale, ApplicationManager am) {
		initComponents();

		this.imf = imf;
		this.inputs = inputs;
		this.scale = scale;
		this.am = am;

		nextButton.addActionListener(this);
		vertHorzCheckbox.addActionListener(this);
		changePicButton.addActionListener(this);

		lineScaleTextField.setInputVerifier(new DecimalInputVerifier());

		imf.setAppPanel(this);
		imf.getMip().addMouseListener(this);
		curIndex = 0;

        System.out.println("ScalePanel inputs " + inputs.size() + " " + inputs.get(0));
        System.out.println("ScalePanel inputs file " + inputs.get(curIndex).getInputDir().getAbsolutePath());
        System.out.println("ScalePanel type " + inputs.get(curIndex).getPreferredType());

		// display the default image
		File imgs_dir = new File(inputs.get(curIndex).getInputDir()
				.getAbsolutePath()
				+ File.separator + inputs.get(curIndex).getPreferredType());
		File[] imgs = imgs_dir.listFiles();

        System.out.println("ScalePanel imgs_dir " + imgs_dir );
        System.out.println("ScalePanel imgs " + imgs.length + " " + imgs[0] );
		// get the first image in the imgs_dir
		imf.setTitle(imgs[0].getAbsolutePath());

		doCurrent();
	}

	protected void doCurrent() {

		double s = imf.getItp().getZoom();
		RsaImageSet ris = inputs.get(curIndex);
		File inputDir = new File(ris.getInputDir().getAbsolutePath()
				+ File.separator + ris.getPreferredType());
		ExtensionFileFilter eff = new ExtensionFileFilter(
				ris.getPreferredType());
        System.out.println(this.getClass() + " " + ris.getInputDir());
        System.out.println(this.getClass() + " " + ris.getPreferredType());
        System.out.println(this.getClass() + " " + eff);
        System.out.println(this.getClass() + " " + inputDir.listFiles(eff).length);
        System.out.println(this.getClass() + " " + inputDir.listFiles(eff)[0]);
		imf.getMip().setImage(inputDir.listFiles(eff)[0]);
		imf.getMip().setScale(s, s);
	}

	protected boolean verifyLineScale() {
		return lineScaleTextField.getInputVerifier().shouldYieldFocus(
				lineScaleTextField);
	}

	public void mouseReleased(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mousePressed(MouseEvent e) {
		if (e.getSource() == imf.getMip()) {
			if (line == null && verifyLineScale()) {
				double s = imf.getItp().getZoom();
				Point p = e.getPoint();
				line = new ResizableLine(e.getPoint(),
						new Point(p.x + 70, p.y), Color.CYAN, 3, s, s);
				imf.getMip().setFocus(line);
				line.setOrthOnly(vertHorzCheckbox.isSelected());
				line.addMouseListener(line);
				line.addPropertyChangeListener("length", this);

				imf.getMip().add(line, new Integer(JLayeredPane.PALETTE_LAYER));
				imf.getMip().revalidate();
			}
		}
	}

	public void mouseExited(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseEntered(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseClicked(MouseEvent e) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == line) {
			double d1 = Double.parseDouble(lineScaleTextField.getText());
			DecimalFormat df = new DecimalFormat("0.####");
			absoluteScaleTextField.setText(df.format(d1 / line.getLength()));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == nextButton) {
			if (!Pattern.matches("(\\+|-)?\\d*(\\.)?\\d+",
					absoluteScaleTextField.getText())) {
				JOptionPane.showMessageDialog(imf,
						"Please enter a valid scale.");
			}
			OutputInfoDBFunctions useless = new OutputInfoDBFunctions();

			double d = Double.parseDouble(absoluteScaleTextField.getText());
			scale.writeScale(false, inputs.get(curIndex), d, am, useless );

			curIndex++;
			firePropertyChange("curIndex", curIndex - 1, curIndex);
			if (curIndex < inputs.size()) {
				doCurrent();
			}

			// mpf.remove(ssp);
			// ssp.setEnabled(false);
			// if (rl != null)
			// {
			// mpf.getMip().remove(rl);
			// rl.setEnabled(false);
			// }
			// mpf.getMip().removeMouseListener(this);
			// mpf.pack();

			// callback.setScaleDone(d, (rl == null) ? 1.0 :
			// Double.parseDouble(ssp.getLineScaleTextField().getText()));
			// System.out.println(rl.toString());
		} else if (e.getSource() == vertHorzCheckbox) {
			if (line != null) {
				line.setOrthOnly(vertHorzCheckbox.isSelected());
			}
		} else if (e.getSource() == changePicButton) {
			javax.swing.JFileChooser jfc = new JFileChooser(new File(inputs
					.get(curIndex).getInputDir().getAbsolutePath()
					+ File.separator + inputs.get(curIndex).getPreferredType()));
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				imf.getMip().setImage(jfc.getSelectedFile());
				imf.setTitle(jfc.getSelectedFile().getAbsolutePath());
			}
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
		absoluteScaleTextField = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		lineScaleTextField = new javax.swing.JTextField();
		vertHorzCheckbox = new javax.swing.JCheckBox();
		nextButton = new javax.swing.JButton();
		changePicButton = new javax.swing.JButton();

		setBorder(javax.swing.BorderFactory.createTitledBorder(
				javax.swing.BorderFactory.createLineBorder(new java.awt.Color(
						0, 0, 0)), "Step 1: Set scale"));

		jLabel1.setText("Set absolulte scale (mm per pixel width):");

		absoluteScaleTextField.setText("1.0");

		jLabel2.setText("or draw line (mm per line):");

		lineScaleTextField.setText("1.0");

		vertHorzCheckbox.setText("[vert/horz only]");

		nextButton.setText("Next...");

		changePicButton.setText("Change Pic...");
		changePicButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changePicButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		changePicButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		160,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		574,
																		Short.MAX_VALUE)
																.addComponent(
																		nextButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		96,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLabel1)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		absoluteScaleTextField,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		80,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jLabel2)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		lineScaleTextField,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		78,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		vertHorzCheckbox)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel1)
												.addComponent(jLabel2)
												.addComponent(vertHorzCheckbox)
												.addComponent(
														absoluteScaleTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														lineScaleTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(changePicButton)
												.addComponent(nextButton))
								.addGap(12, 12, 12)));
	}// </editor-fold>//GEN-END:initComponents

	private void changePicButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_changePicButtonActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_changePicButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextField absoluteScaleTextField;
	private javax.swing.JButton changePicButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField lineScaleTextField;
	private javax.swing.JButton nextButton;
	private javax.swing.JCheckBox vertHorzCheckbox;
	// End of variables declaration//GEN-END:variables

}
