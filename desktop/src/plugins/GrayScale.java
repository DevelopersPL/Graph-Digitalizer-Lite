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
public class GrayScale extends MarvinAbstractImagePlugin {

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int r, g, b, p;
        boolean[][] mask = mim.getMaskArray();

        for (int x = 0; x < mi.getWidth(); x++) {
            for (int y = 0; y < mi.getHeight(); y++) {
                if (mask != null && !mask[x][y]) continue;

                r = mi.getIntComponent0(x, y);
                g = mi.getIntComponent1(x, y);
                b = mi.getIntComponent2(x, y);

                p = (int) (0.299*r + 0.587*g + 0.114*b);
                
                mi1.setIntColor(x, y, p, p, p);
            }
        }
    }
    
}
