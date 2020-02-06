package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia.dbfunctions.MetadataDBFunctions;
import org.jooq.Record;
import org.jooq.Result;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BatchSeedImport extends JFrame implements ActionListener {

    private JTextField selectedPathField;
    private JButton browseButton;
    private JButton uploadCSVFileButton;
    private JPanel panel1;
    private JLabel clickableLabel;
    private JLabel csvInfo;
    private JFileChooser fileChooser;
    private MetadataDBFunctions mdf;

    public BatchSeedImport(final File csvTemplateDir) {
        $$$setupUI$$$();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(this.panel1);
        this.setTitle("Upload CSV File");
        this.fileChooser = new JFileChooser();
        this.fileChooser.setDialogTitle("Select CSV File");
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File", "csv");
        this.fileChooser.addChoosableFileFilter(filter);
        this.fileChooser.setFileFilter(filter);

        ImageIcon questionIcon = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");
        Image scaledImage = questionIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        this.csvInfo.setIcon(scaledIcon);
        clickableLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clickableLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().open(new File(csvTemplateDir, "rsa_gia_seed.csv"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        this.browseButton.addActionListener(this);
        this.uploadCSVFileButton.addActionListener(this);
        this.mdf = new MetadataDBFunctions();
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.browseButton) {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedPathField.setText(this.fileChooser.getSelectedFile().toString());
            }
        } else if (e.getSource() == this.uploadCSVFileButton) {
            File csvFile = new File(this.selectedPathField.getText());
            if (csvFile.getName().endsWith(".csv")) {
                String path = csvFile.getAbsolutePath();
                String[] contentsArray = null;
                String contents = null;
                try {
                    contents = new String(Files.readAllBytes(Paths.get(path)));
                    String[] contentsLines = contents.split(System.lineSeparator());
                    boolean check = true;

                    String[] headingArray = contentsLines[0].split(",");
                    if (!headingArray[0].equals("Organism Name")) {
                        check = false;
                        JOptionPane.showMessageDialog(null, "The first row should contain headings.", "ERROR", JOptionPane.ERROR_MESSAGE);
                        this.dispose();
                    }

                    for (int i = 1; i < contentsLines.length && check == true; i++) {
                        if (!contentsLines[i].isEmpty()) {
                            contentsArray = contentsLines[i].split(",", -1);

                            if (contentsArray.length < 12) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "Each row should have 12 columns.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }

                            String organism = contentsArray[0];
                            if (check == true && organism.isEmpty()) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The organism name should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }
                            if (check == true && !mdf.checkOrganismExists(organism)) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The organism you entered does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }

                            String experiment = contentsArray[1];
                            if (check == true && experiment.isEmpty()) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The experiment name should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }
                            if (check == true && !mdf.checkOrgandExpPairExists(organism, experiment)) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The organism and experiment pair you entered does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }

                            String seed = contentsArray[2];
                            if (check == true && seed.isEmpty()) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The seed name should not be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }
                            if (check == true && !seed.substring(0, 1).equals("p")) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The seed name should start with letter 'p'.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }

                            int genotypeID = -1;
                            String genotypeName = contentsArray[3];
                            if (check == true && !genotypeName.isEmpty()) {
                                Result<Record> genotypeRecord = this.mdf.findGenotypeID(genotypeName, organism);
                                try {
                                    Record r = genotypeRecord.get(0);
                                    genotypeID = (int) r.getValue("genotype_id");
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The genotype name you entered does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            double dry_shoot = -1;
                            String dry_shoot_value = contentsArray[4];
                            if (check == true && !dry_shoot_value.isEmpty()) {
                                try {
                                    dry_shoot = Double.parseDouble(dry_shoot_value);
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The dry shoot value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            double dry_root = -1;
                            String dry_root_value = contentsArray[5];
                            if (check == true && !dry_root_value.isEmpty()) {
                                try {
                                    dry_root = Double.parseDouble(dry_root_value);
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The dry root value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            double wet_shoot = -1;
                            String wet_shoot_value = contentsArray[6];
                            if (check == true && !wet_shoot_value.isEmpty()) {
                                try {
                                    wet_shoot = Double.parseDouble(wet_shoot_value);
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The wet shoot value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            double wet_root = -1;
                            String wet_root_value = contentsArray[7];
                            if (check == true && !wet_root_value.isEmpty()) {
                                try {
                                    wet_root = Double.parseDouble(wet_root_value);
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The wet root value is wrong.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            String str_chamber = contentsArray[8];
                            if (check == true && !str_chamber.isEmpty()) {
                                try {
                                    String str_chamber_second = str_chamber.split("-")[1];
                                } catch (Exception e1) {
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The sterilization chamber that you entered should have '-' between chamber number and row/column (e.g. '3-C4').", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            String imagingUnit = contentsArray[9];
                            if (check == true && !imagingUnit.equals("day") && !imagingUnit.equals("hour")) {
                                check = false;
                                JOptionPane.showMessageDialog(null, "The imaging interval unit should be either 'day' or 'hour'.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                this.dispose();
                            }

                            String description = contentsArray[10];

                            Date imagingStartDate = null;
                            String date = contentsArray[11];
                            if (check == true && !date.isEmpty()) {
                                date = date.replace("_", " ");
                                try {
                                    imagingStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                                    if (imagingStartDate.equals(null)) {
                                        check = false;
                                        JOptionPane.showMessageDialog(null, "The date you entered should be in yyyy-MM-dd_HH:mm:ss format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                        this.dispose();
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                    check = false;
                                    JOptionPane.showMessageDialog(null, "The date you entered should be in yyyy-MM-dd_HH:mm:ss format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    this.dispose();
                                }
                            }

                            if (check == true) {
                                boolean seedExists = mdf.checkSeedExists(organism, experiment, seed);
                                try {
                                    if (!seedExists) {
                                        mdf.insertSeed(organism, experiment, seed, genotypeID, dry_shoot, dry_root, wet_shoot, wet_root,
                                                str_chamber, imagingUnit, description, imagingStartDate);
                                    } else {
                                        mdf.updateSeed(seed, organism, experiment, seed, genotypeID, dry_shoot, dry_root, wet_shoot, wet_root,
                                                str_chamber, imagingUnit, description, imagingStartDate);
                                    }
                                } catch (Exception e2) {
                                    JOptionPane.showMessageDialog(null, "The values you entered are in wrong format.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                    e2.printStackTrace();
                                    this.dispose();
                                }
                            }
                        }
                    }
                    if (check == true) {
                        JOptionPane.showMessageDialog(null, "The seeds are saved", "INFO", JOptionPane.INFORMATION_MESSAGE);
                        firePropertyChange("getall", null, null);
                        this.dispose();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
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
        panel1.setPreferredSize(new Dimension(500, 150));
        final JLabel label1 = new JLabel();
        label1.setText("File Path:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel1.add(label1, gbc);
        selectedPathField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(selectedPathField, gbc);
        browseButton = new JButton();
        browseButton.setText("Browse...");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(browseButton, gbc);
        uploadCSVFileButton = new JButton();
        uploadCSVFileButton.setText("Upload CSV File");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(uploadCSVFileButton, gbc);
        clickableLabel = new JLabel();
        clickableLabel.setForeground(new Color(-16776961));
        clickableLabel.setText("Example CSV file");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(clickableLabel, gbc);
        csvInfo = new JLabel();
        csvInfo.setToolTipText("<html>\n<ul>\n\t<li>The first row should contain headings.</li>\n\t<li>The order of columns is important.</li>\n\t<li>The organism and experiment names should not be empty.</li>\n\t<li>The organism, experiment and genotype names should already be in database. If not, you should add them to database first.</li>\n\t<li>The seed name should not be empty. It should start with letter \"p\".</li>\n\t<li>The date should be in yyyy-MM-dd_HH:mm:ss format.</li>\n\t<li>The imaging interval unit should be either \"day\" or \"hour\".</li>\n\t<li>The sterilization chamber should have \"-\" between chamber number and row/column (e.g. \"3-C4\").</li>\n\t<li>The dry shoot, dry root, wet shoot and wet root values should be whole or decimal numbers.</li>\n</ul>\n</html>");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(csvInfo, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
