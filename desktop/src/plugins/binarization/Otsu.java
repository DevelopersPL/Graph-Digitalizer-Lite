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
import plugins.MPlugin;
import plugins.dialogs.OtsuForm;

/**
 *
 * @author Marek
 */
public class Otsu extends MPlugin {
    private MarvinImageMask mim;
    private MarvinImage imageIn, imageOut;

    OtsuForm settingsWindow;
    
    private int threshold;

    @Override
    public void showSettings() {
        if (settingsWindow != null)
            settingsWindow.setVisible(true);
    }

    public Otsu() {
        this.threshold = -1;
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
            this.imageIn = imageIn;
            this.imageOut = imageOut;
            this.mim = mim;
        if (getThreshold() == -1) {
            boolean[][]mask = mim.getMaskArray();

            int[] H = new int[0x100];

            for (int x = 0; x < imageIn.getWidth(); x++) {
                for (int y = 0; y < imageIn.getHeight(); y++) {
                    if (mask != null && !mask[x][y]) continue;

                    int r = imageIn.getIntComponent0(x, y);
                    int g = imageIn.getIntComponent1(x, y);
                    int b = imageIn.getIntComponent2(x, y);
                    int gray = (int) (0.299*r + 0.587*g + 0.114*b);
                    H[gray]++;
                }
            }
            
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
            
            setThreshold((int) (T1 + T2) / 2);

            settingsWindow = new OtsuForm(this);
        }
        doTheJob();
    }

    void doTheJob() {

        MarvinImagePlugin plugin = new Threshold();
        plugin.setAttribute("Threshold", threshold);
        plugin.process(imageIn, imageOut, mim);
    }
    
    @Override
    public String toString() {
        return "Otsu";
    }

    /**
     * @return the threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
