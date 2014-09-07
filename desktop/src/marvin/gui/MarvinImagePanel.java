/**
 * Marvin Project <2007-2009>
 *
 * Initial version by:
 *
 * Danilo Rosetto Munoz Fabio Andrijauskas Gabriel Ambrosio Archanjo
 *
 * site: http://marvinproject.sourceforge.net
 *
 * GPL Copyright (C) <2007> * This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package marvin.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.JPanel;
import marvin.image.MarvinImage;
import marvin.util.MarvinPluginHistory;

/**
 * Panel to display MarvinImages.
 *
 * @author Gabriel Ambrosio Archanjo
 */
public class MarvinImagePanel extends JPanel implements MouseListener, MouseMotionListener {

    protected MarvinImage image;
    protected MarvinPluginHistory history;
    private boolean fitSizeToImage;
    private int width;
    private int height;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    /**
     * Constructor
     */
    public MarvinImagePanel() {
        super();
        fitSizeToImage = true;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Enable history
     */
    public void enableHistory() {
        history = new MarvinPluginHistory();
    }

    /**
     * Disable history
     */
    public void disableHistory() {
        history = null;
    }

    /**
     * Returns if the history is enabled.
     *
     * @return true if the history is enabled, false otherwise
     */
    public boolean isHistoryEnabled() {
        return (history != null);
    }

    /**
     * Returns the MarvinPluginHistory associated with this panel.
     *
     * @return MarvinPluginHistory reference
     */
    public MarvinPluginHistory getHistory() {
        return history;
    }

    /**
     * Instantiates the MarvinImage object and returns its BufferedImage as
     * off-screen drawable image to be used for double buffering.
     *
     * @param width images width
     * @param height	images width
     */
    public Image createImage(int width, int height) {
        image = new MarvinImage(width, height);
        setPreferredSize(new Dimension(width, height));
        return image.getBufferedImage();
    }

    /**
     * Associates a MarvinImage to the image panel.
     *
     * @param img	image�s reference to be associated with the image panel.
     */
    public void setImage(MarvinImage img) {
        img.update();
        image = img.clone();
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         double width1 = screenSize.getWidth();
         double height1 = screenSize.getHeight();
         while(image.getHeight()>height1/2 || image.getWidth()>width1/2){
         image.resize((int)(image.getWidth()*0.9) , (int)(image.getHeight()*0.9));
                  
         }*/
        image.resize(this.getWidth(), this.getHeight());
        if (fitSizeToImage && img != null && this.width != image.getWidth() && this.height != image.getHeight()) {
            this.width = image.getWidth();
            this.height = image.getHeight();
            Dimension d = new Dimension(this.width, this.height);
            setSize(d);
            setPreferredSize(d);
            validate();

        }
        repaint();
    }

    /**
     * Returns the MarvinImage associated with this panel.
     *
     * @return MarvinImage reference.
     */
    public MarvinImage getImage() {
        return image;
    }

    /**
     * Overwrite the paint method
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            g.drawImage(image.getBufferedImage(), 0, 0, this);
        }
        Graphics2D g2d = (Graphics2D) g;
        int w = xk - xp;
        int h = yk - yp;
        if (clicked) {
            g2d.draw3DRect(xp, yp, w, h, true);
        }
    }

    /**
     * Update component�s graphical representation
     */
    public void update() {
        image.update();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //clicked=true;
        changes.firePropertyChange("clicked", 0, e.getPoint());
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    boolean clicked = false;
    ArrayList<Point> points = new ArrayList<Point>();
    int xp, yp;

    @Override
    public void mousePressed(MouseEvent e) {
        xp = e.getX();
        yp = e.getY();
        clicked = true;

    }
    int xk, yk;

    @Override
    public void mouseDragged(MouseEvent e) {
        xk = e.getX();
        yk = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        changes.firePropertyChange("mouseX", 0, e.getX());
        changes.firePropertyChange("mouseY", 0, e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        xk = e.getX();
        yk = e.getY();
        clicked = false;
        //repaint();
    }

}
