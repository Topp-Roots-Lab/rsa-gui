/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

//import org.danforthcenter.genome.rootarch.rsagia.app.*;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Verifies that a TextField contains a decimal number (scientific notation not
 * supported). Displays a message popup of the field isn't valid.
 * 
 * @author bm93
 */
public class DecimalInputVerifier extends javax.swing.InputVerifier {
	protected Pattern p;

	public DecimalInputVerifier() {
		p = Pattern.compile("(\\+|-)?\\d*(\\.)?\\d+");
	}

	@Override
	public boolean verify(JComponent input) {
		JTextField tf = (JTextField) input;
		return p.matcher(tf.getText()).matches();
	}

	@Override
	public boolean shouldYieldFocus(JComponent input) {
		boolean ans = super.shouldYieldFocus(input);
		if (!ans) {
			JOptionPane.showMessageDialog(input.getParent(),
					"Field must be a valid number.");
		}
		return ans;
	}

}
