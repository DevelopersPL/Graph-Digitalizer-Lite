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


    public void proces(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        final int w = mi.getWidth();
        final int count = w * mi.getHeight();
        int[] h = new int[0x100];
        int r, g, b;
        boolean[][] mask = mim.getMaskArray();

        MarvinImagePlugin plugin = new RGB2YCbCr();
        MarvinImage YCrCb = mi.clone();
        plugin.process(mi, YCrCb, mim);

        for (int x = 0; x < mi.getWidth(); x++) {
            for (int y = 0; y < mi.getHeight(); y++) {
                ++h[mi.getIntComponent0(x, y)];
            }
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

        int [] LUT = new int[256];
        for (int i = 0; i < 256; i++) {
            LUT[i] = (int) ((d[i]-minD)*255./(1-minD));
        }

        int a, Y, Cr, Cb;
        for (int x = 0; x < mi.getWidth(); x++) {
            for (int y = 0; y < mi.getHeight(); y++) {
                if (mask != null && !mask[x][y]) continue;

                a = mi.getAlphaComponent(x, y);
                Y = YCrCb.getIntComponent0(x, y);
                Cb= YCrCb.getIntComponent1(x, y);
                Cr= YCrCb.getIntComponent2(x, y);

                Y = LUT[Y];

                r = (int) Math.abs(Y                        + 1.402   * (Cr - 128));
                g = (int) Math.abs(Y - 0.34414 * (Cb - 128) - 0.71414 * (Cr - 128));
                b = (int) Math.abs(Y + 1.772   * (Cb - 128));

                mi1.setIntColor(x, y, a, r, g, b);
            }
        }
    }

    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int r = mi.getWidth()*mi.getHeight();
        int[] h = new int[256];
        boolean[][] mask = mim.getMaskArray();

        MarvinImagePlugin plugin = new RGB2YCbCr();
        MarvinImage YCrCb = mi.clone();
        plugin.process(mi, YCrCb, mim);

        for (int x = 0; x < mi.getWidth(); x++) {
            for (int y = 0; y < mi.getHeight(); y++) {
                ++h[mi.getIntComponent2(x, y)];
            }
        }

        double[] d = new double[256];
        for (int i = 0; i < 256; ++i) {
            if (i > 0) d[i] = d[i-1];
            d[i] += h[i];
            System.out.println(d[i] + " " + h[i]);
        }
        System.out.println(r);
        for (int i = 0; i < 256; ++i) {
            d[i] /= r;
            System.out.println(d[i] + " " + h[i]);
        }

        int n = 0;
        while (d[n] < 0)
            ++n;

        double minD = d[n];

        int [] LUT = new int[256];
        for (int i = 0; i < 256; i++) {
            LUT[i] = (int) ((d[i]-minD)*255./(1-minD));
        }

        int a, Y, Cr, Cb, g, b;
        for (int x = 0; x < mi.getWidth(); x++) {
            for (int y = 0; y < mi.getHeight(); y++) {
                if (mask != null && !mask[x][y]) continue;

                a = mi.getAlphaComponent(x, y);
                Y = YCrCb.getIntComponent0(x, y);
                Cr= YCrCb.getIntComponent1(x, y);
                Cb= YCrCb.getIntComponent2(x, y);

                Y = LUT[Y];

                r = (int) Math.abs(Y              + 1.403 * Cr);
                g = (int) Math.abs(Y - 0.344 * Cb - 0.714 * Cr);
                b = (int) Math.abs(Y + 1.77  * Cb);

                mi1.setIntColor(x, y, a, r, g, b);
            }
        }
    }


    @Override
    public String toString() {
        return "Wyrównanie histogramu";
    }
}
