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
import marvin.util.MarvinAttributes;
import plugins.utils.BoundGetter;
import plugins.utils.MirrorGetter;

/**
 *
 * @author Marek
 */
public class LocalThreshold extends MarvinAbstractImagePlugin {

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage imageIn, MarvinImage imageOut, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int threshold;
        final int hood = (int) getAttribute("hood");
        boolean[][] mask = mim.getMaskArray();

        BoundGetter getter = new MirrorGetter(imageIn.getIntColorArray(), imageIn.getWidth(), mim);

        for (int y = 0; y < imageIn.getHeight(); y++) {
            for (int x = 0; x < imageIn.getWidth(); x++) {
                if (mask != null && !mask[x][y]) continue;

                int mean = 0, elements = 0;
                for (int i = -hood; i <= hood; i++) {
                    for (int j = -hood; j <= hood; j++) {
                        mean += getter.get(x + i, y + j);
                        ++elements;
                    }
                }
                threshold = mean/elements;

                if ((imageIn.getIntColor(x, y)) < threshold) {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 0, 0, 0);
                } else {
                    imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), 255, 255, 255);
                }
            }
        }
    }
}
