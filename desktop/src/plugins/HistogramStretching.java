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
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinAttributes;
import plugins.convert.RGB2YCbCr;

/**
 *
 * @author Marek
 */
public class HistogramStretching extends MarvinAbstractImagePlugin {

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        final int w = mi.getWidth();
        final int count = w * mi.getHeight();
        
        int[] h = new int[0x100];
        int r, g, b;

        MarvinImagePlugin plugin = new RGB2YCbCr();
        MarvinImage YCrCb = mi.clone();
        plugin.process(mi, YCrCb);

        for (int i : YCrCb.getIntColorArray()) {
            ++h[(0xFF0000 & i)>>16];
        }

        double[] d = new double[0x100];
        for (int i = 0; i < 0xFF; i++) {
            for (int k = 0; k <= i; k++) {
                d[i] += h[k];
            }
            d[i] /= count;
        }
        
        int n = 0;
        while (d[n]<=0 && n < 0x100) {
            n++;
        }
        double minD = d[n];

        int Y, Cr, Cb;
        int[] arr = YCrCb.getIntColorArray();
        for (int i = 0; i < count; i++) {
            Y = (arr[i]&0xFF0000)>>16;
            Cb = (arr[i]&0xFF00)>>8;
            Cr = arr[i]&0xFF;

            Y = (int) ((d[Y] - minD) * 0xFF / (1. - minD));
            
            r = (int) Math.abs(Y                        + 1.402   * (Cr - 128));
            g = (int) Math.abs(Y - 0.34414 * (Cb - 128) - 0.71414 * (Cr - 128));
            b = (int) Math.abs(Y + 1.772   * (Cb - 128));
            
            int fin = 0xFF000000 + ((r & 0xFF) << 16) +
                    ((g & 0xFF) << 8) +
                    (b & 0xFF);
            
            mi1.setIntColor(i%w, i/w, fin);
        }
    }
    
}
