/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.binarization;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinAttributes;
import plugins.GrayScale;
import plugins.utils.BoundGetter;
import plugins.utils.MirrorGetter;

/**
 *
 * @author Marek
 */
public class Bernsen extends MarvinAbstractImagePlugin {
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
        int hood = (int) getAttribute("hood");
        int sigma = (int) getAttribute("sigma");
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
                for (int i = -hood; i <= hood; i++) {
                    for (int j = -hood; j <= hood; j++) {
                        int val = getter.get(x + i, y + j) & 0xFF;
                        if (val > max) max = val;
                        if (val < min) min = val;
                    }
                }
                threshold = (max + min) / 2;
                if (Math.abs(globalMean - threshold) > sigma) threshold = globalMean;

                if ((imageIn.getIntColor(x, y)&0xFF) < threshold) {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 0, 0, 0);
                } else {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 255, 255, 255);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "Bernsen";
    }
}
