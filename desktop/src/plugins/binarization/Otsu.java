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

/**
 *
 * @author Marek
 */
public class Otsu extends MarvinAbstractImagePlugin {
    protected MarvinImage imageIn, imageOut;
    int colorMask;

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage imageIn, MarvinImage imageOut, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        this.imageIn = imageIn;
        this.imageOut = imageOut;
        colorMask = 0xFF;
        
        int[] H = new int[0x100];
        
        for (int i : imageIn.getIntColorArray()) {
            int r = (i&0xFF0000)>>16;
            int g = (i&0xFF00)>>8;
            int b = i&0xFF;
            int gray = (int) (0.299*r + 0.587*g + 0.114*b);
            H[gray]++;
        }

        doTheJob(H);
    }

    void doTheJob(int[] H) {
        double ŁP = imageIn.getWidth()*imageIn.getHeight(), SU = 0,
                W = 0, MAX = 0, SUP = 0,
                WP, T1 = 0, T2 = 0;

        for (int i = 0; i < 0xFF; i++) SU += i*H[i]; //obliczanie sumy prawdopodobieńctw
        for (int i = 0; i < 0xFF; i++) {
            if ((W += H[i]) == 0) continue;
            if ((WP = ŁP - W) == 0) break;
            SUP += i*H[i];
            double SG = SUP/W;
            double SD = (SU-SUP)/WP;
            double R = W*WP*(SG-SD)*(SG-SD);
            if (R>=MAX) {
                T1 = i;
                if (R>MAX) T2 = i;
                MAX = R;
            }
        }

        MarvinImagePlugin plugin = new Threshold();
        plugin.setAttribute("Threshold", (T1 + T2)/2);
    }
}
