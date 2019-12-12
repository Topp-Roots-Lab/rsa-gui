package org.danforthcenter.genome.rootarch.rsagia.app2;

import org.danforthcenter.genome.rootarch.rsagia2.Crop;
import org.danforthcenter.genome.rootarch.rsagia2.ExtensionFileFilter;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Feray Demirci on 4/13/2017.
 */
public class CropAllPanel extends JComponent implements ActionListener, MouseListener {


    protected ImageManipulationFrame imf;
    protected Crop crop;
    protected RsaImageSet gp;
    protected OutputInfo oi;
    protected int rotation;
    protected OutputInfo recropi;
    protected ResizableSquare rs;

    private JPanel panel1;
    private JButton rotLeftButton;
    private JButton rotRightButton;
    private JButton nextButton;
    private JLabel jLabel1;
    private JLabel jLabel2;

    public CropAllPanel(ImageManipulationFrame imf, Crop crop, RsaImageSet gp, OutputInfo recropi, OutputInfo oi, Rectangle rect, int rot) {

        $$$setupUI$$$();

        this.imf = imf;
        this.crop = crop;
        this.gp = gp;
        this.recropi = recropi;
        this.oi = oi;

        double s = 0;
        BufferedImage bi = null;

        imf.getMip().addMouseListener(this);
        imf.setAppPanel(this.panel1);

        ExtensionFileFilter eff = null;
        if (crop.getRecrop()) {
            // based on the oi
            // hard coded for the time being:
            String PreferredType = "tiff";
            eff = new ExtensionFileFilter(PreferredType);
            // the crop images are in the "images" folder
            File cropimgdir = new File(recropi.getDir() + File.separator
                    + "images");
            imf.getMip().setImage(cropimgdir.listFiles(eff)[0]);
            imf.setTitle(recropi.toString());
            // index=0 corresponds to 100% - the already cropped images
            // are smaller,so increase scale
            imf.getItp().getZoomComboBox().setSelectedIndex(3);
            s = 1;
            imf.getMip().setScale(s, s);
            Dimension d = imf.getMip().getPreferredSize();
            if (rot == 1 || rot == 3) {
                int i = d.height;
                d.height = d.width;
                d.width = i;
            }
            bi = scaleAndLoad(crop.getThumbnail(oi), s, d);
            imf.getMip().setImage(bi);
            //imf.setTitle(gp.toString());
        } else {
            // based on the ris
            eff = new ExtensionFileFilter(gp.getPreferredType());
            imf.getMip().setImage(gp.getPreferredInputDir().listFiles(eff)[0]);
            s = imf.getItp().getZoom();
            imf.getMip().setScale(s, s);
            Dimension d = imf.getMip().getPreferredSize();
            if (rot == 1 || rot == 3) {
                int i = d.height;
                d.height = d.width;
                d.width = i;
            }
            bi = scaleAndLoad(crop.getThumbnail(oi), s, d);
            imf.getMip().setImage(bi);
            imf.setTitle(gp.toString());
        }

        this.rotation = 0;

        if (rect == null) {
            int p1x = (int) (bi.getWidth() / 4);
            int p1y = (int) (bi.getHeight() / 4);
            int p2x = (int) (bi.getWidth() / 2);
            int p2y = p1y;
            Point p1 = new Point(p1x, p1y);
            Point p2 = new Point(p2x, p2y);

            p1.x *= s;
            p1.y *= s;
            p2.x *= s;
            p2.y *= s;

            rs = new ResizableSquare(p1, p2, new Point(p2.x, p2.y + 50), new Point(
                    p1.x, p1.y + 50), s, s, Color.MAGENTA, 3);
        } else {
            this.rotation = rot;
            imf.getMip().setRotation(rot);

            int p1x = rect.x;
            int p1y = rect.y;
            int p2x = rect.x + rect.width;
            int p2y = rect.y;
            int p3x = rect.x + rect.width;
            int p3y = rect.y + rect.height;
            int p4x = rect.x;
            int p4y = rect.y + rect.height;

            Point p1 = new Point(p1x, p1y);
            Point p2 = new Point(p2x, p2y);
            Point p3 = new Point(p3x, p3y);
            Point p4 = new Point(p4x, p4y);
            p1.x *= s;
            p1.y *= s;
            p2.x *= s;
            p2.y *= s;
            p3.x *= s;
            p3.y *= s;
            p4.x *= s;
            p4.y *= s;

            rs = new ResizableSquare(p1, p2, p3, p4, s, s, Color.MAGENTA, 3);
        }
        rs.addMouseListener(rs);
        rs.setTopLocked(true);
        imf.getItp().getLockTopButton().setSelected(true);
        rotLeftButton.addActionListener(this);
        rotRightButton.addActionListener(this);
        nextButton.addActionListener(this);
        imf.getMip().add(rs, new Integer(JLayeredPane.PALETTE_LAYER));
        imf.getMip().setFocus(rs);
        imf.getMip().revalidate();

        imf.getItp().getLockTopButton().setVisible(true);
        imf.getItp().getLockTopButton().addActionListener(this);
    }

    protected BufferedImage scaleAndLoad(File f, double s, Dimension d) {
        BufferedImage ans = null;
        try {
            BufferedImage bi = ImageIO.read(f);
            // AffineTransform at = new AffineTransform();
            // at.rotate((rot))
            Dimension d2 = new Dimension(bi.getWidth(), bi.getHeight());
            AffineTransform at = new AffineTransform();
            at.scale(d.getWidth() / s / d2.getWidth(),
                    d.getHeight() / s / d2.getHeight());
            ans = new BufferedImage((int) (d.getWidth() / s),
                    (int) (d.getHeight() / s), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = ans.createGraphics();
            g2d.drawImage(bi, at, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ans;
    }

    public Rectangle getRectangle() {
        return rs.getRectangle();
    }

    public int getRot() {
        return rotation;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rotLeftButton) {
            rotation = incRot(rotation, -1);
            imf.getMip().setRotation(rotation);
        } else if (e.getSource() == rotRightButton) {
            rotation = incRot(rotation, 1);
            imf.getMip().setRotation(rotation);
        } else if (e.getSource() == imf.getItp().getLockTopButton()) {
            rs.setTopLocked(imf.getItp().getLockTopButton().isSelected());
        } else if (e.getSource() == nextButton) {
            imf.remove(this.panel1);
            imf.getMip().remove(rs);
            imf.getMip().removeMouseListener(this);
            imf.getItp().getLockTopButton().setVisible(false);
            imf.getItp().getLockTopButton().removeActionListener(this);
            firePropertyChange("done", new Boolean(false), new Boolean(true));
        }

    }

    protected int incRot(int r1, int r2) {
        int ans = r1 + r2;
        ans = (ans < 0) ? 3 : ans;
        ans = (ans > 3) ? 0 : ans;

        return ans;
    }


    public void mouseExited(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

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
        panel1.setEnabled(true);
        panel1.setMaximumSize(new Dimension(2147483647, 112));
        panel1.setMinimumSize(new Dimension(834, 50));
        panel1.setPreferredSize(new Dimension(834, 112));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Step 2: Rotate and Set Crop"));
        jLabel1 = new JLabel();
        jLabel1.setText("Rotate if needed:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(jLabel1, gbc);
        rotLeftButton = new JButton();
        rotLeftButton.setText("<< 90 Left");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(rotLeftButton, gbc);
        rotRightButton = new JButton();
        rotRightButton.setText("90 Right >>");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(rotRightButton, gbc);
        jLabel2 = new JLabel();
        jLabel2.setText("and draw  crop rectangle.");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel1.add(jLabel2, gbc);
        nextButton = new JButton();
        nextButton.setHorizontalAlignment(4);
        nextButton.setText("Next");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel1.add(nextButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
