package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.OutputInfoDBFunctions;
import org.danforthcenter.genome.rootarch.rsagia2.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Feray Demirci on 4/28/2017.
 */
public class ScaleAllPanel extends JComponent implements
        ActionListener, MouseListener,
        PropertyChangeListener, DocumentListener {
    protected ImageManipulationFrame imf;
    protected ArrayList<RsaImageSet> inputs;
    protected ApplicationManager am;
    protected double prevScale;
    protected int curIndex;
    protected Scale scale;
    protected ResizableLine line;
    protected boolean checkAll;

    private JTextField absoluteScaleTextField;
    private JTextField lineScaleTextField;
    private JCheckBox vertHorzCheckbox;
    private JCheckBox scaleAllCheckBox;
    private JButton nextButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JButton changePicButton;
    private JPanel panel1;

    /**
     * Creates new form SetScalePanel
     */
    public ScaleAllPanel(ImageManipulationFrame imf,
                         ArrayList<RsaImageSet> inputs, Scale scale, ApplicationManager am) {

        this.imf = imf;
        this.inputs = inputs;
        this.scale = scale;
        this.am = am;
        this.checkAll = false;

        $$$setupUI$$$();
        nextButton.addActionListener(this);
        vertHorzCheckbox.addActionListener(this);
        scaleAllCheckBox.addActionListener(this);
        changePicButton.addActionListener(this);
        lineScaleTextField.getDocument().addDocumentListener(this);
        lineScaleTextField.setInputVerifier(new DecimalInputVerifier());

        imf.setAppPanel(this.panel1);
        imf.pack();
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

        System.out.println("ScalePanel imgs_dir " + imgs_dir);
        System.out.println("ScalePanel imgs " + imgs.length + " " + imgs[0]);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            if (!Pattern.matches("(\\+|-)?\\d*(\\.)?\\d+",
                    absoluteScaleTextField.getText())) {
                JOptionPane.showMessageDialog(imf,
                        "Please enter a valid scale.");
            } else {
                double d = Double.parseDouble(absoluteScaleTextField.getText());

                OutputInfoDBFunctions oidbf = new OutputInfoDBFunctions();

                if (checkAll == false) {
                    OutputInfo oi = scale.writeScale(false, inputs.get(curIndex), d, am, oidbf);
                    oidbf.updateRedFlag(oi);
                    oidbf.updateContents(oi);
                    oidbf.updateResults(oi);
                    oi.getRis().updateCountsOfApp("scale");
                    curIndex++;
                    firePropertyChange("curIndex", curIndex - 1, curIndex);
                    if (curIndex < inputs.size()) {
                        doCurrent();
                    }
                } else {
                    for (int i = 0; i < inputs.size(); i++) {
                        OutputInfo oi = scale.writeScale(false, inputs.get(i), d, am, oidbf);
                        oidbf.updateRedFlag(oi);
                        oidbf.updateContents(oi);
                        oidbf.updateResults(oi);
                        oi.getRis().updateCountsOfApp("scale");
                        firePropertyChange("curIndex", i, i + 1);
                    }
                }
            }
        } else if (e.getSource() == scaleAllCheckBox) {
            if (scaleAllCheckBox.isSelected()) {
                checkAll = true;
            } else {
                checkAll = false;
            }
        } else if (e.getSource() == vertHorzCheckbox) {
            if (line != null) {
                line.setOrthOnly(vertHorzCheckbox.isSelected());
            }
        } else if (e.getSource() == changePicButton) {
            JFileChooser jfc = new JFileChooser(new File(inputs
                    .get(curIndex).getInputDir().getAbsolutePath()
                    + File.separator + inputs.get(curIndex).getPreferredType()));
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imf.getMip().setImage(jfc.getSelectedFile());
                imf.setTitle(jfc.getSelectedFile().getAbsolutePath());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
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
                changeLineorLineText();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == line) {
            changeLineorLineText();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changeLineorLineText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changeLineorLineText();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        changeLineorLineText();
    }

    public void changeLineorLineText() {
        if (line != null && lineScaleTextField.getInputVerifier().verify(lineScaleTextField)) {
            double d1 = Double.parseDouble(lineScaleTextField.getText());
            DecimalFormat df = new DecimalFormat("0.####");
            absoluteScaleTextField.setText(df.format(d1 / line.getLength()));
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMaximumSize(new Dimension(2147483647, 112));
        panel1.setMinimumSize(new Dimension(823, 100));
        panel1.setPreferredSize(new Dimension(823, 112));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Step 1: Set scale"));
        jLabel1 = new JLabel();
        jLabel1.setText("Set absolute scale (mm per pixel width):");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(jLabel1, gbc);
        absoluteScaleTextField = new JTextField();
        absoluteScaleTextField.setMinimumSize(new Dimension(60, 24));
        absoluteScaleTextField.setPreferredSize(new Dimension(60, 24));
        absoluteScaleTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(absoluteScaleTextField, gbc);
        jLabel2 = new JLabel();
        jLabel2.setText("or draw line (mm per line):");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(jLabel2, gbc);
        lineScaleTextField = new JTextField();
        lineScaleTextField.setMinimumSize(new Dimension(50, 24));
        lineScaleTextField.setPreferredSize(new Dimension(50, 24));
        lineScaleTextField.setText("1.0");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(lineScaleTextField, gbc);
        vertHorzCheckbox = new JCheckBox();
        vertHorzCheckbox.setText("[vert/horz only]");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(vertHorzCheckbox, gbc);
        nextButton = new JButton();
        nextButton.setText("Next...");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(nextButton, gbc);
        changePicButton = new JButton();
        changePicButton.setPreferredSize(new Dimension(99, 32));
        changePicButton.setText("Change Pic...");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(changePicButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        scaleAllCheckBox = new JCheckBox();
        scaleAllCheckBox.setText("Apply to all datasets");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(scaleAllCheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
