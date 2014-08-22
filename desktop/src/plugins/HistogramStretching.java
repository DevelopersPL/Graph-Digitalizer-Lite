/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/**
 *
 * @author Marek
 */
public class HistogramStretching extends MPlugin {

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }


    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        final int w = mi.getWidth();
        final int count = w * mi.getHeight();
        int[] h = new int[256];
        double[] d = new double[256];
        int[] LUT = new int[256];
        int r, g, b;
        boolean[][] mask = mim.getMaskArray();

        for (int i = 0; i < 3*8; i+=8) { // for each channel
            int channelMask = (0xFF << i);

            for (int j = 0; j < 256; j++) {
                h[j] = 0;
                d[j] = 0;
            }
            for (int x = 0; x < mi.getWidth(); x++) {
                for (int y = 0; y < mi.getHeight(); y++) {
                    ++h[(mi.getIntColor(x, y) & channelMask) >> i];
                }
            }

            for (int j = 0; j < 256; j++) {
                for (int k = 0; k <= j; k++) {
                    d[j] += h[k];
                }
                d[j] /= count;
            }

            int n = 0;
            while (d[n] <= 0 && n < 0x100) {
                n++;
            }
            double minD = d[n];

            for (int j = 0; j < 256; j++) {
                LUT[j] = (int) ((d[j] - minD) * 255. / (1 - minD));
            }
            System.out.println(d[255] + " " + minD + " " + (1 - minD));

            for (int x = 0; x < mi.getWidth(); x++) {
                for (int y = 0; y < mi.getHeight(); y++) {
                    if (mask != null && !mask[x][y]) continue;

                    r = mi.getIntColor(x, y);
                    g = (r & channelMask) >> i;
                    b = r & ~channelMask;

                    g = LUT[g];

                    mi1.setIntColor(x, y, (g << i) | b);
                }
            }
        }
//
//        plugin = new YCbCr2RGB();
//        plugin.process(YCrCb, mi1, mim);
    }

    @Override
    public String toString() {
        return "Wyrównanie histogramu";
    }
}
