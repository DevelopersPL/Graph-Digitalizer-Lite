/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.binarization;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinAttributes;
import plugins.GrayScale;
import plugins.MPlugin;
import plugins.dialogs.BernsenForm;
import plugins.utils.BoundGetter;
import plugins.utils.MirrorGetter;

import javax.swing.*;

/**
 *
 * @author Marek
 */
public class Bernsen extends MPlugin {

    private final BernsenForm settingsWindow = new BernsenForm(this);
    private int hood;
    private int sigma;

    {
        settingsWindow.setVisible(true);
        settingsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage imageIn, MarvinImage imageOut, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int globalMean = 0;
//        int hood = (int) getAttribute("hood");
//        int sigma = (int) getAttribute("sigma");
        boolean[][] mask = mim.getMaskArray();

        MarvinImage gray = imageIn.clone();//imageIn.clone();
        gray.clearImage(0);
        MarvinImagePlugin plugin = new GrayScale();
        plugin.process(imageIn, gray, mim);

        int[] imgArray = gray.getIntColorArray();

        int masked = 0;
        for (int x = 0; x < imageIn.getWidth(); x++) {
            for (int y = 0; y < imageIn.getHeight(); y++) {
                if (mask != null && !mask[x][y]) continue;
                globalMean += gray.getIntComponent0(x, y);
                ++masked;
            }
        }
        globalMean /= masked;

        BoundGetter getter = new MirrorGetter(imgArray, imageIn.getWidth(), mim);

        for (int x = 0; x < imageIn.getWidth(); x++) {
            for (int y = 0; y < imageIn.getHeight(); y++) {
                if (mask != null && !mask[x][y]) continue;
                int min = 0xFF, max = 0, threshold;
                for (int i = -getHood(); i <= getHood(); i++) {
                    for (int j = -getHood(); j <= getHood(); j++) {
                        int val = getter.get(x + i, y + j) & 0xFF;
                        if (val > max) max = val;
                        if (val < min) min = val;
                    }
                }
                threshold = (max + min) / 2;
                if (Math.abs(globalMean - threshold) > getSigma()) threshold = globalMean;

                if ((imageIn.getIntColor(x, y)&0xFF) < threshold) {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 0, 0, 0);
                } else {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 255, 255, 255);
                }
            }
        }
    }

    @Override
    public void showSettings() {
        settingsWindow.setVisible(true);
    }

    @Override
    public String toString() {
        return "Bernsen";
    }

    /**
     * @return the hood
     */
    public int getHood() {
        return hood;
    }

    /**
     * @param hood the hood to set
     */
    public void setHood(int hood) {
        this.hood = hood;
    }

    /**
     * @return the sigma
     */
    public int getSigma() {
        return sigma;
    }

    /**
     * @param sigma the sigma to set
     */
    public void setSigma(int sigma) {
        this.sigma = sigma;
    }
}
