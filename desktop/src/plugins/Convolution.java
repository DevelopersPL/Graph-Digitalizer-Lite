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
import plugins.utils.BoundGetter;
import plugins.utils.MirrorGetter;

/**
 *
 * @author Marek
 */
public class Convolution extends MarvinAbstractImagePlugin {
    private final String name;

    public Convolution() {
        name = "Convolution";
    }
    
    public Convolution(String name) {
        this.name = name;
    }

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int[] input = mi.getIntColorArray(), output = input.clone();
        int cWidth = mi.getWidth(), cHeight = mi.getHeight();
        double[] filter = (double[]) getAttribute("filter");
        Integer width = (Integer) getAttribute("width"),
                height = filter.length/width;
        boolean[][] mask = mim.getMaskArray();

        BoundGetter bg = new MirrorGetter(input, cWidth, mim);
        
        int xTranslation = width/2, yTranslation = height/2;
        for (int y = 0; y < cHeight; y++) {
            for (int x = 0; x < cWidth; x++) {
                if (mask != null && !mask[x][y]) continue;

                int index = x + cWidth*y;
                output[index] = 0;
                int r = 0, g = 0, b = 0;
                for (int fy = 0; fy < height; fy++) {
                    for (int fx = 0; fx < width; fx++) {
                        double f = filter[fx + fy * width];
                        int c = bg.get(x - xTranslation + fx, y - yTranslation + fy);

                        r += (int) (f * (0xFF0000 & c))>>16;
                        g += (int) (f * (0xFF00 & c))>>8;
                        b += (int) (f * (0xFF & c));
                    }
                }
                output[index] = 0xFF000000 | r<<16 | g<<8 | b;
            }
        }
        mi1.setIntColorArray(output);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
